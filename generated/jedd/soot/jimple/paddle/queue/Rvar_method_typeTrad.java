package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_method_typeTrad extends Rvar_method_typeIter {
    Rvar_method_typeTrad(QueueReader r, String name) { super(r, name); }
    
    public Rvar_method_typeTrad copy() {
        return new Rvar_method_typeTrad((QueueReader) ((QueueReader) r).clone(), name);
    }
}
