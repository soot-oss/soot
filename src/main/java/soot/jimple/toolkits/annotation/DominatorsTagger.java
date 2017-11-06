/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
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

package soot.jimple.toolkits.annotation;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.jimple.Stmt;
import soot.tagkit.LinkTag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.MHGDominatorsFinder;

/** A body transformer that records avail expression 
 * information in tags.  - both pessimistic and optimistic options*/
public class DominatorsTagger extends BodyTransformer
{ 
	public DominatorsTagger( Singletons.Global g ) {}
    public static DominatorsTagger v() { return G.v().soot_jimple_toolkits_annotation_DominatorsTagger(); }

    protected void internalTransform(
            Body b, String phaseName, Map opts)
    {

       
        MHGDominatorsFinder analysis = new MHGDominatorsFinder(new ExceptionalUnitGraph(b));
        Iterator it = b.getUnits().iterator();
        while (it.hasNext()){
            Stmt s = (Stmt)it.next();
            List dominators = analysis.getDominators(s);
            Iterator dIt = dominators.iterator();
            while (dIt.hasNext()){
                Stmt ds = (Stmt)dIt.next();
                String info = ds+" dominates "+s;
                s.addTag(new LinkTag(info, ds, b.getMethod().getDeclaringClass().getName(), "Dominators"));
            }
        }
    }
}


