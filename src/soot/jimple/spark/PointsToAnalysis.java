/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.jimple.spark;
import soot.*;
import soot.jimple.*;

/** A generic interface to any type of pointer analysis.
 * @author Ondrej Lhotak
 */

public interface PointsToAnalysis {
    public static final Integer THIS_NODE = new Integer( -1 );
    public static final int RETURN_NODE = -2;
    public static final Integer THROW_NODE = new Integer( -3 );
    public static final Integer ARRAY_ELEMENTS_NODE = new Integer( -4 );
    public static final Integer CAST_NODE = new Integer( -5 );
    public static final Integer STRING_ARRAY_NODE = new Integer( -6 );
    public static final Integer STRING_NODE = new Integer( -7 );
    public static final Integer STRING_NODE_LOCAL = new Integer( -8 );
    public static final Integer EXCEPTION_NODE = new Integer( -9 );
    public static final Integer RETURN_STRING_CONSTANT_NODE = new Integer( -10 );
    public static final Integer STRING_ARRAY_NODE_LOCAL = new Integer( -11 );

    /** Returns the set of objects reaching variable l before stmt in method. */
    public PointsToSet reachingObjects( SootMethod method, Stmt stmt,
            Local l );
}

