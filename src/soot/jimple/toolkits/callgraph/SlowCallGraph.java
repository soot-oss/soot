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

package soot.jimple.toolkits.callgraph;
import soot.*;
import soot.util.*;
import soot.util.queue.*;
import java.util.*;

/** Represents the edges in a call graph. This class is meant to act as
 * only a container of edges; code for various call graph builders should
 * be kept out of it, as well as most code for accessing the edges.
 * @author Ondrej Lhotak
 */
public class SlowCallGraph extends CallGraph
{ 
    private Set edges = new HashSet();
    private MultiMap srcMap = new HashMultiMap();
    private MultiMap unitMap = new HashMultiMap();
    private MultiMap tgtMap = new HashMultiMap();
    private ChunkedQueue stream = new ChunkedQueue();
    private QueueReader reader = stream.reader();
    private Edge dummy = new Edge( null, null, null, Kind.INVALID );

    /** Used to add an edge to the call graph. Returns true iff the edge was
     * not already present. */
    public boolean addEdge( Edge e ) {
        if( !edges.add( e ) ) return false;
        stream.add( e );

        srcMap.put( e.getSrc(), e );
        tgtMap.put( e.getTgt(), e );
        unitMap.put( e.srcUnit(), e );

        return true;
    }
    /** Removes the edge e from the call graph. Returns true iff the edge
     * was originally present in the call graph. */
    public boolean removeEdge( Edge e ) {
        if( !edges.remove( e ) ) return false;

        srcMap.remove(e.getSrc(), e);
        tgtMap.remove(e.getTgt(), e);
        unitMap.remove(e.srcUnit(), e);

        return true;
    }

    /** Returns an iterator over all methods that are the sources of at least
     * one edge. */
    public Iterator sourceMethods() {
        return new ArrayList(srcMap.keySet()).iterator();
    }
    /** Returns an iterator over all edges that have u as their source unit. */
    public Iterator edgesOutOf( Unit u ) {
        return new ArrayList(unitMap.get(u)).iterator();
    }
    /** Returns an iterator over all edges that have m as their source method. */
    public Iterator edgesOutOf( MethodOrMethodContext m ) {
        return new ArrayList(srcMap.get(m)).iterator();
    }
    /** Returns an iterator over all edges that have m as their target method. */
    public Iterator edgesInto( MethodOrMethodContext m ) {
        return new ArrayList(tgtMap.get(m)).iterator();
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

