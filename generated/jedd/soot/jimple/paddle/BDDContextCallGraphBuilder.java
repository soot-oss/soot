package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;

public class BDDContextCallGraphBuilder extends AbsContextCallGraphBuilder {
    BDDContextCallGraphBuilder(Rctxt_method methodsIn,
                               Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn,
                               Qsrcc_srcm_stmt_kind_tgtc_tgtm out,
                               AbsCallGraph cicg) {
        super(methodsIn, edgesIn, out, cicg);
    }
    
    private final jedd.internal.RelationContainer m2c =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                          new jedd.PhysicalDomain[] { C1.v(), MS.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.method> m2c = jedd.internal.Jedd.v().falseBDD" +
                                           "() at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/p" +
                                           "addle/BDDContextCallGraphBuilder.jedd:36,12-26"),
                                          jedd.internal.Jedd.v().falseBDD());
    
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
                                               "ges = edgesIn.get(); at /home/research/ccl/olhota/soot-trunk" +
                                               "/src/soot/jimple/paddle/BDDContextCallGraphBuilder.jedd:38,4" +
                                               "5-53"),
                                              edgesIn.get());
        final jedd.internal.RelationContainer newOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.C2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT> newOu" +
                                               "t = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(" +
                                               "jedd.internal.Jedd.v().project(newEdges, new jedd.PhysicalDo" +
                                               "main[...])), m2c, new jedd.PhysicalDomain[...]); at /home/re" +
                                               "search/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDConte" +
                                               "xtCallGraphBuilder.jedd:39,45-51"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(newEdges,
                                                                                                                                     new jedd.PhysicalDomain[] { C1.v() })),
                                                                          m2c,
                                                                          new jedd.PhysicalDomain[] { MS.v() }));
        final jedd.internal.RelationContainer methods =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), MS.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.MS> methods = methodsIn.get(); at /home/res" +
                                               "earch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDContex" +
                                               "tCallGraphBuilder.jedd:42,23-30"),
                                              methodsIn.get());
        Rsrcc_srcm_stmt_kind_tgtc_tgtm edges =
          cicg.edgesOutOf(new Rctxt_methodBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                                                  new jedd.PhysicalDomain[] { MS.v(), C1.v() },
                                                                                  ("new soot.jimple.paddle.queue.Rctxt_methodBDD(...) at /home/r" +
                                                                                   "esearch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDCont" +
                                                                                   "extCallGraphBuilder.jedd:45,12-15"),
                                                                                  jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(methods,
                                                                                                                                                                         new jedd.PhysicalDomain[] { C1.v() })),
                                                                                                              jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                                             new jedd.Attribute[] { ctxt.v() },
                                                                                                                                             new jedd.PhysicalDomain[] { C1.v() }),
                                                                                                              new jedd.PhysicalDomain[] {  })),
                                              "ccgb"));
        newOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(edges.get(),
                                                                                                              new jedd.PhysicalDomain[] { C1.v() })),
                                                   methods,
                                                   new jedd.PhysicalDomain[] { MS.v() }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), srcm.v(), srcc.v(), kind.v(), stmt.v(), tgtc.v() },
                                                    new jedd.PhysicalDomain[] { MT.v(), MS.v(), C1.v(), KD.v(), ST.v(), C2.v() },
                                                    ("out.add(newOut) at /home/research/ccl/olhota/soot-trunk/src/" +
                                                     "soot/jimple/paddle/BDDContextCallGraphBuilder.jedd:49,8-11"),
                                                    newOut));
        m2c.eqUnion(methods);
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newOut), jedd.internal.Jedd.v().falseBDD());
    }
}
