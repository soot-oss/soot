/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot;
import soot.options.*;

import soot.util.*;
import java.util.*;

/** Utility class providing a timer.  Used for profiling various
 * phases of Sootification. */
public class Timer
{
    private long duration;
    private long startTime;
    private boolean hasStarted;
		
    private String name;
		
    
    /** Creates a new timer with the given name. */
    public Timer(String name)
    {
        this.name = name;
        duration = 0;
    }
    
    /** Creates a new timer. */
    public Timer()
    {
        this("unnamed");
    }
    
    /** Starts the given timer. */
    public void start()
    {
        // Subtract garbage collection time
				if(!G.v().Timer_isGarbageCollecting && Options.v() != null && Options.v().subtract_gc() && ((G.v().Timer_count++ % 4) == 0))
            {
                // garbage collects only every 4 calls to avoid round off errors
                
                G.v().Timer_isGarbageCollecting = true;
            
                G.v().Timer_forcedGarbageCollectionTimer.start();
                
                // Stop all outstanding timers
                {
                    Iterator timerIt = G.v().Timer_outstandingTimers.iterator();
                    
                    while(timerIt.hasNext())
                    {
                        Timer t = (Timer) timerIt.next();
                        
                        t.end();
                    }
                }
                
                System.gc();
        
                // Start all outstanding timers
                {
                    Iterator timerIt = G.v().Timer_outstandingTimers.iterator();
                    
                    while(timerIt.hasNext())
                    {
                        Timer t = (Timer) timerIt.next();
                        
                        t.start();
                    }
                }
                
                G.v().Timer_forcedGarbageCollectionTimer.end();
                
                G.v().Timer_isGarbageCollecting = false;
            }
                        
        
        startTime = System.currentTimeMillis();
        
        if(hasStarted)
            throw new RuntimeException("timer " + name + " has already been started!");
        else
            hasStarted = true;
        
        
        if(!G.v().Timer_isGarbageCollecting) 
        {
            G.v().Timer_outstandingTimers.add(this);
        }
            
    }

    /** Returns the name of the current timer. */
    public String toString()
    {
        return name;
    }
    
    /** Stops the current timer. */
    public void end()
    {   
        if(!hasStarted)
            throw new RuntimeException("timer " + name + " has not been started!");
        else
            hasStarted = false;
        
        duration += System.currentTimeMillis() - startTime;
        
        
        if(!G.v().Timer_isGarbageCollecting)
        {
            G.v().Timer_outstandingTimers.remove(this);
        }
    }

    /** Returns the sum of the intervals start()-end() of the current timer. */
    public long getTime()
    {
        return duration;
    }
}




