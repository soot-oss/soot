/* Soot - a J*va Optimization Framework
 * Copyright (C) 2006 Nomair A. Naeem
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

package soot.dava.toolkits.base.AST.transformations;

import java.util.Iterator;
import java.util.List;

import soot.G;
import soot.dava.DecompilationException;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTLabeledNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.AST.ASTUnconditionalLoopNode;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DAbruptStmt;
import soot.dava.internal.javaRep.DVariableDeclarationStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.dava.toolkits.base.AST.traversals.ASTParentNodeFinder;
import soot.dava.toolkits.base.AST.traversals.LabelToNodeMapper;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;

/*
 * It has been seen that a lot of times there are break statements targeting
 * a label absolutely unnecessarily (with continues this is rare but to be complete
 * we will handle them too)
 * An example:
 *    label1:
 *    if(cond1){
 *      BodyA
 *      break label1
 *    }
 *    
 *    As in the above code the break stmt is absolutely unnessary as the code
 *    will itself flow to the required position.
 *    
 *    However, remember that breaks and continues are also used to 
 *    exit or repeat a loop in which case we should keep the break and continue!!!
 *    
 *    TODO Also if we do decide to remove an abrupt stmt make sure that the
 *    stmt seq node has not become empty. If it has remove the node and see
 *    if the construct carrying this node has become empty and so on..... 
 */
public class UselessAbruptStmtRemover extends DepthFirstAdapter {
	public static boolean DEBUG=false;
	
	ASTParentNodeFinder finder;
	ASTMethodNode methodNode;
	LabelToNodeMapper mapper;
	
	public UselessAbruptStmtRemover(){
		finder=null;
	}

	public UselessAbruptStmtRemover(boolean verbose){
		super(verbose);
		finder=null;
	}

	public void inASTMethodNode (ASTMethodNode node){
		methodNode=node;
		mapper = new LabelToNodeMapper();
		methodNode.apply(mapper);
	}
	
	
	public void caseASTStatementSequenceNode(ASTStatementSequenceNode node) {
		Iterator it = node.getStatements().iterator();
		AugmentedStmt remove = null;
		ASTLabeledNode target=null;
		while (it.hasNext()) {
			AugmentedStmt as = (AugmentedStmt) it.next();
			Stmt s = as.get_Stmt();

			//we only care about break and continue stmts
			if(! (s instanceof DAbruptStmt)){
				continue;
			}
			
			DAbruptStmt abrupt = (DAbruptStmt)s;
			String label = abrupt.getLabel().toString();
			if(label == null){
				//could at some time implement a version of the same
				//analysis with implicit abrupt flow but not needed currently
				continue;
			}
			
			if(it.hasNext()){
				//there is an abrupt stmt and this stmt seq node has something
				//afterwards...that is for sure dead code
				throw new DecompilationException("Dead code detected. Report to developer");
			}
			
			//get the target node
			Object temp = mapper.getTarget(label);
			if(temp == null){
				continue;
				//throw new DecompilationException("Could not find target for abrupt stmt"+abrupt.toString());
			}
		
			target = (ASTLabeledNode)temp;
			
			//will need to find parents of ancestors see if we need to initialize the finder
			if(finder==null){
				finder = new ASTParentNodeFinder();
				methodNode.apply(finder);
			}

			if(DEBUG)
				System.out.println("Starting useless check for abrupt stmt: "+abrupt);
			
			//start condition is that ancestor is the stmt seq node
			ASTNode ancestor = node; 	
			
			while(ancestor != target){
				Object tempParent = finder.getParentOf(ancestor);
				if(tempParent == null)
					throw new DecompilationException("Parent found was null!!. Report to Developer");
				
				ASTNode ancestorsParent = (ASTNode)tempParent; 
				if(DEBUG)
					System.out.println("\tCurrent ancestorsParent has type"+ancestorsParent.getClass());
				
				//ancestor should be last child of ancestorsParent
				if(!checkChildLastInParent(ancestor,ancestorsParent)){
					if(DEBUG)
						System.out.println("\t\tCurrent ancestorParent has more children after this ancestor");
					
					//return from the method since this is the last stmt and we cant do anything
					return;
				}
				
				//ancestorsParent should not be a loop of any kind OR A SWITCH
				if(ancestorsParent instanceof ASTWhileNode || ancestorsParent instanceof ASTDoWhileNode || 
						ancestorsParent instanceof ASTUnconditionalLoopNode || ancestorsParent instanceof ASTForLoopNode
						|| ancestorsParent instanceof ASTSwitchNode){
					if(DEBUG)
						System.out.println("\t\tAncestorsParent is a loop shouldnt remove abrupt stmt");
					return;
				}
				ancestor = ancestorsParent;
			}

			if(DEBUG)
				System.out.println("\tGot to target without returning means we can remove stmt");
			
			remove = as;			
		}//end of while going through the statement sequence
		
		if(remove != null){
			List stmts = node.getStatements();
			stmts.remove(remove);
			if(DEBUG)
				System.out.println("\tRemoved abrupt stmt");

			if(target!= null){
				if(DEBUG)
					System.out.println("Invoking findAndKill on the target");
				UselessLabelFinder.v().findAndKill(target);
			}
			//TODO what if we just emptied a stmt seq block??
			//not doing this for the moment
			
			
			//set modified flag make finder null
			G.v().ASTTransformations_modified=true;
			finder=null;
		}
	}

	public boolean checkChildLastInParent(ASTNode child, ASTNode parent){
		List subBodies = parent.get_SubBodies();
		Iterator it = subBodies.iterator();
		
		while(it.hasNext()){
			List subBody = null;
		    if (parent instanceof ASTTryNode)
		    	subBody = (List) ((ASTTryNode.container) it.next()).o;
		    else
		    	subBody = (List)it.next();

		    if(subBody.contains(child)){
		    	if(subBody.indexOf(child) != subBody.size()-1)
		    		return false;
		    	else
		    		return true;
		    }
		}
			    
		return false;
	}
}
