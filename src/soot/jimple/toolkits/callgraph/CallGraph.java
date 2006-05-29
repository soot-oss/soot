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
import soot.util.queue.*;
import java.util.*;

/** Represents the edges in a call graph. This class is meant to act as
 * only a container of edges; code for various call graph builders should
 * be kept out of it, as well as most code for accessing the edges.
 * @author Ondrej Lhotak
 */
public class CallGraph
{ 
    protected Set edges = new HashSet();
    protected ChunkedQueue stream = new ChunkedQueue();
    protected QueueReader reader = stream.reader();
    protected Map srcMethodToEdge = new HashMap();
    protected Map srcUnitToEdge = new HashMap();
    protected Map tgtToEdge = new HashMap();
    protected Edge dummy = new Edge( null, null, null, Kind.INVALID );

    /** Used to add an edge to the call graph. Returns true iff the edge was
     * not already present. */
    public boolean addEdge( Edge e ) {
        if( !edges.add( e ) ) return false;
        stream.add( e );
        Edge position = null;

        position = (Edge) srcUnitToEdge.get( e.srcUnit() );
        if( position == null ) {
            srcUnitToEdge.put( e.srcUnit(), e );
            position = dummy;
        }
        e.insertAfterByUnit( position );

        position = (Edge) srcMethodToEdge.get( e.getSrc() );
        if( position == null ) {
            srcMethodToEdge.put( e.getSrc(), e );
            position = dummy;
        }
        e.insertAfterBySrc( position );

        position = (Edge) tgtToEdge.get( e.getTgt() );
        if( position == null ) {
            tgtToEdge.put( e.getTgt(), e );
            position = dummy;
        }
        e.insertAfterByTgt( position );
        return true;
    }
    /** Removes the edge e from the call graph. Returns true iff the edge
     * was originally present in the call graph. */
    public boolean removeEdge( Edge e ) {
        if( !edges.remove( e ) ) return false;
        e.remove();

        if( srcUnitToEdge.get(e.srcUnit()) == e ) {
            if( e.nextByUnit().srcUnit() == e.srcUnit() ) {
                srcUnitToEdge.put(e.srcUnit(), e.nextByUnit() );
            } else {
                srcUnitToEdge.put(e.srcUnit(), null);
            }
        }

        if( srcMethodToEdge.get(e.getSrc()) == e ) {
            if( e.nextBySrc().getSrc() == e.getSrc() ) {
                srcMethodToEdge.put(e.getSrc(), e.nextBySrc() );
            } else {
                srcMethodToEdge.put(e.getSrc(), null);
            }
        }

        if( tgtToEdge.get(e.getTgt()) == e ) {
            if( e.nextByTgt().getTgt() == e.getTgt() ) {
                tgtToEdge.put(e.getTgt(), e.nextByTgt() );
            } else {
                tgtToEdge.put(e.getTgt(), null);
            }
        }

        return true;
    }

    /** Returns an iterator over all methods that are the sources of at least
     * one edge. */
    public Iterator sourceMethods() {
        return srcMethodToEdge.keySet().iterator();
    }
    /** Returns an iterator over all edges that have u as their source unit. */
    public Iterator edgesOutOf( Unit u ) {
        return new TargetsOfUnitIterator( u );
    }
    class TargetsOfUnitIterator implements Iterator {
        private Edge position = null;
        private Unit u;
        TargetsOfUnitIterator( Unit u ) {
            this.u = u;
            if( u == null ) throw new RuntimeException();
            position = (Edge) srcUnitToEdge.get( u );
            if( position == null ) position = dummy;
        }
        public boolean hasNext() {
            if( position.srcUnit() != u ) return false;
            if( position.kind() == Kind.INVALID ) return false;
            return true;
        }
        public Object next() {
            Edge ret = position;
            position = position.nextByUnit();
            return ret;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    /** Returns an iterator over all edges that have m as their source method. */
    public Iterator edgesOutOf( MethodOrMethodContext m ) {
        return new TargetsOfMethodIterator( m );
    }
    class TargetsOfMethodIterator implements Iterator {
        private Edge position = null;
        private MethodOrMethodContext m;
        TargetsOfMethodIterator( MethodOrMethodContext m ) {
            this.m = m;
            if( m == null ) throw new RuntimeException();
            position = (Edge) srcMethodToEdge.get( m );
            if( position == null ) position = dummy;
        }
        public boolean hasNext() {
            if( position.getSrc() != m ) return false;
            if( position.kind() == Kind.INVALID ) return false;
            return true;
        }
        public Object next() {
            Edge ret = position;
            position = position.nextBySrc();
            return ret;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    /** Returns an iterator over all edges that have m as their target method. */
    public Iterator edgesInto( MethodOrMethodContext m ) {
        return new CallersOfMethodIterator( m );
    }
    class CallersOfMethodIterator implements Iterator {
        private Edge position = null;
        private MethodOrMethodContext m;
        CallersOfMethodIterator( MethodOrMethodContext m ) {
            this.m = m;
            if( m == null ) throw new RuntimeException();
            position = (Edge) tgtToEdge.get( m );
            if( position == null ) position = dummy;
        }
        public boolean hasNext() {
            if( position.getTgt() != m ) return false;
            if( position.kind() == Kind.INVALID ) return false;
            return true;
        }
        public Object next() {
            Edge ret = position;
            position = position.nextByTgt();
            return ret;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    /** Returns a QueueReader object containing all edges added so far, and
     * which will be informed of any new edges that are later added to
     * the graph. */
    public QueueReader listener() {
        return (QueueReader) reader.clone();
    }
    /** Returns a QueueReader object which will contain ONLY NEW edges
     * which will be added to the graph.
     */
    public QueueReader newListener() {
        return stream.reader();
    }
    public String toString() {
        QueueReader reader = listener();
        StringBuffer out = new StringBuffer();
        while(reader.hasNext()) {
            Edge e = (Edge) reader.next();
            out.append( e.toString() + "\n" );
        }
        return out.toString();
    }
    /** Returns the number of edges in the call graph. */
    public int size() {
        return edges.size();
    }
}

