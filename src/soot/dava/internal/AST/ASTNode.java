package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.dava.toolkits.base.AST.*;

public abstract class ASTNode extends AbstractUnit
{
    public static final String 
	TAB     = "    ",
	NEWLINE = "\n";

    protected List subBodies;

    public ASTNode()
    {
	subBodies = new ArrayList();
    }

    public abstract void toString( UnitPrinter up );
 
    protected void body_toString( UnitPrinter up, List body )
    {
	Iterator it = body.iterator();
	while (it.hasNext()) {
	    ((ASTNode) it.next()).toString( up );

	    if (it.hasNext())
		up.newline();
	}
    }

    protected String body_toString(List body)
    {
	StringBuffer b = new StringBuffer();

	Iterator it = body.iterator();
	while (it.hasNext()) {
	    b.append( ((ASTNode) it.next()).toString());

	    if (it.hasNext())
		b.append( NEWLINE);
	}

	return b.toString();	
    }

    public List get_SubBodies()
    {
	return subBodies;
    }

    public abstract void perform_Analysis( ASTAnalysis a);

    protected void perform_AnalysisOnSubBodies( ASTAnalysis a)
    {
	Iterator sbit = subBodies.iterator();
	while (sbit.hasNext()) {
	    Object subBody = sbit.next();
	    Iterator it = null;

	    if (this instanceof ASTTryNode)
		it = ((List) ((ASTTryNode.container) subBody).o).iterator();
	    else 
		it = ((List) subBody).iterator();
	    
	    while (it.hasNext())
		((ASTNode) it.next()).perform_Analysis( a);
	}
	
	a.analyseASTNode( this);
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
