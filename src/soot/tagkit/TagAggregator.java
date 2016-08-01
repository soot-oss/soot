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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.baf.BafBody;

/** Interface to aggregate tags of units. */

public abstract class TagAggregator extends BodyTransformer {
	/** Decide whether this tag should be aggregated by this aggregator. */
	public abstract boolean wantTag(Tag t);

	/** Aggregate the given tag assigned to the given unit */
	public abstract void considerTag(Tag t, Unit u, LinkedList<Tag> tags,
			LinkedList<Unit> units);

	/** Return name of the resulting aggregated tag. */
	public abstract String aggregatedName();

	protected void internalTransform(Body b, String phaseName,
			Map<String, String> options) {
		BafBody body = (BafBody) b;
		
		LinkedList<Tag> tags = new LinkedList<Tag>();
		LinkedList<Unit> units = new LinkedList<Unit>();
		
		/* aggregate all tags */
		for (Iterator<Unit> unitIt = body.getUnits().iterator(); unitIt
				.hasNext();) {
			final Unit unit = unitIt.next();
			for (Iterator<Tag> tagIt = unit.getTags().iterator(); tagIt
					.hasNext();) {
				final Tag tag = tagIt.next();
				if (wantTag(tag))
					considerTag(tag, unit, tags, units);
			}
		}

		if (units.size() > 0) {
			b.addTag(new CodeAttribute(aggregatedName(), new LinkedList<Unit>(
					units), new LinkedList<Tag>(tags)));
		}
		fini();
	}

	/** Called after all tags for a method have been aggregated. */
	public void fini() {
	}

}
