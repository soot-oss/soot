/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.jimple.spark.sets;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.*;
import java.util.*;
import soot.relations.*;

/** Implementation of PointsToSet providing an interface to a BDD of AllocNodes.
 * @author Ondrej Lhotak
 */
public class BDDPointsToSet implements PointsToSet {
    final private Relation bdd;
    public BDDPointsToSet( Relation bdd ) {
        this.bdd = bdd;
    }
    /** Returns true if this set contains no run-time objects. */
    public boolean isEmpty() { return bdd.isEmpty(); }
    /** Returns true if this set shares some objects with other. */
    public boolean hasNonEmptyIntersection( PointsToSet other ) {
        BDDPointsToSet o = (BDDPointsToSet) other;
        Relation intersection = bdd.sameDomains();
        intersection.eqIntersect( bdd, o.bdd );
        return !intersection.isEmpty();
    }
    /** Set of all possible run-time types of objects in the set. */
    public Set possibleTypes() {
        final HashSet ret = new HashSet();
        Iterator it = bdd.iterator();
        while( it.hasNext() ) {
            AllocNode an = (AllocNode) it.next();
            ret.add( an.getType() );
        }
        return ret;
    }

    /** If this points-to set consists entirely of string constants,
     * returns a set of these constant strings.
     * If this point-to set may contain something other than constant
     * strings, returns null. */
    public Set possibleStringConstants() {
        final HashSet ret = new HashSet();
        Iterator it = bdd.iterator();
        while( it.hasNext() ) {
            AllocNode an = (AllocNode) it.next();
            if( !(an instanceof StringConstantNode) ) return null;
            StringConstantNode scn = (StringConstantNode) an;
            ret.add( scn.getString() );
        }
        return ret;
    }

    /** If this points-to set consists entirely of objects of
     * type java.lang.Class of a known class,
     * returns a set of strings that are the names of these classes.
     * If this point-to set may contain something else, returns null. */
    public Set possibleClassConstants() {
        final HashSet ret = new HashSet();
        Iterator it = bdd.iterator();
        while( it.hasNext() ) {
            AllocNode an = (AllocNode) it.next();
            if( !(an instanceof ClassConstantNode) ) return null;
            ClassConstantNode scn = (ClassConstantNode) an;
            ret.add( scn.getString() );
        }
        return ret;
    }

    /* End of public methods. */
    /* End of package methods. */
}

