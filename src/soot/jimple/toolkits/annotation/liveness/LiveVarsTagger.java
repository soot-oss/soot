package soot.jimple.toolkits.annotation.liveness;

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.tagkit.*;
import java.util.*;
import soot.jimple.*;

public class LiveVarsTagger extends BodyTransformer {


    public LiveVarsTagger(Singletons.Global g) {}
    public static LiveVarsTagger v() { return G.v().soot_jimple_toolkits_annotation_liveness_LiveVarsTagger();}

    protected void internalTransform(Body b, String phaseName, Map options){
    
        SimpleLiveLocals sll = new SimpleLiveLocals(new ExceptionalUnitGraph(b));

        Iterator it = b.getUnits().iterator();
        while (it.hasNext()){
            Stmt s = (Stmt)it.next();
            //System.out.println("stmt: "+s);
            Iterator liveLocalsIt = sll.getLiveLocalsAfter(s).iterator();
            while (liveLocalsIt.hasNext()){
                Value v = (Value)liveLocalsIt.next();
                s.addTag(new StringTag("Live Variable: "+v));

                Iterator usesIt = s.getUseBoxes().iterator();
                while (usesIt.hasNext()){
                    ValueBox use = (ValueBox)usesIt.next();
                    if (use.getValue().equals(v)){
                        use.addTag(new ColorTag(ColorTag.GREEN, "Live Variable"));
                    }
                }
            }
        }
    }
}   
