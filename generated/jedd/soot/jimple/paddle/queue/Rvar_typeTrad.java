package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_typeTrad extends Rvar_typeIter {
    Rvar_typeTrad(QueueReader r, String name) { super(r, name); }
    
    public Rvar_typeTrad copy() { return new Rvar_typeTrad((QueueReader) ((QueueReader) r).clone(), name); }
}
