package soot.jimple.spark.ondemand;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Manu Sridharan
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

import soot.jimple.spark.internal.TypeManager;

public enum HeuristicType {

  MANUAL, INCR, EVERY, MANUALINCR, NOTHING;

  public static FieldCheckHeuristic getHeuristic(HeuristicType type, TypeManager tm, int maxPasses) {
    FieldCheckHeuristic ret = null;
    switch (type) {
      case MANUAL:
        ret = new ManualFieldCheckHeuristic();
        break;
      case INCR:
        ret = new InnerTypesIncrementalHeuristic(tm, maxPasses);
        break;
      case EVERY:
        ret = new EverythingHeuristic();
        break;
      case MANUALINCR:
        ret = new ManualAndInnerHeuristic(tm, maxPasses);
        break;
      case NOTHING:
        ret = new NothingHeuristic();
        break;
      default:
        break;
    }
    return ret;
  }

}
