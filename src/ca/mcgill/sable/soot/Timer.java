/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Soot, a Java(TM) classfile optimization framework.                *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on March 19, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Made the timers subtract garbage collection time.
   
 - Modified on March 13, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Added an assertion to check that end() is always preceeded by a start()
 
 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot;

import ca.mcgill.sable.util.*;
import java.util.*;

public class Timer
{
    private long duration;
    private long startTime;
    private boolean hasStarted;

    private String name;

    private static List outstandingTimers = new ArrayList();
    private static boolean isGarbageCollecting;
    
    public static Timer forcedGarbageCollectionTimer = new Timer("gc");
    private static boolean isSubtractingGC;
    
    private static int count;
    
    public Timer(String name)
    {
        this.name = name;
        duration = 0;
    }
    
    public Timer()
    {
        this("unnamed");
    }
    
    public static void setSubtractingGC(boolean value)
    {
        isSubtractingGC = value;
    }
    
    public void start()
    {
        // Substract garbage collection time
            if(!isGarbageCollecting && isSubtractingGC && ((count++ % 4) == 0))
            {
                // garbage collects only every 4 calls to avoid round off errors
                
                isGarbageCollecting = true;
            
                forcedGarbageCollectionTimer.start();
                
                // Stop all outstanding timers
                {
                    Iterator timerIt = outstandingTimers.iterator();
                    
                    while(timerIt.hasNext())
                    {
                        Timer t = (Timer) timerIt.next();
                        
                        t.end();
                    }
                }
                
                System.gc();
        
                // Start all outstanding timers
                {
                    Iterator timerIt = outstandingTimers.iterator();
                    
                    while(timerIt.hasNext())
                    {
                        Timer t = (Timer) timerIt.next();
                        
                        t.start();
                    }
                }
                
                forcedGarbageCollectionTimer.end();
                
                isGarbageCollecting = false;
            }
                        
        
        startTime = System.currentTimeMillis();
        
        if(hasStarted)
            throw new RuntimeException("timer " + name + " has already been started!");
        else
            hasStarted = true;
        
        
        if(!isGarbageCollecting) 
        {
            outstandingTimers.add(this);
        }
            
    }

    public String toString()
    {
        return name;
    }
    
    public void end()
    {   
        if(!hasStarted)
            throw new RuntimeException("timer " + name + " has not been started!");
        else
            hasStarted = false;
        
        duration += System.currentTimeMillis() - startTime;
        
        
        if(!isGarbageCollecting)
        {
            outstandingTimers.remove(this);
        }
    }

    public long getTime()
    {
        return duration;
    }
}




