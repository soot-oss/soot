package soot.jimple.toolkits.thread.synchronization;

class SynchronizedRegionFlowPair
{
	// Information about the transactional region
	public CriticalSection tn;
	public boolean inside;

	SynchronizedRegionFlowPair(CriticalSection tn, boolean inside)
	{
		this.tn = tn;
		this.inside = inside;
	}
	
	SynchronizedRegionFlowPair(SynchronizedRegionFlowPair tfp)
	{
		this.tn = tfp.tn;
		this.inside = tfp.inside;
	}
	
	public void copy(SynchronizedRegionFlowPair tfp)
	{
		tfp.tn = this.tn;
		tfp.inside = this.inside;
	}

	public Object clone()
	{
		return new SynchronizedRegionFlowPair(tn, inside);
	}
	
    public boolean equals( Object other )
	{
//		G.v().out.print(".");
		if(other instanceof SynchronizedRegionFlowPair)
		{
			SynchronizedRegionFlowPair tfp = (SynchronizedRegionFlowPair) other;
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
