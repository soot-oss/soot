package soot.dava.internal.SET;

import soot.*;
import java.util.*;
import soot.util.*;
import soot.dava.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.misc.*;
import soot.dava.toolkits.base.finders.*;

public class SETTryNode extends SETNode
{
    private ExceptionNode en;
    private DavaBody davaBody;
    private AugmentedStmtGraph asg;

    public SETTryNode( IteratorableSet body, ExceptionNode en, AugmentedStmtGraph asg, DavaBody davaBody) 
    {
	super( body);
	this.en = en;
	this.asg = asg;
	this.davaBody = davaBody;

	add_SubBody( en.get_TryBody());

	Iterator it = en.get_CatchList().iterator();
	while (it.hasNext()) 
	    add_SubBody( (IteratorableSet) it.next());
    }

    public AugmentedStmt get_EntryStmt()
    {
	return ((SETNode) ((IteratorableSet) body2childChain.get( en.get_TryBody())).getFirst()).get_EntryStmt();
    }

    public IteratorableSet get_NaturalExits()
    {
	IteratorableSet c = new IteratorableSet();
	
	Iterator it = subBodies.iterator();
	while (it.hasNext()) {

	    Iterator eit = ((SETNode) ((IteratorableSet) body2childChain.get( it.next())).getLast()).get_NaturalExits().iterator();
	    while (eit.hasNext()) {
		Object o = eit.next();

		if (c.contains( o) == false)
		    c.add( o);
	    }
	}

	return c;
    }

    public ASTNode emit_AST()
    {
	LinkedList catchList = new LinkedList();
	HashMap 
	    exceptionMap = new HashMap(),
	    paramMap     = new HashMap();

	Iterator it = en.get_CatchList().iterator();
	while (it.hasNext()) {
	    IteratorableSet catchBody = (IteratorableSet) it.next();

	    List astBody = emit_ASTBody( (IteratorableSet) body2childChain.get( catchBody));
	    exceptionMap.put( astBody, en.get_Exception( catchBody));
	    catchList.addLast( astBody);

	    Iterator bit = catchBody.iterator();
	    while (bit.hasNext()) {
		Stmt s = ((AugmentedStmt) bit.next()).get_Stmt();

		if (s instanceof IdentityStmt) {
		    IdentityStmt ids = (IdentityStmt) s;
		    
		    Value 
			rightOp = ids.getRightOp(),
			leftOp =  ids.getLeftOp();

		    if (rightOp instanceof CaughtExceptionRef) {
			paramMap.put( astBody, leftOp);
			break;
		    }
		}
	    }
	}

	return new ASTTryNode( get_Label(), emit_ASTBody( (IteratorableSet) body2childChain.get( en.get_TryBody())), catchList, exceptionMap, paramMap);
    }

    protected boolean resolve( SETNode parent)
    {
	Iterator sbit = parent.get_SubBodies().iterator();

    subBody_Loop:
	while (sbit.hasNext()) {
	    IteratorableSet subBody = (IteratorableSet) sbit.next();
	    
	    if (subBody.intersects( get_Body())) {

		if (subBody.isSupersetOf( get_Body()))
		    continue subBody_Loop;

		Iterator it = subBodies.iterator();
		while (it.hasNext())
		    if (subBody.isSubsetOf( (IteratorableSet) it.next()))
			continue subBody_Loop;

		IteratorableSet newTryBody = subBody.intersection( en.get_TryBody());
		if (newTryBody.isStrictSubsetOf( en.get_TryBody())) {
		    en.splitOff_ExceptionNode( newTryBody, asg, davaBody.get_ExceptionFacts());
		    return false;
		}
	    }
	}

	return true;
    }
}


