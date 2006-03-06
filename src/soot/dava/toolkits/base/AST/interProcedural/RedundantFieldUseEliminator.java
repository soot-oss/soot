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
import soot.dava.internal.javaRep.DIntConstant;
import soot.dava.toolkits.base.AST.traversals.AllDefinitionsFinder;
import soot.grimp.internal.GAssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.DoubleConstant;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.LongConstant;

import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.StringConstantValueTag;
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
 *    Use this information to do constant field propagation
 *       i.e. wherever we have the use of a field use the constant value (ideally the default value)
 *    
 *    Use that information to remove un-necessary conditions etc
 *    
 */
public class RedundantFieldUseEliminator {
	public final boolean DEBUG = true;
	
	String combiner = "$p$g";
	
	HashMap fieldToValues = new HashMap();

	Chain appClasses;
	
	public RedundantFieldUseEliminator(Chain classes){
		appClasses = classes;
	}
	
	
	public void applyAnalysis(){
	
		/*
		 * Compute method summaries for each method in the application
		 */
		debug("RedundantFielduseEliminator -- applyAnalyses","computing Method Summaries");
		computeFieldToValuesAssignedList();
		valuesForPrimTypeFields();
		
	}
	
	/*
	 * This method first gives default values to all the fields in all the classes
	 * We only care about fields which have primitive types
	 */
	public void valuesForPrimTypeFields(){
		
		HashMap primTypeFieldValueToUse = new HashMap();
		
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
					debug("TAGGED value found for tag"+combined);
					primTypeFieldValueToUse.put(combined,value);
				}
				else{
					//still havent gotten a value 
					
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
						else if ( (fieldType instanceof IntType || fieldType instanceof ByteType || fieldType instanceof ShortType) || fieldType instanceof CharType){
						    value = new Integer(0);
						}
						else
							throw new DecompilationException("Unknown primitive type...please report to developer");

						primTypeFieldValueToUse.put(combined,value);
						debug("DEFAULT value found for tag"+combined);
					}
					else{
						//there was some value assigned!!!!!!!!!
						debug("CHECKING USER ASSIGNED VALUES FOR"+combined);
						
					}
				}
				
	
	
	
	
	
	
	
	
				//check if this is assigned some value in any of the methods
								
				//if both isnt happening then we know it gets default automatically.....put this in some sort of VALUE TO USE FOR THIS FIELD LIST
				
				
				//if either of the above is true then check that the value in the methods and tags is always the same (0 or true or something like that)
				
				//if you can decide then ...........put this in some sort of VALUE TO USE FOR THIS FIELD LIST
				
				
				
				
				
				
				
			}
		}
	}
	
	/*
	 * Go through all the methods in the application and make a mapping of className+methodName ---> values assigned
	 * There can obviously be more than one value assigned to each field
	 */
	public void computeFieldToValuesAssignedList(){
		//go through all the classes
		Iterator classIt = appClasses.iterator();
		while(classIt.hasNext()){
			SootClass s = (SootClass) classIt.next();
			debug("\ncomputeMethodSummaries","Processing class "+s.getName());
			
			//go though all the methods
			Iterator methodIt = s.methodIterator();
			while (methodIt.hasNext()) {
				SootMethod m = (SootMethod) methodIt.next();
				DavaBody body = (DavaBody)m.getActiveBody();
				ASTNode AST = (ASTNode) body.getUnits().getFirst();
				
				//find all definitions in the program
				AllDefinitionsFinder defFinder = new AllDefinitionsFinder();
				AST.apply(defFinder);
				Iterator allDefIt = defFinder.getAllDefs().iterator();
				
				//go through each definition
				while(allDefIt.hasNext()){
					DefinitionStmt stmt = (DefinitionStmt)allDefIt.next();
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
	
	
	
	public void debug(String methodName, String debug){		
		if(DEBUG)
			System.out.println(methodName+ "    DEBUG: "+debug);
	}

	
	public void debug(String debug){		
		if(DEBUG)
			System.out.println("DEBUG: "+debug);
	}

}
