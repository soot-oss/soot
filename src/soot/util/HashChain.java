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

/** Reference implementation of the Chain interface, 
    using a HashMap as the underlying structure. */
public class HashChain extends AbstractCollection
    implements Chain 
{
    private HashMap map = new HashMap(); 
    private Object firstItem;
    private Object lastItem;
    private long stateCount = 0;  

    /** Erases the contents of the current HashChain. */
    public void clear() 
    {
        stateCount++;
        firstItem = lastItem = null;
        map.clear();
    }

    public void swapWith(Object out, Object in)
    {
        insertBefore(in, out);
        remove(out);
    }
    
    /** Adds the given object to this HashChain. */
    public boolean add(Object item) 
    {
        addLast(item);
        return true;
    }

    /** Returns an unbacked list containing the contents of the given Chain. */
    public static List toList(Chain c)
    {
        Iterator it = c.iterator();
        List list = new ArrayList();
        
        while(it.hasNext()) {
            list.add(it.next());
        }
        
        return list;
    }

    /** Constructs an empty HashChain. */
    public HashChain()
    { 
        firstItem = lastItem = null;
    }

    public boolean follows(Object someObject, Object someReferenceObject)
    {
        Iterator it = iterator(someObject);
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

    public boolean containsAll(Collection c)
    {
        Iterator it = c.iterator();
        while (it.hasNext())
            if (!(map.containsKey(it.next())))
                return false;
        
        return true;
    }
    
    public void insertAfter(Object toInsert, Object point)
    {
        if (toInsert == null)
            throw new RuntimeException("Bad idea! You tried to insert "
                                       + " a null object into a Chain!");

        if(map.containsKey(toInsert))
            throw new RuntimeException("Chain already contains object.");
        stateCount++;
        Link temp = (Link) map.get(point);
        
        Link newLink = temp.insertAfter(toInsert);
        map.put(toInsert, newLink);    
    }

    public void insertAfter(List toInsert, Object point)
    {
        // if the list is null, treat it as an empty list
        if (toInsert == null)
            throw new RuntimeException("Warning! You tried to insert "
                                       + "a null list into a Chain!");            

        Object previousPoint = point;
        Iterator it = toInsert.iterator();
        while (it.hasNext())
            {
                Object o = it.next();
                insertAfter(o, previousPoint);
                previousPoint = o;
            }
    }
    
    public void insertAfter(Chain toInsert, Object point)
    {
        // if the list is null, treat it as an empty list
        if (toInsert == null)
            throw new RuntimeException("Warning! You tried to insert "
                                       + "a null list into a Chain!");            

        Object previousPoint = point;
        Iterator it = toInsert.iterator();
        while (it.hasNext())
            {
                Object o = it.next();
                insertAfter(o, previousPoint);
                previousPoint = o;
            }
    }

    public void insertBefore(Object toInsert, Object point)
    {
        if (toInsert == null)
            throw new RuntimeException("Bad idea! You tried to insert "
                                       + "a null object into a Chain!");

        if(map.containsKey(toInsert))
            throw new RuntimeException("Chain already contains object.");
        stateCount++;
        Link temp = (Link) map.get(point);
        
        Link newLink = temp.insertBefore(toInsert);
        map.put(toInsert, newLink);
    }
    
    public void insertBefore(List toInsert, Object point)
    {
        // if the list is null, treat it as an empty list
        if (toInsert == null)
            throw new RuntimeException("Warning! You tried to insert "
                                       + "a null list into a Chain!");

        Iterator it = toInsert.iterator();
        while (it.hasNext())
            {
                Object o = it.next();
                insertBefore(o, point);
            }
    }

    public void insertBefore(Chain toInsert, Object point)
    {
        // if the list is null, treat it as an empty list
        if (toInsert == null)
            throw new RuntimeException("Warning! You tried to insert "
                                       + "a null list into a Chain!");

        Iterator it = toInsert.iterator();
        while (it.hasNext())
            {
                Object o = it.next();
                insertBefore(o, point);
            }
    }

    public static HashChain listToHashChain(List list) {
        HashChain c = new HashChain();
        Iterator it = list.iterator();
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
        	Link link = (Link) map.get(item);
        
        	link.unlinkSelf();
        	map.remove(item);
        	return true;
        }
        return false;
    }

    public void addFirst(Object item)
    {
        if (item == null)
            throw new RuntimeException("Bad idea!  You tried to insert "
                                       + "a null object into a Chain!");

        stateCount++;
        Link newLink, temp;

        if(map.containsKey(item))
            throw new RuntimeException("Chain already contains object.");

        if(firstItem != null) {
            temp = (Link) map.get(firstItem);
            newLink = temp.insertBefore(item);
        } else {
            newLink = new Link(item);
            firstItem = lastItem = item;
        }
        map.put(item, newLink);
    }

    public void addLast(Object item)
    {
        if (item == null)
            throw new RuntimeException("Bad idea! You tried to insert "
                                       + " a null object into a Chain!");

        stateCount++;
        Link newLink, temp;
        if(map.containsKey(item))
            throw new RuntimeException("Chain already contains object: " 
                                       + item);
        
        if(lastItem != null) {
            temp = (Link) map.get(lastItem);
            newLink = temp.insertAfter(item);   
        } else {
            newLink = new Link(item);
            firstItem = lastItem = item;
        }            
        map.put(item, newLink);
    }
    
    public void removeFirst()
    {
        stateCount++;
        Object item = firstItem;
        ((Link) map.get(firstItem)).unlinkSelf();
        map.remove(item);
    }

    public void removeLast()
    {
        stateCount++;
        Object item = lastItem;
        ((Link) map.get(lastItem)).unlinkSelf();
        map.remove(item);
    }

    public Object getFirst()
    {         
        if(firstItem == null) 
            throw new NoSuchElementException();
        return firstItem; 
    }
    
    public Object getLast()
    { 
        if(lastItem == null) 
            throw new NoSuchElementException();
        return lastItem; 
    }

    public Object getSuccOf(Object point) 
        throws NoSuchElementException
    {
        Link link = (Link) map.get(point);
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
    
    public Object getPredOf(Object point)
        throws NoSuchElementException
    {
        Link link = (Link) map.get(point);
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
    
    public Iterator snapshotIterator() 
    {
	List l = new ArrayList( map.size()); 
	
	l.addAll(this);

        return l.iterator();
    }
   
    public Iterator snapshotIterator( Object item)
    {
        List l = new ArrayList( map.size());
	
        Iterator it = new LinkIterator( item);
        while (it.hasNext())
            l.add( it.next());
	
        return l.iterator();
    }

    public Iterator iterator(){ return new LinkIterator(firstItem); }
    public Iterator iterator(Object item) 
    {
        return new LinkIterator(item);
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
    public Iterator iterator(Object head, Object tail)
    {
	if (head != null && this.getPredOf(head) == tail) { 
	    // special case hack, so empty ranges iterate 0 times
	    return new LinkIterator(null, null);
	} else {
	    return new LinkIterator(head, tail);
	}
    }

    public int size(){ return map.size(); }               

    /** Returns a textual representation of the contents of this Chain. */
    public String toString() 
    {
        StringBuffer strBuf = new StringBuffer();
        Iterator it = iterator();
        boolean b = false;

        strBuf.append("[");
        while(it.hasNext()) {
            if (!b) b = true; else strBuf.append(", ");
            strBuf.append(it.next().toString());
        }
        strBuf.append("]");
        return strBuf.toString();
    }
    

    class Link implements Serializable {
        private Link nextLink;
        private Link previousLink;
        private Object item;
        private int index;
        
        public Link(Object item)
        {
            this.item = item;
            nextLink = previousLink = null;
        }

        public Link getNext() { return nextLink;}
        public Link getPrevious() { return previousLink;}

        public void setNext(Link link) { this.nextLink = link;}
        public void setPrevious(Link link) { this.previousLink = link;}

        
        public void unlinkSelf() 
        {
            bind(previousLink, nextLink);
            
        }

        public Link insertAfter(Object item)
        {
            Link newLink = new Link(item);
            
            bind(newLink, nextLink);
            bind(this, newLink);
            return newLink;
        }
        
        public Link insertBefore(Object item)
        {
            Link newLink = new Link(item);
            
            bind(previousLink, newLink);
            bind(newLink, this);
            return newLink;
        }
        

        private void bind(Link a, Link b) 
        {
            if(a == null) {
                if(b != null)
                    firstItem = b.getItem();
                else
                    firstItem = null;
            } else 
                a.setNext(b);
          
            if(b == null){
                if(a != null)
                    lastItem = a.getItem();
                else
                    lastItem = null;
            }
            else
                b.setPrevious(a);
        }

        public Object getItem() { return item; }


        public String toString() 
        {
            if(item != null)
                return item.toString();
            else
                return  "Link item is null" + super.toString();

        }
    
    }

    class LinkIterator implements Iterator 
    {
        private Link currentLink;
        boolean state;    // only when this is true can remove() be called 
        // (in accordance w/ iterator semantics)
            
        private Object destination;
        private long iteratorStateCount;


        public LinkIterator(Object item) 
        {
	    Link nextLink = (Link) map.get(item);
	    if (nextLink == null && item != null) 
		throw new NoSuchElementException("HashChain.LinkIterator(obj) with obj that is not in the chain: " + item.toString() );
            currentLink = new Link(null);
            currentLink.setNext(nextLink);
            state = false;
            destination = null;
            iteratorStateCount = stateCount;
        }

        public LinkIterator(Object from, Object to)
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
            
        public Object next()
            throws NoSuchElementException
        {
            if(stateCount != iteratorStateCount)
                throw new ConcurrentModificationException();
                        
            Link temp = currentLink.getNext();
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

