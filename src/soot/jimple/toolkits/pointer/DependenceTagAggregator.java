package soot.jimple.toolkits.pointer;


import soot.*;

import java.util.*;
import soot.baf.*;
import soot.tagkit.*;

public class DependenceTagAggregator extends ImportantTagAggregator
{    
    public DependenceTagAggregator( Singletons.Global g ) {}
    public static DependenceTagAggregator v() { return G.v().DependenceTagAggregator(); }

    /** Decide whether this tag should be aggregated by this aggregator. */
    public boolean wantTag( Tag t ) {
        return (t instanceof DependenceTag);
    }

    /** Return name of the resulting aggregated tag. */
    public String aggregatedName() {
        return "SideEffectAttribute";
    }
}







