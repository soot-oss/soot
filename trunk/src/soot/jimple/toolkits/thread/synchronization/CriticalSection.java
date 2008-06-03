package soot.jimple.toolkits.thread.synchronization;

import java.util.*;

import soot.jimple.toolkits.pointer.CodeBlockRWSet;
import soot.*;

class CriticalSection extends SynchronizedRegion
{
	public static int nextIDNum = 1; 
	
	// Information about the transactional region
	public int IDNum;
	public int nestLevel;
	public String name;
	
	public Value origLock;
	public CodeBlockRWSet read, write;
	public HashSet<Unit> invokes;
	public HashSet<Unit> units;
	public HashMap<Unit, CodeBlockRWSet> unitToRWSet;
	public HashMap<Unit, List> unitToUses; // For lockset analysis
	public boolean wholeMethod;
	
	// Information for analyzing conflicts with other transactions
	public SootMethod method;
	public int setNumber; // used for breaking the list of transactions into sets
	public CriticalSectionGroup group;
	public HashSet<CriticalSectionDataDependency> edges;
	public HashSet<Unit> waits;
	public HashSet<Unit> notifys;
	public HashSet<MethodOrMethodContext> transitiveTargets;
	
	// Locking Information
	public Value lockObject;
	public Value lockObjectArrayIndex;
	public List<EquivalentValue> lockset;
	
	CriticalSection(boolean wholeMethod, SootMethod method, int nestLevel)
	{
		super();
		this.IDNum = nextIDNum;
		nextIDNum++;
		this.nestLevel = nestLevel;
		this.read = new CodeBlockRWSet();
		this.write = new CodeBlockRWSet();
		this.invokes = new HashSet<Unit>();
		this.units = new HashSet<Unit>();
		this.unitToRWSet = new HashMap<Unit, CodeBlockRWSet>();
		this.unitToUses = new HashMap<Unit, List>();
		this.wholeMethod = wholeMethod;
		this.method = method;
		this.setNumber = 0; // 0 = no group, -1 = DELETE
		this.group = null;
		this.edges = new HashSet<CriticalSectionDataDependency>();
		this.waits = new HashSet<Unit>();
		this.notifys = new HashSet<Unit>();
		this.transitiveTargets = null;
	    this.lockObject = null;
	    this.lockObjectArrayIndex = null;
	    this.lockset = null;
	}
	
	CriticalSection(CriticalSection tn)
	{
		super(tn);
		this.IDNum = tn.IDNum;
		this.nestLevel = tn.nestLevel;
		this.origLock = tn.origLock;
		this.read = new CodeBlockRWSet(); this.read.union(tn.read);
		this.write = new CodeBlockRWSet(); this.write.union(tn.write);
		this.invokes = (HashSet<Unit>) tn.invokes.clone();
		this.units = (HashSet<Unit>) tn.units.clone();
		this.unitToRWSet = (HashMap<Unit, CodeBlockRWSet>) tn.unitToRWSet.clone();
		this.unitToUses = (HashMap<Unit, List>) tn.unitToUses.clone();
		this.wholeMethod = tn.wholeMethod;
		this.method = tn.method;
		this.setNumber = tn.setNumber;
		this.group = tn.group;
		this.edges = (HashSet<CriticalSectionDataDependency>) tn.edges.clone();
		this.waits = (HashSet<Unit>) tn.waits.clone();
		this.notifys = (HashSet<Unit>) tn.notifys.clone();
		this.transitiveTargets = (HashSet<MethodOrMethodContext>) (tn.transitiveTargets == null ? null : tn.transitiveTargets.clone());
	    this.lockObject = tn.lockObject;
	    this.lockObjectArrayIndex = tn.lockObjectArrayIndex;
	    this.lockset = tn.lockset;
	}

	protected Object clone()
	{
		return new CriticalSection(this);
	}
	
	public String toString()
	{
		return name;
	}
}
