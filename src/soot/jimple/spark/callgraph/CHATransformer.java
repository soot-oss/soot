/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.jimple.spark.callgraph;

import soot.jimple.toolkits.invoke.InvokeGraph;
import soot.options.*;
import soot.*;
import java.util.*;
import soot.jimple.toolkits.pointer.DumbPointerAnalysis;

/** Builds an invoke graph using Class Hierarchy Analysis. */
public class CHATransformer extends SceneTransformer
{
    public CHATransformer( Singletons.Global g ) {}
    public static CHATransformer v() { return G.v().CHATransformer(); }

    protected void internalTransform(String phaseName, Map opts)
    {
        CHAOptions options = new CHAOptions( opts );
        CallGraph cg = new CallGraph( DumbPointerAnalysis.v(), options.verbose(), options.all_clinit() );
        cg.setInvokeGraph( new InvokeGraph() );
        cg.build();
        Scene.v().setActiveInvokeGraph( cg.getInvokeGraph() );
    }
}


