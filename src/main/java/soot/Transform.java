package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai and Patrick Lam
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

import soot.options.Options;
import soot.util.PhaseDumper;

/**
 * Maintains the pair (phaseName, singleton) needed for a transformation.
 */
public class Transform implements HasPhaseOptions {
  private static final Logger logger = LoggerFactory.getLogger(Transform.class);
  final private boolean DEBUG;
  final String phaseName;
  final Transformer t;

  public Transform(String phaseName, Transformer t) {
    this.DEBUG = Options.v().dump_body().contains(phaseName);
    this.phaseName = phaseName;
    this.t = t;
  }

  public String getPhaseName() {
    return phaseName;
  }

  public Transformer getTransformer() {
    return t;
  }

  private String declaredOpts;
  private String defaultOpts;

  public String getDeclaredOptions() {
    if (declaredOpts != null) {
      return declaredOpts;
    }
    return Options.getDeclaredOptionsForPhase(phaseName);
  }

  public String getDefaultOptions() {
    if (defaultOpts != null) {
      return defaultOpts;
    }
    return Options.getDefaultOptionsForPhase(phaseName);
  }

  /**
   * Allows user-defined phases to have options other than just enabled without having to mess with the XML. Call this method
   * with a space-separated list of options declared for this Transform. Only declared options may be passed to this
   * transform as a phase option.
   */
  public void setDeclaredOptions(String options) {
    declaredOpts = options;
  }

  /**
   * Allows user-defined phases to have options other than just enabled without having to mess with the XML. Call this method
   * with a space-separated list of option:value pairs that this Transform is to use as default parameters (eg
   * `enabled:off').
   */
  public void setDefaultOptions(String options) {
    defaultOpts = options;
  }

  public void apply() {
    Map<String, String> options = PhaseOptions.v().getPhaseOptions(phaseName);
    if (PhaseOptions.getBoolean(options, "enabled")) {
      if (Options.v().verbose()) {
        logger.debug("" + "Applying phase " + phaseName + " to the scene.");
      }
    }
    if (DEBUG) {
      PhaseDumper.v().dumpBefore(getPhaseName());
    }

    ((SceneTransformer) t).transform(phaseName, options);

    if (DEBUG) {
      PhaseDumper.v().dumpAfter(getPhaseName());
    }
  }

  public void apply(Body b) {
    Map<String, String> options = PhaseOptions.v().getPhaseOptions(phaseName);
    if (PhaseOptions.getBoolean(options, "enabled")) {
      if (Options.v().verbose()) {
        logger.debug("" + "Applying phase " + phaseName + " to " + b.getMethod() + ".");
      }
    }
    if (DEBUG) {
      PhaseDumper.v().dumpBefore(b, getPhaseName());
    }

    ((BodyTransformer) t).transform(b, phaseName, options);

    if (DEBUG) {
      PhaseDumper.v().dumpAfter(b, getPhaseName());
    }
  }

  @Override
  public String toString() {
    return phaseName;
  }

}
