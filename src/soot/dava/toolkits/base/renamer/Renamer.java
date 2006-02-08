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

package soot.dava.toolkits.base.renamer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.Type;
import soot.dava.internal.AST.ASTMethodNode;
import soot.util.Chain;

// import soot.dava.toolkits.base.AST.analysis.*;
// import soot.*;
// import soot.jimple.*;
// import java.util.*;
// import soot.util.*;
// import soot.dava.*;
// import soot.grimp.*;
// import soot.grimp.internal.*;
// import soot.dava.internal.javaRep.*;
// import soot.dava.internal.asg.*;
// import soot.jimple.internal.*;
// import soot.dava.internal.AST.*;

public class Renamer {
	public final boolean DEBUG = false;
	heuristicSet heuristics;

	List locals; // a list of locals in scope

	Chain fields; // a list of fields in scope

	ASTMethodNode methodNode;
	List forLoopNames;

	public Renamer(heuristicSet info, ASTMethodNode node) {
		heuristics = info;
		locals = null;
		methodNode = node;
		forLoopNames = new ArrayList();
		forLoopNames.add("i");
		forLoopNames.add("j");
		forLoopNames.add("k");
		forLoopNames.add("l");
	}

	public void rename() {
		debug("Renaming started");

		//check for method Argument
		mainMethodArgument();
		forLoopIndexing();
		objectsGetClassName();
		exceptionNaming();
		removeDollarSigns();
	}

	
	/*
	 * If we have no better heuristic for objects then give them the name of the class
	 * if we can find this with a small initial letter (otherwise add a index)
	 */
	private void objectsGetClassName(){
		
		
	}
	
	
	/*
	 * In order to make sure that some previous heuristic which is usually a STRONGER
	 * heuristic has not already changed the name we use this method which checks for
	 * past name changes and only changes the name if the name hasnt been changed previously
	 */
	private void setName(Local var, String newName){
		
		//TODO: Check that name hasnt already been changed
		
		debug("Changed "+var.getName()+" to "+newName);
		var.setName(newName);
	}
	
	
	/*
	 * If we cant come up with any better name atleast we should remove the $ signs
	 */
	private void removeDollarSigns(){
		Iterator it = heuristics.getLocalsIterator();
		while (it.hasNext()) {
			Local tempLocal = (Local) it.next();
			String currentName = tempLocal.getName();
			int dollarIndex = currentName.indexOf('$'); 
			if(dollarIndex == 0){
				//meaning there is a $ sign in the first location
				String newName = currentName.substring(1,currentName.length());
				

				if(isUniqueName(newName)){
					setName(tempLocal,newName);
//					System.out.println("Changed "+currentName+" to "+newName);
					//tempLocal.setName(newName);
				}
			}
		}
	}
	
	
	
	/*
	 * 
	 */
	private void exceptionNaming(){
		Iterator it = heuristics.getLocalsIterator();
		while (it.hasNext()) {
			Local tempLocal = (Local) it.next();
			Type localType = tempLocal.getType();
			String typeString = localType.toString();
			if(typeString.indexOf("Exception")>=0){
				//the string xception occurs in this type
				debug("Type is an exception"+ tempLocal);
				
				//make a new name of all caps characters in typeString
				String newName = "";
				for(int i=0;i<typeString.length();i++){
					char character = typeString.charAt(i);
					if(Character.isUpperCase(character)){
						newName += Character.toLowerCase(character);
					}
				}
				int count =0;
				if(!isUniqueName(newName)){
					count++;
					while(!isUniqueName(newName+count)){
						count++;
					}				
				}
				if(count !=0)
					newName = newName + count;
				
				setName(tempLocal,newName);
			}
		}
	}
	
	
	
	
	
	
	/*
	 * Probably one of the most common programming idioms
	 * for loop indexes are often i j k l
	 */
	private void forLoopIndexing(){
		Iterator it = heuristics.getLocalsIterator();
		while (it.hasNext()) {
			Local tempLocal = (Local) it.next();
			debug("Checking local"+tempLocal.getName());
			if (heuristics.getHeuristic(tempLocal,
					infoGatheringAnalysis.FORLOOPUPDATE)) {
				// this local variable is the main argument
				// will like to set it to args if no one has an objection
				int count = -1;
				
				String newName; 

				do{
					count++;
					if(count>=forLoopNames.size()){
						newName=null;
						break;
					}
					newName = (String)forLoopNames.get(count);					
				}while (!isUniqueName(newName));

				if(newName!=null){
					setName(tempLocal,newName);
				}
			}
		}
	}
	
	
	
	/*
	 * A simple heuristic which sets the mainMethodArgument's name to args
	 */
	private void mainMethodArgument() {
		Iterator it = heuristics.getLocalsIterator();
		while (it.hasNext()) {
			Local tempLocal = (Local) it.next();
			if (heuristics.getHeuristic(tempLocal,
					infoGatheringAnalysis.MAINARG)) {

				// this local variable is the main argument
				// will like to set it to args if no one has an objection
				String newName = "args";
				int count = 0;
				while (!isUniqueName(newName)) {
					if(count==0)
						newName = newName+count;
					else
						newName = newName.substring(0,newName.length()-1)+count;

					count++;
				}
				setName(tempLocal,newName);
				//there cant be a same local with this heuristic set so just return
				return;
			}
		}

	}

	/*
	 * Should return true if the name is unique
	 */
	private boolean isUniqueName(String name) {
		Iterator it = getScopedLocals();
		// check that none of the locals uses this name
		while (it.hasNext()) {
			Local tempLocal = (Local) it.next();
			if (tempLocal.getName().equals(name))
				return false;
			else
				debug("New Name "+ name+ " is different from "+ tempLocal.getName());
		}

		it = getScopedFields();
		// check that none of the fields uses this name
		while (it.hasNext()) {
			SootField tempField = (SootField) it.next();
			if (tempField.getName().equals(name))
				return false;
		}
		return true;
	}

	/*
	 * Method is responsible to find all names with which there could be a
	 * potential clash The variables are: all the fields of this class and all
	 * the locals defined in this method
	 */
	private Iterator getScopedFields() {
		// get the fields for this class and store them
		SootClass sootClass = methodNode.getDavaBody().getMethod()
		.getDeclaringClass();
		fields = sootClass.getFields();

		return fields.iterator();
	}

	/*
	 * Method is responsible to find all variable names with which there could
	 * be a potential clash The variables are: all the fields of this class and
	 * all the locals defined in this method
	 */
	private Iterator getScopedLocals() {
		Iterator it = heuristics.getLocalsIterator();
		
		locals = new ArrayList();
		while(it.hasNext())
			locals.add((Local) it.next());

		return locals.iterator();

	}

	public void debug(String debug){
		if(DEBUG)
			System.out.println(debug);
	}
}