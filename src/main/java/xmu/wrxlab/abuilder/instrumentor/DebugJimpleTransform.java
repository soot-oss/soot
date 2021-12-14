package xmu.wrxlab.abuilder.instrumentor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JArrayRef;
import soot.tagkit.LineNumberTag;
import soot.util.Chain;
import xmu.wrxlab.abuilder.ABuilderServerConfig;

/**
 * 生成插桩后的jimple字节码目录, 用于调试
 */
public class DebugJimpleTransform {
    /** 过滤后的应用类 */
    private final ArrayList<SootClass> myClasses;
    /** AntranceIns */
    private SootClass antranceIns;

    public DebugJimpleTransform(ArrayList<SootClass> myClasses, SootClass antranceIns) {
        this.myClasses = myClasses;
        this.antranceIns = antranceIns;
    }

    public void start() {
        // 创建/清空 debug jimple
        File debugJimple = new File(ABuilderServerConfig.v().getProject(), "debugjimple");
        if (!debugJimple.exists()) {
            debugJimple.mkdir();
        } else {
            try {
                FileUtils.cleanDirectory(debugJimple);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("debugjimple error");
            }
        }

        // 为每个类创建其对应的jimple文件
        for (SootClass curClass : myClasses) {
            // 创建类所在的package
            String classPath = curClass.getName().replace(".", "/");
            File classFile = new File(debugJimple.getAbsolutePath(), classPath);
            if (!classFile.getParentFile().exists()) {
                classFile.getParentFile().mkdirs();
            }
            // 写入类的jimple字节码
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(classFile));

                for (SootMethod curMethod : curClass.getMethods()) {
                    if (!curMethod.hasActiveBody()) continue;
                    Body body = curMethod.getActiveBody();
                    if (body.getUnits().isEmpty()) continue;
                    PatchingChain<Unit> units = body.getUnits();
                    Iterator<Unit> stmtIt = units.snapshotIterator();
                    out.write("--------------------------------------------------\n");
                    out.write(curMethod.getSignature()+"\n");
                    out.write("--------------------------------------------------\n");
                    while (stmtIt.hasNext()) {
                        Stmt stmt = (Stmt) stmtIt.next();
                        out.write(String.format("[%06d]", getLineNumber(stmt)));
                        out.write(stmt+"\n");
                        if (curClass.getShortName().contains("MainActivity") &&
                                curMethod.getName().contains("onDestroy") && stmt instanceof AssignStmt) {
                            AssignStmt as = (AssignStmt) stmt;
                            Value left = as.getLeftOp();
                            System.out.println(left+"###"+left.getType()+"@@"+left.getType().getClass());
                            if (left instanceof JArrayRef) {
                                Value base = ((JArrayRef)left).getBase();
                                Value index = ((JArrayRef)left).getIndex();
                                System.out.println(base + ": " + base.getClass());
                                System.out.println(index + ": " + index.getClass());
                            }
                        }
                    } // end of while stmtIt.hasNext()
                } // end of while methodIterator.hasNext()

                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("debugjimple error");
            }
        }

        // 为AntranceIns的static代码块创建jimple文件, 方便查看静态面值修改结果
        String classPath = antranceIns.getName().replace(".", "/");
        File classFile = new File(debugJimple.getAbsolutePath(), classPath);
        if (!classFile.getParentFile().exists()) {
            classFile.getParentFile().mkdirs();
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(classFile));

            for (SootMethod curMethod : antranceIns.getMethods()) {
                if (!curMethod.getName().equals("<clinit>")) continue;
                if (!curMethod.hasActiveBody()) continue;
                Body body = curMethod.getActiveBody();
                if (body.getUnits().isEmpty()) continue;
                Chain<Unit> units = body.getUnits();
                Iterator<Unit> stmtIt = units.snapshotIterator();
                out.write("--------------------------------------------------\n");
                out.write(curMethod.getSignature()+"\n");
                out.write("--------------------------------------------------\n");
                while (stmtIt.hasNext()) {
                    Stmt stmt = (Stmt) stmtIt.next();
                    out.write(String.format("[%06d]", getLineNumber(stmt)));
                    out.write(stmt+"\n");
                } // end of while stmtIt.hasNext()
            } // end of while methodIterator.hasNext()

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("debugjimple error");
        }
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
