/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai and Patrick Lam
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


package soot;

import soot.util.*;
import java.util.*;
import soot.toolkits.graph.interaction.*;
import soot.options.*;

/** A wrapper object for a pack of optimizations.
 * Provides chain-like operations, except that the key is the phase name. */
public class BodyPack extends Pack
{
    public BodyPack(String name) {
        super(name);
    }

    protected void internalApply(Body b)
    {
        for( Iterator tIt = this.iterator(); tIt.hasNext(); ) {
            final Transform t = (Transform) tIt.next();
            if (Options.v().interactive_mode()){
                //G.v().out.println("sending transform: "+t.getPhaseName()+" for body: "+b+" for body pack: "+this.getPhaseName());
                InteractionHandler.v().handleNewAnalysis(t, b);
            }
            t.apply(b);
            if (Options.v().interactive_mode()){
                InteractionHandler.v().handleTransformDone(t, b);
            }
        }
    }

}
