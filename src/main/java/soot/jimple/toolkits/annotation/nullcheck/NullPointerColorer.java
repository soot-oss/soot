package soot.jimple.toolkits.annotation.nullcheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jennifer Lhotak
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.RefLikeType;
import soot.Singletons;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis.AnalysisInfo;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis.NullnessLattice;
import soot.jimple.toolkits.annotation.tags.NullCheckTag;
import soot.tagkit.StringTag;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;

public class NullPointerColorer extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(NullPointerColorer.class);

  public NullPointerColorer(Singletons.Global g) {
  }

  public static NullPointerColorer v() {
    return G.v().soot_jimple_toolkits_annotation_nullcheck_NullPointerColorer();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    NullnessAnalysis analysis = new NullnessAnalysis(ExceptionalUnitGraphFactory.createExceptionalUnitGraph(b));

    for (Unit s : b.getUnits()) {
      AnalysisInfo beforeSet = analysis.getFlowBefore(s);
      for (ValueBox vBox : s.getUseBoxes()) {
        addTags(vBox, beforeSet, s, analysis);
      }
      AnalysisInfo afterSet = analysis.getFallFlowAfter(s);
      for (ValueBox vBox : s.getDefBoxes()) {
        addTags(vBox, afterSet, s, analysis);
      }
    }

  }

  private void addTags(ValueBox vBox, AnalysisInfo set, Unit u, NullnessAnalysis analysis) {
    Value val = vBox.getValue();
    if (val.getType() instanceof RefLikeType) {
      NullnessLattice p = set.getLattice(val);
      u.addTag(new StringTag(val + ": " + p.toString(), NullCheckTag.NAME));
      u.addTag(new NullnessTag(p));
    }
  }
}
