package soot.dava;

import soot.*;
import soot.util.*;
import soot.dava.internal.AST.*;

public class DavaStmtPrinter implements StmtPrinter
{
    private static DavaStmtPrinter instance = new DavaStmtPrinter();
    private DavaStmtPrinter() {}

    public static DavaStmtPrinter v() { return instance; }

    public void printStatementsInBody(Body body, java.io.PrintWriter out, boolean isPrecise, boolean isNumbered)
    {
	Chain units = ((DavaBody) body).getUnits();

	if (units.size() != 1)
	    throw new RuntimeException( "DavaBody AST doesn't have single root.");
	
	out.print( ((ASTNode) units.getFirst()).toString( null, "        "));
    }

    public void printDebugStatementsInBody(Body b, java.io.PrintWriter out, boolean isPrecise)
    {
    }
}


