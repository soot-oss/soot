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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import soot.Unit;
import soot.UnitBox;
import soot.jimple.GotoStmt;
import soot.jimple.internal.JGotoStmt;

/**
 * Reference implementation of the Chain interface, using a HashMap as the
 * underlying structure.
 */
public class HashChain<E> extends AbstractCollection<E> implements Chain<E> {
	private final Map<E, Link<E>> map = new ConcurrentHashMap<E, Link<E>>();
	private E firstItem;
	private E lastItem;
	private long stateCount = 0;
	
	private final Iterator<E> emptyIterator = new Iterator<E>() {

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public E next() {
			return null;
		}
		
		@Override
		public void remove() {
			// do nothing
		}
		
	};

	/** Erases the contents of the current HashChain. */
	public void clear() {
		stateCount++;
		firstItem = lastItem = null;
		map.clear();
	}

	public void swapWith(E out, E in) {
		insertBefore(in, out);
		remove(out);
	}

	/** Adds the given object to this HashChain. */
	public boolean add(E item) {
		addLast(item);
		return true;
	}
	
	/**
	 * Gets all elements in the chain. There is no guarantee on sorting.
	 * @return All elements in the chain in an unsorted collection
	 */
	public Collection<E> getElementsUnsorted() {
		return map.keySet();
	}

	/**
	 * Returns an unbacked list containing the contents of the given Chain.
	 * 
	 * @deprecated you can use <code>new ArrayList<E>(c)</code> instead
	 */
	@Deprecated
	public static <E> List<E> toList(Chain<E> c) {
		return new ArrayList<E>(c);
	}

	/** Constructs an empty HashChain. */
	public HashChain() {
		firstItem = lastItem = null;
	}

	/** Constructs a HashChain filled with the contents of the src Chain. */
	public HashChain(Chain<E> src) {
		this();
		addAll(src);
	}

	public boolean follows(E someObject, E someReferenceObject) {
		Iterator<E> it = iterator(someObject);
		while (it.hasNext()) {
			if (it.next() == someReferenceObject)
				return false;
		}
		return true;
	}

	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	public boolean containsAll(Collection<?> c) {
		Iterator<?> it = c.iterator();
		while (it.hasNext())
			if (!(map.containsKey(it.next())))
				return false;

		return true;
	}

	public void insertAfter(E toInsert, E point) {
		if (toInsert == null)
			throw new RuntimeException("Bad idea! You tried to insert "
					+ " a null object into a Chain!");

		if (map.containsKey(toInsert))
			throw new RuntimeException("Chain already contains object.");

		Link<E> temp = map.get(point);
		if (temp == null) {
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

	public void insertAfter(List<E> toInsert, E point) {
		insertAfter((Collection<? extends E>) toInsert, point);
	}

	public void insertAfter(Chain<E> toInsert, E point) {
		insertAfter((Collection<? extends E>) toInsert, point);
	}

	public void insertBefore(E toInsert, E point) {
		if (toInsert == null)
			throw new RuntimeException("Bad idea! You tried to insert "
					+ "a null object into a Chain!");

		if (map.containsKey(toInsert))
			throw new RuntimeException("Chain already contains object.");

		Link<E> temp = map.get(point);
		if (temp == null) {
			throw new RuntimeException("Insertion point not found in chain!");
		}
		stateCount++;

		Link<E> newLink = temp.insertBefore(toInsert);
		map.put(toInsert, newLink);
	}

	public void insertBefore(Collection<? extends E> toInsert, E point) {
		// if the list is null, treat it as an empty list
		if (toInsert == null)
			throw new RuntimeException("Warning! You tried to insert "
					+ "a null list into a Chain!");

		for (E o : toInsert) {
			insertBefore(o, point);
		}
	}

	public void insertBefore(List<E> toInsert, E point) {
		insertBefore((Collection<E>) toInsert, point);
	}

	public void insertBefore(Chain<E> toInsert, E point) {
		insertBefore((Collection<E>) toInsert, point);
	}
	
	/**
	 * Inserts instrumentation in a manner such that the resulting control flow
	 * graph (CFG) of the program will contain <code>toInsert</code> on an edge
	 * that is defined by <code>point_source</code> and <code>point_target</code>.
	 * 
	 * @param toInsert  the instrumentation to be added in the Chain
	 * @param point_src the source point of an edge in CFG
	 * @param point_tgt the target point of an edge
	 */
	public void insertOnEdge(E toInsert, E point_src, E point_tgt) {
		
		List<E> o = new ArrayList<E>();
		o.add(toInsert);
		insertOnEdge(o, point_src, point_tgt);
		
	}

	/**
	 * Inserts instrumentation in a manner such that the resulting control flow
	 * graph (CFG) of the program will contain <code>toInsert</code> on an edge
	 * that is defined by <code>point_source</code> and <code>point_target</code>.
	 * 
	 * @param toInsert  instrumentation to be added in the Chain
	 * @param point_src the source point of an edge in CFG
	 * @param point_tgt the target point of an edge
	 */
	public void insertOnEdge(Collection<? extends E> toInsert, E point_src, E point_tgt) {

		if (toInsert == null)
			throw new RuntimeException("Bad idea! You tried to insert " + "a null object into a Chain!");

		// Insert 'toInsert' before 'target' point in chain if the source point is null
		if (point_src == null && point_tgt != null) {
			((Unit) point_tgt).redirectJumpsToThisTo((Unit) toInsert.toArray()[0]);
			insertBefore(toInsert, point_tgt);
			return;
		}

		// Insert 'toInsert' after 'source' point in chain if the target point is null
		if (point_src != null && point_tgt == null) {
			insertAfter(toInsert, point_src);
			return;
		}

		// Throw an exception if both source and target is null
		if (point_src == null && point_tgt == null) {
			throw new RuntimeException("insertOnEdge failed! Both source and target points are null.");
		}

		// If target is right after the source in the Chain
		// 1- Redirect all jumps (if any) from 'source' to 'target', to 'toInsert[0]'
		//    (source->target) ==>  (source->toInsert[0])
		// 2- Insert 'toInsert' after 'source' in Chain
		if (getSuccOf(point_src) == point_tgt) {
			List<UnitBox> boxes = ((Unit) point_src).getUnitBoxes();
			for (UnitBox box : boxes) {
				if (box.getUnit() == point_tgt) {
					box.setUnit((Unit) toInsert.toArray()[0]);
				}
			}
			insertAfter(toInsert, point_src);
			return;
		}
		
		
		// If the target is not right after the source in chain then,
		// 1- Redirect all jumps (if any) from 'source' to 'target', to 'toInsert[0]'
		//    (source->target) ==>  (source->toInsert[0])
		//    1.1- if there are no jumps from source to target, then such an edge does not exist. Throw an exception.
		// 2- Insert 'toInsert' before 'target' in Chain
		// 3- If required, add a 'goto target' statement so that no other edge executes 'toInsert'
		boolean validEdgeFound = false;
		E originalPred = getPredOf(point_tgt);
		
		List<UnitBox> boxes = ((Unit) point_src).getUnitBoxes();
		for (UnitBox box : boxes) {
			if (box.getUnit() == point_tgt) {

				if (point_src instanceof GotoStmt) {

					box.setUnit((Unit) toInsert.toArray()[0]);
					insertAfter(toInsert, point_src);

					E goto_unit = (E) new JGotoStmt((Unit) point_tgt);
					insertAfter(goto_unit, (E) toInsert.toArray()[toInsert.size() - 1]);
					return;
				}

				box.setUnit((Unit) toInsert.toArray()[0]);

				validEdgeFound = true;
			}
		}
		if (validEdgeFound) {
			insertBefore(toInsert, point_tgt);

			if (originalPred != point_src) {
				if (originalPred instanceof GotoStmt)
					return;

				E goto_unit = (E) new JGotoStmt((Unit) point_tgt);
				insertBefore(goto_unit, (E) toInsert.toArray()[0]);
			}
			return;
		}

		// In certain scenarios, the above code can add extra 'goto' units on a different edge
		// So, an edge [src --> tgt] becomes [src -> goto tgt -> tgt].
		// When this happens, the original edge [src -> tgt] ceases to exist.
		// The following code handles such scenarios. 
		if (getSuccOf(point_src) instanceof GotoStmt) {
			if (((Unit) getSuccOf(point_src)).getUnitBoxes().get(0).getUnit() == point_tgt) {

				((Unit) getSuccOf(point_src)).redirectJumpsToThisTo((Unit) toInsert.toArray()[0]);
				insertBefore(toInsert, getSuccOf(point_src));

				return;
			}
		}
		
		// If the control reaches this point, it means that an edge [stc -> tgt] as specified by user does not exist and is thus invalid
		// Return an exception.
		throw new RuntimeException(
				"insertOnEdge failed! No such edge found. The edge on which you want to insert an instrumentation is invalid.");
	}

	/**
	 * Inserts instrumentation in a manner such that the resulting control flow
	 * graph (CFG) of the program will contain <code>toInsert</code> on an edge
	 * that is defined by <code>point_source</code> and <code>point_target</code>.
	 * 
	 * @param toInsert  instrumentation to be added in the Chain
	 * @param point_src the source point of an edge in CFG
	 * @param point_tgt the target point of an edge
	 */
	public void insertOnEdge(List<E> toInsert, E point_src, E point_tgt) {
		insertOnEdge((Collection<E>) toInsert, point_src, point_tgt);
	}

	/**
	 * Inserts instrumentation in a manner such that the resulting control flow
	 * graph (CFG) of the program will contain <code>toInsert</code> on an edge
	 * that is defined by <code>point_source</code> and <code>point_target</code>.
	 * 
	 * @param toInsert  instrumentation to be added in the Chain
	 * @param point_src the source point of an edge in CFG
	 * @param point_tgt the target point of an edge
	 */
	public void insertOnEdge(Chain<E> toInsert, E point_src, E point_tgt) {
		insertOnEdge((Collection<E>) toInsert, point_src, point_tgt);
	}

	public static <T> HashChain<T> listToHashChain(List<T> list) {
		HashChain<T> c = new HashChain<T>();
		Iterator<T> it = list.iterator();
		while (it.hasNext())
			c.addLast(it.next());
		return c;
	}

	public boolean remove(Object item) {
		if (item == null)
			throw new RuntimeException("Bad idea! You tried to remove "
					+ " a null object from a Chain!");

		stateCount++;
		/*
		 * 4th April 2005 Nomair A Naeem map.get(obj) can return null only
		 * return true if this is non null else return false
		 */
		if (map.get(item) != null) {
			Link<E> link = map.get(item);

			link.unlinkSelf();
			map.remove(item);
			return true;
		}
		return false;
	}

	public void addFirst(E item) {
		if (item == null)
			throw new RuntimeException("Bad idea!  You tried to insert "
					+ "a null object into a Chain!");

		stateCount++;
		Link<E> newLink, temp;

		if (map.containsKey(item))
			throw new RuntimeException("Chain already contains object.");

		if (firstItem != null) {
			temp = map.get(firstItem);
			newLink = temp.insertBefore(item);
		} else {
			newLink = new Link<E>(item);
			firstItem = lastItem = item;
		}
		map.put(item, newLink);
	}

	public void addLast(E item) {
		if (item == null)
			throw new RuntimeException("Bad idea! You tried to insert "
					+ " a null object into a Chain!");

		stateCount++;
		Link<E> newLink, temp;
		if (map.containsKey(item))
			throw new RuntimeException("Chain already contains object: " + item);

		if (lastItem != null) {
			temp = map.get(lastItem);
			newLink = temp.insertAfter(item);
		} else {
			newLink = new Link<E>(item);
			firstItem = lastItem = item;
		}
		map.put(item, newLink);
	}

	public void removeFirst() {
		stateCount++;
		Object item = firstItem;
		map.get(firstItem).unlinkSelf();
		map.remove(item);
	}

	public void removeLast() {
		stateCount++;
		Object item = lastItem;
		map.get(lastItem).unlinkSelf();
		map.remove(item);
	}

	public E getFirst() {
		if (firstItem == null)
			throw new NoSuchElementException();
		return firstItem;
	}

	public E getLast() {
		if (lastItem == null)
			throw new NoSuchElementException();
		return lastItem;
	}

	public E getSuccOf(E point) throws NoSuchElementException {
		Link<E> link = map.get(point);
		try {
			link = link.getNext();
		} catch (NullPointerException e) {
			throw new NoSuchElementException();
		}
		if (link == null)
			return null;

		return link.getItem();
	}

	public E getPredOf(E point) throws NoSuchElementException {
		Link<E> link = map.get(point);
		if (point == null)
			throw new RuntimeException("trying to hash null value.");

		try {
			link = link.getPrevious();
		} catch (NullPointerException e) {
			throw new NoSuchElementException();
		}

		if (link == null)
			return null;
		else
			return link.getItem();
	}

	public Iterator<E> snapshotIterator() {
		return (new ArrayList<E>(this)).iterator();
	}

	public Iterator<E> snapshotIterator(E item) {
		List<E> l = new ArrayList<E>(map.size());

		Iterator<E> it = new LinkIterator<E>(item);
		while (it.hasNext())
			l.add(it.next());

		return l.iterator();
	}

	public Iterator<E> iterator() {
		if (firstItem == null || isEmpty())
			return emptyIterator;
		return new LinkIterator<E>(firstItem);
	}

	public Iterator<E> iterator(E item) {
		if (firstItem == null || isEmpty())
			return emptyIterator;
		return new LinkIterator<E>(item);
	}

	/**
	 * <p>
	 * Returns an iterator ranging from <code>head</code> to <code>tail</code>,
	 * inclusive.
	 * </p>
	 * 
	 * <p>
	 * If <code>tail</code> is the element immediately preceding
	 * <code>head</code> in this <code>HashChain</code>, the returned iterator
	 * will iterate 0 times (a special case to allow the specification of an
	 * empty range of elements). Otherwise if <code>tail</code> is not one of
	 * the elements following <code>head</code>, the returned iterator will
	 * iterate past the end of the <code>HashChain</code>, provoking a
	 * {@link NoSuchElementException}.
	 * </p>
	 * 
	 * @throws NoSuchElementException
	 *             if <code>head</code> is not an element of the chain.
	 */
	public Iterator<E> iterator(E head, E tail) {
		if (firstItem == null || isEmpty())
			return emptyIterator;
		if (head != null && this.getPredOf(head) == tail) {
			return emptyIterator;
		}
		return new LinkIterator<E>(head, tail);
	}

	public int size() {
		return map.size();
	}

	/** Returns a textual representation of the contents of this Chain. */
	public String toString() {
		StringBuilder strBuf = new StringBuilder();

		Iterator<E> it = iterator();
		boolean b = false;

		strBuf.append("[");
		while (it.hasNext()) {
			if (!b)
				b = true;
			else
				strBuf.append(", ");
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

		public Link(X item) {
			this.item = item;
			nextLink = previousLink = null;
		}

		public Link<X> getNext() {
			return nextLink;
		}

		public Link<X> getPrevious() {
			return previousLink;
		}

		public void setNext(Link<X> link) {
			this.nextLink = link;
		}

		public void setPrevious(Link<X> link) {
			this.previousLink = link;
		}

		public void unlinkSelf() {
			bind(previousLink, nextLink);
		}

		public Link<X> insertAfter(X item) {
			Link<X> newLink = new Link<X>(item);

			bind(newLink, nextLink);
			bind(this, newLink);
			return newLink;
		}

		public Link<X> insertBefore(X item) {
			Link<X> newLink = new Link<X>(item);

			bind(previousLink, newLink);
			bind(newLink, this);
			return newLink;
		}

		private void bind(Link<X> a, Link<X> b) {
			if (a == null) {
				firstItem = (b == null) ? null : b.item;
			} else
				a.nextLink = b;

			if (b == null) {
				lastItem = (a == null) ? null : a.item;
			} else
				b.previousLink = a;
		}

		public X getItem() {
			return item;
		}

		public String toString() {
			if (item != null)
				return item.toString();
			else
				return "Link item is null" + super.toString();

		}

	}

	class LinkIterator<X extends E> implements Iterator<E> {
		private Link<E> currentLink;
		boolean state; // only when this is true can remove() be called
		// (in accordance w/ iterator semantics)

		private X destination;
		private long iteratorStateCount;

		public LinkIterator(X item) {
			Link<E> nextLink = map.get(item);
			if (nextLink == null && item != null)
				throw new NoSuchElementException(
						"HashChain.LinkIterator(obj) with obj that is not in the chain: "
								+ item.toString());
			currentLink = new Link<E>(null);
			currentLink.setNext(nextLink);
			state = false;
			destination = null;
			iteratorStateCount = stateCount;
		}

		public LinkIterator(X from, X to) {
			this(from);
			destination = to;
		}

		public boolean hasNext() {
			if (stateCount != iteratorStateCount) {
				throw new ConcurrentModificationException();
			}

			if (destination == null)
				return (currentLink.getNext() != null);
			else
				// Ignore whether (currentLink.getNext() == null), so
				// next() will produce a NoSuchElementException if
				// destination is not in the chain.
				return (destination != currentLink.getItem());
		}

		public E next() throws NoSuchElementException {
			if (stateCount != iteratorStateCount)
				throw new ConcurrentModificationException();

			Link<E> temp = currentLink.getNext();
			if (temp == null) {
				String exceptionMsg;
				if (destination != null && destination != currentLink.getItem())
					exceptionMsg = "HashChain.LinkIterator.next() reached end of chain without reaching specified tail unit";
				else
					exceptionMsg = "HashChain.LinkIterator.next() called past the end of the Chain";
				throw new NoSuchElementException(exceptionMsg);
			}
			currentLink = temp;

			state = true;
			return currentLink.getItem();
		}

		public void remove() throws IllegalStateException {
			if (stateCount != iteratorStateCount)
				throw new ConcurrentModificationException();

			stateCount++;
			iteratorStateCount++;
			if (!state)
				throw new IllegalStateException();
			else {
				currentLink.unlinkSelf();
				map.remove(currentLink.getItem());
				state = false;
			}

		}

		public String toString() {
			if (currentLink == null)
				return "Current object under iterator is null"
						+ super.toString();
			else
				return currentLink.toString();
		}

	}

	/** Returns the number of times this chain has been modified. */
	public long getModificationCount() {
		return stateCount;
	}
}
