package soot.jbco;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.io.PrintStream;

import soot.G;

/**
 * @author Michael Batchelder
 *         <p>
 *         Created on 19-Jun-2006
 */
public interface IJbcoTransform {

  @Deprecated
  PrintStream out = soot.G.v().out;
  @Deprecated
  boolean output = G.v().soot_options_Options().verbose() || soot.jbco.Main.jbcoVerbose;
  @Deprecated
  boolean debug = soot.jbco.Main.jbcoDebug;

  /**
   * Gets the code name of {@link IJbcoTransform jbco transformer} implementation.
   *
   * @return the code name of {@link IJbcoTransform jbco transformer}
   */
  String getName();

  /**
   * Gets array of {@link IJbcoTransform jbco transformer} code names on which current transformer depends on.
   *
   * @return array of code names
   */
  String[] getDependencies();

  /**
   * Prints summary of the produced changes.
   */
  void outputSummary();

  /**
   * Checks if {@link IJbcoTransform jbco transformer} can log extra information.
   *
   * @return {@code true} when {@link IJbcoTransform jbco transformer} can log extra information; {@code false} otherwise
   */
  default boolean isVerbose() {
    return G.v().soot_options_Options().verbose() || soot.jbco.Main.jbcoVerbose;
  }

  /**
   * Checks if {@link IJbcoTransform jbco transformer} can log debug information.
   *
   * @return {@code true} when {@link IJbcoTransform jbco transformer} can log debug information; {@code false} otherwise
   */
  default boolean isDebugEnabled() {
    return soot.jbco.Main.jbcoDebug;
  }

}
