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
import java.util.*;

/** Adapts an iterator over a collection of Edge's to be an iterator
 * over the target methods of the edges.
 * @author Ondrej Lhotak
 */
public final class Targets implements Iterator
{ 
    Iterator edges;
    public Targets( Iterator edges ) {
        this.edges = edges;
    }
    public boolean hasNext() {
        return edges.hasNext();
    }
    public Object next() {
        Edge e = (Edge) edges.next();
        return e.getTgt();
    }
    public void remove() { throw new UnsupportedOperationException(); }
}


