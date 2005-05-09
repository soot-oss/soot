package soot.javaToJimple.toolkits;

import soot.*;
import soot.jimple.*;
import java.util.*;

public class GotoEliminator extends BodyTransformer {
    public GotoEliminator (Singletons.Global g) {}
    public static GotoEliminator v() { 
        return G.v().soot_javaToJimple_toolkits_GotoEliminator();
    }


    protected void internalTransform(Body b, String phaseName, Map options){

        G.v().out.println("running goto eliminator");
        /*
         * the idea is to look for groups of statements of the form
         *      goto L0
         * L0:  goto L1
         * 
         * and transform to
         *      goto L1
         */
        
        Iterator it = b.getUnits().iterator();
        while (it.hasNext()){
            Stmt s = (Stmt)it.next();
            Unit target = null;
            if (s instanceof IfStmt){
                target = ((IfStmt)s).getTarget();
            }
            else if (s instanceof GotoStmt){
                target = ((GotoStmt)s).getTarget();
            }
            else continue;
            
        }
    }

}
