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

import soot.*;
import soot.tagkit.*;
import soot.toolkits.graph.*;
import java.util.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;

public class NullnessAnalysisColorer extends BodyTransformer {
    protected void internalTransform (Body b, String phaseName, Map options) {
        NullnessAnalysis analysis = new NullnessAnalysis
            (new CompleteUnitGraph(b));

        Iterator it = b.getUnits().iterator();

        while (it.hasNext()) {
            Stmt s = (Stmt)it.next();
            
            Iterator usesIt = s.getUseBoxes().iterator();
            FlowSet beforeSet = (FlowSet)analysis.getFlowBefore(s);
                
            while (usesIt.hasNext()) {
                ValueBox vBox = (ValueBox)usesIt.next();
                addColorTags(vBox, beforeSet, s, analysis);
            }

            Iterator defsIt = s.getDefBoxes().iterator();
            FlowSet afterSet = (FlowSet)analysis.getFallFlowAfter(s);

            while (defsIt.hasNext()){
                ValueBox vBox = (ValueBox)defsIt.next();
                addColorTags(vBox, afterSet, s, analysis);
            }
        }
    }
    
    private void addColorTags(ValueBox vBox, FlowSet set, 
                              Stmt s, NullnessAnalysis analysis) {
        Value val = vBox.getValue();
        if (val.getType() instanceof RefLikeType &&
                ((ArraySparseSet)set).contains(val))
            vBox.addTag(new ColorTag(ColorTag.GREEN));
    }
}
