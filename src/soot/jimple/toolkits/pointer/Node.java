package soot.jimple.toolkits.pointer;
import java.util.*;
import soot.*;

public class Node
{
    static int nextNodeId = 1;
    int id;
    public int topoSortIndex;
    protected void assignId() {
	id = nextNodeId++;
    }
    public boolean equals( Object o ) {
	/*
	if( id == 0 ) throw new RuntimeException( "Node without id" );
	if( o instanceof Node ) {
	    int otherid = ((Node)o).id;
		if( otherid == 0 ) throw new RuntimeException( "Node without id" );
	    return otherid == id;
	} else return false;
	*/
	return o == this;
    }
    public int hashCode() {
	return id;
    }
    public int getId() {
	return id;
    }

    protected Type type;
    Node( Type t ) {
	assignId();
	this.type = t;
    }
    public Type getType() { return type; }
}

