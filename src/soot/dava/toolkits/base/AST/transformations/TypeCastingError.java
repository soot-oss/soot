package soot.dava.toolkits.base.AST.transformations;

import java.util.Iterator;
import java.util.List;

import soot.BooleanType;
import soot.ByteType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.PrimType;
import soot.ShortType;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DShortcutIf;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.grimp.internal.GCastExpr;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Stmt;
import soot.jimple.internal.ImmediateBox;

public class TypeCastingError extends DepthFirstAdapter {
	public boolean myDebug=false;
	public TypeCastingError(){
		
	}
	
	public TypeCastingError(boolean verbose){
		super(verbose);
	}
	
	public void inASTStatementSequenceNode(ASTStatementSequenceNode node){
		List stmts = node.getStatements();
		Iterator stmtIt = stmts.iterator();
		while(stmtIt.hasNext()){
			AugmentedStmt as = (AugmentedStmt)stmtIt.next();
			Stmt s = as.get_Stmt();
			if(! (s instanceof DefinitionStmt))
				continue;
			
			DefinitionStmt ds = (DefinitionStmt)s;
			if(myDebug) System.out.println("Definition stmt"+ds);			
			
			ValueBox rightBox = ds.getRightOpBox();
			ValueBox leftBox = ds.getLeftOpBox();
			
			Value right = rightBox.getValue();
			Value left = leftBox.getValue();
				
			if(! (left.getType() instanceof PrimType && right.getType() instanceof PrimType )){
				//only interested in prim type casting errors
				if(myDebug) System.out.println("\tDefinition stmt does not contain prims no need to modify");
				continue;
			}
			
			Type leftType = left.getType();
			Type rightType = right.getType();
			if(myDebug) System.out.println("Left type is: "+leftType);
			if(myDebug) System.out.println("Right type is: "+rightType);
			if(leftType.equals(rightType)){
				if(myDebug) System.out.println("\tTypes are the same");
				if(myDebug) System.out.println("Right value is of instance"+right.getClass());
			}
			if(!leftType.equals(rightType)){
				if(myDebug) System.out.println("\tDefinition stmt has to be modified");	
				// ByteType, DoubleType, FloatType, IntType, LongType, ShortType
				/*
				 * byte  	 Byte-length integer  	8-bit two's complement
				 * short 	Short integer 	16-bit two's complement
				 * int 	Integer 	32-bit two's complement
				 * long 	Long integer 	64-bit two's complement
				 * float 	Single-precision floating point 	32-bit IEEE 754
				 * double Double-precision floating point  	64-bit IEEE 754 	
				 */
			    if(leftType instanceof ByteType && (rightType instanceof DoubleType || 
		    			rightType instanceof FloatType || rightType instanceof IntType || rightType instanceof LongType
		    			|| rightType instanceof ShortType)) {
		    	//loss of precision do explicit casting
		    	
			    	if(DEBUG) System.out.println("Explicit casting to BYTE required");
			    	rightBox.setValue(new GCastExpr(right,ByteType.v()));
			    	if(DEBUG)System.out.println("New right expr is "+rightBox.getValue().toString());
			    	continue;
			    }
		    	
			    if(leftType instanceof ShortType && (rightType instanceof DoubleType || 
		    			rightType instanceof FloatType || rightType instanceof IntType || rightType instanceof LongType)) {
			    	//loss of precision do explicit casting
		    	
			    	if(DEBUG)System.out.println("Explicit casting to SHORT required");
			    	rightBox.setValue(new GCastExpr(right,ShortType.v()));
			    	if(DEBUG)System.out.println("New right expr is "+rightBox.getValue().toString());
			    	continue;
			    }

			    
			    if(leftType instanceof IntType && (rightType instanceof DoubleType || 
		    			rightType instanceof FloatType || rightType instanceof LongType)) {
			    	//loss of precision do explicit casting
		    	
			    	if(myDebug)System.out.println("Explicit casting to INT required");
			    	rightBox.setValue(new GCastExpr(right,IntType.v()));
			    	if(myDebug)System.out.println("New right expr is "+rightBox.getValue().toString());
			    	continue;
			    }
			    
			    
			    if(leftType instanceof LongType && (rightType instanceof DoubleType || 
		    			rightType instanceof FloatType )) {
			    	//loss of precision do explicit casting
		    	
			    	if(DEBUG)System.out.println("Explicit casting to LONG required");
			    	rightBox.setValue(new GCastExpr(right,LongType.v()));
			    	if(DEBUG)System.out.println("New right expr is "+rightBox.getValue().toString());
			    	continue;
			    }
			    
			    
			    if(leftType instanceof FloatType && rightType instanceof DoubleType) {
			    	//loss of precision do explicit casting
		    	
			    	if(DEBUG)System.out.println("Explicit casting to FLOAT required");
			    	rightBox.setValue(new GCastExpr(right,FloatType.v()));
			    	if(DEBUG)System.out.println("New right expr is "+rightBox.getValue().toString());
			    	continue;
			    }		    	
		    }
			
		}
		
	}
	
	
}
