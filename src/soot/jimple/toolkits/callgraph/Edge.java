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

package soot.jimple.toolkits.callgraph;
import soot.*;

/** Represents a single edge in a call graph.
 * @author Ondrej Lhotak
 */
public class Edge
{ 
    /** The method in which the call occurs; may be null for calls not
     * occurring in a specific method (eg. implicit calls by the VM)
     */
    public SootMethod src;

    /** The unit at which the call occurs; may be null for calls not
     * occurring at a specific statement (eg. calls in native code)
     */
    public Unit srcUnit;
    
    /** The target method of the call edge. */
    public SootMethod tgt;

    /** Due to explicit invokestatic instruction. */
    public static final int STATIC = 1;
    /** Due to explicit invokevirtual instruction. */
    public static final int VIRTUAL = 2;
    /** Due to explicit invokeinterface instruction. */
    public static final int INTERFACE = 3;
    /** Due to explicit invokespecial instruction. */
    public static final int SPECIAL = 4;
    /** Implicit call to static initializer. */
    public static final int CLINIT = 5;
    /** Implicit call to Thread.run() due to Thread.start() call. */
    public static final int THREAD = 6;

    /** The type of edge. Valid types are given by the static final
     * fields above. Note: type should not be returned; instead,
     * accessors such as isExplicit() should be added. */
    private int type;

    public Edge( SootMethod src, Unit srcUnit, SootMethod tgt, int type ) {
        this.src = src;
        this.srcUnit = srcUnit;
        this.tgt = tgt;
        this.type = type;
    }

    /** Returns true if the call is due to an explicit invoke statement. */
    public boolean isExplicit() {
        return isInstance() || isStatic();
    }

    /** Returns true if the call is due to an explicit instance invoke
     * statement. */
    public boolean isInstance() {
        return type == VIRTUAL || type == INTERFACE || type == SPECIAL;
    }

    /** Returns true if the call is due to an explicit static invoke
     * statement. */
    public boolean isStatic() {
        return type == STATIC;
    }


    public int hashCode() {
        int ret = tgt.hashCode() + type;
        if( src != null ) ret += src.hashCode();
        if( srcUnit != null ) ret += srcUnit.hashCode();
        return ret;
    }
    public boolean equals( Object other ) {
        Edge o = (Edge) other;
        if( o.src != src ) return false;
        if( o.srcUnit != srcUnit ) return false;
        if( o.tgt != tgt ) return false;
        if( o.type != type ) return false;
        return true;
    }
}


