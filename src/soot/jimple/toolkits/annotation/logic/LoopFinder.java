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

public class LoopFinder extends BodyTransformer {

    private UnitGraph g;

    private HashMap loops;

    public HashMap loops(){
        return loops;
    }
    
    protected void internalTransform (Body b, String phaseName, Map options){
    
        g = new ExceptionalUnitGraph(b);
        DominatorAnalysis a = new DominatorAnalysis(g);
        
        loops = new HashMap();
        
        Iterator stmtsIt = b.getUnits().iterator();
        while (stmtsIt.hasNext()){
            Stmt s = (Stmt)stmtsIt.next();

            List succs = g.getSuccsOf(s);
            FlowSet dominators = (FlowSet)a.getFlowAfter(s);

            ArrayList backEdges = new ArrayList();

            Iterator succsIt = succs.iterator();
            while (succsIt.hasNext()){
                Stmt succ = (Stmt)succsIt.next();
                if (dominators.contains(succ)){
                    backEdges.add(succ);
                }
            }

            Iterator headersIt = backEdges.iterator();
            while (headersIt.hasNext()){
                Stmt header = (Stmt)headersIt.next();
                List loopBody = getLoopBodyFor(header, s);

                // for now just print out loops as sets of stmts
                //System.out.println("FOUND LOOP: Header: "+header+" Body: "+loopBody);
                if (loops.containsKey(header)){
                    // merge bodies
                    List lb1 = (List)loops.get(header);
                    loops.put(header, union(lb1, loopBody));
                }
                else {
                    loops.put(header, loopBody);
                }
            }
        }
        
        //print loops found
        int colorId = 0;
        Iterator printIt = loops.keySet().iterator();
        while (printIt.hasNext()){
            Stmt h = (Stmt)printIt.next();
            System.out.println("FOUND LOOP: Header: "+h+" Body: "+loops.get(h));

            // tag loop stmts with colors
            
            /*Iterator bIt = ((List)loops.get(h)).iterator();
            while (bIt.hasNext()){
                tagLoopStmt((Stmt)bIt.next(), colorId);
            }*/

            colorId++;
        }
    }
    

    private List getLoopBodyFor(Stmt header, Stmt node){
    
        ArrayList loopBody = new ArrayList();
        Stack stack = new Stack();

        loopBody.add(header);
        stack.push(node);

        while (!stack.isEmpty()){
            Stmt next = (Stmt)stack.pop();
            if (!loopBody.contains(next)){
                // add next to loop body
                loopBody.add(0, next);
                // put all preds of next on stack
                Iterator it = g.getPredsOf(next).iterator();
                while (it.hasNext()){
                    stack.push(it.next());
                }
            }
        }

        return loopBody;
    }

    private List union(List l1, List l2){
        Iterator it = l2.iterator();
        while (it.hasNext()){
            Object next = it.next();
            if (!l1.contains(next)){
                l1.add(next);
            }
        }
        return l1;
    }

    private void tagLoopStmt(Stmt s, int id){
        switch(id%5){
            case 0: {
                        s.addTag(new ColorTag(ColorTag.GREEN));
                        break;
                    }
            case 1: {
                        s.addTag(new ColorTag(ColorTag.RED));
                        break;
                    }
            case 2: {
                        s.addTag(new ColorTag(ColorTag.BLUE));
                        break;
                    }
            case 3: {
                        s.addTag(new ColorTag(ColorTag.YELLOW));
                        break;
                    }
            case 4: {
                        s.addTag(new ColorTag(ColorTag.ORANGE));
                        break;
                    }
        
        }
    }
}
