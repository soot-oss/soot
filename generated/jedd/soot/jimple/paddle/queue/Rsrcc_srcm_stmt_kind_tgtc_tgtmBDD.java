package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD extends Rsrcc_srcm_stmt_kind_tgtc_tgtm {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                          new PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcc:soot.jimple.padd" +
                                           "le.bdddomains.C1, soot.jimple.paddle.bdddomains.srcm:soot.ji" +
                                           "mple.paddle.bdddomains.MS, soot.jimple.paddle.bdddomains.stm" +
                                           "t:soot.jimple.paddle.bdddomains.ST, soot.jimple.paddle.bdddo" +
                                           "mains.kind:soot.jimple.paddle.bdddomains.KD, soot.jimple.pad" +
                                           "dle.bdddomains.tgtc:soot.jimple.paddle.bdddomains.C2, soot.j" +
                                           "imple.paddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.M" +
                                           "T> bdd at /home/research/ccl/olhota/soot-trunk/src/soot/jimp" +
                                           "le/paddle/queue/Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD.jedd:31,12" +
                                           "-66"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        this.add(new jedd.internal.RelationContainer(new Attribute[] { kind.v(), stmt.v(), srcm.v(), tgtm.v(), srcc.v(), tgtc.v() },
                                                     new PhysicalDomain[] { KD.v(), ST.v(), MS.v(), MT.v(), C1.v(), C2.v() },
                                                     ("this.add(bdd) at /home/research/ccl/olhota/soot-trunk/src/so" +
                                                      "ot/jimple/paddle/queue/Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD.jed" +
                                                      "d:33,134-137"),
                                                     bdd));
    }
    
    Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { kind.v(), stmt.v(), srcm.v(), tgtm.v(), srcc.v(), tgtc.v() },
                                                          new PhysicalDomain[] { KD.v(), ST.v(), MS.v(), MT.v(), C1.v(), C2.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/research/ccl/" +
                                                           "olhota/soot-trunk/src/soot/jimple/paddle/queue/Rsrcc_srcm_st" +
                                                           "mt_kind_tgtc_tgtmBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((Context) components[0],
                                 (SootMethod) components[1],
                                 (Unit) components[2],
                                 (Kind) components[3],
                                 (Context) components[4],
                                 (SootMethod) components[5]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.C2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT> ret =" +
                                               " bdd; at /home/research/ccl/olhota/soot-trunk/src/soot/jimpl" +
                                               "e/paddle/queue/Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD.jedd:55,63-" +
                                               "66"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { kind.v(), stmt.v(), srcm.v(), tgtm.v(), srcc.v(), tgtc.v() },
                                                   new PhysicalDomain[] { KD.v(), ST.v(), MS.v(), MT.v(), C1.v(), C2.v() },
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                    "/jimple/paddle/queue/Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD.jedd:" +
                                                    "57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
