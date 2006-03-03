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
 * Class does a traversal of the AST and checks all definitions of fields.
 * If any of the fields which are assigned is a final field the boolean
 * finalFieldDefined is set to true
 */
package soot.dava;

import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;

public class StaticDefinitionFinder extends DepthFirstAdapter{

    SootMethod method;
    boolean finalFieldDefined;

    public StaticDefinitionFinder(SootMethod method){
	this.method = method;
	finalFieldDefined=false;
    }

    public StaticDefinitionFinder(boolean verbose,SootMethod method){
	super(verbose);
	this.method= method;
	finalFieldDefined=false;
    }

    public void inDefinitionStmt(DefinitionStmt s){
	Value leftOp = s.getLeftOp();
	if(leftOp instanceof FieldRef){
	    //System.out.println("leftOp is a fieldRef:"+s);
	    SootField field = ((FieldRef)leftOp).getField();
	    //check if this is a final field
	    if(field.isFinal()){
		//System.out.println("the field is a final variable");
		finalFieldDefined=true;
	    }
	}
	
    }
		
    public boolean anyFinalFieldDefined(){
	return finalFieldDefined;
    }

}