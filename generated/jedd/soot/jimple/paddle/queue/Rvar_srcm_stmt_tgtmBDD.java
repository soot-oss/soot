package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_srcm_stmt_tgtmBDD extends Rvar_srcm_stmt_tgtm {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { var.v(), srcm.v(), stmt.v(), tgtm.v() },
                                          new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                           "e.bdddomains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jim" +
                                           "ple.paddle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt" +
                                           ":soot.jimple.paddle.bdddomains.ST, soot.jimple.paddle.bdddom" +
                                           "ains.tgtm:soot.jimple.paddle.bdddomains.T2> bdd at /home/olh" +
                                           "otak/soot-trunk2/src/soot/jimple/paddle/queue/Rvar_srcm_stmt" +
                                           "_tgtmBDD.jedd:31,12-47"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rvar_srcm_stmt_tgtmBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        this.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), stmt.v(), srcm.v(), tgtm.v() },
                                                     new PhysicalDomain[] { V1.v(), ST.v(), T1.v(), T2.v() },
                                                     ("this.add(bdd) at /home/olhotak/soot-trunk2/src/soot/jimple/p" +
                                                      "addle/queue/Rvar_srcm_stmt_tgtmBDD.jedd:33,104-107"),
                                                     bdd));
    }
    
    Rvar_srcm_stmt_tgtmBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { var.v(), stmt.v(), srcm.v(), tgtm.v() },
                                                          new PhysicalDomain[] { V1.v(), ST.v(), T1.v(), T2.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-" +
                                                           "trunk2/src/soot/jimple/paddle/queue/Rvar_srcm_stmt_tgtmBDD.j" +
                                                           "edd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { var.v(), srcm.v(), stmt.v(), tgtm.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((VarNode) components[0],
                                 (SootMethod) components[1],
                                 (Unit) components[2],
                                 (SootMethod) components[3]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), srcm.v(), stmt.v(), tgtm.v() },
                                              new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                               "le.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                               "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.tgt" +
                                               "m:soot.jimple.paddle.bdddomains.T2> ret = bdd; at /home/olho" +
                                               "tak/soot-trunk2/src/soot/jimple/paddle/queue/Rvar_srcm_stmt_" +
                                               "tgtmBDD.jedd:55,44-47"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { var.v(), stmt.v(), srcm.v(), tgtm.v() },
                                                   new PhysicalDomain[] { V1.v(), ST.v(), T1.v(), T2.v() },
                                                   ("return ret; at /home/olhotak/soot-trunk2/src/soot/jimple/pad" +
                                                    "dle/queue/Rvar_srcm_stmt_tgtmBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
