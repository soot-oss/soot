package soot.dava.internal.AST;

import java.util.*;
import soot.dava.internal.SET.*;

public class ASTUnconditionalLoopNode extends ASTLabeledNode
{
    private List body;

    public ASTUnconditionalLoopNode( SETNodeLabel label, List body)
    {
	super( label);
	this.body = body;
    }

    public Object clone()
    {
	return new ASTUnconditionalLoopNode( get_Label(), body);
    }

    public String toString( Map stmtToName, String indentation)
    {
	StringBuffer b = new StringBuffer();
	
	b.append( label_toString( indentation));

	b.append( indentation);
	b.append( "while (true)");
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
