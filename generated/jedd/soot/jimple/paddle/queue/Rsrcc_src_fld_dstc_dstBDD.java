package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrcc_src_fld_dstc_dstBDD extends Rsrcc_src_fld_dstc_dst {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                          new PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcc:soot.jimple.padd" +
                                           "le.bdddomains.C1, soot.jimple.paddle.bdddomains.src:soot.jim" +
                                           "ple.paddle.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:" +
                                           "soot.jimple.paddle.bdddomains.FD, soot.jimple.paddle.bdddoma" +
                                           "ins.dstc:soot.jimple.paddle.bdddomains.C2, soot.jimple.paddl" +
                                           "e.bdddomains.dst:soot.jimple.paddle.bdddomains.V2> bdd at /t" +
                                           "mp/soot-trunk-saved/src/soot/jimple/paddle/queue/Rsrcc_src_f" +
                                           "ld_dstc_dstBDD.jedd:31,12-54"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rsrcc_src_fld_dstc_dstBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), srcc.v(), dst.v(), dstc.v(), src.v() },
                                                new PhysicalDomain[] { FD.v(), C1.v(), V2.v(), C2.v(), V1.v() },
                                                ("add(bdd) at /tmp/soot-trunk-saved/src/soot/jimple/paddle/que" +
                                                 "ue/Rsrcc_src_fld_dstc_dstBDD.jedd:33,114-117"),
                                                bdd));
    }
    
    Rsrcc_src_fld_dstc_dstBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { fld.v(), srcc.v(), dst.v(), dstc.v(), src.v() },
                                                          new PhysicalDomain[] { FD.v(), C1.v(), V2.v(), C2.v(), V1.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /tmp/soot-trunk-sav" +
                                                           "ed/src/soot/jimple/paddle/queue/Rsrcc_src_fld_dstc_dstBDD.je" +
                                                           "dd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((Context) components[0],
                                 (VarNode) components[1],
                                 (PaddleField) components[2],
                                 (Context) components[3],
                                 (VarNode) components[4]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dstc" +
                                               ":soot.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddom" +
                                               "ains.dst:soot.jimple.paddle.bdddomains.V2> ret = bdd; at /tm" +
                                               "p/soot-trunk-saved/src/soot/jimple/paddle/queue/Rsrcc_src_fl" +
                                               "d_dstc_dstBDD.jedd:55,51-54"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { fld.v(), srcc.v(), dst.v(), dstc.v(), src.v() },
                                                   new PhysicalDomain[] { FD.v(), C1.v(), V2.v(), C2.v(), V1.v() },
                                                   ("return ret; at /tmp/soot-trunk-saved/src/soot/jimple/paddle/" +
                                                    "queue/Rsrcc_src_fld_dstc_dstBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
