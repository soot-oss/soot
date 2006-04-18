package soot.dava.toolkits.base.AST.transformations;

import java.util.Iterator;
import java.util.List;

import soot.BooleanType;
import soot.IntType;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DShortcutIf;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.Stmt;
import soot.jimple.internal.ImmediateBox;

public class ShortcutIfGenerator extends DepthFirstAdapter {
	
	public ShortcutIfGenerator(){
		
	}
	
	public ShortcutIfGenerator(boolean verbose){
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
			ValueBox rightBox = ds.getRightOpBox();
				
			Value right = rightBox.getValue();
				
			/*
			 * Going to match int i = (int) z where z is a boolean
			 * or int i= z i.e. without the cast
			 */
			
			//right type should contain the expected type on the left
			//in the case of the cast this is the cast type else just get the left type
			Type rightType=null;
			ValueBox OpBox = null;
				
			if(right instanceof CastExpr){
				rightType = ((CastExpr)right).getCastType();
				OpBox = ((CastExpr)right).getOpBox();
			}
			else{
				rightType = ds.getLeftOp().getType();
				OpBox = rightBox;
			}
				
			if(! (rightType instanceof IntType )){
				continue;
			}				
				
			Value Op = OpBox.getValue();
			if(! (Op.getType() instanceof BooleanType)){
				continue;
			}

			//ready for the switch
			ImmediateBox trueBox = new ImmediateBox(IntConstant.v(1));
			ImmediateBox falseBox = new ImmediateBox(IntConstant.v(0));
				
			DShortcutIf shortcut = new DShortcutIf(OpBox,trueBox,falseBox);
			if(DEBUG)
				System.out.println("created: "+shortcut);
			rightBox.setValue(shortcut);
		}
		
	}
}
