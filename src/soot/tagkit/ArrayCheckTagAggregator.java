package soot.tagkit;


import soot.*;
import java.util.*;



public class ArrayCheckTagAggregator implements TagAggregator
{    
    private boolean status = false;
    private List tags = new LinkedList();
    private List units = new LinkedList();

    public ArrayCheckTagAggregator(boolean active)
    {
	this.status = active;
    }

    public boolean isActive()
    {
	return this.status;
    }

    public void aggregateTag(Tag t, Unit u)
    {
	if(t instanceof ArrayCheckTag) {	  
	    units.add(u);
	    tags.add(t);
	}
    }
    
    public Tag produceAggregateTag()
    {
	if(units.size() == 0)
	    return null;
	else
	    return new CodeAttribute("ArrayCheckAttribute", units, tags);
    }
}
