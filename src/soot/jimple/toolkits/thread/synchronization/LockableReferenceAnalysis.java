package soot.jimple.toolkits.thread.synchronization;

import soot.*;
import java.util.*;

import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.infoflow.*;

/**
 * Finds the set of local variables and/or references that represent all of
 * the relevant objects used in a synchronized region, as accessible at the
 * start of that region.
 * Basically this is value numbering, done in reverse, interprocedurally, and
 * only tracking the values that contribute to the given set of side effects.
 * @author Richard L. Halpert
 * @since 2007-04-19
 */
public class LockableReferenceAnalysis extends BackwardFlowAnalysis<Unit,LocksetFlowInfo>
{
	UnitGraph graph;
	SootMethod method;
	CriticalSectionAwareSideEffectAnalysis tasea;
	RWSet contributingRWSet;
	CriticalSection tn;
	Stmt begin;
	boolean lostObjects;
		
	// These two maps hold the final ref->base or ref->index relationship
	Map<Ref, EquivalentValue> refToBase;
	Map<Ref, EquivalentValue> refToIndex;
	
	static Set<SootMethod> analyzing = new HashSet<SootMethod>();
	
	public LockableReferenceAnalysis(UnitGraph g)
	{
		super(g);
		
		graph = g;
		method = g.getBody().getMethod();
		contributingRWSet = null;
		tn = null;
		begin = null;
		lostObjects = false;

		refToBase = new HashMap<Ref, EquivalentValue>();
		refToIndex = new HashMap<Ref, EquivalentValue>();

		// analysis is done on-demand, not now
	}
	
	public void printMsg(String msg)
	{
		G.v().out.print("[wjtp.tn] ");
		for(int i = 0; i < analyzing.size() - 1; i++) 
			G.v().out.print("  ");
		G.v().out.println(msg);
	}

	public List<EquivalentValue> getLocksetOf(CriticalSectionAwareSideEffectAnalysis tasea, RWSet contributingRWSet, CriticalSection tn)
	{
		analyzing.add(method);

		this.tasea = tasea;
		tasea.setExemptTransaction(tn);
		this.contributingRWSet = contributingRWSet;
		this.tn = tn;
		this.begin = (tn == null ? null : tn.beginning);
		lostObjects = false;

		doAnalysis();
		
		if(lostObjects)
		{
			printMsg("Failed lockset:");
			analyzing.remove(method);
			return null;
		}

		// STOP
		List<EquivalentValue> lockset = new ArrayList<EquivalentValue>();
		LocksetFlowInfo resultsInfo = null;
		Map<soot.EquivalentValue,java.lang.Integer> results = null;
		if(begin == null)
		{
			for (Unit u : graph) {
				resultsInfo = getFlowBefore(u); // flow before first unit
			}
		}
		else
			resultsInfo = getFlowBefore(begin); // flow before begin unit
		if(resultsInfo == null)
		{
			analyzing.remove(method);
			throw new RuntimeException("Why is getFlowBefore null???");
		}
		results = resultsInfo.groups;
			
		// Reverse the results so it maps value->keys instead of key->value
		// Then we can pick just one object (key) per group (value)
		Map<Integer, List<EquivalentValue>> reversed = new HashMap<Integer, List<EquivalentValue>>();
		for (Map.Entry<EquivalentValue, Integer> e : results.entrySet()) {
			EquivalentValue key = e.getKey();
			Integer value = e.getValue();
			
			List<EquivalentValue> keys;
			if(!reversed.containsKey(value))
			{
				keys = new ArrayList<EquivalentValue>();
				reversed.put(value, keys);
			}
			else
				keys = reversed.get(value);
			keys.add(key);
		}

		// For each group, choose the one best object to put in the lockset
		for(List<EquivalentValue> objects : reversed.values())
		{
			EquivalentValue bestLock = null;
			for(EquivalentValue object : objects)
			{
				if( bestLock == null || 
					object.getValue() instanceof IdentityRef ||
					(object.getValue() instanceof Ref && !(bestLock instanceof IdentityRef)) )
					bestLock = object;
			}
			
			Integer group = (results.get(bestLock));
			
			// record if bestLock is the base or index for a reference
			for(Ref ref : resultsInfo.refToBaseGroup.keySet())
			{
				if(group == resultsInfo.refToBaseGroup.get(ref))
					refToBase.put(ref, bestLock);
			}
			
			for(Ref ref : resultsInfo.refToIndexGroup.keySet())
			{
				if(group == resultsInfo.refToIndexGroup.get(ref))
					refToIndex.put(ref, bestLock);
			}

			// add bestLock to lockset if it's from a group that requires a lock
			if(group >= 0)
				lockset.add(bestLock); // a lock for each positively-numbered group
		}

		if(lockset.size() == 0)
		{
			printMsg("Empty lockset: S" + lockset.size() + "/G" + reversed.keySet().size() + "/O" + results.keySet().size() + " Method:" + method + " Begin:" + begin + " Result:" + results + " RW:" + contributingRWSet);
			printMsg("|= results:" + results + " refToBaseGroup:" + resultsInfo.refToBaseGroup);
		}	
		else
		{
			printMsg("Healthy lockset: S" + lockset.size() + "/G" + reversed.keySet().size() + "/O" + results.keySet().size() + " " + lockset + " refToBase:" + refToBase + " refToIndex:" + refToIndex);
			printMsg("|= results:" + results + " refToBaseGroup:" + resultsInfo.refToBaseGroup);
		}

		analyzing.remove(method);
		
		return lockset;
	}
	
	public EquivalentValue baseFor(Ref ref)
	{
		return refToBase.get(ref);
	}
	
	public EquivalentValue indexFor(Ref ref)
	{
		return refToIndex.get(ref);
	}
	
	protected void merge(LocksetFlowInfo in1, LocksetFlowInfo in2, LocksetFlowInfo out)
	{			
		LocksetFlowInfo tmpInfo = new LocksetFlowInfo();
		
		// union of the two maps, 
		// When the same key is present, if the groups are different, then they get merged
		
		// (ensure every new group gets a new number)
		// copy in1 into out
		copy(in1, out);
		// copy in2 into tmp
		copy(in2, tmpInfo);
		// for each tmpentry in tmpMap
		for(EquivalentValue key : tmpInfo.groups.keySet())
		{
			Integer newvalue = tmpInfo.groups.get(key);
			
			//  if the key ISN'T in outMap, add it
			if(!out.groups.containsKey(key))
			{
				out.groups.put(key, newvalue);
			}
			
			//  if the key IS in outMap with the same value, do nothing
			
			//  if the key IS in outMap with a different value,
			else if(out.groups.get(key) != tmpInfo.groups.get(key))
			{
				// replace oldvalue with value in both maps,
				// and also in the base and index tracker maps
				Object oldvalue = out.groups.get(key);
				
				// every entry in outMap with the old value gets the new value
				for (Map.Entry<?, Integer> entry : out.groups.entrySet())
				{
					// if the current value == oldvalue, change it to newvalue
					if(entry.getValue() == oldvalue)
						entry.setValue(newvalue);
				}
				
				// every entry in tmpMap with the old value gets the new value
				for (Map.Entry<?, Integer> entry : tmpInfo.groups.entrySet())
				{
					// if the current value == oldvalue, change it to newvalue
					if(entry.getValue() == oldvalue)
						entry.setValue(newvalue);
				}
								
				// every entry in refToBaseGroup with the old value gets the new value
				for (Map.Entry<?, Integer> entry : out.refToBaseGroup.entrySet())
				{
					// if the current value == oldvalue, change it to newvalue
					if (entry.getValue() == oldvalue)
						entry.setValue(newvalue);
				}

				// every entry in refToIndexGroup with the old value gets the new value
				for (Map.Entry<?, Integer> entry : out.refToIndexGroup.entrySet())
				{
					// if the current value == oldvalue, change it to newvalue
					if(entry.getValue() == oldvalue)
						entry.setValue(newvalue);
				}

				// every entry in refToBaseGroup with the old value gets the new value
				for (Map.Entry<?, Integer> entry : tmpInfo.refToBaseGroup.entrySet())
				{
					// if the current value == oldvalue, change it to newvalue
					if (entry.getValue() == oldvalue)
						entry.setValue(newvalue);
				}

				// every entry in refToIndexGroup with the old value gets the new value
				for (Map.Entry<?, Integer> entry : tmpInfo.refToIndexGroup.entrySet())
				{
					// if the current value == oldvalue, change it to newvalue
					if (entry.getValue() == oldvalue)
						entry.setValue(newvalue);
				}
			}
		}
		for(Ref ref : tmpInfo.refToBaseGroup.keySet())
		{
			if(!out.refToBaseGroup.containsKey(ref))
				out.refToBaseGroup.put(ref, tmpInfo.refToBaseGroup.get(ref));
		}
		for(Ref ref : tmpInfo.refToIndexGroup.keySet())
		{
			if(!out.refToIndexGroup.containsKey(ref))
				out.refToIndexGroup.put(ref, tmpInfo.refToIndexGroup.get(ref));
		}
	}
	
	// adds a value from a subanalysis into this analysis, and returns the group it gets put into
	public Integer addFromSubanalysis(LocksetFlowInfo outInfo, LockableReferenceAnalysis la, Stmt stmt, Value lock)
	{
		Map<EquivalentValue, Integer> out = outInfo.groups;
		InvokeExpr ie = stmt.getInvokeExpr();
		printMsg("Attempting to bring up '" + lock + "' from inner lockset at (" + stmt.hashCode() + ") " + stmt);
		if( lock instanceof ThisRef && ie instanceof InstanceInvokeExpr)
		{
			Value use = ((InstanceInvokeExpr)ie).getBase();
			if(!out.containsKey(new EquivalentValue(use)))
			{
				int newGroup = groupNum++;
				out.put(new EquivalentValue(use), newGroup);
				return newGroup;
			}
			return out.get(new EquivalentValue(use));
		}
		else if( lock instanceof ParameterRef )
		{
			Value use = ie.getArg( ((ParameterRef)lock).getIndex() );
			if(!out.containsKey(new EquivalentValue(use)))
			{
				int newGroup = groupNum++;
				out.put(new EquivalentValue(use), newGroup);
				return newGroup;
			}
			return out.get(new EquivalentValue(use));
		}
		else if( lock instanceof StaticFieldRef )
		{
			Value use = lock;
			if(!out.containsKey(new EquivalentValue(use)))
			{
				int newGroup = groupNum++;
				out.put(new EquivalentValue(use), newGroup);
				return newGroup;
			}
			return out.get(new EquivalentValue(use));
		}
		else if( lock instanceof InstanceFieldRef )
		{
			// Step 0: redirect fakejimplelocals to this
			if(((InstanceFieldRef)lock).getBase() instanceof FakeJimpleLocal)
				((FakeJimpleLocal)((InstanceFieldRef)lock).getBase()).setInfo(this);
				
			// Step 1: make sure base is accessible (process it)
			// Step 2: get the group number (here) for the base
			EquivalentValue baseEqVal = la.baseFor((Ref)lock);
			if(baseEqVal == null)
			{
				printMsg("Lost Object from inner Lockset (InstanceFieldRef w/ previously lost base) at " + stmt);
				return 0;
			}
			Value base = baseEqVal.getValue();
			
			Integer baseGroup = addFromSubanalysis(outInfo, la, stmt, base);
			if(baseGroup == 0)
			{
				printMsg("Lost Object from inner Lockset (InstanceFieldRef w/ newly lost base) at " + stmt);
				return 0;
			}
			
			// Step 3: put the FieldRef and basegroupnum into refToBaseGroup
			outInfo.refToBaseGroup.put((Ref)lock, baseGroup); // track relationship between ref and base group

			// Step 4: put the FieldRef into a new group in 'out' unless already there
//			InstanceFieldRef ifr = (InstanceFieldRef) lock;
//			Local oldbase = (Local) ifr.getBase();
//			Local newbase = new FakeJimpleLocal("fakethis", oldbase.getType(), oldbase);
//			Value node = Jimple.v().newInstanceFieldRef(newbase, ifr.getField().makeRef());
//			EquivalentValue nodeEqVal = new EquivalentValue( node ); // fake thisLocal

			Value use = lock;
			if(!out.containsKey(new EquivalentValue(use)))
			{
				int newGroup = groupNum++;
				out.put(new EquivalentValue(use), newGroup);
				return newGroup;
			}
			return out.get(new EquivalentValue(use));
		}
		else if( lock instanceof ArrayRef )
		{
			// Step 0: redirect fakejimplelocals to this
			if(((ArrayRef)lock).getBase() instanceof FakeJimpleLocal)
				((FakeJimpleLocal)((ArrayRef)lock).getBase()).setInfo(this);
			if(((ArrayRef)lock).getIndex() instanceof FakeJimpleLocal)
				((FakeJimpleLocal)((ArrayRef)lock).getIndex()).setInfo(this);

			// Step 1: make sure base is accessible (process it)
			// Step 2: get the group number (here) for the base
			EquivalentValue baseEqVal = la.baseFor((Ref)lock);
			EquivalentValue indexEqVal = la.indexFor((Ref)lock);
			if(baseEqVal == null)
			{
				printMsg("Lost Object from inner Lockset (InstanceFieldRef w/ previously lost base) at " + stmt);
				return 0;
			}
			if(indexEqVal == null)
			{
				printMsg("Lost Object from inner Lockset (InstanceFieldRef w/ previously lost index) at " + stmt);
				return 0;
			}
			Value base = baseEqVal.getValue();
			Value index = indexEqVal.getValue();
			
			Integer baseGroup = addFromSubanalysis(outInfo, la, stmt, base);
			if(baseGroup == 0)
			{
				printMsg("Lost Object from inner Lockset (InstanceFieldRef w/ newly lost base) at " + stmt);
				return 0;
			}
			Integer indexGroup = addFromSubanalysis(outInfo, la, stmt, index);
			if(indexGroup == 0)
			{
				printMsg("Lost Object from inner Lockset (InstanceFieldRef w/ newly lost index) at " + stmt);
				return 0;
			}
			
			
			// Step 3: put the FieldRef and basegroupnum into refToBaseGroup
			outInfo.refToBaseGroup.put((Ref)lock, baseGroup); // track relationship between ref and base group
			outInfo.refToIndexGroup.put((Ref)lock, indexGroup); // track relationship between ref and index group

			// Step 4: put the FieldRef into a new group in 'out' unless already there
//			InstanceFieldRef ifr = (InstanceFieldRef) lock;
//			Local oldbase = (Local) ifr.getBase();
//			Local newbase = new FakeJimpleLocal("fakethis", oldbase.getType(), oldbase);
//			Value node = Jimple.v().newInstanceFieldRef(newbase, ifr.getField().makeRef());
//			EquivalentValue nodeEqVal = new EquivalentValue( node ); // fake thisLocal

			Value use = lock;
			if(!out.containsKey(new EquivalentValue(use)))
			{
				int newGroup = groupNum++;
				out.put(new EquivalentValue(use), newGroup);
				return newGroup;
			}
			return out.get(new EquivalentValue(use));
		}
		else if( lock instanceof Constant )
		{
			Value use = lock;
			if(!out.containsKey(new EquivalentValue(use)))
			{
				int newGroup = groupNum++;
				out.put(new EquivalentValue(use), newGroup);
				return newGroup;
			}
			return out.get(new EquivalentValue(use));
		}
		else
		{
			printMsg("Lost Object from inner Lockset (unknown or unhandled object type) at " + stmt);
		}
		return 0; // failure code... the only number that is never a valid group
	}
	
	static int groupNum = 1;
	
	@Override
	protected void flowThrough(LocksetFlowInfo inInfo, Unit u, LocksetFlowInfo outInfo)
	{		
		copy(inInfo, outInfo);
		
		Stmt stmt = (Stmt) u;
		
		Map<EquivalentValue, Integer> out = outInfo.groups;

		// If this statement contains a contributing use
		if((tn == null || tn.units.contains(stmt)) && !lostObjects)
		{
			// Prepare the RW set for the statement
			CodeBlockRWSet stmtRW = null;
			Set<Value> allUses = new HashSet<Value>();
			RWSet stmtRead = tasea.readSet(method, stmt, tn, allUses);
			if(stmtRead != null)
				stmtRW = (CodeBlockRWSet) stmtRead;
			RWSet stmtWrite = tasea.writeSet(method, stmt, tn, allUses);
			if(stmtWrite != null)
			{
				if(stmtRW != null)
					stmtRW.union(stmtWrite);
				else
					stmtRW = (CodeBlockRWSet) stmtWrite;
			}
			
			// If the stmtRW intersects the contributingRW
			if( stmtRW != null && stmtRW.hasNonEmptyIntersection(contributingRWSet) )
			{
				List<Value> uses = new ArrayList<Value>();
				Iterator<Value> allUsesIt = allUses.iterator();
				while(allUsesIt.hasNext())
				{
					Value vEqVal = allUsesIt.next();
					Value v = vEqVal; //((vEqVal instanceof EquivalentValue) ? ((EquivalentValue) vEqVal).getValue() : vEqVal);
					
					if(stmt.containsFieldRef())
					{
						FieldRef fr = stmt.getFieldRef();
						if(fr instanceof InstanceFieldRef)
						{
							if(((InstanceFieldRef) fr).getBase() == v)
								v = fr;
						}
					}
					if(stmt.containsArrayRef())
					{
						ArrayRef ar = stmt.getArrayRef();
						if( ar.getBase() == v )
							v = ar;
					}
					
					// it would be better to just check if the value has reaching objects in common with the bases of the contributingRWSet
					RWSet valRW = tasea.valueRWSet(v, method, stmt, tn);
					if(	valRW != null && valRW.hasNonEmptyIntersection(contributingRWSet) )
						uses.add(vEqVal);
				}
				
				if(stmt.containsInvokeExpr())
				{
					InvokeExpr ie = stmt.getInvokeExpr();
					SootMethod called = ie.getMethod();
					if(called.isConcrete())
					{
						if( called.getDeclaringClass().toString().startsWith("java.util") ||
							called.getDeclaringClass().toString().startsWith("java.lang") )
						{
							// these uses should already be in use list
							if(uses.size() <= 0)
							{
								printMsg("Lost Object at library call at " + stmt);
								lostObjects = true;
							}
						}
						else
						{
							// find and add this callsite's uses
							if(!analyzing.contains(called))
							{
								LockableReferenceAnalysis la = new LockableReferenceAnalysis(new BriefUnitGraph(called.retrieveActiveBody()));
								List<EquivalentValue> innerLockset = la.getLocksetOf(tasea, stmtRW, null);
								
								if(innerLockset == null || innerLockset.size() <= 0)
								{
									printMsg("innerLockset: " + (innerLockset == null ? "Lost Objects" : "Mysteriously Empty"));
									lostObjects = true;
								}
								else
								{
									printMsg("innerLockset: " + innerLockset.toString());
									
									// Add used receiver and args to uses
									for( EquivalentValue lockEqVal : innerLockset )
									{
										Value lock = lockEqVal.getValue();
										if(addFromSubanalysis(outInfo, la, stmt, lock) == 0)
										{
											lostObjects = true;
											printMsg("Lost Object in addFromSubanalysis()");
											break;
										}
									}
								}
							}
							else
							{
								lostObjects = true;
								printMsg("Lost Object due to recursion " + stmt);
							}
						}
					}
					else if(uses.size() <= 0)
					{
						lostObjects = true;
						printMsg("Lost Object from non-concrete method call at " + stmt);
					}
				}
				else if(uses.size() <= 0)
				{
					lostObjects = true;
					printMsg("Lost Object SOMEHOW at " + stmt);
				}

				// For each use, either add it to an existing lock, or add a new lock
				Iterator<Value> usesIt = uses.iterator();

				while(usesIt.hasNext() && !lostObjects)
				{
					Value use = (Value) usesIt.next();
					// if present, ok, if not, add as new group

					if(use instanceof InstanceFieldRef)
					{
						InstanceFieldRef ifr = (InstanceFieldRef) use;
						Local oldbase = (Local) ifr.getBase();
						if(!(oldbase instanceof FakeJimpleLocal))
						{
							Local newbase = new FakeJimpleLocal("fakethis", oldbase.getType(), oldbase, this);
							Value node = Jimple.v().newInstanceFieldRef(newbase, ifr.getField().makeRef());
							EquivalentValue nodeEqVal = new EquivalentValue( node ); // fake thisLocal
							
							use = node;
						}
					}
					else if(use instanceof ArrayRef) // requires special packaging
					{
						ArrayRef ar = (ArrayRef) use;
						Local oldbase = (Local) ar.getBase();
						Value oldindex = ar.getIndex();
						if(!(oldbase instanceof FakeJimpleLocal))
						{
							Local newbase = new FakeJimpleLocal("fakethis", oldbase.getType(), oldbase, this);
							Value newindex = (oldindex instanceof Local) ? new FakeJimpleLocal("fakeindex", oldindex.getType(), (Local) oldindex, this) : oldindex;
							Value node = Jimple.v().newArrayRef(newbase, newindex);
							EquivalentValue nodeEqVal = new EquivalentValue( node ); // fake thisLocal

							use = node;
						}
					}


					if(!out.containsKey(new EquivalentValue(use)))
					{
						out.put(new EquivalentValue(use), groupNum++);
					}
				}
			}
		}

		if( graph.getBody().getUnits().getSuccOf(stmt) == begin )
			out.clear();

		// if lvalue is in a group:
		//   if rvalue is in a group, group containing lvalue gets merged with group containing rvalue
		//   if rvalue is not in a group
		//		 if rvalue is a local or a static field ref, rvalue gets put into lvalue's group
		//       if rvalue is an instance field ref, DO SOMETHING WITH IT?
		//       if rvalue is anything else, set "lost objects" flag
		//   lvalue gets removed from group
		if( (tn == null || tn.units.contains(stmt)) && !out.isEmpty() && stmt instanceof DefinitionStmt && !lostObjects )
		{
			DefinitionStmt ds = (DefinitionStmt) stmt;

			// Retrieve and package the lvalue
			EquivalentValue lvalue = new EquivalentValue(ds.getLeftOp());
			if(ds.getLeftOp() instanceof InstanceFieldRef) // requires special packaging
			{
				InstanceFieldRef ifr = (InstanceFieldRef) ds.getLeftOp();
				Local oldbase = (Local) ifr.getBase();
				if(!(oldbase instanceof FakeJimpleLocal))
				{
					Local newbase = new FakeJimpleLocal("fakethis", oldbase.getType(), oldbase, this);
					Value node = Jimple.v().newInstanceFieldRef(newbase, ifr.getField().makeRef());
					EquivalentValue nodeEqVal = new EquivalentValue( node ); // fake thisLocal

					lvalue = nodeEqVal;
				}
			}
			else if(ds.getLeftOp() instanceof ArrayRef) // requires special packaging
			{
				ArrayRef ar = (ArrayRef) ds.getLeftOp();
				Local oldbase = (Local) ar.getBase();
				Value oldindex = ar.getIndex();
				if(!(oldbase instanceof FakeJimpleLocal))
				{
					Local newbase = new FakeJimpleLocal("fakethis", oldbase.getType(), oldbase, this);
					Value newindex = (oldindex instanceof Local) ? new FakeJimpleLocal("fakeindex", oldindex.getType(), (Local) oldindex, this) : oldindex;
					Value node = Jimple.v().newArrayRef(newbase, newindex);
					EquivalentValue nodeEqVal = new EquivalentValue( node ); // fake thisLocal

					lvalue = nodeEqVal;
				}
			}
			
			// Retrieve and package the rvalue
			EquivalentValue rvalue = new EquivalentValue(ds.getRightOp());
			if(ds.getRightOp() instanceof CastExpr) // requires unpackaging
				rvalue = new EquivalentValue( ((CastExpr)ds.getRightOp()).getOp() );
			else if(ds.getRightOp() instanceof InstanceFieldRef) // requires special packaging
			{
				InstanceFieldRef ifr = (InstanceFieldRef) ds.getRightOp();
				Local oldbase = (Local) ifr.getBase();
				if(!(oldbase instanceof FakeJimpleLocal))
				{
					Local newbase = new FakeJimpleLocal("fakethis", oldbase.getType(), oldbase, this);
					Value node = Jimple.v().newInstanceFieldRef(newbase, ifr.getField().makeRef());
					EquivalentValue nodeEqVal = new EquivalentValue( node ); // fake thisLocal

					rvalue = nodeEqVal;
				}
			}
			else if(ds.getRightOp() instanceof ArrayRef) // requires special packaging
			{
				ArrayRef ar = (ArrayRef) ds.getRightOp();
				Local oldbase = (Local) ar.getBase();
				Value oldindex = ar.getIndex();
				if(!(oldbase instanceof FakeJimpleLocal))
				{
					Local newbase = new FakeJimpleLocal("fakethis", oldbase.getType(), oldbase, this);
					Value newindex = (oldindex instanceof Local) ? new FakeJimpleLocal("fakeindex", oldindex.getType(), (Local) oldindex, this) : oldindex;
					Value node = Jimple.v().newArrayRef(newbase, newindex);
					EquivalentValue nodeEqVal = new EquivalentValue( node ); // fake thisLocal

					rvalue = nodeEqVal;
				}
			}
			
			// Perform merging, unmerging, additions, and subtractions to flowset
			if(out.containsKey(lvalue))
			{
				Integer lvaluevalue = out.get(lvalue);
				if( stmt instanceof IdentityStmt )
				{
					if(out.containsKey(rvalue))
					{
						// Merge the two groups
						Integer rvaluevalue = out.get(rvalue);
						
						// every entry in 'out' with the left value gets the right value
						for (Map.Entry<?,Integer> entry : out.entrySet()) {
							if(entry.getValue() == lvaluevalue)
								entry.setValue(rvaluevalue);
						}

						// every entry in refToBaseGroup with the left value gets the right value
						for (Map.Entry<?,Integer> entry : outInfo.refToBaseGroup.entrySet())
						{
							// if the current value == oldvalue, change it to newvalue
							if(entry.getValue() == lvaluevalue)
								entry.setValue(rvaluevalue);
						}

						// every entry in refToIndexGroup with the left value gets the right value
						for (Map.Entry<?,Integer> entry : outInfo.refToIndexGroup.entrySet())
						{
							// if the current value == oldvalue, change it to newvalue
							if(entry.getValue() == lvaluevalue)
								entry.setValue(rvaluevalue);
						}
					}
					else
					{
						out.put(rvalue, lvaluevalue);
					}
				}
				else // if( !(lvalue.getValue() instanceof StaticFieldRef && !(lvalue.getValue().getType() instanceof RefLikeType)) )
				{
					if(out.containsKey(rvalue))
					{
						// Merge the two groups
						Integer rvaluevalue = out.get(rvalue);
						
						// every entry in 'out' with the left value gets the right value
						for (Map.Entry<?,Integer> entry : out.entrySet()) {
							if(entry.getValue() == lvaluevalue)
								entry.setValue(rvaluevalue);
						}

						// every entry in refToBaseGroup with the left value gets the right value
						for (Map.Entry<?,Integer> entry : outInfo.refToBaseGroup.entrySet())
						{
							// if the current value == oldvalue, change it to newvalue
							if(entry.getValue() == lvaluevalue)
								entry.setValue(rvaluevalue);
						}

						// every entry in refToIndexGroup with the left value gets the right value
						for (Map.Entry<?,Integer> entry : outInfo.refToIndexGroup.entrySet())
						{
							// if the current value == oldvalue, change it to newvalue
							if(entry.getValue() == lvaluevalue)
								entry.setValue(rvaluevalue);
						}
					}
					else
					{
						if( rvalue.getValue() instanceof Local || 
							rvalue.getValue() instanceof StaticFieldRef ||
							rvalue.getValue() instanceof Constant )
							out.put(rvalue, lvaluevalue); // value not lost
						else if(rvalue.getValue() instanceof InstanceFieldRef)
						{
							// value is not lost, but it is now dependant on both fieldref and base
							// rvalue has already been packaged w/ a fakethis
							InstanceFieldRef ifr = (InstanceFieldRef) rvalue.getValue();
							FakeJimpleLocal newbase = (FakeJimpleLocal) ifr.getBase();
							Local oldbase = newbase.getRealLocal();
							
							out.put(rvalue, lvaluevalue);

							Integer baseGroup;
							if(out.containsKey(new EquivalentValue(oldbase)))
								baseGroup = out.get(new EquivalentValue(oldbase));
							else
								baseGroup = new Integer(-(groupNum++));
							if(!outInfo.refToBaseGroup.containsKey(ifr))
								outInfo.refToBaseGroup.put(ifr, baseGroup); // track relationship between ref and base group
							out.put(new EquivalentValue(oldbase), baseGroup); // track base group, no lock required
						}
						else if(rvalue.getValue() instanceof ArrayRef)
						{
							// value is not lost, but it is now dependant on all of arrayref, base, and index
							// we need to somehow note that, if used as a lock, arrayref's base and index must come from the new groups we create here
							ArrayRef ar = (ArrayRef) rvalue.getValue();
							FakeJimpleLocal newbase = (FakeJimpleLocal) ar.getBase();
							Local oldbase = newbase.getRealLocal();
							FakeJimpleLocal newindex = (ar.getIndex() instanceof FakeJimpleLocal) ? (FakeJimpleLocal) ar.getIndex() : null;
							Value oldindex = (newindex != null) ? (Value) newindex.getRealLocal() : ar.getIndex(); // it's a FJL or a Constant
							
							out.put(rvalue, lvaluevalue);
							
							Integer indexGroup;
							if(out.containsKey(new EquivalentValue(oldindex)))
								indexGroup = out.get(new EquivalentValue(oldindex));
							else
								indexGroup = new Integer(-(groupNum++));
							if(!outInfo.refToIndexGroup.containsKey(ar))
								outInfo.refToIndexGroup.put(ar, indexGroup); // track relationship between ref and index group
							out.put(new EquivalentValue(oldindex), indexGroup); // track index group, no lock required

							Integer baseGroup;
							if(out.containsKey(new EquivalentValue(oldbase)))
								baseGroup = out.get(new EquivalentValue(oldbase));
							else
								baseGroup = new Integer(-(groupNum++));
							if(!outInfo.refToBaseGroup.containsKey(ar))
								outInfo.refToBaseGroup.put(ar, baseGroup); // track relationship between ref and base group
							out.put(new EquivalentValue(oldbase), baseGroup); // track base group, no lock required
						}
						else if(rvalue.getValue() instanceof AnyNewExpr) // value doesn't need locking!
						{
							// ok to lose these values
							printMsg("Ignored Object (assigned new value) at " + stmt);
						}
						else
						{
							printMsg("Lost Object (assigned unacceptable value) at " + stmt);
							lostObjects = true;
						}
					}
					out.remove(lvalue);
				}
			}
		}
	}
	
	protected void copy(LocksetFlowInfo sourceInfo, LocksetFlowInfo destInfo)
	{				
		destInfo.groups.clear();
		destInfo.groups.putAll(sourceInfo.groups);
		
		destInfo.refToBaseGroup.clear();
		destInfo.refToBaseGroup.putAll(sourceInfo.refToBaseGroup);
		
		destInfo.refToIndexGroup.clear();
		destInfo.refToIndexGroup.putAll(sourceInfo.refToIndexGroup);
	}
		
	protected LocksetFlowInfo newInitialFlow()
	{
		return new LocksetFlowInfo();
	}	
}

class LocksetFlowInfo
{
	public Map<EquivalentValue, Integer> groups; // map from each value to a value number
	
	// These two maps track the relationship between array & field refs
	// and the base & index groups they come from
	public Map<Ref, Integer> refToBaseGroup; // map from ArrayRef or InstanceFieldRef to base group number
	public Map<Ref, Integer> refToIndexGroup; // map from ArrayRef to index group number
	
	public LocksetFlowInfo()
	{
		groups = new HashMap<EquivalentValue, Integer>();
		
		refToBaseGroup = new HashMap<Ref, Integer>();
		refToIndexGroup = new HashMap<Ref, Integer>();
	}
	
	public Object clone()
	{
		LocksetFlowInfo ret = new LocksetFlowInfo();

		ret.groups.putAll(groups);		
		ret.refToBaseGroup.putAll(refToBaseGroup);
		ret.refToIndexGroup.putAll(refToIndexGroup);
		
		return ret;
	}
	
	public int hashCode()
	{
		return groups.hashCode(); // + refToBaseGroup.keySet().hashCode() + refToIndexGroup.keySet().hashCode();
	}
	
	public boolean equals(Object o)
	{
		if( o instanceof LocksetFlowInfo )
		{
			LocksetFlowInfo other = (LocksetFlowInfo) o;
			return groups.equals(other.groups);
//				 && 
//				   refToBaseGroup.keySet().equals(other.refToBaseGroup.keySet()) && 
//				   refToIndexGroup.keySet().equals(other.refToIndexGroup.keySet());
		}
		return false;
	}
}

