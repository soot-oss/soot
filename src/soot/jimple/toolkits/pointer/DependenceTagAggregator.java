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

package soot.jimple.toolkits.pointer;


import soot.*;

import java.util.*;
import soot.baf.*;
import soot.tagkit.*;

public class DependenceTagAggregator extends ImportantTagAggregator
{    
    public DependenceTagAggregator( Singletons.Global g ) {}
    public static DependenceTagAggregator v() { return G.v().soot_jimple_toolkits_pointer_DependenceTagAggregator(); }

    /** Decide whether this tag should be aggregated by this aggregator. */
    public boolean wantTag( Tag t ) {
        return (t instanceof DependenceTag);
    }

    /** Return name of the resulting aggregated tag. */
    public String aggregatedName() {
        return "SideEffectAttribute";
    }
}







