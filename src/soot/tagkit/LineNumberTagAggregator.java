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
public class LineNumberTagAggregator extends TagAggregator
{    
    public LineNumberTagAggregator( Singletons.Global g ) {}
    public static LineNumberTagAggregator v() { return G.v().LineNumberTagAggregator(); }

    private Tag lastTag = null;

    public void internalTransform( Body b, String phaseName, Map options ) {
        lastTag = null;
        super.internalTransform( b, phaseName, options );
    }

    /** Decide whether this tag should be aggregated by this aggregator.
     *  Return the tag to be attached to this unit, or null if nothing should
     *  be attached. */
    public Tag wantTag(Tag t, Unit u)
    {
	if(t instanceof LineNumberTag) 
	{
	    if (t != lastTag)
	    {
		lastTag = t;
                return t;
	    }
	}
        return null;
    }
    
    public String aggregatedName()
    {
        return "LineNumberTable";
    }
}







