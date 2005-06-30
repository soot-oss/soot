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

package soot.dava.toolkits.base.AST.transformations;

import java.io.*;
import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.javaRep.*;
import soot.dava.internal.AST.*;
import soot.dava.toolkits.base.AST.analysis.*;

public class ConstructGathering extends DepthFirstAdapter{
    int labeledBlocks=0;
    int labeledConstructs=0;
    int breaksContinues=0;
    int ifs=0;
    int ifelse=0;
    int currentDepth=0;
    int maxDepth=0;


    public ConstructGathering(){
    }

    public ConstructGathering(boolean verbose){
	super(verbose);
    }


    public void normalRetrieving(ASTNode node){
	currentDepth++;
	if(currentDepth> maxDepth)
	    maxDepth=currentDepth;

	//from the Node get the subBodes
	Iterator sbit = node.get_SubBodies().iterator();
	while (sbit.hasNext()) {
	    Object subBody = sbit.next();
	    Iterator it = ((List) subBody).iterator();

	    //go over the ASTNodes in this subBody and apply
	    while (it.hasNext()){
		ASTNode temp = (ASTNode) it.next();

		temp.apply(this);
	    }
	}//end of going over subBodies
	
	currentDepth--;
    }



    public void write(String temp){
	try{
	    soot.dava.Dava.w.write(temp);
	    soot.dava.Dava.w.flush();
	}catch(Exception e){
	    throw new RuntimeException("exception while writing");
	}
    }

    public void outASTMethodNode(ASTMethodNode node){
	write("labeledBlocks **** labels **** BreaksContinues *** If *** ifelse *** maxdepth\n");
	
	write(labeledBlocks+"\t\t\t"+labeledConstructs+"\t\t"+breaksContinues);
	write("\t  "+ifs+"\t  "+ifelse+"\t\t"+maxDepth+"\n");
    }

    public void inASTLabeledBlockNode(ASTLabeledBlockNode node){
	labeledBlocks++;
    }




    public void checkLabel(ASTLabeledNode node){
	if(node.get_Label().toString()!=null){
	    labeledConstructs++;
	}
    }



    
    public void inASTSynchronizedBlockNode(ASTSynchronizedBlockNode node){
	checkLabel(node);
    }

    public void inASTUnconditionalLoopNode (ASTUnconditionalLoopNode node){
	checkLabel(node);
    }

    public void inASTSwitchNode(ASTSwitchNode node){
	checkLabel(node);
    }

    public void inASTIfNode(ASTIfNode node){
	checkLabel(node);
	ifs++;
    }

    public void inASTIfElseNode(ASTIfElseNode node){
	checkLabel(node);
	ifelse++;
    }

    public void inASTWhileNode(ASTWhileNode node){
	checkLabel(node);
    }

    public void inASTForLoopNode(ASTForLoopNode node){
	checkLabel(node);
    }

    public void inASTDoWhileNode(ASTDoWhileNode node){
	checkLabel(node);
    }

    public void inASTTryNode(ASTTryNode node){
	checkLabel(node);
	currentDepth++;
	if(currentDepth> maxDepth)
	    maxDepth=currentDepth;
    }


    public void outASTTryNode(ASTTryNode node){
	currentDepth--;
    }

    public void inStmt(Stmt s){
	if(s instanceof DAbruptStmt)
	    breaksContinues++;
    }
    


    

}