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
    private HashMap cb2clone;

    public SETTryNode( IterableSet body, ExceptionNode en, AugmentedStmtGraph asg, DavaBody davaBody) 
    {
	super( body);
	this.en = en;
	this.asg = asg;
	this.davaBody = davaBody;

	add_SubBody( en.get_TryBody());

	cb2clone = new HashMap();

	Iterator it = en.get_CatchList().iterator();
	while (it.hasNext()) {
	    IterableSet catchBody = (IterableSet) it.next();
	    IterableSet clone     = (IterableSet) catchBody.clone();

	    cb2clone.put( catchBody, clone);
	    add_SubBody( clone);
	}
    }

    public AugmentedStmt get_EntryStmt()
    {
	return ((SETNode) ((IterableSet) body2childChain.get( en.get_TryBody())).getFirst()).get_EntryStmt();
    }

    public IterableSet get_NaturalExits()
    {
	IterableSet c = new IterableSet();
	
	Iterator it = subBodies.iterator();
	while (it.hasNext()) {

	    Iterator eit = ((SETNode) ((IterableSet) body2childChain.get( it.next())).getLast()).get_NaturalExits().iterator();
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
	    IterableSet originalCatchBody = (IterableSet) it.next();
	    IterableSet catchBody = (IterableSet) cb2clone.get( originalCatchBody);

	    List astBody = emit_ASTBody( (IterableSet) body2childChain.get( catchBody));
	    exceptionMap.put( astBody, en.get_Exception( originalCatchBody));
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

	return new ASTTryNode( get_Label(), emit_ASTBody( (IterableSet) body2childChain.get( en.get_TryBody())), catchList, exceptionMap, paramMap);
    }

    protected boolean resolve( SETNode parent)
    {
	Iterator sbit = parent.get_SubBodies().iterator();
	while (sbit.hasNext()) {

	    IterableSet subBody = (IterableSet) sbit.next();
	    if (subBody.intersects( en.get_TryBody())) {

		IterableSet childChain = (IterableSet) parent.get_Body2ChildChain().get( subBody);
		Iterator ccit = childChain.iterator();
		while (ccit.hasNext()) {

		    SETNode child = (SETNode) ccit.next();
		    IterableSet childBody = child.get_Body();

		    if ((childBody.intersects( en.get_TryBody()) == false) || (childBody.isSubsetOf( en.get_TryBody())))
			continue;

		    if (childBody.isSupersetOf( get_Body()))
			return true;

		    IterableSet newTryBody = childBody.intersection( en.get_TryBody());
		    if (newTryBody.isStrictSubsetOf( en.get_TryBody())) {
			en.splitOff_ExceptionNode( newTryBody, asg, davaBody.get_ExceptionFacts());
			return false;
		    }

		    Iterator cit = en.get_CatchList().iterator();
		    while (cit.hasNext()) {

			Iterator bit = ((IterableSet) cb2clone.get( cit.next())).snapshotIterator();
			while (bit.hasNext()) {
			    AugmentedStmt as = (AugmentedStmt) bit.next();
			    
			    if (childBody.contains( as) == false) 
				remove_AugmentedStmt( as);
			    
			    else if (child instanceof SETControlFlowNode) {
				SETControlFlowNode scfn = (SETControlFlowNode) child;
				
				if ((scfn.get_CharacterizingStmt() == as) ||
				    ((as.cpreds.size() == 1) && (as.get_Stmt() instanceof GotoStmt) && (scfn.get_CharacterizingStmt() == as.cpreds.get(0))))
				    
				    remove_AugmentedStmt( as);
			    }
			}
		    }

		    return true;
		}
	    }
	}
	return true;
    }
}


