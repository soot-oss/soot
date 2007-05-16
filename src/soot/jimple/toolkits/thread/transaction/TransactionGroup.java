package soot.jimple.toolkits.thread.transaction;

import java.util.*;
import soot.*;

class TransactionGroup
{
	int groupNum; 
	
	// Information about the group members
	List<Transaction> transactions;

	// Information about the selected lock(s)
	public boolean isDynamicLock; // is lockObject actually dynamic? or is it a static ref?
	public boolean useDynamicLock; // use one dynamic lock per tn
	public Value lockObject;
	public boolean useLocksets;
	
	public TransactionGroup(int groupNum)
	{
		this.groupNum = groupNum;
		this.transactions = new ArrayList<Transaction>();
		
		this.isDynamicLock = false;
		this.useDynamicLock = false;
		this.lockObject = null;
		this.useLocksets = false;
	}
	
	public int num()
	{
		return groupNum;
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
