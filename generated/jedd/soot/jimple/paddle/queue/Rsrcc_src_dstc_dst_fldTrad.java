package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrcc_src_dstc_dst_fldTrad extends Rsrcc_src_dstc_dst_fldIter {
    Rsrcc_src_dstc_dst_fldTrad(QueueReader r, String name) { super(r, name); }
    
    public Rsrcc_src_dstc_dst_fldTrad copy() {
        return new Rsrcc_src_dstc_dst_fldTrad((QueueReader) ((QueueReader) r).clone(), name);
    }
}
