import soot.*;
import java.util.*;
import soot.baf.*;

import soot.tagkit.*;
import soot.jimple.toolkits.annotation.tags.NullCheckTag;

/** The aggregator for NullCheckAttribute. */

public class NullTagAggregator extends TagAggregator
{    
    public NullTagAggregator() {}

    public boolean wantTag( Tag t ) {
	return (t instanceof NullCheckTag);
    }
    public void considerTag(Tag t, Unit u)
    {
		// for illustration, only annotate array references
        if (!((Inst)u).containsArrayRef()) return;

        units.add( u );
        tags.add( t );
    }
    
    public String aggregatedName()
    {
        return "NullCheckAttribute"; 
    }
}








