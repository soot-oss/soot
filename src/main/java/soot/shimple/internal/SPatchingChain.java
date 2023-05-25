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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.TrapManager;
import soot.Unit;
import soot.UnitBox;
import soot.UnitPatchingChain;
import soot.options.Options;
import soot.shimple.PhiExpr;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.util.Chain;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

/**
 * Internal Shimple extension of PatchingChain.
 *
 * @author Navindra Umanee
 * @see soot.PatchingChain
 */
public class SPatchingChain extends UnitPatchingChain {
  private static final Logger logger = LoggerFactory.getLogger(SPatchingChain.class);

  /**
   * Needed to find non-trapped Units of the body.
   */
  protected final Body body;

  protected final boolean debug;

  /**
   * Map from UnitBox to the Phi node owning it.
   */
  protected Map<UnitBox, Unit> boxToPhiNode = new HashMap<UnitBox, Unit>();

  /**
   * Set of the values of boxToPhiNode. Used to allow O(1) contains() on the values.
   */
  protected Set<Unit> phiNodeSet = new HashSet<Unit>();

  /**
   * Flag that indicates whether control flow falls through from the unit referenced in the SUnitBox to the Phi node that
   * owns the SUnitBox.
   */
  protected Map<SUnitBox, Boolean> boxToNeedsPatching = new HashMap<SUnitBox, Boolean>();

  public SPatchingChain(Body aBody, Chain<Unit> aChain) {
    super(aChain);
    this.body = aBody;
    boolean debug = Options.v().debug();
    if (aBody instanceof ShimpleBody) {
      debug |= ((ShimpleBody) aBody).getOptions().debug();
    }
    this.debug = debug;
  }

  @Override
  public boolean add(Unit o) {
    processPhiNode(o);
    return super.add(o);
  }

  @Override
  public void swapWith(Unit out, Unit in) {
    // Ensure that branching statements are swapped correctly.
    // The normal swapWith implementation would still work
    // correctly but redirectToPreds performed during the remove
    // would be more expensive and might print warnings if no
    // actual CFG predecessors for out was found due to the
    // insertion of branching statement in.
    processPhiNode(in);
    Shimple.redirectPointers(out, in);
    super.insertBefore(in, out);// use super to avoid repeating processPhiNode(in)
    this.remove(out);
  }

  @Override
  public void insertAfter(Unit toInsert, Unit point) {
    // important to do these before the patching, so that
    // computeNeedsPatching works properly
    processPhiNode(toInsert);
    super.insertAfter(toInsert, point);

    // update any pointers from Phi nodes only if the unit
    // being inserted is in the same basic block as point.
    //
    // no need to move the pointers
    if (!point.fallsThrough()) {
      return;
    }

    // move pointers unconditionally, needed as a special case
    if (!point.branches()) {
      if (body == null || !TrapManager.getTrappedUnitsOf(body).contains(point)) {
        Shimple.redirectPointers(point, toInsert);
        return;
      }
    }

    /* handle each UnitBox individually */
    for (UnitBox ub : new ArrayList<>(point.getBoxesPointingToThis())) {
      if (ub.getUnit() != point) {
        throw new RuntimeException("Assertion failed.");
      }
      if (ub.isBranchTarget()) {
        continue;
      }

      SUnitBox box = getSBox(ub);
      Boolean needsPatching = boxToNeedsPatching.get(box);
      if (needsPatching == null || box.isUnitChanged()) {
        // if boxes were added or removed to the known Phi
        if (!boxToPhiNode.containsKey(box)) {
          reprocessPhiNodes();

          // *** FIXME: Disabling this allows us to have
          // PiExpr that have UnitBox pointers.
          // I think this means that any changes
          // to the relevant Unit will be ignored by
          // SPatchingChain.
          //
          // Hopefully this also means that any
          // transformation that moves/removes/modifies
          // a Unit pointed at by a PiExpr knows what
          // it's doing.
          if (!boxToPhiNode.containsKey(box) && debug) {
            throw new RuntimeException("SPatchingChain has pointers from a Phi node that has never been seen.");
          }
        }

        computeNeedsPatching();
        needsPatching = boxToNeedsPatching.get(box);

        if (needsPatching == null) {
          // maybe the user forgot to clearUnitBoxes()
          // when removing a Phi node, or the user removed
          // a Phi node and hasn't put it back yet
          if (debug) {
            logger.warn("Orphaned UnitBox to " + point + "?  SPatchingChain will not move the pointer.");
          }
          continue;
        }
      }

      if (needsPatching) {
        box.setUnit(toInsert);
        box.setUnitChanged(false);
      }
    }
  }

  @Override
  public void insertAfter(List<Unit> toInsert, Unit point) {
    for (Unit unit : toInsert) {
      processPhiNode(unit);
    }
    super.insertAfter(toInsert, point);
  }

  @Override
  public void insertAfter(Chain<Unit> toInsert, Unit point) {
    for (Unit unit : toInsert) {
      processPhiNode(unit);
    }
    super.insertAfter(toInsert, point);
  }

  @Override
  public void insertAfter(Collection<? extends Unit> toInsert, Unit point) {
    for (Unit unit : toInsert) {
      processPhiNode(unit);
    }
    super.insertAfter(toInsert, point);
  }

  @Override
  public void insertBefore(Unit toInsert, Unit point) {
    processPhiNode(toInsert);
    super.insertBefore(toInsert, point);
  }

  @Override
  public void insertBefore(List<Unit> toInsert, Unit point) {
    for (Unit unit : toInsert) {
      processPhiNode(unit);
    }
    super.insertBefore(toInsert, point);
  }

  @Override
  public void insertBefore(Chain<Unit> toInsert, Unit point) {
    for (Unit unit : toInsert) {
      processPhiNode(unit);
    }
    super.insertBefore(toInsert, point);
  }

  @Override
  public void insertBefore(Collection<? extends Unit> toInsert, Unit point) {
    for (Unit unit : toInsert) {
      processPhiNode(unit);
    }
    super.insertBefore(toInsert, point);
  }

  @Override
  public void addFirst(Unit u) {
    processPhiNode(u);
    super.addFirst(u);
  }

  @Override
  public void addLast(Unit u) {
    processPhiNode(u);
    super.addLast(u);
  }

  public boolean remove(Unit u) {
    if (contains(u)) {
      Shimple.redirectToPreds(body, u);
      boolean ret = super.remove(u);
      patchAfterRemoval(u);
      return ret;
    } else {
      return false;
    }
  }

  @Override
  public boolean remove(Object obj) {
    return (obj instanceof Unit) ? remove((Unit) obj) : false;
  }

  // After all patching is done, clear references to this Unit
  // from any Unit that one of its UnitBoxes referenced.
  protected static void patchAfterRemoval(Unit removing) {
    if (removing != null) {
      for (UnitBox ub : removing.getUnitBoxes()) {
        Unit u = ub.getUnit();
        if (u != null) {
          u.removeBoxPointingToThis(ub);
        }
      }
    }
  }

  protected void processPhiNode(Unit phiNode) {
    PhiExpr phi = Shimple.getPhiExpr(phiNode);

    // not a Phi node
    // already processed previously, unit chain manipulations?
    if ((phi == null) || phiNodeSet.contains(phiNode)) {
      return;
    }

    for (UnitBox box : phi.getUnitBoxes()) {
      boxToPhiNode.put(box, phiNode);
      phiNodeSet.add(phiNode);
    }
  }

  protected void reprocessPhiNodes() {
    Set<Unit> phiNodes = new HashSet<Unit>(boxToPhiNode.values());
    this.boxToPhiNode = new HashMap<UnitBox, Unit>();
    this.phiNodeSet = new HashSet<Unit>();
    this.boxToNeedsPatching = new HashMap<SUnitBox, Boolean>();

    for (Unit next : phiNodes) {
      processPhiNode(next);
    }
  }

  /**
   * Computes {@link #boxToNeedsPatching} which maps each UnitBox from a PhiExpr to true if the referenced Unit falls-through
   * to the statement containing the PhiExpr, false otherwise.
   *
   * NOTE: This will *miss* all the Phi nodes outside a chain. So make sure you know what you are doing if you remove a Phi
   * node from a chain and don't put it back or call clearUnitBoxes() on it.
   */
  protected void computeNeedsPatching() {
    if (boxToPhiNode.isEmpty()) {
      return;
    }

    // we track the fallthrough control flow from boxes to the
    // corresponding Phi statements. trackedPhi provides a
    // mapping from the Phi being tracked to its relevant boxes.
    final MultiMap<Unit, UnitBox> trackedPhiToBoxes = new HashMultiMap<Unit, UnitBox>();

    // consider:
    //
    // if blah goto label1
    // label1:
    //
    // Here control flow both fallsthrough and branches to label1.
    // If such an if statement is encountered, we do not want to
    // move any UnitBox pointers beyond the if statement.
    final Set<Unit> trackedBranchTargets = new HashSet<Unit>();
    for (Unit u : this) {
      // update trackedPhiToBoxes
      List<UnitBox> boxesToTrack = u.getBoxesPointingToThis();
      if (boxesToTrack != null) {
        for (UnitBox boxToTrack : boxesToTrack) {
          if (!boxToTrack.isBranchTarget()) {
            trackedPhiToBoxes.put(boxToPhiNode.get(boxToTrack), boxToTrack);
          }
        }
      }

      // update trackedBranchTargets
      if (u.fallsThrough() && u.branches()) {
        for (UnitBox ub : u.getUnitBoxes()) {
          trackedBranchTargets.add(ub.getUnit());
        }
      }

      // the tracked Phi nodes may be reached through branching.
      // (note: if u is a Phi node and not a trackedBranchTarget, this
      // is not triggered since u would fall through in that case.)
      if (!u.fallsThrough() || trackedBranchTargets.contains(u)) {
        for (UnitBox next : trackedPhiToBoxes.values()) {
          SUnitBox box = getSBox(next);
          boxToNeedsPatching.put(box, Boolean.FALSE);
          box.setUnitChanged(false);
        }
        trackedPhiToBoxes.clear();
        continue;
      }

      // we found one of the Phi nodes pointing to a Unit
      Set<UnitBox> boxes = trackedPhiToBoxes.get(u);
      if (boxes != null) {
        for (UnitBox ub : boxes) {
          SUnitBox box = getSBox(ub);
          // falls through
          boxToNeedsPatching.put(box, Boolean.TRUE);
          box.setUnitChanged(false);
        }

        trackedPhiToBoxes.remove(u);
      }
    }

    // after the iteration, the rest do not fall through
    for (UnitBox next : trackedPhiToBoxes.values()) {
      SUnitBox box = getSBox(next);
      boxToNeedsPatching.put(box, Boolean.FALSE);
      box.setUnitChanged(false);
    }
  }

  protected SUnitBox getSBox(UnitBox box) {
    if (box instanceof SUnitBox) {
      return (SUnitBox) box;
    } else {
      throw new RuntimeException("Shimple box not an SUnitBox?");
    }
  }

  protected class SPatchingIterator extends PatchingIterator {
    SPatchingIterator(Chain<Unit> innerChain) {
      super(innerChain);
    }

    SPatchingIterator(Chain<Unit> innerChain, Unit u) {
      super(innerChain, u);
    }

    SPatchingIterator(Chain<Unit> innerChain, Unit head, Unit tail) {
      super(innerChain, head, tail);
    }

    @Override
    public void remove() {
      if (!state) {
        throw new IllegalStateException("remove called before first next() call");
      }
      state = false;
      Unit victim = lastObject;
      Shimple.redirectToPreds(SPatchingChain.this.body, victim);
      patchBeforeRemoval(innerChain, victim);
      innerIterator.remove();
      patchAfterRemoval(victim);
    }
  }

  @Override
  public Iterator<Unit> iterator() {
    return new SPatchingIterator(innerChain);
  }

  @Override
  public Iterator<Unit> iterator(Unit u) {
    return new SPatchingIterator(innerChain, u);
  }

  @Override
  public Iterator<Unit> iterator(Unit head, Unit tail) {
    return new SPatchingIterator(innerChain, head, tail);
  }
}
