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

package soot.jimple.spark.pag;
import java.util.*;

import soot.*;
import soot.jimple.*;
import soot.jimple.spark.*;
import soot.jimple.spark.builder.*;
import soot.jimple.spark.internal.*;
import soot.util.*;
import soot.util.queue.*;
import soot.toolkits.scalar.Pair;
import soot.jimple.toolkits.pointer.util.NativeMethodDriver;


/** Part of a pointer assignment graph for a single method.
 * @author Ondrej Lhotak
 */
public final class MethodPAG extends AbstractMethodPAG {
    private PAG pag;
    public AbstractPAG pag() { return pag; }

    protected MethodPAG( PAG pag, SootMethod m ) {
        this.pag = pag;
        this.method = m;
        this.nodeFactory = new MethodNodeFactory( pag, this );
    }

    private Set addedContexts;

    /** Adds this method to the main PAG, with all VarNodes parameterized by
     * varNodeParameter. */
    public void addToPAG( Object varNodeParameter ) {
        if( !hasBeenBuilt ) throw new RuntimeException();
        if( varNodeParameter == null ) {
            if( hasBeenAdded ) return;
            hasBeenAdded = true;
        } else {
            if( addedContexts == null ) addedContexts = new HashSet();
            if( !addedContexts.add( varNodeParameter ) ) return;
        }
        QueueReader reader = (QueueReader) internalReader.clone();
        while(true) {
            Node src = (Node) reader.next();
            if( src == null ) break;
            src = parameterize( src, varNodeParameter );
            Node dst = (Node) reader.next();
            dst = parameterize( dst, varNodeParameter );
            pag.addEdge( src, dst );
        }
        reader = (QueueReader) inReader.clone();
        while(true) {
            Node src = (Node) reader.next();
            if( src == null ) break;
            Node dst = (Node) reader.next();
            dst = parameterize( dst, varNodeParameter );
            pag.addEdge( src, dst );
        }
        reader = (QueueReader) outReader.clone();
        while(true) {
            Node src = (Node) reader.next();
            if( src == null ) break;
            src = parameterize( src, varNodeParameter );
            Node dst = (Node) reader.next();
            pag.addEdge( src, dst );
        }
    }
    public void addInternalEdge( Node src, Node dst ) {
        if( src == null ) return;
        internalEdges.add( src );
        internalEdges.add( dst );
    }
    public void addInEdge( Node src, Node dst ) {
        if( src == null ) return;
        inEdges.add( src );
        inEdges.add( dst );
    }
    public void addOutEdge( Node src, Node dst ) {
        if( src == null ) return;
        outEdges.add( src );
        outEdges.add( dst );
    }
    private ChunkedQueue internalEdges = new ChunkedQueue();
    private ChunkedQueue inEdges = new ChunkedQueue();
    private ChunkedQueue outEdges = new ChunkedQueue();
    private QueueReader internalReader = internalEdges.reader();
    private QueueReader inReader = inEdges.reader();
    private QueueReader outReader = outEdges.reader();

}

