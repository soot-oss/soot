package soot.toolkit.graph;


import java.util.*;
import soot.*;

public interface DirectedGraph
{
    public List getHeads();
    public List getTails();
    public List getPredsOf(Unit s);
    public List getSuccsOf(Unit s);
    public int size();
    public Iterator iterator();
}

 
