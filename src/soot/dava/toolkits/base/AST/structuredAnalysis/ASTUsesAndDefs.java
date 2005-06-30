/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Nomair A. Naeem
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


package soot.dava.toolkits.base.AST.structuredAnalysis;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.AST.analysis.*;



/*
  THE ALGORITHM USES THE RESULTS OF REACHINGDEFS STRUCTURAL FLOW ANALYSIS

  DEFINITION uD Chain:
  For a use of variable x, the uD Chain gives all possible definitions of x that can reach the use x

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
public class ASTUsesAndDefs extends DepthFirstAdapter{
    HashMap uD;
    HashMap dU;
    ReachingDefs reaching;

    public ASTUsesAndDefs(ASTNode AST){
	uD = new HashMap();
	dU = new HashMap();
	reaching = new ReachingDefs(AST);
    }

    public ASTUsesAndDefs(boolean verbose,ASTNode AST){
	super(verbose);
	uD = new HashMap();
	dU = new HashMap();
	reaching = new ReachingDefs(AST);
    }





    public void checkStatementUses(Stmt s,Object useNodeOrStatement){
	List useBoxes = s.getUseBoxes();
	//System.out.println("Uses in this statement:"+useBoxes);
	List uses= getUsesFromBoxes(useBoxes);
	//System.out.println("Local Uses in this statement:"+uses);

	Iterator it = uses.iterator();
	while(it.hasNext()){
	    Local local = (Local)it.next();
	    createUDDUChain(local,useNodeOrStatement);
	}//end of going through all locals uses in statement


	//see if this is a def stmt in which case add an empty entry into the dU chain
	if(s instanceof DefinitionStmt){
	    //check if dU doesnt already have something for this
	    if(dU.get(s)==null){
		dU.put(s,new ArrayList());
	    }
	}
    }

	






    /*
     * The method gets the reaching defs of local used
     * Then all the possible defs are added into the uD chain of the node
     * The use is added to all the defs reaching this node
     */
    public void createUDDUChain(Local local, Object useNodeOrStatement){
	List reachingDefs = reaching.getReachingDefs(local,useNodeOrStatement);
	//System.out.println("Reaching def for:"+local+" are:"+reachingDefs);
	
	//add the reaching defs into the use def chain
	uD.put(useNodeOrStatement,reachingDefs);
	
	//add the use into the def use chain
	Iterator defIt = reachingDefs.iterator();
	while(defIt.hasNext()){
	    //for each reaching def
	    Object defStmt = defIt.next();
	    
	    //get the dU Chain
	    Object useObj = dU.get(defStmt);
	    List uses=null;
	    if(useObj==null)
		uses = new ArrayList();
	    else
		uses = (List)useObj;
	    
	    //add the new local use to this list (we add the node since thats where the local is used
	    uses.add(useNodeOrStatement);
	    //System.out.println("Adding definition:"+defStmt+"with uses:"+uses);
	    dU.put(defStmt,uses);
	}
    }







    /*
     * This method gets a list of all uses of locals in the condition
     * Then it invoked the createUDDUChain for each local
     */
    public void checkConditionalUses(ASTCondition cond,ASTNode node){
	List useList = getUseList(cond);
	//System.out.println("FOR NODE with condition:"+cond+"USE list is:"+useList);

	//FOR EACH USE
	Iterator it = useList.iterator();
	while(it.hasNext()){
	    Local local = (Local)it.next();
	    createUDDUChain(local,node);
	}//end of going through all locals uses in condition
    }








    public List getUseList(ASTCondition cond){
	ArrayList useList = new ArrayList();
	if(cond instanceof ASTAggregatedCondition){
	    useList.addAll(getUseList(((ASTAggregatedCondition)cond).getLeftOp()));
	    useList.addAll(getUseList(((ASTAggregatedCondition)cond).getLeftOp()));
	    return useList;
	}
	else if(cond instanceof ASTUnaryCondition){
	    //get uses from unary condition
	    List uses = new ArrayList();

	    Value val = ((ASTUnaryCondition)cond).getValue();
	    if(val instanceof Local){
		uses.add(val);
	    }
	    else{
		List useBoxes = val.getUseBoxes();
		uses= getUsesFromBoxes(useBoxes);
	    }
	    return uses;
	}
	else if(cond instanceof ASTBinaryCondition){
	    //get uses from binaryCondition
	    Value val = ((ASTBinaryCondition)cond).getConditionExpr();
	    List useBoxes = val.getUseBoxes();
	    return getUsesFromBoxes(useBoxes);
	}
	else{
	    throw new RuntimeException("Method getUseList in ASTUsesAndDefs encountered unknown condition type");
	}
    }





    public List getUsesFromBoxes(List useBoxes){
	ArrayList toReturn = new ArrayList();
	Iterator it = useBoxes.iterator();
	while(it.hasNext()){
	    Value val =((ValueBox)it.next()).getValue();
	    if(val instanceof Local)
		toReturn.add(val);
	}
	//System.out.println("VALUES:"+toReturn);
	return toReturn;
    }












    /*
      The key in a switch stmt can be a local or a value
      which can contain Locals

      Hence the some what indirect approach
    */
    public void inASTSwitchNode(ASTSwitchNode node){
	Value val = (Value)node.get_Key();
	List uses = new ArrayList();
	if(val instanceof Local){
	    uses.add(val);
	}
	else{
	    List useBoxes = val.getUseBoxes();
	    uses= getUsesFromBoxes(useBoxes);
	}

	Iterator it = uses.iterator();
	//System.out.println("SWITCH uses start:");
	while(it.hasNext()){
	    Local local = (Local)it.next();
	    //System.out.println(local);
	    createUDDUChain(local,node);
	}//end of going through all locals uses in switch key
	//System.out.println("SWITCH uses end:");
    }






    public void inASTSynchronizedBlockNode(ASTSynchronizedBlockNode node){
	Local local = node.getLocal();
	createUDDUChain(local,node);
    }







    /*
     * The condition of an if node can use a local
     *
     */
    public void inASTIfNode(ASTIfNode node){
	ASTCondition cond = node.get_Condition();
	checkConditionalUses(cond,node);
    }


    /*
     * The condition of an ifElse node can use a local
     *
     */
    public void inASTIfElseNode(ASTIfElseNode node){
	ASTCondition cond = node.get_Condition();
	checkConditionalUses(cond,node);
    }




    /*
     * The condition of a while node can use a local
     *
     */
    public void inASTWhileNode(ASTWhileNode node){
	ASTCondition cond = node.get_Condition();
	checkConditionalUses(cond,node);
    }



    /*
     * The condition of a doWhile node can use a local
     *
     */
    public void inASTDoWhileNode(ASTDoWhileNode node){
	ASTCondition cond = node.get_Condition();
	checkConditionalUses(cond,node);
    }




    /*
     * The init of a for loop can use a local
     * The condition of a for node can use a local
     * The update in a for loop can use a local
     *
     */
    public void inASTForLoopNode(ASTForLoopNode node){

	//checking uses in init
	List init = node.getInit();
	Iterator it = init.iterator();
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    checkStatementUses(s,node);
	}

	//checking uses in condition
	ASTCondition cond = node.get_Condition();
	checkConditionalUses(cond,node);

	
	//checking uses in update
	List update = node.getUpdate();
	it = update.iterator();
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    checkStatementUses(s,node);
	}
    }



    public void inASTStatementSequenceNode(ASTStatementSequenceNode node){
	List statements = node.getStatements();
	Iterator it = statements.iterator();
	
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    //in the case of stmtts in a stmtt sequence each stmt is considered an entity
	    //compared to the case where these stmts occur within other constructs where the node is the entity
	    checkStatementUses(s,s);
	}
    }


    /*
     * Input is a construct (ASTNode etc) that has some locals used and output are all defs reached 
     * for all the uses in that construct
     */
    public List getUDChain(Object node){
	return (List)uD.get(node);
    }


    /*
     * Give it a def stmt and it will return all places where it is used
     */
    public List getDUChain(Object node){
	return (List)dU.get(node);
    }

    public HashMap getDUHashMap(){
	return dU;
    }


    public void print(){
	//System.out.println("\n\n\nHEREEEEE ______________________________");
	Iterator it = dU.keySet().iterator();
	while(it.hasNext()){
	    DefinitionStmt s = (DefinitionStmt)it.next();
	    //System.out.println("***************The def"+s+" has uses:"+dU.get(s));
	}
    }

}