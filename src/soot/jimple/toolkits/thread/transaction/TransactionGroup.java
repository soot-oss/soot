package soot.jimple.toolkits.thread.transaction;

import java.util.*;

class TransactionGroup
{
	int groupNum; 
	
	// Information about the group members
	List transactions;

	// Information about the selected lock(s)
//	boolean useDynamicLock;
	boolean useLocksets;
	
	public TransactionGroup(int groupNum)
	{
		this.groupNum = groupNum;
		this.transactions = new ArrayList();
//		this.useDynamicLock = false;
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
	
	public Iterator iterator()
	{
		return transactions.iterator();
	}
	
	public void mergeGroups(TransactionGroup other)
	{
		if(other == this)
			return;
			
		Iterator tnIt = other.transactions.iterator();
		while(tnIt.hasNext())
		{
			Transaction tn = (Transaction) tnIt.next();
			add(tn);
//			tn.setNumber = groupNum;
//			tn.group = this;
//			this.transactions.add(tn);
		}
		other.transactions.clear();
	}
}
