package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrcm_stmt_kind_tgtm_src_dstBDD extends Rsrcm_stmt_kind_tgtm_src_dst {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                          new PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v(), V1.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                           "le.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                           "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.kin" +
                                           "d:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bdddo" +
                                           "mains.tgtm:soot.jimple.paddle.bdddomains.MT, soot.jimple.pad" +
                                           "dle.bdddomains.src:soot.jimple.paddle.bdddomains.V1, soot.ji" +
                                           "mple.paddle.bdddomains.dst:soot.jimple.paddle.bdddomains.V2>" +
                                           " bdd at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/queue" +
                                           "/Rsrcm_stmt_kind_tgtm_src_dstBDD.jedd:31,12-64"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rsrcm_stmt_kind_tgtm_src_dstBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { dst.v(), tgtm.v(), srcm.v(), kind.v(), stmt.v(), src.v() },
                                                new PhysicalDomain[] { V2.v(), MT.v(), MS.v(), KD.v(), ST.v(), V1.v() },
                                                ("add(bdd) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/q" +
                                                 "ueue/Rsrcm_stmt_kind_tgtm_src_dstBDD.jedd:33,130-133"),
                                                bdd));
    }
    
    Rsrcm_stmt_kind_tgtm_src_dstBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { dst.v(), tgtm.v(), srcm.v(), kind.v(), stmt.v(), src.v() },
                                                          new PhysicalDomain[] { V2.v(), MT.v(), MS.v(), KD.v(), ST.v(), V1.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /tmp/olhotak/soot-t" +
                                                           "runk/src/soot/jimple/paddle/queue/Rsrcm_stmt_kind_tgtm_src_d" +
                                                           "stBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((SootMethod) components[0],
                                 (Unit) components[1],
                                 (Kind) components[2],
                                 (SootMethod) components[3],
                                 (VarNode) components[4],
                                 (VarNode) components[5]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                              new PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v(), V1.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcm:soot.jimple.paddle.bdddo" +
                                               "mains.MS, soot.jimple.paddle.bdddomains.stmt:soot.jimple.pad" +
                                               "dle.bdddomains.ST, soot.jimple.paddle.bdddomains.kind:soot.j" +
                                               "imple.paddle.bdddomains.KD, soot.jimple.paddle.bdddomains.tg" +
                                               "tm:soot.jimple.paddle.bdddomains.MT, soot.jimple.paddle.bddd" +
                                               "omains.src:soot.jimple.paddle.bdddomains.V1, soot.jimple.pad" +
                                               "dle.bdddomains.dst:soot.jimple.paddle.bdddomains.V2> ret = b" +
                                               "dd; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/queue/" +
                                               "Rsrcm_stmt_kind_tgtm_src_dstBDD.jedd:55,61-64"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { dst.v(), tgtm.v(), srcm.v(), kind.v(), stmt.v(), src.v() },
                                                   new PhysicalDomain[] { V2.v(), MT.v(), MS.v(), KD.v(), ST.v(), V1.v() },
                                                   ("return ret; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                    "e/queue/Rsrcm_stmt_kind_tgtm_src_dstBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
