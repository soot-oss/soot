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
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.toolkits.base.finders.*;

public class SETSynchronizedBlockNode extends SETNode
{
    private Value local;

    public SETSynchronizedBlockNode( ExceptionNode en, Value local)
    {
	super( en.get_Body());

	add_SubBody( en.get_TryBody());
	add_SubBody( en.get_CatchBody());

	this.local = local;
    }

    public IterableSet get_NaturalExits()
    {
	return ((SETNode) ((IterableSet) body2childChain.get( subBodies.get(0))).getLast()).get_NaturalExits();
    }

    public ASTNode emit_AST()
    {
        return new ASTSynchronizedBlockNode( get_Label(), emit_ASTBody( (IterableSet) body2childChain.get( subBodies.get(0))), local);
    }

    public AugmentedStmt get_EntryStmt()
    {
	return ((SETNode) ((IterableSet) body2childChain.get( subBodies.get(0))).getFirst()).get_EntryStmt();
    }

    protected boolean resolve( SETNode parent)
    {
	Iterator sbit = parent.get_SubBodies().iterator();

	while (sbit.hasNext()) {
	    IterableSet subBody = (IterableSet) sbit.next();
	    
	    if (subBody.intersects( get_Body()))
		return subBody.isSupersetOf( get_Body());
	}

	return true;
    }
}
