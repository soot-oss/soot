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

package soot.dava.toolkits.base.AST.interProcedural;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.dava.DavaBody;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.toolkits.base.AST.transformations.EliminateConditions;
import soot.dava.toolkits.base.AST.transformations.SimplifyConditions;
import soot.dava.toolkits.base.AST.transformations.CPApplication;
import soot.dava.toolkits.base.AST.transformations.LocalVariableCleaner;
import soot.dava.toolkits.base.AST.transformations.SimplifyConditions;
import soot.dava.toolkits.base.AST.transformations.SimplifyExpressions;
import soot.dava.toolkits.base.AST.transformations.UselessLabelFinder;
import soot.util.Chain;

public class InterProceduralAnalyses {
	public static boolean DEBUG=false;
	/*
	 * Method is invoked by postProcessDava in PackManager
	 * if the transformations flag is true
	 * 
	 * All interproceduralAnalyses should be applied in here
	 */
	public static void applyInterProceduralAnalyses(){
		Chain classes = Scene.v().getApplicationClasses();
		
		//System.out.println("\n\nInvoking redundantFielduseEliminator");
		ConstantFieldValueFinder finder = new ConstantFieldValueFinder(classes);
		
		HashMap constantValueFields = finder.getFieldsWithConstantValues();
		
		//finder.printConstantValueFields();
		
		
		
		Iterator it = classes.iterator();
		while(it.hasNext()){
			//go though all the methods
			SootClass s = (SootClass)it.next();
			Iterator methodIt = s.methodIterator();
			while (methodIt.hasNext()) {
				SootMethod m = (SootMethod) methodIt.next();
				DavaBody body = (DavaBody)m.getActiveBody();
				ASTNode AST = (ASTNode) body.getUnits().getFirst();

				if(! (AST instanceof ASTMethodNode))
					continue;
				
				Map options = PhaseOptions.v().getPhaseOptions("db.deobfuscate");
		        boolean deobfuscate = PhaseOptions.getBoolean(options, "enabled");
		        //System.out.println("force is "+force);
		        if(deobfuscate){
		        	if(DEBUG)
		        		System.out.println("\nSTART CP Class:"+s.getName()+ " Method: "+m.getName());
		        	CPApplication CPApp = new CPApplication((ASTMethodNode)AST , constantValueFields, finder.getClassNameFieldNameToSootFieldMapping());
		        	AST.apply(CPApp);
				
		        	if(DEBUG)
		        		System.out.println("DONE CP for "+m.getName());
		        }
		        
		        //expression simplification
		        //SimplifyExpressions.DEBUG=true;
		        AST.apply(new SimplifyExpressions());
		        
		        
		        //SimplifyConditions.DEBUG=true;
		        AST.apply(new SimplifyConditions());
		        
		        // condition elimination      
		        //EliminateConditions.DEBUG=true;
		        AST.apply(new EliminateConditions((ASTMethodNode)AST));
		        
		        //local variable cleanup
		        AST.apply(new LocalVariableCleaner((ASTMethodNode)AST));

		        /*
		         * VERY EXPENSIVE STAGE of redoing all analyses!!!!
		         */
		        if(deobfuscate){
		        	//UselessLabelFinder.DEBUG=true;
		        	body.analyzeAST();
		        }
				
			}

		}
		
	}
}
