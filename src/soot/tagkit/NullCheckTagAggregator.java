package soot.tagkit;


import soot.*;
import java.util.*;



public class NullCheckTagAggregator implements TagAggregator
{    

    private List tags = new LinkedList();
    private List units = new LinkedList();

    public void aggregateTag(Tag t, Unit u)
    {
	if(t instanceof NullCheckTag) {	  
	    units.add(u);
	    tags.add(t);
	}
    }
    
    public Tag produceAggregateTag()
    {
	if(units.size() == 0)
	    return null;
	else
	    return new CodeAttribute("NullCheckAttribute", units, tags);
    }
}
