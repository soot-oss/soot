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

package soot.jimple.toolkits.invoke;
import soot.*;
import java.util.*;
import soot.options.*;

/** Builds an invoke graph using Variable Type Analysis. */
public class VTATransformer extends SceneTransformer
{
    public VTATransformer( Singletons.Global g ) {}
    public static VTATransformer v() { return G.v().VTATransformer(); }

    protected void internalTransform(String phaseName, Map opts)
    {
        VTAOptions options = new VTAOptions( opts );

        InvokeGraphBuilder.v().transform(phaseName);
        
        VariableTypeAnalysis vta = null;
        int passes = options.passes();

        for (int i = 0; i < passes; i++)
        {
            if (Options.v().verbose())
                G.v().out.println(Scene.v().getActiveInvokeGraph().computeStats());
            vta = new VariableTypeAnalysis(Scene.v().getActiveInvokeGraph());
            vta.trimActiveInvokeGraph();
            Scene.v().getActiveInvokeGraph().refreshReachableMethods();
        }

    }
}


