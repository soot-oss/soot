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

    public String toString()
    {
	StringBuffer b = new StringBuffer();

	b.append( label_toString());

	b.append( "{");
	b.append( NEWLINE);
 
	b.append( body_toString(body));

	b.append( "}");
	b.append( NEWLINE);

	return b.toString();
    }
}
