/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.toolkits.annotation.logic;

import soot.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import java.util.*;
import soot.toolkits.scalar.*;
import soot.tagkit.*;

public class LoopInvariantFinder extends BodyTransformer {

    private UnitGraph g;

    
    protected void internalTransform (Body b, String phaseName, Map options){
   
        SimpleLocalDefs sld = new SimpleLocalDefs(new ExceptionalUnitGraph(b));
        NaiveSideEffectTester nset = new NaiveSideEffectTester();
        
        LoopFinder lf = new LoopFinder();
        lf.internalTransform(b, phaseName, options);

        HashMap loops = lf.loops();

        // no loop invariants if no loops
        if (loops.isEmpty()) return;
        
        Iterator hIt = loops.keySet().iterator();
        while (hIt.hasNext()){
            Stmt header = (Stmt)hIt.next();
            List loopStmts = (List)loops.get(header);
            Iterator bIt = loopStmts.iterator();
            while (bIt.hasNext()){
                Stmt tStmt = (Stmt)bIt.next();
                //System.out.println("will test stmt: "+tStmt+" for loop header: "+header);
                //System.out.println("will test with loop stmts: "+loopStmts);
                handleLoopBodyStmt(tStmt, nset, loopStmts);
            }
        }
    }

    private void handleLoopBodyStmt(Stmt s, NaiveSideEffectTester nset, List loopStmts){
        // need to do some checks for arrays - when there is an multi-dim array
        // --> for defs there is a get of one of the dims that claims to be 
        // loop invariant
        
        // ignore goto stmts
        if (s instanceof GotoStmt) return;

        // ignore invoke stmts
        if (s instanceof InvokeStmt) return; 
       
        //System.out.println("s : "+s+" use boxes: "+s.getUseBoxes());
        // just use boxes here 
        Iterator useBoxesIt = s.getUseBoxes().iterator();
        boolean result = true;
        uses: while (useBoxesIt.hasNext()){
            ValueBox vb = (ValueBox)useBoxesIt.next();
            Value v = vb.getValue();
            //System.out.println("next vb: "+v+" is a: "+vb.getClass());
            //System.out.println("next vb: "+v+" class is a: "+v.getClass());
            // new's are not invariant
            if (v instanceof NewExpr) {
                result = false;
                //System.out.println("break uses: due to new expr");
                break uses;
            }
            // invokes are not invariant
            if (v instanceof InvokeExpr) {
                result = false;
                //System.out.println("break uses: due to invoke expr");
                break uses;
            }
            // array refs with non-constant indexes - need to check 
            // index box
            //.System.out.println("v : "+v+" is a "+v.getClass());
            /*if (v instanceof ArrayRef){
                System.out.println("loop stmts: "+loopStmts);
                Iterator arrLoopStmtsIt = loopStmts.iterator();
                while (arrLoopStmtsIt.hasNext()){
                    Stmt next = (Stmt)arrLoopStmtsIt.next();
                    System.out.println("testing array ref");
                    System.out.println("next stmt: "+next+" and val: "+((ArrayRef)v).getIndexBox().getValue());
                    if (nset.unitCanWriteTo(next, ((ArrayRef)v).getIndexBox().getValue())){
                        result = false;
                        //System.out.println("not loop invariant due to use: "+s);
                        System.out.println("break uses: due to array ref");
                        break uses;
                    }
                }
            
            }*/
            // side effect tester doesn't handle expr
            if (v instanceof Expr) continue;
        
            Iterator loopStmtsIt = loopStmts.iterator();
            while (loopStmtsIt.hasNext()){
                Stmt next = (Stmt)loopStmtsIt.next();
                if (nset.unitCanWriteTo(next, v)){
                    result = false;
                    //System.out.println("not loop invariant due to use: "+s);
                    //System.out.println("break uses: due to side effect tester");
                    break uses;
                }
            }
            
        }

        //System.out.println("after uses: result: "+result);
        // def boxes - don't check self
        Iterator defBoxesIt = s.getDefBoxes().iterator(); 
        defs: while (defBoxesIt.hasNext()){
            ValueBox vb = (ValueBox)defBoxesIt.next();
            Value v = vb.getValue();
            // new's are not invariant
            if (v instanceof NewExpr) {
                result = false;
                break defs;
            }
            // invokes are not invariant
            if (v instanceof InvokeExpr) {
                result = false;
                break defs;
            }
            // side effect tester doesn't handle expr
            if (v instanceof Expr) continue;
        
            Iterator loopStmtsIt = loopStmts.iterator();
            while (loopStmtsIt.hasNext()){
                Stmt next = (Stmt)loopStmtsIt.next();
                if (next.equals(s)) continue;
                if (nset.unitCanWriteTo(next, v)){
                    result = false;
                    //System.out.println("not loop invariant due to def: "+s);
                    break defs;
                }
            }
            
        }
        //System.out.println("result afte defs: "+result);
        if (result){
            s.addTag(new LoopInvariantTag("is loop invariant"));
            s.addTag(new ColorTag(ColorTag.RED, "Loop Invariant Analysis"));
        }
        else {
            // if loops are nested it might be invariant in one of them 
            // so remove tag
            if (s.hasTag("LoopInvariantTag")) {
                s.removeTag("LoopInvariantTag");
            }
        }
    }
    
}
