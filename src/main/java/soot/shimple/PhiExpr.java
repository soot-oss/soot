package soot.shimple;

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

import java.util.List;

import soot.Local;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.toolkits.graph.Block;
import soot.toolkits.scalar.ValueUnitPair;
import soot.util.Switch;

/**
 * A fully defined PhiExpr usually consists of a list of Values for the arguments alongst with the corresponding control flow
 * predecessor for each argument. This may be provided either as a Soot CFG Block or more directly as the Unit at the end of
 * the corresponding CFG block.
 *
 * <p>
 * As much as possible we try to conform to the semantics as described by Cytron et al., TOPLAS Oct. 91. A Phi node such as
 * "x_1 = Phi(x_2, x_3)" is eliminated by respectively adding the statements "x_1 = x_2" and "x_1 = x_3" at the end of the
 * corresponding control flow predecessor.
 *
 * <p>
 * However, due to the fact that each argument is explicitly associated with the control flow predecessor, there may be some
 * subtle differences. We tried to make the behaviour as robust and transparent as possible by handling the common cases of
 * Unit chain manipulations in the Shimple internal implementation of PatchingChain.
 *
 * @author Navindra Umanee
 * @see <a href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently Computing Static Single Assignment Form and
 *      the Control Dependence Graph</a>
 * @see Shimple#newPhiExpr(List, List)
 * @see Shimple#newPhiExpr(Local, List)
 **/
public interface PhiExpr extends ShimpleExpr {
  /**
   * Returns an unmodifiable, backed view of the arguments to this PhiExpr. Each argument is a ValueUnitPair.
   *
   * @see soot.toolkits.scalar.ValueUnitPair
   **/
  public List<ValueUnitPair> getArgs();

  /**
   * Returns a list of the values used by this PhiExpr.
   **/
  public List<Value> getValues();

  /**
   * Returns a list of the control flow predecessor Units being tracked by this PhiExpr
   **/
  public List<Unit> getPreds();

  /**
   * Returns the number of arguments in this PhiExpr.
   **/
  public int getArgCount();

  /**
   * Returns the argument pair for the given index. Null if out-of-bounds.
   **/
  public ValueUnitPair getArgBox(int index);

  /**
   * Returns the value for the given index into the PhiExpr. Null if out-of-bounds.
   **/
  public Value getValue(int index);

  /**
   * Returns the control flow predecessor Unit for the given index into the PhiExpr. Null if out-of-bounds.
   **/
  public Unit getPred(int index);

  /**
   * Returns the index of the argument associated with the given control flow predecessor Unit. Returns -1 if not found.
   **/
  public int getArgIndex(Unit predTailUnit);

  /**
   * Returns the argument pair corresponding to the given CFG predecessor. Returns null if not found.
   **/
  public ValueUnitPair getArgBox(Unit predTailUnit);

  /**
   * Get the PhiExpr argument corresponding to the given control flow predecessor, returns null if not available.
   **/
  public Value getValue(Unit predTailUnit);

  /**
   * Returns the index of the argument associated with the given control flow predecessor. Returns -1 if not found.
   **/
  public int getArgIndex(Block pred);

  /**
   * Returns the argument pair corresponding to the given CFG predecessor. Returns null if not found.
   **/
  public ValueUnitPair getArgBox(Block pred);

  /**
   * Get the PhiExpr argument corresponding to the given control flow predecessor, returns null if not available.
   **/
  public Value getValue(Block pred);

  /**
   * Modify the PhiExpr argument at the given index with the given information. Returns false on failure.
   **/
  public boolean setArg(int index, Value arg, Unit predTailUnit);

  /**
   * Modify the PhiExpr argument at the given index with the given information. Returns false on failure.
   **/
  public boolean setArg(int index, Value arg, Block pred);

  /**
   * Set the value at the given index into the PhiExpr. Returns false on failure.
   **/
  public boolean setValue(int index, Value arg);

  /**
   * Locate the argument associated with the given CFG predecessor unit and set the value. Returns false on failure.
   **/
  public boolean setValue(Unit predTailUnit, Value arg);

  /**
   * Locate the argument associated with the given CFG predecessor and set the value. Returns false on failure.
   **/
  public boolean setValue(Block pred, Value arg);

  /**
   * Update the CFG predecessor associated with the PhiExpr argument at the given index. Returns false on failure.
   **/
  public boolean setPred(int index, Unit predTailUnit);

  /**
   * Update the CFG predecessor associated with the PhiExpr argument at the given index. Returns false on failure.
   **/
  public boolean setPred(int index, Block pred);

  /**
   * Remove the argument at the given index. Returns false on failure.
   **/
  public boolean removeArg(int index);

  /**
   * Remove the argument corresponding to the given CFG predecessor. Returns false on failure.
   **/
  public boolean removeArg(Unit predTailUnit);

  /**
   * Remove the argument corresponding to the given CFG predecessor. Returns false on failure.
   **/
  public boolean removeArg(Block pred);

  /**
   * Remove the given argument. Returns false on failure.
   **/
  public boolean removeArg(ValueUnitPair arg);

  /**
   * Add the given argument associated with the given CFG predecessor. Returns false on failure.
   **/
  public boolean addArg(Value arg, Block pred);

  /**
   * Add the given argument associated with the given CFG predecessor. Returns false on failure.
   **/
  public boolean addArg(Value arg, Unit predTailUnit);

  /**
   * Set the block number of the Phi node.
   **/
  public void setBlockId(int blockId);

  /**
   * Returns the id number of the block from which the Phi node originated from.
   **/
  public int getBlockId();

  /**
   * The type of the PhiExpr is usually the same as the type of its arguments.
   **/
  public Type getType();

  public void apply(Switch sw);
}
