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
import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import soot.options.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.scalar.*;

/** A body transformer that records avail expression 
 * information in tags.  - both pessimistic and optimistic options*/
public class AvailExprTagger extends BodyTransformer
{ 
	public AvailExprTagger( Singletons.Global g ) {}
    public static AvailExprTagger v() { return G.v().soot_jimple_toolkits_annotation_AvailExprTagger(); }

    protected void internalTransform(
            Body b, String phaseName, Map opts)
    {

        SideEffectTester sideEffect;
        if( Scene.v().hasCallGraph()
            && !PhaseOptions.getBoolean( opts, "naive-side-effect" ) ) {
            sideEffect = new PASideEffectTester();
        } 
        else {
            sideEffect = new NaiveSideEffectTester();
        }
        sideEffect.newMethod( b.getMethod() );
                                
        AETOptions options = new AETOptions(opts);
        if (options.kind() == AETOptions.kind_optimistic){
            new SlowAvailableExpressionsAnalysis(new ExceptionalUnitGraph(b)); 
        }
        else {
            new PessimisticAvailableExpressionsAnalysis(new ExceptionalUnitGraph(b), b.getMethod(), sideEffect); 
        }
    }
}


