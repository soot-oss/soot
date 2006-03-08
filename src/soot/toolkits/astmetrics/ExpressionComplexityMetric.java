/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

import polyglot.ast.Expr;
import polyglot.ast.If;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.Unary;
import polyglot.visit.NodeVisitor;

/**
 * @author Michael Batchelder 
 * 
 * Created on 7-Mar-2006 
 */
public class ExpressionComplexityMetric extends ASTMetric {

  	int currentExprDepth;
	int exprDepthSum;
	int exprCount;

	public ExpressionComplexityMetric(polyglot.ast.Node node) {
	  super(node);
	}
	
	public void reset() {
	  currentExprDepth = 0;
	  exprDepthSum = 0;
	  exprCount = 0;
	}

	public void addMetrics(ClassData data) {
	  double avg = 0;
	  double a = (double)exprDepthSum;
	  double b = (double)exprCount;
	  
	  if (b > 0)
	    avg = a / b;
	  
	  data.addMetric(new MetricData("Expr-Complexity",new Double(avg)));
	}

	public NodeVisitor enter(Node parent, Node n){
	  if(n instanceof Expr){
	    exprCount++;
	    exprDepthSum+=currentExprDepth;
	    
	    currentExprDepth++;
	  }
	  
	  return enter(n);
	}
	
	public Node leave(Node old, Node n, NodeVisitor v){
	  if(n instanceof Expr){
	    currentExprDepth--;
	  }
	  
	  return super.leave(old,n,v);
	}
}

