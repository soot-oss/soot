package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;

public class ASTTryNode extends ASTLabeledNode
{
    private List tryBody, catchList;
    private Map exceptionMap, paramMap;

    public ASTTryNode( SETNodeLabel label, List tryBody, List catchList, Map exceptionMap, Map paramMap)
    {
	super( label);

	this.tryBody = tryBody;
	this.catchList = catchList;
	this.exceptionMap = exceptionMap;
	this.paramMap = paramMap;
    }

    public boolean isEmpty()
    {
	return tryBody.isEmpty();
    }

    public Object clone()
    {
	return new ASTTryNode( get_Label(), tryBody, catchList, exceptionMap, paramMap);
    }

    public String toString( Map stmtToName, String indentation)
    {
	StringBuffer b = new StringBuffer();
	
	b.append( label_toString( indentation));

	b.append( indentation);
	b.append( "try");
	b.append( NEWLINE);
	
	b.append( indentation);
	b.append( "{");
	b.append( NEWLINE);

	b.append( body_toString( stmtToName, indentation + TAB, tryBody));

	b.append( indentation);
	b.append( "}");
	b.append( NEWLINE);

	Iterator cit = catchList.iterator();
	while (cit.hasNext()) {
	    List catchBody = (List) cit.next();

	    b.append( indentation);
	    b.append( "catch (");
	    b.append( ((SootClass) exceptionMap.get( catchBody)).getName());
	    b.append( " ");
	    b.append( ((Local) paramMap.get( catchBody)).getName());
	    b.append( ")");
	    b.append( NEWLINE);

	    b.append( indentation);
	    b.append( "{");
	    b.append( NEWLINE);

	    b.append( body_toString( stmtToName, indentation + TAB, catchBody));

	    b.append( indentation);
	    b.append( "}");
	    b.append( NEWLINE);
	}

	return b.toString();
    }
}
