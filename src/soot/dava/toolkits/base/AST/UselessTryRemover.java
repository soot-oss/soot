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

    public int getAnalysisDepth()
    {
	return ANALYSE_AST;
    }

    public void analyseASTNode( ASTNode n)
    {
	Iterator sbit = n.get_SubBodies().iterator();

	while (sbit.hasNext()) {
	    
	    List 
		subBody = null,
		toRemove = new ArrayList();

	    if (n instanceof ASTTryNode)
		subBody = (List) ((ASTTryNode.container) sbit.next()).o;
	    else
		subBody = (List) sbit.next();


	    Iterator cit = subBody.iterator();
	    while (cit.hasNext()) {
		Object child = cit.next();
		
		if (child instanceof ASTTryNode) {
		    ASTTryNode tryNode = (ASTTryNode) child;

		    tryNode.perform_Analysis( TryContentsFinder.v());

		    if ((tryNode.get_CatchList().isEmpty()) || (tryNode.isEmpty()))
			toRemove.add( tryNode);
		}
	    }

	    Iterator trit = toRemove.iterator();
	    while (trit.hasNext()) {
		ASTTryNode tryNode = (ASTTryNode) trit.next();

		subBody.addAll( subBody.indexOf( tryNode), tryNode.get_TryBody());
		subBody.remove( tryNode);
	    }

	    if (toRemove.isEmpty() == false)
		modified = true;
	}
    }
}
