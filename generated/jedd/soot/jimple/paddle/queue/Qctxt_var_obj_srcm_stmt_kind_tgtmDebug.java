package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qctxt_var_obj_srcm_stmt_kind_tgtmDebug extends Qctxt_var_obj_srcm_stmt_kind_tgtm {
    public Qctxt_var_obj_srcm_stmt_kind_tgtmDebug(String name) { super(name); }
    
    private Qctxt_var_obj_srcm_stmt_kind_tgtmBDD bdd = new Qctxt_var_obj_srcm_stmt_kind_tgtmBDD(name + "bdd");
    
    private Qctxt_var_obj_srcm_stmt_kind_tgtmSet trad = new Qctxt_var_obj_srcm_stmt_kind_tgtmSet(name + "set");
    
    public void add(Context _ctxt,
                    VarNode _var,
                    AllocNode _obj,
                    SootMethod _srcm,
                    Unit _stmt,
                    Kind _kind,
                    SootMethod _tgtm) {
        bdd.add(_ctxt, _var, _obj, _srcm, _stmt, _kind, _tgtm);
        trad.add(_ctxt, _var, _obj, _srcm, _stmt, _kind, _tgtm);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), srcm.v(), kind.v(), stmt.v(), ctxt.v(), tgtm.v(), obj.v() },
                                              new PhysicalDomain[] { V1.v(), T1.v(), FD.v(), ST.v(), V2.v(), T2.v(), H1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/queue/Qctxt_var_obj_srcm_stmt_ki" +
                                               "nd_tgtmDebug.jedd:39,22-24"),
                                              in).iterator(new Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 7; i++) {
                this.add((Context) tuple[0],
                         (VarNode) tuple[1],
                         (AllocNode) tuple[2],
                         (SootMethod) tuple[3],
                         (Unit) tuple[4],
                         (Kind) tuple[5],
                         (SootMethod) tuple[6]);
            }
        }
    }
    
    public Rctxt_var_obj_srcm_stmt_kind_tgtm reader(String rname) {
        return new Rctxt_var_obj_srcm_stmt_kind_tgtmDebug((Rctxt_var_obj_srcm_stmt_kind_tgtmBDD) bdd.reader(rname),
                                                          (Rctxt_var_obj_srcm_stmt_kind_tgtmSet) trad.reader(rname),
                                                          name + ":" +
                                                          rname);
    }
}
