package soot.jimple.spark.sets;
import soot.util.BitVector;
import soot.jimple.spark.pag.Node;

/** An extension of a bit vector which is convenient to use to represent
 * points-to sets.  Used by SharedHybridSet.
 *
 * We have to extend soot.util.BitVector rather than java.util.BitSet
 * because PointsToSetInternal.getBitMask() returns a soot.util.BitVector.
 * which must be combined with other bit vectors.
 * 
 * @author Adam Richard
 *
 */
public class PointsToBitVector extends BitVector {
	public PointsToBitVector(int size) { super(size); }
	
	/**
	 * Adds n to this
	 * @return Whether this actually changed
	 */
	public boolean add(Node n) {
		int num = n.getNumber();
		if (!get(num))
		//if it's not already in this
		{
			set(num);
			return true;
		}
		else return false;
	}
	
	public boolean contains(Node n) {
		//Ripped from the HybridPointsToSet implementation
		//I'm assuming `number' in Node is the location of the node out of all
		//possible nodes.
		return get(n.getNumber());
	}
	
	/**
	 * Adds the Nodes in arr to this bitvector, adding at most size Nodes. 
	 * @return The number of new nodes actually added.
	 */ 
	/*
	public int add(Node[] arr, int size) {
		//assert size <= arr.length;
		int retVal = 0;
		for (int i = 0; i < size; ++i) {
			int num = arr[i].getNumber();
			if (!get(num))
			{
				set(num);
				++retVal;
			}
		}
		return retVal;
	}
*/
	
	/**Returns true iff other is a subset of this bitvector*/
	public boolean isSubsetOf(PointsToBitVector other) {
		//B is a subset of A iff the "and" of A and B gives A.
		BitVector andResult = BitVector.and(this, other);  //Don't want to modify either one
		return andResult.equals(this);
	}
	
	/**@return number of 1 bits in the bitset.
	 * Call this sparingly because it's probably expensive.*/
	
	/*Old algorithm:
	public int cardinality() {
		int retVal = 0;
		BitSetIterator it = iterator();
		while (it.hasNext()) {
			it.next();
			++retVal;
		}
		return retVal;
	}
	*/
	
	public Object clone()
	{
		PointsToBitVector retVal = (PointsToBitVector) super.clone();
		return retVal;
	}
	
	public PointsToBitVector(PointsToBitVector other)
	{
		super(other);
	/*	PointsToBitVector retVal = (PointsToBitVector)(other.clone());
		return retVal;*/
	}
	
	//Reference counting:
	private int refCount = 0;
	public void incRefCount()
	{
		++refCount;
		//An estimate of how much sharing is going on (but it should be 1 less
		//than the printed value in some cases, because incRefCount is called
		//for an intermediate result in nativeAddAll.
		//System.out.println("Reference count = " + refCount);
	}
	public void decRefCount()
	{
		--refCount;
	}
	public boolean unused()
	{
		return refCount == 0;
	}

}
