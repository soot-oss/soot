/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot;

import java.util.*;
import soot.util.*;

/** An implementation of a Chain which can contain only Units,
 * and handles patching to deal with element insertions and removals.
 * This is done by calling Unit.redirectJumpsToThisTo at strategic
 * times. */
public class PatchingChain extends AbstractCollection implements Chain 
{
    private Chain innerChain;

    /**
     * May be used to find trapped Units.
     **/
    private Body body;
    
    /** Constructs a PatchingChain from the given Chain. */
    public PatchingChain(Chain aChain)
    {
        innerChain = aChain;
        body = null;
    }

    /** Constructs a PatchingChain from the given Chain. */
    public PatchingChain(Chain aChain, Body aBody)
    {
        innerChain = aChain;
        body = aBody;
    }

    /**
     * Returns the inner chain used by the PatchingChain.  In general,
     * this should not be used.  However, direct access to the inner
     * chain may be necessary if you wish to perform perform certain
     * operations (such as control-flow manipulations) without
     * interference from the patching algorithms. 
     **/
    public Chain getNonPatchingChain()
    {
        return innerChain;
    }
    
    /** Adds the given object to this Chain. */
    public boolean add(Object o)
    {
        return innerChain.add(o);
    }

    /** Replaces <code>out</code> in the Chain by <code>in</code>. */
    public void swapWith(Object out, Object in)
    {
        insertBefore(in, out);
        remove(out);
    }

    /** Inserts <code>toInsert</code> in the Chain after <code>point</code>. */
    public void insertAfter(Object toInsert, Object point)
    {
        ((Unit) point).redirectPointersToThisTo((Unit) toInsert, body, false);
        innerChain.insertAfter(toInsert, point);
    }

    /** Inserts <code>toInsert</code> in the Chain after <code>point</code>. */
    public void insertAfter(List toInsert, Object point)
    {
        innerChain.insertAfter(toInsert, point);
    }
    
    /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
    public void insertBefore(List toInsert, Object point)
    {
        LinkedList backwardList = new LinkedList();
        // Insert toInsert backwards into the list
        {
            Iterator it = toInsert.iterator();
            
            while(it.hasNext())
                backwardList.addFirst(it.next());
        }
                
        Object previousPoint = point;
        Iterator it = backwardList.iterator();
        while (it.hasNext())
        {
            Object o = it.next();
            insertBefore(o, previousPoint);
            previousPoint = o;
        }
    }

    /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
    public void insertBefore(Object toInsert, Object point)
    {
        ((Unit) point).redirectJumpsToThisTo((Unit) toInsert);
        innerChain.insertBefore(toInsert, point);
    }

    /** Returns true if object <code>a</code> follows object <code>b</code> in the Chain. */
    public boolean follows(Object a, Object b)
    {
        return innerChain.follows(a,b);
    }

    /** Removes the given object from this Chain. */
    public boolean remove(Object obj)
    {
        boolean res = false;

        if(contains(obj))
        {
            Unit successor;
            
            if((successor = (Unit)getSuccOf(obj)) == null)
                successor = (Unit)getPredOf(obj);
            
            // Fix up any PhiExpr's first if necessary.
            soot.shimple.Shimple.redirectToPreds((Unit)obj, this);

            res = innerChain.remove(obj);

            ((Unit)obj).redirectJumpsToThisTo(successor);
        }

        return res;        
    }

    /** Returns true if this patching chain contains the specified element. */
    public boolean contains(Object u)
    {
        return innerChain.contains(u);
    }

    /** Adds the given object at the beginning of the Chain. */
    public void addFirst(Object u)
    {
        innerChain.addFirst(u);
    }
    
    /** Adds the given object at the end of the Chain. */
    public void addLast(Object u)
    {
        innerChain.addLast(u);
    }
    
    /** Removes the first object from this Chain. */
    public void removeFirst() 
    {
        remove(innerChain.getFirst());
    }
    
    /** Removes the last object from this Chain. */
    public void removeLast()
    {
        remove(innerChain.getLast());
    }
    
    /** Returns the first object in this Chain. */
    public Object getFirst() { return innerChain.getFirst(); }

    /** Returns the last object in this Chain. */
    public Object getLast() { return innerChain.getLast(); }
    
    /** Returns the object immediately following <code>point</code>. */
    public Object getSuccOf(Object point){return innerChain.getSuccOf(point);}

    /** Returns the object immediately preceding <code>point</code>. */
    public Object getPredOf(Object point){return innerChain.getPredOf(point);}

    private class PatchingIterator implements Iterator
    {
        Iterator innerIterator = null;
        Object lastObject;
        boolean state = false;

        PatchingIterator (Chain innerChain) { innerIterator = innerChain.iterator(); }
        PatchingIterator (Chain innerChain, Object u) { innerIterator = innerChain.iterator(u); }
        PatchingIterator (Chain innerChain, Object head, Object tail) { innerIterator = innerChain.iterator(head, tail); }

        public boolean hasNext() { return innerIterator.hasNext(); }
        public Object next() { lastObject = innerIterator.next(); state = true; return lastObject; }
        public void remove() 
        { 
            if (!state)
                throw new IllegalStateException("remove called before first next() call");

            Unit successor;
            
              if((successor = (Unit)getSuccOf(lastObject)) == null)
                  successor = (Unit)getPredOf(lastObject);
            
            innerIterator.remove();

            ((Unit)lastObject).redirectJumpsToThisTo(successor);
        }
    }

    /** Returns an iterator over a copy of this chain. 
     * This avoids ConcurrentModificationExceptions from being thrown
     * if the underlying Chain is modified during iteration.
     * Do not use this to remove elements which have not yet been
     * iterated over! */
    public Iterator snapshotIterator() 
    {
        List l = new ArrayList(); l.addAll(this);
        return l.iterator();
    }
   
    /** Returns an iterator over this Chain. */
    public Iterator iterator() { return new PatchingIterator(innerChain); }

    /** Returns an iterator over this Chain, starting at the given object. */
    public Iterator iterator(Object u) { return new PatchingIterator(innerChain, u); }

    /** Returns an iterator over this Chain, starting at head and reaching tail (inclusive). */
    public Iterator iterator(Object head, Object tail) { return new PatchingIterator(innerChain, head, tail); }

    /** Returns the size of this Chain. */
    public int size(){return innerChain.size(); }
}
