package soot.jimple.toolkits.thread.synchronization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.jimple.toolkits.pointer.*;

class CriticalSectionDataDependency
{
	public CriticalSection other;
	public int size;
	public RWSet rw;
	
	CriticalSectionDataDependency(CriticalSection other, int size, RWSet rw)
	{
		this.other = other;
		this.size = size;
		this.rw = rw;
	}
}
