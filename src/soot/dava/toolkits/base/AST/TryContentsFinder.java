/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
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

package soot.dava.toolkits.base.AST;

import soot.*;
import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.dava.internal.AST.*;

public class TryContentsFinder extends ASTAnalysis
{
    public TryContentsFinder( Singletons.Global g ) {}
    public static TryContentsFinder v() { return G.v().soot_dava_toolkits_base_AST_TryContentsFinder(); }

    private IterableSet curExceptionSet = new IterableSet();
    private HashMap node2ExceptionSet = new HashMap();
    
    public int getAnalysisDepth()
    {
	return ANALYSE_VALUES;
    }
    
    public IterableSet remove_CurExceptionSet()
    {
	IterableSet s = curExceptionSet;

	set_CurExceptionSet( new IterableSet());

	return s;
    }

    public void set_CurExceptionSet( IterableSet curExceptionSet)
    {
	this.curExceptionSet = curExceptionSet;
    }
    
    public void analyseThrowStmt( ThrowStmt s)
    {
	Value op = ((ThrowStmt) s).getOp();
	
	if (op instanceof Local) 
	    add_ThrownType( ((Local) op).getType());
	else if (op instanceof FieldRef) 
	    add_ThrownType( ((FieldRef) op).getType());
    }

    private void add_ThrownType( Type t)
    {
	if (t instanceof RefType)
	    curExceptionSet.add( ((RefType) t).getSootClass());
    }

    public void analyseInvokeExpr( InvokeExpr ie)
    {
	curExceptionSet.addAll( ie.getMethod().getExceptions());
    }

    public void analyseInstanceInvokeExpr( InstanceInvokeExpr iie) 
    {
	analyseInvokeExpr( iie);
    }

    public void analyseASTNode( ASTNode n)
    {
	if (n instanceof ASTTryNode) {

	    ASTTryNode tryNode = (ASTTryNode) n;
	    
	    ArrayList toRemove = new ArrayList();
	    IterableSet tryExceptionSet = (IterableSet) node2ExceptionSet.get( tryNode.get_TryBodyContainer());
	    if (tryExceptionSet == null) {
		tryExceptionSet = new IterableSet();
		node2ExceptionSet.put( tryNode.get_TryBodyContainer(), tryExceptionSet);
	    }
	    
	    List catchBodies = tryNode.get_CatchList();
	    List subBodies = tryNode.get_SubBodies();
	    
	    Iterator cit = catchBodies.iterator();
	    while (cit.hasNext()) {
		Object catchBody = cit.next();
		SootClass exception = (SootClass) tryNode.get_ExceptionMap().get( catchBody);
		
		if ((catches_Exception( tryExceptionSet, exception) == false) && (catches_RuntimeException( exception) == false))
		    toRemove.add( catchBody);
	    }
	    
	    Iterator trit = toRemove.iterator();
	    while (trit.hasNext()) {
		Object catchBody = trit.next();
		
		subBodies.remove( catchBody);
		catchBodies.remove( catchBody);
	    }

	    IterableSet passingSet = (IterableSet) tryExceptionSet.clone();
	    cit = catchBodies.iterator();
	    while (cit.hasNext())
		passingSet.remove( tryNode.get_ExceptionMap().get( cit.next()));

	    cit = catchBodies.iterator();
	    while (cit.hasNext())
		passingSet.addAll( get_ExceptionSet( cit.next()));

	    node2ExceptionSet.put( n, passingSet);
	}

	else {
	    Iterator sbit = n.get_SubBodies().iterator();
	    while (sbit.hasNext()) {
		Iterator it = ((List) sbit.next()).iterator();
		while (it.hasNext())
		    add_ExceptionSet( n, get_ExceptionSet( it.next()));
	    }
	}


	remove_CurExceptionSet();
    }

    public IterableSet get_ExceptionSet( Object node)
    {
	IterableSet fullSet = (IterableSet) node2ExceptionSet.get( node);
	if (fullSet == null) {
	    fullSet = new IterableSet();
	    node2ExceptionSet.put( node, fullSet);
	}

	return fullSet;
    }

    public void add_ExceptionSet( Object node, IterableSet s)
    {
	IterableSet fullSet = (IterableSet) node2ExceptionSet.get( node);
	if (fullSet == null) {
	    fullSet = new IterableSet();
	    node2ExceptionSet.put( node, fullSet);
	}
	
	fullSet.addAll( s);
    }

    private boolean catches_Exception( IterableSet tryExceptionSet, SootClass c)
    {
	Iterator it = tryExceptionSet.iterator();
	while (it.hasNext()) {
	    SootClass thrownException = (SootClass) it.next();

	    while (true) {
		if (thrownException == c)
		    return true;

		if (thrownException.hasSuperclass() == false)
		    break;

		thrownException = thrownException.getSuperclass();
	    }
	}

	return false;
    }

    private boolean catches_RuntimeException( SootClass c)
    {
	if ((c == Scene.v().getSootClass( "java.lang.Throwable")) ||
	    (c == Scene.v().getSootClass( "java.lang.Exception")))
	    return true;

	SootClass 
	    caughtException = c,
	    runtimeException = Scene.v().getSootClass( "java.lang.RuntimeException");
	
	while (true) {
	    if (caughtException == runtimeException)
		return true;
	    
	    if (caughtException.hasSuperclass() == false)
		return false;

	    caughtException = caughtException.getSuperclass();
	}
    }
}
