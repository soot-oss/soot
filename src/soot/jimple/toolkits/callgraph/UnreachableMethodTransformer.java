/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.toolkits.callgraph;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.*;
import java.util.*;

public class UnreachableMethodTransformer extends BodyTransformer
{
    protected void internalTransform(Body b, String phaseName, Map options) {
        //System.out.println( "Performing UnreachableMethodTransformer" );
        ReachableMethods reachableMethods = Scene.v().getReachableMethods();
        SootMethod method = b.getMethod();
        //System.out.println( "Method: " + method.getName() );
        if( reachableMethods.contains( method ) ) return;

        JimpleBody body = (JimpleBody) method.getActiveBody();

        PatchingChain units = (PatchingChain) body.getUnits();
        List list = new Vector();

        Local tmpRef = Jimple.v().newLocal( "tmpRef", RefType.v( "java.io.PrintStream" ) );
        body.getLocals().add(tmpRef);
        list.add( Jimple.v().newAssignStmt( tmpRef, Jimple.v().newStaticFieldRef( 
            Scene.v().getField( "<java.lang.System: java.io.PrintStream out>" ).makeRef() ) ) );

        SootMethod toCall = Scene.v().getMethod( "<java.lang.Thread: void dumpStack()>" );
        list.add( Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr( toCall.makeRef() ) ) );

        toCall = Scene.v().getMethod( "<java.io.PrintStream: void println(java.lang.String)>" );
        list.add( Jimple.v().newInvokeStmt( Jimple.v().newVirtualInvokeExpr( 
            tmpRef, toCall.makeRef(), StringConstant.v( "Executing supposedly unreachable method:" ) ) ) );
        list.add( Jimple.v().newInvokeStmt( Jimple.v().newVirtualInvokeExpr( 
            tmpRef, toCall.makeRef(), StringConstant.v( "\t" + method.getDeclaringClass().getName() + "." + method.getName() ) ) ) );

        toCall = Scene.v().getMethod( "<java.lang.System: void exit(int)>" );
        list.add( Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr( toCall.makeRef(), IntConstant.v( 1 ) ) ) );

        /*
        Stmt r;
        if( method.getReturnType() instanceof VoidType ) {
            list.add( r=Jimple.v().newReturnVoidStmt() );
        } else if( method.getReturnType() instanceof RefLikeType ) {
            list.add( r=Jimple.v().newReturnStmt( NullConstant.v() ) );
        } else if( method.getReturnType() instanceof PrimType ) {
            if( method.getReturnType() instanceof DoubleType ) {
                list.add( r=Jimple.v().newReturnStmt( DoubleConstant.v( 0 ) ) );
            } else if( method.getReturnType() instanceof LongType ) {
                list.add( r=Jimple.v().newReturnStmt( LongConstant.v( 0 ) ) );
            } else if( method.getReturnType() instanceof FloatType ) {
                list.add( r=Jimple.v().newReturnStmt( FloatConstant.v( 0 ) ) );
            } else {
                list.add( r=Jimple.v().newReturnStmt( IntConstant.v( 0 ) ) );
            }
        } else {
            throw new RuntimeException( "Wrong return method type: " + method.getReturnType() );
        }
        */

        /*
        if( method.getName().equals( "<init>" ) || method.getName().equals( "<clinit>" ) ) {

            Object o = units.getFirst();
            boolean insertFirst = false;
            while( true ) {
                //System.out.println( "Unit: " + o );
                //System.out.println( "\tClass: " + o.getClass() );
                if( o == null ) {
                    insertFirst = true;
                    break;
                }
                if( o instanceof JInvokeStmt ) {
                    JInvokeStmt stmt = (JInvokeStmt) o;
                    if( (stmt.getInvokeExpr() instanceof SpecialInvokeExpr) ) {
                        SootMethodRef 
                        break;
                    }
                }
                o = units.getSuccOf( o );
            }
            if( insertFirst ) {
                units.insertBefore( list, units.getFirst() );
            } else {
                units.insertAfter( list, o ) ;
            }
        } else {
            */
        {
            units.insertBefore( list, units.getFirst() );
        }
        /*
        ArrayList toRemove = new ArrayList();
        for( Iterator sIt = units.iterator(r); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if(s == r) continue;
            toRemove.add(s);
        }
        for( Iterator sIt = toRemove.iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            units.getNonPatchingChain().remove(s);
        }
        body.getTraps().clear();
        */
    }
}
