package soot.dava.toolkits.base.AST.transformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Nomair A. Naeem
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.G;
import soot.Local;
import soot.SootClass;
import soot.Type;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTLabeledBlockNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;

/*
  Nomair A. Naeem 21-FEB-2005

  In the depthFirstAdaptor children of a ASTNode
  are gotten in three ways
  a, ASTStatementSequenceNode uses one way see caseASTStatementSequenceNode in DepthFirstAdapter
  b, ASTTryNode uses another way see caseASTTryNode in DepthFirstAdapter
  c, All other nodes use normalRetrieving method to retrieve the children

  TO MAKE CODE EFFECIENT BLOCK THE ANALYSIS TO GOING INTO STATEMENTS
  this is done by overriding the caseASTStatementSequenceNode

  Current tasks of the cleaner
         Invoke UselessLabeledBlockRemover
	 Invoke EmptyElseRemover
	 Apply OrAggregatorThree
*/

public class ASTCleaner extends DepthFirstAdapter {

  public ASTCleaner() {
  }

  public ASTCleaner(boolean verbose) {
    super(verbose);
  }

  public void caseASTStatementSequenceNode(ASTStatementSequenceNode node) {
  }

  /*
   * Note the ASTNode in this case can be any of the following: ASTMethodNode ASTSwitchNode ASTIfNode ASTIfElseNode
   * ASTUnconditionalWhileNode ASTWhileNode ASTDoWhileNode ASTForLoopNode ASTLabeledBlockNode ASTSynchronizedBlockNode
   */
  public void normalRetrieving(ASTNode node) {
    if (node instanceof ASTSwitchNode) {
      dealWithSwitchNode((ASTSwitchNode) node);
      return;
    }

    // from the Node get the subBodes
    Iterator<Object> sbit = node.get_SubBodies().iterator();

    // onlyASTIfElseNode has 2 subBodies but we need to deal with that
    int subBodyNumber = 0;
    while (sbit.hasNext()) {
      Object subBody = sbit.next();
      Iterator it = ((List) subBody).iterator();

      int nodeNumber = 0;
      // go over the ASTNodes in this subBody and apply
      while (it.hasNext()) {
        ASTNode temp = (ASTNode) it.next();
        if (temp instanceof ASTLabeledBlockNode) {
          // check if the label is null
          ASTLabeledBlockNode labelBlock = (ASTLabeledBlockNode) temp;
          SETNodeLabel label = labelBlock.get_Label();
          if (label.toString() == null) {
            // uselessLabeledBlock Found REMOVE IT
            UselessLabeledBlockRemover.removeLabeledBlock(node, labelBlock, subBodyNumber, nodeNumber);
            if (G.v().ASTTransformations_modified) {
              return;
            }
          }
        } else if (temp instanceof ASTIfElseNode) {
          // check if there is an empty else body
          List<Object> elseBody = ((ASTIfElseNode) temp).getElseBody();
          if (elseBody.size() == 0) {
            EmptyElseRemover.removeElseBody(node, (ASTIfElseNode) temp, subBodyNumber, nodeNumber);
          }
        } else if (temp instanceof ASTIfNode) {
          // check if the next node in the subBody is also an ASTIfNode in which case invoke OrAggregatorThree
          if (it.hasNext()) {
            // means we can get the nodeNumber+1
            ASTNode nextNode = (ASTNode) ((List) subBody).get(nodeNumber + 1);
            if (nextNode instanceof ASTIfNode) {
              // found an If followed by another if might match Patter 3.
              OrAggregatorThree.checkAndTransform(node, (ASTIfNode) temp, (ASTIfNode) nextNode, nodeNumber, subBodyNumber);
              if (G.v().ASTTransformations_modified) {
                // if we modified something we want to stop since the tree is stale
                // System.out.println("here");
                return;
              }

            }
          }
        }
        temp.apply(this);
        nodeNumber++;
      }
      subBodyNumber++;
    } // end of going over subBodies
  }

  public void caseASTTryNode(ASTTryNode node) {
    inASTTryNode(node);

    // get try body
    List<Object> tryBody = node.get_TryBody();
    Iterator<Object> it = tryBody.iterator();

    int nodeNumber = 0;
    // go over the ASTNodes and apply
    while (it.hasNext()) {
      ASTNode temp = (ASTNode) it.next();
      if (temp instanceof ASTLabeledBlockNode) {
        // check if the label is null
        ASTLabeledBlockNode labelBlock = (ASTLabeledBlockNode) temp;
        SETNodeLabel label = labelBlock.get_Label();
        if (label.toString() == null) {
          // uselessLabeledBlock Found REMOVE IT

          List<Object> newBody = UselessLabeledBlockRemover.createNewSubBody(tryBody, nodeNumber, labelBlock);
          if (newBody != null) {
            // something did not go wrong
            node.replaceTryBody(newBody);
            G.v().ASTTransformations_modified = true;
            // System.out.println("REMOVED LABEL from within trybody");
          }
        }
      } else if (temp instanceof ASTIfElseNode) {
        // check if there is an empty else body
        List<Object> elseBody = ((ASTIfElseNode) temp).getElseBody();
        if (elseBody.size() == 0) {
          // System.out.println("Empty else body found"+temp);
          List<Object> newBody = EmptyElseRemover.createNewNodeBody(tryBody, nodeNumber, (ASTIfElseNode) temp);
          if (newBody != null) {
            // something did not go wrong
            node.replaceTryBody(newBody);
            G.v().ASTTransformations_modified = true;
            // System.out.println("REMOVED ELSEBODY from within trybody");
            return;
          }
        }
      } else if (temp instanceof ASTIfNode) {
        // check if the next node in the subBody is also an ASTIfNode in which case invoke OrAggregatorThree
        if (it.hasNext()) {
          // means we can get the nodeNumber+1
          ASTNode nextNode = (ASTNode) tryBody.get(nodeNumber + 1);
          if (nextNode instanceof ASTIfNode) {
            // found an If followed by another if might match Patter 3.
            List<Object> newBody
                = OrAggregatorThree.createNewNodeBody(tryBody, nodeNumber, (ASTIfNode) temp, (ASTIfNode) nextNode);
            if (newBody != null) {
              // something did not go wrong and pattern was matched
              node.replaceTryBody(newBody);
              G.v().ASTTransformations_modified = true;
              // we modified something we want to stop since the tree is stale
              // System.out.println("here");
              return;
              // System.out.println("OR AGGREGATOR THREE");
            }
          }
        }
      }
      temp.apply(this);
      nodeNumber++;
    }

    Map<Object, Object> exceptionMap = node.get_ExceptionMap();
    Map<Object, Object> paramMap = node.get_ParamMap();
    // get catch list and apply on the following
    // a, type of exception caught
    // b, local of exception
    // c, catchBody
    List<Object> catchList = node.get_CatchList();
    Iterator<Object> itBody = null;
    it = catchList.iterator();
    while (it.hasNext()) {
      ASTTryNode.container catchBody = (ASTTryNode.container) it.next();

      SootClass sootClass = ((SootClass) exceptionMap.get(catchBody));
      Type type = sootClass.getType();

      // apply on type of exception
      caseType(type);

      // apply on local of exception
      Local local = (Local) paramMap.get(catchBody);
      decideCaseExprOrRef(local);

      // apply on catchBody
      List<Object> body = (List<Object>) catchBody.o;
      itBody = body.iterator();

      nodeNumber = 0;
      // go over the ASTNodes and apply
      while (itBody.hasNext()) {
        ASTNode temp = (ASTNode) itBody.next();
        if (temp instanceof ASTLabeledBlockNode) {
          // check if the label is null
          ASTLabeledBlockNode labelBlock = (ASTLabeledBlockNode) temp;
          SETNodeLabel label = labelBlock.get_Label();
          if (label.toString() == null) {
            // uselessLabeledBlock Found REMOVE IT

            List<Object> newBody = UselessLabeledBlockRemover.createNewSubBody(body, nodeNumber, labelBlock);
            if (newBody != null) {
              // something did not go wrong
              catchBody.replaceBody(newBody);
              G.v().ASTTransformations_modified = true;
              // System.out.println("REMOVED LABEL from within catchlist");
            }

          }
        } else if (temp instanceof ASTIfElseNode) {
          // check if there is an empty else body
          List<Object> elseBody = ((ASTIfElseNode) temp).getElseBody();
          if (elseBody.size() == 0) {
            // System.out.println("Empty else body found"+temp);
            List<Object> newBody = EmptyElseRemover.createNewNodeBody(body, nodeNumber, (ASTIfElseNode) temp);
            if (newBody != null) {
              // something did not go wrong
              catchBody.replaceBody(newBody);
              G.v().ASTTransformations_modified = true;
              // System.out.println("REMOVED ELSEBODY FROm within catchlist");
              return;
            }
          }
        } else if (temp instanceof ASTIfNode) {
          // check if the next node in the subBody is also an ASTIfNode in which case invoke OrAggregatorThree
          if (itBody.hasNext()) {
            // means we can get the nodeNumber+1
            ASTNode nextNode = (ASTNode) body.get(nodeNumber + 1);
            if (nextNode instanceof ASTIfNode) {
              // found an If followed by another if might match Patter 3.
              List<Object> newBody
                  = OrAggregatorThree.createNewNodeBody(body, nodeNumber, (ASTIfNode) temp, (ASTIfNode) nextNode);
              if (newBody != null) {
                // something did not go wrong and pattern was matched
                catchBody.replaceBody(newBody);
                G.v().ASTTransformations_modified = true;
                // System.out.println("OR AGGREGATOR THREE");
                return;
              }
            }
          }
        }
        temp.apply(this);
        nodeNumber++;
      }
    }

    outASTTryNode(node);
  }

  private void dealWithSwitchNode(ASTSwitchNode node) {
    // do a depthfirst on elements of the switchNode

    List<Object> indexList = node.getIndexList();
    Map<Object, List<Object>> index2BodyList = node.getIndex2BodyList();

    Iterator<Object> it = indexList.iterator();
    while (it.hasNext()) {
      // going through all the cases of the switch statement
      Object currentIndex = it.next();
      List<Object> body = index2BodyList.get(currentIndex);

      if (body != null) {
        // this body is a list of ASTNodes

        Iterator<Object> itBody = body.iterator();
        int nodeNumber = 0;
        // go over the ASTNodes and apply
        while (itBody.hasNext()) {
          ASTNode temp = (ASTNode) itBody.next();
          if (temp instanceof ASTLabeledBlockNode) {
            // check if the label is null
            ASTLabeledBlockNode labelBlock = (ASTLabeledBlockNode) temp;
            SETNodeLabel label = labelBlock.get_Label();
            if (label.toString() == null) {
              // uselessLabeledBlock Found REMOVE IT

              List<Object> newBody = UselessLabeledBlockRemover.createNewSubBody(body, nodeNumber, labelBlock);
              if (newBody != null) {
                // something did not go wrong

                // put this body in the Map
                index2BodyList.put(currentIndex, newBody);
                // replace in actual switchNode
                node.replaceIndex2BodyList(index2BodyList);
                G.v().ASTTransformations_modified = true;
                // System.out.println("REMOVED LABEL From Within Switch");
              }
            }
          } else if (temp instanceof ASTIfElseNode) {
            // check if there is an empty else body
            List<Object> elseBody = ((ASTIfElseNode) temp).getElseBody();
            if (elseBody.size() == 0) {
              // System.out.println("Empty else body found"+temp);
              List<Object> newBody = EmptyElseRemover.createNewNodeBody(body, nodeNumber, (ASTIfElseNode) temp);
              if (newBody != null) {
                // something did not go wrong

                // put this body in the Map
                index2BodyList.put(currentIndex, newBody);
                // replace in actual switchNode
                node.replaceIndex2BodyList(index2BodyList);
                G.v().ASTTransformations_modified = true;
                // System.out.println("REMOVED ELSEBODY FROM WITHIN SWITCH");
                return;
              }
            }
          } else if (temp instanceof ASTIfNode) {
            // check if the next node in the subBody is also an ASTIfNode in which case invoke OrAggregatorThree
            if (itBody.hasNext()) {
              // means we can get the nodeNumber+1
              ASTNode nextNode = (ASTNode) body.get(nodeNumber + 1);
              if (nextNode instanceof ASTIfNode) {
                // found an If followed by another if might match Patter 3.
                List<Object> newBody
                    = OrAggregatorThree.createNewNodeBody(body, nodeNumber, (ASTIfNode) temp, (ASTIfNode) nextNode);
                if (newBody != null) {
                  // something did not go wrong and pattern was matched

                  // put this body in the Map
                  index2BodyList.put(currentIndex, newBody);
                  // replace in actual switchNode
                  node.replaceIndex2BodyList(index2BodyList);

                  G.v().ASTTransformations_modified = true;
                  // System.out.println("OR AGGREGATOR THREE");
                  return;
                }
              }
            }
          }
          temp.apply(this);
          nodeNumber++;
        }
      }
    }
  }
}
