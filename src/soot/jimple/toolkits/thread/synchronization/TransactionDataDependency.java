package soot.jimple.toolkits.thread.synchronization;

import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.thread.transaction.Transaction;

class TransactionDataDependency
{
	public Transaction other;
	public int size;
	public RWSet rw;
	
	TransactionDataDependency(Transaction other, int size, RWSet rw)
	{
		this.other = other;
		this.size = size;
		this.rw = rw;
	}
}
