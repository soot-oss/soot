package soot.jimple.toolkits.annotation.tags;


import soot.*;
import java.util.*;

import soot.tagkit.*;

/** The aggregator for ArrayNullCheckAttribute. */

public class ArrayNullTagAggregator extends TagAggregator
{    
    public ArrayNullTagAggregator( Singletons.Global g ) {}
    public static ArrayNullTagAggregator v() { return G.v().ArrayNullTagAggregator(); }

    public void wantTag(Tag t, Unit u)
    {
	if(!(t instanceof OneByteCodeTag)) return; 
        OneByteCodeTag obct = (OneByteCodeTag) t;
        if( units.size() == 0 || units.getLast() != u ) {
            units.add( u );
            tags.add( new ArrayNullCheckTag() );
        }
        ArrayNullCheckTag anct = (ArrayNullCheckTag) tags.getLast();
        anct.accumulate(obct.getValue()[0]);
    }
    
    public String aggregatedName()
    {
        return "ArrayNullCheckAttribute"; 
    }
}







