package ca.mcgill.sable.soot;

import java.util.*;
import ca.mcgill.sable.soot.*;


public interface Chain
{
    public void insertAfter(Object toInsert, Object point);
    public void insertBefore(Object toInsert, Object point);
    public boolean remove(Object u);
    public void addFirst(Object u);
    public void addLast(Object u);
    public void removeFirst();
    public void removeLast();
    

    public Object getFirst();
    public Object getLast();
    
    public Object getSuccOf(Object point);
    public Object getPredOf(Object point);
    public Iterator iterator();
    public Iterator iterator(Object u);
    public int size();       
}

