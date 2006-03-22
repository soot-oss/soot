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
package soot.dava.toolkits.base.AST.transformations;

import soot.BooleanType;
import soot.Value;
import soot.dava.internal.AST.ASTAggregatedCondition;
import soot.dava.internal.AST.ASTAndCondition;
import soot.dava.internal.AST.ASTBinaryCondition;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTControlFlowNode;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTOrCondition;
import soot.dava.internal.AST.ASTUnaryCondition;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.javaRep.DIntConstant;
import soot.dava.internal.javaRep.DNotExpr;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.ConditionExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;

	/*
	 * 	5 == 5  true   DONE              
	 *  5 != 5 false   DONE (all other relational operators done)
	 *  
	 *    !true  --> false DONE
	 *    !false --> true DONE
	 *  
	 *   DONE WHEN one or both are constants (did all combinations)
	 *    true || b   ---->   true
	 *    true && b   ----->  b
	 *    false || b  -----> b
	 *    false && b -------> false              
	 *  
	 *  if ( (z0 && z1)  ||  ( ! ( ! (z2) ||  ! (z3)) ) )     
	 *                 ---> if ( (z0 && z1)  ||   (z2 && z3)  )     DONE
	 *    
	 *    
	 *    
	 * TODO currently only doing primtype comparison of same types not handled are following types
	 *        long <= int
	 *        int <=long
	 *         bla bla
	 *         
	 *
	 * TODO IDEA     if(io==0 && io==0) --> if(io==0)
	 */
public class SimplifyConditions extends DepthFirstAdapter {
	public static boolean DEBUG=false;
	public boolean changed=false;
	
	public SimplifyConditions() {
		super();
	}

	public SimplifyConditions(boolean verbose) {
		super(verbose);
	}
	

	public void fixedPoint(ASTControlFlowNode node){
		
		ASTCondition returned;
		do{
			if(DEBUG)
				System.out.println("Invoking simplify");
			changed=false;
			ASTCondition cond = node.get_Condition();
			returned = simplifyTheCondition(cond);
			if(returned != null)
				node.set_Condition(returned);
		}while(changed);
	}
	
    public void outASTIfNode(ASTIfNode node){
    	fixedPoint(node);
    }
    
    
    
    public void outASTIfElseNode(ASTIfElseNode node){
    	fixedPoint(node);
    }
    
    
    
    public void outASTWhileNode(ASTWhileNode node){
    	fixedPoint(node);
    }
    
    
    
    public void outASTDoWhileNode(ASTDoWhileNode node){
    	fixedPoint(node);
    }
    
    
    
    public void outASTForLoopNode(ASTForLoopNode node){
    	fixedPoint(node);
    }

    
    
    
    
    /*
     * !z0 && !z1  ---->   !(z0 || z1)
     * !z0 || !z1  ---->   !(z0 && z1)
     * 
     * Send null if no change else send new condition CONDITION
     */
    public ASTCondition applyDeMorgans(ASTAggregatedCondition aggCond){
    	ASTCondition left = aggCond.getLeftOp();
    	ASTCondition right = aggCond.getRightOp();
    	
    	if(aggCond.isNotted() && left instanceof ASTBinaryCondition && right instanceof ASTBinaryCondition){
    		//we can remove the not sign by simply flipping the two conditions
    		//    ! (  x==y &&  a<b )
    		left.flip();
    		right.flip();
    		if(aggCond instanceof ASTAndCondition)
    			aggCond = new ASTOrCondition(left,right);
    		else
    			aggCond = new ASTAndCondition(left,right);
    		
    		return aggCond;
    	}

    	if(    (  left.isNotted() && right.isNotted() && (  !(left instanceof ASTBinaryCondition) && !(right instanceof ASTBinaryCondition) ) ) 	
    			|| (left.isNotted() && aggCond.isNotted() && !(left instanceof ASTBinaryCondition) ) 	   
    			|| (right.isNotted() && aggCond.isNotted()  && !(right instanceof ASTBinaryCondition) ) ){
    		//both are notted and atleast one is not a binaryCondition
    		left.flip();
    		right.flip();

    		ASTAggregatedCondition newCond;
    		if(aggCond instanceof ASTAndCondition)
    			newCond = new ASTOrCondition(left,right);
    		else
    			newCond = new ASTAndCondition(left,right);

    		if(aggCond.isNotted())
    			return newCond;
    		else{
    			newCond.flip();
    			return newCond;
    		}
    	}
    	
    	return null;
    }
    
    
    
    /*
     * When this method is invoked we are sure that there are no occurences of !true or !false since
     * this is AFTER doing depth first of the children so the unaryCondition must have simplified the above
     * 
     * Return Null if no change else return changed condition
     */
    public ASTCondition simplifyIfAtleastOneConstant(ASTAggregatedCondition aggCond){
		ASTCondition left = aggCond.getLeftOp();
		ASTCondition right = aggCond.getRightOp();
		
		
		Boolean leftBool = null;
		Boolean rightBool = null;
		if (left instanceof ASTUnaryCondition)
			leftBool = isBooleanConstant(((ASTUnaryCondition) left).getValue());

		if (right instanceof ASTUnaryCondition)
			rightBool = isBooleanConstant(((ASTUnaryCondition) right).getValue());

		/*
		 *  a && b NOCHANGE    DONE
		 *  b && a NOCHANGE	 DONE
		 *              
		 *  a || b NOCHANGE DONE 
		 *  b || a NOCHANGE DONE
		 *  
		 */
		if (leftBool == null && rightBool == null) {
			// meaning both are not constants
			return null;
		}

    	if(aggCond instanceof ASTAndCondition){
    		/*
    		 *    true && true ---> true         DONE
    		 *    true && false --> false        DONE
    		 *    false && false ---> false      DONE
    		 *    false && true --> false        DONE
    		 *    
    		 *    true && b   ----->  b          DONE
    		 *    false && b -------> false      DONE
    		 *    
    		 *    b && true  ---> b              DONE
    		 *    b && false ---> b && false (since b could have side effects and the overall condition has to be false)      DONE
    		 *    
    		 */

    		
    		if (leftBool != null && rightBool != null) {
				// meaning both are constants
				if (leftBool.booleanValue() && rightBool.booleanValue()) {
					// both are true
					return new ASTUnaryCondition(DIntConstant.v(1, BooleanType.v()));
				} else {
					// atleast one of the two is false
					return new ASTUnaryCondition(DIntConstant.v(0, BooleanType.v()));
				}
			}

			if (leftBool != null) {
				// implicityly means that rigthBool is null since the above
				// condition passed
				if (leftBool.booleanValue()) {
					// left bool is a true meaning we have to evaluate right
					// condition.......just return the right condition
					return right;
				} else {
					// left bool is false meaning no need to continue since we
					// will never execute the right condition
					// return a unary false
					return new ASTUnaryCondition(DIntConstant.v(0, BooleanType.v()));
				}
			}

			if (rightBool != null) {
				// implicityly means that the leftBool is null
				if (rightBool.booleanValue()) {
					// rightBool is true so it all depends on left
					return left;
				} else {
					// although we know the condition overall is false we cant
					// remove the leftBool since there might be side effects
					return aggCond;
				}
			}
    		
    	
    	}
    	else if(aggCond instanceof ASTOrCondition){
    		/*
			 * 
			 * true || false ---> true    DONE 
			 * true || true --> true      DONE
			 * false || true --> true     DONE
			 * false || false ---> false  DONE
			 * 
			 * 
			 * true || b ----> true DONE
			 * false || b -----> b   DONE
			 *   
			 * b || true ---> b || true .... although we know the condition is true we have to evaluate b because of possible side effects   DONE 
			 * b || false ---> b     DONE
			 * 
			 */    		
			if (leftBool != null && rightBool != null) {
				// meaning both are constants
				if ( !leftBool.booleanValue() && !rightBool.booleanValue()) {
					// both are false
					return new ASTUnaryCondition(DIntConstant.v(0, BooleanType.v()));
				} else {
					// atleast one of the two is true
					return new ASTUnaryCondition(DIntConstant.v(1, BooleanType.v()));
				}
			}
    		
    		
    		
    		
			if (leftBool != null) {
				// implicityly means that rigthBool is null since the above
				// condition passed
				if (leftBool.booleanValue()) {
					//left bool is true that means we will stop evaluation of condition, just return true
					return new ASTUnaryCondition(DIntConstant.v(1, BooleanType.v()));
				}
				else{
					//left bool is false so we have to continue evaluating right
					return right;
				}
			}

			if (rightBool != null) {
				// implicityly means that the leftBool is null
				if (rightBool.booleanValue()) {
					// rightBool is true but leftBool must be evaluated beforehand
					return aggCond;
				} else {
					//rightBool is false so everything depends on left
					return left;
				}
			}   		
    	}
    	else
    		throw new RuntimeException("Found unknown aggregated condition");
    	
    	return null;    	
    }
	

    
    
    
    
    /*
     * Method returns null if the Value is not a constant or not a boolean constant
     * return true if the constant is true
     * return false if the constant is false
     */
    public Boolean isBooleanConstant(Value internal){
    	
    	if(! (internal instanceof DIntConstant))
    		return null;
				
    	if(DEBUG)
    		System.out.println("Found Constant");
    	
    	DIntConstant intConst = (DIntConstant)internal;
    		
    	if(! (intConst.type instanceof BooleanType) )
    		return null;
    				
    	//either true or false
    	if(DEBUG)
    		System.out.println("Found Boolean Constant");

    	if(intConst.value == 1){
    		return new Boolean(true);
    	}
    	else if(intConst.value == 0){
    		return new Boolean(false);
    	}
    	else
    		throw new RuntimeException("BooleanType found with value different than 0 or 1");
    }
    
    
    
    /*
	 * In a loop keep simplifying the condition as much as possible
	 * 
	 */
	public ASTCondition simplifyTheCondition(ASTCondition cond){
		if(cond instanceof ASTAggregatedCondition){
			ASTAggregatedCondition aggCond = (ASTAggregatedCondition)cond;
			ASTCondition leftCond = simplifyTheCondition(aggCond.getLeftOp());
			ASTCondition rightCond = simplifyTheCondition(aggCond.getRightOp());
			
			 //if returned values are non null then set leftop /rightop to new condition
			if(leftCond != null){
				aggCond.setLeftOp(leftCond);
			}

			if(rightCond != null){
				aggCond.setRightOp(rightCond);
			}

			
			ASTCondition returned = simplifyIfAtleastOneConstant(aggCond);
			if(returned != null){
				changed=true;
				return returned;
			}
				
			returned = applyDeMorgans(aggCond);
			if(returned != null){
				changed = true;
				return returned;
			}
			
	    	return aggCond;
		}
		else if(cond instanceof ASTUnaryCondition){
			//dont do anything with unary conditions
			ASTUnaryCondition unary = (ASTUnaryCondition)cond;
			
			/*
			 * if unary is a noted constant simplify it
			 * !true to be converted to false
			 * !false to be converted to true
			 */
			Value unaryVal = unary.getValue();
			if(unaryVal instanceof DNotExpr){
				if(DEBUG) System.out.println("Found NotExpr in unary COndition"+unaryVal);
				
				DNotExpr notted = (DNotExpr)unaryVal;
				Value internal = notted.getOp();
				
				Boolean isIt = isBooleanConstant(internal);
				if(isIt != null){
					//is a boolean constant truth value will give whether its true or false
					//convert !true to false
					if(isIt.booleanValue()){
						//true
						if(DEBUG) System.out.println("CONVERTED !true to false");
						changed=true;
						return new ASTUnaryCondition( DIntConstant.v(0,BooleanType.v()));
					}
					else if(!isIt.booleanValue()){
						//false
						if(DEBUG)	System.out.println("CONVERTED !false to true");
						changed=true;
						return new ASTUnaryCondition( DIntConstant.v(1,BooleanType.v()));
					}
					else
						throw new RuntimeException("BooleanType found with value different than 0 or 1");
				}
				else{
					if(DEBUG)System.out.println("Not boolean type");
				}
			}
			return unary;
		}
		else if(cond instanceof ASTBinaryCondition){
			ASTBinaryCondition binary = (ASTBinaryCondition)cond;
			ConditionExpr expr = binary.getConditionExpr();
			
			//returns null if no change
			ASTUnaryCondition temp = evaluateBinaryCondition(expr);
			if(DEBUG)
				System.out.println("changed binary condition "+cond +" to" + temp);
			if(temp != null)
				changed=true;
			return temp;			
		}
		else{
			throw new RuntimeException("Method getUseList in ASTUsesAndDefs encountered unknown condition type");
		}
	}
	
	
	
	
	//return condition if was able to simplify (convert to a boolean true or false) else null
	public ASTUnaryCondition evaluateBinaryCondition(ConditionExpr expr){
		String symbol = expr.getSymbol();
		
		int op =-1;
		if(symbol.indexOf("==")>-1){
			if(DEBUG)
				System.out.println("==");
			op=1;
		}
		else if(symbol.indexOf(">=")>-1){
			if(DEBUG)
				System.out.println(">=");
			op=2;			
		}
		else if(symbol.indexOf('>')>-1){
			if(DEBUG)
				System.out.println(">");
			op=3;
		}
		else if(symbol.indexOf("<=")>-1){
			if(DEBUG)
				System.out.println("<=");
			op=4;
		}
		else if(symbol.indexOf('<')>-1){
			if(DEBUG)
				System.out.println("<");
			op=5;
		}
		else if(symbol.indexOf("!=")>-1){
			if(DEBUG)
				System.out.println("!=");
			op=6;
		}
		
		
		Value leftOp = expr.getOp1();
		Value rightOp = expr.getOp2();
		
		Boolean result=null;
		if(leftOp instanceof LongConstant  && rightOp instanceof LongConstant){
			if(DEBUG)
				System.out.println("long constants!!");
			long left = ((LongConstant)leftOp).value;
			long right = ((LongConstant)rightOp).value;
			result = longSwitch(op,left,right);
		}
		else if(leftOp instanceof DoubleConstant  && rightOp instanceof DoubleConstant){
			double left = ((DoubleConstant)leftOp).value;
			double right = ((DoubleConstant)rightOp).value;
			result = doubleSwitch(op,left,right);
		}
		else if(leftOp instanceof FloatConstant  && rightOp instanceof FloatConstant){
			float left = ((FloatConstant)leftOp).value;
			float right = ((FloatConstant)rightOp).value;
			result = floatSwitch(op,left,right);
		}
		else if(leftOp instanceof IntConstant  && rightOp instanceof IntConstant){
			int left = ((IntConstant)leftOp).value;
			int right = ((IntConstant)rightOp).value;
			result = intSwitch(op,left,right);
		}
	
		if(result!=null){
			if(result.booleanValue())
				return new ASTUnaryCondition( DIntConstant.v(1,BooleanType.v()));
			else
				return new ASTUnaryCondition( DIntConstant.v(0,BooleanType.v()));
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Boolean longSwitch(int op,long l,long r){
		switch(op){
		case 1:
			// ==
			if (l == r)
				return new Boolean(true);
			else
				return new Boolean(false);
			
			
		case 2:
			// >=
			if(l >= r)
				return new Boolean(true);
			else
				return new Boolean(false);

		case 3:
			// >
			if(l > r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		case 4:
			// <=
			if(l <= r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		case 5:
			// <
			
			if(l < r)
				return new Boolean(true);
			else
				return new Boolean(false);

		case 6:
			// !=
			if(l != r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		default:
			if(DEBUG)
				System.out.println("got here");
			return null;
		}
	}
	
	
	
	
	
	
	
	
	public Boolean doubleSwitch(int op,double l,double r){
		switch(op){
		case 1:
			// ==
			if (l == r)
				return new Boolean(true);
			else
				return new Boolean(false);
			
			
		case 2:
			// >=
			if(l >= r)
				return new Boolean(true);
			else
				return new Boolean(false);

		case 3:
			// >
			if(l > r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		case 4:
			// <=
			if(l <= r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		case 5:
			// <
			
			if(l < r)
				return new Boolean(true);
			else
				return new Boolean(false);

		case 6:
			// !=
			if(l != r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		default:
			return null;
		}
	}
	

	
	
    
	public Boolean floatSwitch(int op,float l,float r){
		switch(op){
		case 1:
			// ==
			if (l == r)
				return new Boolean(true);
			else
				return new Boolean(false);
			
			
		case 2:
			// >=
			if(l >= r)
				return new Boolean(true);
			else
				return new Boolean(false);

		case 3:
			// >
			if(l > r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		case 4:
			// <=
			if(l <= r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		case 5:
			// <
			
			if(l < r)
				return new Boolean(true);
			else
				return new Boolean(false);

		case 6:
			// !=
			if(l != r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		default:
			return null;
		}
	}
    

	
	
	
	
	
	
	
	public Boolean intSwitch(int op,int l,int r){
		switch(op){
		case 1:
			// ==
			if (l == r)
				return new Boolean(true);
			else
				return new Boolean(false);
			
			
		case 2:
			// >=
			if(l >= r)
				return new Boolean(true);
			else
				return new Boolean(false);

		case 3:
			// >
			if(l > r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		case 4:
			// <=
			if(l <= r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		case 5:
			// <
			
			if(l < r)
				return new Boolean(true);
			else
				return new Boolean(false);

		case 6:
			// !=
			if(l != r)
				return new Boolean(true);
			else
				return new Boolean(false);

			
		default:
			return null;
		}
	}
    

}
