
package soot.jimple.toolkits.thread.mhp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.toolkits.scalar.*;

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


public class MonitorSet extends ArraySparseSet{

	private static final Logger logger =LoggerFactory.getLogger(MonitorSet.class);


	//    int size = 0;
	
	MonitorSet(){
		super();
	}
	
	public Object getMonitorDepth(String objName){
		Iterator<?> it = iterator();
		while (it.hasNext()){
			Object obj = it.next();
			if (obj instanceof MonitorDepth){		    
				MonitorDepth md = (MonitorDepth)obj;
				if (md.getObjName().equals(objName)) return md;
			}
		}
		return null;
	}
	
	public MonitorSet clone(){
		MonitorSet newSet = new MonitorSet();
		newSet.union(this);
		return newSet;
	}
	/*public void copy(MonitorSet dest){
	 logger.info("====begin copy");
	 dest.clear();
	 Iterator iterator = iterator();
	 while (iterator.hasNext()){
	 Object obj = iterator.next();
	 if (obj instanceof MonitorDepth) {
	 logger.info("obj: "+((MonitorDepth)obj).getObjName());
	 logger.info("depth: "+((MonitorDepth)obj).getDepth());	
	 }
	 else
	 logger.info("obj: "+obj);
	 if (!dest.contains(obj)) dest.add(obj);
	 else logger.info("dest contains "+obj);
	 }
	 logger.info("===finish copy===");
	 }
	 */
	
	
	/**
	 * Returns the union (join) of this MonitorSet and <code>other</code>, putting
	 * result into <code>this</code>. */
	public void union(MonitorSet other){
		
	}
	
	/**
	 * Returns the union (join) of this MonitorSet and <code>other</code>, putting
	 * result into <code>dest</code>. <code>dest</code>, <code>other</code> and
	 * <code>this</code> could be the same object.
	 */
	/*ublic void union(MonitorSet other, MonitorSet dest){
	 other.copy(dest);
	 Iterator iterator = iterator();
	 while (iterator.hasNext()){
	 
	 MonitorDepth md = (MonitorDepth)iterator.next();
	 Object obj = dest.getMonitorDepth(md.getObjName());
	 if ( obj == null){
	 dest.add(md);
	 }
	 else{
	 if (obj instanceof MonitorDepth){
	 if (md.getDepth() != ((MonitorDepth)obj).getDepth())
	 throw new RuntimeException("Find different monitor depth at merge point!");
	 
	 }
	 else
	 throw new RuntimeException("MonitorSet contains non MonitorDepth element!");
	 }
	 
	 }
	 
	 }
	 */
	public void intersection(MonitorSet other, MonitorSet dest){
		/*
		 logger.info("this:");
		 this.test();
		 logger.info("other:");
		 other.test();
		 */
		if (other.contains("&")) {
			
			this.copy(dest);
			//logger.info("copy this to dest: ");
			//dest.test();
		}
		else if (this.contains("&")) {
			other.copy(dest);
			//logger.info("copy other to dest: ");
			//dest.test();
		}
		else{
			Iterator<?> it = iterator();
			while (it.hasNext()){
				Object o   = it.next();
				if (o   instanceof MonitorDepth){
					MonitorDepth md = (MonitorDepth)o  ;
					Object obj = dest.getMonitorDepth(md.getObjName());
					if ( obj != null)
						
						if (md.getDepth() != ((MonitorDepth)obj).getDepth()) {
							throw new RuntimeException("stmt inside different monitor depth !");
						}
						else  dest.add(obj);
				}
			}
			
			
			
		}
		
	} 
	
	
	public void test(){
		logger.info("====MonitorSet===");
		Iterator<?> it = iterator();
		while (it.hasNext()){
			Object obj = it.next();
			if (obj instanceof MonitorDepth){
				MonitorDepth md = (MonitorDepth)obj;     ;
				logger.info("obj: {}",md.getObjName());
				logger.info("depth: {}",md.getDepth());
			}
			else
				logger.info(obj+"");
		}
		logger.info("====MonitorSet end====");
	}
	
}
