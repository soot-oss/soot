package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rctxt_local_obj_srcm_stmt_kind_tgtmTrad extends Rctxt_local_obj_srcm_stmt_kind_tgtmIter {
    Rctxt_local_obj_srcm_stmt_kind_tgtmTrad(QueueReader r, String name) { super(r, name); }
    
    public Rctxt_local_obj_srcm_stmt_kind_tgtmTrad copy() {
        return new Rctxt_local_obj_srcm_stmt_kind_tgtmTrad((QueueReader) ((QueueReader) r).clone(), name);
    }
}
