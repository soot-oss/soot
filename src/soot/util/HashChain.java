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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Reference implementation of the Chain interface, using a HashMap as the
 * underlying structure.
 */
public class HashChain<E> extends AbstractCollection<E> implements Chain<E> {
	private static class Entry<X> extends WeakReference<X> {
		private Entry<X> prev;
		private Entry<X> next;
		
		private Entry(Entry<X> prev, X item, Entry<X> next) {
			super(requireNonNull(item));			
			this.next = next;
			this.prev = prev;
			next.prev = prev.next = this;
		}
		
		private Entry() {
			super(null);
			next = prev = this;
		}

		@Override
		public void clear() {
			super.clear();
			prev.next = next;
			next.prev = prev;
			prev = next = null;
		}

		public String toString() {
			return String.valueOf(get());
		}
	}
	
	private static final long serialVersionUID = -1490174501247136465L;

	private static final ObjectStreamField[] serialPersistentFields = {
		new ObjectStreamField("entries", Object[].class)
	};
    
	private transient Map<E, Entry<E>> map;
	
	// <ring> 1 <-> 2 <-> 3 <-> ... <-> N-2 <-> N-1 <-> N <ring>
	private transient Entry<E> ring = new Entry<E>();
	
	/**
	 * The number of times this Chain has been structurally modified.
	 * 
	 * This field is used to make iterators on Collection-views of
	 * the Chain fail-fast.  (See ConcurrentModificationException).
	 */
	private transient long modCount = 0;


	/**
	 * Backport of Objects.requireNonNull
	 */
	private static <T> T requireNonNull(T obj) {
		if (obj == null)
			throw new NullPointerException();
		return obj;
	}
	
	/**
	 * Backport of Objects.requireNonNull
	 */
	private static <T> T requireNonNull(T obj, String message) {
		if (obj == null)
			throw new NullPointerException(message);
		return obj;
	}


	/**
	 * Returns an unbacked list containing the contents of the given Chain.
	 * 
	 * @deprecated you can use <code>new java.util.ArrayList<E>(c)</code> instead
	 */
	@Deprecated
	public static <E> List<E> toList(Chain<E> c) {
		return new ArrayList<E>(c);
	}

	/**
	 * @deprecated use <code>new soot.util.HashChain<T>(list)</code> instead
	 * @param list
	 * @return
	 */
	@Deprecated
	public static <T> HashChain<T> listToHashChain(List<T> list) {
		return new HashChain<T>(list);
	}		

	/** Constructs an empty HashChain. */
	public HashChain() {
		map = new HashMap<E, Entry<E>>();
	}	

	/**
	 * Constructs a HashChain filled with the contents of <pre>src</pre>.
	 * 
	 * All elements of the collection will be added in order. It will be handled
	 * if all elements would be added with addLast separately
	 * 
	 * @throws NullPointerException if <pre>src</pre> is null
	 **/
	public HashChain(Collection<? extends E> src) {
		map = new HashMap<E, Entry<E>>(src.size());
		addAll(src);
	}
	
	/** Erases the contents of the current HashChain. */
	@Override
	public void clear() {
		modCount++;
		
		// GC help
		for (Entry<E> link : map.values()) {
			link.clear();
		}
		assert ring.prev == ring;
		assert ring.next == ring;
		
		map.clear();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @param in the new element
	 * @param out the old element
	 * @throws NullPointerException if <tt>out</tt> is null or <tt>in</tt> is null
	 * @throws NoSuchElementException if <tt>out</tt> is not part of this chain
	 * @throws RuntimeException if <tt>in</tt> is already part of this chain
	 */
	@Override
	public void swapWith(E out, E in) {		
		insertBefore(in, out);
		remove(out);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws NullPointerException if <tt>someObject</tt> or <tt>someReferenceObject</tt> is null
	 * @throws NoSuchElementException if <tt>someObject</tt> or <tt>someReferenceObject</tt> are not part of this chain
	 */
	@Override
	public boolean follows(E someObject, E someReferenceObject) {		
		Entry<E> h = getEntry(someReferenceObject);
		Entry<E> t = getEntry(someObject);	
		E last = null;
		for (Iterator<E> it = newIterator(h.next, t); it.hasNext();) {
			last = it.next();
		}
		return someObject.equals(last);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return map.keySet().containsAll(c);
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @param toInsert the new element
	 * @param point the point after
	 * @throws NullPointerException if <tt>toInsert</tt> is null or <tt>point</tt> is null
	 * @throws NoSuchElementException if <tt>point</tt> is not part of this chain
	 * @throws RuntimeException if <tt>toInsert</tt> is already part of this chain
	 */
	@Override
	public void insertAfter(E toInsert, E point) {
		insertBefore(toInsert, getEntry(point).next);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @param toInsert a list of new elements
	 * @param point the point after
	 * @throws NullPointerException if <tt>toInsert</tt> (or an element of it) is null or <tt>point</tt> is null
	 * @throws NoSuchElementException if <tt>point</tt> is not part of this chain
	 * @throws RuntimeException if an element of <tt>toInsert</tt> is already part of this chain
	 */
	@Override
	public void insertAfter(List<E> toInsert, E point) {
		insertBefore(toInsert, getEntry(point).next);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @param toInsert a chain of new elements
	 * @param point the point after
	 * @throws NullPointerException if <tt>toInsert</tt> (or an element of it) is null or <tt>point</tt> is null
	 * @throws NoSuchElementException if <tt>point</tt> is not part of this chain
	 * @throws RuntimeException if an element of <tt>toInsert</tt> is already part of this chain
	 */
	@Override
	public void insertAfter(Chain<E> toInsert, E point) {
		insertBefore(toInsert, getEntry(point).next);
	}
	

	/**
	 * {@inheritDoc}
	 * 
	 * @param toInsert the new element
	 * @param point the point before
	 * @throws NullPointerException if <tt>toInsert</tt> is null or <tt>point</tt> is null
	 * @throws NoSuchElementException if <tt>point</tt> is not part of this chain
	 * @throws RuntimeException if <tt>toInsert</tt> is already part of this chain
	 */
	@Override
	public void insertBefore(E toInsert, E point) {
		insertBefore(toInsert, getEntry(point));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @param toInsert a list of new elements
	 * @param point the point before
	 * @throws NullPointerException if <tt>toInsert</tt> (or an element of it) is null or <tt>point</tt> is null
	 * @throws NoSuchElementException if <tt>point</tt> is not part of this chain
	 * @throws RuntimeException if an element of <tt>toInsert</tt> is already part of this chain
	 */
	@Override
	public void insertBefore(List<E> toInsert, E point) {
		insertBefore(toInsert, getEntry(point));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param toInsert a chain of new elements
	 * @param point the point before
	 * @throws NullPointerException if <tt>toInsert</tt> (or an element of it) is null or <tt>point</tt> is null
	 * @throws NoSuchElementException if <tt>point</tt> is not part of this chain
	 * @throws RuntimeException if an element of <tt>toInsert</tt> is already part of this chain
	 */
	@Override
	public void insertBefore(Chain<E> toInsert, E point) {
		insertBefore(toInsert, getEntry(point));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws NullPointerException if item is null
	 * @throws RuntimeException if item is already part of this chain
	 * @param item
	 */
	@Override
	public void addFirst(E item) {
		insertBefore(item, ring.next);
	}

	/**
	 * Adds the given object at the end of the Chain.
	 * 
	 * @param item the entry to insert
	 * @throws NullPointerException if item is null
	 * @throws RuntimeException if item is already part of this chain
	 */
	@Override
	public void addLast(E item) {
		insertBefore(item, ring);
	}
	
	/**
	 * Adds the given object at the end of the Chain.
	 * 
	 * @param item the entry to insert
	 * @throws NullPointerException if item is null
	 * @throws RuntimeException if item is already part of this chain
	 */
	@Override
	public boolean add(E item) {
		addLast(item);
		return true;
	}
	

	@Override
	public boolean addAll(Collection<? extends E> e) {
		insertBefore(e, ring);
		return true;
	}
	
	/**
	 * @param toInsert
	 * @param link
	 * @throws RuntimeException if the map already contains the linked item
	 */
	private void insertBefore(E toInsert, Entry<E> point) {		
		requireNonNull(toInsert, "Warning! You tried to insert "
				+ "a null entry into a Chain!");
		
		Entry<E> e = new Entry<E>(point.prev, toInsert, point);		
		Entry<E> o = map.put(toInsert, e);		
		if (o == null) {
			modCount++;
			return;
		} 
		
		// rollback last put
		e.clear();
		map.put(toInsert, o);			
		throw new RuntimeException("Chain already contains object: "+toInsert);		
	}	
	
	private void insertBefore(Collection<? extends E> toInsert, Entry<E> point) {
		// if the list is null, treat it as an empty list
		requireNonNull(toInsert, "Warning! You tried to insert "
				+ "a null list into a Chain!");
		
		for (E o : toInsert) {
			insertBefore(o, point);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @throws NoSuchElementException - if this chain is empty
	 */
	@Override
	public void removeFirst() {		
		remove(getFirst());
	}

	/**
	 * {@inheritDoc}
	 * @throws NoSuchElementException - if this chain is empty
	 */
	@Override
	public void removeLast() {
		remove(getLast());
	}

	/**
	 * {@inheritDoc}
	 * @throws NullPointerException if item is null
	 */
	public boolean remove(Object item) {
		requireNonNull(item);
		Entry<E> old = map.remove(item);
		if ( old != null ) {		
			modCount++;
			old.clear();
			return true;
		}
		return false;
	}

	
	/**
	 * {@inheritDoc}
	 * @throws NoSuchElementException if this chain is empty
	 * @return
	 */
	@Override
	public E getFirst() {
		Entry<E> link = ring.next;
		if (link == ring)
			throw new NoSuchElementException();
		return link.get();
	}
	
	/**
	 * {@inheritDoc}
	 * @throws NoSuchElementException if this chain is empty
	 * @return the last entry of this chain
	 */
	@Override
	public E getLast() {
		Entry<E> link = ring.prev;
		if (link == ring)
			throw new NoSuchElementException();
		return link.get();
	}

	/**
	 * {@inheritDoc}
	 * @throws NullPointerException if point is null
	 * @throws NoSuchElementException if point is not part of this chain
	 * @param point
	 * @return the immediately following entry or null if point is the last entry of the chain
	 */
	@Override
	public E getSuccOf(E point) throws NoSuchElementException {
		return getEntry(point).next.get();
	}
	
	/**
	 * {@inheritDoc}
	 * @throws NullPointerException if point is null
	 * @throws NoSuchElementException if point is not part of this chain
	 * @param point
	 * @return the immediately preceding entry or null if point is the first entry of the chain
	 */
	@Override
	public E getPredOf(E point) throws NoSuchElementException {
		return getEntry(point).prev.get();
	}

	/**
	 * @throws NullPointerException if point is null
	 * @throws NoSuchElementException if point is not part of this chain
	 * @param point
	 * @return the entry associated with the point. the result will never be null.
	 */
	private Entry<E> getEntry(Object point) {
		requireNonNull(point);
		Entry<E> link = map.get(point);
		if (link == null)
			throw new NoSuchElementException();
		return link;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<E> snapshotIterator() {
		return (new ArrayList<E>(this)).iterator();
	}
	
	/**
	 * 
	 * @param head the first entry in the iteration
	 * @return an iterator that start at <tt>head</tt> and ends at the last entry of this chain
	 * @see #getLast()
	 * @throws NullPointerException
	 *             if <tt>head</tt> is null.
	 * @throws NoSuchElementException
	 *             if <tt>head</tt> is not an element
	 *             of this chain.
	 */
	public Iterator<E> snapshotIterator(E head) {
		List<E> l = new ArrayList<E>(map.size());

		Iterator<E> it = iterator(head);
		while (it.hasNext())
			l.add(it.next());

		return l.iterator();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<E> iterator() {
		return newIterator(ring.next, ring.prev);
	}

	/**
	 * @param head the first entry in the iteration
	 * @return an iterator that starts at <tt>head</tt> and ends at the 
	 * last entry of this chain
	 * @see #getLast()
	 * @throws NullPointerException
	 *             if <tt>head</tt> is null.
	 * @throws NoSuchElementException
	 *             if <tt>head</tt> is not an element
	 *             of this chain.
	 */
	@Override
	public Iterator<E> iterator(E head) {
		return newIterator(getEntry(head), ring.prev);
	}

	/** 
	 * Returns an iterator ranging from <tt>head</tt> to <tt>tail</tt>,
	 * inclusive.
	 * 
	 * If <tt>tail</tt> is the element immediately preceding
	 * <tt>head</tt> in this <tt>Chain</tt>, the returned iterator
	 * will iterate 0 times (a special case to allow the specification of an
	 * empty range of elements). Otherwise if <tt>tail</tt> is not one of
	 * the elements following <tt>head</tt>, the returned iterator will
	 * stop at the end of the <tt>Chain</tt>.
	 * 
	 * @throws NullPointerException
	 *             if <tt>head</tt> is null.
	 *             if <tt>tail</tt> is null and <tt>head</tt> is not the 
	 *             first entry in this <tt>Chain</tt>.
	 * @throws NoSuchElementException
	 *             if <tt>head</tt> or <tt>tail</tt> is not an element
	 *             of the chain.
	 */
	@Override
	public Iterator<E> iterator(E head, E tail) {
		Entry<E> headEntry = getEntry(head);		
		if (headEntry.prev.get() == tail) {
			// special case hack, so empty ranges iterate 0 times
			return Collections.<E>emptyList().iterator();
		}
		return newIterator(headEntry, getEntry(tail));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return map.size();
	}

	private Iterator<E> newIterator(final Entry<E> from, final Entry<E> to) {
		assert from != null;
		assert to != null;
		
		return new Iterator<E>() {
			private long expectedModCount = modCount;
			private Entry<E> current = null;
			private Entry<E> next = from;

			public boolean hasNext() {
				return next != ring;
			}

			public E next() throws NoSuchElementException {
				if (modCount != expectedModCount) {
					throw new ConcurrentModificationException();
				}
				if (next == ring) {
					throw new NoSuchElementException();
				}
				current = next;
				next = (current == to) ? ring : current.next;
				return current.get();
			}

			public void remove() throws IllegalStateException {
				if (current == null) {
					throw new IllegalStateException();
				}
				if (modCount != expectedModCount) {
					throw new ConcurrentModificationException();
				}
				HashChain.this.remove(current.get());
				expectedModCount = modCount;
				current = null;
			}
		};
	}

	
    /**
     * Save the state of the {@code HashChain} instance to a stream (i.e.,
     * serialize it).
     */
    private void writeObject(ObjectOutputStream s)
        throws IOException {       	
        ObjectOutputStream.PutField fields = s.putFields();
        s.writeObject(toArray());
        fields.put("entries", toArray());
        s.writeFields();
    }

    /**
     * Reconstitute the {@code HashChain} instance from a stream (i.e.,
     * deserialize it).
     */
	private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException {

        ObjectInputStream.GetField fields = s.readFields();
        
        Object[] rawEntries = (Object[]) fields.get("entries", new Object[0]);
        
        @SuppressWarnings("unchecked")
        List<E> list = (List<E>)Arrays.asList(rawEntries);
        
        ring = new Entry<E>();
        map = new HashMap<E, Entry<E>>(rawEntries.length);
        addAll(list);
    }
}
