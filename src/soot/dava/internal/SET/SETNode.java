package soot.dava.internal.SET;

import java.io.*;
import java.util.*;
import soot.util.*;
import soot.dava.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.finders.*;

public abstract class SETNode
{
    private IteratorableSet body;
    private SETNodeLabel label;

    protected SETNode parent;
    protected AugmentedStmt entryStmt;
    protected IteratorableSet predecessors, successors;
    protected LinkedList subBodies;
    protected Map body2childChain;

    public abstract IteratorableSet get_NaturalExits();
    public abstract ASTNode emit_AST();
    public abstract AugmentedStmt get_EntryStmt();
    protected abstract boolean resolve( SETNode parent);


    public SETNode( IteratorableSet body)
    {
	this.body = body;

	parent = null;
	label = new SETNodeLabel();
	subBodies = new LinkedList();
	body2childChain = new HashMap();
	predecessors = new IteratorableSet();
	successors = new IteratorableSet();
    }

    public void add_SubBody( IteratorableSet body)
    {
	subBodies.add( body);
	body2childChain.put( body, new IteratorableSet());
    }

    public Map get_Body2ChildChain()
    {
	return body2childChain;
    }

    public List get_SubBodies()
    {
	return subBodies;
    }

    public IteratorableSet get_Body()
    {
	return body;
    }

    public SETNodeLabel get_Label()
    {
	return label;
    }

    public SETNode get_Parent()
    {
	return parent;
    }

    public boolean contains( Object o)
    {
	return body.contains( o);
    }


    public IteratorableSet get_Successors()
    {
	return successors;
    }

    public IteratorableSet get_Predecessors()
    {
	return predecessors;
    }

    public boolean add_Child( SETNode child, IteratorableSet children)
    {
	if ((this == child) || (children.contains( child))) 
	    return false;
	
	children.add( child);
	child.parent = this;
	return true;
    }

    public boolean remove_Child( SETNode child, IteratorableSet children)
    {
	if ((this == child) || (children.contains( child) == false))
	    return false;

	children.remove( child);
	child.parent = null;
	return true;
    }

    public boolean insert_ChildBefore( SETNode child, SETNode point, IteratorableSet children )
    {
	if ((this == child) || (this == point) || (children.contains( point) == false))
	    return false;

	children.insertBefore( child, point);
	child.parent = this;
	return true;
    }

    public List emit_ASTBody( IteratorableSet children)
    {
	LinkedList l = new LinkedList();

	Iterator cit = children.iterator();
	while (cit.hasNext()) {
	    ASTNode astNode = ((SETNode) cit.next()).emit_AST();
	    
	    if (astNode != null)
		l.addLast( astNode);
	}
	
	return l;
    }
    

    /*
     *  Basic inter-SETNode utilities.
     */

    public IteratorableSet get_IntersectionWith( SETNode other)
    {
	return body.intersection( other.get_Body());
    }

    public boolean has_IntersectionWith( SETNode other)
    {
	Iterator bit = other.get_Body().iterator();
	while (bit.hasNext())
	    if (body.contains( bit.next()))
		return true;

	return false;
    }

    public boolean is_SupersetOf( SETNode other)
    {
	return body.isSupersetOf( other.get_Body());
    }

    public boolean is_StrictSupersetOf( SETNode other)
    {
	return body.isStrictSubsetOf( other.get_Body());
    }
    
    

    /* 
     *  Tree traversing utilities. 
     */

    public void find_SmallestSETNode( AugmentedStmt as)
    {
	Iterator sbit = subBodies.iterator();
	while (sbit.hasNext()) {
	    Iterator it = ((IteratorableSet) body2childChain.get( sbit.next())).iterator();
	    while (it.hasNext()) {
		SETNode child = (SETNode) it.next();
		
		if (child.contains( as)) {
		    child.find_SmallestSETNode( as);
		    return;
		}
	    }
	}
	    
	as.myNode = this;
    }

    public void find_LabeledBlocks( LabeledBlockFinder lbf)
    {
	Iterator sbit = subBodies.iterator();
	while (sbit.hasNext()) {
	    Iterator cit = ((IteratorableSet) body2childChain.get( sbit.next())).iterator();
	    while (cit.hasNext())
		((SETNode) cit.next()).find_LabeledBlocks( lbf);
	}
	
	lbf.perform_ChildOrder( this);
	lbf.find_LabeledBlocks( this);
    }

    public void find_StatementSequences( SequenceFinder sf, DavaBody davaBody)
    {
	Iterator sbit = subBodies.iterator();
	while (sbit.hasNext()) {

	    IteratorableSet body = (IteratorableSet) sbit.next();
	    IteratorableSet children = (IteratorableSet) body2childChain.get( body);
	    HashSet childUnion = new HashSet();

	    Iterator cit = children.iterator();
	    while (cit.hasNext()) {
		SETNode child = (SETNode) cit.next();

		child.find_StatementSequences( sf, davaBody);
		childUnion.addAll( child.get_Body()); 
	    }
	    
	    sf.find_StatementSequences( this, body, childUnion, davaBody);
	}
    }

    public void find_AbruptEdges( AbruptEdgeFinder aef)
    {
	Iterator sbit = subBodies.iterator();
	while (sbit.hasNext()) {
	    IteratorableSet body = (IteratorableSet) sbit.next();
	    IteratorableSet children = (IteratorableSet) body2childChain.get( body);
	    
	    Iterator cit = children.iterator();
	    while (cit.hasNext())
		((SETNode) cit.next()).find_AbruptEdges( aef);

	    aef.find_Continues( this, body, children);
	}

	sbit = subBodies.iterator();
	while (sbit.hasNext()) {
	    IteratorableSet children = (IteratorableSet) body2childChain.get( sbit.next());
	    
	    Iterator cit = children.iterator();
	    if (cit.hasNext()) {

		SETNode 
		    cur = (SETNode) cit.next(),
		    prev = null;


		while (cit.hasNext()) {
		    prev = cur;
		    cur = (SETNode) cit.next();

		    aef.find_Breaks( prev, cur);
		}
	    }
	}
    }
    
    protected void remove_AugmentedStmt( AugmentedStmt as)
    {
	body.remove( as);

	Iterator it = subBodies.iterator();
	while (it.hasNext()) {
	    IteratorableSet subBody = (IteratorableSet) it.next();

	    if (subBody.contains( as)) {
		subBody.remove( as);
		return;
	    }
	}
    }



    public boolean nest( SETNode other)
    {
	if (other.resolve( this) == false)
	    return false;

	IteratorableSet otherBody = other.get_Body();
	
	Iterator sbit = subBodies.iterator();
	while (sbit.hasNext()) {
	    IteratorableSet subBody = (IteratorableSet) sbit.next();
	    
	    if (subBody.intersects( otherBody)) {
		IteratorableSet childChain = (IteratorableSet) body2childChain.get( subBody);
		
		Iterator ccit = childChain.snapshotIterator();
		while (ccit.hasNext()) {
		    SETNode curChild = (SETNode) ccit.next();
		    
		    IteratorableSet childBody = curChild.get_Body();
		    
		    if (childBody.intersects( otherBody)) {
			
			if (childBody.isSupersetOf( otherBody))
			    return curChild.nest( other);
			
			
			else {
			    remove_Child( curChild, childChain);
			    
			    Iterator osbit = other.subBodies.iterator();
			    while (osbit.hasNext()) {
				IteratorableSet otherSubBody = (IteratorableSet) osbit.next();
				
				if (otherSubBody.isSupersetOf( childBody)) {
				    other.add_Child( curChild, (IteratorableSet) other.get_Body2ChildChain().get( otherSubBody));
				    break;
				}
			    }
			}
		    }
		}
		
		add_Child( other, childChain);
	    }
	}

	return true;
    }



    /*
     *  Debugging stuff.
     */
    
    public void dump()
    {
	dump( System.out);
    }

    public void dump( PrintStream out)
    {
	dump( out, "");
    }

    private void dump( PrintStream out, String indentation)
    {
	String 
	    TOP = ".---",
	    TAB = "|  " ,
	    MID = "+---",
	    BOT = "`---";

	out.println( indentation);
        out.println( indentation + TOP);
	out.println( indentation + TAB + getClass());
	out.println( indentation + TAB);
	Iterator it = body.iterator();
	while (it.hasNext())
	    out.println( indentation + TAB + ((AugmentedStmt) it.next()).toString());

	Iterator sbit = subBodies.iterator();
	while (sbit.hasNext()) {
	    IteratorableSet subBody = (IteratorableSet) sbit.next();

	    out.println( indentation + MID);
	    Iterator bit = subBody.iterator();
	    while (bit.hasNext()) 
		out.println( indentation + TAB + ((AugmentedStmt) bit.next()).toString());

	    out.println( indentation + TAB);

	    Iterator cit = ((IteratorableSet) body2childChain.get( subBody)).iterator();
	    while (cit.hasNext())
		((SETNode) cit.next()).dump( out, TAB + indentation);
	}
	out.println( indentation + BOT);
    }

    public void verify()
    {
	Iterator sbit = subBodies.iterator();
	while (sbit.hasNext()) {
	    IteratorableSet body = (IteratorableSet) sbit.next();
	    
	    Iterator bit = body.iterator();
	    while (bit.hasNext())
		if ((bit.next() instanceof AugmentedStmt) == false)
		    System.out.println( "Error in body: " + getClass());

	    Iterator cit = ((IteratorableSet) body2childChain.get( body)).iterator();
	    while (cit.hasNext())
		((SETNode) cit.next()).verify();
	}
    }
}
