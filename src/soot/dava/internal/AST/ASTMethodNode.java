package soot.dava.internal.AST;

import java.util.*;

public class ASTMethodNode extends ASTNode
{
    private List body;

    public ASTMethodNode( List body)
    {
	this.body = body;
    }

    public Object clone()
    {
	return new ASTMethodNode( body);
    }

    public String toString( Map stmtToName, String indentation)
    {
	String s = body_toString( stmtToName, indentation, body);
	String suffix = ";" + ASTNode.NEWLINE;

	if (s.endsWith( suffix))
	    s = s.substring( 0, s.length() - suffix.length());
	
	return s;
    }
}
