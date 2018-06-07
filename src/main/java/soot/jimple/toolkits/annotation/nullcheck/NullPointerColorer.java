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

import java.util.Iterator;
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
import soot.jimple.Stmt;
import soot.tagkit.ColorTag;
import soot.tagkit.KeyTag;
import soot.tagkit.StringTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.FlowSet;

public class NullPointerColorer extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(NullPointerColorer.class);

  public NullPointerColorer(Singletons.Global g) {
  }

  public static NullPointerColorer v() {
    return G.v().soot_jimple_toolkits_annotation_nullcheck_NullPointerColorer();
  }

  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {

    BranchedRefVarsAnalysis analysis = new BranchedRefVarsAnalysis(new ExceptionalUnitGraph(b));

    Iterator<Unit> it = b.getUnits().iterator();

    while (it.hasNext()) {
      Stmt s = (Stmt) it.next();

      Iterator<ValueBox> usesIt = s.getUseBoxes().iterator();
      FlowSet beforeSet = (FlowSet) analysis.getFlowBefore(s);

      while (usesIt.hasNext()) {
        ValueBox vBox = (ValueBox) usesIt.next();
        addColorTags(vBox, beforeSet, s, analysis);
      }

      Iterator<ValueBox> defsIt = s.getDefBoxes().iterator();
      FlowSet afterSet = (FlowSet) analysis.getFallFlowAfter(s);

      while (defsIt.hasNext()) {
        ValueBox vBox = (ValueBox) defsIt.next();
        addColorTags(vBox, afterSet, s, analysis);
      }
    }

    Iterator<Tag> keysIt = b.getMethod().getDeclaringClass().getTags().iterator();
    boolean keysAdded = false;
    while (keysIt.hasNext()) {
      Tag next = keysIt.next();
      if (next instanceof KeyTag) {
        if (((KeyTag) next).analysisType().equals("NullCheckTag")) {
          keysAdded = true;
        }
      }
    }
    if (!keysAdded) {
      b.getMethod().getDeclaringClass().addTag(new KeyTag(ColorTag.RED, "Nullness: Null", "NullCheckTag"));
      b.getMethod().getDeclaringClass().addTag(new KeyTag(ColorTag.GREEN, "Nullness: Not Null", "NullCheckTag"));
      b.getMethod().getDeclaringClass().addTag(new KeyTag(ColorTag.BLUE, "Nullness: Nullness Unknown", "NullCheckTag"));
    }
  }

  private void addColorTags(ValueBox vBox, FlowSet set, Stmt s, BranchedRefVarsAnalysis analysis) {

    Value val = vBox.getValue();
    if (val.getType() instanceof RefLikeType) {
      // logger.debug(""+val+": "+val.getClass().toString());

      int vInfo = analysis.anyRefInfo(val, set);

      switch (vInfo) {
        case 1: {
          // analysis.kNull
          s.addTag(new StringTag(val + ": Null", "NullCheckTag"));
          vBox.addTag(new ColorTag(ColorTag.RED, "NullCheckTag"));
          break;
        }
        case 2: {
          // analysis.kNonNull
          s.addTag(new StringTag(val + ": NonNull", "NullCheckTag"));
          vBox.addTag(new ColorTag(ColorTag.GREEN, "NullCheckTag"));
          break;
        }
        case 99: {
          // analysis.KTop:
          s.addTag(new StringTag(val + ": Nullness Unknown", "NullCheckTag"));
          vBox.addTag(new ColorTag(ColorTag.BLUE, "NullCheckTag"));
          break;
        }
        case 0: {
          // analysis.kBottom
          s.addTag(new StringTag(val + ": Nullness Unknown", "NullCheckTag"));
          vBox.addTag(new ColorTag(ColorTag.BLUE, "NullCheckTag"));
          break;
        }
      }
    } else {

    }
  }
}
