/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.util;

import java.util.*;
import soot.*;
import java.io.*;

/** Augmented data type guaranteeing O(1) insertion and removal from a set
 * of ordered, unique elements.  */
public interface Chain extends Collection, Serializable
{
    /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
    public void insertBefore(List toInsert, Object point);
    /** Inserts <code>toInsert</code> in the Chain after <code>point</code>. */
    public void insertAfter(List toInsert, Object point);
    /** Inserts <code>toInsert</code> in the Chain after <code>point</code>. */
    public void insertAfter(Object toInsert, Object point);
    /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
    public void insertBefore(Object toInsert, Object point);
    /** Inserts <code>toInsert</code> in the Chain before <code>point</code>.
     * (It would probably be better to make Chain implement List)*/
    public void insertBefore(Chain toInsert, Object point);
    /** Inserts <code>toInsert</code> in the Chain after <code>point</code>. 
     * (It would probably be better to make Chain implement List)*/
    public void insertAfter(Chain toInsert, Object point);

    
    /** Replaces <code>out</code> in the Chain by <code>in</code>. */
    public void swapWith(Object out, Object in);

    /** Removes the given object from this Chain. */
    public boolean remove(Object u);

    /** Adds the given object at the beginning of the Chain. */
    public void addFirst(Object u);

    /** Adds the given object at the end of the Chain. */
    public void addLast(Object u);

    /** Removes the first object contained in this Chain. */
    public void removeFirst();
    /** Removes the last object contained in this Chain. */
    public void removeLast();

    /** Returns true if object <code>someObject</code> follows object <code>someReferenceObject</code> in the Chain. */
    public boolean follows(Object someObject, Object someReferenceObject);

    /** Returns the first object in this Chain. */
    public Object getFirst();

    /** Returns the last object in this Chain. */
    public Object getLast();
    
    /** Returns the object immediately following <code>point</code>. */
    public Object getSuccOf(Object point);

    /** Returns the object immediately preceding <code>point</code>. */
    public Object getPredOf(Object point);

    /** Returns an iterator over a copy of this chain. 
     * This avoids ConcurrentModificationExceptions from being thrown
     * if the underlying Chain is modified during iteration.
     * Do not use this to remove elements which have not yet been
     * iterated over! */
    public Iterator snapshotIterator();

    /** Returns an iterator over this Chain. */
    public Iterator iterator();

    /** Returns an iterator over this Chain, starting at the given object. */
    public Iterator iterator(Object u);

    /** Returns an iterator over this Chain, starting at head and reaching tail (inclusive). */
    public Iterator iterator(Object head, Object tail);

    /** Returns the size of this Chain. */
    public int size();   
}

