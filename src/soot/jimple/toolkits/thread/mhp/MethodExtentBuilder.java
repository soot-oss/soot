package soot.jimple.toolkits.thread.mhp;


import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.CheckRecursiveCalls;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.PegCallGraph;
import soot.jimple.spark.*;
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
	
	
	//private List inlineSites = new ArrayList(); 
	private Set methodsNeedingInlining = new HashSet();	  
	
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
	
	private void testMap(){
		System.out.println("methodsNeedingInlining set:");
//		Set keySet = methodsNeedingInlining.keySet();
//		Iterator it = keySet.iterator();
		Iterator it = methodsNeedingInlining.iterator();
		while (it.hasNext()){
			System.out.println(it.next());
		}
	}
	public Set getMethodsNeedingInlining(){
		return (Set)methodsNeedingInlining;
	}
	
	private void build(PegCallGraph pcg, CallGraph cg){
		
		Iterator it = pcg.iterator();
		while (it.hasNext()){
			SootMethod method = (SootMethod)it.next();
			computeForMethodInlining(method, cg);
		}
		
		
	}
	
	private void computeForMethodInlining(SootMethod targetMethod, CallGraph cg){
//		System.out.println("method:  "+targetMethod);
		
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
				//System.out.println("put: "+targetMethod);
				return;
				//return true;
			}
			else {
				if (stmt.containsInvokeExpr()){
					//System.out.println("stmt is: "+stmt);
					Value invokeExpr =(Value)((Stmt)stmt).getInvokeExpr();
					
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
							Iterator  targetIt = ((List)tmd.find(stmt, cg, true, false)).iterator();
							while (targetIt.hasNext()){
								SootMethod target = (SootMethod)targetIt.next();     
								if (target.isSynchronized()){
									//System.out.println("method is synchronized: "+method);    
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
		Set gray = new HashSet();
		Iterator it = cg.iterator();
		
		while (it.hasNext()){
			Object o = it.next();
			if (methodsNeedingInlining.contains(o)) continue;
			else if ( !gray.contains(o)){
				//System.out.println("visit: "+o);
				if (visitNode(o, gray, cg)){
					methodsNeedingInlining.add(o);
					//System.out.println("put: "+o+"in pro");
				}
			}
		}
		
		//System.out.println("======after pro========");
		
	}
	
	
	
	
	
	private boolean visitNode(Object o, Set gray, PegCallGraph cg){
		//System.out.println("visit(in visit): "+o);
		gray.add(o);
		Iterator childIt = ((List)cg.getSuccsOf(o)).iterator();
		while(childIt.hasNext()){
			Object child = childIt.next();
			if (methodsNeedingInlining.contains(child)) {
				gray.add(child);
				//methodsNeedingInlining.add(child);
				//System.out.println("return true for: "+child);
				return true;
			}
			else{   
				if (!gray.contains(child)){
					if (visitNode(child, gray, cg)) {
						
						methodsNeedingInlining.add(child);
						//System.out.println("put: "+child+"in pro");
						//System.out.println("return true for: "+child);
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
	private void checkMethodsNeedingInlining(){
		System.out.println("===========methodsNeedingInlining===========");
		Iterator it = methodsNeedingInlining.iterator();
		while (it.hasNext()){
			System.out.println(it.next());
		}
		System.out.println("==end===methodsNeedingInlining========");
	}
	/*
	 private void checkSccList(List sccList, CallGraph cg){
	 Iterator sccListIt = sccList.iterator();
	 while (sccListIt.hasNext()){
	 List scc = (List)sccListIt.next();
	 Iterator it = scc.iterator();
	 while (it.hasNext()){
	 SootMethod sm = (SootMethod)it.next();
	 if (sm.isConcrete() && !sm.getDeclaringClass().isLibraryClass()){
	 if (methodsNeedingInlining.containsKey(sm) ){
	 if (sm.getName().equals("main")) continue;
	 else{
	 //  System.out.println(sm.getName());
	  throw new RuntimeException("methodsNeedingInlining does not contains key: "+sm);
	  }
	  }
	  else{
	  
	  Iterator cgit = cg.sourceMethods();
	  while (cgit.hasNext()){
	  SootMethod m = (SootMethod)it.next();
	  if (sm.equals(m)){
	  System.out.println("**ha, find method: "+sm +"in sourcesMethod");
	  }
	  }
	  System.out.println("find key of method: "+sm);
	  if (((Boolean)methodsNeedingInlining.get(sm)).booleanValue() == true){
	  System.err.println("System exit because interested methods relate to recursive calls!");
	  System.exit(1);
	  }
	  }
	  }
	  }
	  }
	  }
	  
	  
	  */
	
}
