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

package soot.dava.internal.SET;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.dava.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.javaRep.*;

public class SETStatementSequenceNode extends SETNode
{
    private DavaBody davaBody;
    private boolean hasContinue;

    public SETStatementSequenceNode( IterableSet body, DavaBody davaBody)
    {
	super( body);
	add_SubBody( body);

	this.davaBody = davaBody;

	hasContinue = false;
    }

    public SETStatementSequenceNode( IterableSet body)
    {
	this( body, null);
    }

    public boolean has_Continue()
    {
	return hasContinue;
    }

    public IterableSet get_NaturalExits()
    {
	IterableSet c = new IterableSet();
	AugmentedStmt last = (AugmentedStmt) get_Body().getLast();
	
	if ((last.csuccs != null) && (last.csuccs.isEmpty() == false))
	    c.add( last);

	return c; 
    }

    public ASTNode emit_AST()
    {
	List<Object> l = new LinkedList<Object>();
	
	boolean isStaticInitializer = davaBody.getMethod().getName().equals( SootMethod.staticInitializerName);
	
	Iterator it = get_Body().iterator();
	while (it.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) it.next();
	    Stmt s = as.get_Stmt();

	    if (davaBody != null) {
		
		if ((s instanceof ReturnVoidStmt) && (isStaticInitializer))
		    continue;

		if (s instanceof GotoStmt)
		    continue;

		if (s instanceof MonitorStmt)
		    continue;
		
		/*
		  January 12th 2006
		  Trying to fix the super problem we need to not ignore constructor unit
		  i.e. this or super
		  
		*/
		if (s == davaBody.get_ConstructorUnit()){
		    //System.out.println("ALLOWING this.init STMT TO GET ADDED..............SETStatementSequenceNode");
		    //  continue;
		}



		if (s instanceof IdentityStmt) {
		    IdentityStmt ids = (IdentityStmt) s;
		    
		    Value 
			rightOp = ids.getRightOp(),
			leftOp =  ids.getLeftOp();
		    
		    if (davaBody.get_ThisLocals().contains( leftOp))
			continue;
		    
		    if (rightOp instanceof ParameterRef)
			continue;

		    if (rightOp instanceof CaughtExceptionRef)
			continue;
		}
	    }

	    l.add( as);
	}

	if (l.isEmpty())
	    return null;
	else
	    return new ASTStatementSequenceNode( l);
    }

    public AugmentedStmt get_EntryStmt()
    {
	return (AugmentedStmt) get_Body().getFirst();
    }


    public void insert_AbruptStmt( DAbruptStmt stmt)
    {
	if (hasContinue)
	    return;

	get_Body().addLast( new AugmentedStmt( stmt));
	hasContinue = stmt.is_Continue();
    }

    protected boolean resolve( SETNode parent)
    {
	throw new RuntimeException( "Attempting auto-nest a SETStatementSequenceNode.");
    }
}
