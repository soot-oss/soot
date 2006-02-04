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
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;
import soot.dava.toolkits.base.AST.analysis.*;

public class ASTSwitchNode extends ASTLabeledNode
{
    private ValueBox keyBox;
    private List indexList;
    private Map index2BodyList;

    public ASTSwitchNode( SETNodeLabel label, Value key, List indexList, Map index2BodyList)
    {
	super( label);

	this.keyBox = Jimple.v().newRValueBox( key );
	this.indexList = indexList;
	this.index2BodyList = index2BodyList;

	Iterator it = indexList.iterator();
	while (it.hasNext()) {
	    List body = (List) index2BodyList.get( it.next());
	    
	    if (body != null)
		subBodies.add( body);
	}
    }

    /*
      Nomair A. Naeem 22-FEB-2005
      Added for ASTCleaner
    */
    public List getIndexList(){
	return indexList;
    }

    public Map getIndex2BodyList(){
	return index2BodyList;
    }

    public void replaceIndex2BodyList(Map index2BodyList){
	this.index2BodyList=index2BodyList;

	subBodies = new ArrayList();
	Iterator it = indexList.iterator();
	while (it.hasNext()) {
	    List body = (List) index2BodyList.get( it.next());
	    
	    if (body != null)
		subBodies.add( body);
	}
    }




    public ValueBox getKeyBox(){
	return keyBox;
    }
    


    public Value get_Key()
    {
	return keyBox.getValue();
    }

    public void set_Key(Value key){
	this.keyBox = Jimple.v().newRValueBox( key );
    }



    public Object clone()
    {
	return new ASTSwitchNode( get_Label(), get_Key(), indexList, index2BodyList);
    }

    public void perform_Analysis( ASTAnalysis a)
    {
	ASTWalker.v().walk_value( a, get_Key());

	if (a instanceof TryContentsFinder) {
	    TryContentsFinder tcf = (TryContentsFinder) a;
	    tcf.v().add_ExceptionSet( this, tcf.v().remove_CurExceptionSet());
	}
  
	perform_AnalysisOnSubBodies( a);
    }


    public void toString( UnitPrinter up )
    {
        label_toString( up );

        up.literal( "switch" );
        up.literal( " " );
        up.literal( "(" );
        keyBox.toString( up );
        up.literal( ")" );
        up.newline();

        up.literal( "{" );
        up.newline();

	Iterator it = indexList.iterator();
	while (it.hasNext()) {
	    
	    Object index = it.next();

            up.incIndent();
	    
	    if (index instanceof String) 
                up.literal( "default" );

	    else {
                up.literal( "case" );
                up.literal( " " );
                up.literal( index.toString() );
	    }
	    
            up.literal( ":" );
            up.newline();

	    List subBody = (List) index2BodyList.get( index);

	    if (subBody != null) {
                up.incIndent();
                body_toString( up, subBody );
	    
		if (it.hasNext())
		    up.newline();
                up.decIndent();
	    }
            up.decIndent();
	}

	up.literal( "}");
        up.newline();
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();
	
	b.append( label_toString( ));
	
	b.append( "switch (");
	b.append( get_Key() );
	b.append( ")");
	b.append( NEWLINE);

	b.append( "{");
	b.append( NEWLINE);

	Iterator it = indexList.iterator();
	while (it.hasNext()) {
	    
	    Object index = it.next();

	    b.append( TAB);
	    
	    if (index instanceof String) 
		b.append( "default");

	    else {
		b.append( "case ");
		b.append( ((Integer) index).toString());
	    }
	    
	    b.append( ":");
	    b.append( NEWLINE);

	    List subBody = (List) index2BodyList.get( index);

	    if (subBody != null) {
		b.append( body_toString(subBody));
	    
		if (it.hasNext())
		    b.append( NEWLINE);
	    }
	}

	b.append( "}");
	b.append( NEWLINE);

	return b.toString();
    }



    /*
      Nomair A. Naeem, 7-FEB-05
      Part of Visitor Design Implementation for AST
      See: soot.dava.toolkits.base.AST.analysis For details
    */
    public void apply(Analysis a){
	a.caseASTSwitchNode(this);
    }
}
