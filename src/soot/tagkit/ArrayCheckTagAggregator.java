package soot.tagkit;


import soot.*;
import java.util.*;



public class ArrayCheckTagAggregator implements TagAggregator
{    

    private List arrayCheckTags = new LinkedList();
    private List units = new LinkedList();

    public void aggregateTag(Tag t, Unit u)
    {
	if(t instanceof ArrayCheckTag) {	  
	    units.add(u);
	    arrayCheckTags.add((ArrayCheckTag) t);
	}
    }
    
    public Tag produceAggregateTag()
    {
	if(units.size() == 0)
	    return null;
	else
	    return new ArrayCheckAttribute(arrayCheckTags, units);
    }
}
