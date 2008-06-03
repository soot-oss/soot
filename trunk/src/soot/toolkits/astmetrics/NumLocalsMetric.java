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

import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

public class NumLocalsMetric extends ASTMetric {

	int numLocals; //store the current number of locals for this CLASS
	
	public NumLocalsMetric(polyglot.ast.Node node){
		super(node);
	}
	
	/*
	 * Will be invoked by the super as well as whenever a new class is entered
	 * 
	 */
	public void reset() {
		numLocals = 0;
	}

	/*
	 * Will be invoked whenever we are leaving a subtree which was a classDecl
	 */
	public void addMetrics(ClassData data) {
		data.addMetric(new MetricData("Number-Locals",new Integer(numLocals)));
	}
	
	public NodeVisitor enter(Node parent, Node n){
		if(n instanceof LocalDecl){
			//System.out.println("Local declared is"+ ((LocalDecl)n).name()  );
			numLocals++;
		}
		return enter(n);
	}

}
