package soot.tagkit;


import soot.*;
import java.util.*;



public class OneByteTagAggregator implements TagAggregator
{    

    private boolean status = false;
    private List tags = new LinkedList();
    private List units = new LinkedList();

    private Unit lastUnit = null;
    private ArrayNullCheckTag lastTag = null;
    
    public OneByteTagAggregator(boolean active)
    {
	this.status = active;
    }

    public boolean isActive()
    {
	return this.status;
    }

    public void aggregateTag(Tag t, Unit u)
    {
	if(t instanceof OneByteCodeTag) 
	{	
	    if (lastUnit == u)
	    {
		byte[] v = ((OneByteCodeTag)t).getValue();
	    	lastTag.accumulate(v[0]);
            }
	    else
	    {
		units.add(u);
		lastUnit = u;
		
		byte[] v = ((OneByteCodeTag)t).getValue();
		lastTag = new ArrayNullCheckTag(v[0]);
		tags.add(lastTag);
	    }
	}
    }
    
    public Tag produceAggregateTag()
    {
	if(units.size() == 0)
	    return null;
	else
	    return new CodeAttribute("ArrayNullCheckAttribute", units, tags);
    }
}







