package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.*;

public class BDDReachableMethods extends AbsReachableMethods {
    private final jedd.internal.RelationContainer reachables =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                          new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.method> reachables at /home/research/ccl/olho" +
                                           "ta/soot-trunk/src/soot/jimple/paddle/BDDReachableMethods.jed" +
                                           "d:30,12-26"));
    
    private AbsCallGraph cg;
    
    private Rctxt_method newMethods;
    
    BDDReachableMethods(Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn,
                        Rctxt_method methodsIn,
                        Qctxt_method out,
                        AbsCallGraph cg) {
        super(edgesIn, methodsIn, out);
        this.cg = cg;
        newMethods = out.reader("bddrm");
    }
    
    boolean update() {
        boolean change = false;
        if (methodsIn != null) {
            final jedd.internal.RelationContainer newMethodsIn =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                                  new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                                  ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                                   "mains.V2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                                   "addle.bdddomains.T2> newMethodsIn = jedd.internal.Jedd.v().r" +
                                                   "eplace(methodsIn.get(), new jedd.PhysicalDomain[...], new je" +
                                                   "dd.PhysicalDomain[...]); at /home/research/ccl/olhota/soot-t" +
                                                   "runk/src/soot/jimple/paddle/BDDReachableMethods.jedd:42,27-3" +
                                                   "9"),
                                                  jedd.internal.Jedd.v().replace(methodsIn.get(),
                                                                                 new jedd.PhysicalDomain[] { V1.v(), T1.v() },
                                                                                 new jedd.PhysicalDomain[] { V2.v(), T2.v() }));
            newMethodsIn.eqMinus(reachables);
            if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newMethodsIn),
                                               jedd.internal.Jedd.v().falseBDD()))
                change = true;
            reachables.eqUnion(newMethodsIn);
            out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                        new jedd.PhysicalDomain[] { T1.v(), V1.v() },
                                                        ("out.add(jedd.internal.Jedd.v().replace(newMethodsIn, new jed" +
                                                         "d.PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /ho" +
                                                         "me/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDD" +
                                                         "ReachableMethods.jedd:46,12-15"),
                                                        jedd.internal.Jedd.v().replace(newMethodsIn,
                                                                                       new jedd.PhysicalDomain[] { T2.v(), V2.v() },
                                                                                       new jedd.PhysicalDomain[] { T1.v(), V1.v() })));
        }
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
                                               "d(edgesIn.get()), jedd.internal.Jedd.v().replace(reachables," +
                                               " new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...])" +
                                               ", new jedd.PhysicalDomain[...]); at /home/research/ccl/olhot" +
                                               "a/soot-trunk/src/soot/jimple/paddle/BDDReachableMethods.jedd" +
                                               ":49,45-53"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(edgesIn.get()),
                                                                          jedd.internal.Jedd.v().replace(reachables,
                                                                                                         new jedd.PhysicalDomain[] { T2.v(), V2.v() },
                                                                                                         new jedd.PhysicalDomain[] { T1.v(), V1.v() }),
                                                                          new jedd.PhysicalDomain[] { V1.v(), T1.v() }));
        newEdges.eqUnion(cg.edgesOutOf(newMethods).get());
        while (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newEdges),
                                              jedd.internal.Jedd.v().falseBDD())) {
            final jedd.internal.RelationContainer newTargets =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                                  new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                                  ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                                   "mains.V2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                                   "addle.bdddomains.T2> newTargets = jedd.internal.Jedd.v().pro" +
                                                   "ject(newEdges, new jedd.PhysicalDomain[...]); at /home/resea" +
                                                   "rch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDReachabl" +
                                                   "eMethods.jedd:55,27-37"),
                                                  jedd.internal.Jedd.v().project(newEdges,
                                                                                 new jedd.PhysicalDomain[] { T1.v(), ST.v(), V1.v(), FD.v() }));
            newTargets.eqMinus(reachables);
            out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                        new jedd.PhysicalDomain[] { T1.v(), V1.v() },
                                                        ("out.add(jedd.internal.Jedd.v().replace(newTargets, new jedd." +
                                                         "PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /home" +
                                                         "/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDRe" +
                                                         "achableMethods.jedd:58,12-15"),
                                                        jedd.internal.Jedd.v().replace(newTargets,
                                                                                       new jedd.PhysicalDomain[] { T2.v(), V2.v() },
                                                                                       new jedd.PhysicalDomain[] { T1.v(), V1.v() })));
            if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newTargets),
                                               jedd.internal.Jedd.v().falseBDD()))
                change = true;
            reachables.eqUnion(newTargets);
            newEdges.eq(cg.edgesOutOf(newMethods).get());
        }
        return change;
    }
    
    boolean add(MethodOrMethodContext m) {
        final jedd.internal.RelationContainer newM =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.T1> newM = jedd.internal.Jedd.v().literal(n" +
                                               "ew java.lang.Object[...], new jedd.Attribute[...], new jedd." +
                                               "PhysicalDomain[...]); at /home/research/ccl/olhota/soot-trun" +
                                               "k/src/soot/jimple/paddle/BDDReachableMethods.jedd:66,23-27"),
                                              jedd.internal.Jedd.v().literal(new Object[] { m.context(), m.method() },
                                                                             new jedd.Attribute[] { ctxt.v(), method.v() },
                                                                             new jedd.PhysicalDomain[] { V1.v(), T1.v() }));
        final jedd.internal.RelationContainer newReachables =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.T2> newReachables = jedd.internal.Jedd.v()." +
                                               "replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v()." +
                                               "read(jedd.internal.Jedd.v().replace(reachables, new jedd.Phy" +
                                               "sicalDomain[...], new jedd.PhysicalDomain[...])), newM), new" +
                                               " jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]); at" +
                                               " /home/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle" +
                                               "/BDDReachableMethods.jedd:67,23-36"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(reachables,
                                                                                                                                                                     new jedd.PhysicalDomain[] { T2.v(), V2.v() },
                                                                                                                                                                     new jedd.PhysicalDomain[] { T1.v(), V1.v() })),
                                                                                                          newM),
                                                                             new jedd.PhysicalDomain[] { T1.v(), V1.v() },
                                                                             new jedd.PhysicalDomain[] { T2.v(), V2.v() }));
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newReachables), reachables)) {
            reachables.eq(newReachables);
            out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                        new jedd.PhysicalDomain[] { T1.v(), V1.v() },
                                                        ("out.add(newM) at /home/research/ccl/olhota/soot-trunk/src/so" +
                                                         "ot/jimple/paddle/BDDReachableMethods.jedd:70,12-15"),
                                                        newM));
            return true;
        }
        return false;
    }
    
    int size() {
        return (int)
                 new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                     new jedd.PhysicalDomain[] { T2.v(), V2.v() },
                                                     ("reachables.size() at /home/research/ccl/olhota/soot-trunk/sr" +
                                                      "c/soot/jimple/paddle/BDDReachableMethods.jedd:76,21-31"),
                                                     reachables).size();
    }
    
    boolean contains(MethodOrMethodContext m) {
        final jedd.internal.RelationContainer newM =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.T2> newM = jedd.internal.Jedd.v().literal(n" +
                                               "ew java.lang.Object[...], new jedd.Attribute[...], new jedd." +
                                               "PhysicalDomain[...]); at /home/research/ccl/olhota/soot-trun" +
                                               "k/src/soot/jimple/paddle/BDDReachableMethods.jedd:79,23-27"),
                                              jedd.internal.Jedd.v().literal(new Object[] { m.context(), m.method() },
                                                                             new jedd.Attribute[] { ctxt.v(), method.v() },
                                                                             new jedd.PhysicalDomain[] { V2.v(), T2.v() }));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(reachables),
                                                                                                           newM)),
                                              jedd.internal.Jedd.v().falseBDD());
    }
}
