package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rctxt_local_obj_srcm_stmt_kind_tgtmIter extends Rctxt_local_obj_srcm_stmt_kind_tgtm {
    protected Iterator r;
    
    public Rctxt_local_obj_srcm_stmt_kind_tgtmIter(Iterator r) {
        super();
        this.r = r;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            public boolean hasNext() {
                boolean ret = r.hasNext();
                return ret;
            }
            
            public Object next() {
                return new Tuple((Context) r.next(),
                                 (Local) r.next(),
                                 (AllocNode) r.next(),
                                 (SootMethod) r.next(),
                                 (Unit) r.next(),
                                 (Kind) r.next(),
                                 (SootMethod) r.next());
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), local.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                              new PhysicalDomain[] { V2.v(), V1.v(), H1.v(), T1.v(), ST.v(), FD.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V2, soot.jimple.paddle.bdddomains.local:soot.jimple.pa" +
                                               "ddle.bdddomains.V1, soot.jimple.paddle.bdddomains.obj:soot.j" +
                                               "imple.paddle.bdddomains.H1, soot.jimple.paddle.bdddomains.sr" +
                                               "cm:soot.jimple.paddle.bdddomains.T1, soot.jimple.paddle.bddd" +
                                               "omains.stmt:soot.jimple.paddle.bdddomains.ST, soot.jimple.pa" +
                                               "ddle.bdddomains.kind:soot.jimple.paddle.bdddomains.FD, soot." +
                                               "jimple.paddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains." +
                                               "T2> ret = jedd.internal.Jedd.v().falseBDD(); at /tmp/soot-tr" +
                                               "unk/src/soot/jimple/paddle/queue/Rctxt_local_obj_srcm_stmt_k" +
                                               "ind_tgtmIter.jedd:46,72-75"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (r.hasNext()) {
            ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { r.next(), r.next(), r.next(), r.next(), r.next(), r.next(), r.next() },
                                                       new Attribute[] { ctxt.v(), local.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                                       new PhysicalDomain[] { V2.v(), V1.v(), H1.v(), T1.v(), ST.v(), FD.v(), T2.v() }));
        }
        return new jedd.internal.RelationContainer(new Attribute[] { kind.v(), obj.v(), local.v(), ctxt.v(), tgtm.v(), srcm.v(), stmt.v() },
                                                   new PhysicalDomain[] { FD.v(), H1.v(), V1.v(), V2.v(), T2.v(), T1.v(), ST.v() },
                                                   ("return ret; at /tmp/soot-trunk/src/soot/jimple/paddle/queue/" +
                                                    "Rctxt_local_obj_srcm_stmt_kind_tgtmIter.jedd:50,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() { return r.hasNext(); }
}
