package soot.jimple.toolkits.thread.mhp;


import soot.jimple.*;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
import soot.toolkits.scalar.*;
import soot.jimple.internal.*;
import soot.toolkits.graph.*;
import soot.util.*;
import soot.tagkit.*;
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

public class CheckMSet{
	CheckMSet(Map m1, Map m2){
		checkKeySet(m1,m2);
		check(m1, m2);   
		
	}
	private void checkKeySet(Map m1, Map m2){
		Iterator keySetIt2 = m2.keySet().iterator();
		FlowSet temp = new ArraySparseSet();
		while (keySetIt2.hasNext()){
			Object key2 = keySetIt2.next();
			if (key2 instanceof List){
				Iterator it = ((List)key2).iterator();
				while (it.hasNext()){
					Object obj = it.next();
					if (obj instanceof List){
						Iterator itit = ((List)obj).iterator();
						while (itit.hasNext()){
							Object o = itit.next();
							temp.add(o);
							if (!m1.containsKey(o)){
								System.err.println("1--before compacting map does not contains key "+o);
								System.exit(1);
							}
						}
					}
					else{
						temp.add(obj);
						if (!m1.containsKey(obj)){
							System.err.println("2--before compacting map does not contains key "+obj);
							System.exit(1);
						}
					}
				}
			}
			else{
				if (!(key2 instanceof JPegStmt)){
					System.err.println("key error: "+ key2);
					System.exit(1);
				}
				temp.add(key2);
				if (!m1.containsKey(key2)){
					System.err.println("3--before compacting map does not contains key "+key2);
					System.exit(1);
				}
			}
		}
		
		
		Iterator keySetIt1 = m1.keySet().iterator();
		while (keySetIt1.hasNext()) {
			Object key1 = keySetIt1.next();
			if (!temp.contains(key1)){
				System.err.println("after compacting map does not contains key "+key1);
				System.exit(1);
			}
		}
		
		
		
	}
	
	private void check(Map m1, Map m2){
		Iterator keySetIt = m2.keySet().iterator();
		while (keySetIt.hasNext()){
			Object key = keySetIt.next();
			if (key instanceof JPegStmt){
				Tag tag1 = (Tag)((JPegStmt)key).getTags().get(0);
				// System.out.println("check key: "+tag1+" "+key);
				
			}
			// System.out.println("check key: "+key);
			FlowSet mSet2 = (FlowSet)m2.get(key);
			if (key instanceof List){
				Iterator it = ((List)key).iterator();
				while (it.hasNext()){
					Object obj = it.next();
					if (obj instanceof List){
						Iterator itit = ((List)obj).iterator();
						while (itit.hasNext()){
							Object oo = itit.next();
							FlowSet mSet11 = (FlowSet)m1.get(oo);
							if (mSet11 == null){
								System.err.println("1--mSet of "+obj +" is null!");
								System.exit(1);
							}
							if (!compare(mSet11, mSet2)){
								System.err.println("1--mSet before and after are NOT the same!");
								System.exit(1);
							}
						}
					}
					else{
						FlowSet mSet1 = (FlowSet)m1.get(obj);
						if (mSet1 == null){
							System.err.println("2--mSet of "+obj +" is null!");
							System.exit(1);
						}
						if (!compare(mSet1, mSet2)){
							System.err.println("2--mSet before and after are NOT the same!");
							System.exit(1);
						}
					}
				}
			}
			else{
				FlowSet mSet1 = (FlowSet)m1.get(key);
				
				if (!compare(mSet1, mSet2)){
					System.err.println("3--mSet before and after are NOT the same!");
					System.exit(1);
				}
			}
		}
		//	    System.err.println("---mSet before and after are the same!---");
	}
	
	
	
	private boolean compare(FlowSet mSet1, FlowSet mSet2){
		{
			
			Iterator it = mSet2.iterator();	
			FlowSet temp = new ArraySparseSet();
			while (it.hasNext()){
				Object obj = it.next();
				if (obj instanceof List){
					Iterator listIt = ((List)obj).iterator();
					while (listIt.hasNext()){
						Object o = listIt.next();
						if (o instanceof List){
							Iterator itit = ((List)o).iterator(); 
							while (itit.hasNext()){
								temp.add(itit.next());
							}
						}
						else	    
							temp.add(o);
					}
				}
				else{
					
					temp.add(obj);
				}
				
			}
			
			Iterator it1= mSet1.iterator();
			while (it1.hasNext()){
				Object o = it1.next();
				if (!temp.contains(o)){
					System.out.println("mSet2: \n"+mSet2);
					System.err.println("mSet2 does not contains "+o);
					
					return false;
				}
				
				
			}
			
		}
		
		{
			Iterator it = mSet2.iterator();
			
			while (it.hasNext()){
				Object obj = it.next();
				if (obj instanceof List){
					Iterator listIt = ((List)obj).iterator();    
					while (listIt.hasNext()){
						Object o = listIt.next();
						if (o instanceof List){
							Iterator itit = ((List)o).iterator();
							while (itit.hasNext()){
								Object oo = itit.next();
								
								if (!mSet1.contains(oo)){
									System.err.println("1--mSet1 does not contains "+oo);
									return false;
								}
							}
						}
						else{
							if (!mSet1.contains(o)){
								System.err.println("2--mSet1 does not contains "+o);
								return false;
							}
						}
					}
				}
				
				else{
					
					if (!mSet1.contains(obj)){
						System.err.println("3--mSet1 does not contains "+obj);
						return false;
					}
				}
			}
		}
		
		return true;
		
		
	}
}

