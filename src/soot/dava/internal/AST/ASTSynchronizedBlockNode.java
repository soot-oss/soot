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

    public String toString( Map stmtToName, String indentation)
    {
	StringBuffer b = new StringBuffer();

	b.append( label_toString(  indentation));

	b.append( indentation);
	b.append( "synchronized (");
	b.append( getLocal());
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
