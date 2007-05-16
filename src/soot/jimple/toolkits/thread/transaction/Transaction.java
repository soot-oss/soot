package soot.jimple.toolkits.thread.transaction;

import java.util.*;

import soot.jimple.toolkits.pointer.CodeBlockRWSet;
import soot.MethodOrMethodContext;
import soot.SootMethod;
import soot.Value;

class Transaction extends LockRegion
{
	public static int nextIDNum = 1; 
	
	// Information about the transactional region
	public int IDNum;
	public int nestLevel;
	public String name;
	
	public Value origLock;
	public CodeBlockRWSet read, write;
	public HashSet<Object> invokes;
	public HashSet<Object> units;
	public HashMap<Object, CodeBlockRWSet> unitToRWSet;
	public HashMap<Object, List> unitToUses; // For lockset analysis
	public boolean wholeMethod;
	
	// Information for analyzing conflicts with other transactions
	public SootMethod method;
	public int setNumber; // used for breaking the list of transactions into sets
	public TransactionGroup group;
	public HashSet<TransactionDataDependency> edges;
	public HashSet<Object> waits;
	public HashSet<Object> notifys;
	public HashSet<MethodOrMethodContext> transitiveTargets;
	
	// Locking Information
	public Value lockObject;
	public Value lockObjectArrayIndex;
	public List lockset;
	
	Transaction(boolean wholeMethod, SootMethod method, int nestLevel)
	{
		super();
		this.IDNum = nextIDNum;
		nextIDNum++;
		this.nestLevel = nestLevel;
		this.read = new CodeBlockRWSet();
		this.write = new CodeBlockRWSet();
		this.invokes = new HashSet<Object>();
		this.units = new HashSet<Object>();
		this.unitToRWSet = new HashMap<Object, CodeBlockRWSet>();
		this.unitToUses = new HashMap<Object, List>();
		this.wholeMethod = wholeMethod;
		this.method = method;
		this.setNumber = 0; // 0 = no group, -1 = DELETE
		this.group = null;
		this.edges = new HashSet<TransactionDataDependency>();
		this.waits = new HashSet<Object>();
		this.notifys = new HashSet<Object>();
		this.transitiveTargets = null;
	    this.lockObject = null;
	    this.lockObjectArrayIndex = null;
	    this.lockset = null;
	}
	
	Transaction(Transaction tn)
	{
		super(tn);
		this.IDNum = tn.IDNum;
		this.nestLevel = tn.nestLevel;
		this.origLock = tn.origLock;
		this.read = new CodeBlockRWSet(); this.read.union(tn.read);
		this.write = new CodeBlockRWSet(); this.write.union(tn.write);
		this.invokes = (HashSet<Object>) tn.invokes.clone();
		this.units = (HashSet<Object>) tn.units.clone();
		this.unitToRWSet = (HashMap<Object, CodeBlockRWSet>) tn.unitToRWSet.clone();
		this.unitToUses = (HashMap<Object, List>) tn.unitToUses.clone();
		this.wholeMethod = tn.wholeMethod;
		this.method = tn.method;
		this.setNumber = tn.setNumber;
		this.group = tn.group;
		this.edges = (HashSet<TransactionDataDependency>) tn.edges.clone();
		this.waits = (HashSet<Object>) tn.waits.clone();
		this.notifys = (HashSet<Object>) tn.notifys.clone();
		this.transitiveTargets = (HashSet<MethodOrMethodContext>) (tn.transitiveTargets == null ? null : tn.transitiveTargets.clone());
	    this.lockObject = tn.lockObject;
	    this.lockObjectArrayIndex = tn.lockObjectArrayIndex;
	    this.lockset = tn.lockset;
	}

	protected Object clone()
	{
		return new Transaction(this);
	}
}
