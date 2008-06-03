import soot.*;
import java.util.*;
import soot.baf.*;

import soot.tagkit.*;
import soot.jimple.toolkits.annotation.tags.NullCheckTag;

/** The aggregator for NullCheckAttribute. */

public class NullTagAggregator extends ImportantTagAggregator
{    
    public NullTagAggregator() {}

    public boolean wantTag( Tag t ) {
	return (t instanceof NullCheckTag);
    }
    
    public String aggregatedName()
    {
        return "NullCheckAttribute"; 
    }
}








