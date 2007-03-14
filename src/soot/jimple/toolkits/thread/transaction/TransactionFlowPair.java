package soot.jimple.toolkits.thread.transaction;

import soot.*;

class TransactionFlowPair
{
	// Information about the transactional region
	public Transaction tn;
	public boolean inside;

	TransactionFlowPair(Transaction tn, boolean inside)
	{
		this.tn = tn;
		this.inside = inside;
	}
	
	TransactionFlowPair(TransactionFlowPair tfp)
	{
		this.tn = tfp.tn;
		this.inside = tfp.inside;
	}
	
	public void copy(TransactionFlowPair tfp)
	{
		tfp.tn = this.tn;
		tfp.inside = this.inside;
	}

	public Object clone()
	{
		return new TransactionFlowPair(tn, inside);
	}
	
    public boolean equals( Object other )
	{
//		G.v().out.print(".");
		if(other instanceof TransactionFlowPair)
		{
			TransactionFlowPair tfp = (TransactionFlowPair) other;
			if(this.tn.IDNum == tfp.tn.IDNum) // && this.inside == tfp.inside)
				return true;
		}
		return false;
	}
	
	public String toString()
	{
		return "[" + (inside ? "in," : "out,") + tn.toString() + "]";
	}
}
