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

package soot.util.queue;
import java.util.*;

/** A queue of Object's. One can add objects to the queue, and they are
 * later read by a QueueReader. One can create arbitrary numbers of
 * QueueReader's for a queue, and each one receives all the Object's that
 * are added. Only objects that have not been read by all the QueueReader's
 * are kept. A QueueReader only receives the Object's added to the queue
 * <b>after</b> the QueueReader was created.
 * @author Ondrej Lhotak
 */
public class QueueReader<E> implements java.util.Iterator<E>
{ 
    private E[] q;
    private int index;
    QueueReader( E[] q, int index ) {
        this.q = q;
        this.index = index;
    }
    /** Returns (and removes) the next object in the queue, or null if
     * there are none. */
    @SuppressWarnings("unchecked")
	public final E next() {
        if( q[index] == null ) throw new NoSuchElementException();
        if( index == q.length - 1 ) {
            q = (E[]) q[index];
            index = 0;
            if( q[index] == null ) throw new NoSuchElementException();
        }
        E ret = q[index];
        if( ret == ChunkedQueue.NULL_CONST ) ret = null;
        index++;
        return ret;
    }

    /** Returns true iff there is currently another object in the queue. */
    @SuppressWarnings("unchecked")
	public final boolean hasNext() {
        if (q[index] == null) return false;
        if (index == q.length - 1) {
            q = (E[]) q[index];
            index = 0;
            if (q[index] == null) return false;
        }
        return true;
    }

    public final void remove() {
        throw new UnsupportedOperationException();
    }

    public final QueueReader<E> clone() {
        return new QueueReader<E>( q, index );
    }
}


