package soot.jimple.toolkits.pointer;
import soot.toolkits.graph.*;
import soot.*;
import soot.jimple.*;
import java.util.*;

public class Pair
{
    public Object o1;
    public Object o2;
    public Pair( Object o1, Object o2 ) { this.o1 = o1; this.o2 = o2; }
    public int hashCode() {
	return o1.hashCode() + o2.hashCode();
    }
    public boolean equals( Object other ) {
	if( other instanceof Pair ) {
	    Pair p = (Pair) other;
	    return o1.equals( p.o1 ) && o2.equals( p.o2 );
	} else return false;
    }
    public String toString() {
	return "Pair "+o1+","+o2;
    }
}
