package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class RobjTrad extends RobjIter {
    RobjTrad(QueueReader r, String name) { super(r, name); }
    
    public RobjTrad copy() { return new RobjTrad((QueueReader) ((QueueReader) r).clone(), name); }
}
