package ca.mcgill.sable.soot;

import java.util.*;
import ca.mcgill.sable.soot.*;


public interface Chain extends Collection
{
    public void insertAfter(Object toInsert, Object point);
    public void insertBefore(Object toInsert, Object point);
    public void swapWith(Object out, Object in);
    public boolean remove(Object u);
    public void addFirst(Object u);
    public void addLast(Object u);
    public void removeFirst();
    public void removeLast();
    public boolean follows(Object someObject, Object someReferenceObject);
    
    public Object getFirst();
    public Object getLast();
    
    public Object getSuccOf(Object point);
    public Object getPredOf(Object point);
    public Iterator snapshotIterator();
    public Iterator iterator();
    public Iterator iterator(Object u);
    public Iterator iterator(Object head, Object tail);
    public int size();   
}

