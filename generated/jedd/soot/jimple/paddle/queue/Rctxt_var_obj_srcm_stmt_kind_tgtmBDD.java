package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rctxt_var_obj_srcm_stmt_kind_tgtmBDD extends Rctxt_var_obj_srcm_stmt_kind_tgtm {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                          new PhysicalDomain[] { C1.v(), V1.v(), H1.v(), MS.v(), ST.v(), KD.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt:soot.jimple.padd" +
                                           "le.bdddomains.C1, soot.jimple.paddle.bdddomains.var:soot.jim" +
                                           "ple.paddle.bdddomains.V1, soot.jimple.paddle.bdddomains.obj:" +
                                           "soot.jimple.paddle.bdddomains.H1, soot.jimple.paddle.bdddoma" +
                                           "ins.srcm:soot.jimple.paddle.bdddomains.MS, soot.jimple.paddl" +
                                           "e.bdddomains.stmt:soot.jimple.paddle.bdddomains.ST, soot.jim" +
                                           "ple.paddle.bdddomains.kind:soot.jimple.paddle.bdddomains.KD," +
                                           " soot.jimple.paddle.bdddomains.tgtm:soot.jimple.paddle.bdddo" +
                                           "mains.MT> bdd at /tmp/olhotak/soot-trunk/src/soot/jimple/pad" +
                                           "dle/queue/Rctxt_var_obj_srcm_stmt_kind_tgtmBDD.jedd:31,12-73"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rctxt_var_obj_srcm_stmt_kind_tgtmBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), stmt.v(), srcm.v(), var.v(), tgtm.v(), kind.v(), obj.v() },
                                                new PhysicalDomain[] { C1.v(), ST.v(), MS.v(), V1.v(), MT.v(), KD.v(), H1.v() },
                                                ("add(bdd) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/q" +
                                                 "ueue/Rctxt_var_obj_srcm_stmt_kind_tgtmBDD.jedd:33,144-147"),
                                                bdd));
    }
    
    Rctxt_var_obj_srcm_stmt_kind_tgtmBDD(String name) {
        super(name);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
    }
    
    public Iterator iterator() {
        ;
        return new Iterator() {
            private Iterator it;
            
            public boolean hasNext() {
                if (it != null && it.hasNext()) return true;
                if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD()))
                    return true;
                return false;
            }
            
            public Object next() {
                if (it == null || !it.hasNext()) {
                    it =
                      new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), stmt.v(), srcm.v(), var.v(), tgtm.v(), kind.v(), obj.v() },
                                                          new PhysicalDomain[] { C1.v(), ST.v(), MS.v(), V1.v(), MT.v(), KD.v(), H1.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /tmp/olhotak/soot-t" +
                                                           "runk/src/soot/jimple/paddle/queue/Rctxt_var_obj_srcm_stmt_ki" +
                                                           "nd_tgtmBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((Context) components[0],
                                 (VarNode) components[1],
                                 (AllocNode) components[2],
                                 (SootMethod) components[3],
                                 (Unit) components[4],
                                 (Kind) components[5],
                                 (SootMethod) components[6]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), H1.v(), MS.v(), ST.v(), KD.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.obj:soot.jim" +
                                               "ple.paddle.bdddomains.H1, soot.jimple.paddle.bdddomains.srcm" +
                                               ":soot.jimple.paddle.bdddomains.MS, soot.jimple.paddle.bdddom" +
                                               "ains.stmt:soot.jimple.paddle.bdddomains.ST, soot.jimple.padd" +
                                               "le.bdddomains.kind:soot.jimple.paddle.bdddomains.KD, soot.ji" +
                                               "mple.paddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT" +
                                               "> ret = bdd; at /tmp/olhotak/soot-trunk/src/soot/jimple/padd" +
                                               "le/queue/Rctxt_var_obj_srcm_stmt_kind_tgtmBDD.jedd:55,70-73"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), stmt.v(), srcm.v(), var.v(), tgtm.v(), kind.v(), obj.v() },
                                                   new PhysicalDomain[] { C1.v(), ST.v(), MS.v(), V1.v(), MT.v(), KD.v(), H1.v() },
                                                   ("return ret; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                    "e/queue/Rctxt_var_obj_srcm_stmt_kind_tgtmBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
