package soot.dava.toolkits.base.AST;

import java.util.*;
import soot.dava.internal.AST.*;

public class UselessTryRemover extends ASTAnalysis
{
    private UselessTryRemover() {}
    private static UselessTryRemover instance = new UselessTryRemover();

    public static UselessTryRemover v() 
    {
	return instance;
    }

    public void analyse( ASTNode n)
    {
	Iterator sbit = n.get_SubBodies().iterator();

	while (sbit.hasNext()) {
	    List 
		subBody = (List) sbit.next(),
		toRemove = new ArrayList();

	    Iterator cit = subBody.iterator();
	    while (cit.hasNext()) {
		Object child = cit.next();
		
		if ((child instanceof ASTTryNode) && (((ASTTryNode) child).isEmpty()))
		    toRemove.add( child);
	    }

	    Iterator trit = toRemove.iterator();
	    while (trit.hasNext())
		subBody.remove( trit.next());

	    if (toRemove.isEmpty() == false)
		modified = true;
	}
    }
}
