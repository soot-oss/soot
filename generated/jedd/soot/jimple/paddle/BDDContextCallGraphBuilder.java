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
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.method> m2c = jedd.internal.Jedd.v().falseBDD" +
                                           "() at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCon" +
                                           "textCallGraphBuilder.jedd:36,12-26"),
                                          jedd.internal.Jedd.v().falseBDD());
    
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
                                               "ges = edgesIn.get(); at /home/olhotak/soot-trunk/src/soot/ji" +
                                               "mple/paddle/BDDContextCallGraphBuilder.jedd:38,45-53"),
                                              edgesIn.get());
        final jedd.internal.RelationContainer newOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.FD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.V2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T2> newOu" +
                                               "t = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(" +
                                               "jedd.internal.Jedd.v().project(newEdges, new jedd.PhysicalDo" +
                                               "main[...])), m2c, new jedd.PhysicalDomain[...]); at /home/ol" +
                                               "hotak/soot-trunk/src/soot/jimple/paddle/BDDContextCallGraphB" +
                                               "uilder.jedd:39,45-51"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(newEdges,
                                                                                                                                     new jedd.PhysicalDomain[] { V1.v() })),
                                                                          m2c,
                                                                          new jedd.PhysicalDomain[] { T1.v() }));
        final jedd.internal.RelationContainer methods =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.T1> methods = methodsIn.get(); at /home/olh" +
                                               "otak/soot-trunk/src/soot/jimple/paddle/BDDContextCallGraphBu" +
                                               "ilder.jedd:42,23-30"),
                                              methodsIn.get());
        Rsrcc_srcm_stmt_kind_tgtc_tgtm edges =
          cicg.edgesOutOf(new Rctxt_methodBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                                                  new jedd.PhysicalDomain[] { T1.v(), V1.v() },
                                                                                  ("new soot.jimple.paddle.queue.Rctxt_methodBDD(...) at /home/o" +
                                                                                   "lhotak/soot-trunk/src/soot/jimple/paddle/BDDContextCallGraph" +
                                                                                   "Builder.jedd:45,12-15"),
                                                                                  jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(methods,
                                                                                                                                                                         new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                              jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                                             new jedd.Attribute[] { ctxt.v() },
                                                                                                                                             new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                              new jedd.PhysicalDomain[] {  })),
                                              "ccgb"));
        newOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(edges.get(),
                                                                                                              new jedd.PhysicalDomain[] { V1.v() })),
                                                   methods,
                                                   new jedd.PhysicalDomain[] { T1.v() }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), tgtc.v(), stmt.v(), srcm.v(), kind.v(), srcc.v() },
                                                    new jedd.PhysicalDomain[] { T2.v(), V2.v(), ST.v(), T1.v(), FD.v(), V1.v() },
                                                    ("out.add(newOut) at /home/olhotak/soot-trunk/src/soot/jimple/" +
                                                     "paddle/BDDContextCallGraphBuilder.jedd:49,8-11"),
                                                    newOut));
        m2c.eqUnion(methods);
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newOut), jedd.internal.Jedd.v().falseBDD());
    }
}
