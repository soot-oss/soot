package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrcm_stmt_kind_tgtm_src_dstTrad extends Rsrcm_stmt_kind_tgtm_src_dstIter {
    Rsrcm_stmt_kind_tgtm_src_dstTrad(QueueReader r, String name) { super(r, name); }
    
    public Rsrcm_stmt_kind_tgtm_src_dstTrad copy() {
        return new Rsrcm_stmt_kind_tgtm_src_dstTrad((QueueReader) ((QueueReader) r).clone(), name);
    }
}
