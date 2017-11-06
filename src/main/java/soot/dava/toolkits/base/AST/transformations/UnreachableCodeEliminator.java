package soot.dava.toolkits.base.AST.transformations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Local;
import soot.SootClass;
import soot.Type;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTTryNode;
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
		List<AugmentedStmt> toRemove = new ArrayList<AugmentedStmt>();
		for (AugmentedStmt as : node.getStatements()) {
			Stmt s = as.get_Stmt();
			//System.out.println("HERE!!!"+s.toString());
			if(!codeFinder.isConstructReachable(s)){
				toRemove.add(as);
				//if(DEBUG) System.out.println("Statement "+s.toString()+ " is NOT REACHABLE REMOVE IT");
			}
		}
		for (AugmentedStmt as : toRemove){
			node.getStatements().remove(as);
		}
	}
	
	
	
	public void normalRetrieving(ASTNode node) {
    	if(node instanceof ASTSwitchNode){
  		   dealWithSwitchNode((ASTSwitchNode)node);
  	    return;
  	}

    	//from the Node get the subBodes
    	List<ASTNode> toReturn = new ArrayList<ASTNode>();
		Iterator<Object> sbit = node.get_SubBodies().iterator();
		while (sbit.hasNext()) {
			Object subBody = sbit.next();
			Iterator<ASTNode> it = ((List<ASTNode>) subBody).iterator();

			//go over the ASTNodes in this subBody and apply
			while (it.hasNext()) {
				ASTNode temp = it.next();
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
		List<Object> tryBody = node.get_TryBody();
		Iterator<Object> it = tryBody.iterator();

		//go over the ASTNodes in this tryBody and apply
		List<Object> toReturn = new ArrayList<Object>();
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
		
		
		
		
		
		Map<Object, Object> exceptionMap = node.get_ExceptionMap();
		Map<Object, Object> paramMap = node.get_ParamMap();
		//get catch list and apply on the following
		// a, type of exception caught
		// b, local of exception
		// c, catchBody
		List<Object> catchList = node.get_CatchList();
		Iterator<Object> itBody = null;
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
			List<Object> body = (List<Object>) catchBody.o;
			toReturn = new ArrayList<Object>();
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

    	List<Object> indexList = node.getIndexList();
    	Map<Object, List<Object>> index2BodyList = node.getIndex2BodyList();

    	Iterator<Object> it = indexList.iterator();
    	while (it.hasNext()) {//going through all the cases of the switch statement
    	    Object currentIndex = it.next();
    	    List body = index2BodyList.get( currentIndex);
    	    
    	    if (body == null)
    	    	continue;
    	    
    	    //this body is a list of ASTNodes 

    	    List<ASTNode> toReturn = new ArrayList<ASTNode>();
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
			
    	    Iterator<ASTNode> newit = toReturn.iterator();
			while(newit.hasNext()){
				//System.out.println("Removed");
				body.remove(newit.next());
			}

    	}
    }
}
