package soot.jimple.paddle;

import soot.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;

public class BDDCallGraph extends AbsCallGraph {
    private final jedd.internal.RelationContainer edges =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcc, soot.jimple.pad" +
                                           "dle.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soo" +
                                           "t.jimple.paddle.bdddomains.kind, soot.jimple.paddle.bdddomai" +
                                           "ns.tgtc, soot.jimple.paddle.bdddomains.tgtm> edges at /tmp/o" +
                                           "lhotak/soot-trunk/src/soot/jimple/paddle/BDDCallGraph.jedd:3" +
                                           "0,12-48"));
    
    BDDCallGraph(Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) { super(in, out); }
    
    public boolean update() {
        final jedd.internal.RelationContainer newEdges =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.C2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT> newEd" +
                                               "ges = in.get(); at /tmp/olhotak/soot-trunk/src/soot/jimple/p" +
                                               "addle/BDDCallGraph.jedd:35,45-53"),
                                              in.get());
        newEdges.eqMinus(edges);
        edges.eqUnion(newEdges);
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), srcm.v(), tgtm.v(), tgtc.v(), kind.v(), srcc.v() },
                                                    new jedd.PhysicalDomain[] { ST.v(), MS.v(), MT.v(), C2.v(), KD.v(), C1.v() },
                                                    ("out.add(newEdges) at /tmp/olhotak/soot-trunk/src/soot/jimple" +
                                                     "/paddle/BDDCallGraph.jedd:38,8-11"),
                                                    newEdges));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newEdges), jedd.internal.Jedd.v().falseBDD());
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesOutOf(Rctxt_method methods) {
        return new Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), srcm.v(), tgtm.v(), kind.v(), tgtc.v(), srcc.v() },
                                                                                         new jedd.PhysicalDomain[] { ST.v(), MS.v(), MT.v(), KD.v(), C2.v(), C1.v() },
                                                                                         ("new soot.jimple.paddle.queue.Rsrcc_srcm_stmt_kind_tgtc_tgtmB" +
                                                                                          "DD(...) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BD" +
                                                                                          "DCallGraph.jedd:42,15-18"),
                                                                                         jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(edges),
                                                                                                                     methods.get(),
                                                                                                                     new jedd.PhysicalDomain[] { MS.v(), C1.v() })),
                                                     "edgesOutOf");
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesOutOf(MethodOrMethodContext m) {
        return new Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), srcm.v(), tgtm.v(), kind.v(), tgtc.v(), srcc.v() },
                                                                                         new jedd.PhysicalDomain[] { ST.v(), MS.v(), MT.v(), KD.v(), C2.v(), C1.v() },
                                                                                         ("new soot.jimple.paddle.queue.Rsrcc_srcm_stmt_kind_tgtc_tgtmB" +
                                                                                          "DD(...) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BD" +
                                                                                          "DCallGraph.jedd:47,15-18"),
                                                                                         jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(edges),
                                                                                                                     jedd.internal.Jedd.v().literal(new Object[] { m.context(), m.method() },
                                                                                                                                                    new jedd.Attribute[] { ctxt.v(), method.v() },
                                                                                                                                                    new jedd.PhysicalDomain[] { C1.v(), MS.v() }),
                                                                                                                     new jedd.PhysicalDomain[] { MS.v(), C1.v() })),
                                                     "edgesOutOf");
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edges() {
        return new Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), srcm.v(), tgtm.v(), tgtc.v(), kind.v(), srcc.v() },
                                                                                         new jedd.PhysicalDomain[] { ST.v(), MS.v(), MT.v(), C2.v(), KD.v(), C1.v() },
                                                                                         ("new soot.jimple.paddle.queue.Rsrcc_srcm_stmt_kind_tgtc_tgtmB" +
                                                                                          "DD(...) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BD" +
                                                                                          "DCallGraph.jedd:54,15-18"),
                                                                                         edges),
                                                     "edges");
    }
    
    public int size() {
        return (int)
                 new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), srcm.v(), tgtm.v(), tgtc.v(), kind.v(), srcc.v() },
                                                     new jedd.PhysicalDomain[] { ST.v(), MS.v(), MT.v(), C2.v(), KD.v(), C1.v() },
                                                     ("edges.size() at /tmp/olhotak/soot-trunk/src/soot/jimple/padd" +
                                                      "le/BDDCallGraph.jedd:57,21-26"),
                                                     edges).size();
    }
}
