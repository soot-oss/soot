package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rctxt_methodTrad extends Rctxt_methodIter {
    Rctxt_methodTrad(QueueReader r) { super(r); }
    
    public Rctxt_methodTrad copy() { return new Rctxt_methodTrad((QueueReader) ((QueueReader) r).clone()); }
}
