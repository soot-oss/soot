package soot.jimple.toolkits.thread.transaction;

import java.util.*;

class TransactionGroup
{
	int groupNum; 
	
	// Information about the group members
	List transactions;

	// Information about the selected lock(s)
//	boolean staticLock;
	
	public TransactionGroup(int groupNum)
	{
		this.groupNum = groupNum;
		this.transactions = new ArrayList();
//		this.staticLock = true;
	}
	
	public int num()
	{
		return groupNum;
	}
	
	public void add(Transaction tn)
	{
		tn.setNumber = groupNum;
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
			tn.setNumber = groupNum;
			this.transactions.add(tn);
//			other.transactions.remove(tn);
		}
		other.transactions.clear();
	}
}
