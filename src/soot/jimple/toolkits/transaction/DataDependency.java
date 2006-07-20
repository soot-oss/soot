package soot.jimple.toolkits.transaction;

class DataDependency
{
	public Transaction other;
	public int size;
	
	DataDependency(Transaction other, int size)
	{
		this.other = other;
		this.size = size;
	}
}
