package soot.dava.internal.AST;

import java.util.*;
import soot.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;

public class ASTWhileNode extends ASTControlFlowNode
{
    private List body;

    public ASTWhileNode( SETNodeLabel label, ConditionExpr ce, List body)
    {
	super( label, ce);
	this.body = body;

	subBodies.add( body);
    }

    public Object clone()
    {
	return new ASTWhileNode( get_Label(), get_Condition(), body);
    }

    public void toString( UnitPrinter up )
    {
        label_toString( up );

        up.literal( "while" );
        up.literal( " " );
        up.literal( "(" );
        conditionBox.toString( up );
        up.literal( ")" );
        up.newline();

        up.literal( "{" );
        up.newline();

        up.incIndent();
        body_toString( up, body );
        up.decIndent();

        up.literal( "}" );
        up.newline();
    }

    public String toString( )
    {
	StringBuffer b = new StringBuffer();
	
	b.append( label_toString( ));

	b.append( "while (");
	b.append( get_Condition().toString());
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
