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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.G;
import soot.Local;
import soot.SootClass;
import soot.Type;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTLabeledBlockNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTSynchronizedBlockNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.AST.ASTUnconditionalLoopNode;
import soot.dava.internal.AST.ASTWhileNode;
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

  Current tasks of the cleaner:

*/

public class LoopStrengthener extends DepthFirstAdapter {

  public LoopStrengthener() {
  }

  public LoopStrengthener(boolean verbose) {
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

        if (temp instanceof ASTWhileNode || temp instanceof ASTUnconditionalLoopNode || temp instanceof ASTDoWhileNode) {

          ASTNode oneNode = getOnlySubNode(temp);
          if (oneNode != null) {
            List<ASTNode> newNode = null;
            if (oneNode instanceof ASTIfNode) {
              newNode = StrengthenByIf.getNewNode(temp, (ASTIfNode) oneNode);
            } else if (oneNode instanceof ASTIfElseNode) {
              newNode = StrengthenByIfElse.getNewNode(temp, (ASTIfElseNode) oneNode);
            }

            if (newNode != null) {
              // some pattern was matched
              // replace the temp node with the newNode
              replaceNode(node, subBodyNumber, nodeNumber, temp, newNode);
              UselessLabelFinder.v().findAndKill(node);
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
      if (temp instanceof ASTWhileNode || temp instanceof ASTUnconditionalLoopNode || temp instanceof ASTDoWhileNode) {

        ASTNode oneNode = getOnlySubNode(temp);
        if (oneNode != null) {
          List<ASTNode> newNode = null;
          if (oneNode instanceof ASTIfNode) {
            newNode = StrengthenByIf.getNewNode(temp, (ASTIfNode) oneNode);
          } else if (oneNode instanceof ASTIfElseNode) {
            newNode = StrengthenByIfElse.getNewNode(temp, (ASTIfElseNode) oneNode);
          }

          if (newNode != null) {
            // some pattern was matched
            // replace the temp node with the newNode

            List<Object> newBody = createNewSubBody(tryBody, nodeNumber, temp, newNode);
            if (newBody != null) {
              // something did not go wrong
              node.replaceTryBody(newBody);
              G.v().ASTTransformations_modified = true;
              // System.out.println("strengthened loop within trybody");
            }
            UselessLabelFinder.v().findAndKill(node);
          }
        }
      } // it was a loop node
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
        if (temp instanceof ASTWhileNode || temp instanceof ASTUnconditionalLoopNode || temp instanceof ASTDoWhileNode) {

          ASTNode oneNode = getOnlySubNode(temp);
          if (oneNode != null) {
            List<ASTNode> newNode = null;
            if (oneNode instanceof ASTIfNode) {
              newNode = StrengthenByIf.getNewNode(temp, (ASTIfNode) oneNode);
            } else if (oneNode instanceof ASTIfElseNode) {
              newNode = StrengthenByIfElse.getNewNode(temp, (ASTIfElseNode) oneNode);
            }

            if (newNode != null) {
              // some pattern was matched
              // replace the temp node with the newNode

              List<Object> newBody = createNewSubBody(body, nodeNumber, temp, newNode);
              if (newBody != null) {
                // something did not go wrong
                catchBody.replaceBody(newBody);
                G.v().ASTTransformations_modified = true;
                // System.out.println("strengthened loop within catchbody");
              }
              UselessLabelFinder.v().findAndKill(node);
            }
          }
        } // it was a loop node
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
          if (temp instanceof ASTWhileNode || temp instanceof ASTUnconditionalLoopNode || temp instanceof ASTDoWhileNode) {

            ASTNode oneNode = getOnlySubNode(temp);
            if (oneNode != null) {
              List<ASTNode> newNode = null;
              if (oneNode instanceof ASTIfNode) {
                newNode = StrengthenByIf.getNewNode(temp, (ASTIfNode) oneNode);
              } else if (oneNode instanceof ASTIfElseNode) {
                newNode = StrengthenByIfElse.getNewNode(temp, (ASTIfElseNode) oneNode);
              }

              if (newNode != null) {
                // some pattern was matched
                // replace the temp node with the newNode

                List<Object> newBody = createNewSubBody(body, nodeNumber, temp, newNode);
                if (newBody != null) {
                  // something did not go wrong put this body in the Map
                  index2BodyList.put(currentIndex, newBody);
                  // replace in actual switchNode
                  node.replaceIndex2BodyList(index2BodyList);
                  G.v().ASTTransformations_modified = true;
                  // System.out.println("strengthened loop within switch body");
                }
                UselessLabelFinder.v().findAndKill(node);
              }
            }
          } // it was a loop node
          temp.apply(this);
          nodeNumber++;
        }
      }
    }
  }

  /*
   * Given an ASTNode as input this method checks the following: 1, The node is either ASTWhile, ASTDoWhile or
   * ASTUnconditionalLoop 2, The node has one subBody 3, The onlySubBody has one node
   *
   * it returns the only node in the only SubBody
   */
  private ASTNode getOnlySubNode(ASTNode node) {
    if (!(node instanceof ASTWhileNode || node instanceof ASTDoWhileNode || node instanceof ASTUnconditionalLoopNode)) {
      // not one of these loops
      return null;
    }
    List<Object> subBodies = node.get_SubBodies();
    if (subBodies.size() != 1) {
      // we are coming from loop nodes so subBodies should always be one
      return null;
    }
    List subBody = (List) subBodies.get(0);
    if (subBody.size() != 1) {
      // only want the case which the subBody has a single node
      return null;
    }
    return (ASTNode) subBody.get(0);
  }

  /*
   * - Go through the node bodies till you find subBodyNumber - Go through this subBody until you find nodeNumber - This is
   * the temp node Replace it with the newNodes
   *
   * Node is the node which contains the loop node subBodyNumber is the subBody which of the node which contains the loopNode
   * nodeNumber is the location of the loopNode in the subBody newNode is the loopNode which will replace the old loopNode
   */
  private void replaceNode(ASTNode node, int subBodyNumber, int nodeNumber, ASTNode loopNode, List<ASTNode> newNode) {
    if (!(node instanceof ASTIfElseNode)) {
      // these are the nodes which always have one subBody
      List<Object> subBodies = node.get_SubBodies();
      if (subBodies.size() != 1) {
        // there is something wrong
        throw new RuntimeException("Please report this benchmark to the programmer");
      }
      List<Object> onlySubBody = (List<Object>) subBodies.get(0);

      /*
       * The onlySubBody contains the loopNode to be replaced at location given by the nodeNumber variable
       */
      List<Object> newBody = createNewSubBody(onlySubBody, nodeNumber, loopNode, newNode);
      if (newBody == null) {
        // something went wrong
        return;
      }
      if (node instanceof ASTMethodNode) {
        ((ASTMethodNode) node).replaceBody(newBody);
        G.v().ASTTransformations_modified = true;
        // System.out.println("Stenghtened Loop");
      } else if (node instanceof ASTSynchronizedBlockNode) {
        ((ASTSynchronizedBlockNode) node).replaceBody(newBody);
        G.v().ASTTransformations_modified = true;
        // System.out.println("Stenghtened Loop in synchblock");
      } else if (node instanceof ASTLabeledBlockNode) {
        ((ASTLabeledBlockNode) node).replaceBody(newBody);
        G.v().ASTTransformations_modified = true;
        // System.out.println("Stenghtened Loop in labeledblock node");
      } else if (node instanceof ASTUnconditionalLoopNode) {
        ((ASTUnconditionalLoopNode) node).replaceBody(newBody);
        G.v().ASTTransformations_modified = true;
        // System.out.println("Stenghtened Loop in unconditionalloopNode");
      } else if (node instanceof ASTIfNode) {
        ((ASTIfNode) node).replaceBody(newBody);
        G.v().ASTTransformations_modified = true;
        // System.out.println("Stenghtened Loop in ifnode");
      } else if (node instanceof ASTWhileNode) {
        ((ASTWhileNode) node).replaceBody(newBody);
        G.v().ASTTransformations_modified = true;
        // System.out.println("Stenghtened Loop in whilenode");
      } else if (node instanceof ASTDoWhileNode) {
        ((ASTDoWhileNode) node).replaceBody(newBody);
        G.v().ASTTransformations_modified = true;
        // System.out.println("Stenghtened Loop in dowhile node");
      } else {
        // there is no other case something is wrong if we get here
        return;
      }
    } else {
      // its an ASTIfElseNode
      // if its an ASIfElseNode then check which Subbody has the labeledBlock
      if (subBodyNumber != 0 && subBodyNumber != 1) {
        // something bad is happening dont do nothin
        // System.out.println("Error-------not modifying AST");
        return;
      }
      List<Object> subBodies = node.get_SubBodies();
      if (subBodies.size() != 2) {
        // there is something wrong
        throw new RuntimeException("Please report this benchmark to the programmer");
      }

      List<Object> toModifySubBody = (List<Object>) subBodies.get(subBodyNumber);

      /*
       * The toModifySubBody contains the labeledBlockNode to be removed at location given by the nodeNumber variable
       */
      List<Object> newBody = createNewSubBody(toModifySubBody, nodeNumber, loopNode, newNode);
      if (newBody == null) {
        // something went wrong
        return;
      }
      if (subBodyNumber == 0) {
        // the if body was modified
        // System.out.println("Stenghtened Loop");
        G.v().ASTTransformations_modified = true;
        ((ASTIfElseNode) node).replaceBody(newBody, (List<Object>) subBodies.get(1));
      } else if (subBodyNumber == 1) {
        // else body was modified
        // System.out.println("Stenghtened Loop");
        G.v().ASTTransformations_modified = true;
        ((ASTIfElseNode) node).replaceBody((List<Object>) subBodies.get(0), newBody);
      } else {
        // realllly shouldnt come here
        // something bad is happening dont do nothin
        // System.out.println("Error-------not modifying AST");
        return;
      }

    } // end of ASTIfElseNode
  }

  public static List<Object> createNewSubBody(List<Object> oldSubBody, int nodeNumber, ASTNode oldNode,
      List<ASTNode> newNode) {
    // create a new SubBody
    List<Object> newSubBody = new ArrayList<Object>();

    // this is an iterator of ASTNodes
    Iterator<Object> it = oldSubBody.iterator();

    // copy to newSubBody all nodes until you get to nodeNumber
    int index = 0;
    while (index != nodeNumber) {
      if (!it.hasNext()) {
        return null;
      }
      newSubBody.add(it.next());
      index++;
    }

    // at this point the iterator is pointing to the ASTNode to be removed
    // just to make sure check this
    ASTNode toRemove = (ASTNode) it.next();
    if (toRemove.toString().compareTo(oldNode.toString()) != 0) {
      System.out.println("The replace nodes dont match please report benchmark to developer");
      return null;
    } else {
      // not adding the oldNode into the newSubBody but adding its replacement
      newSubBody.addAll(newNode);
    }

    // add any remaining nodes in the oldSubBody to the new one
    while (it.hasNext()) {
      newSubBody.add(it.next());
    }

    // newSubBody is ready return it
    return newSubBody;
  }

}
