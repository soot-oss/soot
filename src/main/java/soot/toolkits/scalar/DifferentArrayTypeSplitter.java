package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Scene;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.ValueBox;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.util.Chain;

/**
 * Consider the following code <code>
 * a = newarray (int)[2];
 * a[0] = 2;
 * goto x
 * 
 * a = newarray (char)[2];
 * a[0] = 'x';
 * 
 * x:
 * staticinvoke <useAsObject: void x(java.lang.Object)>(a);
 * </code> In this case, it is hard to find a good typing for <code>a</<code>. Since both usages (as int[] and char[]) both
 * are used by the staticinvoke, the local splitter does not split the locals here.
 *
 * This analysis will split the locals and merge the different locals right before the join.
 */
public class DifferentArrayTypeSplitter extends BodyTransformer {

  protected ThrowAnalysis throwAnalysis;
  protected boolean omitExceptingUnitEdges;

  public DifferentArrayTypeSplitter() {
  }

  public DifferentArrayTypeSplitter(ThrowAnalysis ta) {
    this(ta, false);
  }

  public DifferentArrayTypeSplitter(ThrowAnalysis ta, boolean omitExceptingUnitEdges) {
    this.throwAnalysis = ta;
    this.omitExceptingUnitEdges = omitExceptingUnitEdges;
  }

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    if (throwAnalysis == null) {
      throwAnalysis = Scene.v().getDefaultThrowAnalysis();
    }

    // Go through the definitions, building the webs
    ExceptionalUnitGraph graph
        = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body, throwAnalysis, omitExceptingUnitEdges);

    // run in panic mode on first split (maybe change this depending on the input source)
    final LocalDefs defs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(graph, true);
    final LocalUses uses = LocalUses.Factory.newLocalUses(graph, defs);
    Chain<Local> locals = body.getLocals();
    int w = 0;

    Set<Unit> handledDef = new HashSet<>();
    Iterator<Unit> itU = body.getUnits().snapshotIterator();
    while (itU.hasNext()) {
      Unit u = itU.next();
      Iterator<ValueBox> it = u.getUseBoxesIterator();
      while (it.hasNext()) {
        ValueBox i = it.next();
        Value value = i.getValue();
        if (value instanceof Local &&
        // performance: Only consider untyped variables
            value.getType() instanceof UnknownType) {
          Local lcl = (Local) value;
          Iterator<Unit> alldefs = defs.getDefsOfAtIterator(lcl, u);
          Type agreeingType = null;
          boolean doTypesAgree = true, usedAsArray = false;
          while (alldefs.hasNext()) {
            Unit udef = alldefs.next();
            DefinitionStmt def = (DefinitionStmt) udef;
            Type t = def.getRightOp().getType();
            if (t instanceof ArrayType) {
              usedAsArray = true;
            }
            if (agreeingType == null) {
              agreeingType = t;
            } else if (agreeingType != t) {
              doTypesAgree = false;
              if (usedAsArray) {
                break;
              }
            }
          }
          if (!doTypesAgree && usedAsArray) {
            // now we need to find clusters of variables that are used together that we want to change
            List<Unit> alldefsList = defs.getDefsOfAt(lcl, u);
            for (Unit def : alldefsList) {
              if (!handledDef.add(def)) {
                continue;
              }
              Set<Unit> allUses = new HashSet<Unit>();
              for (UnitValueBoxPair d : uses.getUsesOf(def)) {
                allUses.add(d.unit);
              }
              for (Unit otherdef : alldefsList) {
                if (def != otherdef) {
                  for (UnitValueBoxPair d : uses.getUsesOf(otherdef)) {
                    allUses.remove(d.unit);
                  }
                }
              }
              // now, allUses contains the set of all uses that are only used by this definition site, forming a cluster

              Local newLocal = (Local) lcl.clone();
              newLocal.setName(newLocal.getName() + '_' + ++w);
              locals.add(newLocal);
              DefinitionStmt d = (DefinitionStmt) def;
              Unit pos = d;
              while (pos instanceof IdentityStmt) {
                IdentityStmt ipos = (IdentityStmt) pos;
                if (!(ipos.getRightOp() instanceof CaughtExceptionRef)) {
                  // we cannot place this at the parameter identity statement
                  pos = body.getUnits().getSuccOf(pos);
                }
              }
              body.getUnits().insertAfter(Jimple.v().newAssignStmt(lcl, newLocal), pos);
              for (Unit useStmt : allUses) {
                replaceLocalsInUnitUses(useStmt, lcl, newLocal);
              }
              d.setLeftOp(newLocal);
            }
          }
        }
      }
    }
    UnusedLocalEliminator.v().transform(body);
  }

  private void replaceLocalsInUnitUses(Unit change, Value oldLocal, Local newLocal) {
    for (Iterator<ValueBox> iterator = change.getUseBoxesIterator(); iterator.hasNext();) {
      ValueBox u = iterator.next();
      if (u.getValue() == oldLocal) {
        u.setValue(newLocal);
      }
    }
  }
}
