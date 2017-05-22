package soot.jimple.spark.sets;

import soot.Type;
import soot.util.BitSetIterator;
import soot.util.BitVector;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;

import java.util.*;

/*
 * Possible sources of inefficiency:
 * -It seems like there must be a way to immediately know which subset bitvector
 * will exist, since sets are shared when a node with a smaller points-to set is
 * pointing to another node.  Why not just take that node's bitvector as a base?
 * -addAll could probably use many improvements.
 * -Cast masking - calling typeManager.get
 * -An interesting problem is that when merging a bitvector into an overflow list, if 
 * the one being merged
 * in has a bitvector, the mask or exclude might mask it down to a bitvector with very
 * few ones.  (In fact, this might even result in one with 0 ones!)
 * Should that new bitvector stay as a bitvector, or be converted to an
 * overflow list?  And how can we tell when it will have few ones?  (Modify BitVector?)
 * 
 */
/**
 * A shared representation of a points-to set which uses a bit vector + a list
 * of extra elements, an "overflow list", to make adding single elements fast in
 * most cases.
 * 
 * The bit vector may be shared by multiple points-to sets, while the overflow
 * list is specific to each points-to set.
 * 
 * To facilitate sharing of the bitvectors, there is a "hash table" of all
 * existing bitvectors kept, called BitVectorLookupMap, where the ith element
 * contains a list of all existing bitvectors of cardinality i (i.e. has i one
 * bits).
 * 
 * @author Adam Richard
 * 
 */

public class SharedHybridSet extends PointsToSetInternal {

	public SharedHybridSet(Type type, PAG pag) {
		// I'm not sure what "type" is for, but this is the way the other set
		// representations
		// did it
		super(type);
		this.pag = pag;
		//System.out.println("Using new heintze set");
	}

	// The following 2 constants should be tweaked for efficiency
	public final static int OVERFLOW_SIZE = 16;

	/**
	 * The max number of elements allowed in the set before creating a new
	 * bitvector for it.
	 */
	public final static int OVERFLOW_THRESHOLD = 5;

	/**
	 * When the overflow list overflows, the maximum number of elements that may
	 * remain in the overflow list (the rest are moved into the base bit vector)
	 */

	public boolean contains(Node n) {
		// Which should be checked first, bitVector or overflow? (for
		// performance)
		// I think the bit vector, since it only takes O(1) to check many
		// elements

		// Check the bit vector
		if (bitVector != null && bitVector.contains(n))
			return true;

		// Check overflow
		if (overflow.contains(n))
			return true;

		return false;
	}

	public boolean isEmpty() {
		return numElements == 0;
	}

	/**
	 * @return an overflow list of all elements in a that aren't in b (b is
	 *         assumed to be a subset of a)
	 */
	private OverflowList remainder(PointsToBitVector a, PointsToBitVector b) {
		// Since a contains everything b contains, doing an XOR will give
		// everything
		// in a not in b
		PointsToBitVector xorResult = new PointsToBitVector(a);
		xorResult.xor(b);
		// xorResult must now contain <= 20 elements, assuming
		// OVERFLOW_THRESHOLD <= OVERFLOW_SIZE
		return new OverflowList(xorResult);
	}

	// Look for an existing bitvector in the lookupMap which is a subset of the
	// newBitVector (the bitVector to set as the new points-to set), with only a
	// few
	// elements missing. If we find one, make that set the new `bitVector', and
	// the leftovers the new `overflow'
	//szBitVector is the size of the ORIGINAL bit vector, NOT the size of newBitVector
	private void findAppropriateBitVector(PointsToBitVector newBitVector, PointsToBitVector otherBitVector, int otherSize, int szBitvector) {
		//First check "other" and "this"'s bitvector, to maximize sharing and
		//minimize searching for a new bitvector
		if (otherBitVector != null && 
				otherSize <= numElements &&
				otherSize + OVERFLOW_THRESHOLD >= numElements &&
				otherBitVector.isSubsetOf(newBitVector))
		{
			setNewBitVector(szBitvector, otherBitVector);
			overflow = remainder(newBitVector, otherBitVector);
		}
		else if (bitVector != null && 
				szBitvector <= numElements &&
				szBitvector + OVERFLOW_THRESHOLD >= numElements &&
				bitVector.isSubsetOf(newBitVector))
		{
			overflow = remainder(newBitVector, bitVector);
		}
		else
		{
			for (int overFlowSize = 0; overFlowSize < OVERFLOW_THRESHOLD; ++overFlowSize) 
			{
				int bitVectorCardinality = numElements - overFlowSize;
				if (bitVectorCardinality < 0) break;   //We might be trying to add a bitvector
					//with <OVERFLOW_THRESHOLD ones (in fact, there might be bitvectors with 0
					//ones).  This results from merging bitvectors and masking out certain values.
				if (bitVectorCardinality < AllSharedHybridNodes.v().lookupMap.map.length
						&& AllSharedHybridNodes.v().lookupMap.map[bitVectorCardinality] != null) 
				{
					List<PointsToBitVector> lst = AllSharedHybridNodes.v()
							.lookupMap.map[bitVectorCardinality];
					for (PointsToBitVector candidate : lst) {
						// for each existing bit vector with bitVectorCardinality
						// ones
						if (candidate.isSubsetOf(newBitVector)) {
							setNewBitVector(szBitvector, candidate);
							overflow = remainder(newBitVector, candidate);
							return;
						}
					}
				}
			}
			// Didn't find an appropriate bit vector to use as a base; add the new
			// bit vector to the map of all bit vectors and set it as the new base
			// bit vector
			setNewBitVector(szBitvector, newBitVector);
			overflow.removeAll();
			AllSharedHybridNodes.v().lookupMap.add(numElements, newBitVector);
		}
	}

	//Allows for reference counting and deleting the old bit vector if it
	//isn't being shared
	private void setNewBitVector(int size, PointsToBitVector newBitVector)
	{
		newBitVector.incRefCount();
		if (bitVector != null) 
		{
			bitVector.decRefCount();
		
			if (bitVector.unused())
			{
				//delete bitVector from lookupMap
				AllSharedHybridNodes.v().lookupMap.remove(size, bitVector);
			}
		}
		bitVector = newBitVector;
	}
	
	public boolean add(Node n) 
	{
		/*
		 * This algorithm is described in the paper "IBM Research Report: Fast
		 * Pointer Analysis" by Hirzel, Dincklage, Diwan, and Hind, pg. 11
		 */
		if (contains(n))
			return false;
		++numElements;

		if (!overflow.full()) {
			overflow.add(n);
		} else {

			// Put everything in the bitvector
			PointsToBitVector newBitVector;
			if (bitVector == null)
				newBitVector = new PointsToBitVector(pag.getAllocNodeNumberer()
						.size());
			else
				newBitVector = new PointsToBitVector(bitVector);
			newBitVector.add(n); // add n to it
			add(newBitVector, overflow);
			
			// Now everything is in newBitVector, and it must have numElements
			// ones

			// The algorithm would still work without this step, but wouldn't be
			// a
			// shared implmentation at all.
			findAppropriateBitVector(newBitVector, null, 0, numElements - overflow.size() - 1);

		}
		return true;
	}

	private boolean nativeAddAll(SharedHybridSet other, SharedHybridSet exclude) {
		/*
		 * If one of the shared hybrid sets has a bitvector but the other
		 * doesn't, set that bitvector as the base bitvector and add the stuff
		 * from the other overflow list. If they both have a bitvector, AND them
		 * together, then add it to the lookupMap. If neither of them has a
		 * bitvector, just combine the overflow lists.
		 */

        BitVector mask = getBitMask(other, pag);

		if (exclude != null)
		{
			if (exclude.overflow.size() > 0) 
			{
				// Make exclude only a bitvector, for simplicity
				PointsToBitVector newBitVector;
				if (exclude.bitVector == null) {
					newBitVector = new PointsToBitVector(pag.getAllocNodeNumberer()
							.size());
				} else {
					newBitVector = new PointsToBitVector(exclude.bitVector);
				}
				add(newBitVector, exclude.overflow);
				exclude = new SharedHybridSet(type, pag);
				exclude.bitVector = newBitVector;
			}

			//It's possible at this point that exclude could have been passed in non-null,
			//but with no elements.  Simplify the rest of the algorithm by setting it to null
			//in that case.
			else if (exclude.bitVector == null) exclude = null;
		}
		
		int originalSize = size(), 
		    originalOnes = originalSize - overflow.size(),
		    otherBitVectorSize = other.size() - other.overflow.size();

		// Decide on the base bitvector
		if (bitVector == null) {
			bitVector = other.bitVector;
			if (bitVector != null) { // Maybe both bitvectors were null; in
				                     // that case, no need to do this
				bitVector.incRefCount();

				// Since merging in new bits might add elements that
				// were
				// already in the overflow list, we have to remove and re-add
				// them all.
				// TODO: Can this be avoided somehow?
				// Maybe by allowing an element to be both in the overflow set
				// and
				// the bitvector?
				// Or could it be better done by checking just the bitvector and
				// removing elements that are there?
				OverflowList toReAdd = overflow;
				overflow = new OverflowList();
				
				boolean newBitVectorCreated = false;  //whether a new bit vector
					//was created, which is used to decide whether to re-add the
					//overflow list as an overflow list again or merge it into the
					//new bit vector.
				
				numElements = otherBitVectorSize;
				if (exclude != null || mask != null) 
				{
					PointsToBitVector result = new PointsToBitVector(bitVector);
					if (exclude != null) result.andNot(exclude.bitVector);
					if (mask != null) result.and(mask);
					if (!result.equals(bitVector))
					{
						add(result, toReAdd);
						int newBitVectorSize = result.cardinality(); 
						numElements = newBitVectorSize;
						findAppropriateBitVector(result, other.bitVector, otherBitVectorSize, otherBitVectorSize);
						newBitVectorCreated = true;
					}
				}

				if (!newBitVectorCreated)  //if it was, then toReAdd has
					//already been re-added
				{
					for (OverflowList.ListNode i = toReAdd.overflow; i != null; i = i.next) {
						add(i.elem);
					}					
				}
			}
		} else if (other.bitVector != null) {
			// Now both bitvectors are non-null; merge them
			PointsToBitVector newBitVector = new PointsToBitVector(other.bitVector);
			if (exclude != null)
				newBitVector.andNot(exclude.bitVector);
			if (mask != null) newBitVector.and(mask);
			
			newBitVector.or(bitVector);

			if (!newBitVector.equals(bitVector)) // if some elements were
													// actually added
			{

				//At this point newBitVector is bitVector + some new bits
				
				// Have to make a tough choice - is it better at this point to
				// put both overflow lists into this bitvector (which involves
				// recalculating bitVector.cardinality() again since there might 
				// have been overlap), or is it better to re-add both the
				// overflow lists to the set?
				// I suspect the former, so I'll do that.

				// Basically we now want to merge both overflow lists into this
				// new
				// bitvector (if it is indeed a new bitvector), then add that
				// resulting
				// huge bitvector to the lookupMap, unless a subset of it is
				// already there.

				if (other.overflow.size() != 0) {
					PointsToBitVector toAdd = 
						new PointsToBitVector(newBitVector.size());
					add(toAdd, other.overflow);
					if (mask != null) toAdd.and(mask);
					if (exclude != null) toAdd.andNot(exclude.bitVector);
					newBitVector.or(toAdd);
				}
				//At this point newBitVector is still bitVector + some new bits

				int numOnes = newBitVector.cardinality();  //# of bits in the 
					//new bitvector
				int numAdded = add(newBitVector, overflow);
				numElements += numOnes - originalOnes   //number of new bits
					+ numAdded - overflow.size();   //might be negative due to 
						//elements in overflow already being in the new bits

				if (size() > originalSize)
				{
					findAppropriateBitVector(newBitVector, other.bitVector, otherBitVectorSize, originalOnes);
					//checkSize();
					return true;
				}
				else 
				{
					//checkSize();
					return false;   //It might happen that the bitvector being merged in adds some bits
					//to the existing bitvector, but that those new bits are all elements that were already
					//in the overflow list.  In that case, the set might not change, and if not we return false.
					//We also leave the set the way it was by not calling findAppropriateBitvector,
					//which maximizes sharing and is fastest in the short term.  I'm not sure whether it
					//would be faster overall to keep the already calculated bitvector anyway.
				}
			}
		}
		// Add all the elements in the overflow list of other, unless they're in
		// exclude
		OverflowList overflow = other.overflow;
		for (OverflowList.ListNode i = overflow.overflow; i != null; i = i.next) {
//		for (int i = 0; i < overflow.size(); ++i) {
			Node nodeToMaybeAdd = i.elem;
			if ((exclude == null) || !exclude.contains(nodeToMaybeAdd)) {
				if (mask == null || mask.get(nodeToMaybeAdd.getNumber()))
				{
					add(nodeToMaybeAdd);
				}
			}
		}

		//checkSize();
		return size() > originalSize;
	}

	/**@
	 * Adds the Nodes in arr to this bitvector.
	 * @return The number of new nodes actually added.
	 */ 
	private int add(PointsToBitVector p, OverflowList arr) {
		//assert size <= arr.length;
		int retVal = 0;
		for (OverflowList.ListNode i = arr.overflow; i != null; i = i.next) {
			if (p.add(i.elem)) ++retVal;
/*			int num = arr[i].getNumber();
			if (!get(num))
			{
				set(num);
				++retVal;
			}*/
		}
		return retVal;
	}

	/*
	//A class invariant - numElements correctly holds the size
	//Only used for testing
	private void checkSize()
	{
		int realSize = overflow.size();
		if (bitVector != null) realSize += bitVector.cardinality();
		if (numElements != realSize)
		{
			throw new RuntimeException("Assertion failed.");
		}
	}
	*/
	
	public boolean addAll(PointsToSetInternal other,
			final PointsToSetInternal exclude) {
		// Look at the sort of craziness we have to do just because of a lack of
		// multimethods
		if (other == null)
			return false;
		if ((!(other instanceof SharedHybridSet))
				|| (exclude != null && !(exclude instanceof SharedHybridSet))) {
			return super.addAll(other, exclude);
		} else {
			return nativeAddAll((SharedHybridSet) other,
					(SharedHybridSet) exclude);
		}
	}

	public boolean forall(P2SetVisitor v) {
		// Iterate through the bit vector. Ripped from BitPointsToSet again.
		// It seems there should be a way to share code between BitPointsToSet
		// and
		// SharedHybridSet, but I don't know how at the moment.
		if (bitVector != null) {
			for (BitSetIterator it = bitVector.iterator(); it.hasNext();) {
				v.visit((Node) pag.getAllocNodeNumberer().get(it.next()));
			}
		}
		// Iterate through the overflow list
		for (OverflowList.ListNode i = overflow.overflow; i != null; i = i.next) {
			v.visit(i.elem);
		}
		return v.getReturnValue();
	}

	// Ripped from the other points-to sets - returns a factory that can be
	// used to construct SharedHybridSets
	public final static P2SetFactory getFactory() {
		return new P2SetFactory() {
			public final PointsToSetInternal newSet(Type type, PAG pag) {
				return new SharedHybridSet(type, pag);
			}
		};
	}

	private PointsToBitVector bitVector = null; // Shared with other points-to
												// sets

	private OverflowList overflow = new OverflowList();

	private PAG pag; // I think this is needed to get the size of the bit
						// vector and the mask for casting

	private int numElements = 0; // # of elements in the set

	public int size() {
		return numElements;
	}

	private class OverflowList {
		public class ListNode {
			public Node elem;
			public ListNode next;
			public ListNode(Node elem, ListNode next)
			{
				this.elem = elem;
				this.next = next;
			}
		}
		public OverflowList() {
		}

		public OverflowList(PointsToBitVector bv) {
			BitSetIterator it = bv.iterator(); // Iterates over only the 1 bits
			while (it.hasNext()) {
				// Get the next node in the bitset by looking it up in the
				// pointer assignment graph.
				// Ripped from BitPointsToSet.
				Node n = (Node) (pag.getAllocNodeNumberer().get(it.next()));
				add(n);
			}

		}

		public void add(Node n) {
			if (full())
				throw new RuntimeException(
						"Can't add an element to a full overflow list.");
			overflow = new ListNode(n, overflow);
			++overflowElements;
		}

		public int size() {
			return overflowElements;
		}

		public boolean full() {
			return overflowElements == OVERFLOW_SIZE;
		}

		public boolean contains(Node n) {
			for (ListNode l = overflow; l != null; l = l.next) {
				if (n == l.elem)
					return true;
			}
			return false;
		}

		public void removeAll() {
			overflow = null;
			overflowElements = 0;
		}

		/*
		public ListNode next() {
			return overflow.next;
		}
		public Node elem() {
			return overflow.elem;
		}
		*/
		public ListNode overflow = null;  //Not shared with
			//other points-to sets - the extra elements besides the ones in bitVector
		private int overflowElements = 0; // # of elements actually in the
		// array `overflow'

	}

}
