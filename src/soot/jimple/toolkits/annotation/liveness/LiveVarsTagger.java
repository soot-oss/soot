/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.jimple.toolkits.annotation.liveness;

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.tagkit.*;
import java.util.*;
import soot.jimple.*;

public class LiveVarsTagger extends BodyTransformer {


    public LiveVarsTagger(Singletons.Global g) {}
    public static LiveVarsTagger v() { return G.v().soot_jimple_toolkits_annotation_liveness_LiveVarsTagger();}

    protected void internalTransform(Body b, String phaseName, Map options){
    
        LiveLocals sll = new SimpleLiveLocals(new ExceptionalUnitGraph(b));

        Iterator it = b.getUnits().iterator();
        while (it.hasNext()){
            Stmt s = (Stmt)it.next();
            //System.out.println("stmt: "+s);
            Iterator liveLocalsIt = sll.getLiveLocalsAfter(s).iterator();
            while (liveLocalsIt.hasNext()){
                Value v = (Value)liveLocalsIt.next();
                s.addTag(new StringTag("Live Variable: "+v, "Live Variable"));

                Iterator usesIt = s.getUseBoxes().iterator();
                while (usesIt.hasNext()){
                    ValueBox use = (ValueBox)usesIt.next();
                    if (use.getValue().equals(v)){
                        use.addTag(new ColorTag(ColorTag.GREEN, "Live Variable"));
                    }
                }
                Iterator defsIt = s.getDefBoxes().iterator();
                while (defsIt.hasNext()){
                    ValueBox def = (ValueBox)defsIt.next();
                    if (def.getValue().equals(v)){
                        def.addTag(new ColorTag(ColorTag.GREEN, "Live Variable"));
                    }
                }
            }
        }
    }
}   
