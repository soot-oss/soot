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
    private MethodOrMethodContext src;
    public SootMethod src() {
        if( src == null ) return null; else return src.method();
    }
    public Context srcCtxt() {
        if( src == null ) return null; else return src.context();
    }
    public MethodOrMethodContext getSrc() { return src; }

    /** The unit at which the call occurs; may be null for calls not
     * occurring at a specific statement (eg. calls in native code)
     */
    private Unit srcUnit;
    public Unit srcUnit() { return srcUnit; }
    public Stmt srcStmt() { return (Stmt) srcUnit; }
    
    /** The target method of the call edge. */
    private MethodOrMethodContext tgt;
    public SootMethod tgt() { return tgt.method(); }
    public Context tgtCtxt() { return tgt.context(); }
    public MethodOrMethodContext getTgt() { return tgt; }

    /** The kind of edge. Note: kind should not be tested by other classes;
     *  instead, accessors such as isExplicit() should be added.
     **/
    private Kind kind;
    public Kind kind() { return kind; }

    public Edge( MethodOrMethodContext src, Unit srcUnit, MethodOrMethodContext tgt, Kind kind ) {
        this.src = src;
        this.srcUnit = srcUnit;
        this.tgt = tgt;
        this.kind = kind;
    }

    public Edge( MethodOrMethodContext src, Stmt srcUnit, MethodOrMethodContext tgt ) {
        this.kind = ieToKind( srcUnit.getInvokeExpr() );
        this.src = src;
        this.srcUnit = srcUnit;
        this.tgt = tgt;
    }

    public static Kind ieToKind( InvokeExpr ie ) {
        if( ie instanceof VirtualInvokeExpr ) return Kind.VIRTUAL;
        else if( ie instanceof SpecialInvokeExpr ) return Kind.SPECIAL;
        else if( ie instanceof InterfaceInvokeExpr ) return Kind.INTERFACE;
        else if( ie instanceof StaticInvokeExpr ) return Kind.STATIC;
        else throw new RuntimeException();
    }

    /** Returns true if the call is due to an explicit invoke statement. */
    public boolean isExplicit() {
        return kind.isExplicit();
    }

	
    /** Returns true if the call is due to an explicit instance invoke
     * statement. */
    public boolean isInstance() {
        return kind.isInstance();
    }

    /** Returns true if the call is to static initializer. */
    public boolean isClinit() {
        return kind.isClinit();
    }
    /** Returns true if the call is due to an explicit static invoke
     * statement. */
    public boolean isStatic() {
        return kind.isStatic();
    }

    public boolean passesParameters() {
        return kind.passesParameters();
    }

    public int hashCode() {
        int ret = tgt.hashCode() + kind.getNumber();
        if( src != null ) ret += src.hashCode();
        if( srcUnit != null ) ret += srcUnit.hashCode();
        return ret;
    }
    public boolean equals( Object other ) {
        Edge o = (Edge) other;
        if( o.src != src ) return false;
        if( o.srcUnit != srcUnit ) return false;
        if( o.tgt != tgt ) return false;
        if( o.kind != kind ) return false;
        return true;
    }
    
    public String toString() {
        return kind.toString()+" edge: "+srcUnit+" in "+src+" ==> "+tgt;
    }

    private Edge nextByUnit = this;
    private Edge prevByUnit = this;
    private Edge nextBySrc = this;
    private Edge prevBySrc = this;
    private Edge nextByTgt = this;
    private Edge prevByTgt = this;
    void insertAfterByUnit( Edge other ) {
        nextByUnit = other.nextByUnit;
        nextByUnit.prevByUnit = this;
        other.nextByUnit = this;
        prevByUnit = other;
    }
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
    void insertBeforeByUnit( Edge other ) {
        prevByUnit = other.prevByUnit;
        prevByUnit.nextByUnit = this;
        other.prevByUnit = this;
        nextByUnit = other;
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
        nextByUnit.prevByUnit = prevByUnit;
        prevByUnit.nextByUnit = nextByUnit;
        nextBySrc.prevBySrc = prevBySrc;
        prevBySrc.nextBySrc = nextBySrc;
        nextByTgt.prevByTgt = prevByTgt;
        prevByTgt.nextByTgt = nextByTgt;
    }
    Edge nextByUnit() {
        return nextByUnit;
    }
    Edge nextBySrc() {
        return nextBySrc;
    }
    Edge nextByTgt() {
        return nextByTgt;
    }
    Edge prevByUnit() {
        return prevByUnit;
    }
    Edge prevBySrc() {
        return prevBySrc;
    }
    Edge prevByTgt() {
        return prevByTgt;
    }
}


