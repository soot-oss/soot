
package soot.jimple.toolkits.thread.synchronization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.*;

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
