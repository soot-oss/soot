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
                                           "ns.tgtc, soot.jimple.paddle.bdddomains.tgtm> edges at /home/" +
                                           "research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDCal" +
                                           "lGraph.jedd:30,12-48"));
    
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
                                               "ges = in.get(); at /home/research/ccl/olhota/soot-trunk/src/" +
                                               "soot/jimple/paddle/BDDCallGraph.jedd:35,45-53"),
                                              in.get());
        newEdges.eqMinus(edges);
        edges.eqUnion(newEdges);
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v(), stmt.v(), srcm.v(), tgtm.v(), srcc.v(), tgtc.v() },
                                                    new jedd.PhysicalDomain[] { KD.v(), ST.v(), MS.v(), MT.v(), C1.v(), C2.v() },
                                                    ("out.add(newEdges) at /home/research/ccl/olhota/soot-trunk/sr" +
                                                     "c/soot/jimple/paddle/BDDCallGraph.jedd:38,8-11"),
                                                    newEdges));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newEdges), jedd.internal.Jedd.v().falseBDD());
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesOutOf(Rctxt_method methods) {
        return new Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v(), srcm.v(), stmt.v(), tgtm.v(), srcc.v(), tgtc.v() },
                                                                                         new jedd.PhysicalDomain[] { KD.v(), MS.v(), ST.v(), MT.v(), C1.v(), C2.v() },
                                                                                         ("new soot.jimple.paddle.queue.Rsrcc_srcm_stmt_kind_tgtc_tgtmB" +
                                                                                          "DD(...) at /home/research/ccl/olhota/soot-trunk/src/soot/jim" +
                                                                                          "ple/paddle/BDDCallGraph.jedd:42,15-18"),
                                                                                         jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(edges),
                                                                                                                     methods.get(),
                                                                                                                     new jedd.PhysicalDomain[] { MS.v(), C1.v() })),
                                                     "edgesOutOf");
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesOutOf(MethodOrMethodContext m) {
        return new Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v(), srcm.v(), stmt.v(), tgtm.v(), srcc.v(), tgtc.v() },
                                                                                         new jedd.PhysicalDomain[] { KD.v(), MS.v(), ST.v(), MT.v(), C1.v(), C2.v() },
                                                                                         ("new soot.jimple.paddle.queue.Rsrcc_srcm_stmt_kind_tgtc_tgtmB" +
                                                                                          "DD(...) at /home/research/ccl/olhota/soot-trunk/src/soot/jim" +
                                                                                          "ple/paddle/BDDCallGraph.jedd:47,15-18"),
                                                                                         jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(edges),
                                                                                                                     jedd.internal.Jedd.v().literal(new Object[] { m.context(), m.method() },
                                                                                                                                                    new jedd.Attribute[] { ctxt.v(), method.v() },
                                                                                                                                                    new jedd.PhysicalDomain[] { C1.v(), MS.v() }),
                                                                                                                     new jedd.PhysicalDomain[] { MS.v(), C1.v() })),
                                                     "edgesOutOf");
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edges() {
        return new Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v(), stmt.v(), srcm.v(), tgtm.v(), srcc.v(), tgtc.v() },
                                                                                         new jedd.PhysicalDomain[] { KD.v(), ST.v(), MS.v(), MT.v(), C1.v(), C2.v() },
                                                                                         ("new soot.jimple.paddle.queue.Rsrcc_srcm_stmt_kind_tgtc_tgtmB" +
                                                                                          "DD(...) at /home/research/ccl/olhota/soot-trunk/src/soot/jim" +
                                                                                          "ple/paddle/BDDCallGraph.jedd:54,15-18"),
                                                                                         edges),
                                                     "edges");
    }
    
    public int size() {
        return (int)
                 new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v(), stmt.v(), srcm.v(), tgtm.v(), srcc.v(), tgtc.v() },
                                                     new jedd.PhysicalDomain[] { KD.v(), ST.v(), MS.v(), MT.v(), C1.v(), C2.v() },
                                                     ("edges.size() at /home/research/ccl/olhota/soot-trunk/src/soo" +
                                                      "t/jimple/paddle/BDDCallGraph.jedd:57,21-26"),
                                                     edges).size();
    }
}
