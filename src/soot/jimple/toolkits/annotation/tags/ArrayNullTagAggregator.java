package soot.jimple.toolkits.annotation.tags;


import soot.*;
import java.util.*;
import soot.baf.*;

import soot.tagkit.*;

/** The aggregator for ArrayNullCheckAttribute. */

public class ArrayNullTagAggregator extends TagAggregator
{    
    public ArrayNullTagAggregator( Singletons.Global g ) {}
    public static ArrayNullTagAggregator v() { return G.v().ArrayNullTagAggregator(); }

    public boolean wantTag( Tag t ) {
	return (t instanceof OneByteCodeTag);
    }
    public void considerTag(Tag t, Unit u)
    {
        Inst i = (Inst) u;
        if(! ( i.containsInvokeExpr()
            || i.containsFieldRef()
            || i.containsArrayRef() ) ) return;

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







