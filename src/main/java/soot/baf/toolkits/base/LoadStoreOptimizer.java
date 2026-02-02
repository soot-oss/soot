package soot.baf.toolkits.base;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.PhaseOptions;
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.baf.AddInst;
import soot.baf.AndInst;
import soot.baf.ArrayReadInst;
import soot.baf.ArrayWriteInst;
import soot.baf.Baf;
import soot.baf.Dup1Inst;
import soot.baf.DupInst;
import soot.baf.EnterMonitorInst;
import soot.baf.ExitMonitorInst;
import soot.baf.FieldArgInst;
import soot.baf.FieldGetInst;
import soot.baf.FieldPutInst;
import soot.baf.IdentityInst;
import soot.baf.IncInst;
import soot.baf.Inst;
import soot.baf.LoadInst;
import soot.baf.MethodArgInst;
import soot.baf.MulInst;
import soot.baf.NewInst;
import soot.baf.OrInst;
import soot.baf.PushInst;
import soot.baf.StaticGetInst;
import soot.baf.StaticPutInst;
import soot.baf.StoreInst;
import soot.baf.XorInst;
import soot.options.Options;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ZonedBlockGraph;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.Chain;

public class LoadStoreOptimizer extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(LoadStoreOptimizer.class);

  private static final boolean SKIP_SLOW_ASSERTS = true;

  public LoadStoreOptimizer(Singletons.Global g) {
  }

  public static LoadStoreOptimizer v() {
    return G.v().soot_baf_toolkits_base_LoadStoreOptimizer();
  }

  // constants returned by the stackIndependent function.
  final static private int FAILURE = 0;
  final static private int SUCCESS = 1;
  final static private int MAKE_DUP = 2;
  final static private int MAKE_DUP1_X1 = 3;
  final static private int SPECIAL_SUCCESS = 4;
  final static private int HAS_CHANGED = 5;
  final static private int SPECIAL_SUCCESS2 = 6;

  final static private int STORE_LOAD_ELIMINATION = 0;
  final static private int STORE_LOAD_LOAD_ELIMINATION = -1;

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    boolean debug = PhaseOptions.getBoolean(options, "debug");
    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Performing LoadStore optimizations...");
    }
    if (debug) {
      logger.debug("\n\nOptimizing Method: " + body.getMethod().getName());
    }
    new Instance(body, options, debug).go();
  }

  private static class Instance {

    // Instance vars.
    private final boolean debug;
    private final Map<String, String> gOptions;
    private final Chain<Unit> mUnits;
    private final Body mBody;
    private final Map<Unit, Block> mUnitToBlockMap; // maps each Unit to it's containing Block
    private LocalDefs mLocalDefs;
    private LocalUses mLocalUses;

    public Instance(Body b, Map<String, String> options, boolean debug) {
      this.mBody = b;
      this.mUnits = b.getUnits();
      this.gOptions = options;
      this.debug = debug;
      this.mUnitToBlockMap = new HashMap<>();
    }

    public void go() {
      if (!mUnits.isEmpty()) {
        buildUnitToBlockMap();
        assert (SKIP_SLOW_ASSERTS || unitToBlockMapIsValid());

        if (debug) {
          logger.debug("Calling optimizeLoadStore(1)\n");
        }
        optimizeLoadStores(false);
        assert (SKIP_SLOW_ASSERTS || unitToBlockMapIsValid());

        if (PhaseOptions.getBoolean(gOptions, "inter")) {
          if (debug) {
            logger.debug("Calling doInterBlockOptimizations");
          }
          doInterBlockOptimizations();
          assert (SKIP_SLOW_ASSERTS || unitToBlockMapIsValid());
        }

        if (PhaseOptions.getBoolean(gOptions, "sl2") || PhaseOptions.getBoolean(gOptions, "sll2")) {
          if (debug) {
            logger.debug("Calling optimizeLoadStore(2)");
          }
          optimizeLoadStores(true);
          assert (SKIP_SLOW_ASSERTS || unitToBlockMapIsValid());
        }
      }
    }

    /**
     * Computes a map binding each unit in a method to the unique basic block that contains it.
     */
    private void buildUnitToBlockMap() {
      mUnitToBlockMap.clear();// clear the map in case of rebuilds
      BlockGraph blockGraph = new ZonedBlockGraph(mBody);
      if (debug) {
        logger.debug("Method " + mBody.getMethod().getName() + " Block Graph: ");
        logger.debug(blockGraph.toString());
      }
      for (Block block : blockGraph.getBlocks()) {
        for (Unit unit : block) {
          mUnitToBlockMap.put(unit, block);
        }
      }
    }

    private void computeLocalDefsAndLocalUsesInfo() {
      if (mLocalDefs == null) {
        mLocalDefs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(mBody);
      }
      if (mLocalUses == null) {
        mLocalUses = LocalUses.Factory.newLocalUses(mBody, mLocalDefs);
      }
    }

    private void clearLocalDefsAndLocalUsesInfo() {
      mLocalDefs = null;
      mLocalUses = null;
    }

    /**
     * Computes a list of all the stores in mUnits in order of their occurrences therein.
     */
    private List<Unit> buildStoreList() {
      List<Unit> storeList = new ArrayList<>();
      for (Unit unit : mUnits) {
        if (unit instanceof StoreInst) {
          storeList.add(unit);
        }
      }
      return storeList;
    }

    /**
     * Main optimizing method.
     */
    private void optimizeLoadStores(boolean mPass2) {
      computeLocalDefsAndLocalUsesInfo();

      // build a list of all store units in mUnits
      List<Unit> storeList = buildStoreList();

      // Eliminate store/load
      for (boolean hasChanged = true, hasChangedFlag = false; hasChanged;) {
        hasChanged = false;

        // Iterate over the storeList
        nextUnit: for (Iterator<Unit> unitIt = storeList.iterator(); unitIt.hasNext();) {
          final Unit unit = unitIt.next();
          List<UnitValueBoxPair> uses = mLocalUses.getUsesOf(unit);

          // if uses of a store < 3, attempt some form of store/load elimination
          if (uses.size() < 3) {
            // check that all uses have only the current store as their definition
            for (UnitValueBoxPair pair : uses) {
              Unit loadUnit = pair.getUnit();
              if (!(loadUnit instanceof LoadInst)) {
                continue nextUnit;
              }

              List<Unit> defs = mLocalDefs.getDefsOfAt((Local) pair.getValueBox().getValue(), loadUnit);
              if (defs.size() > 1 || defs.get(0) != unit) {
                continue nextUnit;
              }
            }

            // Check that all loads are in the same bb as the store
            {
              Block storeBlock = mUnitToBlockMap.get(unit);
              assert (SKIP_SLOW_ASSERTS || contains(storeBlock, unit));
              for (UnitValueBoxPair pair : uses) {
                Block useBlock = mUnitToBlockMap.get(pair.getUnit());
                assert (SKIP_SLOW_ASSERTS || contains(useBlock, pair.getUnit()));
                if (useBlock != storeBlock) {
                  continue nextUnit;
                }
              }
            }

            // Check for stack independance (automatic reordering may be performed by stackIndependent() fcnt)
            switch (uses.size()) {
              case 0:
                /*
                 * if(Options.getBoolean(gOptions, "s-elimination")) { // replace store by a pop and remove store from store
                 * list replaceUnit(unit, Baf.v().newPopInst(((StoreInst)unit).getOpType())); unitIt.remove();
                 * 
                 * hasChanged = true; hasChangedFlag = false; }
                 */
                break;
              case 1:
                if (PhaseOptions.getBoolean(gOptions, "sl")) {
                  if (!mPass2 || PhaseOptions.getBoolean(gOptions, "sl2")) {
                    // try to eliminate store/load pair
                    Unit loadUnit = uses.get(0).getUnit();
                    Block block = mUnitToBlockMap.get(unit);
                    assert (SKIP_SLOW_ASSERTS || contains(block, unit));
                    int test = stackIndependent(unit, loadUnit, block, STORE_LOAD_ELIMINATION);
                    if (test == SUCCESS || test == SPECIAL_SUCCESS) {
                      block.remove(unit);
                      mUnitToBlockMap.remove(unit);
                      block.remove(loadUnit);
                      mUnitToBlockMap.remove(loadUnit);

                      unitIt.remove();
                      hasChanged = true;
                      hasChangedFlag = false;

                      if (debug) {
                        logger.debug("Store/Load elimination occurred case1.");
                      }
                    }
                  }
                }
                break;
              case 2:
                if (PhaseOptions.getBoolean(gOptions, "sll")) {
                  if (!mPass2 || PhaseOptions.getBoolean(gOptions, "sll2")) {
                    // try to replace store/load/load trio by a flavor of the dup unit
                    Unit firstLoad = uses.get(0).getUnit();
                    Unit secondLoad = uses.get(1).getUnit();

                    // xxx try to optimize this
                    if (mUnits.follows(firstLoad, secondLoad)) {
                      Unit temp = secondLoad;
                      secondLoad = firstLoad;
                      firstLoad = temp;
                    }

                    Block block = mUnitToBlockMap.get(unit);
                    assert (SKIP_SLOW_ASSERTS || contains(block, unit));
                    int result = stackIndependent(unit, firstLoad, block, STORE_LOAD_ELIMINATION);
                    if (result == SUCCESS) {
                      // move the first load just after its defining store.
                      block.remove(firstLoad);
                      block.insertAfter(firstLoad, unit);

                      int res = stackIndependent(unit, secondLoad, block, STORE_LOAD_LOAD_ELIMINATION);
                      if (res == MAKE_DUP) {
                        // replace store by dup, drop both loads
                        Dup1Inst dup = Baf.v().newDup1Inst(((LoadInst) secondLoad).getOpType());
                        dup.addAllTagsOf(unit);
                        replaceUnit(unit, dup);
                        unitIt.remove(); // remove store from store list

                        block.remove(firstLoad);
                        mUnitToBlockMap.remove(firstLoad);
                        block.remove(secondLoad);
                        mUnitToBlockMap.remove(secondLoad);

                        hasChanged = true;
                        hasChangedFlag = false;

                      }
                    } else if (result == SPECIAL_SUCCESS || result == HAS_CHANGED || result == SPECIAL_SUCCESS2) {
                      if (!hasChangedFlag) {
                        hasChangedFlag = true;
                        hasChanged = true;
                      }
                    }
                  }
                }
            }
          }
        }
      }
    }

    /**
     * Checks if the units occurring between [from, to] consume . stack items not produced by these interval units. (ie if
     * the stack height ever goes negative between from and to, assuming the stack height is set to 0 upon executing the
     * instruction following 'from'.
     */
    private boolean isRequiredByFollowingUnits(Unit from, Unit to) {
      if (from != to) {
        int stackHeight = 0;
        Iterator<Unit> it = mUnits.iterator(from, to);
        // advance past the 'from' unit
        it.next();
        while (it.hasNext()) {
          Unit currentInst = it.next();
          if (currentInst == to) {
            break;
          }

          stackHeight -= ((Inst) currentInst).getInCount();
          if (stackHeight < 0) {
            return true;
          }
          stackHeight += ((Inst) currentInst).getOutCount();
        }
      }
      return false;
    }

    private int pushStoreToLoad(final Unit from, final Unit to, final Block block) {
      final Unit storePred = block.getPredOf(from);
      if (storePred == null || ((Inst) storePred).getOutCount() != 1) {
        if (debug) {
          if (storePred == null) {
            logger.debug("xxx: failed due: cannot move past block tail ");
          } else {
            logger.debug("xxx: failed due: pred out-count != 1 ");
          }
        }
        return FAILURE;
      }

      Set<Unit> unitsToMove = new HashSet<>();
      unitsToMove.add(storePred);
      unitsToMove.add(from);
      int h = ((Inst) storePred).getInCount();
      Unit currentUnit = storePred;
      if (h != 0) {
        currentUnit = block.getPredOf(storePred);
        while (currentUnit != null) {

          h -= ((Inst) currentUnit).getOutCount();
          if (h < 0) { // xxx could be more flexible here?
            if (debug) {
              logger.debug("xxx: negative");
            }
            return FAILURE;
          }
          h += ((Inst) currentUnit).getInCount();
          unitsToMove.add(currentUnit);
          if (h == 0) {
            break;
          }
          currentUnit = block.getPredOf(currentUnit);
        }
      }
      if (currentUnit == null) {
        if (debug) {
          logger.debug("xxx: null");
        }
        return FAILURE;
      }

      for (Unit uu = block.getSuccOf(from); uu != to; uu = block.getSuccOf(uu)) {
        for (Unit nu : unitsToMove) {
          if (!canMoveUnitOver(nu, uu)) {
            if (debug) {
              logger.debug("xxx: failure cannot move " + nu + " over " + uu);
            }
            return FAILURE;
          }
          if (debug) {
            logger.debug("can move " + nu + " over " + uu);
          }
        }
      }

      // if we get here it means we can move all the units in the
      // set past the units in between [to, from]
      for (Unit unitToMove = currentUnit; unitToMove != from;) {
        Unit succ = block.getSuccOf(unitToMove);
        if (debug) {
          logger.debug("moving " + unitToMove);
        }
        block.remove(unitToMove);
        block.insertBefore(unitToMove, to);
        unitToMove = succ;
      }
      block.remove(from);
      block.insertBefore(from, to);

      if (debug) {
        logger.debug("xxx: success pushing forward stuff.");
      }
      return SPECIAL_SUCCESS;
    }

    /**
     * @return FAILURE when store load elimination is not possible in any form.
     * @return SUCCESS when a load in a store load pair can be moved IMMEDIATELY after it's defining store.
     * @return MAKE_DUP when a store/load/load trio can be replaced by a dup unit.
     * @return MAKE_DUP_X1 when store/load/load trio can be replaced by a dup1_x1 unit.
     * @return SPECIAL_SUCCESS when a store/load pair can AND must be immediately annihilated.
     * @return HAS_CHANGED when store load elimination is not possible in any form, but some unit reordering has occurred.
     */
    private int stackIndependent(Unit from, Unit to, Block block, int aContext) {
      assert (aContext == STORE_LOAD_ELIMINATION || aContext == STORE_LOAD_LOAD_ELIMINATION);
      if (debug) {
        logger.debug("Trying: " + from + "/" + to + " in block  " + block.getIndexInMethod() + ":");
        logger.debug(
            "context: " + (aContext == STORE_LOAD_ELIMINATION ? "STORE_LOAD_ELIMINATION" : "STORE_LOAD_LOAD_ELIMINATION"));
      }

      int minStackHeightAttained = 0; // records the min stack height attained between [from, to]
      int stackHeight = 0; // records the stack height when similating the effects on the stack

      {
        Iterator<Unit> it = mUnits.iterator(mUnits.getSuccOf(from));
        Unit currentInst = it.next(); // get unit following the store
        if (aContext == STORE_LOAD_LOAD_ELIMINATION) {
          currentInst = it.next(); // jump after first load
        }

        // find minStackHeightAttained
        while (currentInst != to && it.hasNext()) {
          stackHeight -= ((Inst) currentInst).getInCount();
          if (stackHeight < minStackHeightAttained) {
            minStackHeightAttained = stackHeight;
          }

          stackHeight += ((Inst) currentInst).getOutCount();
          currentInst = it.next();
        }
      }

      // note: now stackHeight contains the delta height of the stack
      // after executing the units contained in [from, to] non-inclusive.
      if (debug) {
        logger.debug("nshv = " + stackHeight);
        logger.debug("mshv = " + minStackHeightAttained);
      }

      // Iterate until an elimination clause is taken or no reordering of the code occurs
      for (boolean hasChanged = true; hasChanged;) {
        hasChanged = false;

        if (aContext == STORE_LOAD_LOAD_ELIMINATION) {
          // check for possible sll elimination
          if (stackHeight == 0 && minStackHeightAttained == 0) {
            if (debug) {
              logger.debug("xxx: succ: -1, makedup ");
            }
            return MAKE_DUP;
          } else if (stackHeight == -1 && minStackHeightAttained == -1) {
            if (debug) {
              logger.debug("xxx: succ: -1, makedup , -1");
            }
            return MAKE_DUP;
          } else if (stackHeight == -2 && minStackHeightAttained == -2) {
            if (debug) {
              logger.debug("xxx: succ -1 , make dupx1 ");
            }
            return MAKE_DUP1_X1;
          } else if (minStackHeightAttained < -2) {
            if (debug) {
              logger.debug("xxx: failed due: minStackHeightAttained < -2 ");
            }
            return FAILURE;
          }
        } else if (aContext == STORE_LOAD_ELIMINATION) {
          // check for possible sl elimination
          if (stackHeight == 0 && minStackHeightAttained == 0) {
            if (debug) {
              logger.debug("xxx: success due: 0, SUCCESS");
            }
            return SUCCESS;
          }
          /*
           * xxx broken data depensie problem. else if (minStackHeightAttained == -1 && stackHeight == -1) { // try to make
           * it more generic Unit u = (Unit) block.getPredOf(from); if(u instanceof FieldGetInst) if(block.getPredOf(u)
           * instanceof Dup1Inst) { block.remove(u); block.insertBefore(u, to); block.remove(from); block.insertBefore(from,
           * to); if(debug) { logger.debug("xxx: success due to 1, SPECIAL_SUCCESS2");} return SPECIAL_SUCCESS2; } }
           */
          else if (minStackHeightAttained < 0) {
            return pushStoreToLoad(from, to, block);
          }
        }

        Iterator<Unit> it = mUnits.iterator(mUnits.getSuccOf(from), to);
        Unit u = it.next();
        if (aContext == STORE_LOAD_LOAD_ELIMINATION) {
          u = it.next();
        }

        // find a candidate to move before the store/load/(load) group
        for (; u != to; u = it.next()) {
          Unit unitToMove = null;
          if (((Inst) u).getNetCount() == 1) {
            // xxx remove this check
            if (u instanceof LoadInst || u instanceof PushInst || u instanceof NewInst || u instanceof StaticGetInst
                || u instanceof Dup1Inst) {
              // verify that unitToMove is not required by following units (until the 'to' unit)
              if (!isRequiredByFollowingUnits(u, to)) {
                unitToMove = u;
              }
            } else if (debug) {
              logger.debug("(LoadStoreOptimizer@stackIndependent): found unknown unit w/ getNetCount == 1: " + u);
            }
          }

          if (unitToMove != null) {
            if (tryToMoveUnit(unitToMove, block, from, to, 0)) {
              if (stackHeight > -2 && minStackHeightAttained == -2) {
                if (debug) {
                  logger.debug("xxx: has changed ");
                }
                return HAS_CHANGED;
              }

              stackHeight--;
              if (stackHeight < minStackHeightAttained) {
                minStackHeightAttained = stackHeight;
              }
              hasChanged = true;
              break;
            }
          }
        }
      }

      if (isCommutativeBinOp(block.getSuccOf(to))) {
        if (aContext == STORE_LOAD_ELIMINATION && stackHeight == 1 && minStackHeightAttained == 0) {
          if (debug) {
            logger.debug("xxx: commutative ");
          }
          return SPECIAL_SUCCESS;
        }
        Inst toAsInst = (Inst) to;
        if (toAsInst.getOutCount() == 1 && toAsInst.getInCount() == 0) {
          Inst toPred = (Inst) mUnits.getPredOf(to);
          if (toPred.getOutCount() == 1 && toPred.getInCount() == 0) {
            block.remove(toPred);
            block.insertAfter(toPred, to);
            if (debug) {
              logger.debug("xxx: (commutative) has changed ");
            }
            return HAS_CHANGED;
          }
        }
        if (debug) {
          logger.debug("xxx: (commutative) failed due: ??? ");
        }
        return FAILURE;
      }
      if (aContext == STORE_LOAD_ELIMINATION) {
        return pushStoreToLoad(from, to, block);
      }
      if (debug) {
        logger.debug("xxx: failed due: ??? ");
      }
      return FAILURE;
    }

    /**
     * @return true if aUnit perform a non-local read or write. false otherwise.
     */
    private boolean isNonLocalReadOrWrite(Unit aUnit) {
      return (aUnit instanceof FieldArgInst) || (aUnit instanceof ArrayReadInst) || (aUnit instanceof ArrayWriteInst);
    }

    /**
     * When reordering bytecode, check if it is safe to move aUnitToMove past aUnitToGoOver.
     *
     * @return true if aUnitToMove can be moved past aUnitToGoOver.
     */
    private boolean canMoveUnitOver(Unit aUnitToMove, Unit aUnitToGoOver) { // xxx missing cases

      // can't change method call order or change fieldargInst and method call order
      if ((aUnitToGoOver instanceof MethodArgInst && aUnitToMove instanceof MethodArgInst)
          || (aUnitToGoOver instanceof MethodArgInst && isNonLocalReadOrWrite(aUnitToMove))
          || (isNonLocalReadOrWrite(aUnitToGoOver) && aUnitToMove instanceof MethodArgInst)
          || (aUnitToGoOver instanceof ArrayReadInst && aUnitToMove instanceof ArrayWriteInst)
          || (aUnitToGoOver instanceof ArrayWriteInst && aUnitToMove instanceof ArrayReadInst)
          || (aUnitToGoOver instanceof ArrayWriteInst && aUnitToMove instanceof ArrayWriteInst)
          || (aUnitToGoOver instanceof FieldPutInst && aUnitToMove instanceof FieldGetInst
              && ((FieldArgInst) aUnitToGoOver).getField() == ((FieldArgInst) aUnitToMove).getField())
          || (aUnitToGoOver instanceof FieldGetInst && aUnitToMove instanceof FieldPutInst
              && ((FieldArgInst) aUnitToGoOver).getField() == ((FieldArgInst) aUnitToMove).getField())
          || (aUnitToGoOver instanceof FieldPutInst && aUnitToMove instanceof FieldPutInst
              && ((FieldArgInst) aUnitToGoOver).getField() == ((FieldArgInst) aUnitToMove).getField())
          || (aUnitToGoOver instanceof StaticPutInst && aUnitToMove instanceof StaticGetInst
              && ((FieldArgInst) aUnitToGoOver).getField() == ((FieldArgInst) aUnitToMove).getField())
          || (aUnitToGoOver instanceof StaticGetInst && aUnitToMove instanceof StaticPutInst
              && ((FieldArgInst) aUnitToGoOver).getField() == ((FieldArgInst) aUnitToMove).getField())
          || (aUnitToGoOver instanceof StaticPutInst && aUnitToMove instanceof StaticPutInst
              && ((FieldArgInst) aUnitToGoOver).getField() == ((FieldArgInst) aUnitToMove).getField())) {
        return false;
      }

      // xxx to be safe don't mess w/ monitors. These rules could be relaxed. ? Maybe.
      if (aUnitToGoOver instanceof EnterMonitorInst || aUnitToGoOver instanceof ExitMonitorInst
          || aUnitToMove instanceof EnterMonitorInst || aUnitToGoOver instanceof ExitMonitorInst) {
        return false;
      }

      if (aUnitToGoOver instanceof IdentityInst || aUnitToMove instanceof IdentityInst) {
        return false;
      }

      if (aUnitToMove instanceof LoadInst) {
        if (aUnitToGoOver instanceof StoreInst) {
          if (((StoreInst) aUnitToGoOver).getLocal() == ((LoadInst) aUnitToMove).getLocal()) {
            return false;
          }
        } else if (aUnitToGoOver instanceof IncInst) {
          if (((IncInst) aUnitToGoOver).getLocal() == ((LoadInst) aUnitToMove).getLocal()) {
            return false;
          }
        }
      }

      // don't move def of load pass it.
      if (aUnitToMove instanceof StoreInst) {
        if (aUnitToGoOver instanceof LoadInst) {
          if (((LoadInst) aUnitToGoOver).getLocal() == ((StoreInst) aUnitToMove).getLocal()) {
            return false;
          }
        } else if (aUnitToGoOver instanceof IncInst) {
          if (((IncInst) aUnitToGoOver).getLocal() == ((StoreInst) aUnitToMove).getLocal()) {
            return false;
          }
        }
      }

      if (aUnitToMove instanceof IncInst) {
        if (aUnitToGoOver instanceof StoreInst) {
          if (((StoreInst) aUnitToGoOver).getLocal() == ((IncInst) aUnitToMove).getLocal()) {
            return false;
          }
        } else if (aUnitToGoOver instanceof LoadInst) {
          if (((LoadInst) aUnitToGoOver).getLocal() == ((IncInst) aUnitToMove).getLocal()) {
            return false;
          }
        }
      }
      return true;
    }

    private boolean tryToMoveUnit(Unit unitToMove, Block block, Unit from, Unit to, int flag) {
      if (debug) {
        logger.debug("[tryToMoveUnit]: trying to move:" + unitToMove);
      }
      if (unitToMove == null) {
        return false;
      }

      int h = 0;
      boolean reachedStore = false;
      boolean reorderingOccurred = false;

      for (Unit current = unitToMove; current != block.getHead();) { // do not go past basic block limit
        current = mUnits.getPredOf(current);

        if (!canMoveUnitOver(current, unitToMove)) {
          return false;
        }

        if (current == from) {
          reachedStore = true;
        }

        h -= ((Inst) current).getOutCount();
        if (h < 0) {
          if (debug) {
            logger.debug("(LoadStoreOptimizer@stackIndependent): Stack went negative while trying to reorder code.");
          }
          if (flag == 1 && current instanceof DupInst) {
            block.remove(unitToMove);
            block.insertAfter(unitToMove, current);
            // block.insertAfter(new BSwapInst( ), unitToMove);
          }
          return false;
        }
        h += ((Inst) current).getInCount();

        if (h == 0 && reachedStore) {
          if (!isRequiredByFollowingUnits(unitToMove, to)) {
            if (debug) {
              logger.debug(
                  "(LoadStoreOptimizer@stackIndependent): reordering bytecode move: " + unitToMove + " before: " + current);
            }
            block.remove(unitToMove);
            block.insertBefore(unitToMove, current);

            reorderingOccurred = true;
            break;
          }
        }
      }
      if (debug) {
        if (reorderingOccurred) {
          logger.debug("reordering occurred");
        } else {
          logger.debug("(LoadStoreOptimizer@stackIndependent):failed to find a new slot for unit to move");
        }
      }
      return reorderingOccurred;
    }

    /**
     * Replace 1 or 2 units by a third unit in a block. Both units to replace should be in the same block. The map
     * 'mUnitToBlockMap' is updated. The replacement unit is inserted in the position, of the aToReplace2 if not null,
     * otherwise in aToReplace1's slot.
     *
     * @param aToReplace1
     *          Unit to replace. (shouldn't be null)
     * @param aToReplace2
     *          Second Unit to replace (can be null)
     * @param aReplacement
     *          Unit that replaces the 2 previous units (shouldn't be null)
     */
    private void replaceUnit(Unit aToReplace1, Unit aToReplace2, Unit aReplacement) {
      Block block = mUnitToBlockMap.get(aToReplace1);
      assert (SKIP_SLOW_ASSERTS || contains(block, aToReplace1));

      if (aToReplace2 != null) {
        block.insertAfter(aReplacement, aToReplace2);
        block.remove(aToReplace2);
        mUnitToBlockMap.remove(aToReplace2);
      } else {
        block.insertAfter(aReplacement, aToReplace1);
      }

      block.remove(aToReplace1);
      mUnitToBlockMap.remove(aToReplace1);

      // add the new unit the block map
      mUnitToBlockMap.put(aReplacement, block);
    }

    private void replaceUnit(Unit aToReplace, Unit aReplacement) {
      replaceUnit(aToReplace, null, aReplacement);
    }

    /**
     * @return true if the Block is an exception handler.
     */
    private boolean isExceptionHandlerBlock(Block aBlock) {
      Unit blockHead = aBlock.getHead();
      for (Trap trap : mBody.getTraps()) {
        if (trap.getHandlerUnit() == blockHead) {
          return true;
        }
      }
      return false;
    }

    // not a save function :: goes over block boundries
    private int getDeltaStackHeightFromTo(Unit aFrom, Unit aTo) {
      int h = 0;
      for (Iterator<Unit> it = mUnits.iterator(aFrom, aTo); it.hasNext();) {
        Unit next = it.next();
        h += ((Inst) next).getNetCount();
      }
      return h;
    }

    // not a save function :: goes over block boundries
    private boolean isZeroStackDeltaWithoutClobbering(Unit aFrom, Unit aTo) {
      int h = 0;
      for (Iterator<Unit> it = mUnits.iterator(aFrom, aTo); it.hasNext();) {
        Inst next = (Inst) it.next();
        // detect use of the top stack value at 'aFrom'
        if (next.getInCount() > h) {
          return false;
        }
        h += next.getNetCount();
        // detect removal of the top stack value at 'aFrom'
        if (h < 0) {
          return false;
        }
      }
      return h == 0;
    }

    /**
     * Performs 2 simple inter-block optimizations in order to keep some variables on the stack between blocks. Both are
     * intended to catch 'if' like constructs where the control flow branches temporarily into two paths that join up at a
     * later point.
     */
    private void doInterBlockOptimizations() {
      for (boolean hasChanged = true; hasChanged;) {
        // Ensure LocalDefs and LocalUses are computed
        computeLocalDefsAndLocalUsesInfo();

        hasChanged = false;
        if (debug) {
          logger.debug("[doInterBlockOptimizations] begin pass...");
        }
        for (Unit u : new ArrayList<>(mUnits)) {
          if (u instanceof LoadInst) {
            if (debug) {
              logger.debug("interopt trying: " + u);
            }
            final Block loadBlock = mUnitToBlockMap.get(u);
            assert (SKIP_SLOW_ASSERTS || contains(loadBlock, u));
            final List<Unit> defs = mLocalDefs.getDefsOfAt(((LoadInst) u).getLocal(), u);

            if (debug) {
              logger.debug("  loadBlock: " + loadBlock);
              logger.debug("  defs: " + defs);
            }
            if (defs.size() == 1) { // first optimization
              final Unit def = defs.get(0);
              final Block defBlock = mUnitToBlockMap.get(def);
              assert (SKIP_SLOW_ASSERTS || contains(defBlock, def));
              if (defBlock != loadBlock && !isExceptionHandlerBlock(loadBlock)) {
                if (def instanceof StoreInst) {
                  List<UnitValueBoxPair> uses = mLocalUses.getUsesOf(def);
                  if (uses.size() == 1 && allSuccesorsOfAreThePredecessorsOf(defBlock, loadBlock)) {
                    if (isZeroStackDeltaWithoutClobbering(defBlock.getSuccOf(def), defBlock.getTail())) {
                      boolean res = true;
                      for (Block b : defBlock.getSuccs()) {
                        if (getDeltaStackHeightFromTo(b.getHead(), b.getTail()) != 0 || b.getPreds().size() != 1
                            || b.getSuccs().size() != 1) {
                          res = false;
                          break;
                        }
                      }
                      if (debug) {
                        logger.debug(defBlock.toString() + loadBlock.toString());
                      }
                      if (res) {
                        defBlock.remove(def);
                        mUnitToBlockMap.put(def, loadBlock);
                        loadBlock.insertBefore(def, loadBlock.getHead());
                        hasChanged = true;
                        if (debug) {
                          logger.debug("inter-block opt 1 occurred " + def + " " + u);
                        }
                        if (debug) {
                          logger.debug(defBlock.toString() + loadBlock.toString());
                        }
                      }
                    }
                  }
                }
              }
            } else if (defs.size() == 2) { // second optimization
              final Unit def0 = defs.get(0);
              final Block defBlock0 = mUnitToBlockMap.get(def0);
              assert (SKIP_SLOW_ASSERTS || contains(defBlock0, def0));
              final Unit def1 = defs.get(1);
              final Block defBlock1 = mUnitToBlockMap.get(def1);
              assert (SKIP_SLOW_ASSERTS || contains(defBlock1, def1));
              if (defBlock0 != loadBlock && defBlock1 != loadBlock && defBlock0 != defBlock1
                  && !(isExceptionHandlerBlock(loadBlock))) {
                if (mLocalUses.getUsesOf(def0).size() == 1 && mLocalUses.getUsesOf(def1).size() == 1) {
                  List<Block> def0Succs = defBlock0.getSuccs();
                  List<Block> def1Succs = defBlock1.getSuccs();
                  if (def0Succs.size() == 1 && def1Succs.size() == 1) {
                    if (def0Succs.get(0) == loadBlock && def1Succs.get(0) == loadBlock) {
                      if (loadBlock.getPreds().size() == 2) {
                        final Unit tailB0 = defBlock0.getTail();
                        final Unit tailB1 = defBlock1.getTail();
                        if ((def0 == tailB0 || isZeroStackDeltaWithoutClobbering(defBlock0.getSuccOf(def0), tailB0))
                            && (def1 == tailB1 || isZeroStackDeltaWithoutClobbering(defBlock1.getSuccOf(def1), tailB1))) {

                          defBlock0.remove(def0);
                          defBlock1.remove(def1);
                          loadBlock.insertBefore(def0, loadBlock.getHead());
                          mUnitToBlockMap.put(def0, loadBlock);
                          mUnitToBlockMap.remove(def1);

                          hasChanged = true;
                          if (debug) {
                            logger.debug("inter-block opt 2 occurred " + def0);
                          }
                        } else if (debug) {
                          logger.debug("failed: inter: unacceptable stack offset");
                        }
                      } else if (debug) {
                        logger.debug("failed: inter: 'loadBlock' #preds != 2");
                      }
                    } else if (debug) {
                      logger.debug("failed: inter: successor is not 'loadBlock'");
                    }
                  } else if (debug) {
                    logger.debug("failed: inter: #successors != 1");
                  }
                } else if (debug) {
                  logger.debug("failed: inter: #defs != 1");
                }
              } else if (debug) {
                logger.debug("failed: inter: unacceptable blocks");
              }
            }
          }
        }
        if (debug) {
          logger.debug("[doInterBlockOptimizations] completed pass. changed? " + hasChanged);
        }
        if (hasChanged) {
          // Clear the use/def sets so they will be recomputed if running another
          // iteration or if running optimizeLoadStores() again. Otherwise, their
          // stale information could cause incomplete or incorrect optimization.
          clearLocalDefsAndLocalUsesInfo();
        }
      }
    }

    /**
     * Given 2 blocks, checks whether all the successors of the first block are the predecessors of the second block.
     */
    private boolean allSuccesorsOfAreThePredecessorsOf(Block aFirstBlock, Block aSecondBlock) {
      List<Block> preds = aSecondBlock.getPreds();
      for (Block next : aFirstBlock.getSuccs()) {
        if (!preds.contains(next)) {
          return false;
        }
      }
      return aFirstBlock.getSuccs().size() == preds.size();
    }

    /**
     * @return true if the Unit is a commutative binary operator
     */
    private boolean isCommutativeBinOp(Unit aUnit) {
      return aUnit instanceof AddInst || aUnit instanceof MulInst || aUnit instanceof AndInst || aUnit instanceof OrInst
          || aUnit instanceof XorInst;
    }

    // For assertions
    private boolean unitToBlockMapIsValid() {
      // Ensure every Unit in the body is mapped
      for (Unit u : mUnits) {
        assert (mUnitToBlockMap.containsKey(u));
      }
      HashSet<Block> blocks = new HashSet<Block>();
      for (Map.Entry<Unit, Block> e : mUnitToBlockMap.entrySet()) {
        blocks.add(e.getValue());
        // Ensure the Unit is mapped to the correct Block
        assert (contains(e.getValue(), e.getKey()));
      }
      // Ensure that every Unit in the Block is mapped to the Block
      for (Block b : blocks) {
        final Unit t = b.getTail();
        assert (mUnitToBlockMap.get(t) == b);
        for (Unit u2 = b.getHead(); u2 != t; u2 = b.getSuccOf(u2)) {
          assert (mUnitToBlockMap.get(u2) == b);
        }
      }
      return true;
    }

    // For assertions
    private static boolean contains(Block b, Unit u) {
      final Unit t = b.getTail();
      if (u == t) {
        return true;
      }
      for (Unit u2 = b.getHead(); u2 != t; u2 = b.getSuccOf(u2)) {
        if (u == u2) {
          return true;
        }
      }
      return false;
    }
  }
}
