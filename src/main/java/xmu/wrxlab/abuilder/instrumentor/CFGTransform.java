package xmu.wrxlab.abuilder.instrumentor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import soot.Body;
import soot.PatchingChain;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.tagkit.LineNumberTag;
import xmu.wrxlab.abuilder.ABuilderServerConfig;
import xmu.wrxlab.abuilder.utils.SimpleJson;

/**
 * 构建cfg, 保存在项目目录下的cfg文件夹中, 按照类路径存储.
 * <p> 由于做了分文件保存, 结构上也没有压缩的必要了, 直接按照soot内置的结构信息进行打印
 */
public class CFGTransform {
    private final ArrayList<SootClass> myClasses;
    /** 用于关联语句和对应的字节码行号, 每遍历到一个新函数时会初始化 */
    private Map<Unit, Integer> stmt_id;

    public CFGTransform(ArrayList<SootClass> myClasses) {
        this.myClasses = myClasses;
    }

    private String calMethodSig(SootMethod curMethod) {
        String methodSig = curMethod.getSignature(); // 函数唯一标识
        // 避免一些html转义问题
        methodSig = methodSig.replace("<", "[");
        methodSig = methodSig.replace(">", "]");
        return methodSig;
    }

    public void start() {
        // 创建/清空 cfg
        File cfg = new File(ABuilderServerConfig.v().getProject(), "cfg");
        if (!cfg.exists()) {
            cfg.mkdir();
        } else {
            // 只有first为true时才能清空
            if (ABuilderServerConfig.v().isFirst()) {
                try {
                    FileUtils.cleanDirectory(cfg);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("cfg error");
                }
            }
        }

        for (SootClass curClass : myClasses) {

            // cfg json格式:
            // { "methods"(类文件下的函数):[
            //     {
            //       "methodSig"(函数签名):"de.rampro.activitydiary.ui.generic.DetailViewHolders@void onClick(android.view.View)",
            //       "stmts"(按soot结构信息记录的语句):[
            //         "jid@sid@type@targets@fallThrough"(jid:语句在函数中的字节码id, sid:语句在文件中的源码行,
            //           type分为branch(br),goto(gt),normal(n):
            //           1.br: targets是若干用'#'分隔的数字, 表示目标语句jid, 编号0对应false/default分支, >=1对应true/各个case分支)
            //           2.gt: targets表示跳转语句jid
            //           3.n: targets为空
            //           fallThrough(0/1)对应soot中的stmt.fallsThrough(), 表示当前语句和jid+1的语句执行上是否相连)
            //       ]
            //     }
            //   ]
            // }

            // 记录类对应的路径, 作为cfg的json文件名, 从而在创建文件时保存到相应路径
            String jsonPath = curClass.getName().replace(".", "/") + ".json";
            File jsonFile = new File(cfg, jsonPath);
            if(!jsonFile.getParentFile().exists()) {
                jsonFile.getParentFile().mkdirs();
            }

            SimpleJson jsonStr = new SimpleJson();
            jsonStr.object();

            jsonStr.key("methods");
            jsonStr.array();
            boolean first = true;
            for (SootMethod curMethod : curClass.getMethods()) {
                String methodSig = calMethodSig(curMethod);
                if (!curMethod.hasActiveBody()) {
                    continue;
                }
                Body body = curMethod.getActiveBody();
                if (body.getUnits().isEmpty()) {
                    continue;
                }
                PatchingChain<Unit> units = body.getUnits();
                Iterator<Unit> stmtIt = units.snapshotIterator();

                // 先做语句:jid映射, 这样后面遇到target时就能获取其jid了
                stmt_id = new HashMap<>();
                int id = 0;

                while (stmtIt.hasNext()) {
                    Stmt stmt = (Stmt) stmtIt.next();
                    stmt_id.put(stmt, id);
                    id++;
                } // end of while stmtIt.hasNext()

                if (!first) jsonStr.comma();
                first = false;
                jsonStr.object();

                jsonStr.key("methodSig");
                jsonStr.value(methodSig).comma();
                jsonStr.key("stmts");
                jsonStr.array();
                stmtIt = units.snapshotIterator();
                boolean first_ = true;
                while (stmtIt.hasNext()) {
                    if (!first_) jsonStr.comma();
                    first_ = false;
                    Stmt stmt = (Stmt) stmtIt.next();
                    if (stmt instanceof IfStmt) {
                        jsonStr.value(visitIfStmt((IfStmt) stmt));
                    } else if (stmt instanceof LookupSwitchStmt) {
                        jsonStr.value(visitLookupSwitchStmt((LookupSwitchStmt) stmt));
                    } else if (stmt instanceof TableSwitchStmt) {
                        jsonStr.value(visitTableSwitchStmt((TableSwitchStmt) stmt));
                    } else if (stmt instanceof GotoStmt) {
                        jsonStr.value(visitGotoStmt((GotoStmt) stmt));
                    } else {
                        jsonStr.value(visitNormalStmt(stmt));
                    }
                } // end of while stmtIt.hasNext()
                jsonStr.endArray();

                jsonStr.endObject();
            } // end of while methodIterator.hasNext()
            jsonStr.endArray();

            jsonStr.endObject();

            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(jsonFile));
                out.write(jsonStr.toString());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("cfg error");
            }
        } // end of for curClass
    }

    private String visitIfStmt(IfStmt stmt) {
        int jid = stmt_id.get(stmt);
        return jid+"@"+getLineNumber(stmt)+"@br@"+
                stmt_id.get(stmt.getTarget())+"#"+(jid+1)+"@"+(stmt.fallsThrough()?"1":"0");
    }

    private String visitLookupSwitchStmt(LookupSwitchStmt stmt) {
        StringBuilder ans = new StringBuilder(stmt_id.get(stmt)+"@"+getLineNumber(stmt)+"@br@"+
                stmt_id.get(stmt.getDefaultTarget()));
        int caseNum = stmt.getTargetCount();
        for (int i = 0; i < caseNum; i++) {
            ans.append("#").append(stmt_id.get(stmt.getTarget(i)));
        }
        ans.append("@").append((stmt.fallsThrough()?"1":"0"));
        return ans.toString();
    }

    private String visitTableSwitchStmt(TableSwitchStmt stmt) {
        StringBuilder ans = new StringBuilder(stmt_id.get(stmt)+"@"+getLineNumber(stmt)+"@br@"+
                stmt_id.get(stmt.getDefaultTarget()));
        int caseNum = stmt.getTargets().size();
        for (int i = 0; i < caseNum; i++) {
            ans.append("#").append(stmt_id.get(stmt.getTarget(i)));
        }
        ans.append("@").append((stmt.fallsThrough()?"1":"0"));
        return ans.toString();
    }

    private String visitGotoStmt(GotoStmt stmt) {
        return stmt_id.get(stmt)+"@"+getLineNumber(stmt)+"@gt@"+
                stmt_id.get(stmt.getTarget())+"@"+(stmt.fallsThrough()?"1":"0");
    }

    private String visitNormalStmt(Stmt stmt) {
        return stmt_id.get(stmt)+"@"+getLineNumber(stmt)+"@n@@"+(stmt.fallsThrough()?"1":"0");
    }

    private int getLineNumber(Stmt s) {
        for (Object o : s.getTags()) {
            if (o instanceof LineNumberTag) {
                return Integer.parseInt(o.toString());
            }
        }
        return 0;
    }

}
