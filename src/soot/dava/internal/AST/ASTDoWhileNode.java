package soot.dava.internal.AST;

import java.util.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;

public class ASTDoWhileNode extends ASTControlFlowNode
{
    private List body;

    public ASTDoWhileNode( SETNodeLabel label, ConditionExpr ce, List body)
    {
	super( label, ce);
	this.body = body;

	subBodies.add( body);
    }

    public Object clone()
    {
	return new ASTDoWhileNode( get_Label(), get_Condition(), body);
    }

    public String toString( Map stmtToName, String indentation)
    {
	StringBuffer b = new StringBuffer();
	
	b.append( label_toString( indentation));

	b.append( indentation);
	b.append( "do");
	b.append( NEWLINE);

	b.append( indentation);
	b.append( "{");
	b.append( NEWLINE);

	b.append( body_toString( stmtToName, indentation + TAB, body));

	b.append( indentation);
	b.append( "}");
	b.append( NEWLINE);

	b.append( indentation);
	b.append( "while (");
	b.append( get_Condition().toString());
	b.append( ");");
	b.append( NEWLINE);

	return b.toString();
    }
}
