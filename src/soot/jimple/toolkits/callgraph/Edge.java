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
import soot.jimple.*;

/** Represents a single edge in a call graph.
 * @author Ondrej Lhotak
 */
public final class Edge
{ 
    /** The method in which the call occurs; may be null for calls not
     * occurring in a specific method (eg. implicit calls by the VM)
     */
    private SootMethod src;
    public SootMethod src() { return src; }

    /** The unit at which the call occurs; may be null for calls not
     * occurring at a specific statement (eg. calls in native code)
     */
    private Unit srcUnit;
    public Unit srcUnit() { return srcUnit; }
    public Stmt srcStmt() { return (Stmt) srcUnit; }
    
    /** The target method of the call edge. */
    private SootMethod tgt;
    public SootMethod tgt() { return tgt; }

    public static final int INVALID = 0;
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
    /** Implicit call to Thread.exit(). */
    public static final int EXIT = 7;
    /** Implicit call to non-trivial finalizer from constructor. */
    public static final int FINALIZE = 8;
    /** Implicit call to run() through AccessController.doPrivileged(). */
    public static final int PRIVILEGED = 9;

    public static final String[] types = { "INVALID",
        "STATIC", "VIRTUAL", "INTERFACE", "SPECIAL",
        "CLINIT", "THREAD", "EXIT", "FINALIZE", "PRIVILEGED" };

    /** The type of edge. Valid types are given by the static final
     * fields above. Note: type should not be returned; instead,
     * accessors such as isExplicit() should be added. */
    private int type;
    public int type() { return type; }

    public Edge( SootMethod src, Unit srcUnit, SootMethod tgt, int type ) {
        this.src = src;
        this.srcUnit = srcUnit;
        this.tgt = tgt;
        this.type = type;
    }

    public Edge( SootMethod src, Stmt srcUnit, SootMethod tgt ) {
        InvokeExpr ie = srcUnit.getInvokeExpr();
        if( ie instanceof VirtualInvokeExpr ) this.type = VIRTUAL;
        else if( ie instanceof SpecialInvokeExpr ) this.type = SPECIAL;
        else if( ie instanceof InterfaceInvokeExpr ) this.type = INTERFACE;
        else if( ie instanceof StaticInvokeExpr ) this.type = STATIC;
        else throw new RuntimeException();
        this.src = src;
        this.srcUnit = srcUnit;
        this.tgt = tgt;
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

    public boolean passesParameters() {
        return isExplicit() || type == THREAD || type == EXIT ||
            type == FINALIZE || type == PRIVILEGED;
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
    
    public static String typeToString(int type) {
        return types[type];
    }
    public String toString() {
        return typeToString(type)+" edge: "+srcUnit+" in "+src+" ==> "+tgt;
    }

    private Edge nextBySrc = this;
    private Edge prevBySrc = this;
    private Edge nextByTgt = this;
    private Edge prevByTgt = this;
    void insertAfterBySrc( Edge other ) {
        nextBySrc = other.nextBySrc;
        nextBySrc.prevBySrc = this;
        other.nextBySrc = this;
        prevBySrc = other;
    }
    void insertAfterByTgt( Edge other ) {
        nextByTgt = other.nextByTgt;
        nextByTgt.prevByTgt = this;
        other.nextByTgt = this;
        prevByTgt = other;
    }
    void insertBeforeBySrc( Edge other ) {
        prevBySrc = other.prevBySrc;
        prevBySrc.nextBySrc = this;
        other.prevBySrc = this;
        nextBySrc = other;
    }
    void insertBeforeByTgt( Edge other ) {
        prevByTgt = other.prevByTgt;
        prevByTgt.nextByTgt = this;
        other.prevByTgt = this;
        nextByTgt = other;
    }
    void remove() {
        nextBySrc.prevBySrc = prevBySrc;
        prevBySrc.nextBySrc = nextBySrc;
        nextByTgt.prevByTgt = prevByTgt;
        prevByTgt.nextByTgt = nextByTgt;
    }
    Edge nextBySrc() {
        return nextBySrc;
    }
    Edge nextByTgt() {
        return nextByTgt;
    }
}


