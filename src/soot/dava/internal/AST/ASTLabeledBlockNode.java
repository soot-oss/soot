package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;

public class ASTLabeledBlockNode extends ASTLabeledNode
{
    private List body;
    private SETNodeLabel label;

    public ASTLabeledBlockNode( SETNodeLabel label, List body)
    {
	super( label);
	this.body = body;

	subBodies.add( body);
    }

    public int size()
    {
	return body.size();
    }

    public Object clone()
    {
	return new ASTLabeledBlockNode( get_Label(), body);
    }

    public void toString( UnitPrinter up )
    {
        label_toString( up );

        up.literal( "{" );
        up.newline();
 
        up.incIndent();
        body_toString( up, body );
        up.decIndent();

        up.literal( "}" );
        up.newline();
    }

    public String toString( Map stmtToName, String indentation)
    {
	StringBuffer b = new StringBuffer();

	b.append( label_toString(  indentation));

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
