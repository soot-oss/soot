package soot.dava.toolkits.base.finders;

import soot.*;
import java.util.*;
import soot.dava.*;
import soot.util.*;
import soot.dava.internal.asg.*;

public class ExceptionNode
{
    private IteratorableSet body, tryBody, catchBody;
    private boolean dirty;
    private LinkedList exitList, catchList;
    private SootClass exception;
    private HashMap catch2except;
    private AugmentedStmt handlerAugmentedStmt;

    public ExceptionNode( IteratorableSet tryBody, SootClass exception, AugmentedStmt handlerAugmentedStmt)
    {
	this.tryBody = tryBody;
	this.catchBody = null;
	this.exception = exception;
	this.handlerAugmentedStmt = handlerAugmentedStmt;

	body = new IteratorableSet();
	body.addAll( tryBody);

	dirty = true;
	exitList = null;
	catchList = null;

	catch2except = null;
    }

    public boolean add_TryStmts( Collection c)
    {
	Iterator it = c.iterator();
	while (it.hasNext()) 
	    if (add_TryStmt( (AugmentedStmt) it.next()) == false)
		return false;

	return true;
    }

    public boolean add_TryStmt( AugmentedStmt as)
    {
	if ((body.contains( as)) || (tryBody.contains( as)))
	    return false;
	
	body.add( as);
	tryBody.add( as);

	return true;
    }

    public void refresh_CatchBody( ExceptionFinder ef)
    {
	if (catchBody != null)
	    body.removeAll( catchBody);

	catchBody = ef.get_CatchBody( handlerAugmentedStmt);
	body.addAll( catchBody);
    }


    public IteratorableSet get_Body()
    {
	return body;
    }
    
    public IteratorableSet get_TryBody()
    {
	return tryBody;
    }

    public IteratorableSet get_CatchBody()
    {
	return catchBody;
    }

    public boolean remove( AugmentedStmt as)
    {
	if (body.contains( as) == false)
	    return false;

	if (tryBody.contains( as))
	    tryBody.remove( as);
	else if ((catchBody != null) && (catchBody.contains( as))) {
	    catchBody.remove( as);
	    dirty = true;
	}
	else
	    return false;

	body.remove( as);

	return true;
    }

    public List get_CatchExits()
    {
	if (catchBody == null)
	    return null;

	if (dirty) {
	    exitList = new LinkedList();
	    dirty = false;

	    Iterator it = catchBody.iterator();
	    while (it.hasNext()) {
		AugmentedStmt as = (AugmentedStmt) it.next();

		Iterator sit = as.bsuccs.iterator();
		while (sit.hasNext())
		    if (catchBody.contains( sit.next()) == false) {
			exitList.add( as);
			break;
		    }
	    }
	}
	
	return exitList;
    }

    public void splitOff_ExceptionNode( IteratorableSet newTryBody, AugmentedStmtGraph asg, IteratorableSet enlist)
    {
	IteratorableSet oldTryBody = new IteratorableSet();
	oldTryBody.addAll( tryBody);

	IteratorableSet oldBody = new IteratorableSet();
	oldBody.addAll( body);

	Iterator it = newTryBody.iterator();
	while (it.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) it.next();
	    if (remove( as) == false) {
		StringBuffer b = new StringBuffer();
		it = newTryBody.iterator();
		while (it.hasNext())
		    b.append( "\n" + ((AugmentedStmt) it.next()).toString());
		b.append( "\n-");

		it = oldTryBody.iterator();
		while (it.hasNext())
		    b.append( "\n" + ((AugmentedStmt) it.next()).toString());
		b.append( "\n-");

		it = oldBody.iterator();
		while (it.hasNext())
		    b.append( "\n" + ((AugmentedStmt) it.next()).toString());
		b.append( "\n-");

	
		
		throw new RuntimeException( "Tried to split off a new try body that isn't in the old one.\n"+ as+"\n - " + b.toString());
	    }
	}

	asg.clone_Body( catchBody);

	AugmentedStmt
	    oldCatchTarget = handlerAugmentedStmt,
	    newCatchTarget = asg.get_CloneOf( handlerAugmentedStmt);

	Iterator tbit = newTryBody.iterator();
	while (tbit.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) tbit.next();

	    as.remove_CSucc( oldCatchTarget);
	    oldCatchTarget.remove_CPred( as);
	}

	tbit = tryBody.iterator();
	while (tbit.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) tbit.next();

	    as.remove_CSucc( newCatchTarget);
	    newCatchTarget.remove_CPred( as);
	}
	
	Iterator enlit = enlist.snapshotIterator();
	while (enlit.hasNext()) {
	    ExceptionNode en = (ExceptionNode) enlit.next();
	    
	    if (this == en)
		continue;
	    
	    if (catchBody.isSupersetOf( en.get_Body())) {
		
		IteratorableSet clonedTryBody = new IteratorableSet();
		
		Iterator trit = en.get_TryBody().iterator();
		while (trit.hasNext())
		    clonedTryBody.add( asg.get_CloneOf( (AugmentedStmt) trit.next()));
		
		enlist.addLast( new ExceptionNode( clonedTryBody, en.exception, asg.get_CloneOf( en.handlerAugmentedStmt)));
	    }
	}

	enlist.addLast( new ExceptionNode( newTryBody, exception, asg.get_CloneOf( handlerAugmentedStmt)));

	asg.find_Dominators();
    }

    public void add_CatchBody( ExceptionNode other)
    {
	if (other.get_CatchList() == null) {
	    add_CatchBody( other.get_CatchBody(), other.get_Exception());
	    return;
	}

	Iterator it = other.get_CatchList().iterator();
	while (it.hasNext()) {
	    IteratorableSet c = (IteratorableSet) it.next();

	    add_CatchBody( c, other.get_Exception( c));
	}
    }

    public void add_CatchBody( IteratorableSet newCatchBody, SootClass except)
    {
	if (catchList == null) {
	    catchList = new LinkedList();
	    catchList.addLast( catchBody);

	    catch2except = new HashMap();
	    catch2except.put( catchBody, exception);
	}

	body.addAll( newCatchBody);
	catchList.addLast( newCatchBody);
	catch2except.put( newCatchBody, except);
    }

    public List get_CatchList()
    {
	List l = catchList;

	if (l == null) {
	    l = new LinkedList();
	    l.add( catchBody);
	}

	return l;
    }

    public Map get_ExceptionMap()
    {
	Map m = catch2except;

	if (m == null) {
	    m = new HashMap();
	    m.put( catchBody, exception);
	}
	
	return m;
    }

    public SootClass get_Exception()
    {
	return exception;
    }

    public SootClass get_Exception( IteratorableSet catchBody)
    {
	if (catch2except == null)
	    return exception;

	return (SootClass) catch2except.get( catchBody);
    }

    public void dump()
    {
	System.out.println("try {");
	Iterator tit = get_TryBody().iterator();
	while (tit.hasNext())
	    System.out.println( "\t" + tit.next());
	System.out.println( "}");
	
	Iterator cit = get_CatchList().iterator();
	while (cit.hasNext()) {
	    IteratorableSet catchBody = (IteratorableSet) cit.next();
	    
	    System.out.println( "catch " + get_ExceptionMap().get( catchBody) + " {");
	    Iterator cbit = catchBody.iterator();
	    while (cbit.hasNext()) 
		System.out.println( "\t" + cbit.next());
	    System.out.println("}");
	    
	}
    }
}
