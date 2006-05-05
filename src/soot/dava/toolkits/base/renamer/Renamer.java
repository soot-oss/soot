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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import soot.ArrayType;
import soot.Local;
import soot.RefLikeType;
import soot.SootClass;
import soot.SootField;
import soot.Type;
import soot.dava.internal.AST.ASTMethodNode;
import soot.util.Chain;


public class Renamer {
	public final boolean DEBUG = false;
	heuristicSet heuristics;

	List locals; // a list of locals in scope

	Chain fields; // a list of fields in scope

	ASTMethodNode methodNode;
	List forLoopNames;

	HashMap changedOrNot;//keeps track of which local was changed previously
	
	public Renamer(heuristicSet info, ASTMethodNode node) {
		heuristics = info;
		locals = null;
		methodNode = node;
		
		changedOrNot = new HashMap();
		Iterator localIt = info.getLocalsIterator();
		while(localIt.hasNext())
			changedOrNot.put(localIt.next(),new Boolean(false));
		
		
		forLoopNames = new ArrayList();
		forLoopNames.add("i");
		forLoopNames.add("j");
		forLoopNames.add("k");
		forLoopNames.add("l");
	}

	/*
	 * Add any naming heuristic as a separate method and invoke the method from
	 * this method.
	 * 
	 * HOWEVER, NOTE that the order of naming really really matters
	 */
	public void rename() {
		debug("rename","Renaming started");

		// String args
		mainMethodArgument();
		
		//for(i=0;i<bla;i++)
		forLoopIndexing();
		
		//exceptions are named using first letter of each capital char in the class name
		exceptionNaming();
		
		//arrays get <type>Array
		arraysGetTypeArray();
		
		//if a local is assigned a field that name can be used since fields are conserved
		assignedFromAField();
		
		//check if a local is assigned the result of a new invocation
		newClassName();

		//check if a local is assigned after casting
		castedObject();	
		
		//if nothing else give a reference the name of the class
		objectsGetClassName();
		
		//atleast remove the ugly dollar signs
		removeDollarSigns();
	}

	
	
	/*
	 * if there is an array int[] x. then if no other heuristic matches give it the name intArray 
	 */
	private void arraysGetTypeArray(){
		Iterator it = heuristics.getLocalsIterator();
		while (it.hasNext()) {
			Local tempLocal = (Local) it.next();
			if(alreadyChanged(tempLocal)){
				continue;
			}
			
			debug("arraysGetTypeArray","checking "+tempLocal);
			
			Type type = tempLocal.getType();
			if(type instanceof ArrayType){
				debug("arraysGetTypeArray","Local:"+tempLocal+" is an Array Type: "+type.toString());
				String tempClassName = type.toString();
				//remember that a toString of an array gives you the square brackets
				if(tempClassName.indexOf('[')>=0)
					tempClassName = tempClassName.substring(0,tempClassName.indexOf('['));
				
				//debug("arraysGetTypeArray","type of object is"+tempClassName);
				if(tempClassName.indexOf('.')!= -1){
					//contains a dot have to remove that
					tempClassName=tempClassName.substring(tempClassName.lastIndexOf('.')+1);
				}
				
				
				String newName = tempClassName.toLowerCase();
				newName = newName+"Array";
				int count=0;
				newName += count;
				count++;
				
				while(!isUniqueName(newName)){
					newName = newName.substring(0,newName.length()-1)+count;
					count++;						
				}
				setName(tempLocal,newName);

			}
		}
		
		
	}
	
	
	
	/*
	 * The method assigns any local whose name hasnt been changed yet to 
	 * the name of the class type it belongs to
	 */
	private void objectsGetClassName(){
		Iterator it = heuristics.getLocalsIterator();
		while (it.hasNext()) {
			Local tempLocal = (Local) it.next();
			if(alreadyChanged(tempLocal)){
				continue;
			}
			
			debug("objectsGetClassName","checking "+tempLocal);
			
			Type type = tempLocal.getType();
			if(type instanceof ArrayType){
				//should have been handled by arraysGetTypeArray heuristic
				continue;
			}
			
			if(type instanceof RefLikeType){
				debug("objectsGetClassName","Local:"+tempLocal+" Type: "+type.toString());
				//debug("objectsGetClassName","getting array type"+type.getArrayType());
				String tempClassName = type.toString();
				//debug("objectsGetClassName","type of object is"+tempClassName);
				if(tempClassName.indexOf('.')!= -1){
					//contains a dot have to remove that
					tempClassName=tempClassName.substring(tempClassName.lastIndexOf('.')+1);
				}
				
				
				String newName = tempClassName.toLowerCase();
				int count=0;
				newName += count;
				count++;
				
				while(!isUniqueName(newName)){
					newName = newName.substring(0,newName.length()-1)+count;
					count++;						
				}
				setName(tempLocal,newName);

			}
		}
		
	}
	
	
	
	/*
	 * If a local is assigned the resullt of a cast expression   temp = (List) object;
	 * then u can use list as the name...however only if its always casted to the same object
	 */
	private void castedObject(){
		debug("castedObject","");
		
		Iterator it = heuristics.getLocalsIterator();
		while (it.hasNext()) {
			Local tempLocal = (Local) it.next();
			if(!alreadyChanged(tempLocal)){
				debug("castedObject","checking "+tempLocal);
				List classes = heuristics.getCastStrings(tempLocal);
				
				Iterator itClass = classes.iterator();
				String classNameToUse = null;
				while(itClass.hasNext()){
					String tempClassName = (String)itClass.next();
					if(tempClassName.indexOf('.')!= -1){
						//contains a dot have to remove that
						tempClassName=tempClassName.substring(tempClassName.lastIndexOf('.')+1);
					}
					if(classNameToUse == null)
						classNameToUse = tempClassName;			
					else if(!classNameToUse.equals(tempClassName)){
						//different new assignment
						//cant use these classNames
						classNameToUse=null;
						break;
					}
				}//going through class names stored
				if(classNameToUse!=null){
					debug("castedObject","found a classNametoUse through cast expr");
					/*
					 * We should use this classNAme to assign to the local name
					 * We are guaranteed that all cast expressions use this type
					 */
					String newName = classNameToUse.toLowerCase();
					int count=0;
					newName += count;
					count++;
					
					while(!isUniqueName(newName)){
						newName = newName.substring(0,newName.length()-1)+count;
						count++;						
					}
					setName(tempLocal,newName);
				}
			}//not already changed
		}//going through locals
	}
	
	/*
	 * See if any local was initialized using the new operator
	 * That name might give us a hint to a name to use for the local
	 */
	private void newClassName(){
		
		debug("newClassName","");
		//check if CLASSNAME is set
		//that would mean there was new className invocation
		Iterator it = heuristics.getLocalsIterator();
		while (it.hasNext()) {
			Local tempLocal = (Local) it.next();
			if(!alreadyChanged(tempLocal)){
				debug("newClassName","checking "+tempLocal);
				List classes = heuristics.getObjectClassName(tempLocal);
				Iterator itClass = classes.iterator();
				String classNameToUse = null;
				while(itClass.hasNext()){
					String tempClassName = (String)itClass.next();
					if(tempClassName.indexOf('.')!= -1){
						//contains a dot have to remove that
						tempClassName=tempClassName.substring(tempClassName.lastIndexOf('.')+1);
					}
					if(classNameToUse == null)
						classNameToUse = tempClassName;			
					else if(!classNameToUse.equals(tempClassName)){
						//different new assignment
						//cant use these classNames
						classNameToUse=null;
						break;
					}
				}//going through class names stored
				if(classNameToUse!=null){
					debug("newClassName","found a classNametoUse");
					/*
					 * We should use this classNAme to assign to the local name
					 * We are guaranteed that all new invocations use this class name
					 */
					String newName = classNameToUse.toLowerCase();
					int count=0;
					newName += count;
					count++;
					
					while(!isUniqueName(newName)){
						newName = newName.substring(0,newName.length()-1)+count;
						count++;						
					}
					setName(tempLocal,newName);
				}
			}//not already changed
		}//going through locals
		
	}
	
	/*
	 * If a local is assigned from a field (static or non staitc) we can use that name
	 * to assign a some what better name for the local
	 * 
	 * If multiple fields are assigned then it might be a better idea to not do
	 * anything since that will only confuse the user
	 * 
	 */
	private void assignedFromAField(){
		Iterator it = heuristics.getLocalsIterator();
		while (it.hasNext()) {
			Local tempLocal = (Local) it.next();
			if(!alreadyChanged(tempLocal)){
				debug("assignedFromField","checking "+tempLocal);
				List fieldNames = heuristics.getFieldName(tempLocal);
				if(fieldNames.size()>1){
					//more than one fields were assigned to this var
					continue;
				}
				else if(fieldNames.size()==1){
					//only one field was used
					String fieldName = (String)fieldNames.get(0);
					
					//okkay to use the name of the field if its not in scope
					//eg it was some other classes field
					int count=0;
					while(!isUniqueName(fieldName)){
						if(count==0)
							fieldName = fieldName+count;
						else
							fieldName = fieldName.substring(0,fieldName.length()-1)+count;
						count++;
					}
					
					setName(tempLocal,fieldName);
				}//only one field assigned to this local
			}//not changed
		}//going through locals
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
				debug("exceptionNaming","Type is an exception"+ tempLocal);
				
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
			debug("foeLoopIndexing","Checking local"+tempLocal.getName());
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
	 * In order to make sure that some previous heuristic which is usually a STRONGER
	 * heuristic has not already changed the name we use this method which checks for
	 * past name changes and only changes the name if the name hasnt been changed previously
	 */
	private void setName(Local var, String newName){
			
		Object truthValue = changedOrNot.get(var);
		
		//if it wasnt in there add it
		if(truthValue == null)
			changedOrNot.put(var,new Boolean(false));
		else{
			if(((Boolean)truthValue).booleanValue()){
				//already changed just return
				debug("setName","Var: "+var + " had already been renamed");
				return;
			}
		}
		//will only get here if the var had not been changed 
		
		debug("setName","Changed "+var.getName()+" to "+newName);
		var.setName(newName);
		changedOrNot.put(var,new Boolean(true));
	}

	
	
	
	
	
	
	
	
	
	
	/*
	 * Check if a local has already been changed
	 * @param local to check
	 * @return true if already changed otherwise false
	 */
	private boolean alreadyChanged(Local var){
		Object truthValue = changedOrNot.get(var);
		
		//if it wasnt in there add it
		if(truthValue == null){
			changedOrNot.put(var,new Boolean(false));
			return false;
		}
		else{
			if(((Boolean)truthValue).booleanValue()){
				//already changed just return
				debug("alreadyChanged","Var: "+var + " had already been renamed");
				return true;
			}
			else
				return false;
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
			if (tempLocal.getName().equals(name)){
				debug("isUniqueName","New Name "+ name+ " is not unique (matches some local)..changing");	
				return false;
			}
			else
				debug("isUniqueName","New Name "+ name+ " is different from local "+ tempLocal.getName());
		}

		it = getScopedFields();
		// check that none of the fields uses this name
		while (it.hasNext()) {
			SootField tempField = (SootField) it.next();
			if (tempField.getName().equals(name)){
				debug("isUniqueName","New Name "+ name+ " is not unique (matches field)..changing");
				return false;
			}
			else
				debug("isUniqurName","New Name "+ name+ " is different from field "+ tempField.getName());
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

	public void debug(String methodName, String debug){
		
		if(DEBUG)
			System.out.println(methodName+ "    DEBUG: "+debug);
	}
	
}