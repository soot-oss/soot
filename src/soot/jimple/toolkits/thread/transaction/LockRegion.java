package soot.jimple.toolkits.thread.transaction;

import java.util.*;

import soot.jimple.Stmt;
import soot.toolkits.scalar.Pair;

public class LockRegion
{
	public Stmt prepStmt;
	public Stmt entermonitor;
	public Stmt beginning; // first stmt of body
//	public Vector exitmonitors; // all of them... this should be removed
	public List earlyEnds; // list of <return/branch stmt, exitmonitor> pairs
	public Pair exceptionalEnd; // <throw stmt, exitmonitor> pair
	public Pair end; // <goto stmt, exitmonitor> pair
	public Stmt last; // the last stmt before exception handling (usually a goto, return, or branch stmt from one of the ends)
	public Stmt after;

	public LockRegion()
	{
		this.prepStmt = null;
		this.entermonitor = null;
		this.beginning = null;
//		this.exitmonitors = new Vector();
		this.earlyEnds = new ArrayList();
		this.exceptionalEnd = null;
		this.end = null;
		this.last = null;
		this.after = null;
	}
	
	public LockRegion(LockRegion lr)
	{
		this.prepStmt = lr.prepStmt;
		this.entermonitor = lr.entermonitor;
		this.beginning = lr.beginning;
//		this.exitmonitors = (Vector) lr.exitmonitors.clone();
		this.earlyEnds = new ArrayList();
		this.earlyEnds.addAll(lr.earlyEnds);
		this.exceptionalEnd = null;
		this.end = lr.end;
		this.last = lr.last;
		this.after = lr.after;
	}

	protected Object clone()
	{
		return new LockRegion(this);
	}
}
