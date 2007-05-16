/* Soot - a J*va Optimization Framework
 * Copyright (C) 2006 Ondrej Lhotak
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


 



package soot.jimple.toolkits.base;
import soot.options.*;

import soot.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

public class JimpleConstructorFolder extends BodyTransformer
{
    static boolean isNew(Stmt s) {
        if(!(s instanceof AssignStmt)) return false;
        if(!(rhs(s) instanceof NewExpr)) return false;
        return true;
    }
    static boolean isConstructor(Stmt s) {
        if(!(s instanceof InvokeStmt)) return false;
        InvokeStmt is = (InvokeStmt) s;
        InvokeExpr expr = is.getInvokeExpr();
        if(!(expr instanceof SpecialInvokeExpr)) return false;
        SpecialInvokeExpr sie = (SpecialInvokeExpr) expr;
        if(!sie.getMethodRef().name().equals(SootMethod.constructorName))
                return false;
        return true;
    }
    static Local base(Stmt s) {
        InvokeStmt is = (InvokeStmt) s;
        InstanceInvokeExpr expr = (InstanceInvokeExpr) is.getInvokeExpr();
        return (Local) expr.getBase();
    }
    static void setBase(Stmt s, Local l) {
        InvokeStmt is = (InvokeStmt) s;
        InstanceInvokeExpr expr = (InstanceInvokeExpr) is.getInvokeExpr();
        expr.getBaseBox().setValue(l);
    }
    static boolean isCopy(Stmt s) {
        if(!(s instanceof AssignStmt)) return false;
        if(!(rhs(s) instanceof Local)) return false;
        if(!(lhs(s) instanceof Local)) return false;
        return true;
    }
    static Value rhs(Stmt s) {
        AssignStmt as = (AssignStmt) s;
        return as.getRightOp();
    }
    static Value lhs(Stmt s) {
        AssignStmt as = (AssignStmt) s;
        return as.getLeftOp();
    }
    static Local rhsLocal(Stmt s) { return (Local) rhs(s); }
    static Local lhsLocal(Stmt s) { return (Local) lhs(s); }
    private class Fact {
        private Map<Local, Stmt> varToStmt = new HashMap<Local, Stmt>();
        private MultiMap stmtToVar = new HashMultiMap();
        private Stmt alloc = null;
        public void add(Local l, Stmt s) {
            varToStmt.put(l, s);
            stmtToVar.put(s, l);
        }
        public Stmt get(Local l) {
            return varToStmt.get(l);
        }
        public Set get(Stmt s) {
            return stmtToVar.get(s);
        }
        public void removeAll(Stmt s) {
            for(Iterator it = stmtToVar.get(s).iterator(); it.hasNext();) {
                final Local var = (Local) it.next();
                varToStmt.remove(var);
            }
            stmtToVar.remove(s);
        }
        public void copyFrom(Fact in) {
            varToStmt = new HashMap<Local, Stmt>(in.varToStmt);
            stmtToVar = new HashMultiMap(in.stmtToVar);
            alloc = in.alloc;
        }
        public void mergeFrom(Fact in1, Fact in2) {
            varToStmt = new HashMap<Local, Stmt>();
            Iterator<Local> it = in1.varToStmt.keySet().iterator();
            while(it.hasNext()) {
                Local l = it.next();
                Stmt newStmt = in1.varToStmt.get(l);
                if(in2.varToStmt.containsKey(l)) {
                    Stmt newStmt2 = in2.varToStmt.get(l);
                    if(!newStmt.equals(newStmt2)) {
                        throw new RuntimeException("Merge of different uninitialized values; are you sure this bytecode is verifiable?");
                    }
                }
                add(l, newStmt);
            }
            it = in2.varToStmt.keySet().iterator();
            while(it.hasNext()) {
                Local l = it.next();
                Stmt newStmt = in2.varToStmt.get(l);
                add(l, newStmt);
            }
            if(in1.alloc != null && in1.alloc.equals(in2.alloc)) {
                alloc = in1.alloc;
            } else {
                alloc = null;
            }
        }
        public boolean equals(Object other) {
            if(!(other instanceof Fact)) return false;
            Fact o = (Fact) other;
            if(!stmtToVar.equals(o.stmtToVar)) return false;
            if(alloc == null && o.alloc != null) return false;
            if(alloc != null && o.alloc == null) return false;
            if(alloc != null && !alloc.equals(o.alloc)) return false;
            return true;
        }
        public Stmt alloc() { return alloc; }
        public void setAlloc(Stmt newAlloc) { alloc = newAlloc; }
    }
    private class Analysis extends ForwardFlowAnalysis {
        public Analysis(DirectedGraph graph) {
            super(graph);
            doAnalysis();
        }
        protected Object entryInitialFlow() {
            return new Fact();
        }
        protected Object newInitialFlow() {
            return new Fact();
        }
        public void flowThrough(Object inFact, Object unit, Object outFact) {
            Stmt s = (Stmt) unit;
            Fact in = (Fact) inFact;
            Fact out = (Fact) outFact;

            copy(in, out);
            out.setAlloc(null);

            if(isNew(s)) {
                out.add(lhsLocal(s), s);
            } else if(isCopy(s)) {
                Stmt newStmt = out.get(rhsLocal(s));
                if(newStmt != null) out.add(lhsLocal(s), newStmt);
            } else if(isConstructor(s)) {
                Stmt newStmt = out.get(base(s));
                if(newStmt != null) {
                    out.removeAll(newStmt);
                    out.setAlloc(newStmt);
                }
            }
        }
        public void copy(Object source, Object dest) {
            ((Fact) dest).copyFrom((Fact) source);
        }
        public void merge(Object in1, Object in2, Object out) {
            ((Fact) out).mergeFrom((Fact) in1, (Fact) in2);
        }
    }
    /** This method pushes all newExpr down to be the stmt directly before every
     * invoke of the init */
    public void internalTransform(Body b, String phaseName, Map options)
    {
        JimpleBody body = (JimpleBody)b;

        //PhaseDumper.v().dumpBody(body, "constructorfolder.in");

        if(Options.v().verbose())
            G.v().out.println("[" + body.getMethod().getName() +
                "] Folding Jimple constructors...");

        Analysis analysis = new Analysis(new BriefUnitGraph(body));

        Chain units = body.getUnits();
        List<Unit> stmtList = new ArrayList<Unit>();
        stmtList.addAll(units);

        Iterator<Unit> it;
        it = stmtList.iterator();
        while(it.hasNext()) {
            Stmt s = (Stmt) it.next();
            if(isCopy(s)) continue;
            if(isConstructor(s)) continue;
            Fact before = (Fact) analysis.getFlowBefore(s);
            Iterator usesIt = s.getUseBoxes().iterator();
            while(usesIt.hasNext()) {
                ValueBox usebox = (ValueBox) usesIt.next();
                Value value = usebox.getValue();
                if(!(value instanceof Local)) continue;
                Local local = (Local) value;
                if(before.get(local) != null)
                    throw new RuntimeException("Use of an unitialized value "
                            +"before constructor call; are you sure this "
                            +"bytecode is verifiable?\n"+s);
            }
        }

        // throw out all new statements
        it = stmtList.iterator();
        while(it.hasNext()) {
            Stmt s = (Stmt) it.next();
            if(isNew(s)) {
                units.remove(s);
            }
        }

        it = stmtList.iterator();
        while(it.hasNext()) {
            Stmt s = (Stmt) it.next();
            Fact before = (Fact) analysis.getFlowBefore(s);
            Fact after = (Fact) analysis.getFlowAfter(s);

            // throw out copies of uninitialized variables
            if(isCopy(s)) {
                Stmt newStmt = before.get(rhsLocal(s));
                if(newStmt != null) units.remove(s);
            } else if(after.alloc() != null) {
                // insert the new just before the constructor
                Stmt newStmt = before.get(base(s));
                setBase(s, lhsLocal(newStmt));
                units.insertBefore(newStmt, s);

                // add necessary copies
                Iterator copyIt = before.get(newStmt).iterator();
                while(copyIt.hasNext()) {
                    Local l = (Local) copyIt.next();
                    if(l.equals(base(s))) continue;
                    units.insertAfter(Jimple.v().newAssignStmt(l, base(s)), s);
                }
            }
        }
        //PhaseDumper.v().dumpBody(body, "constructorfolder.out");
    }  
}
