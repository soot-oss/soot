package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;

public class ASTIfElseNode extends ASTControlFlowNode
{
    private List ifBody, elseBody;

    public ASTIfElseNode( SETNodeLabel label, ConditionExpr condition, List ifBody, List elseBody)
    {
	super( label, condition);
	this.ifBody = ifBody;
	this.elseBody = elseBody;

	subBodies.add( ifBody);
	subBodies.add( elseBody);
    }

    public Object clone()
    {
	return new ASTIfElseNode( get_Label(), get_Condition(), ifBody, elseBody);
    }

    public void toString( UnitPrinter up ) 
    {
        label_toString( up );

        up.literal( "if" );
        up.literal( " " );
        up.literal( "(" );
        conditionBox.toString( up );
        up.literal( ")" );
        up.newline();
	
        up.literal( "{" );
        up.newline();

        up.incIndent();
        body_toString( up, ifBody );
        up.decIndent();

        up.literal( "}" );
        up.newline();

        up.literal( "else" );
        up.newline();

        up.literal( "{" );
        up.newline();

        up.incIndent();
        body_toString( up, elseBody );
        up.decIndent();

        up.literal( "}" );
        up.newline();
    }

    public String toString( Map stmtToName, String indentation)
    {
	StringBuffer b = new StringBuffer();
	
	b.append( label_toString( indentation));

	b.append( indentation);
	b.append( "if (");
	b.append( get_Condition().toString());
	b.append( ")");
	b.append( NEWLINE);
	
	b.append( indentation);
	b.append( "{");
	b.append( NEWLINE);

	b.append( body_toString( stmtToName, indentation + TAB, ifBody));

	b.append( indentation);
	b.append( "}");
	b.append( NEWLINE);

	b.append( indentation);
	b.append( "else");
	b.append( NEWLINE);

	b.append( indentation);
	b.append( "{");
	b.append( NEWLINE);

	b.append( body_toString( stmtToName, indentation + TAB, elseBody));

	b.append( indentation);
	b.append( "}");
	b.append( NEWLINE);

	return b.toString();
    }
}
