package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qctxt_var_obj_srcm_stmt_kind_tgtmTrad extends Qctxt_var_obj_srcm_stmt_kind_tgtm {
    public Qctxt_var_obj_srcm_stmt_kind_tgtmTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(Context _ctxt,
                    VarNode _var,
                    AllocNode _obj,
                    SootMethod _srcm,
                    Unit _stmt,
                    Kind _kind,
                    SootMethod _tgtm) {
        q.add(_ctxt);
        q.add(_var);
        q.add(_obj);
        q.add(_srcm);
        q.add(_stmt);
        q.add(_kind);
        q.add(_tgtm);
        invalidate();
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), kind.v(), obj.v(), tgtm.v(), stmt.v(), ctxt.v(), var.v() },
                                              new PhysicalDomain[] { MS.v(), KD.v(), H1.v(), MT.v(), ST.v(), C2.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/soot-trunk/src/soot/jimple/paddle/queue/Qctxt_var_obj_" +
                                               "srcm_stmt_kind_tgtmTrad.jedd:44,22-24"),
                                              in).iterator(new Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 7; i++) {
                add((Context) tuple[0],
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
        return new Rctxt_var_obj_srcm_stmt_kind_tgtmTrad(q.reader(), name + ":" + rname);
    }
}
