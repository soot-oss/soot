package soot.jimple.toolkits.pointer;


import soot.*;
import java.util.*;

import soot.tagkit.*;

public class DependenceTagAggregator implements TagAggregator
{    
    private boolean status = false;
    private HashMap tagToUnit = new HashMap();

    public DependenceTagAggregator(boolean active)
    {
	this.status = active;
    }

    public boolean isActive()
    {
	return this.status;
    }

  /** Clears accumulated tags. */
    public void refresh()
    {
        tagToUnit = new HashMap();
    }

  /** Adds a new (unit, tag) pair. */
    public void aggregateTag(Tag t, Unit u)
    {
	tagToUnit.put( t, u );
    }
    
  /** Returns a CodeAttribute with all tags aggregated. */ 
    public Tag produceAggregateTag()
    {
	LinkedList units = new LinkedList();
	LinkedList tags = new LinkedList();
	for( Iterator it = tagToUnit.keySet().iterator(); it.hasNext(); ) {
	    Tag t = (Tag) it.next();
	    tags.add( t );
	    units.add( tagToUnit.get( t )  );
	}
	if(units.size() == 0)
	    return null;
	else
	    return new CodeAttribute("SideEffectAttribute", 
				     units, 
				     tags);
    }
}







