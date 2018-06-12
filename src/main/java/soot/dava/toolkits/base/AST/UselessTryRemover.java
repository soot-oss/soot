package soot.dava.toolkits.base.AST;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.G;
import soot.Singletons;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTTryNode;

public class UselessTryRemover extends ASTAnalysis {
  public UselessTryRemover(Singletons.Global g) {
  }

  public static UselessTryRemover v() {
    return G.v().soot_dava_toolkits_base_AST_UselessTryRemover();
  }

  public int getAnalysisDepth() {
    return ANALYSE_AST;
  }

  public void analyseASTNode(ASTNode n) {
    Iterator<Object> sbit = n.get_SubBodies().iterator();

    while (sbit.hasNext()) {

      List<Object> subBody = null, toRemove = new ArrayList<Object>();

      if (n instanceof ASTTryNode) {
        subBody = (List<Object>) ((ASTTryNode.container) sbit.next()).o;
      } else {
        subBody = (List<Object>) sbit.next();
      }

      Iterator<Object> cit = subBody.iterator();
      while (cit.hasNext()) {
        Object child = cit.next();

        if (child instanceof ASTTryNode) {
          ASTTryNode tryNode = (ASTTryNode) child;

          tryNode.perform_Analysis(TryContentsFinder.v());

          if ((tryNode.get_CatchList().isEmpty()) || (tryNode.isEmpty())) {
            toRemove.add(tryNode);
          }
        }
      }

      Iterator<Object> trit = toRemove.iterator();
      while (trit.hasNext()) {
        ASTTryNode tryNode = (ASTTryNode) trit.next();

        subBody.addAll(subBody.indexOf(tryNode), tryNode.get_TryBody());
        subBody.remove(tryNode);
      }

      if (toRemove.isEmpty() == false) {
        G.v().ASTAnalysis_modified = true;
      }
    }
  }
}
