

package ca.mcgill.sable.soot;


import java.util.*;
import ca.mcgill.sable.soot.*;


class PatchingChain extends AbstractCollection implements Chain {


    private Chain innerChain;
    
    public PatchingChain(Chain aChain)
    {
	innerChain = aChain;
    }

    public boolean add(Object o)
    {
        return innerChain.add(o);
    }

    public void insertAfter(Object toInsert, Object point)
    {
	innerChain.insertAfter(toInsert, point);
    }


    public void insertBefore(Object toInsert, Object point)
    {
	((Unit) point).redirectJumpsToThisTo((Unit) toInsert);
	innerChain.insertBefore(toInsert, point);
    }
   



    public boolean follows(Object a, Object b)
    {
	return innerChain.follows(a,b);
    }


    public boolean remove(Object obj)
    {
        boolean res = false;

        if(contains(obj))
        {
            Unit successor;

            
	    if((successor = (Unit)getSuccOf(obj)) == null)
		successor = (Unit) getPredOf(obj);
            
            
	    res = innerChain.remove(obj);

            ((Unit)obj).redirectJumpsToThisTo( successor);
            
        }

        return res;	
    }



    

    public void addFirst(Object u)
    {
	insertBefore(u, innerChain.getFirst());
    }
    

    public void addLast(Object u)
    {
	innerChain.addLast(u);
    }
    
    public void removeFirst() 
    {
	remove(innerChain.getFirst());
    }
    
    public void removeLast()
    {
	remove(innerChain.getLast());
    }
    



    public Object getFirst(){ return innerChain.getFirst();}
    public Object getLast(){return innerChain.getLast();}
    
    public Object getSuccOf(Object point){return innerChain.getSuccOf(point);}
    public Object getPredOf(Object point){return innerChain.getPredOf(point);}
    public Iterator iterator(){return innerChain.iterator();}
    public Iterator iterator(Object u){return innerChain.iterator(u);}
    public Iterator iterator(Object head, Object tail){return innerChain.iterator(head, tail);}
    public int size(){return innerChain.size();}       


}
