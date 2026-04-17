package soot.jimple.toolkits.annotation.parity;

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
import soot.Singletons;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.parity.ParityAnalysis.Parity;
import soot.tagkit.StringTag;
import soot.toolkits.graph.BriefUnitGraph;

/**
 * A body transformer that records parity analysis information in tags.
 */
public class ParityTagger extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(ParityTagger.class);

  public ParityTagger(Singletons.Global g) {
  }

  public static ParityTagger v() {
    return G.v().soot_jimple_toolkits_annotation_parity_ParityTagger();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    ParityAnalysis a = new ParityAnalysis(new BriefUnitGraph(b));

    Iterator<Unit> sIt = b.getUnits().iterator();
    while (sIt.hasNext()) {

      Stmt s = (Stmt) sIt.next();

      Map<Value, Parity> parityVars = a.getFlowAfter(s);

      Iterator<Value> it = parityVars.keySet().iterator();
      while (it.hasNext()) {

        final Value variable = it.next();
        if ((variable instanceof IntConstant) || (variable instanceof LongConstant)) {
          // don't add string tags (just color tags)
        } else {
          StringTag t = new StringTag("Parity variable: " + variable + " " + parityVars.get(variable), "Parity Analysis");
          s.addTag(t);
        }
      }

      Map<Value, Parity> parityVarsUses = a.getFlowBefore(s);
      applyResults(parityVarsUses, s.getUseBoxes().iterator());

      Map<Value, Parity> parityVarsDefs = a.getFlowAfter(s);
      applyResults(parityVarsDefs, s.getDefBoxes().iterator());

    }
  }

  private void applyResults(Map<Value, Parity> parityVarsUses, Iterator<ValueBox> valBoxIt) {
    while (valBoxIt.hasNext()) {
      ValueBox vb = valBoxIt.next();
      Parity type = parityVarsUses.get(vb.getValue());
      if (type != null) {
        addTag(vb, type);
      }
    }
  }

  protected void addTag(ValueBox vb, Parity type) {
    vb.addTag(ParityTag.v(type));
  }
}
