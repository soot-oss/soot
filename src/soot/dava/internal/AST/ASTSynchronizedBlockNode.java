package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.dava.internal.SET.*;

public class ASTSynchronizedBlockNode extends ASTLabeledNode
{
    private List body;
    private Value local;

    public ASTSynchronizedBlockNode( SETNodeLabel label, List body, Value local)
    {
	super( label);
	this.body = body;
	this.local = local;
    }

    public int size()
    {
	return body.size();
    }

    public Object clone()
    {
	return new ASTSynchronizedBlockNode( get_Label(), body, local);
    }

    public String toString( Map stmtToName, String indentation)
    {
	StringBuffer b = new StringBuffer();

	b.append( label_toString(  indentation));

	b.append( indentation);
	b.append( "synchronized (");
	b.append( local);
	b.append( ")");
	b.append( NEWLINE);

	b.append( indentation);
	b.append( "{");
	b.append( NEWLINE);
 
	b.append( body_toString( stmtToName, indentation + TAB, body));

	b.append( indentation);
	b.append( "}");
	b.append( NEWLINE);

	return b.toString();
    }
}
