package soot.dava.internal.SET;

import soot.*;
import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.finders.*;

public class SETSwitchNode extends SETDagNode
{
    private List switchNodeList;
    private Value key;
    private IteratorableSet junkBody;

    public SETSwitchNode( AugmentedStmt characterizingStmt, Value key, IteratorableSet body, List switchNodeList, IteratorableSet junkBody)
    {
	super( characterizingStmt, body);

	this.key = key;
	this.switchNodeList = switchNodeList;
	this.junkBody = junkBody;

	Iterator it = switchNodeList.iterator();
	while (it.hasNext())
	    add_SubBody(  ((SwitchNode) it.next()).get_Body());

	add_SubBody( junkBody);
    }
    

    public IteratorableSet get_NaturalExits()
    {
	return new IteratorableSet();
    }

    public ASTNode emit_AST()
    {
	LinkedList indexList = new LinkedList();
	Map index2ASTBody = new HashMap();
	
	Iterator it = switchNodeList.iterator();
	while (it.hasNext()) {
	    SwitchNode sn = (SwitchNode) it.next();

	    Object lastIndex = sn.get_IndexSet().last();
	    Iterator iit = sn.get_IndexSet().iterator();
	    while (iit.hasNext()) {
		Object index = iit.next();

		indexList.addLast( index);

		if (index != lastIndex) 
		    index2ASTBody.put( index, null);
		else
		    index2ASTBody.put( index, emit_ASTBody( (IteratorableSet) get_Body2ChildChain().get( sn.get_Body())));
	    }
	}

	return new ASTSwitchNode( get_Label(), key, indexList, index2ASTBody);
    }

    public AugmentedStmt get_EntryStmt()
    {
	return get_CharacterizingStmt();
    }
}
