package soot.jimple.toolkits.pointer;


import soot.*;

import java.util.*;
import soot.baf.*;
import soot.tagkit.*;

public class DependenceTagAggregator extends TagAggregator
{    
    private HashSet allTags = new HashSet();
    private HashSet addedTags = new HashSet();

    public DependenceTagAggregator( Singletons.Global g ) {}
    public static DependenceTagAggregator v() { return G.v().DependenceTagAggregator(); }

    /** Decide whether this tag should be aggregated by this aggregator. */
    public void wantTag( Tag t, Unit u ) {
        if(!( t instanceof DependenceTag )) return;
        allTags.add(t);
        Inst i = (Inst) u;
        if(! ( i.containsInvokeExpr()
            || i.containsFieldRef()
            || i.containsArrayRef() ) ) return;
        if( tags.size() == 0 || tags.getLast() != t ) {
            tags.add( t );
            addedTags.add(t);
            units.add( u );
        } else {
            G.v().out.println( "previous unit is "+units.getLast() );
            G.v().out.println( "current unit is "+u );
            throw new RuntimeException( "Multiple units getting the same tag." );
        }
    }

    /** Called after all tags for a method have been aggregated. */
    public void fini() {
        if( addedTags.size() != allTags.size() ) {
            G.v().out.println( "Tags added: "+addedTags );
            G.v().out.println( "All tags: "+allTags );
            throw new RuntimeException( "Failed to add all tags." );
        }
        addedTags = new HashSet();
        allTags = new HashSet();
    }

    /** Return name of the resulting aggregated tag. */
    public String aggregatedName() {
        return "SideEffectAttribute";
    }
}







