package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.jimple.JimpleBody;
import soot.options.JBOptions;
import soot.options.Options;

/**
 * A wrapper object for a pack of optimizations. Provides chain-like operations, except that the key is the phase name. This
 * is a specific one for the very messy jb phase.
 */
public class JimpleBodyPack extends BodyPack {

  public JimpleBodyPack() {
    super("jb");
  }

  /**
   * Applies the transformations corresponding to the given options.
   */
  private void applyPhaseOptions(JimpleBody b, Map<String, String> opts) {
    JBOptions options = new JBOptions(opts);

    if (options.use_original_names()) {
      PhaseOptions.v().setPhaseOptionIfUnset("jb.lns", "only-stack-locals");
    }

    final PackManager pacman = PackManager.v();
    final boolean time = Options.v().time();

    if (time) {
      Timers.v().splitTimer.start();
    }

    pacman.getTransform("jb.tt").apply(b); // TrapTigthener
    pacman.getTransform("jb.dtr").apply(b); // DuplicateCatchAllTrapRemover

    // UnreachableCodeEliminator: We need to do this before splitting
    // locals for not creating disconnected islands of useless assignments
    // that afterwards mess up type assignment.
    pacman.getTransform("jb.uce").apply(b);
    pacman.getTransform("jb.ls").apply(b);
    pacman.getTransform("jb.sils").apply(b);

    if (time) {
      Timers.v().splitTimer.end();
    }

    pacman.getTransform("jb.a").apply(b);
    pacman.getTransform("jb.ule").apply(b);

    if (time) {
      Timers.v().assignTimer.start();
    }

    pacman.getTransform("jb.tr").apply(b);

    if (time) {
      Timers.v().assignTimer.end();
    }

    if (options.use_original_names()) {
      pacman.getTransform("jb.ulp").apply(b);
    }
    pacman.getTransform("jb.lns").apply(b); // LocalNameStandardizer
    pacman.getTransform("jb.cp").apply(b); // CopyPropagator
    pacman.getTransform("jb.dae").apply(b); // DeadAssignmentElimintaor
    pacman.getTransform("jb.cp-ule").apply(b); // UnusedLocalEliminator
    pacman.getTransform("jb.lp").apply(b); // LocalPacker
    pacman.getTransform("jb.ne").apply(b); // NopEliminator
    pacman.getTransform("jb.uce").apply(b); // UnreachableCodeEliminator: Again, we might have new dead code
    pacman.getTransform("jb.cp").apply(b); // CopyPropagator

    // LocalNameStandardizer: After all these changes, some locals
    // may end up being eliminated. If we want a stable local iteration
    // order between soot instances, running LocalNameStandardizer
    // again after all other changes is required.
    if (options.stabilize_local_names()) {
      PhaseOptions.v().setPhaseOption("jb.lns", "sort-locals:true");
      pacman.getTransform("jb.lns").apply(b);
    }

    if (time) {
      Timers.v().stmtCount += b.getUnits().size();
    }
  }

  @Override
  protected void internalApply(Body b) {
    applyPhaseOptions((JimpleBody) b, PhaseOptions.v().getPhaseOptions(getPhaseName()));
  }
}
