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
import soot.SootClass;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.toolkits.annotation.tags.NullCheckTag;
import soot.tagkit.ColorTag;
import soot.tagkit.KeyTag;
import soot.tagkit.StringTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.scalar.FlowSet;

public class NullPointerColorer extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(NullPointerColorer.class);

  public NullPointerColorer(Singletons.Global g) {
  }

  public static NullPointerColorer v() {
    return G.v().soot_jimple_toolkits_annotation_nullcheck_NullPointerColorer();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    BranchedRefVarsAnalysis analysis
        = new BranchedRefVarsAnalysis(ExceptionalUnitGraphFactory.createExceptionalUnitGraph(b));

    for (Unit s : b.getUnits()) {
      FlowSet<RefIntPair> beforeSet = analysis.getFlowBefore(s);
      for (ValueBox vBox : s.getUseBoxes()) {
        addColorTags(vBox, beforeSet, s, analysis);
      }
      FlowSet<RefIntPair> afterSet = analysis.getFallFlowAfter(s);
      for (ValueBox vBox : s.getDefBoxes()) {
        addColorTags(vBox, afterSet, s, analysis);
      }
    }

    boolean keysAdded = false;
    final SootClass declaringClass = b.getMethod().getDeclaringClass();
    for (Tag next : declaringClass.getTags()) {
      if (next instanceof KeyTag) {
        if (NullCheckTag.NAME.equals(((KeyTag) next).analysisType())) {
          keysAdded = true;
        }
      }
    }
    if (!keysAdded) {
      declaringClass.addTag(new KeyTag(ColorTag.RED, "Nullness: Null", NullCheckTag.NAME));
      declaringClass.addTag(new KeyTag(ColorTag.GREEN, "Nullness: Not Null", NullCheckTag.NAME));
      declaringClass.addTag(new KeyTag(ColorTag.BLUE, "Nullness: Nullness Unknown", NullCheckTag.NAME));
    }
  }

  private void addColorTags(ValueBox vBox, FlowSet<RefIntPair> set, Unit u, BranchedRefVarsAnalysis analysis) {
    Value val = vBox.getValue();
    if (val.getType() instanceof RefLikeType) {
      // logger.debug(""+val+": "+val.getClass().toString());
      switch (analysis.anyRefInfo(val, set)) {
        case BranchedRefVarsAnalysis.kNull: {
          // analysis.kNull
          u.addTag(new StringTag(val + ": Null", NullCheckTag.NAME));
          vBox.addTag(new ColorTag(ColorTag.RED, NullCheckTag.NAME));
          break;
        }
        case BranchedRefVarsAnalysis.kNonNull: {
          u.addTag(new StringTag(val + ": NonNull", NullCheckTag.NAME));
          vBox.addTag(new ColorTag(ColorTag.GREEN, NullCheckTag.NAME));
          break;
        }
        case BranchedRefVarsAnalysis.kTop: {
          u.addTag(new StringTag(val + ": Nullness Unknown", NullCheckTag.NAME));
          vBox.addTag(new ColorTag(ColorTag.BLUE, NullCheckTag.NAME));
          break;
        }
        case BranchedRefVarsAnalysis.kBottom: {
          u.addTag(new StringTag(val + ": Nullness Unknown", NullCheckTag.NAME));
          vBox.addTag(new ColorTag(ColorTag.BLUE, NullCheckTag.NAME));
          break;
        }
      }
    }
  }
}
