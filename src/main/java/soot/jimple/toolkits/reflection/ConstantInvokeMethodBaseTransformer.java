package soot.jimple.toolkits.reflection;

import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;

import java.util.Iterator;
import java.util.Map;

/**
 * This class creates a local for each string constant that is used as a base object to a reflective Method.invoke call.
 * Therefore, {@link soot.jimple.toolkits.callgraph.OnFlyCallGraphBuilder.TypeBasedReflectionModel} can handle such cases and extend the call graph
 * for edges to the specific java.lang.String method invoked by the reflective call.
 *
 * @author Manuel Benz
 * created on 02.08.17
 */
public class ConstantInvokeMethodBaseTransformer extends SceneTransformer {

    private final static String INVOKE_SIG = "<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>";

    public ConstantInvokeMethodBaseTransformer(Singletons.Global g) {
    }

    public static ConstantInvokeMethodBaseTransformer v() {
        return G.v().soot_jimple_toolkits_reflection_ConstantInvokeMethodBaseTransformer();
    }

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        boolean verbose = options.containsKey("verbose");

        for (SootClass sootClass : Scene.v().getApplicationClasses()) {
            // In some rare cases we will have application classes that are not resolved due to being located in excluded packages (e.g., the ServiceConnection class constructed by FlowDroid: soot.jimple.infoflow.cfg.LibraryClassPatcher#patchServiceConnection)
            if (sootClass.resolvingLevel() < SootClass.BODIES)
                continue;
            for (SootMethod sootMethod : sootClass.getMethods()) {
                Body body = sootMethod.retrieveActiveBody();

                for (Iterator<Unit> iterator = body.getUnits().snapshotIterator(); iterator.hasNext(); ) {
                    Stmt u = (Stmt) iterator.next();

                    if (u.containsInvokeExpr()) {
                        InvokeExpr invokeExpr = u.getInvokeExpr();
                        if (invokeExpr.getMethod().getSignature().equals(INVOKE_SIG)) {
                            if (invokeExpr.getArg(0) instanceof StringConstant) {

                                StringConstant constant = (StringConstant) invokeExpr.getArg(0);
                                Local newLocal = Jimple.v().newLocal("sc" + body.getLocalCount(), constant.getType());
                                body.getLocals().add(newLocal);
                                body.getUnits().insertBefore(Jimple.v().newAssignStmt(newLocal, constant), u);
                                invokeExpr.setArg(0, newLocal);

                                if (verbose)
                                    G.v().out.println("Replaced constant base object of Method.invoke() by local in: " + sootMethod.toString());
                            }
                        }
                    }
                }
            }
        }
    }
}
