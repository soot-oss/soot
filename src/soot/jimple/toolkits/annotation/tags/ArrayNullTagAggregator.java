package soot.jimple.toolkits.annotation.tags;


import soot.*;
import java.util.*;

import soot.tagkit.*;

/** The aggregator for ArrayNullCheckAttribute. */

public class ArrayNullTagAggregator extends TagAggregator
{    
    private Unit lastUnit = null;
    private ArrayNullCheckTag lastTag = null;

    private ArrayNullTagAggregator() {}
    private static ArrayNullTagAggregator instance = new ArrayNullTagAggregator();
    public static ArrayNullTagAggregator v() { return instance; }

    public void internalTransform( Body b, String phaseName, Map options )
    {
	lastUnit = null;
	lastTag = null;
        super.internalTransform( b, phaseName, options );
    }

    public Tag wantTag(Tag t, Unit u)
    {
	if(t instanceof OneByteCodeTag) 
	{	
            OneByteCodeTag obct = (OneByteCodeTag) t;
	    if (lastUnit == u) {
	    	lastTag.accumulate(obct.getValue()[0]);
            } else {
		lastUnit = u;
		lastTag = new ArrayNullCheckTag(obct.getValue()[0]);
                return lastTag;
	    }
	}
        return null;
    }
    
    public String aggregatedName()
    {
        return "ArrayNullCheckAttribute"; 
    }
}







