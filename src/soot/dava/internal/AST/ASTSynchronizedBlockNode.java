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
import java.util.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;

public class ASTSynchronizedBlockNode extends ASTLabeledNode
{
    private List body;
    private ValueBox localBox;

    public ASTSynchronizedBlockNode( SETNodeLabel label, List body, Value local)
    {
	super( label);
	this.body = body;
	this.localBox = Jimple.v().newLocalBox( local );

	subBodies.add( body);
    }

    public int size()
    {
	return body.size();
    }
    
    public Local getLocal() {
        return (Local) localBox.getValue();
    }

    public Object clone()
    {
	return new ASTSynchronizedBlockNode( get_Label(), body, getLocal());
    }

    public void toString( UnitPrinter up)
    {
	label_toString(up);

        up.literal( "synchronized" );
        up.literal( " " );
        up.literal( "(" );

	up.literal( "synchronized (");
	localBox.toString(up);
	up.literal( ")");
        up.newline();

        up.literal( "{" );
        up.newline();
 
        up.incIndent();
        body_toString( up, body );
        up.decIndent();

        up.literal( "}" );
        up.newline();
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();

	b.append( label_toString(  ));

	b.append( "synchronized (");
	b.append( getLocal());
	b.append( ")");
	b.append( NEWLINE);

	b.append( "{");
	b.append( NEWLINE);
 
	b.append( body_toString( body));

	b.append( "}");
	b.append( NEWLINE);

	return b.toString();
    }
}
