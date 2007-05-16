package soot.dava.toolkits.base.AST.structuredAnalysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.PrimType;
import soot.ShortType;
import soot.SootField;
import soot.Type;
import soot.Value;
import soot.dava.DavaFlowAnalysisException;
import soot.dava.internal.AST.ASTBinaryCondition;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTUnaryCondition;

import soot.dava.internal.AST.ASTUnaryBinaryCondition;
import soot.dava.internal.javaRep.DNotExpr;

import soot.dava.toolkits.base.AST.interProcedural.ConstantFieldValueFinder;
import soot.jimple.BinopExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.Stmt;

public class CP extends StructuredAnalysis {
	/*  
	 *   Constant Propagation:
	 *   
	 *	 Step 1:
     *     Sets of CPTuple (className, CPVariable, value)
     *    where: CPVariable contains a local or SootField
     *     
     *   Step 2:
     *	  A local or SootField has a constant value at a program point p if on all
     *	  program paths from the start of the method to point p the local or Sootfield
     *    has only been assigned this constant value.
     *    
     *   Step 3:
     *       Forward Analysis
     *       
     *   Step 4:     
     *      Intersection  (See intersection method in CPFlowSet)
     *      
     *   Step 5:
     *       See method processStatement
     *              
     *   Step 6:
     *       out(start) = all locals set to bottom(they shouldnt be present in the initialSet all formals set to Top, 
     *       all constant fields set to constant values
     *       
     *       newInitialFlow: all locals and formals set to Top, all constant fields set to constant values
     *       remember new InitialFlow is ONLY used for input to catchBodies
     *       
     *       Any local or field which is not in the flow set at any time is necessarily bottom
     *       
     *       
     *   knowing how a condition evaluates can give us useful insight to the values of variables
	 *   Handle this for a == b where both a and b are primtype 
	 *   and special case handle it for A where A is a unary boolean condition
	 *         
	 *   See following over-riden methods:
	 *
	 *     public Object processASTIfElseNode(ASTIfElseNode node,Object input)
	 *     public Object processASTIfNode(ASTIfNode node,Object input)
	 *     public Object processASTSwitchNode(ASTSwitchNode node,Object input)
     */
	
	ArrayList<CPTuple> constantFieldTuples = null;  //VariableTuples of constantFields
	ArrayList<CPTuple> formals = null;   //VariableTuples for formals initially set to T
	ArrayList<CPTuple> locals = null;  //VariableTuples for locals initially set to default

	
	ArrayList<CPTuple> initialInput = null; //VariableTuples of constantFields, locals set to 0 and formals set to T
	
	ASTMethodNode methodNode = null;
	String localClassName = null;
	
	/*
	 * The start of the analysis takes place whenever this constructor is invoked
	 */
    public CP(ASTMethodNode analyze, HashMap<String, Object> constantFields,HashMap<String, SootField> classNameFieldNameToSootFieldMapping){
    	super();
/*
    	DEBUG = true;
    	DEBUG_IF = true;
    	DEBUG_WHILE = true;
    	DEBUG_STATEMENTS = true;
*/
    	this.methodNode=analyze;
    	localClassName = analyze.getDavaBody().getMethod().getDeclaringClass().getName();
    	
    	//Create a list of VariableValueTuples for all the constantFields
    	createConstantFieldsList(constantFields, classNameFieldNameToSootFieldMapping);	

    	//Create the complete list of vars to go into constant propagation
    	createInitialInput();
    	    	
    	//input to constant propagation should not be an empty flow set it is the set of all constant fields, locals assigned 0 and formals assigned T
    	CPFlowSet initialSet = new CPFlowSet();
    	Iterator<CPTuple> it = initialInput.iterator();
    	while(it.hasNext())
    		initialSet.add(it.next());
    	
    	//System.out.println("Initial set"+initialSet.toString());
    	CPFlowSet result = (CPFlowSet)process(analyze, initialSet);
    	//System.out.println("Last result :"+result.toString());
    }
	
   

	/*
	 * constant fields added with KNOWN CONSTANT VALUE    
	 * formals added with TOP
	 * locals added with 0
	 * other fields IGNORED
	 * 
	 */
    public void createInitialInput(){
    	initialInput = new ArrayList<CPTuple>();    	
    
    	//adding constant fields
    	initialInput.addAll(constantFieldTuples);
    	
    	//String className = analyze.getDavaBody().getMethod().getDeclaringClass().getName();
    	    	   	
    	//adding formals
    	formals = new ArrayList<CPTuple>();
    	//System.out.println("Adding following formals: with TOP");
    	Collection col = methodNode.getDavaBody().get_ParamMap().values();
    	Iterator it = col.iterator();
    	while(it.hasNext()){
    		Object temp = it.next();
    		if(temp instanceof Local){
    			Local tempLocal = (Local)temp;
    			if(! (tempLocal.getType() instanceof PrimType ))
    				continue;
    			
    			CPVariable newVar = new CPVariable(tempLocal);
    			
    			//new tuple set to top since this is a formal and we dont know what value we will get into it
    			CPTuple newTuple = new CPTuple(localClassName,newVar,true);
    			initialInput.add(newTuple);
    			formals.add(newTuple);
    			//System.out.print("\t"+tempLocal.getName());
    		}
    	}
    	//System.out.println();
    	
    	  	
    	//adding locals
    	List decLocals = methodNode.getDeclaredLocals();
    	it = decLocals.iterator();
    	locals = new ArrayList<CPTuple>();
    	//System.out.println("Adding following locals with default values:");
    	while(it.hasNext()){
    		Object temp = it.next();
    		if(temp instanceof Local){
    			Local tempLocal = (Local)temp;
    			Type localType = tempLocal.getType();
    			
    			if(! (localType instanceof PrimType ))
    					continue;
			
    			CPVariable newVar = new CPVariable(tempLocal);
    			
    			//store the default value into this object
    			Object value;    			
    			
    			//locals value is set to the default value that it can have depending on its type
    			if(localType instanceof BooleanType)
    				value = new Boolean(false);
    			else if(localType instanceof ByteType)
    				value = new Integer(0);    			
    			else if(localType instanceof CharType)
    				value = new Integer(0);    			
    			else if(localType instanceof DoubleType)
    				value = new Double(0);    			
    			else if(localType instanceof FloatType)
    				value = new Float(0);    			
    			else if(localType instanceof IntType)
    				value = new Integer(0);    			
    			else if(localType instanceof LongType)
    				value = new Long(0);    			
    			else if(localType instanceof ShortType)
    				value = new Integer(0);    			
    			else
    				throw new DavaFlowAnalysisException("Unknown PrimType");
				
				CPTuple newTuple = new CPTuple(localClassName,newVar,value);
				
				/*
				 * Commenting the next line since we dont want initial Input to have any locals in it
				 * all locals are considered bottom initially 
				 */				
    			//initialInput.add(newTuple);
    			
				locals.add(newTuple);
    			//System.out.print("\t"+tempLocal.getName());
    		}//was a local		
    	}
    	//System.out.println();
	}
	
	
	/*
	 * Uses the results of the ConstantValueFinder to create a list of constantField CPTuple
	 */
	private void createConstantFieldsList(HashMap<String, Object> constantFields, HashMap<String, SootField> classNameFieldNameToSootFieldMapping){
		constantFieldTuples = new ArrayList<CPTuple>();
		
		Iterator<String> it = constantFields.keySet().iterator();
		//System.out.println("Adding constant fields to initial set: ");
		while(it.hasNext()){
			String combined = it.next();
			
			int temp = combined.indexOf(ConstantFieldValueFinder.combiner,0);
			if(temp > 0){
				String className = combined.substring(0,temp);
				
				//String fieldName = combined.substring(temp+ ConstantFieldValueFinder.combiner.length());
				SootField field = classNameFieldNameToSootFieldMapping.get(combined);
				//String fieldName = field.getName();
				
				if(! (field.getType() instanceof PrimType)){
					//we only care about PrimTypes
					continue;
				}
				
				//object type is double float long boolean or integer
				Object value = constantFields.get(combined);

				CPVariable var = new CPVariable(field);
				
				CPTuple newTuples = new CPTuple(className,var,value);
				constantFieldTuples.add(newTuples);
				//System.out.print("Class: "+className + " Field: "+fieldName+" Value: "+value+"   ");
			}
			else{
				throw new DavaFlowAnalysisException("Second argument of VariableValuePair not a variable");
			}
 
		}
		//System.out.println("");
	}
	
	

    public DavaFlowSet emptyFlowSet(){
    	return new CPFlowSet();
    }
    

    
    /*
	 * Setting the mergetype to intersection but since we are going to have constantpropagation flow sets
	 * the intersection method for this flow set will be invoked which defines the correct semantics of intersection
	 * for the case of constant propagation
	 */
	public void setMergeType() {
		MERGETYPE = INTERSECTION;
	}

	
	
	
	/*
	 * newInitialFlow is invoked for the input set of the catchBodies
	 * 
	 * formals initialized to top since we dont know what has happened so far in the body
	 * locals initialized to top since we dont know what has happened so far in the method body
	 * constant fields present with their constant value since that never changes
	 */		
	public Object newInitialFlow() {
		CPFlowSet flowSet = new CPFlowSet();
		
		//formals and locals should be both initialized to top since we dont know what has happened so far in the body
		ArrayList<CPTuple> localsAndFormals = new ArrayList<CPTuple>();
		localsAndFormals.addAll(formals);
		localsAndFormals.addAll(locals);
		
		Iterator<CPTuple> it = localsAndFormals.iterator();
		while(it.hasNext()){
			CPTuple tempTuple = (CPTuple)it.next().clone();
			
			//just making sure all are set to Top
			if(!tempTuple.isTop())
				tempTuple.setTop();
			
			flowSet.add(tempTuple);
		}
		
		//constant fields should be present with their constant value since that never changes
		it = constantFieldTuples.iterator();
		while(it.hasNext()){
			flowSet.add(it.next());
		}
		
		 return flowSet;
	}

	
	
	
	
	
	
	public Object cloneFlowSet(Object flowSet) {
		if(flowSet instanceof CPFlowSet){
		    return ((CPFlowSet)flowSet).clone();
		}
		else
		    throw new RuntimeException("cloneFlowSet not implemented for other flowSet types"+flowSet.toString());
	}

	
	
    
    
    
    
	/*
	 * Since we are only keeping track of constant fields now there will be no kill 
	 * if we were keeping track of fields then we woul dneed to kill all non constant fields if there was an invokeExpr
	 */
	public Object processUnaryBinaryCondition(ASTUnaryBinaryCondition cond, Object input) {
		if(!(input instanceof CPFlowSet)){
		    throw new RuntimeException("processCondition is not implemented for other flowSet types"+input.toString());
		}
		CPFlowSet inSet = (CPFlowSet)input;
		return inSet;
	}

	

	
	
	
	/*
	 *  Has no effect whatsoever on the analysis
	 */	
	public Object processSynchronizedLocal(Local local, Object input) {
		if(!(input instanceof CPFlowSet)){
		    throw new RuntimeException("processSynchronized  is not implemented for other flowSet types"+input.toString());
		}
		DavaFlowSet inSet = (DavaFlowSet)input;
		return inSet;

	}

	
	
	
	
	/*
	 * no effect on the analysis since we are keeping track of non constant fields only
	 * if we were tracking other fields then
	 * If the value contained an invoke expr top all non constant fields similar to as done for unarybinary condition
	 */
	public Object processSwitchKey(Value key, Object input) {
		if(!(input instanceof CPFlowSet)){
		    throw new RuntimeException("processCondition is not implemented for other flowSet types"+input.toString());
		}
		CPFlowSet inSet = (CPFlowSet)input;
		return inSet;

	}
	
	
	
	
	
	
	
	
	/*	
	 *   x = expr;
	 *    
	 *  expr is a constant
	 * 
	 *  epr is a local (Note we are not going to worry about fieldRefs)
	 * 	      
	 *  expr is a Unary op with neg followed by a local or field
	 *  
	 *  expr is actually exp1 op expr2 ..... ( x = x+1, y = i * 3,   x = 3 *0 
	 *            
	 *  expr contains an invokeExpr
	 */
	public Object processStatement(Stmt s, Object input) {
		if(!(input instanceof CPFlowSet))
		    throw new RuntimeException("processStatement is not implemented for other flowSet types");
		
		CPFlowSet inSet = (CPFlowSet)input;		
		if(inSet == NOPATH)
			return inSet;
		
		if(! (s instanceof DefinitionStmt) ) 
			return inSet;
			
		DefinitionStmt defStmt = (DefinitionStmt)s;
		//x = expr;
		//confirm that the left side is a local with a primitive type
		Value left = defStmt.getLeftOp();
		if( ! (left instanceof Local && ((Local)left).getType() instanceof PrimType) )
			return inSet;

		//left is a primitive primitive local 		
		CPFlowSet toReturn = (CPFlowSet)cloneFlowSet(inSet);

		/*
		 * KILL ANY PREVIOUS VALUE OF this local as this is an assignment
		 * Remember the returned value can be null if the element was not found or it was TOP
		 */
		Object killedValue = killButGetValueForUse((Local)left,toReturn);

		Value right = defStmt.getRightOp();
			
		Object value = CPHelper.isAConstantValue(right);
		if(value != null){
			//EXPR IS A CONSTANT
			if(left.getType() instanceof BooleanType ){
				Integer tempValue = (Integer)value;
				if(tempValue.intValue() == 0)
					value = new Boolean(false);
				else
					value = new Boolean(true);
			}
			addOrUpdate(toReturn,(Local)left,value);
		}
		else{
			//EXPR IS NOT A CONSTANT 
			handleMathematical(toReturn,(Local)left,right,killedValue);					
		}
		return toReturn;
	}
	
	
	

	
	/*
	 * The returned value is the current constant associated with left. Integer/Long/Double/Float/Boolean
	 * The returned value can also be null if the element was not found or it was TOP
	 */
	public Object killButGetValueForUse(Local left, CPFlowSet toReturn){
	
		Iterator it = toReturn.toList().iterator();
		while(it.hasNext()){
			CPTuple tempTuple = (CPTuple)it.next();
			
			if( ! (tempTuple.getSootClassName().equals(localClassName)) )		
				continue;
			
			//className is the same check if the variable is the same or not
			if(tempTuple.containsLocal()){ //remmeber the sets contain constant fields also
				Local tempLocal = tempTuple.getVariable().getLocal();
				if(left.getName().equals(tempLocal.getName())){
					//KILL THIS SUCKA!!!
					Object killedValue  = tempTuple.getValue();
					tempTuple.setTop();
					return killedValue;
				}					
			}
		} //going through all elements of the flow set
		
		//if this element was no where enter it with top
		CPVariable newVar = new CPVariable(left);
		
		//create the CPTuple
		//System.out.println("trying to kill something which was not present so added with TOP");
		CPTuple newTuple = new CPTuple(localClassName,newVar,false);
		toReturn.add(newTuple);
		return null;		
	}
	
	
	
	
	
	/*
	 * Create a CPTuple for left with the val and update the toReturn set  
	 */
	private void addOrUpdate(CPFlowSet toReturn , Local left, Object val){
		CPVariable newVar = new CPVariable(left);
		
		CPTuple newTuple = new CPTuple(localClassName,newVar,val);
		toReturn.addIfNotPresent(newTuple);
		//System.out.println("DefinitionStmt checked right expr for constants"+toReturn.toString());
	}
	
		
		
	/*
	 *   x = b where b is in the before set of the statement as a constant then we can simply say x = that constant also
	 * 
	 *   TODO: DONT WANT TO DO IT::::    If right expr is a unary expression  see if the stuff inside is a Local
	 *          
	 *   x = exp1 op exp2 (check if both exp1 and exp2 are int constants
	 *   
	 *   killedValuse is either the constant value which left had before this assignment stmt or null if left was Top or not in the set
	 *   
	 *    handle the special case when the inputset could not find a value because its the killed value
	 *    //eg. x = x+1 since we top x first we will never get a match IMPORTANT
	 */
	private void handleMathematical(CPFlowSet toReturn, Local left, Value right, Object killedValue){
		
		//if right expr is a local or field
		Object value = isANotTopConstantInInputSet(toReturn,right);
		if(value != null){
			//right was a local or field with a value other than top
			//dont send value SEND A CLONE OF VALUE.....IMPORTANT!!!!
			Object toSend = CPHelper.wrapperClassCloner(value);
			
			if(toSend != null){
				addOrUpdate(toReturn ,left,toSend);
			}
			
			//return if value was != null as this means that the left was a primitive local assigned some value from the right
			return;
		}
		
		//if we get here we know that right is not a local or field whose value we could find in the set
		if(right instanceof BinopExpr){
			Value op1 = ((BinopExpr)right).getOp1();
			Value op2 = ((BinopExpr)right).getOp2();
			
			Object op1Val = CPHelper.isAConstantValue(op1);
			Object op2Val = CPHelper.isAConstantValue(op2);
		    
		    if(op1Val ==null)
		    	op1Val = isANotTopConstantInInputSet(toReturn,op1);
		    
		    if(op2Val == null)
		    	op2Val = isANotTopConstantInInputSet(toReturn,op2);
		      
		    if(op1 == left){
		    	//System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>OP1 is the same as LHS");
		    	op1Val = killedValue;
		    }
		    
		    if(op2 == left){
		    	//System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>OP2 is the same as LHS");
		    	op2Val = killedValue;
		    }
		    
			if(op1Val != null && op2Val != null){
				//System.out.println("found constant values for both operands of binary expression");
				if(left.getType() instanceof IntType && op1Val instanceof Integer && op2Val instanceof Integer){
					//only caring about operations on two integers and result is an integer
					
					int op1IntValue = ((Integer)op1Val).intValue();
					int op2IntValue = ((Integer)op2Val).intValue();
					
					String tempStr = ((BinopExpr)right).getSymbol();
					if(tempStr.length()>1){
						char symbol = tempStr.charAt(1);
						//System.out.println("found symbol "+symbol+" for the operands of binary expression");
						int newValue=0;
						boolean set =false;
						switch(symbol){						
						case '+':
							//System.out.println("Adding");
							newValue = op1IntValue + op2IntValue;
							set =true;
							break;					
						case '-':
							//System.out.println("Subtracting");
							newValue = op1IntValue - op2IntValue;
							set =true;

							break;
											
						case '*':
							//System.out.println("Multiplying");
							newValue = op1IntValue * op2IntValue;
							set =true;

							break;
						}
						
						if(set){
							//we have our new value 
							Integer newValueObject = new Integer(newValue);
							addOrUpdate(toReturn ,left,newValueObject);
							return;
						}
					}
				}
			}
			else{
				//System.out.println("atleast one value is not constant so cant simplify expression");
			}	
		}		
		//System.out.println("DefinitionStmt checked right expr for mathematical stuff"+toReturn.toString());
	}
	
	
	
	/*
	 * 	Check whether it is a local or field which has a constant value (could be top) in the current inSet
	 * 
	 *  The method returns null if its not found or its TOP
	 *  Otherwise it will return the constant value
	 */
	private Object isANotTopConstantInInputSet(CPFlowSet set, Value toCheck){
		if(toCheck instanceof Local || toCheck instanceof FieldRef){
			String toCheckClassName = null;
			if(toCheck instanceof Local)
				toCheckClassName = localClassName;
			else
				toCheckClassName = ((FieldRef)toCheck).getField().getDeclaringClass().getName();
					
			Iterator it = set.toList().iterator();
			while(it.hasNext()){
				CPTuple tempTuple = (CPTuple)it.next();
				
				//check that the classNames are the same
				if( !( tempTuple.getSootClassName().equals(toCheckClassName))){
					//classNames are not the same no point in continuing with checks
					continue;
				}

				boolean tupleFound=false;
				if(tempTuple.containsLocal() && toCheck instanceof Local){
					//check they are the same Local
					Local tempLocal = tempTuple.getVariable().getLocal();
					if(tempLocal.getName().equals(   ((Local)toCheck).getName())){
						//the set does have a constant value for this local
						tupleFound=true;
					}
				}
				else if(tempTuple.containsField() && toCheck instanceof FieldRef){
					SootField toCheckField= ((FieldRef)toCheck).getField();
					SootField tempField = tempTuple.getVariable().getSootField();
					
					if(tempField.getName().equals(toCheckField.getName())){
						//the set contains a constant value for this field
						tupleFound=true;
					}
				}
				
				if(tupleFound){
					if(tempTuple.isTop())
						return null;
					else
						return tempTuple.getValue();
				}
			}
			
		}
		return null;
	}

	
	
	
	
	
	
	
	
    /*
     * over-riding the StructuredFlow Analysis implementation because we want to be able to
     * gather information about the truth value in the condition
     */
    public Object processASTIfNode(ASTIfNode node,Object input){
    	if(DEBUG_IF)
    		System.out.println("Processing if node using over-ridden process if method"+input.toString());;
    
    	input = processCondition(node.get_Condition(),input);

    	if( ! (input instanceof CPFlowSet )  ){
			throw new DavaFlowAnalysisException("not a flow set");
		}

    	CPFlowSet inputToBody = ((CPFlowSet)input).clone();
    	
    	CPTuple tuple = checkForValueHints(node.get_Condition(),inputToBody,false);
  	
    	    	
    	if(tuple != null){
    		//if not null,  is a belief going into the if branch simply add it into the input set
    		//System.out.println(">>>>>Adding tuple because of condition"+tuple.toString());
    		inputToBody.addIfNotPresentButDontUpdate(tuple);
    	}    	
    	
    	Object output1 = processSingleSubBodyNode(node,inputToBody);

    	if(DEBUG_IF)	
    		System.out.println("\n\nINPUTS TO MERGE ARE input (original):"+input.toString() +"processingBody output:"+output1.toString()+"\n\n\n");
	
    	//merge with input which tells if the cond did not evaluate to true
    	Object output2 = merge(input,output1);

    	//handle break
    	String label = getLabel(node);
	
    	Object temp= handleBreak(label,output2,node);
	
    	if(DEBUG_IF)
    		System.out.println("Exiting if node"+temp.toString());;
    
    	return temp;
    }

    

    
    
    
    
    public Object processASTIfElseNode(ASTIfElseNode node,Object input){
    	if(DEBUG_IF)
    		System.out.println("Processing IF-ELSE node using over-ridden process if method"+input.toString());;

         	if( ! (input instanceof CPFlowSet )  ){
    			throw new DavaFlowAnalysisException("not a flow set");
    		}

         	//get the subBodies
    		List<Object> subBodies = node.get_SubBodies();
    		if(subBodies.size()!=2){
    		    throw new RuntimeException("processASTIfElseNode called with a node without two subBodies");
    		}
    		//we know there is only two subBodies
    		List subBodyOne = (List)subBodies.get(0);
    		List subBodyTwo = (List)subBodies.get(1);

    		//process Condition
    		input = processCondition(node.get_Condition(),input);
    		
    		//the current input flowset is sent to both branches
    		Object clonedInput = cloneFlowSet(input);

    		CPTuple tuple = checkForValueHints(node.get_Condition(),(CPFlowSet)clonedInput,false);
     	   	if(tuple != null){
        		//if not null,  is a belief going into the if branch simply add it into the input set
        		//System.out.println(">>>>>Adding tuple because of condition into if branch"+tuple.toString());
        		((CPFlowSet)clonedInput).addIfNotPresentButDontUpdate(tuple);
        	}    	
     
    		Object output1 = process(subBodyOne,clonedInput);
    		clonedInput = cloneFlowSet(input);
    		CPTuple tuple1 = checkForValueHints(node.get_Condition(),(CPFlowSet)clonedInput,true);
    		
    	   	if(tuple1 != null){
        		//if not null,  is a belief going into the else branch simply add it into the input set
        		//System.out.println(">>>>>Adding tuple because of condition  into else branch"+tuple1.toString());
        		((CPFlowSet)clonedInput).addIfNotPresentButDontUpdate(tuple1);
        	}    	
    		Object output2 = process(subBodyTwo,clonedInput);

    		if(DEBUG_IF){
    			
    			System.out.println("\n\n  IF-ELSE   INPUTS TO MERGE ARE input (if):"+output1.toString() +" else:"+output2.toString()+"\n\n\n");
    		}	
    		Object temp=merge(output1,output2);

    		//notice we handle breaks only once since these are breaks to the same label or same node
    		String label = getLabel(node);
    		output1 = handleBreak(label,temp,node);
    		if(DEBUG_IF){
    			System.out.println("Exiting ifelse node"+output1.toString());;
    		}

    		return output1;
    	
    	}
         

    
    
    
    
    /*
     * The isElseBranch flag is true if the caller is the else branch of the ifelse statement. 
     * In that case we might be able to send something for the else branch
     */
    public CPTuple checkForValueHints(ASTCondition cond, CPFlowSet input, boolean isElseBranch){
    	if(cond instanceof ASTUnaryCondition){
    		//check for lone boolean  if(notDone)
    		ASTUnaryCondition unary = (ASTUnaryCondition)cond;
    		Value unaryValue = unary.getValue();

    		boolean NOTTED=false;    		
    		//Get the real value if this is a notted expression 
    		if(unaryValue instanceof DNotExpr){
    			unaryValue =((DNotExpr)unaryValue).getOp();
    			NOTTED=true;
    		}
    		
    		if( ! (unaryValue instanceof Local) ){
    			//since we only track locals we cant possibly add info to the inset
    			return null;
    		}

    		//the unary value is a local add the value to the inset which woul dbe present in the if branch
    		CPVariable variable = new CPVariable((Local)unaryValue);
    		
    		//since NOTTED true means the if branch has variable with value false and vice verse
    		if(!isElseBranch){
    			//we are in the if branch hence notted true would mean the variable is actually false here
        		Boolean boolVal = new Boolean(!NOTTED);
        		return new CPTuple(localClassName,variable,boolVal);    		
    		}
    		else{
    			//in the else branch NOTTED true means the variable is true
        		Boolean boolVal = new Boolean(NOTTED);
        		return new CPTuple(localClassName,variable,boolVal);    		
    		}
    	}
    	else if(cond instanceof ASTBinaryCondition){
    		ASTBinaryCondition binary = (ASTBinaryCondition)cond;
    		ConditionExpr expr = binary.getConditionExpr();
    		
    		Boolean equal = null;
    		
    		String symbol = expr.getSymbol();
    		if(symbol.indexOf("==") >-1){
    			//System.out.println("!!!!!!!!!1 FOUND == in binary comparison operaiton");
    			equal = new Boolean(true);
    		}
    		else if(symbol.indexOf("!=") > -1){
    			equal = new Boolean(false);
    			//System.out.println("!!!!!!!!!!!!!! FOUND != in binary comparison operaiton");
    		}
    		else{
    			//a symbol we are not interested in
    			//System.out.println("symbol is"+symbol);
    			return null;
    		}


   			//we have a comparison the truth value of equal tells whether we are doing == or !=
    		Value a = expr.getOp1();
    		Value b = expr.getOp2();

    		//see if its possible to deduce a hint from these values
    		CPTuple tuple = createCPTupleIfPossible(a,b,input);
    		
    		//if the tuple is not created
    		if(tuple == null)
    			return null;
    		
    		//we have to make sure is that the == and != are taken into account
    		if(equal.booleanValue()){
    			//using equality comparison a == b   this then means in the if branch a is in fact equal to b
    			if(!isElseBranch)
    				return tuple;
    			else
    				return null;
    		}
    		else{
    			if(isElseBranch)
    				return tuple;
    			else
    				return null;
    		}
    		    		
    	}  
    	return null;  	
    }
	
    
	

    
    
    /*
     * Should create the final tuple to add
     * 
	 * a == b
	 * 
	 * case 1   a is constant                  b is constant                   dont give a damm
	 * case 2   a is constant                  b is a local                   useful
	 * case 3   a is a local                   b is a constant                 useful
	 * case 4   a is a local or sootfield      b is a local or sootfield       useful if one of them is in the inset
	 * 
	 */
    public CPTuple createCPTupleIfPossible(Value a,Value b,CPFlowSet input){
    	Object aVal = CPHelper.isAConstantValue(a);
		Object bVal = CPHelper.isAConstantValue(b);
		

		if(aVal != null && bVal != null){
			//both are constants dont want to do anything..case 1
			return null;			
		}

    	CPVariable cpVar= null;
		Object constantToUse = null;

		if(aVal == null && bVal == null){
			//both are not constants but one of their values could be known in the inset ... case 4
			//System.out.println("a:"+a+" is not a constant b:"+b+" is not");
			
			//check the input set to see if either a or b have known beliefs. 
			//its useful if one and only one of the two has a known belief
			Object av1 = isANotTopConstantInInputSet(input,a);
			Object av2 = isANotTopConstantInInputSet(input,b);
			if(av1 == null && av2 == null){
				//either top or not present hence useless
				return null;
			}
			else if (av1 == null && av2 != null){
				//no value of a found but value of b was found   <classname, a,b>
				//System.out.println("From INSET: a:"+a+" is not a constant b "+b+" is" );
				if( !  ( a instanceof Local && ((Local)a).getType() instanceof PrimType )  ){
					//we only hanlde primitive locals
					return null;
				}
				cpVar = new CPVariable((Local)a);
				constantToUse = av2;
			}
			else if(av1 != null && av2 == null){
				//no value of b found but value of a was found   <classname, b,a>
				//System.out.println("From INSET: a:"+a+" is a constant b "+b+" is not" );
				if( !  ( b instanceof Local && ((Local)b).getType() instanceof PrimType )  ){
					//we only hanlde primitive locals
					return null;
				}
				cpVar = new CPVariable((Local)b);
				constantToUse = av1;
			}
		}
		else if(aVal != null && bVal == null){
			 // CASE 2: a is a constant and b is not so we have a chance of entering a tuple <className,b,a> maybe
			//System.out.println("a:"+a+" is a constant b:"+b+" is not");
			
			if( !  ( b instanceof Local && ((Local)b).getType() instanceof PrimType )  ){
				//we only hanlde primitive locals
				return null;
			}
			
			//able to create cpVar
			cpVar = new CPVariable((Local)b);
			constantToUse=aVal;
		}
		else if(aVal == null && bVal != null){
			//CASE 3: a is not a constant but b is a constant so we have a chance of entering a tuple <className,a,b>
			//System.out.println("a:"+a+" is not a constant b:"+b+" is ");
			
			if( !  ( a instanceof Local && ((Local)a).getType() instanceof PrimType )  ){
				//we only hanlde primitive locals
				return null;
			}
			
			//able to create cpVar
			cpVar = new CPVariable((Local)a);
			constantToUse=bVal;
			
		}

		//if cpVar is not null and constantToUse is not null thats good
		if(cpVar != null && constantToUse != null){
			//create a CPTuple which contains the belief for cpVar with constantToUse we will for sure have going into the if branch
			//we know cpVar is always a local since we create it only for locals
			
			//need to see if constant is supposed to be a boolean (isAConstantValue returns an Integer for a boolean)
			if( cpVar.getLocal().getType() instanceof BooleanType){
				if( ! (constantToUse instanceof Integer ) ){
					//booleans are represented by Integeres in the isConstantValue method what happened here???
					return null;
				}
				
				Integer tempValue = (Integer)constantToUse;
				if(tempValue.intValue() == 0)
					constantToUse = new Boolean(false);
				else
					constantToUse = new Boolean(true);
			}
			
			//ready to create the CPTuple
			
			return new CPTuple(localClassName,cpVar,constantToUse);
		}
    	return null;
    }
    

    	
    
	        
	/*
	 * TODO some other time
	 * 
	 public Object processASTSwitchNode(ASTSwitchNode node,Object input){
		
	}*/

    
}	