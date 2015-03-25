package soot.jimple.toolkits.thread.mhp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.CheckRecursiveCalls;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.PegCallGraph;
import java.util.*;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class MethodExtentBuilder {

	private static final Logger logger =LoggerFactory.getLogger(MethodExtentBuilder.class);
	
	
	//private List inlineSites = new ArrayList(); 
	private final Set<Object> methodsNeedingInlining = new HashSet<Object>();	  
	
	public MethodExtentBuilder(Body unitBody, PegCallGraph pcg, CallGraph cg){
		//testCallGraph(cg);
		build(pcg, cg);
//		checkMethodNeedExtent();
		CheckRecursiveCalls crc = new  CheckRecursiveCalls(pcg, methodsNeedingInlining);	 
		//testMap();
		// checkSccList(sccList, cg);
		propagate(pcg);
//		checkMethodNeedExtent();
	}
	
	public Set<Object> getMethodsNeedingInlining(){
		return methodsNeedingInlining;
	}
	
	private void build(PegCallGraph pcg, CallGraph cg){
		
		Iterator it = pcg.iterator();
		while (it.hasNext()){
			SootMethod method = (SootMethod)it.next();
			computeForMethodInlining(method, cg);
		}
		
		
	}
	
	private void computeForMethodInlining(SootMethod targetMethod, CallGraph cg){
//		logger.info("method:  "+targetMethod);
		
		if (targetMethod.isSynchronized()){
			methodsNeedingInlining.add(targetMethod);
			return;
		}
		Body mBody = targetMethod.getActiveBody();
		Iterator bodyIt = mBody.getUnits().iterator();
		while (bodyIt.hasNext()){
			Stmt stmt = (Stmt)bodyIt.next();
			
			if (stmt instanceof MonitorStmt) {
//				methodsNeedingInlining.put(targetMethod, new Boolean(true));
				methodsNeedingInlining.add(targetMethod);		
				//logger.info("put: "+targetMethod);
				return;
				//return true;
			}
			else {
				if (stmt.containsInvokeExpr()){
					//logger.info("stmt is: "+stmt);
					Value invokeExpr =(stmt).getInvokeExpr();
					
					SootMethod method = ((InvokeExpr)invokeExpr).getMethod();
					
					String name = method.getName();
					
					if( name.equals("wait") ||  name.equals("notify") || name.equals("notifyAll") ||
							((name.equals("start") || name.equals("join") || 
									name.equals("suspend") || name.equals("resume") ||
									name.equals("destroy") || name.equals("stop")) &&
									method.getDeclaringClass().getName().equals("java.lang.Thread") )){
						methodsNeedingInlining.add(targetMethod);
						return;
					}
					else{
						
						if (method.isConcrete() && !method.getDeclaringClass().isLibraryClass()){
							Iterator it = cg.edgesOutOf(stmt);
							TargetMethodsFinder tmd = new TargetMethodsFinder();
							Iterator<SootMethod>  targetIt = (tmd.find(stmt, cg, true, false)).iterator();
							while (targetIt.hasNext()){
								SootMethod target = targetIt.next();     
								if (target.isSynchronized()){
									//logger.info("method is synchronized: "+method);    
									methodsNeedingInlining.add(targetMethod);
									return;
								}
							}
						}
					} 
				}		       
			}
			
			
		}
		return;
		
		
	}
	
	protected void propagate(PegCallGraph cg){
		/*if a method is not in methodsNeedingInlining, 
		 * use DFS to find out if it's parents need inlining.
		 * If so, add it to methodsNeedingInlining
		 */
		Set<Object> gray = new HashSet<Object>();
		Iterator it = cg.iterator();
		
		while (it.hasNext()){
			Object o = it.next();
			if (methodsNeedingInlining.contains(o)) continue;
			else if ( !gray.contains(o)){
				//logger.info("visit: "+o);
				if (visitNode(o, gray, cg)){
					methodsNeedingInlining.add(o);
					//logger.info("put: "+o+"in pro");
				}
			}
		}
		
		//logger.info("======after pro========");
		
	}
	
	
	
	
	
	private boolean visitNode(Object o, Set<Object> gray, PegCallGraph cg){
		//logger.info("visit(in visit): "+o);
		gray.add(o);
		Iterator childIt = (cg.getSuccsOf(o)).iterator();
		while(childIt.hasNext()){
			Object child = childIt.next();
			if (methodsNeedingInlining.contains(child)) {
				gray.add(child);
				//methodsNeedingInlining.add(child);
				//logger.info("return true for: "+child);
				return true;
			}
			else{   
				if (!gray.contains(child)){
					if (visitNode(child, gray, cg)) {
						
						methodsNeedingInlining.add(child);
						//logger.info("put: "+child+"in pro");
						//logger.info("return true for: "+child);
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
}
