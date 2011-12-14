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

package soot.jimple.toolkits.callgraph;

import soot.*;

import java.util.*;
import soot.options.CGOptions;

/** A radio pack implementation for the call graph pack that calls the
 * intra-procedural clinit eliminator after the call graph has been built. */
public class CallGraphPack extends RadioScenePack
{
    public CallGraphPack(String name) {
        super(name);
    }

    protected void internalApply()
    {
        CGOptions options = new CGOptions( PhaseOptions.v().getPhaseOptions(this) );
        if(!Scene.v().hasCustomEntryPoints()) {
	        if(!options.implicit_entry()) {
	            Scene.v().setEntryPoints(EntryPoints.v().application());
	        }
	        if( options.all_reachable() ) {
	            List<SootMethod> entryPoints = new ArrayList<SootMethod>();
	            entryPoints.addAll( EntryPoints.v().all() );
	            entryPoints.addAll( EntryPoints.v().methodsOfApplicationClasses() );
	            Scene.v().setEntryPoints( entryPoints );
	        }
        }
        super.internalApply();
        ClinitElimTransformer trimmer = new ClinitElimTransformer();

        if( options.trim_clinit() ) {
            for (SootClass cl : Scene.v().getClasses(SootClass.BODIES)) {
                for( Iterator mIt = cl.getMethods().iterator(); mIt.hasNext(); ) {
                    final SootMethod m = (SootMethod) mIt.next();
                    if( m.isConcrete() && m.hasActiveBody() ) {
                        trimmer.transform( m.getActiveBody() );
                    }
                }
            }
        }
    }
}
