/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot.util;

/** A decorator for jedd.Numberer used so that we don't need the Jedd runtime
 * to run Soot unless we actually use Jedd code in Soot.
 */
public final class JeddNumberer implements jedd.Numberer {
    final soot.util.Numberer n;
    public JeddNumberer( soot.util.Numberer n ) {
        this.n = n;
    }
    /** Tells the numberer that a new object needs to be assigned a number. */
    public void add( Object o ) { n.add(o); }
    /** Should return the number that was assigned to object o that was
     * previously passed as an argument to add().
     */
    public int get( Object o ) { return n.get(o); }
    /** Should return the object that was assigned the number number. */
    public Object get( int number ) { return n.get(number); }
    /** Should return the number of objects that have been assigned numbers. */
    public int size() { return n.size(); }
}
