package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrc_dst_fldTrad extends Rsrc_dst_fldIter {
    Rsrc_dst_fldTrad(QueueReader r, String name) { super(r, name); }
    
    public Rsrc_dst_fldTrad copy() { return new Rsrc_dst_fldTrad((QueueReader) ((QueueReader) r).clone(), name); }
}
