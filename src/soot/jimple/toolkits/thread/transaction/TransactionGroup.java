package soot.jimple.toolkits.thread.transaction;

import java.util.*;
import soot.*;
import soot.jimple.toolkits.pointer.RWSet;
import soot.jimple.toolkits.pointer.CodeBlockRWSet;

class TransactionGroup implements Iterable<Transaction>
{
	int groupNum; 
	
	// Information about the group members
	List<Transaction> transactions;
	
	// Group read/write set
	RWSet rwSet;

	// Information about the selected lock(s)
	public boolean isDynamicLock; // is lockObject actually dynamic? or is it a static ref?
	public boolean useDynamicLock; // use one dynamic lock per tn
	public Value lockObject;
	public boolean useLocksets;
	
	public TransactionGroup(int groupNum)
	{
		this.groupNum = groupNum;
		this.transactions = new ArrayList<Transaction>();
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
		return transactions.size();
	}
	
	public void add(Transaction tn)
	{
		tn.setNumber = groupNum;
		tn.group = this;
		if(!transactions.contains(tn))
			transactions.add(tn);
	}
	
	public boolean contains(Transaction tn)
	{
		return transactions.contains(tn);
	}
	
	public Iterator<Transaction> iterator()
	{
		return transactions.iterator();
	}
	
	public void mergeGroups(TransactionGroup other)
	{
		if(other == this)
			return;
			
		Iterator<Transaction> tnIt = other.transactions.iterator();
		while(tnIt.hasNext())
		{
			Transaction tn = tnIt.next();
			add(tn);
		}
		other.transactions.clear();
	}
}
