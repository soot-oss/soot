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
    private IterableSet junkBody;

    public SETSwitchNode( AugmentedStmt characterizingStmt, Value key, IterableSet body, List switchNodeList, IterableSet junkBody)
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
    

    public IterableSet get_NaturalExits()
    {
	return new IterableSet();
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
		    index2ASTBody.put( index, emit_ASTBody( (IterableSet) get_Body2ChildChain().get( sn.get_Body())));
	    }
	}

	return new ASTSwitchNode( get_Label(), key, indexList, index2ASTBody);
    }

    public AugmentedStmt get_EntryStmt()
    {
	return get_CharacterizingStmt();
    }
}
