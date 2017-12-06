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

/*
 * Maintained by Nomair A. Naeem
 */

/*
 * Change log: * November 23rd, 2005 Class Created
 */

package soot.dava.toolkits.base.AST.transformations;

import soot.*;

import java.util.*;

import soot.jimple.*;
import soot.util.Chain;
//import soot.dava.internal.javaRep.*;
import soot.dava.DavaBody;
import soot.dava.DecompilationException;
import soot.dava.internal.AST.*;
import soot.dava.internal.asg.*;
import soot.dava.toolkits.base.AST.analysis.*;
//import soot.dava.toolkits.base.AST.structuredAnalysis.*;
import soot.dava.toolkits.base.AST.traversals.*;

/**
 * The class is aimed to target cleaning up of unused local variables.
 * Should be invoked after executing CopyPropagation
 *
 * Another thing that this class does which perhaps should have been implemented separately
 * is to check whether there is an assignment which never gets used later on.
 * If there exists such an assignment this assignment is removed first and then all the useless
 * locals checks should be reapplied (until a fixed point)
 */

public class LocalVariableCleaner extends DepthFirstAdapter {
	public final boolean DEBUG = false;
	
	
	ASTNode AST;

	ASTUsesAndDefs useDefs;

	ASTParentNodeFinder parentOf;

	public LocalVariableCleaner(ASTNode AST) {
		super();
		this.AST = AST;
		parentOf = new ASTParentNodeFinder();
		AST.apply(parentOf);
	}

	public LocalVariableCleaner(boolean verbose, ASTNode AST) {
		super(verbose);
		this.AST = AST;
		parentOf = new ASTParentNodeFinder();
		AST.apply(parentOf);
	}

	/*
	 * Get all locals declared in the method
	 * If the local is never defined (and hence never used) remove it
	 * If the local is defined BUT never used then you may remove it IF AND ONLY IF
	 *    The definition is either a copy stmt or an assignment of a constant (i.e. no side effects)
	 */
	public void outASTMethodNode(ASTMethodNode node) {
		boolean redo = false;

		useDefs = new ASTUsesAndDefs(AST); //create the uD and dU chains
		AST.apply(useDefs);

		//get all local variables declared in this method
		Iterator decIt = node.getDeclaredLocals().iterator();

		ArrayList<Local> removeList = new ArrayList<Local>();
		while (decIt.hasNext()) {
			//going through each local declared

			Local var = (Local) decIt.next();

			List<DefinitionStmt> defs = getDefs(var);

			//if defs is 0 it means var never got defined
			if (defs.size() == 0) {
				//var is never defined and hence is certainly not used anywhere
				removeList.add(var);
			} else {
				//if a var is defined but not used then in some conditions we can remove it

				//check that each def is removable
				Iterator<DefinitionStmt> defIt = defs.iterator();

				while (defIt.hasNext()) {
					DefinitionStmt ds = defIt.next();

					if (canRemoveDef(ds)) {
						//if removeStmt is successful since something change we need to redo 
						//everything hoping something else might be removed....
						//in this case method returns true
						redo = removeStmt(ds);
					}
				}//while going through defs

			}//end else defs was not zero
		}//going through each stmt

		//go through the removeList and remove all locals
		Iterator<Local> remIt = removeList.iterator();
		while (remIt.hasNext()) {
			Local removeLocal = remIt.next();
			node.removeDeclaredLocal(removeLocal);
			
			/*
			 *  Nomair A. Naeem 7th Feb 2005
			 * 	these have to be removed from the legacy lists in DavaBody also
			 *
			 */
			//retrieve DavaBody
			if(AST instanceof ASTMethodNode){
				//this should always be true but whatever
				DavaBody body = ((ASTMethodNode)AST).getDavaBody();
				if(DEBUG){
					System.out.println("body information");
					System.out.println("Control local is: "+body.get_ControlLocal());
					System.out.println("his locals are: "+body.get_ThisLocals());
					System.out.println("Param Map is: "+body.get_ParamMap());
					System.out.println("Locals are:"+body.getLocals());
				}			
				Collection<Local> localChain = body.getLocals();
				if(removeLocal != null && localChain != null)
					localChain.remove(removeLocal);
			}
			else
				throw new DecompilationException("found AST which is not a methodNode");
			
			
			
			
			if(DEBUG)
				System.out.println("Removed"+removeLocal);
			redo = true;
		}

		if (redo) {
			//redo the whole function
			outASTMethodNode(node);
		}
	}

	/*
	 * A def can be removed if and only if:
	 *  1, there are no uses of this definition
	 *  2, the right hand size is either a local or a constant i.e. no need to worry about side effects
	 */
	public boolean canRemoveDef(DefinitionStmt ds) {
		List uses = useDefs.getDUChain(ds);

		if (uses.size() != 0)
			return false;

		//there is no use of this def, we can remove it if it is copy stmt or a constant assignment
		if (ds.getRightOp() instanceof Local
				|| ds.getRightOp() instanceof Constant)
			return true;

		return false;
	}

	/*
	 * This method looks up all defs and returns those of this local
	 */
	public List<DefinitionStmt> getDefs(Local var) {
		List<DefinitionStmt> toReturn = new ArrayList<DefinitionStmt>();

		HashMap<Object, List> dU = useDefs.getDUHashMap();
		Iterator<Object> it = dU.keySet().iterator();
		while (it.hasNext()) {
			DefinitionStmt s = (DefinitionStmt) it.next();
			Value left = s.getLeftOp();
			if (left instanceof Local) {
				if (((Local) left).getName().compareTo(var.getName()) == 0)
					toReturn.add(s);
			}
		}
		return toReturn;
	}

	public boolean removeStmt(Stmt stmt) {
		Object tempParent = parentOf.getParentOf(stmt);
		if (tempParent == null) {
			//System.out.println("NO PARENT FOUND CANT DO ANYTHING");
			return false;
		}

		//parents are always ASTNodes, hence safe to cast
		ASTNode parent = (ASTNode) tempParent;

		//REMOVING STMT 
		if (!(parent instanceof ASTStatementSequenceNode)) {
			//parent of a statement should always be a ASTStatementSequenceNode
			return false;
		}
		ASTStatementSequenceNode parentNode = (ASTStatementSequenceNode) parent;

		ArrayList<AugmentedStmt> newSequence = new ArrayList<AugmentedStmt>();
		int size = parentNode.getStatements().size();
		for (AugmentedStmt as : parentNode.getStatements()) {
			Stmt s = as.get_Stmt();
			if (s.toString().compareTo(stmt.toString()) != 0) {
				//this is not the stmt to be removed
				newSequence.add(as);
			}
		}
		//System.out.println("STMT REMOVED---------------->"+stmt);
		parentNode.setStatements(newSequence);
		if (newSequence.size() < size)
			return true; //size of new node is smaller than orignal size

		return false;//didnt actually delete anything for some weird reason...shouldnt happen
	}

}
