package soot.jimple.toolkits.thread.transaction;

import soot.Local;
import soot.jimple.Stmt;
import soot.jimple.EnterMonitorStmt;
import soot.toolkits.scalar.Pair;
import java.util.*;

import soot.jimple.toolkits.infoflow.AbstractDataSource;
import soot.jimple.toolkits.pointer.CodeBlockRWSet;
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
	public HashSet invokes;
	public HashSet units;
	public HashMap unitToRWSet;
	public HashMap unitToUses; // For lockset analysis
	public boolean wholeMethod;
	
	// Information for analyzing conflicts with other transactions
	public SootMethod method;
	public int setNumber; // used for breaking the list of transactions into sets
	public TransactionGroup group;
	public HashSet edges;
	public HashSet waits;
	public HashSet notifys;
	public HashSet transitiveTargets;
	
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
		this.invokes = new HashSet();
		this.units = new HashSet();
		this.unitToRWSet = new HashMap();
		this.unitToUses = new HashMap();
		this.wholeMethod = wholeMethod;
		this.method = method;
		this.setNumber = 0; // 0 = no group, -1 = DELETE
		this.group = null;
		this.edges = new HashSet();
		this.waits = new HashSet();
		this.notifys = new HashSet();
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
		this.invokes = (HashSet) tn.invokes.clone();
		this.units = (HashSet) tn.units.clone();
		this.unitToRWSet = (HashMap) tn.unitToRWSet.clone();
		this.unitToUses = (HashMap) tn.unitToUses.clone();
		this.wholeMethod = tn.wholeMethod;
		this.method = tn.method;
		this.setNumber = tn.setNumber;
		this.group = tn.group;
		this.edges = (HashSet) tn.edges.clone();
		this.waits = (HashSet) tn.waits.clone();
		this.notifys = (HashSet) tn.notifys.clone();
		this.transitiveTargets = (HashSet) (tn.transitiveTargets == null ? null : tn.transitiveTargets.clone());
	    this.lockObject = tn.lockObject;
	    this.lockObjectArrayIndex = tn.lockObjectArrayIndex;
	    this.lockset = tn.lockset;
	}

	protected Object clone()
	{
		return new Transaction(this);
	}
}
