package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qctxt_var_obj_srcm_stmt_kind_tgtmBDD extends Qctxt_var_obj_srcm_stmt_kind_tgtm {
    public Qctxt_var_obj_srcm_stmt_kind_tgtmBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _ctxt,
                    VarNode _var,
                    AllocNode _obj,
                    SootMethod _srcm,
                    Unit _stmt,
                    Kind _kind,
                    SootMethod _tgtm) {
        this.add(new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                                     new PhysicalDomain[] { V2.v(), V1.v(), H1.v(), T1.v(), ST.v(), FD.v(), T2.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/olhotak/soot-trunk2/src/soot/jimple/paddle/queue" +
                                                      "/Qctxt_var_obj_srcm_stmt_kind_tgtmBDD.jedd:34,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _ctxt, _var, _obj, _srcm, _stmt, _kind, _tgtm },
                                                                                    new Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                                                                    new PhysicalDomain[] { V2.v(), V1.v(), H1.v(), T1.v(), ST.v(), FD.v(), T2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rctxt_var_obj_srcm_stmt_kind_tgtmBDD reader = (Rctxt_var_obj_srcm_stmt_kind_tgtmBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), srcm.v(), tgtm.v(), var.v(), kind.v(), stmt.v(), obj.v() },
                                                           new PhysicalDomain[] { V2.v(), T1.v(), T2.v(), V1.v(), FD.v(), ST.v(), H1.v() },
                                                           ("reader.add(in) at /home/olhotak/soot-trunk2/src/soot/jimple/" +
                                                            "paddle/queue/Qctxt_var_obj_srcm_stmt_kind_tgtmBDD.jedd:39,12" +
                                                            "-18"),
                                                           in));
        }
    }
    
    public Rctxt_var_obj_srcm_stmt_kind_tgtm reader(String rname) {
        Rctxt_var_obj_srcm_stmt_kind_tgtm ret = new Rctxt_var_obj_srcm_stmt_kind_tgtmBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
