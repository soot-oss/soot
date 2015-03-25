package soot.dava.toolkits.base.AST.transformations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Value;
import soot.ValueBox;
import soot.dava.internal.javaRep.DCmpExpr;
import soot.dava.internal.javaRep.DCmpgExpr;
import soot.dava.internal.javaRep.DCmplExpr;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.AddExpr;
import soot.jimple.BinopExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.MulExpr;
import soot.jimple.NumericConstant;
import soot.jimple.SubExpr;

/*
 * x = 2+3  should be simplified to x =5
 * 4l -3l should be 1l DONE
 
 * Unary Condition:DONT NEED TO HANDLE IT since what would simplify 
 * in a boolean flag which is what unary conditions are
 *  
 * Binary Codition: has a ConditionExpr stored in it not a valuebox???
 * all other expression to be handled by caseExprOrRefValueBox
 */

public class SimplifyExpressions extends DepthFirstAdapter {

	private static final Logger logger =LoggerFactory.getLogger(SimplifyExpressions.class);

	public SimplifyExpressions() {
		super();
	}

	public SimplifyExpressions(boolean verbose) {
		super(verbose);
	}
	

/*	public void inASTBinaryCondition(ASTBinaryCondition cond){
		ConditionExpr condExpr = cond.getConditionExpr();
		
		ValueBox op1Box = condExpr.getOp1Box();
	
		ValueBox op2Box = condExpr.getOp2Box();
	}
	*/
	
	public void outExprOrRefValueBox(ValueBox vb){
		//logger.info("here"+vb);
		Value v = vb.getValue();
		if(! (v instanceof BinopExpr )){
			return;
		}

		BinopExpr binop = (BinopExpr)v;
		
			logger.debug("calling getResult");
		NumericConstant constant = getResult(binop);
		
		if(constant ==null)
			return;
		logger.debug("Changin{} to....{}",vb,constant);
		vb.setValue(constant);
	}
		
		
	
	public NumericConstant getResult(BinopExpr binop){
		logger.debug("Binop expr{}",binop);
		Value leftOp = binop.getOp1();
		Value rightOp = binop.getOp2();
		
		int op = 0;
		if(binop instanceof AddExpr){
			op=1;
		}
		else if(binop instanceof SubExpr || 
				binop instanceof DCmpExpr || binop instanceof DCmpgExpr
				|| binop instanceof DCmplExpr){
			op=2;
		}
		else if(binop instanceof MulExpr){
			op=3;
		}

		if(op == 0){
			logger.debug("not add sub or mult");
			logger.debug(binop.getClass().getName());
					
			return null;
		}
		NumericConstant constant = null;
		if(leftOp instanceof LongConstant  && rightOp instanceof LongConstant){
			logger.debug("long constants!!");
			if(op ==1)
				constant = ((LongConstant)leftOp).add((LongConstant)rightOp);
			else if(op ==2)
				constant = ((LongConstant)leftOp).subtract((LongConstant)rightOp);
			else if (op ==3)
				constant = ((LongConstant)leftOp).multiply((LongConstant)rightOp);
		}
		else if(leftOp instanceof DoubleConstant  && rightOp instanceof DoubleConstant){
			logger.debug("double constants!!");
			if(op ==1)
				constant = ((DoubleConstant)leftOp).add((DoubleConstant)rightOp);
			else if(op ==2)
				constant = ((DoubleConstant)leftOp).subtract((DoubleConstant)rightOp);
			else if (op ==3)
				constant = ((DoubleConstant)leftOp).multiply((DoubleConstant)rightOp);

		}
		else if(leftOp instanceof FloatConstant  && rightOp instanceof FloatConstant){
			logger.debug("Float constants!!");
			if(op ==1)
				constant = ((FloatConstant)leftOp).add((FloatConstant)rightOp);
			else if(op ==2)
				constant = ((FloatConstant)leftOp).subtract((FloatConstant)rightOp);
			else if (op ==3)
				constant = ((FloatConstant)leftOp).multiply((FloatConstant)rightOp);
		}
		else if(leftOp instanceof IntConstant  && rightOp instanceof IntConstant){
			logger.debug("Integer constants!!");
			if(op ==1)
				constant = ((IntConstant)leftOp).add((IntConstant)rightOp);
			else if(op ==2)
				constant = ((IntConstant)leftOp).subtract((IntConstant)rightOp);
			else if (op ==3)
				constant = ((IntConstant)leftOp).multiply((IntConstant)rightOp);
		}
		
		return constant;
	}
	
}
