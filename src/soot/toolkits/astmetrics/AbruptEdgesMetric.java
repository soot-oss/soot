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

import polyglot.ast.Branch;
import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

/*
 * Should take care of the following metrics:
 * 
 * Break Statements

       1. of implicit breaks (breaking inner most loop)   DONE
       2. of explicit breaks (breaking an outer loop)        NOTQUITE DONE
       3. of explicit breaks (breaking other constructs)      DONE (any explicit break)

 * Continue Statements

       1. of implicit continues (breaking inner most loop)   DONE
       2. of explicit continues (breaking outer loops)       DONE
  */
public class AbruptEdgesMetric extends ASTMetric {

	private int iBreaks, eBreaks;
	private int iContinues, eContinues;
	
	public AbruptEdgesMetric(polyglot.ast.Node astNode){
		super(astNode);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see soot.toolkits.astmetrics.ASTMetric#reset()
	 * Implementation of the abstract method which is 
	 * invoked by parent constructor and whenever the classDecl in the polyglot changes 
	 */
	public void reset(){
		iBreaks=eBreaks=iContinues=eContinues=0;
	}

	/*
	 * Implementation of the abstract method
	 * 
	 * Should add the metrics to the data object sent
	 */
	public void addMetrics(ClassData data){

		data.addMetric(new MetricData("Total-breaks",new Integer(iBreaks+eBreaks)));
		data.addMetric(new MetricData("I-breaks",new Integer(iBreaks)));
		data.addMetric(new MetricData("E-breaks",new Integer(eBreaks)));

		data.addMetric(new MetricData("Total-continues",new Integer(iContinues+eContinues)));
		data.addMetric(new MetricData("I-continues",new Integer(iContinues)));
		data.addMetric(new MetricData("E-continues",new Integer(eContinues)));
		
		data.addMetric(new MetricData("Total-Abrupt",new Integer(iBreaks+eBreaks+iContinues+eContinues)));
	}
	
	
	/*
	 * A branch in polyglot is either a break or continue
	 */
	public NodeVisitor enter(Node parent, Node n){
		if(n instanceof Branch){
			Branch branch = (Branch)n;
			if(branch.kind().equals(Branch.BREAK)){
				if(branch.label() != null) 
					eBreaks++;
				else 
					iBreaks++;
			}
			else if(branch.kind().equals(Branch.CONTINUE)){
				if(branch.label() != null)
					eContinues++;
				else
					iContinues++;
			}
			else{
				System.out.println("\t Error:'"+branch.toString()+"'");
			}
		}
		return enter(n);
	}
}
