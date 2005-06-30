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


import java.util.*;
import soot.jimple.*;
import soot.dava.internal.AST.*;
import soot.dava.toolkits.base.AST.analysis.*;


/*
 * This traversal class is responsible to gather information
 * regarding the different nodes and statements in the AST.
 * The class produces a HashMap between the node/statement given as
 * key and the parent of this construct (value)
 */

public class ASTParentNodeFinder extends DepthFirstAdapter{

    HashMap parentOf;
    Stack parentStack;

    public ASTParentNodeFinder(){
	parentOf = new HashMap();
	parentStack = new Stack();
    }

    public ASTParentNodeFinder(boolean verbose){
	super(verbose);
	parentOf = new HashMap();
	parentStack = new Stack();
    }

    public void inASTMethodNode(ASTMethodNode node){
	parentOf.put(node,null);
	parentStack.push(node);
    }

    public void outASTMethodNode(ASTMethodNode node){
	parentStack.pop();
    }


    public void inASTSynchronizedBlockNode(ASTSynchronizedBlockNode node){
	parentOf.put(node,parentStack.peek());
	parentStack.push(node);
    }
    public void outASTSynchronizedBlockNode(ASTSynchronizedBlockNode node){
	parentStack.pop();
    }


    public void inASTLabeledBlockNode (ASTLabeledBlockNode node){
	parentOf.put(node,parentStack.peek());
	parentStack.push(node);
    }
    public void outASTLabeledBlockNode (ASTLabeledBlockNode node){
	parentStack.pop();
    }



    public void inASTUnconditionalLoopNode (ASTUnconditionalLoopNode node){
	parentOf.put(node,parentStack.peek());
	parentStack.push(node);
    }
    public void outASTUnconditionalLoopNode (ASTUnconditionalLoopNode node){
	parentStack.pop();
    }



    public void inASTSwitchNode(ASTSwitchNode node){
	parentOf.put(node,parentStack.peek());
	parentStack.push(node);
    }
    public void outASTSwitchNode(ASTSwitchNode node){
	parentStack.pop();
    }




    public void inASTIfNode(ASTIfNode node){
	parentOf.put(node,parentStack.peek());
	parentStack.push(node);
    }
    public void outASTIfNode(ASTIfNode node){
	parentStack.pop();
    }




    public void inASTIfElseNode(ASTIfElseNode node){
	parentOf.put(node,parentStack.peek());
	parentStack.push(node);
    }
    public void outASTIfElseNode(ASTIfElseNode node){
	parentStack.pop();
    }




    public void inASTWhileNode(ASTWhileNode node){
	parentOf.put(node,parentStack.peek());
	parentStack.push(node);
    }
    public void outASTWhileNode(ASTWhileNode node){
	parentStack.pop();
    }







    public void inASTForLoopNode(ASTForLoopNode node){
	parentOf.put(node,parentStack.peek());
	parentStack.push(node);
    }
    public void outASTForLoopNode(ASTForLoopNode node){
	parentStack.pop();
    }




    public void inASTDoWhileNode(ASTDoWhileNode node){
	parentOf.put(node,parentStack.peek());
	parentStack.push(node);
    }
    public void outASTDoWhileNode(ASTDoWhileNode node){
	parentStack.pop();
    }







    public void inASTTryNode(ASTTryNode node){
	parentOf.put(node,parentStack.peek());
	parentStack.push(node);
    }
    public void outASTTryNode(ASTTryNode node){
	parentStack.pop();
    }






    public void inASTStatementSequenceNode(ASTStatementSequenceNode node){
	parentOf.put(node,parentStack.peek());
	parentStack.push(node);
    }
    public void outASTStatementSequenceNode(ASTStatementSequenceNode node){
	parentStack.pop();
    }




    public void inDefinitionStmt(DefinitionStmt s){
	parentOf.put(s,parentStack.peek());
    }

    public void inReturnStmt(ReturnStmt s){
	parentOf.put(s,parentStack.peek());
    }



    public void inInvokeStmt(InvokeStmt s){
	parentOf.put(s,parentStack.peek());
    }




    public void inThrowStmt(ThrowStmt s){
	parentOf.put(s,parentStack.peek());
    }


    public void inStmt(Stmt s){
	parentOf.put(s,parentStack.peek());
    }


    /*
     * This is the method which should be invoked by classes needing parent information.
     * When the method is invoked with a statement or node as input it returns the parent
     * of that object. The parent can safely be casted to ASTNode as long as the parent
     * returned is non null
     */
    public Object getParentOf(Object statementOrNode){
	return parentOf.get(statementOrNode);
    }
}