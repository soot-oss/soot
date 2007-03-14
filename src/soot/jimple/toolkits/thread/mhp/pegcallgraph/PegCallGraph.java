package soot.jimple.toolkits.thread.mhp.pegcallgraph;

import soot.toolkits.graph.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.*;
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


public class PegCallGraph implements DirectedGraph{
	List heads;
	List tails;
	Chain chain;
	// protected Map methodToSuccs;
	//protected Map methodToPreds;
	private Map methodToSuccs;
	private Map methodToPreds;
	private Map methodToSuccsTrim;
	private Set clinitMethods;
	
	public PegCallGraph( CallGraph cg){
		clinitMethods = new HashSet();
		chain = new HashChain();
		heads = new ArrayList();
		tails = new ArrayList();
		methodToSuccs = new HashMap();
		methodToPreds = new HashMap();
		methodToSuccsTrim = new HashMap();
		//	buildfortest();
		buildChainAndSuccs(cg);
		//testChain();
//		testMethodToSucc();
		buildPreds();
//		trim(); BROKEN
		//testMethodToPred();
		//testClinitMethods();
	}
	
	private void buildfortest(){
		String s1 ="1";
		String s2 ="2";
		String s3 ="3";
		String s4 ="4";
		String s5 ="5";
		String s6 ="6";
		String s7 ="7";
		String s8 ="8";
		
		List succ1 = new ArrayList();
		succ1.add(s2);
		methodToSuccs.put(s1, succ1);
		
		List succ2 = new ArrayList();
		succ2.add(s5);
		succ2.add(s6);
		methodToSuccs.put(s2, succ2);
		
		List succ3 = new ArrayList();
		succ3.add(s4);
		succ3.add(s7);
		methodToSuccs.put(s3, succ3);
		
		List succ4 = new ArrayList();
		succ4.add(s3);
		succ4.add(s5);
		methodToSuccs.put(s4, succ4);
		
		List succ5 = new ArrayList();
		succ5.add(s6);
		methodToSuccs.put(s5, succ5);
		
		List succ6 = new ArrayList();
		succ6.add(s7);
		methodToSuccs.put(s6, succ6);
		
		List succ7 = new ArrayList();
		succ7.add(s6);
		succ7.add(s8);
		methodToSuccs.put(s7, succ7);
		
		List succ8 = new ArrayList();
		succ8.add(s8);
		methodToSuccs.put(s8, succ8);
		
		
		
		
	}
	private void testClinitMethods(){
		System.out.println("******** clinit methods********");
		Iterator it = clinitMethods.iterator();
		while (it.hasNext()) {
			SootMethod sm = (SootMethod)it.next();
			System.out.println(sm);
			
		}   
	}
	protected void testChain()
	{
		System.out.println("******** chain of pegcallgraph********");
		Iterator it = chain.iterator();
		while (it.hasNext()) {
			SootMethod sm = (SootMethod)it.next();
			System.out.println(sm);
			//System.out.println("name: "+sm.getName());	
		}
	}
	public Set getClinitMethods(){
		return (Set)clinitMethods;
	}
	private void buildChainAndSuccs(CallGraph cg){
		Iterator it = cg.sourceMethods();
		while (it.hasNext()){
			SootMethod sm = (SootMethod)it.next();
			if (sm.getName().equals("main")) heads.add(sm);
			//if (sm.isConcrete() && !sm.getDeclaringClass().isLibraryClass()){
			//if (sm.hasActiveBody() && sm.getDeclaringClass().isApplicationClass() ){
			if (sm.isConcrete() && sm.getDeclaringClass().isApplicationClass() ){
				if (!chain.contains(sm)){
					chain.add(sm);
				}
				List succsList = new ArrayList();
				Iterator edgeIt = cg.edgesOutOf(sm);
				while(edgeIt.hasNext()){
					Edge edge = (Edge)edgeIt.next();
					SootMethod target = edge.tgt();
					//if (target.isConcrete() && !target.getDeclaringClass().isLibraryClass()){
					//if (target.hasActiveBody()  && target.getDeclaringClass().isApplicationClass()){
					if(target.isConcrete() && target.getDeclaringClass().isApplicationClass())
					{
						succsList.add(target);
						if (!chain.contains(target)){
							chain.add(target);
							//			    System.out.println("add: "+target);
						}
						if (edge.isClinit()){
							clinitMethods.add(target);
						}
						
						
					}
				}
				//if (succsList == null) System.out.println("null succsList");
				if (succsList.size()>0 ) methodToSuccs.put(sm, succsList);
			}
		}
		//testChain();
		/* Because CallGraph.sourceMethods only "Returns an iterator 
		 * over all methods that are the sources of at least one edge",
		 * some application methods may not in methodToSuccs. So add them.
		 */
		{
			Iterator chainIt = chain.iterator();
			while (chainIt.hasNext()){
				SootMethod sm = (SootMethod)chainIt.next();
				
				if (!methodToSuccs.containsKey(sm)){
					methodToSuccs.put(sm, new ArrayList());
					//System.out.println("put: "+sm+"into methodToSuccs");
				}
			}
		}
		//remove the entry for those who's preds are null.
		{
			Iterator chainIt = chain.iterator();
			while (it.hasNext()){
				SootMethod s = (SootMethod)chainIt.next();
				if (methodToSuccs.containsKey(s)){
					List succList = (List) methodToSuccs.get(s);
					if (succList.size()<=0) {
//						methodToSuccs.remove(s);
					}
				}
			}
		}
		//testMethodToSucc();
		
		
		//unmodidiable
		{
			Iterator chainIt = chain.iterator();
			while(chainIt.hasNext()){
				
				SootMethod s =  (SootMethod)chainIt.next();
				//		System.out.println(s);
				if (methodToSuccs.containsKey(s)){
					methodToSuccs.put(s, Collections.unmodifiableList((List) methodToSuccs.get(s)));
				}
			}
		}
		
		
	}
	private void buildPreds(){
		
		
		// initialize the pred sets to empty
		{
			Iterator unitIt = chain.iterator();
			
			while(unitIt.hasNext()){
				
				methodToPreds.put(unitIt.next(), new ArrayList());
			}
		}
		
		{
			Iterator unitIt = chain.iterator();
			
			while(unitIt.hasNext())
			{
				Object s =  unitIt.next();
				
				// Modify preds set for each successor for this statement
				List succList = (List) methodToSuccs.get(s);
				if (succList.size()>0){
					Iterator succIt = succList.iterator();
					
					while(succIt.hasNext()){
						
						Object successor =  succIt.next();
						
						List predList = (List) methodToPreds.get(successor);
						//  if (predList == null) System.out.println("null predList");
						//if (s == null) System.out.println("null s");
						try {
							predList.add(s);
						} catch(NullPointerException e) {
							System.out.println(s + "successor: " + successor);
							throw e;
						}
					}
				}
			}
		}
		
		// Make pred lists unmodifiable.
		{
			Iterator unitIt = chain.iterator();
			
			while(unitIt.hasNext())
			{
				SootMethod s =  (SootMethod)unitIt.next();
				if (methodToPreds.containsKey(s)){
					List predList = (List) methodToPreds.get(s);
					methodToPreds.put(s, Collections.unmodifiableList(predList));
				}
			}
		}
		
	}
	public void trim(){
		//If there are multiple edges from one method to another, we only keeps one edge.  BROKEN
		Set maps = methodToSuccs.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			List list = (List)entry.getValue();
			List newList = new ArrayList();
			Iterator it = list.iterator();
			while (it.hasNext()){
				Object obj = it.next();
				if (!list.contains(obj)) newList.add(obj);
			}
			methodToSuccsTrim.put(entry.getKey(), newList);
		}
	}
	public List getHeads(){
		return heads;
	}
	public List getTails(){
		return tails;
	}
	
	public List getTrimSuccsOf(Object s){
		if (!methodToSuccsTrim.containsKey(s))
			return java.util.Collections.EMPTY_LIST;
//			throw new RuntimeException("Invalid method"+s);
		return (List)methodToSuccsTrim.get(s);
	}
	public List getSuccsOf(Object s){
		if (!methodToSuccs.containsKey(s))
			return java.util.Collections.EMPTY_LIST;
//			throw new RuntimeException("Invalid method"+s);
		return (List)methodToSuccs.get(s);
	}
	
	public List getPredsOf(Object s){
		if (!methodToPreds.containsKey(s))	
			return java.util.Collections.EMPTY_LIST;
//			throw new RuntimeException("Invalid method"+s);
		return (List)methodToPreds.get(s);
	}
	public Iterator iterator(){
		return chain.iterator();
	}
	public int size(){
		return chain.size();	
	}
	
	protected void testMethodToSucc(){
		System.out.println("=====test methodToSucc ");
		Set maps = methodToSuccs.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			System.out.println("---key=  "+entry.getKey());
			List list = (List)entry.getValue();
			if (list.size()>0){
				
				System.out.println("**succ set:");
				Iterator it = list.iterator();
				while (it.hasNext()){
					System.out.println(it.next());
					
					
				}
				
			}
		}
		System.out.println("=========methodToSucc--ends--------");	
	}
	protected void testMethodToPred(){
		System.out.println("=====test methodToPred ");
		Set maps = methodToPreds.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			System.out.println("---key=  "+entry.getKey());
			List list = (List)entry.getValue();
			if (list.size()>0){
				
				System.out.println("**pred set:");
				Iterator it = list.iterator();
				while (it.hasNext()){
					System.out.println(it.next());
					
					
				}
				
			}
		}
		System.out.println("=========methodToPred--ends--------");	
	}
	
}
