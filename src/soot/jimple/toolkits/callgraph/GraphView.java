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

/** Represents a view of (possibly a subset of) the edges in a call graph.
 * @author Ondrej Lhotak
 */
public interface GraphView
{ 
    /** Returns a QueueReader object containing all edges added so far, and
     * which will be informed of any new edges that are later added to
     * the graph. */
    public QueueReader listener();
    /** Returns a QueueReader object which will contain ONLY NEW edges
     * which will be added to the graph.
     */
    public QueueReader newListener();
    /** Causes the QueueReader objects to be filled up with any edges
     * that have been added since the last call. */
    public void pollForEdges();
}


