/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2005 Nomair A. Naeem
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
import java.util.*;
import soot.dava.toolkits.base.AST.*;
import soot.dava.toolkits.base.AST.analysis.*;

public abstract class ASTNode extends AbstractUnit
{
    public static final String 
	TAB     = "    ",
	NEWLINE = "\n";

    protected List subBodies;

    public ASTNode()
    {
	subBodies = new ArrayList();
    }

    public abstract void toString( UnitPrinter up );
 
    protected void body_toString( UnitPrinter up, List body )
    {
	Iterator it = body.iterator();
	while (it.hasNext()) {
	    ((ASTNode) it.next()).toString( up );

	    if (it.hasNext())
		up.newline();
	}
    }

    protected String body_toString(List body)
    {
	StringBuffer b = new StringBuffer();

	Iterator it = body.iterator();
	while (it.hasNext()) {
	    b.append( ((ASTNode) it.next()).toString());

	    if (it.hasNext())
		b.append( NEWLINE);
	}

	return b.toString();	
    }

    public List get_SubBodies()
    {
	return subBodies;
    }


    public abstract void perform_Analysis( ASTAnalysis a);

    protected void perform_AnalysisOnSubBodies( ASTAnalysis a)
    {
	Iterator sbit = subBodies.iterator();
	while (sbit.hasNext()) {
	    Object subBody = sbit.next();
	    Iterator it = null;

	    if (this instanceof ASTTryNode)
		it = ((List) ((ASTTryNode.container) subBody).o).iterator();
	    else 
		it = ((List) subBody).iterator();
	    
	    while (it.hasNext())
		((ASTNode) it.next()).perform_Analysis( a);
	}
	
	a.analyseASTNode( this);
    }

    public boolean fallsThrough()
    {
        return false;
    }

    public boolean branches()
    {
        return false;
    }

    /*
      Nomair A. Naeem, 7-FEB-05
      Part of Visitor Design Implementation for AST
      See: soot.dava.toolkits.base.AST.analysis For details
    */
    public void apply(Analysis a){
    	throw new RuntimeException("Analysis invoked apply method on ASTNode");
    }
    
}
