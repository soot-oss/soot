package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_objTrad extends Rvar_objIter {
    Rvar_objTrad(QueueReader r) { super(r); }
    
    public Rvar_objTrad copy() { return new Rvar_objTrad((QueueReader) ((QueueReader) r).clone()); }
}
