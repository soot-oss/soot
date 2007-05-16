package soot.dava.toolkits.base.AST.transformations;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import soot.Local;
import soot.SootField;
import soot.Value;
import soot.ValueBox;
import soot.dava.internal.AST.ASTAggregatedCondition;
import soot.dava.internal.AST.ASTBinaryCondition;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTUnaryCondition;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.dava.toolkits.base.AST.structuredAnalysis.CP;
import soot.dava.toolkits.base.AST.structuredAnalysis.CPFlowSet;
import soot.dava.toolkits.base.AST.structuredAnalysis.CPHelper;
import soot.jimple.FieldRef;
import soot.jimple.Stmt;
	
/*
 * The traversal utilizes the results of the CP (constant propagation analysis) to substitute uses
 * of locals where ever possible. Note we also have information about the constant fields in the program but
 * we are not going to de-inline those fields because it is thought that refereing to the field as a TYPE-DEF 
 * gives more info than the actual value
 * 
 * 
  Need to be very clear when a local can be used
  It can be used in the following places:
  a, a conditional in if, ifelse, 
  a1 while , do while, for condition  (after set of these will be used since those are the vars true at the start of the loop
   i.e. when the loop has not executed and also true at the end of the loop
   
  b, in the for init or update
  c, in a switch choice 
  d, in a syncrhnoized block //wont do dont think we need to since this is a local
     and synching is always done on objects and we are not tracking objects 
  d, in a statement
 *
 */	
	
public class CPApplication extends DepthFirstAdapter{
	CP cp = null;
	String className = null;
	
	public CPApplication(ASTMethodNode AST, HashMap<String, Object> constantValueFields, HashMap<String, SootField> classNameFieldNameToSootFieldMapping){
		className = AST.getDavaBody().getMethod().getDeclaringClass().getName();
		cp = new CP(AST,constantValueFields, classNameFieldNameToSootFieldMapping);
	}

	public CPApplication(boolean verbose,ASTMethodNode AST,HashMap<String, Object> constantValueFields, HashMap<String, SootField> classNameFieldNameToSootFieldMapping){
		super(verbose);
		className = AST.getDavaBody().getMethod().getDeclaringClass().getName();
		cp = new CP(AST,constantValueFields, classNameFieldNameToSootFieldMapping);
	}

	
	
	
	

	public void inASTSwitchNode(ASTSwitchNode node){
		Object obj = cp.getBeforeSet(node);
	    if(obj == null )
	    	return;
	    if(! (obj instanceof CPFlowSet ))
	    	return;
	    
	    //before set is a non null CPFlowSet
	    CPFlowSet beforeSet = (CPFlowSet)obj;
		

	    Value key = node.get_Key();
		if(key instanceof Local){
			Local useLocal = (Local)key;
			//System.out.println("switch key is a local: "+useLocal);
			Object value = beforeSet.contains(className,useLocal.toString());
			if(value != null){
				//System.out.println("switch key Local "+useLocal+"is present in before set with value"+value);
				//create constant value for the value and replace this local use with the constant value use
				Value newValue = CPHelper.createConstant(value);
				if(newValue != null){
					//System.out.println("Substituted the switch key local use with the constant value"+newValue);
					node.set_Key(newValue);
				}
				else{
					//System.out.println("FAILED TO Substitute the local use with the constant value");
				}
			}
		}
		else if (key instanceof FieldRef){
			FieldRef useField = (FieldRef)key;
			//System.out.println("switch key is a FieldRef which is: "+useField);
			SootField usedSootField = useField.getField();
			Object value = beforeSet.contains(usedSootField.getDeclaringClass().getName(),usedSootField.getName().toString());
			if(value != null){
				//System.out.println("FieldRef "+usedSootField+"is present in before set with value"+value);
				//create constant value for the value and replace this local use with the constant value use
				Value newValue = CPHelper.createConstant(value);
				if(newValue != null){
					//System.out.println("Substituted the constant field ref use with the constant value"+newValue);
					node.set_Key(newValue);
				}
				else{
					//System.out.println("FAILED TO Substitute the constant field ref use with the constant value");
				}
			}
			
	    		
		}		
	}
	

	
	
	
	
	
	
	
	
	
	
	public void inASTForLoopNode(ASTForLoopNode node){
		/*
		 * For the init part we should actually use 
		 * the before set for each init stmt
		 */
		Iterator<Object> it = node.getInit().iterator();
		while(it.hasNext()){
			AugmentedStmt as = (AugmentedStmt)it.next();
			Stmt s = as.get_Stmt();    
			List useBoxes = s.getUseBoxes();

			Object obj = cp.getBeforeSet(s);
    	    if(obj == null )
    	    	continue;
    	    if(! (obj instanceof CPFlowSet ))
    	    	continue;
    	    
    	    //before set is a non null CPFlowSet
    	    CPFlowSet beforeSet = (CPFlowSet)obj;
    	    
    	    //System.out.println("Init Statement: "+s);
    	    //System.out.println("Before set is: "+beforeSet.toString());
    	    
    	    /*
    	     * get all use boxes see if their value is determined 
    	     * from the before set if yes replace them
    	     */
    	    substituteUses(useBoxes,beforeSet);
		}


		//get after set for the condition and update
		Object obj = cp.getAfterSet(node);
    	
	    if(obj == null )
	    	return;
	    if(! (obj instanceof CPFlowSet ))
	    	return;
	    
	    //after set is a non null CPFlowSet
	    CPFlowSet afterSet = (CPFlowSet)obj;
	    
	    
	    //conditon
		ASTCondition cond = node.get_Condition();
		
	    //System.out.println("For Loop with condition: "+cond);
	    //System.out.println("After set is: "+afterSet.toString());
    
	    changedCondition(cond,afterSet);		

	    
	    //update
	    it = node.getUpdate().iterator();
	    while(it.hasNext()){
    	    AugmentedStmt as = (AugmentedStmt)it.next();
    	    Stmt s = as.get_Stmt();
    	    
    	    List useBoxes = s.getUseBoxes();
    	    
    	    //System.out.println("For update Statement: "+s);
    	    //System.out.println("After set is: "+afterSet.toString());
    	    
    	    /*
    	     * get all use boxes see if their value is determined 
    	     * from the before set if yes replace them
    	     */
    	    substituteUses(useBoxes,afterSet);
    	}

    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
		
	}
	
	public void inASTWhileNode(ASTWhileNode node){
		Object obj = cp.getAfterSet(node);
    	
	    if(obj == null )
	    	return;
	    if(! (obj instanceof CPFlowSet ))
	    	return;
	    
	    //after set is a non null CPFlowSet
	    CPFlowSet afterSet = (CPFlowSet)obj;
	    
		ASTCondition cond = node.get_Condition();
		
	    //System.out.println("While Statement with condition: "+cond);
	    //System.out.println("After set is: "+afterSet.toString());
    
	    changedCondition(cond,afterSet);		
	}
	
	
	
	
	
	public void inASTDoWhileNode(ASTDoWhileNode node){
		Object obj = cp.getAfterSet(node);
    	
	    if(obj == null )
	    	return;
	    if(! (obj instanceof CPFlowSet ))
	    	return;
	    
	    //after set is a non null CPFlowSet
	    CPFlowSet afterSet = (CPFlowSet)obj;
	    
		ASTCondition cond = node.get_Condition();
		
	    //System.out.println("Do While Statement with condition: "+cond);
	    //System.out.println("After set is: "+afterSet.toString());
    
	    changedCondition(cond,afterSet);		
	}
	
	
	
	
    public void inASTIfNode(ASTIfNode node){
    	//System.out.println(node);
    	Object obj = cp.getBeforeSet(node);
    	
	    if(obj == null )
	    	return;
	    if(! (obj instanceof CPFlowSet ))
	    	return;
	    
	    //before set is a non null CPFlowSet
	    CPFlowSet beforeSet = (CPFlowSet)obj;
	    
	    //System.out.println("Printing before Set for IF"+beforeSet.toString());
	    
		ASTCondition cond = node.get_Condition();
		
	    //System.out.println("If Statement with condition: "+cond);
	    //System.out.println("Before set is: "+beforeSet.toString());
    
	    changedCondition(cond,beforeSet);		
	}

    
    public void inASTIfElseNode(ASTIfElseNode node){
    	Object obj = cp.getBeforeSet(node);
    	
	    if(obj == null )
	    	return;
	    if(! (obj instanceof CPFlowSet ))
	    	return;
	    
	    //before set is a non null CPFlowSet
	    CPFlowSet beforeSet = (CPFlowSet)obj;
	    
		ASTCondition cond = node.get_Condition();
		
	    //System.out.println("IfElse Statement with condition: "+cond);
	    //System.out.println("Before set is: "+beforeSet.toString());
    
	    changedCondition(cond,beforeSet);
	}

    
    
	/*
     * Given a unary/binary or aggregated condition this method is used 
     * to find all the useBoxes or locals or fieldref in the case of unary conditions
     * and then the set is checked for appropriate substitutions
     */
    public ASTCondition changedCondition(ASTCondition cond, CPFlowSet set){
    	if(cond instanceof ASTAggregatedCondition){
    		ASTCondition left = changedCondition(((ASTAggregatedCondition)cond).getLeftOp(),set);
    		ASTCondition right = changedCondition(((ASTAggregatedCondition)cond).getRightOp(),set);
    		((ASTAggregatedCondition)cond).setLeftOp(left);
    		((ASTAggregatedCondition)cond).setRightOp(right);
    		//System.out.println("New condition is: "+cond);
    		return cond;
    	}
    	else if(cond instanceof ASTUnaryCondition){
    		Value val = ((ASTUnaryCondition)cond).getValue();
    		if(val instanceof Local){
    			Object value = set.contains(className,((Local)val).toString());
    			if(value != null){
    				//System.out.println("if Condition Local "+((Local)val)+"is present in before set with value"+value);
    				//create constant value for the value and replace this local use with the constant value use
    				Value newValue = CPHelper.createConstant(value);
    				if(newValue != null){
    					//System.out.println("Substituted the local use with the constant value"+newValue);
    					((ASTUnaryCondition)cond).setValue(newValue);
    				}
    				else{
    					//System.out.println("FAILED TO Substitute the local use with the constant value");
    				}
    			}
    		}
    		else if (val instanceof FieldRef){
    			FieldRef useField = (FieldRef)val;
    			SootField usedSootField = useField.getField();
    			Object value = set.contains(usedSootField.getDeclaringClass().getName(),usedSootField.getName().toString());
    			if(value != null){
    				//System.out.println("if condition FieldRef "+usedSootField+"is present in before set with value"+value);
    				//create constant value for the value and replace this field use with the constant value use
    				Value newValue = CPHelper.createConstant(value);
    				if(newValue != null){
    					//System.out.println("Substituted the constant field ref use with the constant value"+newValue);
    					((ASTUnaryCondition)cond).setValue(newValue);
    				}
    				else{
    					//System.out.println("FAILED TO Substitute the constant field ref use with the constant value");
    				}
    			}
    		}
    		else{
    			substituteUses(val.getUseBoxes(),set);
    		}
    		//System.out.println("New condition is: "+cond);
    		return cond;
    	}
    	else if(cond instanceof ASTBinaryCondition){
    		//get uses from binaryCondition
    		Value val = ((ASTBinaryCondition)cond).getConditionExpr();
    		substituteUses(val.getUseBoxes(),set);
    		
    		//System.out.println("New condition is: "+cond);
    		return cond;
    	}
    	else{
    		throw new RuntimeException("Method getUseList in ASTUsesAndDefs encountered unknown condition type");
    	}
    }

    
    
    
	
	
    public void inASTStatementSequenceNode(ASTStatementSequenceNode node){
    	List<Object> statements = node.getStatements();
    	Iterator<Object> it = statements.iterator();
    	
    	while(it.hasNext()){
    	    AugmentedStmt as = (AugmentedStmt)it.next();
    	    Stmt s = as.get_Stmt();
    	    
    	    List useBoxes = s.getUseBoxes();
    	    
    	    Object obj = cp.getBeforeSet(s);
    	    
    	    if(obj == null )
    	    	continue;
    	    if(! (obj instanceof CPFlowSet ))
    	    	continue;
    	    
    	    //before set is a non null CPFlowSet
    	    CPFlowSet beforeSet = (CPFlowSet)obj;
    	    
    	    //System.out.println("Statement: "+s);
    	    //System.out.println("Before set is: "+beforeSet.toString());
    	    
    	    /*
    	     * get all use boxes see if their value is determined from the before set
    	     * if yes replace them
    	     */
    	    substituteUses(useBoxes,beforeSet);
    	    
    	}
    }

	    
    public void substituteUses(List useBoxes, CPFlowSet beforeSet){
    	Iterator useIt = useBoxes.iterator();
    	while(useIt.hasNext()){
    		Object useObj = useIt.next();
    		Value use = ((ValueBox)useObj).getValue();
    		if(use instanceof Local){
    			Local useLocal = (Local)use;
    			//System.out.println("local is: "+useLocal);
    			Object value = beforeSet.contains(className,useLocal.toString());
    			if(value != null){
    				//System.out.println("Local "+useLocal+"is present in before set with value"+value);
    				//create constant value for the value and replace this local use with the constant value use
    				Value newValue = CPHelper.createConstant(value);
    				if(newValue != null){
    					//System.out.println("Substituted the local use with the constant value"+newValue);
    					((ValueBox)useObj).setValue(newValue);
    				}
    				else{
    					//System.out.println("FAILED TO Substitute the local use with the constant value");
    				}
    			}
    		}
    		else if (use instanceof FieldRef){
    			FieldRef useField = (FieldRef)use;
    			//System.out.println("FieldRef is: "+useField);
    			SootField usedSootField = useField.getField();
    			Object value = beforeSet.contains(usedSootField.getDeclaringClass().getName(),usedSootField.getName().toString());
    			if(value != null){
    				//System.out.println("FieldRef "+usedSootField+"is present in before set with value"+value);
    				//create constant value for the value and replace this local use with the constant value use
    				Value newValue = CPHelper.createConstant(value);
    				if(newValue != null){
    					//System.out.println("Substituted the constant field ref use with the constant value"+newValue);
    					((ValueBox)useObj).setValue(newValue);
    				}
    				else{
    					//System.out.println("FAILED TO Substitute the constant field ref use with the constant value");
    				}
    			}
    			
    	    		
    		}
    	}
    }
}
