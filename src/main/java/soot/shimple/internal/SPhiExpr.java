package soot.shimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.shimple.PhiExpr;
import soot.shimple.Shimple;
import soot.shimple.ShimpleExprSwitch;
import soot.toolkits.graph.Block;
import soot.toolkits.scalar.ValueUnitPair;
import soot.util.Switch;

/**
 * Internal implementation of Phi nodes.
 *
 * @author Navindra Umanee
 * @see soot.shimple.PhiExpr
 **/
public class SPhiExpr implements PhiExpr {
  private static final Logger logger = LoggerFactory.getLogger(SPhiExpr.class);
  protected List<ValueUnitPair> argPairs = new ArrayList<ValueUnitPair>();
  protected Map<Unit, ValueUnitPair> predToPair = new HashMap<Unit, ValueUnitPair>(); // cache
  protected Type type;

  /**
   * Create a trivial Phi expression for leftLocal. preds is an ordered list of the control flow predecessor Blocks of the
   * PhiExpr.
   **/
  public SPhiExpr(Local leftLocal, List<Block> preds) {
    type = leftLocal.getType();

    for (Block pred : preds) {
      addArg(leftLocal, pred);
    }
  }

  /**
   * Create a Phi expression from the given list of Values and Blocks.
   **/
  public SPhiExpr(List<Value> args, List<Unit> preds) {
    if (args.size() == 0) {
      throw new RuntimeException("Arg list may not be empty");
    }

    if (args.size() != preds.size()) {
      throw new RuntimeException("Arg list does not match Pred list");
    }

    type = args.get(0).getType();
    Iterator<Value> argsIt = args.iterator();
    Iterator<Unit> predsIt = preds.iterator();

    while (argsIt.hasNext()) {
      Value arg = argsIt.next();
      Object pred = predsIt.next();

      if (pred instanceof Block) {
        addArg(arg, (Block) pred);
      } else if (pred instanceof Unit) {
        addArg(arg, (Unit) pred);
      } else {
        throw new RuntimeException("Must be a CFG block or tail unit.");
      }
    }
  }

  /* get-accessor implementations */

  public List<ValueUnitPair> getArgs() {
    return Collections.unmodifiableList(argPairs);
  }

  public List<Value> getValues() {
    List<Value> args = new ArrayList<Value>();
    for (ValueUnitPair vup : argPairs) {
      Value arg = vup.getValue();
      args.add(arg);
    }

    return args;
  }

  public List<Unit> getPreds() {
    List<Unit> preds = new ArrayList<Unit>();
    for (ValueUnitPair up : argPairs) {
      Unit arg = up.getUnit();
      preds.add(arg);
    }

    return preds;
  }

  public int getArgCount() {
    return argPairs.size();
  }

  public ValueUnitPair getArgBox(int index) {
    if (index < 0 || index >= argPairs.size()) {
      return null;
    }
    return (ValueUnitPair) argPairs.get(index);
  }

  public Value getValue(int index) {
    ValueUnitPair arg = getArgBox(index);
    if (arg == null) {
      return null;
    }
    return arg.getValue();
  }

  public Unit getPred(int index) {
    ValueUnitPair arg = getArgBox(index);
    if (arg == null) {
      return null;
    }
    return arg.getUnit();
  }

  public int getArgIndex(Unit predTailUnit) {
    ValueUnitPair pair = getArgBox(predTailUnit);
    return argPairs.indexOf(pair); // (-1 on null)
  }

  public ValueUnitPair getArgBox(Unit predTailUnit) {
    ValueUnitPair vup = predToPair.get(predTailUnit);

    // we pay a penalty for misses but hopefully the common case
    // is faster than an iteration over argPairs every time
    if (vup == null || vup.getUnit() != predTailUnit) {
      updateCache();
      vup = predToPair.get(predTailUnit);
      if ((vup != null) && (vup.getUnit() != predTailUnit)) {
        throw new RuntimeException("Assertion failed.");
      }
    }

    // (null if not found)
    return vup;
  }

  public Value getValue(Unit predTailUnit) {
    ValueBox vb = getArgBox(predTailUnit);
    if (vb == null) {
      return null;
    }
    return vb.getValue();
  }

  public int getArgIndex(Block pred) {
    ValueUnitPair box = getArgBox(pred);
    return argPairs.indexOf(box); // (-1 on null)
  }

  public ValueUnitPair getArgBox(Block pred) {
    Unit predTailUnit = pred.getTail();
    ValueUnitPair box = getArgBox(predTailUnit);

    // workaround added for internal cases where the predTailUnit
    // may not be at the end of the predecessor block
    // (eg, fall-through pointer not moved)
    while (box == null) {
      predTailUnit = pred.getPredOf(predTailUnit);
      if (predTailUnit == null) {
        break;
      }
      box = getArgBox(predTailUnit);
    }

    return box;
  }

  public Value getValue(Block pred) {
    ValueBox vb = getArgBox(pred);
    if (vb == null) {
      return null;
    }
    return vb.getValue();
  }

  /* set-accessor implementations */

  public boolean setArg(int index, Value arg, Unit predTailUnit) {
    boolean ret1 = setValue(index, arg);
    boolean ret2 = setPred(index, predTailUnit);
    if (ret1 != ret2) {
      throw new RuntimeException("Assertion failed.");
    }
    return ret1;
  }

  public boolean setArg(int index, Value arg, Block pred) {
    return setArg(index, arg, pred.getTail());
  }

  public boolean setValue(int index, Value arg) {
    ValueUnitPair argPair = getArgBox(index);
    if (argPair == null) {
      return false;
    }
    argPair.setValue(arg);
    return true;
  }

  public boolean setValue(Unit predTailUnit, Value arg) {
    int index = getArgIndex(predTailUnit);
    return setValue(index, arg);
  }

  public boolean setValue(Block pred, Value arg) {
    int index = getArgIndex(pred);
    return setValue(index, arg);
  }

  public boolean setPred(int index, Unit predTailUnit) {
    ValueUnitPair argPair = getArgBox(index);
    if (argPair == null) {
      return false;
    }

    int other = getArgIndex(predTailUnit);
    if (other != -1) {
      logger.warn("An argument with control flow predecessor " + predTailUnit + " already exists in " + this + "!");
      logger.warn("setPred resulted in deletion of " + argPair + " from " + this + ".");
      removeArg(argPair);
      return false;
    }

    argPair.setUnit(predTailUnit);
    return true;
  }

  public boolean setPred(int index, Block pred) {
    return setPred(index, pred.getTail());
  }

  /* add/remove implementations */

  public boolean removeArg(int index) {
    ValueUnitPair arg = getArgBox(index);
    return removeArg(arg);
  }

  public boolean removeArg(Unit predTailUnit) {
    ValueUnitPair arg = getArgBox(predTailUnit);
    return removeArg(arg);
  }

  public boolean removeArg(Block pred) {
    ValueUnitPair arg = getArgBox(pred);
    return removeArg(arg);
  }

  public boolean removeArg(ValueUnitPair arg) {
    if (argPairs.remove(arg)) {
      // update cache
      predToPair.remove(arg.getUnit());
      // remove back-pointer
      arg.getUnit().removeBoxPointingToThis(arg);
      return true;
    }

    return false;
  }

  public boolean addArg(Value arg, Block pred) {
    return addArg(arg, pred.getTail());
  }

  public boolean addArg(Value arg, Unit predTailUnit) {
    // Do not allow phi nodes for dummy blocks
    if (predTailUnit == null) {
      return false;
    }

    // we disallow duplicate arguments
    if (predToPair.keySet().contains(predTailUnit)) {
      return false;
    }

    ValueUnitPair vup = new SValueUnitPair(arg, predTailUnit);

    // add and update cache
    argPairs.add(vup);
    predToPair.put(predTailUnit, vup);
    return true;
  }

  int blockId = -1;

  public void setBlockId(int blockId) {
    this.blockId = blockId;
  }

  public int getBlockId() {
    if (blockId == -1) {
      throw new RuntimeException("Assertion failed:  Block Id unknown.");
    }
    return blockId;
  }

  /* misc */

  /**
   * Update predToPair cache map which could be out-of-sync due to external setUnit or clone operations on the UnitBoxes.
   **/
  protected void updateCache() {
    int needed = argPairs.size();
    predToPair = new HashMap<Unit, ValueUnitPair>(needed << 1, 1.0F); // Always attempt to allocate the next power of 2 sized
                                                                      // map
    for (ValueUnitPair vup : argPairs) {
      predToPair.put(vup.getUnit(), vup);
    }
  }

  public boolean equivTo(Object o) {
    if (o instanceof SPhiExpr) {
      SPhiExpr pe = (SPhiExpr) o;

      if (getArgCount() != pe.getArgCount()) {
        return false;
      }

      for (int i = 0; i < getArgCount(); i++) {
        if (!getArgBox(i).equivTo(pe.getArgBox(i))) {
          return false;
        }
      }

      return true;
    }

    return false;
  }

  public int equivHashCode() {
    int hashcode = 1;

    for (int i = 0; i < getArgCount(); i++) {
      hashcode = hashcode * 17 + getArgBox(i).equivHashCode();
    }

    return hashcode;
  }

  @Override
  public List<UnitBox> getUnitBoxes() {
    Set<UnitBox> boxes = new HashSet<UnitBox>(argPairs.size());
    for (ValueUnitPair up : argPairs) {
      boxes.add(up);
    }
    return new ArrayList<UnitBox>(boxes);
  }

  public void clearUnitBoxes() {
    for (UnitBox box : getUnitBoxes()) {
      box.setUnit(null);
    }
  }

  public List<ValueBox> getUseBoxes() {
    Set<ValueBox> set = new HashSet<ValueBox>();

    for (ValueUnitPair argPair : argPairs) {
      set.addAll(argPair.getValue().getUseBoxes());
      set.add(argPair);
    }

    return new ArrayList<ValueBox>(set);
  }

  public Type getType() {
    return type;
  }

  public String toString() {
    StringBuffer expr = new StringBuffer(Shimple.PHI + "(");
    boolean isFirst = true;
    for (ValueUnitPair vuPair : argPairs) {
      if (!isFirst) {
        expr.append(", ");
      }
      Value arg = vuPair.getValue();
      expr.append(arg.toString());
      isFirst = false;
    }

    expr.append(")");

    return expr.toString();
  }

  public void toString(UnitPrinter up) {
    up.literal(Shimple.PHI);
    up.literal("(");

    boolean isFirst = true;
    for (ValueUnitPair vuPair : argPairs) {
      if (!isFirst) {
        up.literal(", ");
      }
      vuPair.toString(up);
      isFirst = false;
    }

    up.literal(")");
  }

  public void apply(Switch sw) {
    ((ShimpleExprSwitch) sw).casePhiExpr(this);
  }

  public Object clone() {
    // Note to self: Do not try to "fix" this *again*. Yes, it
    // should be a shallow copy in order to conform with the rest
    // of Soot. When a body is cloned, the Values are cloned
    // explicitly and replaced, and UnitBoxes are explicitly
    // patched. See Body.importBodyContentsFrom for details.
    return new SPhiExpr(getValues(), getPreds());
  }
}
