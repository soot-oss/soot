
package soot.jimple.toolkits.thread.synchronization;

import soot.*;
import soot.jimple.toolkits.thread.transaction.DeadlockAvoidanceEdge;
import soot.jimple.toolkits.thread.transaction.NewStaticLock;

public class DeadlockAvoidanceEdge extends NewStaticLock
{	
	public DeadlockAvoidanceEdge(SootClass sc)
	{
		super(sc);
	}
	
    /** Clones the object. */
    public Object clone() 
    {
        return new DeadlockAvoidanceEdge(sc);
    }
    
    public boolean equals(Object c)
    {
    	if(c instanceof DeadlockAvoidanceEdge)
    	{
    		return ((DeadlockAvoidanceEdge) c).idnum == idnum;
    	}
    	return false;
    }
        
    public String toString()
    {
    	return "dae<" + sc.toString() + ">";
    }
}
