package soot.jimple.toolkits.thread.synchronization;

import java.util.*;

import soot.jimple.Stmt;
import soot.toolkits.scalar.Pair;

public class SynchronizedRegion
{
	public Stmt prepStmt;
	public Stmt entermonitor;
	public Stmt beginning; // first stmt of body
	public List<Pair<Stmt, Stmt>> earlyEnds; // list of <return/branch stmt, exitmonitor> pairs
	public Pair<Stmt, Stmt> exceptionalEnd; // <throw stmt, exitmonitor> pair
	public Pair<Stmt, Stmt> end; // <goto stmt, exitmonitor> pair
	public Stmt last; // the last stmt before exception handling (usually a goto, return, or branch stmt from one of the ends)
	public Stmt after;

	public SynchronizedRegion()
	{
		this.prepStmt = null;
		this.entermonitor = null;
		this.beginning = null;
		this.earlyEnds = new ArrayList<Pair<Stmt, Stmt>>();
		this.exceptionalEnd = null;
		this.end = null;
		this.last = null;
		this.after = null;
	}
	
	public SynchronizedRegion(SynchronizedRegion sr)
	{
		this.prepStmt = sr.prepStmt;
		this.entermonitor = sr.entermonitor;
		this.beginning = sr.beginning;
		this.earlyEnds = new ArrayList<Pair<Stmt, Stmt>>();
		this.earlyEnds.addAll(sr.earlyEnds);
		this.exceptionalEnd = null;
		this.end = sr.end;
		this.last = sr.last;
		this.after = sr.after;
	}

	protected Object clone()
	{
		return new SynchronizedRegion(this);
	}
}
