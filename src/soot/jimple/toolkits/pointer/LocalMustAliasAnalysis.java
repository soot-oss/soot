/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Patrick Lam
 * Copyright (C) 2007 Eric Bodden
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
package soot.jimple.toolkits.pointer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.omg.CORBA.UNKNOWN;

import soot.Local;
import soot.RefLikeType;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/** LocalMustAliasAnalysis attempts to determine if two local
 * variables (at two potentially different program points) must point
 * to the same object.
 *
 * The underlying abstraction is based on global value numbering.
 * 
 * @author Patrick Lam
 * @author Eric Bodden
 * */
public class LocalMustAliasAnalysis extends ForwardFlowAnalysis
{
	public static final String UNKNOWN_LABEL = "UNKNOWN";
	
	private static final Object UNKNOWN = new Object() {
    	public String toString() { return UNKNOWN_LABEL; }
    };
    private static final Object NOTHING = new Object() {
    	public String toString() { return "NOTHING"; }
    };
    
    protected List<Local> locals;

    protected int nextNumber = 1;
    

    public LocalMustAliasAnalysis(UnitGraph g)
    {
        super(g);
        this.locals = new LinkedList<Local>(); 

        for (Local l : (Collection<Local>) g.getBody().getLocals()) {
            if (l.getType() instanceof RefLikeType)
                this.locals.add(l);
        }

        doAnalysis();
    }

    protected void merge(Object in1, Object in2, Object o)
    {
        HashMap inMap1 = (HashMap) in1;
        HashMap inMap2 = (HashMap) in2;
        HashMap outMap = (HashMap) o;

        for (Local l : locals) {
            Object i1 = inMap1.get(l), i2 = inMap2.get(l);
            if (i1.equals(i2)) 
                outMap.put(l, i1);
            else if (i1 == NOTHING)
            	outMap.put(l, i2);
            else if (i2 == NOTHING)
            	outMap.put(l, i1);
            else
                outMap.put(l, UNKNOWN);
        }
    }
    

    protected void flowThrough(Object inValue, Object unit,
            Object outValue)
    {
        HashMap     in  = (HashMap) inValue;
        HashMap     out = (HashMap) outValue;
        Stmt    s   = (Stmt)    unit;

        out.clear();

        List<Local> preserve = new ArrayList();
        preserve.addAll(locals);
        for (ValueBox vb : (Collection<ValueBox>)s.getDefBoxes()) {
            preserve.remove(vb.getValue());
        }

        for (Local l : preserve) {
            out.put(l, in.get(l));
        }

        if (s instanceof DefinitionStmt) {
            DefinitionStmt ds = (DefinitionStmt) s;
            Value lhs = ds.getLeftOp();
            Value rhs = ds.getRightOp();

            if (rhs instanceof CastExpr) {
				CastExpr castExpr = (CastExpr) rhs;
            	rhs = castExpr.getOp();
            }
            
            if (lhs instanceof Local && lhs.getType() instanceof RefLikeType) {
                if (rhs instanceof NewExpr ||
                    rhs instanceof InvokeExpr || 
                    rhs instanceof ParameterRef || 
                    rhs instanceof FieldRef || 
                    rhs instanceof ThisRef ||
                    rhs instanceof ArrayRef) {
                    //expression could have changed, hence assign a fresh number
                    //(thisref and parameterref cannot actually change but whatever...)
                    out.put(lhs, nextNumber++);
                } else if (rhs instanceof Local) {
                    out.put(lhs, in.get(rhs));
                } else out.put(lhs, UNKNOWN);
            }
        }
    }

    protected void copy(Object source, Object dest)
    {
        HashMap sourceMap = (HashMap) source;
        HashMap destMap   = (HashMap) dest;
            
        for (Local l : (Collection<Local>) locals) {
            destMap.put (l, sourceMap.get(l));
        }
    }

    /** Initial most conservative value: has to be {@link UNKNOWN} (top). */
    protected Object entryInitialFlow()
    {
        HashMap m = new HashMap();
        for (Local l : (Collection<Local>) locals) {
            m.put(l, UNKNOWN);
        }
        return m;
    }

    /** Initial bottom value: objects have no definitions. */
    protected Object newInitialFlow()
    {
        HashMap m = new HashMap();
        for (Local l : (Collection<Local>) locals) {
            m.put(l, NOTHING);
        }
        return m;
    }
    
    /**
     * Returns a string (natural number) representation of the instance key associated with l
     * at statement s or <code>null</code> if there is no such key associated or <code>UNKNOWN</code> if 
     * the value of l at s is {@link #UNKNOWN}. 
     * @param l any local of the associated method
     * @param s the statement at which to check
     */
    public String instanceKeyString(Local l, Stmt s) {
        Object ln = ((HashMap)getFlowBefore(s)).get(l);
        if(ln==null) {
        	return null;
        } else  if(ln==UNKNOWN) {
        	return UNKNOWN.toString();
        }
        return ln.toString();
    }
    
    /**
     * Returns true if this analysis has any information about local l
     * at statement s (i.e. it is not {@link #UNKNOWN}).
     * In particular, it is safe to pass in locals/statements that are not
     * even part of the right method. In those cases <code>false</code>
     * will be returned.
     * Permits s to be <code>null</code>, in which case <code>false</code> will be returned.
     */
    public boolean hasInfoOn(Local l, Stmt s) {
    	HashMap flowBefore = (HashMap) getFlowBefore(s);
    	if(flowBefore==null) {
    		return false;
    	} else {
    		Object info = flowBefore.get(l);
    		return info!=null && info!=UNKNOWN;
    	}
    }
    
    /**
     * @return true if values of l1 (at s1) and l2 (at s2) have the
     * exact same object IDs, i.e. at statement s1 the variable l1 must point to the same object
     * as l2 at s2.
     */
    public boolean mustAlias(Local l1, Stmt s1, Local l2, Stmt s2) {
        Object l1n = ((HashMap)getFlowBefore(s1)).get(l1);
        Object l2n = ((HashMap)getFlowBefore(s2)).get(l2);

        if (l1n == UNKNOWN || l2n == UNKNOWN)
            return false;

        return l1n == l2n;
    }
}
