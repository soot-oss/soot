/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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

package soot;

import soot.grimp.*;
import soot.jimple.internal.*;
import soot.jimple.toolkits.base.*;
import soot.grimp.toolkits.base.*;
import soot.toolkits.scalar.*;
import soot.util.*;
import java.util.*;
import soot.baf.*;
import java.io.*;

/** A wrapper object for a pack of optimizations.
 * Provides chain-like operations, except that the key is the phase name.
 * This is a specific one for the very messy gb phase. */
public class GrimpBodyPack extends BodyPack
{
    GrimpBodyPack()
    {
        super("gb");
    }

        public void apply(Body b)
    {
        applyPhaseOptions( (GrimpBody) b,
                PackManager.v().getPhaseOptions( getPhaseName() ) );
    }


    public void applyPhaseOptions(GrimpBody b, Map options) {
        boolean aggregateAllLocals = PackManager.getBoolean(options, "aggregate-all-locals");
        boolean noAggregating = PackManager.getBoolean(options, "no-aggregating");

        PackManager.v().setPhaseOptionIfUnset( "gb.asv1", "only-stack-locals" );
        PackManager.v().setPhaseOptionIfUnset( "gb.asv2", "only-stack-locals" );

        if(aggregateAllLocals)
        {
            PackManager.v().getTransform("gb.a").apply( b );
            PackManager.v().getTransform("gb.cf").apply( b );
            PackManager.v().getTransform("gb.a").apply( b );
            PackManager.v().getTransform("gb.ule").apply( b );
        }
        else if (!noAggregating)
        {
            PackManager.v().getTransform("gb.asv1").apply( b );
            PackManager.v().getTransform("gb.cf").apply( b );
            PackManager.v().getTransform("gb.asv2").apply( b );
            PackManager.v().getTransform("gb.ule").apply( b );
        }    
    }
}
