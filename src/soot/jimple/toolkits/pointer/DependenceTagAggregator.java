package soot.jimple.toolkits.pointer;


import soot.*;
import java.util.*;

import soot.tagkit.*;

public class DependenceTagAggregator extends TagAggregator
{    
    public DependenceTagAggregator( Singletons.Global g ) {}
    public static DependenceTagAggregator v() { return G.v().DependenceTagAggregator(); }

    /** Decide whether this tag should be aggregated by this aggregator. */
    public void wantTag( Tag t, Unit u ) {
        if(!( t instanceof DependenceTag )) return;
        if( tags.size() == 0 || tags.getLast() != t ) {
            tags.add( t );
            units.add( u );
        } else {
            units.removeLast();
            units.add( u );
        }
    }

    /** Return name of the resulting aggregated tag. */
    public String aggregatedName() {
        return "SideEffectAttribute";
    }
}







