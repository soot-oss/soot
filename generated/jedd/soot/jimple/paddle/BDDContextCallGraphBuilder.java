package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;

public class BDDContextCallGraphBuilder extends AbsContextCallGraphBuilder {
    BDDContextCallGraphBuilder(Rctxt_method in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out, AbsCallGraph cicg) {
        super(in, out, cicg);
    }
    
    public void update() {
        final jedd.internal.RelationContainer methods =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.T1> methods = in.get(); at /tmp/soot-trunk/" +
                                               "src/soot/jimple/paddle/BDDContextCallGraphBuilder.jedd:35,23" +
                                               "-30"),
                                              in.get());
        Rsrcc_srcm_stmt_kind_tgtc_tgtm edges =
          cicg.edgesOutOf(new Rctxt_methodBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                                                  new jedd.PhysicalDomain[] { T1.v(), V1.v() },
                                                                                  ("new soot.jimple.paddle.queue.Rctxt_methodBDD(...) at /tmp/so" +
                                                                                   "ot-trunk/src/soot/jimple/paddle/BDDContextCallGraphBuilder.j" +
                                                                                   "edd:38,12-15"),
                                                                                  jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(methods,
                                                                                                                                                                         new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                              jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                                             new jedd.Attribute[] { ctxt.v() },
                                                                                                                                             new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                              new jedd.PhysicalDomain[] {  }))));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v(), tgtc.v(), tgtm.v(), srcm.v(), stmt.v(), srcc.v() },
                                                    new jedd.PhysicalDomain[] { FD.v(), V2.v(), T2.v(), T1.v(), ST.v(), V1.v() },
                                                    ("out.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                                     "ead(jedd.internal.Jedd.v().project(edges.get(), new jedd.Phy" +
                                                     "sicalDomain[...])), methods, new jedd.PhysicalDomain[...])) " +
                                                     "at /tmp/soot-trunk/src/soot/jimple/paddle/BDDContextCallGrap" +
                                                     "hBuilder.jedd:41,8-11"),
                                                    jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(edges.get(),
                                                                                                                                           new jedd.PhysicalDomain[] { V1.v() })),
                                                                                methods,
                                                                                new jedd.PhysicalDomain[] { T1.v() })));
    }
}
