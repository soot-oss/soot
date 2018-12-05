package soot.dava.toolkits.base.AST.traversals;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import soot.Local;
import soot.Value;
import soot.ValueBox;
import soot.dava.internal.AST.ASTAggregatedCondition;
import soot.dava.internal.AST.ASTBinaryCondition;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTSynchronizedBlockNode;
import soot.dava.internal.AST.ASTUnaryCondition;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.dava.toolkits.base.AST.structuredAnalysis.ReachingDefs;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;

/*
 THE ALGORITHM USES THE RESULTS OF REACHINGDEFS STRUCTURAL FLOW ANALYSIS

 DEFINITION uD Chain:
 For a use of variable x, the uD Chain gives ALL POSSIBLE definitions of x that can reach the use x

 DEFINITION dU Chain:
 For a definition d, the dU Chain gives all places where this definition is used


 Need to be very clear when a local can be used
 It can be used in the following places:
 a, a conditional in if, ifelse, while , do while, for condition
 b, in the for init or update
 c, in a switch choice
 d, in a syncrhnoized block
 d, in a statement

 */
public class ASTUsesAndDefs extends DepthFirstAdapter {
  public static boolean DEBUG = false;
  HashMap<Object, List<DefinitionStmt>> uD; // mapping a use to all possible
  // definitions
  HashMap<Object, List> dU; // mapping a def to all possible uses
  ReachingDefs reaching; // using structural analysis information

  public ASTUsesAndDefs(ASTNode AST) {
    uD = new HashMap<Object, List<DefinitionStmt>>();
    dU = new HashMap<Object, List>();
    reaching = new ReachingDefs(AST);
  }

  public ASTUsesAndDefs(boolean verbose, ASTNode AST) {
    super(verbose);
    uD = new HashMap<Object, List<DefinitionStmt>>();
    dU = new HashMap<Object, List>();
    reaching = new ReachingDefs(AST);
  }

  /*
   * Method is used to strip away boxes from the actual values only those are returned which are locals
   */
  private List<Value> getUsesFromBoxes(List useBoxes) {
    ArrayList<Value> toReturn = new ArrayList<Value>();
    Iterator it = useBoxes.iterator();
    while (it.hasNext()) {
      Value val = ((ValueBox) it.next()).getValue();
      if (val instanceof Local) {
        toReturn.add(val);
      }
    }
    // System.out.println("VALUES:"+toReturn);
    return toReturn;
  }

  public void checkStatementUses(Stmt s, Object useNodeOrStatement) {
    List useBoxes = s.getUseBoxes();
    // System.out.println("Uses in this statement:"+useBoxes);
    List<Value> uses = getUsesFromBoxes(useBoxes);
    // System.out.println("Local Uses in this statement:"+uses);

    Iterator<Value> it = uses.iterator();
    while (it.hasNext()) {
      Local local = (Local) it.next();
      createUDDUChain(local, useNodeOrStatement);
    } // end of going through all locals uses in statement

    /*
     * see if this is a def stmt in which case add an empty entry into the dU chain
     *
     * The wisdowm behind this is that later on when this definition is used we will use this arraylist to store the uses of
     * this definition
     */
    if (s instanceof DefinitionStmt) {
      // check if dU doesnt already have something for this
      if (dU.get(s) == null) {
        dU.put(s, new ArrayList());
      }
    }
  }

  /*
   * The method gets the reaching defs of local used Then all the possible defs are added into the uD chain of the node The
   * use is added to all the defs reaching this node
   */
  public void createUDDUChain(Local local, Object useNodeOrStatement) {
    // System.out.println("Local is:"+local);
    // System.out.println("useNodeOrStatement is"+useNodeOrStatement);

    List<DefinitionStmt> reachingDefs = reaching.getReachingDefs(local, useNodeOrStatement);
    if (DEBUG) {
      System.out.println("Reaching def for:" + local + " are:" + reachingDefs);
    }

    // add the reaching defs into the use def chain
    Object tempObj = uD.get(useNodeOrStatement);
    if (tempObj != null) {
      List<DefinitionStmt> tempList = (List<DefinitionStmt>) tempObj;
      tempList.addAll(reachingDefs);
      uD.put(useNodeOrStatement, tempList);
    } else {
      uD.put(useNodeOrStatement, reachingDefs);
    }

    // add the use into the def use chain
    Iterator<DefinitionStmt> defIt = reachingDefs.iterator();
    while (defIt.hasNext()) {
      // for each reaching def
      Object defStmt = defIt.next();

      // get the dU Chain
      Object useObj = dU.get(defStmt);
      List<Object> uses = null;
      if (useObj == null) {
        uses = new ArrayList<Object>();
      } else {
        uses = (List<Object>) useObj;
      }

      // add the new local use to this list (we add the node since thats
      // where the local is used
      uses.add(useNodeOrStatement);
      // System.out.println("Adding definition:"+defStmt+"with uses:"+uses);
      dU.put(defStmt, uses);
    }
  }

  /*
   * Given a unary/binary or aggregated condition this method is used to find the locals used in the condition
   */
  public List<Value> getUseList(ASTCondition cond) {
    ArrayList<Value> useList = new ArrayList<Value>();
    if (cond instanceof ASTAggregatedCondition) {
      useList.addAll(getUseList(((ASTAggregatedCondition) cond).getLeftOp()));
      useList.addAll(getUseList(((ASTAggregatedCondition) cond).getRightOp()));
      return useList;
    } else if (cond instanceof ASTUnaryCondition) {
      // get uses from unary condition
      List<Value> uses = new ArrayList<Value>();

      Value val = ((ASTUnaryCondition) cond).getValue();
      if (val instanceof Local) {
        if (DEBUG) {
          System.out.println("adding local from unary condition as a use" + val);
        }
        uses.add(val);
      } else {
        List useBoxes = val.getUseBoxes();
        uses = getUsesFromBoxes(useBoxes);
      }
      return uses;
    } else if (cond instanceof ASTBinaryCondition) {
      // get uses from binaryCondition
      Value val = ((ASTBinaryCondition) cond).getConditionExpr();
      List useBoxes = val.getUseBoxes();
      return getUsesFromBoxes(useBoxes);
    } else {
      throw new RuntimeException("Method getUseList in ASTUsesAndDefs encountered unknown condition type");
    }
  }

  /*
   * This method gets a list of all uses of locals in the condition Then it invokes the createUDDUChain for each local
   */
  public void checkConditionalUses(ASTCondition cond, ASTNode node) {
    List<Value> useList = getUseList(cond);

    // System.out.println("FOR NODE with condition:"+cond+"USE list is:"+useList);

    // FOR EACH USE
    Iterator<Value> it = useList.iterator();
    while (it.hasNext()) {
      Local local = (Local) it.next();
      // System.out.println("creating uddu for "+local);
      createUDDUChain(local, node);
    } // end of going through all locals uses in condition
  }

  /*
   * The key in a switch stmt can be a local or a value which can contain Locals
   *
   * Hence the some what indirect approach
   */
  public void inASTSwitchNode(ASTSwitchNode node) {
    Value val = node.get_Key();
    List<Value> uses = new ArrayList<Value>();
    if (val instanceof Local) {
      uses.add(val);
    } else {
      List useBoxes = val.getUseBoxes();
      uses = getUsesFromBoxes(useBoxes);
    }

    Iterator<Value> it = uses.iterator();
    // System.out.println("SWITCH uses start:");
    while (it.hasNext()) {
      Local local = (Local) it.next();
      // System.out.println(local);
      createUDDUChain(local, node);
    } // end of going through all locals uses in switch key
      // System.out.println("SWITCH uses end:");
  }

  public void inASTSynchronizedBlockNode(ASTSynchronizedBlockNode node) {
    Local local = node.getLocal();
    createUDDUChain(local, node);
  }

  /*
   * The condition of an if node can use a local
   */
  public void inASTIfNode(ASTIfNode node) {
    ASTCondition cond = node.get_Condition();
    checkConditionalUses(cond, node);
  }

  /*
   * The condition of an ifElse node can use a local
   */
  public void inASTIfElseNode(ASTIfElseNode node) {
    ASTCondition cond = node.get_Condition();
    checkConditionalUses(cond, node);
  }

  /*
   * The condition of a while node can use a local
   */
  public void inASTWhileNode(ASTWhileNode node) {
    ASTCondition cond = node.get_Condition();
    checkConditionalUses(cond, node);
  }

  /*
   * The condition of a doWhile node can use a local
   */
  public void inASTDoWhileNode(ASTDoWhileNode node) {
    ASTCondition cond = node.get_Condition();
    checkConditionalUses(cond, node);
  }

  /*
   * The init of a for loop can use a local The condition of a for node can use a local The update in a for loop can use a
   * local
   */
  public void inASTForLoopNode(ASTForLoopNode node) {

    // checking uses in init
    for (AugmentedStmt as : node.getInit()) {
      Stmt s = as.get_Stmt();
      checkStatementUses(s, node);
    }

    // checking uses in condition
    ASTCondition cond = node.get_Condition();
    checkConditionalUses(cond, node);

    // checking uses in update
    for (AugmentedStmt as : node.getUpdate()) {
      Stmt s = as.get_Stmt();
      checkStatementUses(s, node);
    }
  }

  public void inASTStatementSequenceNode(ASTStatementSequenceNode node) {
    for (AugmentedStmt as : node.getStatements()) {
      Stmt s = as.get_Stmt();
      // in the case of stmtts in a stmtt sequence each stmt is considered
      // an entity
      // compared to the case where these stmts occur within other
      // constructs
      // where the node is the entity
      checkStatementUses(s, s);
    }
  }

  /*
   * Input is a construct (ASTNode or statement) that has some locals used and output are all defs reached for all the uses
   * in that construct...
   *
   * dont know whether it actually makes sense for the nodes but it definetly makes sense for the statements
   */
  public List getUDChain(Object node) {
    return uD.get(node);
  }

  /*
   * Give it a def stmt and it will return all places where it is used a use is either a statement or a node(condition,
   * synch, switch , for etc)
   */
  public List getDUChain(Object node) {
    return dU.get(node);
  }

  public HashMap<Object, List> getDUHashMap() {
    return dU;
  }

  public void outASTMethodNode(ASTMethodNode node) {
    // print();
  }

  public void print() {
    System.out.println("\n\n\nPRINTING uD dU CHAINS ______________________________");
    Iterator<Object> it = dU.keySet().iterator();
    while (it.hasNext()) {
      DefinitionStmt s = (DefinitionStmt) it.next();
      System.out.println("*****The def  " + s + " has following uses:");
      Object obj = dU.get(s);
      if (obj != null) {
        ArrayList list = (ArrayList) obj;
        Iterator tempIt = list.iterator();
        while (tempIt.hasNext()) {
          Object tempUse = tempIt.next();
          System.out.println("-----------Use  " + tempUse);
          System.out.println("----------------Defs of this use:   " + uD.get(tempUse));
        }
      }
    }
    System.out.println("END --------PRINTING uD dU CHAINS ______________________________");
  }

}
