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

/** Represents a subset of the edges in a call graph satisfying an EdgePredicate
 * predicate.
 * @author Ondrej Lhotak
 */
public class Filter implements Iterator
{ 
    private Iterator source;
    private EdgePredicate pred;
    private Edge next = null;
    public Filter( EdgePredicate pred ) {
        this.pred = pred;
    }
    public Iterator wrap( Iterator source ) {
        this.source = source;
        advance();
        return this;
    }
    private void advance() {
        while( source.hasNext() ) {
            next = (Edge) source.next();
            if( pred.want( next ) ) {
                return;
            }
        }
        next = null;
    }
    public boolean hasNext() {
        return next != null;
    }
    public Object next() {
        Object ret = next;
        advance();
        return ret;
    }
    public void remove() {
        throw new UnsupportedOperationException(); 
    }
}

