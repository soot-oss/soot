package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qctxt_local_obj_srcm_stmt_kind_tgtmNumTrace extends Qctxt_local_obj_srcm_stmt_kind_tgtm {
    public Qctxt_local_obj_srcm_stmt_kind_tgtmNumTrace(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _ctxt,
                    Local _local,
                    AllocNode _obj,
                    SootMethod _srcm,
                    Unit _stmt,
                    Kind _kind,
                    SootMethod _tgtm) {
        Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple in =
          new Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple(_ctxt, _local, _obj, _srcm, _stmt, _kind, _tgtm);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rctxt_local_obj_srcm_stmt_kind_tgtmNumTrace reader =
              (Rctxt_local_obj_srcm_stmt_kind_tgtmNumTrace) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rctxt_local_obj_srcm_stmt_kind_tgtm reader(String rname) {
        Rctxt_local_obj_srcm_stmt_kind_tgtm ret = new Rctxt_local_obj_srcm_stmt_kind_tgtmNumTrace(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
