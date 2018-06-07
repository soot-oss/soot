/* Soot - a Java Optimization Framework
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.toolkits.exceptions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import soot.dexpler.DalvikThrowAnalysis;
import soot.options.Options;

/**
 * Factory that returns an appropriate ThrowAnalysis instances for a given task.
 *
 */
public class ThrowAnalysisFactory {

  /**
   * Resolve the ThrowAnalysis to be used for initialization checking (e.g. soot.Body.checkInit())
   *
   */
  public static ThrowAnalysis checkInitThrowAnalysis() {
    switch (Options.v().check_init_throw_analysis()) {
      case soot.options.Options.check_init_throw_analysis_auto:
        if (!Options.v().android_jars().equals("") || !Options.v().force_android_jar().equals("")) {
          // If Android related options are set, use 'dalvik' throw
          // analysis.
          return DalvikThrowAnalysis.v();
        } else {
          return PedanticThrowAnalysis.v();
        }
      case soot.options.Options.check_init_throw_analysis_pedantic:
        return PedanticThrowAnalysis.v();
      case soot.options.Options.check_init_throw_analysis_unit:
        return UnitThrowAnalysis.v();
      case soot.options.Options.check_init_throw_analysis_dalvik:
        return DalvikThrowAnalysis.v();
      default:
        assert false; // The above cases should cover all posible options
        return PedanticThrowAnalysis.v();
    }
  }
}
