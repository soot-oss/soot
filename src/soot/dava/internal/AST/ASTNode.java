package soot.dava.internal.AST;

import soot.*;
import java.util.*;

public abstract class ASTNode extends AbstractUnit
{
    public static final String TAB   = "    ";
    public static final String NEWLINE = "\n";

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

    public boolean fallsThrough()
    {
        return false;
    }

    public boolean branches()
    {
        return false;
    }
}
