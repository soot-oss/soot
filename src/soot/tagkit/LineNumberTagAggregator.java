/* Soot - a J*va Optimization Framework
 * Copyright (C) Feng Qian
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */



package soot.tagkit;


import soot.*;
import java.util.*;

/** The aggregator for LineNumberTable attribute. */
public class LineNumberTagAggregator implements TagAggregator
{    
    private boolean status = false;
    private List tags = new LinkedList();
    private List units = new LinkedList();

    private Tag lastTag = null;

    public LineNumberTagAggregator(boolean active)
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
        tags.clear();
	units.clear();
	lastTag = null;
    }

  /** Adds a new (unit, tag) pair. Probabely we are assuming
   * the (unit, tag) pairs come in the order.
   */
    public void aggregateTag(Tag t, Unit u)
    {
	if(t instanceof LineNumberTag) 
	{
	    if (t != lastTag)
	    {
		units.add(u);
		tags.add(t);
		lastTag = t;
	    }
	}
    }
    
  /** Returns a CodeAttribute with all tags aggregated. */ 
    public Tag produceAggregateTag()
    {
	if(units.size() == 0)
	    return null;
	else
	    return new CodeAttribute("LineNumberTable", 
				     new LinkedList(units), 
				     new LinkedList(tags));
    }
}







