package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.dava.internal.asg.*;

public class ASTStatementSequenceNode extends ASTNode
{
    private List statementSequence;

    public ASTStatementSequenceNode( List statementSequence)
    {
	this.statementSequence = statementSequence;
    }

    public Object clone()
    {
	return new ASTStatementSequenceNode( statementSequence);
    }

    public String toString( Map stmtToName, String indentation)
    {
	StringBuffer b = new StringBuffer();

	Iterator it = statementSequence.iterator();
	while (it.hasNext()) {
	    b.append( ((Unit) ((AugmentedStmt) it.next()).get_Stmt()).toBriefString( stmtToName, indentation));
	    b.append( ";");
	    b.append( NEWLINE);
	}

	return b.toString();
    }
}
