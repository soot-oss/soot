package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrc_fld_dstTrad extends Rsrc_fld_dstIter {
    Rsrc_fld_dstTrad(QueueReader r) { super(r); }
    
    public Rsrc_fld_dstTrad copy() { return new Rsrc_fld_dstTrad((QueueReader) ((QueueReader) r).clone()); }
}
