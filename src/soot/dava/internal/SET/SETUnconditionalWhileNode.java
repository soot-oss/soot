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

import java.util.*;
import soot.util.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;

public class SETUnconditionalWhileNode extends SETCycleNode
{
    public SETUnconditionalWhileNode( IterableSet body)
    {
	super( (AugmentedStmt) body.getFirst(), body);
	add_SubBody( body);
    }

    public IterableSet get_NaturalExits()
    {
	return new IterableSet();
    }

    public ASTNode emit_AST()
    {
	return new ASTUnconditionalLoopNode( get_Label(), emit_ASTBody( (IterableSet) body2childChain.get( subBodies.get(0))));
    }

    public AugmentedStmt get_EntryStmt()
    {
	return get_CharacterizingStmt();
    }
}
