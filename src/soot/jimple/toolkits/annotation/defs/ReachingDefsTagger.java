package soot.jimple.toolkits.annotation.defs;

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.tagkit.*;
import java.util.*;
import soot.jimple.*;

public class ReachingDefsTagger extends BodyTransformer {


    public ReachingDefsTagger(Singletons.Global g) {}
    public static ReachingDefsTagger v() { return G.v().soot_jimple_toolkits_annotation_defs_ReachingDefsTagger();}

    protected void internalTransform(Body b, String phaseName, Map options){
    
        SimpleLocalDefs sld = new SimpleLocalDefs(new ExceptionalUnitGraph(b));

        Iterator it = b.getUnits().iterator();
        while (it.hasNext()){
            Stmt s = (Stmt)it.next();
            //System.out.println("stmt: "+s);
            Iterator usesIt = s.getUseBoxes().iterator();
            while (usesIt.hasNext()){
                ValueBox vbox = (ValueBox)usesIt.next();
                if (vbox.getValue() instanceof Local) {
                    Local l = (Local)vbox.getValue();
                    //System.out.println("local: "+l);
                    Iterator rDefsIt = sld.getDefsOfAt(l, s).iterator();
                    while (rDefsIt.hasNext()){
                        Stmt next = (Stmt)rDefsIt.next();
                        String info = l+" has reaching def: "+next.toString();
                        s.addTag(new LinkTag(info, next, b.getMethod().getDeclaringClass().getName(), "Reaching Defs"));
                    }
                }
            }
        }
    }
}   
