package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qctxt_var_obj_srcm_stmt_kind_tgtmSet extends Qctxt_var_obj_srcm_stmt_kind_tgtm {
    public Qctxt_var_obj_srcm_stmt_kind_tgtmSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _ctxt,
                    VarNode _var,
                    AllocNode _obj,
                    SootMethod _srcm,
                    Unit _stmt,
                    Kind _kind,
                    SootMethod _tgtm) {
        Rctxt_var_obj_srcm_stmt_kind_tgtm.Tuple in =
          new Rctxt_var_obj_srcm_stmt_kind_tgtm.Tuple(_ctxt, _var, _obj, _srcm, _stmt, _kind, _tgtm);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rctxt_var_obj_srcm_stmt_kind_tgtmSet reader = (Rctxt_var_obj_srcm_stmt_kind_tgtmSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rctxt_var_obj_srcm_stmt_kind_tgtm reader(String rname) {
        Rctxt_var_obj_srcm_stmt_kind_tgtm ret = new Rctxt_var_obj_srcm_stmt_kind_tgtmSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Rctxt_var_obj_srcm_stmt_kind_tgtm revreader(String rname) {
        Rctxt_var_obj_srcm_stmt_kind_tgtm ret = new Rctxt_var_obj_srcm_stmt_kind_tgtmRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
