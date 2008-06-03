package dk.brics.soot.flowsets;

import java.util.Arrays;
import java.util.List;

import soot.toolkits.scalar.AbstractFlowSet;
import soot.toolkits.scalar.FlowSet;
import soot.EquivTo;

/**
 * This class is the exact copy of soot.toolkits.scalar.ArraySparseSet with two exceptions.
 * <ol>
 * <li>The contains method has been modified to check whether the element being compared
 * implements the soot.EquivTo interface, and if so then use the soot.EquivTo.equivTo
 * method for comparison.<br/>
 * soot.Value extends soot.EquivTo, so it only makes sense to take advantage of that
 * instead of using the more naiive equals method inherited from Object (only comparing
 * references).</li>
 * <li>All fields and methods declared as private, changed to protected.</li>
 * </ol> 
 * @author Changes made by Árni Einarsson
 * 
 */public class ValueArraySparseSet extends AbstractFlowSet {
	protected static final int DEFAULT_SIZE = 8;
	protected int numElements;
	protected int maxElements;
	protected Object[] elements;

	public ValueArraySparseSet() {
		maxElements = DEFAULT_SIZE;
		elements = new Object[DEFAULT_SIZE];
		numElements = 0;
	}

	protected ValueArraySparseSet(ValueArraySparseSet other) {
		numElements = other.numElements;
		maxElements = other.maxElements;
		elements = (Object[]) other.elements.clone();
	}

	/** Returns true if flowSet is the same type of flow set as this. */
	protected boolean sameType(Object flowSet) {
		return (flowSet instanceof ValueArraySparseSet);
	}

	public Object clone() {
		return new ValueArraySparseSet(this);
	}

	public Object emptySet() {
		return new ValueArraySparseSet();
	}

	public void clear() {
		numElements = 0;
	}

	public int size() {
		return numElements;
	}

	public boolean isEmpty() {
		return numElements == 0;
	}

	/** Returns a unbacked list of elements in this set. */
	public List toList() {
		Object[] copiedElements = new Object[numElements];
		System.arraycopy(elements, 0, copiedElements, 0, numElements);
		return Arrays.asList(copiedElements);
	}

	/*
	 * Expand array only when necessary, pointed out by Florian Loitsch March
	 * 08, 2002
	 */
	public void add(Object e) {
		/* Expand only if necessary! and removes one if too:) */
		// Add element
		if (!contains(e)) {
			// Expand array if necessary
			if (numElements == maxElements)
				doubleCapacity();
			elements[numElements++] = (Object)e;
		}
	}

	protected void doubleCapacity() {
		int newSize = maxElements * 2;

		Object[] newElements = new Object[newSize];

		System.arraycopy(elements, 0, newElements, 0, numElements);
		elements = newElements;
		maxElements = newSize;
	}

	public void remove(Object obj) {
		int i = 0;
		while (i < this.numElements) {
			if (elements[i].equals(obj)) {
				elements[i] = elements[--numElements];
				return;
			} else
				i++;
		}
	}

	public void union(FlowSet otherFlow, FlowSet destFlow) {
		if (sameType(otherFlow) && sameType(destFlow)) {
			ValueArraySparseSet other = (ValueArraySparseSet) otherFlow;
			ValueArraySparseSet dest = (ValueArraySparseSet) destFlow;

			// For the special case that dest == other
			if (dest == other) {
				for (int i = 0; i < this.numElements; i++)
					dest.add(this.elements[i]);
			}

			// Else, force that dest starts with contents of this
			else {
				if (this != dest)
					copy(dest);

				for (int i = 0; i < other.numElements; i++)
					dest.add(other.elements[i]);
			}
		} else
			super.union(otherFlow, destFlow);
	}

	public void intersection(FlowSet otherFlow, FlowSet destFlow) {
		if (sameType(otherFlow) && sameType(destFlow)) {
			ValueArraySparseSet other = (ValueArraySparseSet) otherFlow;
			ValueArraySparseSet dest = (ValueArraySparseSet) destFlow;
			ValueArraySparseSet workingSet;

			if (dest == other || dest == this)
				workingSet = new ValueArraySparseSet();
			else {
				workingSet = dest;
				workingSet.clear();
			}

			for (int i = 0; i < this.numElements; i++) {
				if (other.contains(this.elements[i]))
					workingSet.add(this.elements[i]);
			}

			if (workingSet != dest)
				workingSet.copy(dest);
		} else
			super.intersection(otherFlow, destFlow);
	}

	public void difference(FlowSet otherFlow, FlowSet destFlow) {
		if (sameType(otherFlow) && sameType(destFlow)) {
			ValueArraySparseSet other = (ValueArraySparseSet) otherFlow;
			ValueArraySparseSet dest = (ValueArraySparseSet) destFlow;
			ValueArraySparseSet workingSet;

			if (dest == other || dest == this)
				workingSet = new ValueArraySparseSet();
			else {
				workingSet = dest;
				workingSet.clear();
			}

			for (int i = 0; i < this.numElements; i++) {
				if (!other.contains(this.elements[i]))
					workingSet.add(this.elements[i]);
			}

			if (workingSet != dest)
				workingSet.copy(dest);
		} else
			super.difference(otherFlow, destFlow);
	}

	public boolean contains(Object obj) {
		for (int i = 0; i < numElements; i++)
			if (elements[i] instanceof EquivTo
					&& ((EquivTo) elements[i]).equivTo(obj))
				return true;
			else if (elements[i].equals(obj))
				return true;

		return false;
	}

	public boolean equals(Object otherFlow) {
		if (sameType(otherFlow)) {
			ValueArraySparseSet other = (ValueArraySparseSet) otherFlow;

			if (other.numElements != this.numElements)
				return false;

			int size = this.numElements;

			// Make sure that thisFlow is contained in otherFlow
			for (int i = 0; i < size; i++)
				if (!other.contains(this.elements[i]))
					return false;

			/*
			 * both arrays have the same size, no element appears twice in one
			 * array, all elements of ThisFlow are in otherFlow -> they are
			 * equal! we don't need to test again! // Make sure that otherFlow
			 * is contained in ThisFlow for(int i = 0; i < size; i++)
			 * if(!this.contains(other.elements[i])) return false;
			 */

			return true;
		} else
			return super.equals(otherFlow);
	}

	public void copy(FlowSet destFlow) {
		if (sameType(destFlow)) {
			ValueArraySparseSet dest = (ValueArraySparseSet) destFlow;

			while (dest.maxElements < this.maxElements)
				dest.doubleCapacity();

			dest.numElements = this.numElements;

			System.arraycopy(this.elements, 0, dest.elements, 0,
					this.numElements);
		} else
			super.copy(destFlow);
	}
}
