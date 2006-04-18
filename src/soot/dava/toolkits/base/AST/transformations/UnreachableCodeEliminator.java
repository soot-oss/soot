package soot.dava.toolkits.base.AST.transformations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.G;
import soot.Local;
import soot.SootClass;
import soot.Type;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTLabeledBlockNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.dava.toolkits.base.AST.structuredAnalysis.UnreachableCodeFinder;
import soot.jimple.Stmt;



public class UnreachableCodeEliminator extends DepthFirstAdapter {
	public boolean DUBUG=true;
	ASTNode AST;
	UnreachableCodeFinder codeFinder;
	
	public UnreachableCodeEliminator(ASTNode AST) {
		super();
		this.AST = AST;
		setup();
	}

	public UnreachableCodeEliminator(boolean verbose, ASTNode AST) {
		super(verbose);
		this.AST = AST;
		setup();
	}

	private void setup() {	
		codeFinder = new UnreachableCodeFinder(AST);
		//parentOf = new ASTParentNodeFinder();
		//AST.apply(parentOf);
	}

	public void inASTStatementSequenceNode(ASTStatementSequenceNode node){
		List toRemove = new ArrayList();
		List stmts = node.getStatements();
		Iterator it = stmts.iterator();
		while(it.hasNext()){
			AugmentedStmt as = (AugmentedStmt)it.next();
			Stmt s = as.get_Stmt();
			//System.out.println("HERE!!!"+s.toString());
			if(!codeFinder.isConstructReachable(s)){
				toRemove.add(as);
				//if(DEBUG) System.out.println("Statement "+s.toString()+ " is NOT REACHABLE REMOVE IT");
			}
		}
		it = toRemove.iterator();
		while(it.hasNext()){
			stmts.remove(it.next());
		}
	}
	
	
	
	public void normalRetrieving(ASTNode node) {
    	if(node instanceof ASTSwitchNode){
  		   dealWithSwitchNode((ASTSwitchNode)node);
  	    return;
  	}

    	//from the Node get the subBodes
    	List toReturn = new ArrayList();
		Iterator sbit = node.get_SubBodies().iterator();
		while (sbit.hasNext()) {
			Object subBody = sbit.next();
			Iterator it = ((List) subBody).iterator();

			//go over the ASTNodes in this subBody and apply
			while (it.hasNext()) {
				ASTNode temp = (ASTNode) it.next();
				if(!codeFinder.isConstructReachable(temp)){
					//System.out.println("-------------------------A child of node of type "+node.getClass()+" whose type is "+temp.getClass()+" is unreachable");
					toReturn.add(temp);
				}
				else{
					//only apply on reachable nodes
					temp.apply(this);
				}
			}
			
			it = toReturn.iterator();
			while(it.hasNext()){
				//System.out.println("Removed");
				((List)subBody).remove(it.next());
			}
		}//end of going over subBodies
	}
	
	
	
	
	
	
	
	
	
	
	
	//TODO
	public void caseASTTryNode(ASTTryNode node) {
		//get try body 
		List tryBody = node.get_TryBody();
		Iterator it = tryBody.iterator();

		//go over the ASTNodes in this tryBody and apply
		List toReturn = new ArrayList();
		while (it.hasNext()){
	    	ASTNode temp = (ASTNode) it.next();
	    	if(!codeFinder.isConstructReachable(temp)){
				toReturn.add(temp);
			}
			else{
				//only apply on reachable nodes
				temp.apply(this);
			}
		}
		
		it = toReturn.iterator();
		while(it.hasNext()){
			tryBody.remove(it.next());
		}
		
		
		
		
		
		Map exceptionMap = node.get_ExceptionMap();
		Map paramMap = node.get_ParamMap();
		//get catch list and apply on the following
		// a, type of exception caught
		// b, local of exception
		// c, catchBody
		List catchList = node.get_CatchList();
		Iterator itBody = null;
		it = catchList.iterator();
		while (it.hasNext()) {
			ASTTryNode.container catchBody = (ASTTryNode.container) it.next();

			SootClass sootClass = ((SootClass) exceptionMap.get(catchBody));
			Type type = sootClass.getType();

			//apply on type of exception
			caseType(type);

			//apply on local of exception
			Local local = (Local) paramMap.get(catchBody);
			/*
			 * March 18th, 2006, Since these are always locals we dont have access to ValueBox
			 */
			decideCaseExprOrRef(local);

			//apply on catchBody
			List body = (List) catchBody.o;
			toReturn = new ArrayList();
			itBody = body.iterator();
			while (itBody.hasNext()) {
		    	ASTNode temp = (ASTNode) itBody.next();
		    	if(!codeFinder.isConstructReachable(temp)){
					toReturn.add(temp);
				}
				else{
					//only apply on reachable nodes
					temp.apply(this);
				}
			}
			
			itBody = toReturn.iterator();
			while(itBody.hasNext()){
				body.remove(itBody.next());
			}
		}
	}

	
	
	
	

    private void dealWithSwitchNode(ASTSwitchNode node){
    	//System.out.println("dealing with SwitchNode");
    	//do a depthfirst on elements of the switchNode

    	List indexList = node.getIndexList();
    	Map index2BodyList = node.getIndex2BodyList();

    	Iterator it = indexList.iterator();
    	while (it.hasNext()) {//going through all the cases of the switch statement
    	    Object currentIndex = it.next();
    	    List body = (List) index2BodyList.get( currentIndex);
    	    
    	    if (body == null)
    	    	continue;
    	    
    	    //this body is a list of ASTNodes 

    	    List toReturn = new ArrayList();
    	    Iterator itBody = body.iterator();
    	    //go over the ASTNodes and apply
    	    while (itBody.hasNext()){
    	    	ASTNode temp = (ASTNode) itBody.next();
    	    	//System.out.println("Checking whether child of type "+temp.getClass()+" is reachable");
    	    	if(!codeFinder.isConstructReachable(temp)){
					//System.out.println(">>>>>>>>>>>>>>>>>-------------------------A child of node of type "+node.getClass()+" whose type is "+temp.getClass()+" is unreachable");
					toReturn.add(temp);
				}
				else{
	    	    	//System.out.println("child of type "+temp.getClass()+" is reachable");
					//only apply on reachable nodes
					temp.apply(this);
				}
			}
			
    	    Iterator newit = toReturn.iterator();
			while(newit.hasNext()){
				//System.out.println("Removed");
				body.remove(newit.next());
			}

    	}
    }
}
