package soot.dava.toolkits.base.AST.traversals;

import java.util.List;

import soot.Local;
import soot.Value;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;

/*
 * Given a statement of interest this traversal checks whether
 * a, the defined variable is a local 
 * b, there is any def of the defined local of interest before
 * the given statement
 * 
 * if no then possible is set to true
 * else false
 */
public class InitializationDeclarationShortcut extends DepthFirstAdapter {
	AugmentedStmt ofInterest;
	boolean possible=false;
	Local definedLocal=null;
	int seenBefore=0;//if there is a definition of local which is not by stmt of interest increment this
	
	public InitializationDeclarationShortcut(AugmentedStmt ofInterest){
		this.ofInterest = ofInterest;
	}
	
	public InitializationDeclarationShortcut(boolean verbose, AugmentedStmt ofInterest){
		super(verbose);
		this.ofInterest = ofInterest;
	}
	
	public boolean isShortcutPossible(){
		return possible;
	}
	
	/*
	 * Check that the stmt of interest defines a local in
	 * the DVariableDeclarationNode of the method
	 * else set to false and stop
	 */
	public void inASTMethodNode(ASTMethodNode node){
		Stmt s = ofInterest.get_Stmt();
		//check this is a definition
		if(! (s instanceof DefinitionStmt )){
			possible=false;
			return;
		}
			
		Value defined = ((DefinitionStmt)s).getLeftOp();
		if(!(defined instanceof Local)){
			possible=false;
			return;
		}
		
		//check that this is a local defined in this method
		//its a sanity check
		List declaredLocals = node.getDeclaredLocals();
		if(!declaredLocals.contains((Local)defined)){
			possible=false;
			return;
		}
		definedLocal = (Local)defined;
	}
	
	
	public void inDefinitionStmt(DefinitionStmt s){
		if(definedLocal==null)
			return;
		
		Value defined = ((DefinitionStmt)s).getLeftOp();
		if(!(defined instanceof Local)){
			return;
		}
		
		if(defined.equals(definedLocal)){
			//the local of interest is being defined
			
			//if this is the augmentedStmt of interest set possible to true if not already seen
			if(s.equals(ofInterest.get_Stmt())){
				//it is the stmt of interest
				if(seenBefore==0)
					possible=true;
				else
					possible=false;
			}
			else{
				//its a definition of the local of interest but not by the stmt of interest
				seenBefore++;
			}

		}	
	}
	
	
}
