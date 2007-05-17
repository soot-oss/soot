/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Manu Sridharan
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
package soot.jimple.spark.ondemand;

import soot.jimple.spark.internal.TypeManager;

public enum HeuristicType {

    MANUAL, INCR, EVERY, MANUALINCR, NOTHING;
    
    public static FieldCheckHeuristic getHeuristic(HeuristicType type,
            TypeManager tm, int maxPasses) {
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
