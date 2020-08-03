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

  /** Applies the transformations corresponding to the given options. */
  private void applyPhaseOptions(JimpleBody b, Map<String, String> opts) {
    JBOptions options = new JBOptions(opts);

    if (options.use_original_names()) {
      PhaseOptions.v().setPhaseOptionIfUnset("jb.lns", "only-stack-locals");
    }

    if (Options.v().time()) {
      Timers.v().splitTimer.start();
    }

    final PackManager pacMan = PackManager.v();
    pacMan.getTransform("jb.tt").apply(b); // TrapTigthener
    pacMan.getTransform("jb.dtr").apply(b); // DuplicateCatchAllTrapRemover

    // UnreachableCodeEliminator: We need to do this before splitting
    // locals for not creating disconnected islands of useless assignments
    // that afterwards mess up type assignment.
    pacMan.getTransform("jb.uce").apply(b);

    pacMan.getTransform("jb.ls").apply(b);

    if (Options.v().time()) {
      Timers.v().splitTimer.end();
    }

    pacMan.getTransform("jb.a").apply(b);
    pacMan.getTransform("jb.ule").apply(b);

    if (Options.v().time()) {
      Timers.v().assignTimer.start();
    }

    pacMan.getTransform("jb.tr").apply(b);

    if (Options.v().time()) {
      Timers.v().assignTimer.end();
    }

    if (options.use_original_names()) {
      pacMan.getTransform("jb.ulp").apply(b);
    }
    pacMan.getTransform("jb.lns").apply(b); // LocalNameStandardizer
    pacMan.getTransform("jb.cp").apply(b); // CopyPropagator
    pacMan.getTransform("jb.dae").apply(b); // DeadAssignmentElimintaor
    pacMan.getTransform("jb.cp-ule").apply(b); // UnusedLocalEliminator
    pacMan.getTransform("jb.lp").apply(b); // LocalPacker
    pacMan.getTransform("jb.ne").apply(b); // NopEliminator
    pacMan.getTransform("jb.uce").apply(b); // UnreachableCodeEliminator: Again, we might have new dead code

    // LocalNameStandardizer: After all these changes, some locals
    // may end up being eliminated. If we want a stable local iteration
    // order between soot instances, running LocalNameStandardizer
    // again after all other changes is required.
    if (options.stabilize_local_names()) {
      PhaseOptions.v().setPhaseOption("jb.lns", "sort-locals:true");
      pacMan.getTransform("jb.lns").apply(b);
    }

    if (Options.v().time()) {
      Timers.v().stmtCount += b.getUnits().size();
    }
  }

  @Override
  protected void internalApply(Body b) {
    applyPhaseOptions((JimpleBody) b, PhaseOptions.v().getPhaseOptions(getPhaseName()));
  }
}
