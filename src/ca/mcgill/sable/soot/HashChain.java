package ca.mcgill.sable.soot;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Soot, a Java(TM) classfile optimization framework.                *
 * Copyright (C) 1999 Patrice Pominville (patrice@sable.mcgill.ca)   *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on June 2 1999, by Patrice Pominville (patrice@sable.mcgill.ca) (*)
   First release.
*/





import java.util.*;
import ca.mcgill.sable.soot.*;



public class HashChain extends AbstractCollection
    implements Chain 
{
    
    private HashMap map = new HashMap(); 
    private Object firstItem;
    private Object lastItem;
  

    // temporary conversion methods
    public static HashChain listToHashChain(List l)
    {
	Iterator it = l.iterator();
	HashChain chain = new HashChain();
	HashMap bindings = new HashMap();

	while(it.hasNext()) {
	    Unit tempp = (Unit) it.next();
	    System.out.println(tempp.toString());
	    Unit copy = (Unit) tempp.clone();
	    bindings.put(tempp,copy);
	    chain.addLast(copy);
	}

	return chain;
    }

    public static List hashChainToList(HashChain c)
    {
	Iterator it = c.iterator();
	List list = new ArrayList();
	
	while(it.hasNext()) {
	    list.add(it.next());
	}
	
	return list;
    }



    public HashChain()
    { 
	firstItem = lastItem = null;
    }
   
    public void insertAfter(Object toInsert, Object point)
    {
	Link temp = (Link) map.get(point);
	
	Link newLink = temp.insertAfter(toInsert);
	map.put(toInsert, newLink);    
    }
    

    public void insertBefore(Object toInsert, Object point)
    {
	Link temp = (Link) map.get(point);
	
	Link newLink = temp.insertBefore(toInsert);
	map.put(toInsert, newLink);
    }
  
    public boolean remove(Object item)
    { 
	Link link = (Link) map.get(item);
	
	link.unlinkSelf();
	map.remove(link);
	return true;
    }

    public void addFirst(Object item)
    {
	Link newLink, temp;

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
	Link newLink, temp;

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
	((Link) map.get(firstItem)).unlinkSelf();
    }

    public void removeLast()
    {
	//	Object temp = getPredOf(
	((Link) map.get(lastItem)).unlinkSelf();
	//lastItem = temp;
    }

    public Object getFirst(){ return firstItem; }
    public Object getLast(){ return lastItem; }

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
	
	return link.getItem();
    } 
    
    public Iterator iterator(){ return new LinkIterator(); }
    
    public Iterator iterator(Object item) 
    {
	return new LinkIterator(item);
    }

    public int size(){ return map.size(); }       	


    public String toString() 
    {
	StringBuffer strBuf = new StringBuffer();
	Iterator it = iterator();

	
	while(it.hasNext()) {
	    strBuf.append(it.next().toString() + "\n" );
	}
	
	return strBuf.toString();
    }
    

    class Link {
	private Link nextLink;
	private Link previousLink;
	private Object item;
	
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
	    } else 
		a.setNext(b);
	  
	    if(b == null){
		if(a != null)
		    lastItem = a.getItem();
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
	    
	public LinkIterator() 
	{
	    currentLink = new Link(null);
	    currentLink.setNext((Link) map.get(firstItem));
	    state = false;
	}

	public LinkIterator(Object item) 
	{
	    currentLink = new Link(null);
	    currentLink.setNext((Link) map.get(item));
	    state = false;
	}

	    
	public boolean hasNext() 
	{
	    if(currentLink.getNext() == null)
		return false;
	    else
		return true;
	}
	    
	public Object next()
	    throws NoSuchElementException
	{
	    Link temp = currentLink.getNext();
	    if(temp == null)
		throw new NoSuchElementException();
	    
	    currentLink = temp;
	    state = true;
	    return currentLink.getItem();
	}

	public void remove()
	    throws IllegalStateException
	{
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
		return  "Current object under iterator is null" + super.toString();
	    else
		return currentLink.toString();
	}
    }
    
}

