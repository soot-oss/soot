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

/** Provides access to the call graph edges selected because they originate
 * or target a specific statement or method.
 * criteria.
 * @author Ondrej Lhotak
 */
public abstract class Selector
{ 
    protected GraphView graph;
    private QueueReader listener;
    protected Map map = new HashMap();
    public Selector( GraphView graph ) {
        this.graph = graph;
        listener = graph.listener();
        updateEdges();
    }
    private void updateEdges() {
        graph.pollForEdges();
        while(true) {
            Edge e = (Edge) listener.next();
            if( e == null ) break;
            addEdge( e );
        }
    }
    protected abstract void addEdge( Edge e );
    public Iterator iterator( Object src ) {
        updateEdges();
        Collection l = (Collection) map.get(src);
        if( l == null ) l = Collections.EMPTY_LIST;
        return l.iterator();
    }
    public Iterator keys() {
        updateEdges();
        return map.keySet().iterator();
    }
}


