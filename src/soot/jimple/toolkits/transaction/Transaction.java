package soot.jimple.toolkits.transaction;

import soot.jimple.Stmt;
import java.util.Vector;
import java.util.HashSet;
import java.util.HashMap;
import soot.jimple.toolkits.pointer.CodeBlockRWSet;
import soot.SootMethod;

class Transaction
{
	public static int nextIDNum = 1; 
	
	// Information about the transactional region
	public int IDNum;
	public String name;
	public Stmt begin;
	public Vector ends;
	public CodeBlockRWSet read, write;
//	public HashSet invokes;
	public HashSet units;
	public boolean wholeMethod;
	
	// Information for analyzing conflicts with other transactions
	public SootMethod method;
	public int setNumber; // used for breaking the list of transactions into sets
	public HashSet edges;
	public HashSet waits;
	public HashSet notifys;
	
	Transaction(Stmt end, boolean wholeMethod, SootMethod method)
	{
		this.IDNum = nextIDNum;
		nextIDNum++;
		this.begin = null;
		this.ends = new Vector();
		if(end != null)
			ends.add(end);
		this.read = new CodeBlockRWSet();
		this.write = new CodeBlockRWSet();
//		this.invokes = new HashSet();
		this.units = new HashSet();
		this.wholeMethod = wholeMethod;
		this.method = method;
		this.setNumber = 0; // 0 = no group, -1 = DELETE
		this.edges = new HashSet();
		this.waits = new HashSet();
		this.notifys = new HashSet();
	}
}
