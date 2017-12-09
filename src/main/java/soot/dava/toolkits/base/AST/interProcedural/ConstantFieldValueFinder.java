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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.PrimType;
import soot.ShortType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;

import soot.Value;
import soot.dava.DavaBody;
import soot.dava.DecompilationException;

import soot.dava.internal.AST.ASTNode;
import soot.dava.toolkits.base.AST.traversals.AllDefinitionsFinder;
import soot.jimple.DefinitionStmt;
import soot.jimple.DoubleConstant;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NumericConstant;

import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.util.Chain;

/*
 * Deemed important because of obfuscation techniques which add crazy
 * control flow under some condition which is never executed because
 * it uses some field which is always false!!
 * 
 * Goal:
 *    Prove that a field is never assigned a value or that if it is assigned a value
 *    we can statically tell this value
 *    
 *    
 */
public class ConstantFieldValueFinder {
	public final boolean DEBUG = false;
	
	public static String combiner = "_$p$g_";

	HashMap<String, SootField> classNameFieldNameToSootFieldMapping = new HashMap<String, SootField>();
	
	HashMap<String, ArrayList> fieldToValues = new HashMap<String, ArrayList>();
	HashMap<String, Object> primTypeFieldValueToUse = new HashMap<String, Object>();
	
	Chain appClasses;
	
	public ConstantFieldValueFinder(Chain classes){
		appClasses = classes;
		debug("ConstantFieldValueFinder -- applyAnalyses","computing Method Summaries");
		computeFieldToValuesAssignedList();
		valuesForPrimTypeFields();
	}
	
	
	/*
	 * The hashMap returned contains a mapping of
	 * class + combiner + field   ----> Double/Float/Long/Integer
	 * if there is no mapping for a particular field then that means we couldnt detect a constant value for it
	 */
	public HashMap<String, Object> getFieldsWithConstantValues(){	
		return primTypeFieldValueToUse;
	}
	
	public HashMap<String, SootField> getClassNameFieldNameToSootFieldMapping(){
		return classNameFieldNameToSootFieldMapping;
	}
	
	
	/*
	 * This method gives values to all the fields in all the classes if they can be determined statically
	 * We only care about fields which have primitive types
	 */
	private void valuesForPrimTypeFields(){		
		//go through all the classes
		Iterator classIt = appClasses.iterator();
		while(classIt.hasNext()){
			SootClass s = (SootClass) classIt.next();
			debug("\nvaluesforPrimTypeFields","Processing class "+s.getName());

			String declaringClass = s.getName();
			Iterator fieldIt = s.getFields().iterator();
			while(fieldIt.hasNext()){
				SootField f = (SootField)fieldIt.next();
				
				String fieldName = f.getName();
				Type fieldType = f.getType();
				if(! (fieldType instanceof PrimType ) )
					continue;
				
				String combined = declaringClass + combiner + fieldName;
				classNameFieldNameToSootFieldMapping.put(combined,f);
				
				Object value=null;
				
				//check for constant value tags
				if(fieldType instanceof DoubleType && f.hasTag("DoubleConstantValueTag")){
				    double val = ((DoubleConstantValueTag)f.getTag("DoubleConstantValueTag")).getDoubleValue();
				    value = new Double(val);
				}
				else if (fieldType instanceof FloatType && f.hasTag("FloatConstantValueTag")){
				    float val = ((FloatConstantValueTag)f.getTag("FloatConstantValueTag")).getFloatValue();
				    value = new Float(val);
				}
				else if (fieldType instanceof LongType && f.hasTag("LongConstantValueTag")){
				    long val = ((LongConstantValueTag)f.getTag("LongConstantValueTag")).getLongValue();
				    value = new Long(val);
				}
				else if (fieldType instanceof CharType && f.hasTag("IntegerConstantValueTag")){
				    int val = ((IntegerConstantValueTag)f.getTag("IntegerConstantValueTag")).getIntValue();
				    value = new Integer(val);
				}
				else if (fieldType instanceof BooleanType && f.hasTag("IntegerConstantValueTag")){
				    int val = ((IntegerConstantValueTag)f.getTag("IntegerConstantValueTag")).getIntValue();
				    if (val ==0)
				    	value = new Boolean(false);
				    else
				    	value = new Boolean(true);
				}
				else if ( (fieldType instanceof IntType || fieldType instanceof ByteType || fieldType instanceof ShortType) && 
					  f.hasTag("IntegerConstantValueTag")){
				    int val = ((IntegerConstantValueTag)f.getTag("IntegerConstantValueTag")).getIntValue();
				    value = new Integer(val);
				}

				
				//if there was a constant value tag we have its value now
				if(value != null){
					debug("TAGGED value found for field: "+combined);
					primTypeFieldValueToUse.put(combined,value);
					
					//continue with next field
					continue;
				}

					
				//see if the field was never assigned in which case it gets default values
				Object temp = fieldToValues.get(combined);
				if(temp == null){
					//no value list found is good
						
					//add default value to primTypeFieldValueToUse hashmap

					if(fieldType instanceof DoubleType )
						value = new Double(0);
					else if (fieldType instanceof FloatType )
						value = new Float(0);
					else if (fieldType instanceof LongType )
						value = new Long(0);
					else if (fieldType instanceof BooleanType)
						value = new Boolean(false);
					else if ( (fieldType instanceof IntType || fieldType instanceof ByteType || 
							fieldType instanceof ShortType) || fieldType instanceof CharType){
						value = new Integer(0);
					}
					else
						throw new DecompilationException("Unknown primitive type...please report to developer");

					primTypeFieldValueToUse.put(combined,value);
					debug("DEFAULT value for field: "+combined);
					
					//continue with next field
					continue;
				}

				//havent got a tag with value and havent use default since SOME method did define the field atleast once
						
				//there was some value assigned!!!!!!!!!
				debug("CHECKING USER ASSIGNED VALUES FOR: "+combined);
				ArrayList values = (ArrayList)temp;
						
				//check if they are all constants and that too the same constant
				Iterator it = values.iterator();
				NumericConstant tempConstant = null;
					
				while(it.hasNext()){
					Value val = (Value)it.next();
					if(! (val instanceof NumericConstant)){
						tempConstant=null;
						debug("Not numeric constant hence giving up");
						break;
					}
							
					if(tempConstant == null){
						tempConstant = (NumericConstant)val;
					}
					else{
						//check that this value is the same as previous
						if( ! tempConstant.equals(val)){
							tempConstant = null;
							break;
						}
					}								
				}
				if(tempConstant == null){
					
					//continue with next field cant do anything about this one
					continue;
				}
				
				//agreed on a unique constant value
				
				/*
				 * Since these are fields are we are doing CONTEXT INSENSITIVE
				 * WE need to make sure that the agreed unique constant value is the default value
				 * 
				 * I KNOW IT SUCKS BUT HEY WHAT CAN I DO!!!
				 */
					
				if(tempConstant instanceof LongConstant){
					Long tempVal = new Long( ((LongConstant)tempConstant).value );
					if(tempVal.compareTo(new Long(0)) ==0)
						primTypeFieldValueToUse.put(combined,tempVal);
					else
						debug("Not assigning the agreed value since that is not the default value for "+combined);
				}
				else if(tempConstant instanceof DoubleConstant){
					Double tempVal = new Double( ((DoubleConstant)tempConstant).value );
					if(tempVal.compareTo(new Double(0)) ==0)
						primTypeFieldValueToUse.put(combined,tempVal);
					else
						debug("Not assigning the agreed value since that is not the default value for "+combined);

				}
				else if(tempConstant instanceof FloatConstant){
					Float tempVal = new Float( ((FloatConstant)tempConstant).value );
					if(tempVal.compareTo(new Float(0)) ==0)
						primTypeFieldValueToUse.put(combined,tempVal);
					else
						debug("Not assigning the agreed value since that is not the default value for "+combined);

				}
				else if(tempConstant instanceof IntConstant){
					Integer tempVal = new Integer( ((IntConstant)tempConstant).value );
					if(tempVal.compareTo(new Integer(0)) ==0){
						SootField tempField = classNameFieldNameToSootFieldMapping.get(combined);
						if(tempField.getType() instanceof BooleanType){
							primTypeFieldValueToUse.put(combined,new Boolean(false));							
							//System.out.println("puttingvalue false for"+combined);	
						}
						else{
							primTypeFieldValueToUse.put(combined,tempVal);
							//System.out.println("puttingvalue 0 for"+combined);
						}
					}
					else
						debug("Not assigning the agreed value since that is not the default value for "+combined);

				}
				else{
					throw new DecompilationException("Un handled Numberic Constant....report to programmer");
				}						
			} //all fields of the class
		} //all classes
	}
	
	/*
	 * Go through all the methods in the application and make a mapping of className+methodName ---> values assigned
	 * There can obviously be more than one value assigned to each field
	 */
	private void computeFieldToValuesAssignedList(){
		//go through all the classes
		Iterator classIt = appClasses.iterator();
		while(classIt.hasNext()){
			SootClass s = (SootClass) classIt.next();
			debug("\ncomputeMethodSummaries","Processing class "+s.getName());
			
			//go though all the methods
			Iterator methodIt = s.methodIterator();
			while (methodIt.hasNext()) {
				SootMethod m = (SootMethod) methodIt.next();
				DavaBody body = null;
				if(m.hasActiveBody()){
					/*
					 * Added to try to fix the no active body found exception
					 */
					body = (DavaBody)m.getActiveBody();
				}
				else{
					continue;
				}
				
				ASTNode AST = (ASTNode) body.getUnits().getFirst();
				
				//find all definitions in the program
				AllDefinitionsFinder defFinder = new AllDefinitionsFinder();
				AST.apply(defFinder);
				Iterator<DefinitionStmt> allDefIt = defFinder.getAllDefs().iterator();
				
				//go through each definition
				while(allDefIt.hasNext()){
					DefinitionStmt stmt = allDefIt.next();
					//debug("DefinitionStmt")
					Value left = stmt.getLeftOp();
					
					/*
					 * Only care if we have fieldRef on the left
					 */
					if(! (left instanceof FieldRef) ){
						continue;
					}
					
					//we know definition is to a field
					debug("computeMethodSummaries method: "+m.getName(),"Field ref is: "+left);
					// Information we want to store is class of field and name of field and the right op
					
					FieldRef ref = (FieldRef)left;
					SootField field = ref.getField();
					
					
					/*
					 * Only care about fields with primtype
					 */
					if(!( field.getType() instanceof PrimType))
						continue;
					
					
					
					String fieldName = field.getName();
					String declaringClass = field.getDeclaringClass().getName();
					
					debug("\tField Name: "+ fieldName);
					debug("\tField DeclaringClass: "+ declaringClass);
						
					//get the valueList for this class+field combo
					String combined = declaringClass + combiner + fieldName;
					Object temp = fieldToValues.get(combined);

					ArrayList valueList;
					if(temp == null){
						//no value of this field was yet assigned
						valueList = new ArrayList();
						fieldToValues.put(combined,valueList);
					}
					else{
						valueList = (ArrayList)temp;
					}
						
					valueList.add(stmt.getRightOp());
				}//going through all the definitions
			}//going through methods of class s
		}//going through  classes
	}
	
	
	public void printConstantValueFields(){
		System.out.println("\n\n Printing Constant Value Fields (method: printConstantValueFields)");
		Iterator<String> it =  primTypeFieldValueToUse.keySet().iterator();
		while(it.hasNext()){
			String combined = it.next();
			
			int temp = combined.indexOf(combiner,0);
			if(temp > 0){
				System.out.println("Class: "+ combined.substring(0,temp)+" Field: "+combined.substring(temp+combiner.length()) +" Value: "+ primTypeFieldValueToUse.get(combined));
				
			}
		}
	}
	
	public void debug(String methodName, String debug){		
		if(DEBUG)
			System.out.println(methodName+ "    DEBUG: "+debug);
	}

	
	public void debug(String debug){		
		if(DEBUG)
			System.out.println("DEBUG: "+debug);
	}

}
