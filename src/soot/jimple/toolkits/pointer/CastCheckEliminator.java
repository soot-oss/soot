/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003, 2004 Ondrej Lhotak
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
import java.util.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.*;
import soot.util.*;
import soot.jimple.*;

/** A flow analysis that detects redundant cast checks. */
public class CastCheckEliminator extends ForwardBranchedFlowAnalysis {
    Map unitToKill = new HashMap();
    Map unitToGenFallThrough = new HashMap();
    Map unitToGenBranch = new HashMap();
    LocalTypeSet emptySet;

    public CastCheckEliminator(BriefUnitGraph cfg) {
        super(cfg);
        makeInitialSet();
        doAnalysis();
        tagCasts();
    }

    /** Put the results of the analysis into tags in cast statements. */
    protected void tagCasts() {
        for( Iterator sIt = ((UnitGraph)graph).getBody().getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if( s instanceof AssignStmt ) {
                AssignStmt as = (AssignStmt) s;
                Value rhs = as.getRightOp();
                if( rhs instanceof CastExpr ) {
                    CastExpr cast = (CastExpr) rhs;
                    Type t = cast.getCastType();
                    if( t instanceof RefType ) {
                        if( cast.getOp() instanceof Local ) {
                            Local l = (Local) cast.getOp(); 
                            LocalTypeSet set = (LocalTypeSet) unitToBeforeFlow.get(s);
                            s.addTag( new CastCheckTag( set.get( set.indexOf(
                                    l, (RefType) t ) ) ) );
                        } else {
                            NullConstant nc = (NullConstant) cast.getOp();
                            s.addTag( new CastCheckTag( true ) );
                        }
                    }
                }
            }
        }
    }

    /** Find all the locals of reference type and all the types used in casts
     * to initialize the mapping from locals and types to bits in the bit vector
     * in LocalTypeSet. */
    protected void makeInitialSet() {
        // Find all locals of reference type
        Chain locals = ((UnitGraph)graph).getBody().getLocals();
        List refLocals = new ArrayList();
        for( Iterator lIt = locals.iterator(); lIt.hasNext(); ) {
            final Local l = (Local) lIt.next();
            if( l.getType() instanceof RefType ) {
                refLocals.add( l );
            }
        }

        // Find types of all casts
        List types = new ArrayList();
        for( Iterator sIt = ((UnitGraph)graph).getBody().getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if( s instanceof AssignStmt ) {
                AssignStmt as = (AssignStmt) s;
                Value rhs = as.getRightOp();
                if( rhs instanceof CastExpr ) {
                    Type t = ( (CastExpr) rhs ).getCastType();
                    if( t instanceof RefType && !types.contains( t ) ) {
                        types.add( t );
                    }
                }
            }
        }
        
        emptySet = new LocalTypeSet( refLocals, types );
    }
    

    /** Returns a new, aggressive (local,type) set. */
    protected Object newInitialFlow() {
        LocalTypeSet ret = (LocalTypeSet) emptySet.clone();
        ret.setAllBits();
        return ret;
    }

    /** This is the flow function as described in the assignment write-up. */
    protected void flowThrough( Object inValue, Unit unit, List outFallValues,
                                List outBranchValues ) 
    {
        final LocalTypeSet in = (LocalTypeSet) inValue;
        final LocalTypeSet out = (LocalTypeSet) in.clone();
        LocalTypeSet outBranch = out; // aliased to out unless unit is IfStmt
        final Stmt stmt = (Stmt) unit;
        
        // First kill all locals defined in this statement
        for( Iterator bIt = stmt.getDefBoxes().iterator(); bIt.hasNext(); ) {
            final ValueBox b = (ValueBox) bIt.next();
            Value v = b.getValue();
            if( v instanceof Local && v.getType() instanceof RefType ) {
                out.killLocal( (Local) v );
            }
        }
        
        // An AssignStmt may be a new, a simple copy, or a cast
        if( stmt instanceof AssignStmt ) {
            AssignStmt astmt = (AssignStmt) stmt;
            Value rhs = astmt.getRightOp();
            Value lhs = astmt.getLeftOp();
            if( lhs instanceof Local && rhs.getType() instanceof RefType ) {
                Local l = (Local) lhs;
                if( rhs instanceof NewExpr ) {
                    out.localMustBeSubtypeOf( l, (RefType) rhs.getType() );
                } else if( rhs instanceof CastExpr ) {
                    CastExpr cast = (CastExpr) rhs;
                    Type castType = cast.getCastType();
                    if( castType instanceof RefType 
                    &&  cast.getOp() instanceof Local ) {
                        RefType refType = (RefType) castType;
                        Local opLocal = (Local) cast.getOp();
                        out.localCopy( l, opLocal );
                        out.localMustBeSubtypeOf( l, refType );
                        out.localMustBeSubtypeOf( opLocal, refType );
                    }
                } else if( rhs instanceof Local ) {
                    out.localCopy( l, (Local) rhs );
                }
            }
            
            // Handle if statements
        } else if( stmt instanceof IfStmt ) {
            IfStmt ifstmt = (IfStmt) stmt;
            
            // This do ... while(false) is here so I can break out of it rather
            // than having to have seven nested if statements. Silly people who
            // took goto's out of the language... <grumble> <grumble>
            do {
                if( graph.getPredsOf( stmt ).size() != 1 ) break;
                Object predecessor = (Stmt) graph.getPredsOf( stmt ).get(0);
                if( !( predecessor instanceof AssignStmt ) ) break;
                AssignStmt pred = (AssignStmt) predecessor;
                if( !(pred.getRightOp() instanceof InstanceOfExpr ) ) break;
                InstanceOfExpr iofexpr = (InstanceOfExpr) pred.getRightOp();
                if( !(iofexpr.getCheckType() instanceof RefType ) ) break;
                if( !(iofexpr.getOp() instanceof Local ) ) break;
                ConditionExpr c = (ConditionExpr) ifstmt.getCondition();
                if( !c.getOp1().equals( pred.getLeftOp() ) ) break;
                if( !( c.getOp2() instanceof IntConstant ) ) break;
                if( ( (IntConstant) c.getOp2() ).value != 0 ) break;
                if( c instanceof NeExpr ) {
                    // The IfStmt is like this:
                    // if x instanceof t goto somewhere_else
                    // So x is of type t on the taken branch
                    outBranch = (LocalTypeSet) out.clone();
                    outBranch.localMustBeSubtypeOf( (Local) iofexpr.getOp(),
                                                    (RefType) iofexpr.getCheckType() );
                } else if( c instanceof EqExpr ) {
                    // The IfStmt is like this:
                    // if !(x instanceof t) goto somewhere_else
                    // So x is of type t on the fallthrough branch
                    outBranch = (LocalTypeSet) out.clone();
                    out.localMustBeSubtypeOf( (Local) iofexpr.getOp(),
                                              (RefType) iofexpr.getCheckType() );
                }
            } while( false );
        }
        
        // Now copy the computed (local,type) set to all successors
        for( Iterator it = outFallValues.iterator(); it.hasNext(); ) {
            copy( out, it.next() );
        }
        for( Iterator it = outBranchValues.iterator(); it.hasNext(); ) {
            copy( outBranch, it.next() );
        }
    }

    protected void copy( Object source, Object dest ) {
        LocalTypeSet s = (LocalTypeSet) source;
        LocalTypeSet d = (LocalTypeSet) dest;
        d.and( s );
        d.or( s );
    }

    // The merge operator is set intersection.
    protected void merge( Object in1, Object in2, Object out ) {
        LocalTypeSet o = (LocalTypeSet) out;
        o.setAllBits();
        o.and( (LocalTypeSet) in1 );
        o.and( (LocalTypeSet) in2 );
    }
    
    /** Returns a new, aggressive (local,type) set. */
    protected Object entryInitialFlow() {
        LocalTypeSet ret = (LocalTypeSet) emptySet.clone();
        return ret;
    }
}

