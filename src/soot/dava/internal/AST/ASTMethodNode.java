package soot.dava.internal.AST;

import java.util.*;
import soot.dava.toolkits.base.AST.*;

public class ASTMethodNode extends ASTNode
{
    private List body;

    public ASTMethodNode( List body)
    {
	super();
	this.body = body;

	subBodies.add( body);
    }

    public Object clone()
    {
	return new ASTMethodNode( body);
    }

    public void perform_Analysis( ASTAnalysis a)
    {
	perform_AnalysisOnSubBodies( a);
    }

    public String toString( Map stmtToName, String indentation)
    {
	return body_toString( stmtToName, indentation, body);
    }
}
