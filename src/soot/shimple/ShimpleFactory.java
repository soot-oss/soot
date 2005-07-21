/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Navindra Umanee <navindra@cs.mcgill.ca>
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

package soot.shimple;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.shimple.*;
import soot.shimple.toolkits.scalar.*;
import soot.shimple.toolkits.graph.*;
import soot.options.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.jimple.toolkits.base.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

/**
 * @author Navindra Umanee
 **/
public interface ShimpleFactory
{
    /**
     * Some constructors may require a body.  If this is not set at
     * the time of need, an exception will be thrown.
     **/
    public void setBody(Body body);

    /**
     * Constructors should memoize their return value.  Call clearCache()
     * to force recomputations if body has changed and setBody()
     * hasn't been called again.
     **/
    public void clearCache();

    public UnitGraph getUnitGraph();
    public BlockGraph getBlockGraph();
    public DominatorsFinder getDominatorsFinder();
    public DominatorTree getDominatorTree();
    public DominanceFrontier getDominanceFrontier();

    public GlobalValueNumberer getGlobalValueNumberer();
    public ReversibleGraph getReverseBlockGraph();
    public DominatorsFinder getReverseDominatorsFinder();
    public DominatorTree getReverseDominatorTree();
    public DominanceFrontier getReverseDominanceFrontier();
}
