package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Robj_typeTrad extends Robj_typeIter {
    Robj_typeTrad(QueueReader r, String name) { super(r, name); }
    
    public Robj_typeTrad copy() { return new Robj_typeTrad((QueueReader) ((QueueReader) r).clone(), name); }
}
