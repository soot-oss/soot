package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.dava.toolkits.base.AST.*;

public abstract class ASTNode extends AbstractUnit
{
    public static final int 
	PREFIX_TRAVERSAL  = 0,
	POSTFIX_TRAVERSAL = 1;

    public static final String 
	TAB     = "    ",
	NEWLINE = "\n";

    protected List subBodies;

    public ASTNode()
    {
	subBodies = new ArrayList();
    }

    protected String toString( boolean isBrief, Map stmtToName, String indentation)
    {
	return toString( stmtToName, indentation);
    }

    public abstract String toString( Map stmtToName, String indentation);
 
    protected String body_toString( Map stmtToName, String indentation, List body)
    {
	StringBuffer b = new StringBuffer();

	Iterator it = body.iterator();
	while (it.hasNext()) {
	    b.append( ((ASTNode) it.next()).toString( stmtToName, indentation));

	    if (it.hasNext())
		b.append( NEWLINE);
	}

	return b.toString();	
    }

    public void perform( ASTAnalysis analysis)
    {
	perform( analysis, POSTFIX_TRAVERSAL);
    }

    public void perform( ASTAnalysis analysis, int traversalOrder)
    {
	walk( analysis, traversalOrder);
    }

    public List get_SubBodies()
    {
	return subBodies;
    }



    private void walk( ASTAnalysis analysis, int traversalOrder)
    {
	if (traversalOrder == PREFIX_TRAVERSAL)
	    analysis.analyse( this);

	Iterator sbit = subBodies.iterator();
	while (sbit.hasNext()) {
	    Iterator it = ((List) sbit.next()).iterator();
	    while (it.hasNext())
		((ASTNode) it.next()).walk( analysis, traversalOrder);
	}

	if (traversalOrder != PREFIX_TRAVERSAL)
	    analysis.analyse( this);
    }

    public boolean fallsThrough()
    {
        return false;
    }

    public boolean branches()
    {
        return false;
    }
}
