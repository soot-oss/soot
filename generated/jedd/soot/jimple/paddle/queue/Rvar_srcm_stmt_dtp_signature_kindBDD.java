package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_srcm_stmt_dtp_signature_kindBDD extends Rvar_srcm_stmt_dtp_signature_kind {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { var.v(), srcm.v(), stmt.v(), dtp.v(), signature.v(), kind.v() },
                                          new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), T1.v(), SG.v(), KD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                           "e.bdddomains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jim" +
                                           "ple.paddle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt" +
                                           ":soot.jimple.paddle.bdddomains.ST, soot.jimple.paddle.bdddom" +
                                           "ains.dtp:soot.jimple.paddle.bdddomains.T1, soot.jimple.paddl" +
                                           "e.bdddomains.signature:soot.jimple.paddle.bdddomains.SG, soo" +
                                           "t.jimple.paddle.bdddomains.kind:soot.jimple.paddle.bdddomain" +
                                           "s.KD> bdd at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/" +
                                           "queue/Rvar_srcm_stmt_dtp_signature_kindBDD.jedd:31,12-69"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rvar_srcm_stmt_dtp_signature_kindBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { dtp.v(), stmt.v(), srcm.v(), var.v(), signature.v(), kind.v() },
                                                new PhysicalDomain[] { T1.v(), ST.v(), MS.v(), V1.v(), SG.v(), KD.v() },
                                                ("add(bdd) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/q" +
                                                 "ueue/Rvar_srcm_stmt_dtp_signature_kindBDD.jedd:33,140-143"),
                                                bdd));
    }
    
    Rvar_srcm_stmt_dtp_signature_kindBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { dtp.v(), stmt.v(), srcm.v(), var.v(), signature.v(), kind.v() },
                                                          new PhysicalDomain[] { T1.v(), ST.v(), MS.v(), V1.v(), SG.v(), KD.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /tmp/olhotak/soot-t" +
                                                           "runk/src/soot/jimple/paddle/queue/Rvar_srcm_stmt_dtp_signatu" +
                                                           "re_kindBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { var.v(), srcm.v(), stmt.v(), dtp.v(), signature.v(), kind.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((VarNode) components[0],
                                 (SootMethod) components[1],
                                 (Unit) components[2],
                                 (Type) components[3],
                                 (NumberedString) components[4],
                                 (Kind) components[5]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), srcm.v(), stmt.v(), dtp.v(), signature.v(), kind.v() },
                                              new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), T1.v(), SG.v(), KD.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                               "le.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                               "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.dtp" +
                                               ":soot.jimple.paddle.bdddomains.T1, soot.jimple.paddle.bdddom" +
                                               "ains.signature:soot.jimple.paddle.bdddomains.SG, soot.jimple" +
                                               ".paddle.bdddomains.kind:soot.jimple.paddle.bdddomains.KD> re" +
                                               "t = bdd; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/q" +
                                               "ueue/Rvar_srcm_stmt_dtp_signature_kindBDD.jedd:55,66-69"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { dtp.v(), stmt.v(), srcm.v(), var.v(), signature.v(), kind.v() },
                                                   new PhysicalDomain[] { T1.v(), ST.v(), MS.v(), V1.v(), SG.v(), KD.v() },
                                                   ("return ret; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                    "e/queue/Rvar_srcm_stmt_dtp_signature_kindBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
