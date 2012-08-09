/* Soot - a J*va Optimization Framework
 * Copyright (C) 2012 Tata Consultancy Services & Ecole Polytechnique de Montreal
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
package soot.entrypoints;

import soot.SootMethod;

/**
 * Plain interface for a detector of entry points.
 * 
 * @author Marc-Andre Laverdiere-Papineau
 *
 */
public interface EntryPointDetector {

    /**
     * Checks if the given method is an entry point
     * @return true if the SootMethod should be an entry point.
     */
    public boolean isEntryPoint(SootMethod sm);
}
