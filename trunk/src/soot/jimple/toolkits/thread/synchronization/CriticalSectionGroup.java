package soot.jimple.toolkits.thread.synchronization;

import java.util.*;
import soot.*;
import soot.jimple.toolkits.pointer.RWSet;
import soot.jimple.toolkits.pointer.CodeBlockRWSet;

class CriticalSectionGroup implements Iterable<CriticalSection>
{
	int groupNum; 
	
	// Information about the group members
	List<CriticalSection> criticalSections;
	
	// Group read/write set
	RWSet rwSet;

	// Information about the selected lock(s)
	public boolean isDynamicLock; // is lockObject actually dynamic? or is it a static ref?
	public boolean useDynamicLock; // use one dynamic lock per tn
	public Value lockObject;
	public boolean useLocksets;
	
	public CriticalSectionGroup(int groupNum)
	{
		this.groupNum = groupNum;
		this.criticalSections = new ArrayList<CriticalSection>();
		this.rwSet = new CodeBlockRWSet();
		
		this.isDynamicLock = false;
		this.useDynamicLock = false;
		this.lockObject = null;
		this.useLocksets = false;
	}
	
	public int num()
	{
		return groupNum;
	}
	
	public int size()
	{
		return criticalSections.size();
	}
	
	public void add(CriticalSection tn)
	{
		tn.setNumber = groupNum;
		tn.group = this;
		if(!criticalSections.contains(tn))
			criticalSections.add(tn);
	}
	
	public boolean contains(CriticalSection tn)
	{
		return criticalSections.contains(tn);
	}
	
	public Iterator<CriticalSection> iterator()
	{
		return criticalSections.iterator();
	}
	
	public void mergeGroups(CriticalSectionGroup other)
	{
		if(other == this)
			return;
			
		Iterator<CriticalSection> tnIt = other.criticalSections.iterator();
		while(tnIt.hasNext())
		{
			CriticalSection tn = tnIt.next();
			add(tn);
		}
		other.criticalSections.clear();
	}
}
