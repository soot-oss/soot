package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.*;

public class BDDReachableMethods extends AbsReachableMethods {
    private final jedd.internal.RelationContainer reachables =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                          new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.method> reachables at /tmp/soot-trunk/src/soo" +
                                           "t/jimple/paddle/BDDReachableMethods.jedd:30,12-26"));
    
    private AbsCallGraph cg;
    
    private Rctxt_method newMethods;
    
    BDDReachableMethods(Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qctxt_method out, AbsCallGraph cg) {
        super(in, out);
        this.cg = cg;
        newMethods = out.reader();
    }
    
    void update() {
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
                                               "ges = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().rea" +
                                               "d(in.get()), jedd.internal.Jedd.v().replace(reachables, new " +
                                               "jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]), new" +
                                               " jedd.PhysicalDomain[...]); at /tmp/soot-trunk/src/soot/jimp" +
                                               "le/paddle/BDDReachableMethods.jedd:39,45-53"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(in.get()),
                                                                          jedd.internal.Jedd.v().replace(reachables,
                                                                                                         new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                                                                                         new jedd.PhysicalDomain[] { V1.v(), T1.v() }),
                                                                          new jedd.PhysicalDomain[] { V1.v(), T1.v() }));
        while (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newEdges),
                                              jedd.internal.Jedd.v().falseBDD())) {
            final jedd.internal.RelationContainer newTargets =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                                  new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                                  ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                                   "mains.V2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                                   "addle.bdddomains.T2> newTargets = jedd.internal.Jedd.v().pro" +
                                                   "ject(newEdges, new jedd.PhysicalDomain[...]); at /tmp/soot-t" +
                                                   "runk/src/soot/jimple/paddle/BDDReachableMethods.jedd:41,27-3" +
                                                   "7"),
                                                  jedd.internal.Jedd.v().project(newEdges,
                                                                                 new jedd.PhysicalDomain[] { FD.v(), T1.v(), V1.v(), ST.v() }));
            newTargets.eqMinus(reachables);
            out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                                        new jedd.PhysicalDomain[] { V1.v(), T1.v() },
                                                        ("out.add(jedd.internal.Jedd.v().replace(newTargets, new jedd." +
                                                         "PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /tmp/" +
                                                         "soot-trunk/src/soot/jimple/paddle/BDDReachableMethods.jedd:4" +
                                                         "3,12-15"),
                                                        jedd.internal.Jedd.v().replace(newTargets,
                                                                                       new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                                                                       new jedd.PhysicalDomain[] { V1.v(), T1.v() })));
            reachables.eqUnion(newTargets);
            newEdges.eq(cg.edgesOutOf(newMethods).get());
        }
    }
    
    void add(MethodOrMethodContext m) {
        final jedd.internal.RelationContainer newM =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.T2> newM = jedd.internal.Jedd.v().literal(n" +
                                               "ew java.lang.Object[...], new jedd.Attribute[...], new jedd." +
                                               "PhysicalDomain[...]); at /tmp/soot-trunk/src/soot/jimple/pad" +
                                               "dle/BDDReachableMethods.jedd:49,23-27"),
                                              jedd.internal.Jedd.v().literal(new Object[] { m.context(), m.method() },
                                                                             new jedd.Attribute[] { ctxt.v(), method.v() },
                                                                             new jedd.PhysicalDomain[] { V2.v(), T2.v() }));
        final jedd.internal.RelationContainer newReachables =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.T2> newReachables = jedd.internal.Jedd.v()." +
                                               "union(jedd.internal.Jedd.v().read(reachables), newM); at /tm" +
                                               "p/soot-trunk/src/soot/jimple/paddle/BDDReachableMethods.jedd" +
                                               ":50,23-36"),
                                              jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(reachables),
                                                                           newM));
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newReachables), reachables)) {
            reachables.eq(newReachables);
            out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                                        new jedd.PhysicalDomain[] { V1.v(), T1.v() },
                                                        ("out.add(jedd.internal.Jedd.v().replace(newM, new jedd.Physic" +
                                                         "alDomain[...], new jedd.PhysicalDomain[...])) at /tmp/soot-t" +
                                                         "runk/src/soot/jimple/paddle/BDDReachableMethods.jedd:53,12-1" +
                                                         "5"),
                                                        jedd.internal.Jedd.v().replace(newM,
                                                                                       new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                                                                       new jedd.PhysicalDomain[] { V1.v(), T1.v() })));
        }
    }
}
