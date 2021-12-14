package xmu.wrxlab.abuilder.instrumentor;

import java.util.ArrayList;
import java.util.Map;

import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import xmu.wrxlab.abuilder.ABuilderServerConfig;

/**
 * 运行4个transform: cfg, antrance ins, antrance ins config, debug
 */
public class Instrumentor extends SceneTransformer {
    /** 过滤后的应用类 */
    private final ArrayList<SootClass> myClasses;
    /** Antrance类 */
    private SootClass antranceIns;

    public Instrumentor() {
        myClasses = new ArrayList<>();
        antranceIns = null;
    }

    /**
     * 过滤应用类得到myClasses, 寻找AntranceIns
     */
    private void initialize() {
        for (SootClass myClass : Scene.v().getApplicationClasses()) {
            String[] antranceInses = ABuilderServerConfig.v().getAntranceInses();
            if (myClass.getShortName().equals(antranceInses[0])) {
                antranceIns = myClass;
                continue;
            }
            // antrance ins不要加载到myClasses中
            boolean skip = false;
            for (int i = 1; i < antranceInses.length; i++) {
                if (myClass.getShortName().equals(antranceInses[i])) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }
            myClasses.add(myClass);
        }
    }

    protected void internalTransform(String phase, Map options) {
        initialize();
        // 构造程序的控制流图, 注意要在字节码修改前构造
        (new CFGTransform(myClasses)).start();
        // 插桩, 插桩过程中统计stmtTableSize, 更新AntranceBuilderConfig,
        // 从而在AntranceInsConfigTransform时可以正确配置stmtTableSize
        (new AntranceInsTransform(myClasses, antranceIns)).start();
        // 修改静态面值, 配置AntranceIns
        (new AntranceInsConfigTransform(antranceIns)).start();
        // debug jimple
        (new DebugJimpleTransform(myClasses, antranceIns)).start();
    }

} // end of class