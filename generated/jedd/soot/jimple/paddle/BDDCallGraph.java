package soot.jimple.paddle;

import soot.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;

public class BDDCallGraph extends AbsCallGraph {
    private final jedd.internal.RelationContainer edges =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), V2.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcc, soot.jimple.pad" +
                                           "dle.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soo" +
                                           "t.jimple.paddle.bdddomains.kind, soot.jimple.paddle.bdddomai" +
                                           "ns.tgtc, soot.jimple.paddle.bdddomains.tgtm> edges at /home/" +
                                           "olhotak/soot-trunk/src/soot/jimple/paddle/BDDCallGraph.jedd:" +
                                           "30,12-48"));
    
    BDDCallGraph(Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) { super(in, out); }
    
    public boolean update() {
        final jedd.internal.RelationContainer newEdges =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.FD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.V2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T2> newEd" +
                                               "ges = in.get(); at /home/olhotak/soot-trunk/src/soot/jimple/" +
                                               "paddle/BDDCallGraph.jedd:35,45-53"),
                                              in.get());
        newEdges.eqMinus(edges);
        edges.eqUnion(newEdges);
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtc.v(), srcc.v(), tgtm.v(), kind.v(), stmt.v(), srcm.v() },
                                                    new jedd.PhysicalDomain[] { V2.v(), V1.v(), T2.v(), FD.v(), ST.v(), T1.v() },
                                                    ("out.add(newEdges) at /home/olhotak/soot-trunk/src/soot/jimpl" +
                                                     "e/paddle/BDDCallGraph.jedd:38,8-11"),
                                                    newEdges));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newEdges), jedd.internal.Jedd.v().falseBDD());
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesOutOf(Rctxt_method methods) {
        return new Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), tgtc.v(), tgtm.v(), kind.v(), stmt.v(), srcm.v() },
                                                                                         new jedd.PhysicalDomain[] { V1.v(), V2.v(), T2.v(), FD.v(), ST.v(), T1.v() },
                                                                                         ("new soot.jimple.paddle.queue.Rsrcc_srcm_stmt_kind_tgtc_tgtmB" +
                                                                                          "DD(...) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/B" +
                                                                                          "DDCallGraph.jedd:42,15-18"),
                                                                                         jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(edges),
                                                                                                                     methods.get(),
                                                                                                                     new jedd.PhysicalDomain[] { T1.v(), V1.v() })),
                                                     "edgesOutOf");
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesOutOf(MethodOrMethodContext m) {
        return new Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), tgtc.v(), tgtm.v(), kind.v(), stmt.v(), srcm.v() },
                                                                                         new jedd.PhysicalDomain[] { V1.v(), V2.v(), T2.v(), FD.v(), ST.v(), T1.v() },
                                                                                         ("new soot.jimple.paddle.queue.Rsrcc_srcm_stmt_kind_tgtc_tgtmB" +
                                                                                          "DD(...) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/B" +
                                                                                          "DDCallGraph.jedd:47,15-18"),
                                                                                         jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(edges),
                                                                                                                     jedd.internal.Jedd.v().literal(new Object[] { m.context(), m.method() },
                                                                                                                                                    new jedd.Attribute[] { ctxt.v(), method.v() },
                                                                                                                                                    new jedd.PhysicalDomain[] { V1.v(), T1.v() }),
                                                                                                                     new jedd.PhysicalDomain[] { T1.v(), V1.v() })),
                                                     "edgesOutOf");
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edges() {
        return new Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtc.v(), srcc.v(), tgtm.v(), kind.v(), stmt.v(), srcm.v() },
                                                                                         new jedd.PhysicalDomain[] { V2.v(), V1.v(), T2.v(), FD.v(), ST.v(), T1.v() },
                                                                                         ("new soot.jimple.paddle.queue.Rsrcc_srcm_stmt_kind_tgtc_tgtmB" +
                                                                                          "DD(...) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/B" +
                                                                                          "DDCallGraph.jedd:54,15-18"),
                                                                                         edges),
                                                     "edges");
    }
    
    public int size() {
        return (int)
                 new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtc.v(), srcc.v(), tgtm.v(), kind.v(), stmt.v(), srcm.v() },
                                                     new jedd.PhysicalDomain[] { V2.v(), V1.v(), T2.v(), FD.v(), ST.v(), T1.v() },
                                                     ("edges.size() at /home/olhotak/soot-trunk/src/soot/jimple/pad" +
                                                      "dle/BDDCallGraph.jedd:57,21-26"),
                                                     edges).size();
    }
}
