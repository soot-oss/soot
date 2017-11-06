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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.MHGDominatorsFinder;
import soot.toolkits.graph.UnitGraph;

public class LoopFinder extends BodyTransformer {

    private UnitGraph g;

    private HashMap<Stmt, List<Stmt>> loops;

    public Collection<Loop> loops(){
        Collection<Loop> result = new HashSet<Loop>();
        for (Map.Entry<Stmt,List<Stmt>> entry : loops.entrySet()) {
            result.add(new Loop(entry.getKey(),entry.getValue(),g));
        }
        return result;
    }
    
    protected void internalTransform (Body b, String phaseName, Map options){
    
        g = new ExceptionalUnitGraph(b);
        MHGDominatorsFinder a = new MHGDominatorsFinder(g);
        
        loops = new HashMap<Stmt, List<Stmt>>();
        
        Iterator<Unit> stmtsIt = b.getUnits().iterator();
        while (stmtsIt.hasNext()){
            Stmt s = (Stmt)stmtsIt.next();

            List<Unit> succs = g.getSuccsOf(s);
            Collection<Unit> dominaters = (Collection<Unit>)a.getDominators(s);

            ArrayList<Stmt> headers = new ArrayList<Stmt>();

            Iterator<Unit> succsIt = succs.iterator();
            while (succsIt.hasNext()){
                Stmt succ = (Stmt)succsIt.next();
                if (dominaters.contains(succ)){
                	//header succeeds and dominates s, we have a loop
                    headers.add(succ);
                }
            }

            Iterator<Stmt> headersIt = headers.iterator();
            while (headersIt.hasNext()){
                Stmt header = headersIt.next();
                List<Stmt> loopBody = getLoopBodyFor(header, s);

                // for now just print out loops as sets of stmts
                //System.out.println("FOUND LOOP: Header: "+header+" Body: "+loopBody);
                if (loops.containsKey(header)){
                    // merge bodies
                    List<Stmt> lb1 = loops.get(header);
                    loops.put(header, union(lb1, loopBody));
                }
                else {
                    loops.put(header, loopBody);
                }
            }
        }

    }
    

    private List<Stmt> getLoopBodyFor(Stmt header, Stmt node){
    
        ArrayList<Stmt> loopBody = new ArrayList<Stmt>();
        Stack<Unit> stack = new Stack<Unit>();

        loopBody.add(header);
        stack.push(node);

        while (!stack.isEmpty()){
            Stmt next = (Stmt)stack.pop();
            if (!loopBody.contains(next)){
                // add next to loop body
                loopBody.add(0, next);
                // put all preds of next on stack
                Iterator<Unit> it = g.getPredsOf(next).iterator();
                while (it.hasNext()){
                    stack.push(it.next());
                }
            }
        }
        
        assert (node==header && loopBody.size()==1) || loopBody.get(loopBody.size()-2)==node;
        assert loopBody.get(loopBody.size()-1)==header;
        
        return loopBody;
    }

    private List<Stmt> union(List<Stmt> l1, List<Stmt> l2){
        Iterator<Stmt> it = l2.iterator();
        while (it.hasNext()){
            Stmt next = it.next();
            if (!l1.contains(next)){
                l1.add(next);
            }
        }
        return l1;
    }
}
