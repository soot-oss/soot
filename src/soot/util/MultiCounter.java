package soot.util;

import java.* ;

/** It is a counter class with multi counters. It only serves the profiling purpose.
 * And it is a template here.
 */  

public class MultiCounter {
    private static long c[] = {0,0,0,0,0,0,0,0} ;

    /** Reset all counters */
    public static void reset()
    {
        for (int i=0; i<c.length; i++)
	{
	    c[i] = 0;
	}
    }

    /** Report all counters */
    public static synchronized void report ()
    {
    	double prec;
	
	for (int i=0; i<c.length; i++)
	    System.out.println("Counter "+i+" : "+c[i]);

        System.out.println("Total     : "+(c[0]+c[1])) ;
	
	prec = ((double)c[4])*100/(c[0]+c[1]);

	System.out.println("Safe arrayref : "+prec);
	
	if ((c[5]+c[6])!=0)
	{
	    prec = ((double)c[6])*100/(c[5]+c[6]);
	}
	else
	    prec = 0;
	System.out.println("Safe nullcheck: "+prec);

	prec = ((double)c[7])*100/(c[0]+c[1]);
	System.out.println("All safe      : "+prec);
    }

    /** Increase one of counters */
    public static synchronized void increase(int which)
    {
     	if (which >= 0 && which < c.length) 
	    c[which]++ ;
    }
}
	

