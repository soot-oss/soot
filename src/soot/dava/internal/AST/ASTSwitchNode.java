package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;

public class ASTSwitchNode extends ASTLabeledNode
{
    private Value key;
    private List indexList;
    private Map index2BodyList;

    public ASTSwitchNode( SETNodeLabel label, Value key, List indexList, Map index2BodyList)
    {
	super( label);

	this.key = key;
	this.indexList = indexList;
	this.index2BodyList = index2BodyList;

	Iterator it = indexList.iterator();
	while (it.hasNext()) {
	    List body = (List) index2BodyList.get( it.next());
	    
	    if (body != null)
		subBodies.add( body);
	}
    }


    public Object clone()
    {
	return new ASTSwitchNode( get_Label(), key, indexList, index2BodyList);
    }

    public String toString( Map stmtToName, String indentation)
    {
	StringBuffer b = new StringBuffer();
	
	b.append( label_toString( indentation));
	
	b.append( indentation);
	b.append( "switch (");
	b.append( key);
	b.append( ")");
	b.append( NEWLINE);

	b.append( indentation);
	b.append( "{");
	b.append( NEWLINE);

	Iterator it = indexList.iterator();
	while (it.hasNext()) {
	    
	    Object index = it.next();

	    b.append( indentation);
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
		b.append( body_toString( stmtToName, indentation + TAB + TAB, subBody));
	    
		if (it.hasNext())
		    b.append( NEWLINE);
	    }
	}

	b.append( indentation);
	b.append( "}");
	b.append( NEWLINE);

	return b.toString();
    }
}
