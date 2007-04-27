package soot.jimple.toolkits.thread.transaction;

import soot.Local;
import soot.jimple.Stmt;
import soot.jimple.EnterMonitorStmt;
import java.util.*;

import soot.jimple.toolkits.infoflow.AbstractDataSource;
import soot.jimple.toolkits.pointer.CodeBlockRWSet;
import soot.SootMethod;
import soot.Value;

class Transaction
{
	public static int nextIDNum = 1; 
	
	// Information about the transactional region
	public int IDNum;
	public int nestLevel;
	public String name;
	public Stmt begin;
	public Value origLock;
	public Vector ends;
	public CodeBlockRWSet read, write;
	public HashSet invokes;
	public HashSet units;
	public HashMap unitToRWSet;
	public HashMap unitToUses; // For lockset analysis
	public Stmt prepStmt;
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
	
	Transaction(Stmt begin, boolean wholeMethod, SootMethod method, int nestLevel)
	{
		this.IDNum = nextIDNum;
		nextIDNum++;
		this.nestLevel = nestLevel;
		this.begin = begin;
		if(begin != null && begin instanceof EnterMonitorStmt)
			this.origLock = (Local) ((EnterMonitorStmt) begin).getOp();
		else
		{
			if(wholeMethod)
			{
				if(method.isStatic())
					this.origLock = new AbstractDataSource( method.getDeclaringClass().getName() + ".class" ); // a dummy type meant for display
				else
					this.origLock = method.retrieveActiveBody().getThisLocal();
			}
			else
				this.origLock = null;
		}
		this.ends = new Vector();
		this.read = new CodeBlockRWSet();
		this.write = new CodeBlockRWSet();
		this.invokes = new HashSet();
		this.units = new HashSet();
		this.unitToRWSet = new HashMap();
		this.unitToUses = new HashMap();
		this.prepStmt = null;
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
		this.IDNum = tn.IDNum;
		this.nestLevel = tn.nestLevel;
		this.begin = tn.begin;
		this.origLock = tn.origLock;
		this.ends = (Vector) tn.ends.clone();
		this.read = new CodeBlockRWSet(); this.read.union(tn.read);
		this.write = new CodeBlockRWSet(); this.write.union(tn.write);
		this.invokes = (HashSet) tn.invokes.clone();
		this.units = (HashSet) tn.units.clone();
		this.unitToRWSet = (HashMap) tn.unitToRWSet.clone();
		this.unitToUses = (HashMap) tn.unitToUses.clone();
		this.prepStmt = tn.prepStmt;
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
