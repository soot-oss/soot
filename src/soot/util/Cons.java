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

/** A Lisp-style cons cell. */
public final class Cons {
    public Cons( Object car, Object cdr ) {
        this.car = car;
        this.cdr = cdr;
    }
    final private Object car;
    final private Object cdr;
    public int hashCode() {
        int ret = 0;
        if( car != null ) ret += car.hashCode();
        if( cdr != null ) ret += cdr.hashCode();
        return ret;
    }
    public boolean equals(Object o) {
        if( !( o instanceof Cons ) ) return false;
        Cons other = (Cons) o;
        if( car == null ) {
            if( other.car != null ) return false;
        } else {
            if( !car.equals(other.car) ) return false;
        }
        if( cdr == null ) {
            if( other.cdr != null ) return false;
        } else {
            if( !cdr.equals(other.cdr) ) return false;
        }
        return true;
    }
    public Object car() { return car; }
    public Object cdr() { return cdr; }
}
