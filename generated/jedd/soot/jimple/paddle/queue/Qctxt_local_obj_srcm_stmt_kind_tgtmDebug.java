package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qctxt_local_obj_srcm_stmt_kind_tgtmDebug extends Qctxt_local_obj_srcm_stmt_kind_tgtm {
    private Qctxt_local_obj_srcm_stmt_kind_tgtmBDD bdd = new Qctxt_local_obj_srcm_stmt_kind_tgtmBDD();
    
    private Qctxt_local_obj_srcm_stmt_kind_tgtmSet trad = new Qctxt_local_obj_srcm_stmt_kind_tgtmSet();
    
    public void add(Context _ctxt,
                    Local _local,
                    AllocNode _obj,
                    SootMethod _srcm,
                    Unit _stmt,
                    Kind _kind,
                    SootMethod _tgtm) {
        bdd.add(_ctxt, _local, _obj, _srcm, _stmt, _kind, _tgtm);
        trad.add(_ctxt, _local, _obj, _srcm, _stmt, _kind, _tgtm);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { stmt.v(), local.v(), srcm.v(), ctxt.v(), kind.v(), tgtm.v(), obj.v() },
                                              new PhysicalDomain[] { ST.v(), V1.v(), T1.v(), V2.v(), FD.v(), T2.v(), H1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/queue/Qctxt_local_obj_srcm_stmt_" +
                                               "kind_tgtmDebug.jedd:38,22-24"),
                                              in).iterator(new Attribute[] { ctxt.v(), local.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 7; i++) {
                this.add((Context) tuple[0],
                         (Local) tuple[1],
                         (AllocNode) tuple[2],
                         (SootMethod) tuple[3],
                         (Unit) tuple[4],
                         (Kind) tuple[5],
                         (SootMethod) tuple[6]);
            }
        }
    }
    
    public Rctxt_local_obj_srcm_stmt_kind_tgtm reader() {
        return new Rctxt_local_obj_srcm_stmt_kind_tgtmDebug((Rctxt_local_obj_srcm_stmt_kind_tgtmBDD) bdd.reader(),
                                                            (Rctxt_local_obj_srcm_stmt_kind_tgtmSet) trad.reader());
    }
    
    public Qctxt_local_obj_srcm_stmt_kind_tgtmDebug() { super(); }
}
