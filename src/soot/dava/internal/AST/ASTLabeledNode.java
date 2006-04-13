/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2004-2006 Nomair A. Naeem
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
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;

public abstract class ASTLabeledNode extends ASTNode
{
    private SETNodeLabel label;
    
    public ASTLabeledNode( SETNodeLabel label)
    {
	super();

	set_Label( label);
    }

    public SETNodeLabel get_Label()
    {
	return label;
    }

    public void set_Label( SETNodeLabel label)
    {
	this.label = label;
    }

    public void perform_Analysis( ASTAnalysis a)
    {
	perform_AnalysisOnSubBodies( a);
    }

    public void label_toString( UnitPrinter up )
    {
	if (label.toString() != null) {
            up.literal( label.toString() );
            up.literal( ":" );
            up.newline();
	}
    }

    public String label_toString()
    {
	if (label.toString() == null)
	    return new String();

	else {
	    StringBuffer b = new StringBuffer();

	    b.append( label.toString());
	    b.append( ":");
	    b.append( ASTNode.NEWLINE);

	    return b.toString();
	}
    }
}
