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


package soot.toolkits.astmetrics;

import polyglot.ast.Block;
import polyglot.ast.Do;
import polyglot.ast.For;
import polyglot.ast.If;
import polyglot.ast.Labeled;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.While;
import polyglot.visit.NodeVisitor;

/*
 * Calculate the number of different Java Constructs present in the
 * code
 */
public class ConstructNumbersMetric extends ASTMetric {

	private int numIf, numIfElse;
	
	private int numLabeledBlocks;
	
	private int doLoop, forLoop, whileLoop, whileTrue;
	
	public ConstructNumbersMetric(Node node){
		super(node);
	}
	
	
	public void reset() {
		numIf = numIfElse = 0;
		numLabeledBlocks=0;
		doLoop=forLoop=whileLoop=whileTrue=0;
	}

	public void addMetrics(ClassData data) {
		// TODO Auto-generated method stub
		//conditionals
		data.addMetric(new MetricData("If",new Integer(numIf)));
		data.addMetric(new MetricData("IfElse",new Integer(numIfElse)));
		data.addMetric(new MetricData("Total-Conditionals",new Integer(numIf+numIfElse)));
		
		//labels
		data.addMetric(new MetricData("LabelBlock",new Integer(numLabeledBlocks)));
		
		//loops
		data.addMetric(new MetricData("Do",new Integer(doLoop)));
		data.addMetric(new MetricData("For",new Integer(forLoop)));
		data.addMetric(new MetricData("While",new Integer(whileLoop)));
		data.addMetric(new MetricData("UnConditional",new Integer(whileTrue)));
		data.addMetric(new MetricData("Total Loops",new Integer(whileTrue+whileLoop+forLoop+doLoop)));
	}
	
	
	public NodeVisitor enter(Node parent, Node n){

		/*
		 * Num if and ifelse
		 */
		if(n instanceof If){
			//check if there is the "optional" else branch present
			If ifNode = (If)n;
			Stmt temp = ifNode.alternative();
			if(temp == null){
				//else branch is empty
				//System.out.println("This was an if stmt"+n);
				numIf++;
			}
			else{
				//else branch has something
				//System.out.println("This was an ifElse stmt"+n);
				numIfElse++;
			}
		}
		
		/*
		 * Num Labeled Blocks
		 */
		if (n instanceof Labeled){
				Stmt s = ((Labeled)n).statement();
				//System.out.println("labeled"+((Labeled)n).label());
				if(s instanceof Block){
					//System.out.println("labeled block with label"+((Labeled)n).label());
					numLabeledBlocks++;
				}
		}
		
		/*
		 * Do
		 */	
		if(n instanceof Do){
			//System.out.println((Do)n);
			doLoop++;
		}
		/*
		 * For
		 */	
		if(n instanceof For){
			//System.out.println((For)n);
			forLoop++;
		}

		/*
		 * While and While True loop
		 */	
		if(n instanceof While){
			//System.out.println((While)n);
			if(((While)n).condIsConstantTrue())
				whileTrue++;
			else
				whileLoop++;
		}


		
		return enter(n);
	}

}
