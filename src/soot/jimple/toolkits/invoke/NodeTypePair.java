package soot.jimple.toolkits.invoke;

import java.util.*;
import soot.util.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

/** A data structure that makes it easier to remember cast edges. */
class NodeTypePair {
    Object node;
    Type t;

    NodeTypePair(Object node, Type t) {
        this.node = node;
        this.t = t;
    }
    
    Object getNode() {
        return node;
    }

    Type getType() {
        return t;
    }
}
