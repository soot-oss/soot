package soot.jimple.toolkits.pointer;


import soot.*;
import java.util.*;

import soot.tagkit.*;

public class DependenceTagAggregator extends TagAggregator
{    
    public DependenceTagAggregator( Singletons.Global g ) {}
    public static DependenceTagAggregator v() { return G.v().DependenceTagAggregator(); }

    /** Decide whether this tag should be aggregated by this aggregator.
     *  Return the tag to be attached to this unit, or null if nothing should
     *  be attached. */
    public Tag wantTag( Tag t, Unit u ) {
        if( t instanceof DependenceTag ) return t;
        return null;
    }

    /** Return name of the resulting aggregated tag. */
    public String aggregatedName() {
        return "SideEffectAttribute";
    }
}







