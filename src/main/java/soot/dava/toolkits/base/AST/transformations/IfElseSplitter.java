package soot.dava.toolkits.base.AST.transformations;

import java.util.Iterator;
import java.util.List;

import soot.G;
import soot.dava.DecompilationException;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DAbruptStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.dava.toolkits.base.AST.traversals.ASTParentNodeFinder;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;

/*
 *  look for patterns of the form
 *   if(cond1){                  if(cond1){
 *      BodyA                        BodyA
 *      abrupt       ---->           abrupt
 *   }                           }
 *   else{                       BodyB
 *      BodyB
 *   }
 *   
 *   Things to ensure:
 *       If abrupt then check BodyA does not target a label on the ifelse
 *       
 * ALWAYS  Make sure BodyB does not target a label on the ifelse
 *       
 *    If the pattern is NOT matched check the reverse i.e. maybe BodyB
 *    has the abrupt statement in that case we just negated the condition
 */
public class IfElseSplitter extends DepthFirstAdapter {
	public static boolean DEBUG=false;
	boolean targeted=false;
	ASTMethodNode methodNode;

	
	ASTNode parent;
	ASTIfElseNode toReplace;
	ASTIfNode toInsert;
	List<Object> bodyAfterInsert;
	boolean transform=false;
	
	public IfElseSplitter(){
		
	}
	
	public IfElseSplitter(boolean verbose){
		super(verbose);
	}
	
	public void inASTMethodNode(ASTMethodNode node){
		methodNode = node;
	}
	
	
	public void outASTMethodNode(ASTMethodNode a){
		if(!transform)
			return;
		
		List<Object> parentBodies = parent.get_SubBodies();
		Iterator<Object> it = parentBodies.iterator();
		while(it.hasNext()){
			List<Object> subBody = null;
		    if (parent instanceof ASTTryNode)
		    	subBody = (List<Object>) ((ASTTryNode.container) it.next()).o;
		    else
		    	subBody = (List<Object>)it.next();


		    if(subBody.indexOf(toReplace)>-1){
		    	//in the subBody list the node is present
		    	subBody.add(subBody.indexOf(toReplace),toInsert);
		    	subBody.addAll(subBody.indexOf(toReplace),bodyAfterInsert);
		    	subBody.remove(toReplace);
		    	G.v().ASTTransformations_modified=true;
		    }
		}
	
	}
	
	
	
	public void outASTIfElseNode(ASTIfElseNode node){
		//if some pattern has already matched cant do another one in this go
		if(transform)
			return;
		
		List<Object> subBodies = node.get_SubBodies();
		if(subBodies.size()!=2)
			throw new DecompilationException("IfelseNode without two subBodies. report to developer");
		
		List<Object> ifBody = (List<Object>)subBodies.get(0);
		List<Object> elseBody = (List<Object>)subBodies.get(1);
		
		boolean patternMatched = tryBodyPattern(ifBody,node.get_Label(),elseBody);
		List<Object> newIfBody = null;
		List<Object> outerScopeBody = null;
		boolean negateIfCondition=false;
		
		if(patternMatched){
			if(DEBUG)
				System.out.println("First pattern matched");
			newIfBody = ifBody;
			outerScopeBody=elseBody;
			negateIfCondition=false;
		}
		else{
			patternMatched = tryBodyPattern(elseBody,node.get_Label(),ifBody);
			if(patternMatched){
				if(DEBUG)
					System.out.println("Second pattern matched");

				newIfBody = elseBody;
				outerScopeBody = ifBody;
				negateIfCondition=true;
			}
		}
	
		//if at this point newIfBody and outerScopeBody are non null we got ourselves a transformation :)
		if(newIfBody!= null && outerScopeBody!= null){
			ASTCondition cond = node.get_Condition();
			if(negateIfCondition)
				cond.flip();
			
			ASTIfNode newNode = new ASTIfNode(node.get_Label(),cond,newIfBody);
			if(DEBUG){
				System.out.println("New IF Node is: "+newNode.toString());
				System.out.println("Outer scope body list is:\n");
				for(int i=0;i<outerScopeBody.size();i++)
					System.out.println("\n\n "+outerScopeBody.get(i).toString());
			}
			
			

			ASTParentNodeFinder finder = new ASTParentNodeFinder();
			methodNode.apply(finder);
			Object returned = finder.getParentOf(node);
			if(returned == null){
				//coundnt find parent so cant do anything
				return;
			}
			
			/*
			 * Setting globals since everything is ready for transformation
			 * BECAUSE we cant modify the parent here we are going to do some 
			 * bad coding style
			 * store the information needed for this into globals
			 * set a flag
			 * and the outASTMethod checks for this
			 */
			parent = (ASTNode)returned;
			toReplace=node;
			toInsert = newNode;
			bodyAfterInsert = outerScopeBody;
			transform=true;			
			
		}
	}
	
	public boolean tryBodyPattern(List<Object> body,SETNodeLabel label, List<Object> otherBody){
		Stmt lastStmt = getLastStmt(body);
		if(lastStmt == null){
			//dont have a last stmt so cant match pattern
			return false;
		}
			
		if(! (lastStmt instanceof ReturnStmt || lastStmt instanceof ReturnVoidStmt || lastStmt instanceof DAbruptStmt)){
			//lastStmt is not an abrupt stmt
			return false;
		}

		if(bodyTargetsLabel(label,body) || bodyTargetsLabel(label,otherBody)){
			//one of the bodies targets the label on the ifelse cant match pattern
			return false;
		}

		//pattern matched
		return true;
	}
	

	
	
	/*
	 * Check that label is non null and the string inside is non null... if yes return false
	 * Check that the given list (sequeneof ASTNodes have no abrupt edge targeting the label.
	 *
	 */
	public boolean bodyTargetsLabel(SETNodeLabel label, List<Object> body){
		//no SETNodeLabel is good
		if(label == null)
			return false;
		
		//SETNodeLabel but with no string is also good
		if(label.toString() == null)
			return false;
		
		final String strLabel = label.toString();
		
		//go through the body use traversal to find whether there is an abrupt stmt targeting this
		Iterator<Object> it = body.iterator();
		
		targeted=false;
		while (it.hasNext()) {
			ASTNode temp = (ASTNode) it.next();

			temp.apply( new DepthFirstAdapter(){
				//set targeted to true if DAbruptStmt targets it
				public void inStmt(Stmt s){
					//only interested in abrupt stmts
					if(!(s instanceof DAbruptStmt))
						return;
					
					DAbruptStmt abrupt = (DAbruptStmt)s;
					SETNodeLabel label = abrupt.getLabel();
					if(label != null && label.toString()!= null && label.toString().equals(strLabel)){
						targeted=true;
						
					}
				}
			});
					
			if(targeted)
				break;
		}
		return targeted;		
	}
	
	
	
	
	/*
	 * Given a list of ASTNodes see if the last astnode is a StatementSequenceNode
	 * if not return null
	 * else, return the last statement in this node
	 */
	public Stmt getLastStmt(List<Object> body){
		if(body.size()==0)
			return null;
		
		ASTNode lastNode = (ASTNode)body.get(body.size()-1);
		if(!(lastNode instanceof ASTStatementSequenceNode))
			return null;
		
		ASTStatementSequenceNode stmtNode = (ASTStatementSequenceNode)lastNode;
		List<AugmentedStmt> stmts = stmtNode.getStatements();
		if(stmts.size()==0)
			return null;
		
		AugmentedStmt lastStmt = stmts.get(stmts.size()-1);
		return lastStmt.get_Stmt();						
	}
}
