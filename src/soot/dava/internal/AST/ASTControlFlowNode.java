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

package soot.dava.internal.AST;

import soot.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;

public abstract class ASTControlFlowNode extends ASTLabeledNode
{
    protected ValueBox conditionBox;

    public ASTControlFlowNode( SETNodeLabel label, ConditionExpr condition)
    {
	super( label);
        this.conditionBox = Jimple.v().newConditionExprBox(condition);
    }

    public ConditionExpr get_Condition()
    {
	return (ConditionExpr) conditionBox.getValue();
    }

    public void perform_Analysis( ASTAnalysis a)
    {
	ASTWalker.v().walk_value( a, get_Condition());

	if (a instanceof TryContentsFinder) {
	    TryContentsFinder tcf = (TryContentsFinder) a;
	    tcf.v().add_ExceptionSet( this, tcf.v().remove_CurExceptionSet());
	}

	perform_AnalysisOnSubBodies( a);
    }
}
