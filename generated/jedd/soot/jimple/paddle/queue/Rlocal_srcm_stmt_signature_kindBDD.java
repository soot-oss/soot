package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rlocal_srcm_stmt_signature_kindBDD extends Rlocal_srcm_stmt_signature_kind {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { local.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                          new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.local:soot.jimple.pad" +
                                           "dle.bdddomains.V1, soot.jimple.paddle.bdddomains.srcm:soot.j" +
                                           "imple.paddle.bdddomains.T1, soot.jimple.paddle.bdddomains.st" +
                                           "mt:soot.jimple.paddle.bdddomains.ST, soot.jimple.paddle.bddd" +
                                           "omains.signature:soot.jimple.paddle.bdddomains.H2, soot.jimp" +
                                           "le.paddle.bdddomains.kind:soot.jimple.paddle.bdddomains.FD> " +
                                           "bdd at /tmp/soot-trunk/src/soot/jimple/paddle/queue/Rlocal_s" +
                                           "rcm_stmt_signature_kindBDD.jedd:31,12-63"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rlocal_srcm_stmt_signature_kindBDD(final jedd.internal.RelationContainer bdd) {
        this();
        add(new jedd.internal.RelationContainer(new Attribute[] { kind.v(), local.v(), signature.v(), srcm.v(), stmt.v() },
                                                new PhysicalDomain[] { FD.v(), V1.v(), H2.v(), T1.v(), ST.v() },
                                                ("add(bdd) at /tmp/soot-trunk/src/soot/jimple/paddle/queue/Rlo" +
                                                 "cal_srcm_stmt_signature_kindBDD.jedd:33,115-118"),
                                                bdd));
    }
    
    Rlocal_srcm_stmt_signature_kindBDD() {
        super();
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
                      new jedd.internal.RelationContainer(new Attribute[] { kind.v(), local.v(), signature.v(), srcm.v(), stmt.v() },
                                                          new PhysicalDomain[] { FD.v(), V1.v(), H2.v(), T1.v(), ST.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /tmp/soot-trunk/src" +
                                                           "/soot/jimple/paddle/queue/Rlocal_srcm_stmt_signature_kindBDD" +
                                                           ".jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { local.v(), srcm.v(), stmt.v(), signature.v(), kind.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((Local) components[0],
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
          new jedd.internal.RelationContainer(new Attribute[] { local.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                              new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.local:soot.jimple.paddle.bddd" +
                                               "omains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pa" +
                                               "ddle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot." +
                                               "jimple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.s" +
                                               "ignature:soot.jimple.paddle.bdddomains.H2, soot.jimple.paddl" +
                                               "e.bdddomains.kind:soot.jimple.paddle.bdddomains.FD> ret = bd" +
                                               "d; at /tmp/soot-trunk/src/soot/jimple/paddle/queue/Rlocal_sr" +
                                               "cm_stmt_signature_kindBDD.jedd:55,60-63"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { kind.v(), local.v(), signature.v(), srcm.v(), stmt.v() },
                                                   new PhysicalDomain[] { FD.v(), V1.v(), H2.v(), T1.v(), ST.v() },
                                                   ("return ret; at /tmp/soot-trunk/src/soot/jimple/paddle/queue/" +
                                                    "Rlocal_srcm_stmt_signature_kindBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
