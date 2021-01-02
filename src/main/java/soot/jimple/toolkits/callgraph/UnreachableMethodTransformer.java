package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ondrej Lhotak
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.List;
import java.util.Map;
import java.util.Vector;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;

public class UnreachableMethodTransformer extends BodyTransformer {

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    // System.out.println( "Performing UnreachableMethodTransformer" );

    SootMethod method = b.getMethod();
    final Scene scene = Scene.v();
    // System.out.println( "Method: " + method.getName() );
    if (scene.getReachableMethods().contains(method)) {
      return;
    }

    final Jimple jimp = Jimple.v();
    List<Unit> list = new Vector<Unit>();

    Local tmpRef = jimp.newLocal("tmpRef", RefType.v("java.io.PrintStream"));
    b.getLocals().add(tmpRef);
    list.add(jimp.newAssignStmt(tmpRef,
        jimp.newStaticFieldRef(scene.getField("<java.lang.System: java.io.PrintStream out>").makeRef())));

    SootMethod toCall = scene.getMethod("<java.lang.Thread: void dumpStack()>");
    list.add(jimp.newInvokeStmt(jimp.newStaticInvokeExpr(toCall.makeRef())));

    toCall = scene.getMethod("<java.io.PrintStream: void println(java.lang.String)>");
    list.add(jimp.newInvokeStmt(
        jimp.newVirtualInvokeExpr(tmpRef, toCall.makeRef(), StringConstant.v("Executing supposedly unreachable method:"))));
    list.add(jimp.newInvokeStmt(jimp.newVirtualInvokeExpr(tmpRef, toCall.makeRef(),
        StringConstant.v("\t" + method.getDeclaringClass().getName() + "." + method.getName()))));

    toCall = scene.getMethod("<java.lang.System: void exit(int)>");
    list.add(jimp.newInvokeStmt(jimp.newStaticInvokeExpr(toCall.makeRef(), IntConstant.v(1))));

    /*
     * Stmt r; if( method.getReturnType() instanceof VoidType ) { list.add( r=Jimple.v().newReturnVoidStmt() ); } else if(
     * method.getReturnType() instanceof RefLikeType ) { list.add( r=Jimple.v().newReturnStmt( NullConstant.v() ) ); } else
     * if( method.getReturnType() instanceof PrimType ) { if( method.getReturnType() instanceof DoubleType ) { list.add(
     * r=Jimple.v().newReturnStmt( DoubleConstant.v( 0 ) ) ); } else if( method.getReturnType() instanceof LongType ) {
     * list.add( r=Jimple.v().newReturnStmt( LongConstant.v( 0 ) ) ); } else if( method.getReturnType() instanceof FloatType
     * ) { list.add( r=Jimple.v().newReturnStmt( FloatConstant.v( 0 ) ) ); } else { list.add( r=Jimple.v().newReturnStmt(
     * IntConstant.v( 0 ) ) ); } } else { throw new RuntimeException( "Wrong return method type: " + method.getReturnType()
     * ); }
     */

    UnitPatchingChain units = b.getUnits();
    /*
     * if( method.getName().equals( "<init>" ) || method.getName().equals( "<clinit>" ) ) {
     *
     * Object o = units.getFirst(); boolean insertFirst = false; while( true ) { //System.out.println( "Unit: " + o );
     * //System.out.println( "\tClass: " + o.getClass() ); if( o == null ) { insertFirst = true; break; } if( o instanceof
     * JInvokeStmt ) { JInvokeStmt stmt = (JInvokeStmt) o; if( (stmt.getInvokeExpr() instanceof SpecialInvokeExpr) ) {
     * SootMethodRef break; } } o = units.getSuccOf( o ); } if( insertFirst ) { units.insertBefore( list, units.getFirst() );
     * } else { units.insertAfter( list, o ) ; } } else {
     */

    units.insertBefore(list, units.getFirst());

    /*
     * ArrayList toRemove = new ArrayList(); for( Iterator sIt = units.iterator(r); sIt.hasNext(); ) { final Stmt s = (Stmt)
     * sIt.next(); if(s == r) continue; toRemove.add(s); } for( Iterator sIt = toRemove.iterator(); sIt.hasNext(); ) { final
     * Stmt s = (Stmt) sIt.next(); units.getNonPatchingChain().remove(s); } body.getTraps().clear();
     */
  }
}
