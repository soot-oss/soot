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
import soot.dava.internal.javaRep.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.asg.*;
import soot.dava.toolkits.base.AST.analysis.*;
import soot.dava.toolkits.base.AST.structuredAnalysis.*;

/*
  This analysis uses the results from
      ReachingCopies and uD and dU chains 
  to eliminate extra copies


  ALGORITHM:
  When you encounter a copy stmt (a=b) find all uses of local a (using dU chain)

  if For ALL uses the ReachingCopies set contains copy stmt (a=b)
    Remove Copy Stmt
    Replace use of a with use of b

    Note:
      copy stmts can be encountered in:
        a, ASTStatementSequenceNode
	b, for loop init      -------> dont want to remove this
	c, for loop update   ---------> dont want to remove this

    IMPORTANT: NEED TO REMOVE LOCALS WHICH ARE NO LONGER USED
    At the end of the depth first traversal i.e. outASTMethodNode method
    we need to check that any locals declared which are unnecceasy should be removed
*/

public class CopyPropagation extends DepthFirstAdapter{
    ASTNode AST;
    ASTUsesAndDefs useDefs;
    ReachingCopies reachingCopies;
    ASTParentNodeFinder parentOf;
    
    boolean someCopyStmtModified;

    public CopyPropagation(ASTNode AST){
	someCopyStmtModified=false;
	this.AST=AST;
	setup();
    }

    public CopyPropagation(boolean verbose,ASTNode AST){
	super(verbose);
	someCopyStmtModified=false;
	this.AST=AST;
	setup();
    }




    private void setup(){
	//create the uD and dU chains
	useDefs = new ASTUsesAndDefs(AST);
	AST.apply(useDefs);
	
	//apply the reaching copies Structural flow Analysis
	reachingCopies = new ReachingCopies(AST);

	parentOf = new ASTParentNodeFinder();
	AST.apply(parentOf);
    }






    public void inASTStatementSequenceNode(ASTStatementSequenceNode node){
	List statements = node.getStatements();
	Iterator it = statements.iterator();
	
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    if(isCopyStmt(s)){
		handleCopyStmt(s,s);
	    }
	}
    }
    



    public boolean isCopyStmt(Stmt s){
	if(!(s instanceof DefinitionStmt)){
	    //only definition stmts can be copy stmts
	    return false;
	}

	// x = expr;
	//check if expr is a local in which case this is a copy
	Value leftOp = ((DefinitionStmt)s).getLeftOp();
	Value rightOp = ((DefinitionStmt)s).getRightOp();

	if(leftOp instanceof Local && rightOp instanceof Local){
	    //this is a copy statement
	    return true;
	}
	return false;
    }



    /*
     * Given a copy stmt (a=b) find all uses of local a (using dU chain)

     * if For ALL uses the ReachingCopies set contains copy stmt (a=b)
     * Remove Copy Stmt
     * Replace use of a with use of b
    */
    public void handleCopyStmt(Stmt copyStmt,Object nodeOrStmt){
	//System.out.println("COPY STMT FOUND-----------------------------------"+copyStmt);

	//get defined local
	Value leftVal = ((DefinitionStmt)copyStmt).getLeftOp();
	if(!(leftVal instanceof Local))
	    return;
	Local definedLocal = (Local)leftVal;

	//get all uses of this local from the dU chain
	Object temp = useDefs.getDUChain(copyStmt);

	ArrayList uses = new ArrayList();
	if(temp != null){
	    uses = (ArrayList)temp;
	}

	//the uses list contains all stmts / nodes which use the definedLocal

	//check if uses is non-empty
	if(uses.size()!=0){
	    //System.out.println("The defined local:"+definedLocal+" is used in the following");
	    //System.out.println("\n numof uses:"+uses.size()+uses+"\n\n");
	    
	    //continuing with copy propagation algorithm
	    
	    //create localPair for copy stmt in question
	    Value leftOp = ((DefinitionStmt)copyStmt).getLeftOp();
	    Value rightOp = ((DefinitionStmt)copyStmt).getRightOp();

	    if(!(leftOp instanceof Local && rightOp instanceof Local)){	    
		return;
	    }
	    
	    Local leftLocal = (Local)leftOp;
	    Local rightLocal = (Local)rightOp;

	    LocalPair localPair = new LocalPair(leftLocal,rightLocal);

	    //check for all uses
	    Iterator useIt = uses.iterator();
	    while(useIt.hasNext()){
		//check that the reaching copies of each use has the copy stmt
		//a use is either a statement or a node(condition, synch, switch , for etc)
		Object tempUse = useIt.next();
		
		DavaFlowSet reaching = reachingCopies.getReachingCopies(tempUse);
		
		if(!reaching.contains(localPair)){
		    //this copy stmt does not reach this use
		    return;
		}
	    }

	    //if we get here that means that the copy stmt reached each use
	    
	    //replace each use of a with b
	    useIt = uses.iterator();
	    while(useIt.hasNext()){
		Object tempUse = useIt.next();		
		replace(leftLocal,rightLocal,tempUse);
	    }

	    //remove copy stmt a=b
	    removeStmt(copyStmt);
	    



	    if(someCopyStmtModified){
		setup();
		someCopyStmtModified=false;
	    }
	}
	else{
	    //the copy stmt is usesless since the definedLocal is not being used anywhere after definition
	    //System.out.println("The defined local:"+definedLocal+" is not used anywhere");
	    removeStmt(copyStmt);
	}
    }



    public void removeStmt(Stmt stmt){
	Object tempParent = parentOf.getParentOf(stmt);
	if(tempParent == null){
	    //System.out.println("NO PARENT FOUND CANT DO ANYTHING");
	    return;
	}

	//parents are always ASTNodes, hence safe to cast
	ASTNode parent = (ASTNode)tempParent;

	//REMOVING STMT 
	if(!(parent instanceof ASTStatementSequenceNode)){
	    //parent of a statement should always be a ASTStatementSequenceNode
	    throw new RuntimeException("Found a stmt whose parent is not an ASTStatementSequenceNode");
	}
	ASTStatementSequenceNode parentNode = (ASTStatementSequenceNode)parent;

	ArrayList newSequence = new ArrayList();
	
	Iterator it = parentNode.getStatements().iterator();
	while (it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    if(s.toString().compareTo(stmt.toString())!=0){
		//this is not the stmt to be removed
		newSequence.add(as);
	    }
	}
	//System.out.println("STMT REMOVED---------------->"+stmt);
	parentNode.setStatements(newSequence);
	return;
    }







    public void replaceBoxes(Local from, Local to, List useBoxes){
	Iterator it = useBoxes.iterator();
	while(it.hasNext()){
	    ValueBox valBox =(ValueBox)it.next();
	    Value val = valBox.getValue();
	    if(val instanceof Local){
		Local local = (Local)val;
		if(local.getName().compareTo(from.getName())==0){
		    //replace the name with the one in "to"
		    valBox.setValue(to);
		}
	    }
	}
    }







    public List getUseList(ASTCondition cond){
	if(cond instanceof ASTAggregatedCondition){
	    ArrayList useList = new ArrayList();
	    useList.addAll(getUseList(((ASTAggregatedCondition)cond).getLeftOp()));
	    useList.addAll(getUseList(((ASTAggregatedCondition)cond).getLeftOp()));
	    return useList;
	}
	else if(cond instanceof ASTUnaryCondition){
	    //get uses from unary condition
	    Value val = ((ASTUnaryCondition)cond).getValue();
	    return val.getUseBoxes();
	}
	else if(cond instanceof ASTBinaryCondition){
	    //get uses from binaryCondition
	    Value val = ((ASTBinaryCondition)cond).getConditionExpr();
	    return val.getUseBoxes();
	}
	else{
	    throw new RuntimeException("Method getUseList in CopyPropagation encountered unknown condition type");
	}
    }










    public void replace(Local from, Local to, Object use){
	if(use instanceof Stmt){
	    Stmt s = (Stmt)use;
	    if(isCopyStmt(s)){
		someCopyStmtModified=true;
	    }
	    List useBoxes = s.getUseBoxes();
	    replaceBoxes(from,to,useBoxes);
	}
	else if (use instanceof ASTNode){
	    if (use instanceof ASTSwitchNode){
		ASTSwitchNode temp = (ASTSwitchNode)use;
		Value val = (Value)temp.get_Key();
		if(val instanceof Local){
		    if(((Local)val).getName().compareTo(from.getName())==0){
			//replace the name with the one in "to"
			temp.set_Key(to);
		    }
		}
		else{
		    List useBoxes = val.getUseBoxes();
		    replaceBoxes(from,to,useBoxes);
		}
	    }
	    else if (use instanceof ASTSynchronizedBlockNode){
		ASTSynchronizedBlockNode temp = (ASTSynchronizedBlockNode)use;
		Local local = temp.getLocal();
		if(local.getName().compareTo(from.getName())==0){
		    //replace the name with the one in "to"
		    temp.setLocal(to);
		}
	    }
	    else if(use instanceof ASTIfNode){
		ASTIfNode temp = (ASTIfNode)use;
		ASTCondition cond = temp.get_Condition();
		List useBoxes = getUseList(cond);
		replaceBoxes(from,to,useBoxes);
	    }
	    else if (use instanceof ASTIfElseNode){
		ASTIfElseNode temp = (ASTIfElseNode)use;
		ASTCondition cond = temp.get_Condition();
		List useBoxes = getUseList(cond);
		replaceBoxes(from,to,useBoxes);
	    }
	    else if (use instanceof ASTWhileNode){
		ASTWhileNode temp = (ASTWhileNode)use;
		ASTCondition cond = temp.get_Condition();
		List useBoxes = getUseList(cond);
		replaceBoxes(from,to,useBoxes);
	    }
	    else if (use instanceof ASTDoWhileNode){
		ASTDoWhileNode temp = (ASTDoWhileNode)use;
		ASTCondition cond = temp.get_Condition();
		List useBoxes = getUseList(cond);
		replaceBoxes(from,to,useBoxes);
	    }
	    else if (use instanceof ASTForLoopNode){
		ASTForLoopNode temp = (ASTForLoopNode)use;

		//init
		List init = temp.getInit();
		Iterator it = init.iterator();
		while(it.hasNext()){
		    AugmentedStmt as = (AugmentedStmt)it.next();
		    Stmt s = as.get_Stmt();
		    replace(from,to,s);
		}


		//update	
		List update = temp.getUpdate();
		it = update.iterator();
		while(it.hasNext()){
		    AugmentedStmt as = (AugmentedStmt)it.next();
		    Stmt s = as.get_Stmt();
		    replace(from,to,s);
		}


		//condition
		ASTCondition cond = temp.get_Condition();
		List useBoxes = getUseList(cond);
		replaceBoxes(from,to,useBoxes);
	    }
	    else{
		throw new RuntimeException("Encountered an unknown ASTNode in copyPropagation method replace");
	    }
	}
	else{
	    throw new RuntimeException("Encountered an unknown use in copyPropagation method replace");
	}

    }






    /*
     * Get all locals declared in the method
     * If the local is never defined (and hence never used) remove it
     * If the local is defined BUT never used then you may remove it IF AND ONLY IF
     *    The definition is either a copy stmt or an assignment of a constant
     */
    public void outASTMethodNode(ASTMethodNode node){
	//create the uD and dU chains
	//System.out.println("outASTMethod Node");
	useDefs = new ASTUsesAndDefs(AST);


	AST.apply(useDefs);


	//useDefs.print();


	ASTStatementSequenceNode stmtNode = node.getDeclarations();
	List sequence = stmtNode.getStatements();
	Iterator it = sequence.iterator();
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    if(! (s instanceof DVariableDeclarationStmt))
		continue;
	    DVariableDeclarationStmt varStmt = (DVariableDeclarationStmt)s;
	    ArrayList removeList = new ArrayList();

	    //System.out.println("Variable declaration stmt is :"+varStmt);
	    List declarations = varStmt.getDeclarations();
	    Iterator decIt = declarations.iterator();
	    while(decIt.hasNext()){
		Object var = decIt.next();
		//System.out.println("CHECKING:"+var);

		//var is either a local or a definition stmt
		if(var instanceof Local){
		    if(!isDefined((Local)var)){
			//var is never defined and hence is certainly not used anywhere
			removeList.add((Local)var);
		    }
		    else{
			//System.out.println(var+" is defined");

			//if a var is defined but not used then in some conditions we can remove it
			
			//get all defs
			List defs = getDefs((Local)var);
			//System.out.println("DEFS reaching are:"+defs);

			//check that each def is never used
			Iterator defIt = defs.iterator();
			boolean removeVar=true;
			while(defIt.hasNext()){
			    boolean defRemoved=false;

			    //DefinitionStmt ds = (DefinitionStmt)defIt.next();
			    Object tempds = defIt.next();
			    List uses = useDefs.getDUChain(tempds);
			    DefinitionStmt ds = (DefinitionStmt)tempds;
			    if(uses.size()==0){
				/*
				  there is no use of this def, we can remove it if it is
				  copy stmt or a constant assignment
				*/
				//System.out.println("the variable:"+var+" and def:"+ds+" is never used");
				if(ds.getRightOp() instanceof Local){
				    //removeStmt(ds);
				    defRemoved=true;
				    //System.out.println("Chose to remove stmt:"+ds);
				}
				else if(ds.getRightOp() instanceof Constant){
				    //removeStmt(ds);
				    //System.out.println("Chose to remove stmt:"+ds);
				    defRemoved=true;
				}
			    }
			    if(!defRemoved)
				removeVar=false;
			}
			if(removeVar){
			    removeList.add((Local)var);			    
			}

		    }
		}
		else if (var instanceof DefinitionStmt){
		    /*
		      Theoretically we should deal with this occurance also
		      However Since there are no definitions added into the DVariableDeclationStmt
		      there is no need to do this at this time
		    */
		    
		}
		else{
		    //System.out.println("here???");
		}
		
	    }


	    //go through the removeList and remove all locals
	    Iterator remIt = removeList.iterator();
	    while(remIt.hasNext()){
		Local removeLocal = (Local)remIt.next();
		varStmt.removeLocal(removeLocal);
	    }
	}

	//since we might have removed locals from VariableDeclarationStmts
	//check to see if some variable DeclarationStmt is un needed

	
	List newSequence = new ArrayList();
	sequence = stmtNode.getStatements();
	it = sequence.iterator();
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    if(! (s instanceof DVariableDeclarationStmt))
		continue;
	    DVariableDeclarationStmt varStmt = (DVariableDeclarationStmt)s;
	    List declarations = varStmt.getDeclarations();
	    if(declarations.size()!=0){
		newSequence.add(as);
	    }
	}
	stmtNode.setStatements(newSequence);


	
    }



    /*
     * This method looks up all defs and returns those of this local
     */
    public List getDefs(Local var){
	List toReturn = new ArrayList();
	
	HashMap dU = useDefs.getDUHashMap();
	Iterator it = dU.keySet().iterator();
	while(it.hasNext()){
	    DefinitionStmt s = (DefinitionStmt)it.next();
	    Value left = s.getLeftOp();
	    if(left instanceof Local){
		if(((Local)left).getName().compareTo(var.getName())==0)
		    toReturn.add(s);
	    }
	}
	return toReturn;
    }

    /*
     * Go through all definition stmts in the system and check if the local
     * in question has a definition
     */
    public boolean isDefined(Local var){
	HashMap dU = useDefs.getDUHashMap();
	Iterator it = dU.keySet().iterator();
	while(it.hasNext()){
	    DefinitionStmt s = (DefinitionStmt)it.next();
	    Value left = s.getLeftOp();
	    if(left instanceof Local){
		if(((Local)left).getName().compareTo(var.getName())==0)
		    return true;
	    }
	}
	return false;
    }
}