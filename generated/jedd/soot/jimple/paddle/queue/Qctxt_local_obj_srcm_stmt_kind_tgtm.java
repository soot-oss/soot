package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qctxt_local_obj_srcm_stmt_kind_tgtm {
    public abstract void add(Context _ctxt,
                             Local _local,
                             AllocNode _obj,
                             SootMethod _srcm,
                             Unit _stmt,
                             Kind _kind,
                             SootMethod _tgtm);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rctxt_local_obj_srcm_stmt_kind_tgtm reader();
    
    public Qctxt_local_obj_srcm_stmt_kind_tgtm() { super(); }
}
