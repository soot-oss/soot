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

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/** Reference implementation of the Chain interface, 
    using a HashMap as the underlying structure. */
public class HashChain<E> extends AbstractCollection<E>
    implements Chain<E> 
{
    private final Map<E, Link<E>> map = new HashMap<E, Link<E>>(); 
    private E firstItem;
    private E lastItem;
    private long stateCount = 0;  

    /** Erases the contents of the current HashChain. */
    public void clear() 
    {
        stateCount++;
        firstItem = lastItem = null;
        map.clear();
    }

    public void swapWith(E out, E in)
    {
        insertBefore(in, out);
        remove(out);
    }
    
    /** Adds the given object to this HashChain. */
    public boolean add(E item) 
    {
        addLast(item);
        return true;
    }

    /** 
     * Returns an unbacked list containing the contents of the given Chain. 
     * @deprecated you can use <code>new ArrayList<E>(c)</code> instead
     */
    @Deprecated
    public static <E> List<E> toList(Chain<E> c)
    {
        return new ArrayList<E>(c);
    }

    /** Constructs an empty HashChain. */
    public HashChain()
    { 
        firstItem = lastItem = null;
    }

    /** Constructs a HashChain filled with the contents of the src Chain. */
    public HashChain(Chain<E> src) {
      this();
      addAll(src);
    }

    public boolean follows(E someObject, E someReferenceObject)
    {
        Iterator<E> it = iterator(someObject);
        while(it.hasNext()) {
            if(it.next() == someReferenceObject)
                return false;
        }
        return true;
    }
    
    public boolean contains(Object o)
    {
        return map.containsKey(o);
    }

    public boolean containsAll(Collection<?> c)
    {
        Iterator<?> it = c.iterator();
        while (it.hasNext())
            if (!(map.containsKey(it.next())))
                return false;
        
        return true;
    }
    
    public void insertAfter(E toInsert, E point)
    {
        if (toInsert == null)
            throw new RuntimeException("Bad idea! You tried to insert "
                                       + " a null object into a Chain!");

        if(map.containsKey(toInsert))
            throw new RuntimeException("Chain already contains object.");
        
        Link<E> temp = map.get(point);
        if (temp==null) {
        	throw new RuntimeException("Insertion point not found in chain!");
        }
        
        stateCount++;
        
        Link<E> newLink = temp.insertAfter(toInsert);
        map.put(toInsert, newLink);    
    }


    public void insertAfter(Collection<? extends E> toInsert, E point) {
        // if the list is null, treat it as an empty list
        if (toInsert == null)
            throw new RuntimeException("Warning! You tried to insert "
                                       + "a null list into a Chain!");            

        E previousPoint = point;
        for (E o : toInsert) {
            insertAfter(o, previousPoint);
            previousPoint = o;
        }
    }

    public void insertAfter(List<E> toInsert, E point)
    {
    	insertAfter((Collection<? extends E>) toInsert, point);
    }
    
    public void insertAfter(Chain<E> toInsert, E point)
    {
    	insertAfter((Collection<? extends E>) toInsert, point);
    }

    public void insertBefore(E toInsert, E point)
    {
        if (toInsert == null)
            throw new RuntimeException("Bad idea! You tried to insert "
                                       + "a null object into a Chain!");

        if(map.containsKey(toInsert))
            throw new RuntimeException("Chain already contains object.");
        
        Link<E> temp = map.get(point);
        if (temp==null) {
        	throw new RuntimeException("Insertion point not found in chain!");
        }
        stateCount++;
        
        Link<E> newLink = temp.insertBefore(toInsert);
        map.put(toInsert, newLink);
    }
    
    public void insertBefore(Collection<? extends E> toInsert, E point)
    {
        // if the list is null, treat it as an empty list
        if (toInsert == null)
            throw new RuntimeException("Warning! You tried to insert "
                                       + "a null list into a Chain!");

        for (E o : toInsert) {
        	insertBefore(o, point);
        }
    }

    public void insertBefore(List<E> toInsert, E point)
    {
    	insertBefore((Collection<E>) toInsert, point);
    }
    
    public void insertBefore(Chain<E> toInsert, E point)
	{
    	insertBefore((Collection<E>) toInsert, point);
    }

    public static <T> HashChain<T> listToHashChain(List<T> list) {
        HashChain<T> c = new HashChain<T>();
        Iterator<T> it = list.iterator();
        while (it.hasNext()) 
            c.addLast(it.next());
        return c;
    }
  
    public boolean remove(Object item)
    { 
        if (item == null)
            throw new RuntimeException("Bad idea! You tried to remove "
                                       + " a null object from a Chain!");

        stateCount++;
        /*
         * 4th April 2005 Nomair A Naeem
         * map.get(obj) can return null
         * only return true if this is non null
         * else return false
         */
        if(map.get(item) != null){
        	Link<E> link = map.get(item);
        
        	link.unlinkSelf();
        	map.remove(item);
        	return true;
        }
        return false;
    }

    public void addFirst(E item)
    {
        if (item == null)
            throw new RuntimeException("Bad idea!  You tried to insert "
                                       + "a null object into a Chain!");

        stateCount++;
        Link<E> newLink, temp;

        if(map.containsKey(item))
            throw new RuntimeException("Chain already contains object.");

        if(firstItem != null) {
            temp = map.get(firstItem);
            newLink = temp.insertBefore(item);
        } else {
            newLink = new Link<E>(item);
            firstItem = lastItem = item;
        }
        map.put(item, newLink);
    }

    public void addLast(E item)
    {
        if (item == null)
            throw new RuntimeException("Bad idea! You tried to insert "
                                       + " a null object into a Chain!");

        stateCount++;
        Link<E> newLink, temp;
        if(map.containsKey(item))
            throw new RuntimeException("Chain already contains object: " 
                                       + item);
        
        if(lastItem != null) {
            temp = map.get(lastItem);
            newLink = temp.insertAfter(item);   
        } else {
            newLink = new Link<E>(item);
            firstItem = lastItem = item;
        }            
        map.put(item, newLink);
    }
    
    public void removeFirst()
    {
        stateCount++;
        Object item = firstItem;
        map.get(firstItem).unlinkSelf();
        map.remove(item);
    }

    public void removeLast()
    {
        stateCount++;
        Object item = lastItem;
        map.get(lastItem).unlinkSelf();
        map.remove(item);
    }

    public E getFirst()
    {         
        if(firstItem == null) 
            throw new NoSuchElementException();
        return firstItem; 
    }
    
    public E getLast()
    { 
        if(lastItem == null) 
            throw new NoSuchElementException();
        return lastItem; 
    }

    public E getSuccOf(E point)
        throws NoSuchElementException
    {
        Link<E> link = map.get(point);
        try {
            link = link.getNext();
        }
        catch (NullPointerException e) {
            throw new NoSuchElementException();
        }
        if(link == null) 
            return null;
        
        return link.getItem();
    } 
    
    public E getPredOf(E point)
        throws NoSuchElementException
    {
        Link<E> link = map.get(point);
        if(point == null)
            throw new RuntimeException("trying to hash null value.");

        try {
            link = link.getPrevious();
        }
        catch (NullPointerException e) {
            throw new NoSuchElementException();
        }
        
        if(link == null) 
            return null;
        else
            return link.getItem();
    } 
    
    public Iterator<E> snapshotIterator() 
    {    	
        return (new ArrayList<E>(this)).iterator();
    }
   
    public Iterator<E> snapshotIterator(E item)
    {
        List<E> l = new ArrayList<E>(map.size());
	
        Iterator<E> it = new LinkIterator<E>(item);
        while (it.hasNext())
            l.add( it.next());
	
        return l.iterator();
    }

    public Iterator<E> iterator()
    { 
    	return new LinkIterator<E>(firstItem); 
    }
    
    public Iterator<E> iterator(E item) 
    {
        return new LinkIterator<E>(item);
    }

    /** <p>Returns an iterator ranging from <code>head</code> to
     *  <code>tail</code>, inclusive.</p>

        <p>If <code>tail</code> is the element immediately preceding
        <code>head</code> in this <code>HashChain</code>, the returned
        iterator will iterate 0 times (a special case to allow the
        specification of an empty range of elements). Otherwise if
        <code>tail</code> is not one of the elements following
        <code>head</code>, the returned iterator will iterate past the
        end of the <code>HashChain</code>, provoking a
        {@link NoSuchElementException}.</p>

	@throws NoSuchElementException if <code>head</code> is not
	an element of the chain.
     */
    public Iterator<E> iterator(E head, E tail)
    {
		if (head != null && this.getPredOf(head) == tail) { 
		    // special case hack, so empty ranges iterate 0 times
		    return new LinkIterator<E>(null, null);
		}
		return new LinkIterator<E>(head, tail);
    }

    public int size(){ return map.size(); }               

    /** Returns a textual representation of the contents of this Chain. */
    public String toString() 
    {
    	StringBuilder strBuf = new StringBuilder();
    	
        Iterator<E> it = iterator();
        boolean b = false;

        strBuf.append("[");
        while(it.hasNext()) {
            if (!b) b = true; else strBuf.append(", ");
            strBuf.append(it.next().toString());
        }
        strBuf.append("]");
        return strBuf.toString();
    }
    

    @SuppressWarnings("serial")
	class Link<X extends E> implements Serializable {
        private Link<X> nextLink;
        private Link<X> previousLink;
        private X item;
        public Link(X item)
        {
            this.item = item;
            nextLink = previousLink = null;
        }

        public Link<X> getNext() { return nextLink;}
        public Link<X> getPrevious() { return previousLink;}

        public void setNext(Link<X> link) { this.nextLink = link;}
        public void setPrevious(Link<X> link) { this.previousLink = link;}

        
        public void unlinkSelf() 
        {
            bind(previousLink, nextLink);            
        }

        public Link<X> insertAfter(X item)
        {
            Link<X> newLink = new Link<X>(item);
            
            bind(newLink, nextLink);
            bind(this, newLink);
            return newLink;
        }
        
        public Link<X> insertBefore(X item)
        {
            Link<X> newLink = new Link<X>(item);
            
            bind(previousLink, newLink);
            bind(newLink, this);
            return newLink;
        }
        

        private void bind(Link<X> a, Link<X> b) 
        {
            if (a == null) {
                firstItem = (b == null) ? null : b.item;
            } else 
                a.nextLink = b;
          
            if (b == null) {
            	lastItem = (a == null) ? null : a.item;
            }
            else
                b.previousLink = a;
        }

        public X getItem() { return item; }


        public String toString() 
        {
            if(item != null)
                return item.toString();
            else
                return  "Link item is null" + super.toString();

        }
    
    }

    class LinkIterator<X extends E> implements Iterator<X> 
    {
        private Link<X> currentLink;
        boolean state;    // only when this is true can remove() be called 
        // (in accordance w/ iterator semantics)
            
        private X destination;
        private long iteratorStateCount;


        public LinkIterator(X item) 
        {
	    Link nextLink = map.get(item);
	    if (nextLink == null && item != null) 
		throw new NoSuchElementException("HashChain.LinkIterator(obj) with obj that is not in the chain: " + item.toString() );
            currentLink = new Link(null);
            currentLink.setNext(nextLink);
            state = false;
            destination = null;
            iteratorStateCount = stateCount;
        }

        public LinkIterator(X from, X to)
        {
            this(from);
            destination = to;
        }

            
        public boolean hasNext() 
        {
            if(stateCount != iteratorStateCount) {
                throw new ConcurrentModificationException();
            }

	    if(destination == null)
		return (currentLink.getNext() != null);
	    else
		// Ignore whether (currentLink.getNext() == null), so
		// next() will produce a NoSuchElementException if
		// destination is not in the chain.
		return (destination != currentLink.getItem());
        }
            
        public X next()
            throws NoSuchElementException
        {
            if(stateCount != iteratorStateCount)
                throw new ConcurrentModificationException();
                        
            Link<X> temp = currentLink.getNext();
            if(temp == null) {
		String exceptionMsg;
		if(destination != null && destination != currentLink.getItem())
		    exceptionMsg = "HashChain.LinkIterator.next() reached end of chain without reaching specified tail unit";
	        else 
		    exceptionMsg = "HashChain.LinkIterator.next() called past the end of the Chain";
                throw new NoSuchElementException(exceptionMsg);
	    }
            currentLink = temp;

            state = true;
            return currentLink.getItem();
        }

        public void remove()
            throws IllegalStateException
        {
            if(stateCount != iteratorStateCount)
                throw new ConcurrentModificationException();
            
            stateCount++; iteratorStateCount++;
            if(!state)
                throw new IllegalStateException();
            else {
                currentLink.unlinkSelf();
                map.remove(currentLink.getItem());
                state = false;
            }

        }
   
        public String toString() 
        {
            if(currentLink == null) 
                return  "Current object under iterator is null" 
                    + super.toString();
            else
                return currentLink.toString();
        }
    }    
}

