package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;

public class ASTIfElseNode extends ASTControlFlowNode
{
    private List ifBody, elseBody;

    public ASTIfElseNode( SETNodeLabel label, ConditionExpr condition, List ifBody, List elseBody)
    {
	super( label, condition);
	this.ifBody = ifBody;
	this.elseBody = elseBody;
    }

    public Object clone()
    {
	return new ASTIfElseNode( get_Label(), get_Condition(), ifBody, elseBody);
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
