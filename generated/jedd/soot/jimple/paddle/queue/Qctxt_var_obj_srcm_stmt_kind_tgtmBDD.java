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
        add(new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                                new PhysicalDomain[] { C2.v(), V1.v(), H1.v(), MS.v(), ST.v(), KD.v(), MT.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /home/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle" +
                                                 "/queue/Qctxt_var_obj_srcm_stmt_kind_tgtmBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _ctxt, _var, _obj, _srcm, _stmt, _kind, _tgtm },
                                                                               new Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                                                               new PhysicalDomain[] { C2.v(), V1.v(), H1.v(), MS.v(), ST.v(), KD.v(), MT.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rctxt_var_obj_srcm_stmt_kind_tgtmBDD reader = (Rctxt_var_obj_srcm_stmt_kind_tgtmBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), kind.v(), srcm.v(), ctxt.v(), stmt.v(), obj.v(), tgtm.v() },
                                                           new PhysicalDomain[] { V1.v(), KD.v(), MS.v(), C2.v(), ST.v(), H1.v(), MT.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                            "oot/jimple/paddle/queue/Qctxt_var_obj_srcm_stmt_kind_tgtmBDD" +
                                                            ".jedd:40,12-18"),
                                                           in));
        }
    }
    
    public Rctxt_var_obj_srcm_stmt_kind_tgtm reader(String rname) {
        Rctxt_var_obj_srcm_stmt_kind_tgtm ret = new Rctxt_var_obj_srcm_stmt_kind_tgtmBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
