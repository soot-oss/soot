package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.toolkits.base.AST.*;

public class ASTStatementSequenceNode extends ASTNode
{
    private List statementSequence;

    public ASTStatementSequenceNode( List statementSequence)
    {
	super();

	this.statementSequence = statementSequence;
    }

    public Object clone()
    {
	return new ASTStatementSequenceNode( statementSequence);
    }

    public void perform_Analysis( ASTAnalysis a)
    {
	if (a.getAnalysisDepth() > ASTAnalysis.ANALYSE_AST) {

	    Iterator it = statementSequence.iterator();
	    while (it.hasNext())
		ASTWalker.v().walk_stmt( a, ((AugmentedStmt) it.next()).get_Stmt());
	}

	if (a instanceof TryContentsFinder) {
	    TryContentsFinder tcf = (TryContentsFinder) a;
	    tcf.v().add_ExceptionSet( this, tcf.v().remove_CurExceptionSet());
	}
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
