/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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
import java.util.*;
import soot.baf.*;

/** A tag aggregator that associates a tag with the <b>most important</b>
 * instruction that is tagged with it. An instruction is important if
 * it contains a field or array reference, a method invocation, or an
 * object allocation.
 */
public abstract class ImportantTagAggregator extends TagAggregator
{    
    /** Decide whether this tag should be aggregated by this aggregator. */
    public abstract boolean wantTag( Tag t );

    /** Return name of the resulting aggregated tag. */
    public abstract String aggregatedName();

    /** Decide whether this tag should be aggregated by this aggregator. */
    public void considerTag(Tag t, Unit u)
    {
        Inst i = (Inst) u;
        if(! ( i.containsInvokeExpr()
            || i.containsFieldRef()
            || i.containsArrayRef()
            || i.containsNewExpr() ) ) return;
        units.add(u);
        tags.add(t);
    }
}







