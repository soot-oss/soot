package soot.jimple.interproc.ifds;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FixedUniverse<E> {
    
    protected Map<E,Integer> elemToIndex;
    protected Map<Integer,E> indexToElem;

    public FixedUniverse(Collection<E> universe) {
        elemToIndex = new HashMap<E, Integer>();
        indexToElem = new HashMap<Integer,E>();
        int i = 1; //start to index by 1 because 0 has a special meaning in the RHS tabulation algorithm
        for (E e : universe) {
            elemToIndex.put(e, i);
            indexToElem.put(i,e);
            i++;
        }
        elemToIndex = Collections.unmodifiableMap(elemToIndex);
        indexToElem = Collections.unmodifiableMap(indexToElem);
    }
    
    public int indexOf(E element) {
    	if(element==null) return 0;
    	return elemToIndex.get(element);
    }
    
    public E elementAt(int index) {
    	if(index==0) return null;
    	return indexToElem.get(index);
    }
   
}
