package soot.jimple.toolkits.annotation;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.tagkit.*;

public class LineNumberAdder extends SceneTransformer {

    public LineNumberAdder( Singletons.Global g) {}
    public static LineNumberAdder v() { return G.v().soot_jimple_toolkits_annotation_LineNumberAdder();}

    public void internalTransform(String phaseName, Map opts){

        Iterator it = Scene.v().getApplicationClasses().iterator();
        while (it.hasNext()){
            SootClass sc = (SootClass)it.next();
            // make map of first line to each method
            HashMap lineToMeth = new HashMap();
            Iterator methIt = sc.getMethods().iterator();
            while (methIt.hasNext()){
                SootMethod meth = (SootMethod)methIt.next();
                Body body = meth.retrieveActiveBody();
                Stmt s = (Stmt)body.getUnits().getFirst();
                while (s instanceof IdentityStmt){
                    s = (Stmt)body.getUnits().getSuccOf(s);
                }
                if (s.hasTag("LineNumberTag")){
                    LineNumberTag tag = (LineNumberTag)s.getTag("LineNumberTag");
                    lineToMeth.put(new Integer(tag.getLineNumber()), meth); 
                }
            } 
            Iterator methIt2 = sc.getMethods().iterator();
            while (methIt2.hasNext()){
                SootMethod meth = (SootMethod)methIt2.next();
                Body body = meth.retrieveActiveBody();
                Stmt s = (Stmt)body.getUnits().getFirst();
                while (s instanceof IdentityStmt){
                    s = (Stmt)body.getUnits().getSuccOf(s);
                }
                if (s.hasTag("LineNumberTag")){
                    LineNumberTag tag = (LineNumberTag)s.getTag("LineNumberTag");
                    int line_num = tag.getLineNumber() - 1;
                    // already taken
                    if (lineToMeth.containsKey(new Integer(line_num))){
                        meth.addTag(new LineNumberTag(line_num + 1));
                    }
                    // still available - so use it for this meth
                    else {
                        meth.addTag(new LineNumberTag(line_num));
                    }
                }
            }
            
        }
    }                
}
