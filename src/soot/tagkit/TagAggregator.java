/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville and Feng Qian
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
import soot.baf.BafBody;
import java.util.*;

/** Interface to aggregate tags of units. */

public abstract class TagAggregator extends BodyTransformer {
    protected LinkedList tags = new LinkedList();
    protected LinkedList units = new LinkedList();

    /** Decide whether this tag should be aggregated by this aggregator. */
    public abstract void wantTag( Tag t, Unit u );

    /** Return name of the resulting aggregated tag. */
    public abstract String aggregatedName();

    public void internalTransform(Body b, String phaseName, Map options)
    {
        BafBody body = (BafBody) b;
       
	/* clear the aggregator first. */
        tags.clear();
	units.clear();

	/* aggregate all tags */
        for( Iterator unitIt = body.getUnits().iterator(); unitIt.hasNext(); ) {
            final Unit unit = (Unit) unitIt.next();
            for( Iterator tagIt = unit.getTags().iterator(); tagIt.hasNext(); ) {
                final Tag tag = (Tag) tagIt.next();
                wantTag( tag, unit );
	    }         
        }        

	if(units.size() > 0) {
            b.addTag( new CodeAttribute(aggregatedName(), 
                 new LinkedList(units), new LinkedList(tags)) );
        }
    }
}
