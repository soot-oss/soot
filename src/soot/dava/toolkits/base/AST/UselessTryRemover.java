/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dava.toolkits.base.AST;
import soot.*;

import java.util.*;
import soot.dava.internal.AST.*;

public class UselessTryRemover extends ASTAnalysis
{
    public UselessTryRemover( Singletons.Global g ) {}
    public static UselessTryRemover v() { return G.v().soot_dava_toolkits_base_AST_UselessTryRemover(); }

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
		G.v().ASTAnalysis_modified = true;
	}
    }
}
