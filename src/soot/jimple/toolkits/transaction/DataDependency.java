package soot.jimple.toolkits.transaction;

import soot.jimple.toolkits.pointer.*;

class DataDependency
{
	public Transaction other;
	public int size;
	public RWSet rw;
	
	DataDependency(Transaction other, int size, RWSet rw)
	{
		this.other = other;
		this.size = size;
		this.rw = rw;
	}
}
