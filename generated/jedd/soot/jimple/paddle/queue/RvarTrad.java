package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class RvarTrad extends RvarIter {
    RvarTrad(QueueReader r) { super(r); }
    
    public RvarTrad copy() { return new RvarTrad((QueueReader) ((QueueReader) r).clone()); }
}
