package soot.jimple.toolkits.pointer.kloj;
import java.util.*;
import soot.jimple.toolkits.pointer.*;

public class TopoSortComparator implements Comparator
{
    public int compare( Object o1, Object o2 ) {
	Node n1 = (Node) o1;
	Node n2 = (Node) o2;
	if( n1.topoSortIndex == 0 ) throw new RuntimeException( "Forgot to run toposort?" );
	if( n2.topoSortIndex == 0 ) throw new RuntimeException( "Forgot to run toposort?" );
	return n2.topoSortIndex - n1.topoSortIndex;
    }
}
