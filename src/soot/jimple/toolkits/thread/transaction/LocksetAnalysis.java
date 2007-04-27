package soot.jimple.toolkits.thread.transaction;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.callgraph.*;
import soot.tagkit.*;
import soot.jimple.internal.*;
import soot.jimple.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.pag.*;
import soot.toolkits.scalar.*;

// LocksetAnalysis written by Richard L. Halpert, 2007-04-19
// Finds the set of local variables and/or references that represent all of
// the relevant objects used in a synchronized region, as accessible at the
// start of that region.

public class LocksetAnalysis extends BackwardFlowAnalysis
{
	Map unitToUses;
	Stmt begin;
	boolean lostObjects;
	
	public LocksetAnalysis(UnitGraph g)
	{
		super(g);
		
		unitToUses = null;
		begin = null;
		lostObjects = false;
		
		// analysis is done on-demand, not now
	}

	public List getLocksetOf(Map unitToUses, Stmt begin)
	{
		this.unitToUses = unitToUses;
		this.begin = begin;
		lostObjects = false;

		doAnalysis();
		
		if(lostObjects)
			return null;
		
		HashMap results = (HashMap) getFlowAfter(begin);
		List lockset = new ArrayList();
		
		for(Iterator resultsIt = results.keySet().iterator(); resultsIt.hasNext(); ) 
			lockset.add(resultsIt.next());

		return lockset;
	}
	
	protected void merge(Object in1, Object in2, Object out)
	{
		Map inMap1 = (Map) in1;
		Map inMap2 = (Map) in2;
		Map outMap = (Map) out;
		Map tmpMap = new HashMap();
		
		// union of the two maps, except when the same key is present,
		// if the groups are different, then they get merged
		
		// (ensure every new group gets a new number)
		// copy inMap1 into outMap
		outMap.clear();
		outMap.putAll(inMap1);
		// copy inMap2 into tmpMap
		tmpMap.putAll(inMap2);
		// for each tmpentry in tmpMap
		for(Iterator tmpKeyIt = tmpMap.keySet().iterator(); tmpKeyIt.hasNext(); )
		{
			Object key = tmpKeyIt.next();
			Object value = tmpMap.get(key);
		//  if the key ISN'T in outMap, add it
			if(!outMap.containsKey(key))
			{
				outMap.put(key, value);
			}
		//  if the key IS in outMap with the same value, do nothing
		//  if the key IS in outMap with a different outvalue,
			else if(outMap.get(key) != tmpMap.get(key))
			{
				Object outvalue = outMap.get(key);
		//      for each outentry in outMap
				for(Iterator outEntryIt = outMap.entrySet().iterator(); outEntryIt.hasNext(); )
				{
					Map.Entry entry = (Map.Entry) outEntryIt.next();
		//          if the value == outvalue, change it to tmpvalue
					if(entry.getValue() == outvalue)
						entry.setValue(value);
				}
		//   	for each tmpentry in tmpMap // deals with groups that already were merged
				for(Iterator tmpEntryIt = tmpMap.entrySet().iterator(); tmpEntryIt.hasNext(); )
				{
					Map.Entry entry = (Map.Entry) tmpEntryIt.next();
		//     		if the value == outvalue, change it to tmpvalue
					if(entry.getValue() == outvalue)
						entry.setValue(value);
				}
			}
		}
	}
	
	static int groupNum = 0;
	
	protected void flowThrough(Object inValue, Object unit,
			Object outValue)
	{
		Map in  = (Map) inValue;
		Map out = (Map) outValue;
		Stmt stmt = (Stmt) unit;
		
//		out.clear();
//		out.putAll(in);
		merge(in, (Map) ((HashMap) out).clone(), out);
		
		// If this statement contains a use
		if( unitToUses.keySet().contains(stmt) )
		{
			// For each use, either add it to an existing lock, or add a new lock
			List uses = (List) unitToUses.get(stmt);
			Iterator usesIt = uses.iterator();
			if(!usesIt.hasNext()) // an empty set of uses indicates that some uses are inaccessible
			{
				lostObjects = true;
			}
			while(usesIt.hasNext())
			{
				Value use = (Value) usesIt.next();
				// if present, ok, if not, add as new group
				if(!out.containsKey(use))
				{
					out.put(new EquivalentValue(use), new Integer(groupNum++));
				}
			}
		}

		if( stmt == begin )
			out.clear();

		// if lvalue is in a group:
		//   if rvalue is in a group, group containing lvalue gets merged with group containing rvalue
		//   if rvalue is not in a group
		//		 if rvalue is a local or a static field ref, revalue gets put into lvalue's group
		//       if rvalue is an instance field ref, DO SOMETHING WITH IT?
		//       if rvalue is anything else, set "lost objects" flag
		//   lvalue gets removed from group
		if( !out.isEmpty() && stmt instanceof DefinitionStmt )
		{
			DefinitionStmt ds = (DefinitionStmt) stmt;
			EquivalentValue lvalue = new EquivalentValue(ds.getLeftOp());
			EquivalentValue rvalue = new EquivalentValue(ds.getRightOp());
			if(out.containsKey(lvalue))
			{
				Object lvaluevalue = out.get(lvalue);
				if(out.containsKey(rvalue))
				{
					Object rvaluevalue = out.get(rvalue);
					for(Iterator outEntryIt = out.entrySet().iterator(); outEntryIt.hasNext(); )
					{
						Map.Entry entry = (Map.Entry) outEntryIt.next();
						if(entry.getValue() == lvaluevalue)
							entry.setValue(rvaluevalue);
					}
				}
				else
				{
					if(rvalue.getValue() instanceof Local || rvalue.getValue() instanceof FieldRef || rvalue.getValue() instanceof ArrayRef)
						out.put(rvalue, lvaluevalue);
					else
						lostObjects = true;
				}
				out.remove(lvalue);
			}
		}
//		if(!out.isEmpty())
//			G.v().out.println("  " + out);		
	}
	
	protected void copy(Object source, Object dest)
	{
		
		Map sourceMap = (Map) source;
		Map destMap   = (Map) dest;
		
		destMap.clear();
		destMap.putAll(sourceMap);
	}
	
	protected Object entryInitialFlow()
	{
		return new HashMap();
	}
	
	protected Object newInitialFlow()
	{
		return new HashMap();
	}	
}

