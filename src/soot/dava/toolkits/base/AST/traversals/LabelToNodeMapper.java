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

package soot.dava.toolkits.base.AST.traversals;

import java.util.HashMap;

import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTLabeledBlockNode;
import soot.dava.internal.AST.ASTLabeledNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTSynchronizedBlockNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.AST.ASTUnconditionalLoopNode;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;

/*
 * For each labeled node make a mapping of the label (a string)
 * to the node.
 * Eg. if label1:
 *        while(cond){
 *            BodyA
 *        }
 *        
 *        Then the mapping is (label1,ASTWhileNode Reference)
 *        
 */
public class LabelToNodeMapper extends DepthFirstAdapter {
	private HashMap labelsToNode;
	
	public LabelToNodeMapper(){
		labelsToNode = new HashMap();
	}
	
	public LabelToNodeMapper(boolean verbose){
		super(verbose);
		labelsToNode = new HashMap();
	}
	
	/*
	 * If the returned object is non null it is safe to cast it to ASTLabeledNode
	 */
	public Object getTarget(String label){
		return labelsToNode.get(label);
	}
	
	private void addToMap(ASTLabeledNode node){
		String str = node.get_Label().toString();
		if(str != null)
			labelsToNode.put(str,node);		
	}
	
	public void inASTLabeledBlockNode(ASTLabeledBlockNode node){
		addToMap(node);
	}
	
	public void inASTTryNode(ASTTryNode node){
		addToMap(node);
	}
	
	public void inASTUnconditionalLoopNode (ASTUnconditionalLoopNode node){
		addToMap(node);
	}
	
	public void inASTDoWhileNode (ASTDoWhileNode node){
		addToMap(node);
	}
	
	public void inASTForLoopNode(ASTForLoopNode node){
		addToMap(node);
	}
	
	public void inASTIfElseNode(ASTIfElseNode node){
		addToMap(node);
	}
	
	public void inASTIfNode(ASTIfNode node){
		addToMap(node);
	}
	
	public void inASTWhileNode(ASTWhileNode node){
		addToMap(node);
	}
	
	public void inASTSwitchNode(ASTSwitchNode node){
		addToMap(node);
	}
	
	public void inASTSynchronizedBlockNode(ASTSynchronizedBlockNode node){
		addToMap(node);
	}
}
