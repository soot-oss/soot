/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville and Feng Qian
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
    protected LinkedList<Tag> tags = new LinkedList<Tag>();
    protected LinkedList<Unit> units = new LinkedList<Unit>();

    /** Decide whether this tag should be aggregated by this aggregator. */
    public abstract boolean wantTag( Tag t );
    /** Aggregate the given tag assigned to the given unit */
    public abstract void considerTag( Tag t, Unit u );

    /** Return name of the resulting aggregated tag. */
    public abstract String aggregatedName();

    protected void internalTransform(Body b, String phaseName, Map options)
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
                if( wantTag( tag ) ) considerTag( tag, unit );
	    }         
        }        

	if(units.size() > 0) {
            b.addTag( new CodeAttribute(aggregatedName(), 
                 new LinkedList<Unit>(units), new LinkedList<Tag>(tags)) );
        }
        fini();
    }

    /** Called after all tags for a method have been aggregated. */
    public void fini() {}


}
