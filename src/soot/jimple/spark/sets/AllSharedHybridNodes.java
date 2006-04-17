package soot.jimple.spark.sets;

import java.util.LinkedList;

import soot.G;
import soot.Singletons;

/**A singleton to hold the hash table for SharedHybridSet*/

public class AllSharedHybridNodes {
    public AllSharedHybridNodes( Singletons.Global g ) {}
    public static AllSharedHybridNodes v() { return G.v().soot_jimple_spark_sets_AllSharedHybridNodes(); }

	public class BitVectorLookupMap {
		// Each element i is a list of BitVectors which have i 1s
		// (i elements in the set).
		// But should this be a LinkedList or some kind of Set?
		// I'll try LinkedList
		// TODO: Maybe implement my own linked list here
		// -it would need an add method and an iterator

		public LinkedList[] map = new LinkedList[1];

		private final static int INCREASE_FACTOR = 2;  //change to affect the
			//speed/memory tradeoff
		
		public void add(int size, PointsToBitVector toAdd) {
			if (map.length < size + 1)
			// if the `map' array isn't big enough
			{
				// TODO: The paper says it does some rearranging at this point
				LinkedList[] newMap = new LinkedList[size * INCREASE_FACTOR];
				System.arraycopy(map, 0, newMap, 0, map.length);
				map = newMap;
			}

			if (map[size] == null)
				map[size] = new LinkedList();
			map[size].add(toAdd);
		}
		
		public void remove(int size, PointsToBitVector toRemove)
		{
/*			if (map[size] == null)
			{
				//Can't happen
				System.out.println(toRemove.cardinality());
			}
*/
			map[size].remove(toRemove);
		}
	}


	public BitVectorLookupMap lookupMap = new BitVectorLookupMap(); // A hash table of all
	// the bit vectors for all points-to sets.
	// It can keep growing as more bit vectors are added to it, which
	// means it will have to occasionally double in size, which is expensive.


}
