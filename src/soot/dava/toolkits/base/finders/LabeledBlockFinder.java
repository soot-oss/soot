package soot.dava.toolkits.base.finders;

import soot.dava.*;
import java.util.*;
import soot.util.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.AST.*;

public class LabeledBlockFinder implements FactFinder
{
    private LabeledBlockFinder() { orderNumber = new HashMap(); }
    private static LabeledBlockFinder instance = new LabeledBlockFinder();

    private HashMap orderNumber;

    public static LabeledBlockFinder v()
    {
	return instance;
    }

    public void find( DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException
    {
	Iterator bit = SET.get_Body().iterator();
	while (bit.hasNext()) 
	    SET.find_SmallestSETNode((AugmentedStmt) bit.next());

	SET.find_LabeledBlocks( this);
    }

    public void perform_ChildOrder( SETNode SETParent)
    {
	if (SETParent instanceof SETStatementSequenceNode)
	    return;

	Iterator sbit = SETParent.get_SubBodies().iterator();
	while (sbit.hasNext()) {

	    IteratorableSet body = (IteratorableSet) sbit.next();
	    IteratorableSet children = (IteratorableSet) SETParent.get_Body2ChildChain().get( body);

	    HashSet touchSet  = new HashSet();
	    IteratorableSet childOrdering = new IteratorableSet();
	    LinkedList worklist = new LinkedList();
	    List SETBasicBlocks = null;
	    
	    if (SETParent instanceof SETUnconditionalWhileNode) {
		
		SETNode startSETNode = ((SETUnconditionalWhileNode) SETParent).get_CharacterizingStmt().myNode;
		while (children.contains( startSETNode) == false)
		    startSETNode = startSETNode.get_Parent();
		
		SETBasicBlocks = build_Connectivity( SETParent, body, startSETNode);

		worklist.add( SETBasicBlock.get_SETBasicBlock( startSETNode));
	    }
	    else {
		SETBasicBlocks = build_Connectivity( SETParent, body, null);

		Iterator cit = children.iterator();
		while (cit.hasNext()) {
		    SETNode child = (SETNode) cit.next();
		    
		    if (child.get_Predecessors().isEmpty()) 
			worklist.add( SETBasicBlock.get_SETBasicBlock( child));
		}
	    }
	    
	    while (worklist.isEmpty() == false) {
		SETBasicBlock sbb = (SETBasicBlock) worklist.removeFirst();
		
		// extract and append the basic block to child ordering
		Iterator bit = sbb.get_Body().iterator();
		while (bit.hasNext()) 
		    childOrdering.addLast( bit.next());
		
		touchSet.add( sbb);
		
		
		/*  ************************************************  *
		 *  Basic orderer.
		 */
		
		TreeSet sortedSuccessors = new TreeSet();
		
		Iterator sit = sbb.get_Successors().iterator();
	    SETBasicBlock_successor_loop:
		while (sit.hasNext()) {
		    SETBasicBlock ssbb = (SETBasicBlock) sit.next();

		    if (touchSet.contains( ssbb))
			continue;
		    
		    Iterator psit = ssbb.get_Predecessors().iterator();
		    while (psit.hasNext()) 
			if (touchSet.contains( psit.next()) == false)
			    continue SETBasicBlock_successor_loop;
		    
		    sortedSuccessors.add( ssbb);
		}
		
		sit = sortedSuccessors.iterator();
		while (sit.hasNext()) 
		    worklist.addFirst( sit.next());
		
		/*
		 *  End of Basic orderer.
		 *  ************************************************  */
		
	    }
	    
	    int count = 0;
	    orderNumber = new HashMap();
	    
	    Iterator it = childOrdering.iterator();
	    while (it.hasNext())
		orderNumber.put( it.next(), new Integer( count++));
	    
	    children.clear();
	    children.addAll( childOrdering);
	}
	// SETParent.dump();
    }
    
    private List build_Connectivity( SETNode SETParent, IteratorableSet body, SETNode startSETNode)
    {
	IteratorableSet children = (IteratorableSet) SETParent.get_Body2ChildChain().get( body);
	
	/* 
	 *  First task: establish the connectivity between the children of the current node.
	 */
	
	// look through all the statements in the current SETNode
	Iterator it = body.iterator();
	while (it.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) it.next();

	    // for each statement, examine each of it's successors
	    Iterator sit = as.csuccs.iterator();
	    while (sit.hasNext()) {
		AugmentedStmt sas = (AugmentedStmt) sit.next();
		
		if (body.contains( sas)) {

		    // get the child nodes that contain the source and destination statements
		    SETNode srcNode = as.myNode;
		    SETNode dstNode = sas.myNode;

		    while (children.contains( srcNode) == false)
			srcNode = srcNode.get_Parent();

		    while (children.contains( dstNode) == false)
			dstNode = dstNode.get_Parent();
		    
		    if (srcNode == dstNode)
			continue;

		    // hook up the src and dst nodes
		    if (srcNode.get_Successors().contains( dstNode) == false)
			srcNode.get_Successors().add( dstNode);

		    if (dstNode.get_Predecessors().contains( srcNode) == false)
			dstNode.get_Predecessors().add( srcNode);
		}
	    }
	}


	/*
	 *  Second task:  build the basic block graph between the node.
	 */

	// first create the basic blocks
	LinkedList basicBlockList = new LinkedList();

	Iterator cit = children.iterator();
	while (cit.hasNext()) {
	    SETNode child = (SETNode) cit.next();

	    if (SETBasicBlock.get_SETBasicBlock( child) != null)
		continue;

	    // build a basic block for every node with != 1 predecessor
	    SETBasicBlock basicBlock = new SETBasicBlock();
	    while (child.get_Predecessors().size() == 1) {

		if ((SETParent instanceof SETUnconditionalWhileNode) && (child == startSETNode))
		    break;

		SETNode prev = (SETNode) child.get_Predecessors().getFirst();
		if ((SETBasicBlock.get_SETBasicBlock( prev) != null) || (prev.get_Successors().size() != 1))
		    break;

		child = prev;
	    }

	    basicBlock.add( child);

	    while (child.get_Successors().size() == 1) {
		child = (SETNode) child.get_Successors().getFirst();
		
		if ((SETBasicBlock.get_SETBasicBlock( child) != null) || (child.get_Predecessors().size() != 1))
		    break;
		
		basicBlock.add( child);
	    }

	    basicBlockList.add( basicBlock);
	}

	// next build the connectivity between the nodes of the basic block graph
	Iterator bblit = basicBlockList.iterator();
	while (bblit.hasNext()) {
	    SETBasicBlock sbb = (SETBasicBlock) bblit.next();
	    SETNode entryNode = sbb.get_EntryNode();

	    Iterator pit = entryNode.get_Predecessors().iterator();
	    while (pit.hasNext()) {
		SETNode psn = (SETNode) pit.next();

		SETBasicBlock psbb = SETBasicBlock.get_SETBasicBlock( psn);
		
		if (sbb.get_Predecessors().contains( psbb) == false)
		    sbb.get_Predecessors().add( psbb);

		if (psbb.get_Successors().contains( sbb) == false)
		    psbb.get_Successors().add( sbb);
	    }
	}

	/*
	if (basicBlockList.size() > 1) {
	    Iterator sbbit = basicBlockList.iterator();
	    while (sbbit.hasNext()) {
		System.out.println( "^^^");
		((SETBasicBlock) sbbit.next()).dump();
		System.out.println( "***");
	    }
	}
	*/

	return basicBlockList;
    }

    public void find_LabeledBlocks( SETNode SETParent)
    {
	Iterator sbit = SETParent.get_SubBodies().iterator();
	while (sbit.hasNext()) {
	    IteratorableSet curBody = (IteratorableSet) sbit.next();
	    IteratorableSet children = (IteratorableSet) SETParent.get_Body2ChildChain().get( curBody);
	    
	    Iterator it = children.snapshotIterator();
	    if (it.hasNext()) {
		SETNode 
		    curNode = (SETNode) it.next(),
		    prevNode = null;
		
		// Look through all the children of the current SET node.
		while (it.hasNext()) {
		    prevNode = curNode;
		    curNode = (SETNode) it.next();
		    AugmentedStmt entryStmt = curNode.get_EntryStmt();
		    
		    SETNode minNode = null;
		    boolean build = false;
		    
		    // For each SET node, check the edges that come into it.
		    Iterator pit = entryStmt.cpreds.iterator();
		    while (pit.hasNext()) {
			AugmentedStmt pas = (AugmentedStmt) pit.next();

			if (curBody.contains( pas) == false) // will happen with do-while loops
			    continue;

			SETNode srcNode = pas.myNode;

			while (children.contains( srcNode) == false)
			    srcNode = srcNode.get_Parent();
			
			if (srcNode == curNode)
			    continue;

			if (srcNode != prevNode) {
			    build = true;

			    if ((minNode == null) || (((Integer) orderNumber.get( srcNode)).intValue() < ((Integer) orderNumber.get( minNode)).intValue()))
				minNode = srcNode;
			}
		    }
		    
		    if (build) {
			IteratorableSet labeledBlockBody = new IteratorableSet();
			
			Iterator cit = children.iterator( minNode);
			while (cit.hasNext()) {
			    SETNode child = (SETNode) cit.next();
			    if (child == curNode)
				break;
			    
			    labeledBlockBody.addAll( child.get_Body());
			}
			
			SETLabeledBlockNode slbn = new SETLabeledBlockNode( labeledBlockBody);
			orderNumber.put( slbn, orderNumber.get( minNode));
			
			cit = children.snapshotIterator( minNode);
			while (cit.hasNext()) {
			    SETNode child = (SETNode) cit.next();
			    if (child == curNode) 
				break;
			    
			    SETParent.remove_Child( child, children );
			    slbn.add_Child( child, (IteratorableSet) slbn.get_Body2ChildChain().get( slbn.get_SubBodies().get(0)));
			}
			
			SETParent.insert_ChildBefore( slbn, curNode, children);
		    }
		}
	    }	
	}
    }
}
