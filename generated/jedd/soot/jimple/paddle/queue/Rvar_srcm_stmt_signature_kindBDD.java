package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_srcm_stmt_signature_kindBDD extends Rvar_srcm_stmt_signature_kind {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                          new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), SG.v(), KD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                           "e.bdddomains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jim" +
                                           "ple.paddle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt" +
                                           ":soot.jimple.paddle.bdddomains.ST, soot.jimple.paddle.bdddom" +
                                           "ains.signature:soot.jimple.paddle.bdddomains.SG, soot.jimple" +
                                           ".paddle.bdddomains.kind:soot.jimple.paddle.bdddomains.KD> bd" +
                                           "d at /home/olhotak/soot-trunk/src/soot/jimple/paddle/queue/R" +
                                           "var_srcm_stmt_signature_kindBDD.jedd:31,12-61"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rvar_srcm_stmt_signature_kindBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { kind.v(), srcm.v(), signature.v(), var.v(), stmt.v() },
                                                new PhysicalDomain[] { KD.v(), MS.v(), SG.v(), V1.v(), ST.v() },
                                                ("add(bdd) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/" +
                                                 "queue/Rvar_srcm_stmt_signature_kindBDD.jedd:33,128-131"),
                                                bdd));
    }
    
    Rvar_srcm_stmt_signature_kindBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { kind.v(), srcm.v(), signature.v(), var.v(), stmt.v() },
                                                          new PhysicalDomain[] { KD.v(), MS.v(), SG.v(), V1.v(), ST.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-" +
                                                           "trunk/src/soot/jimple/paddle/queue/Rvar_srcm_stmt_signature_" +
                                                           "kindBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((VarNode) components[0],
                                 (SootMethod) components[1],
                                 (Unit) components[2],
                                 (NumberedString) components[3],
                                 (Kind) components[4]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                              new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), SG.v(), KD.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                               "le.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                               "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.sig" +
                                               "nature:soot.jimple.paddle.bdddomains.SG, soot.jimple.paddle." +
                                               "bdddomains.kind:soot.jimple.paddle.bdddomains.KD> ret = bdd;" +
                                               " at /home/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Rv" +
                                               "ar_srcm_stmt_signature_kindBDD.jedd:55,58-61"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { kind.v(), srcm.v(), signature.v(), var.v(), stmt.v() },
                                                   new PhysicalDomain[] { KD.v(), MS.v(), SG.v(), V1.v(), ST.v() },
                                                   ("return ret; at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                                    "le/queue/Rvar_srcm_stmt_signature_kindBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
