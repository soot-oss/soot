package soot.jimple.paddle;

import soot.*;
import soot.jimple.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public class BDDCflow {
    public BDDCflow() {
        super();
        stmtMethod.eq(jedd.internal.Jedd.v().falseBDD());
        for (Iterator scIt = Scene.v().getApplicationClasses().iterator(); scIt.hasNext(); ) {
            final SootClass sc = (SootClass) scIt.next();
            for (Iterator mIt = sc.getMethods().iterator(); mIt.hasNext(); ) {
                final SootMethod m = (SootMethod) mIt.next();
                if (m.hasActiveBody()) {
                    for (Iterator sIt = m.getActiveBody().getUnits().iterator(); sIt.hasNext(); ) {
                        final Stmt s = (Stmt) sIt.next();
                        Scene.v().getUnitNumberer().add(s);
                        stmtMethod.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { s, m },
                                                                          new jedd.Attribute[] { stmt.v(), method.v() },
                                                                          new jedd.PhysicalDomain[] { ST.v(), MT.v() }));
                    }
                }
            }
        }
        final jedd.internal.RelationContainer wantedKinds =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v() },
                                              new jedd.PhysicalDomain[] { KD.v() },
                                              ("<soot.jimple.paddle.bdddomains.kind:soot.jimple.paddle.bdddo" +
                                               "mains.KD> wantedKinds = jedd.internal.Jedd.v().union(jedd.in" +
                                               "ternal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.inter" +
                                               "nal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal" +
                                               ".Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Je" +
                                               "dd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd." +
                                               "v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v()" +
                                               ".read(jedd.internal.Jedd.v().literal(new java.lang.Object[.." +
                                               ".], new jedd.Attribute[...], new jedd.PhysicalDomain[...]))," +
                                               " jedd.internal.Jedd.v().literal(new java.lang.Object[...], n" +
                                               "ew jedd.Attribute[...], new jedd.PhysicalDomain[...]))), jed" +
                                               "d.internal.Jedd.v().literal(new java.lang.Object[...], new j" +
                                               "edd.Attribute[...], new jedd.PhysicalDomain[...]))), jedd.in" +
                                               "ternal.Jedd.v().literal(new java.lang.Object[...], new jedd." +
                                               "Attribute[...], new jedd.PhysicalDomain[...]))), jedd.intern" +
                                               "al.Jedd.v().literal(new java.lang.Object[...], new jedd.Attr" +
                                               "ibute[...], new jedd.PhysicalDomain[...]))), jedd.internal.J" +
                                               "edd.v().literal(new java.lang.Object[...], new jedd.Attribut" +
                                               "e[...], new jedd.PhysicalDomain[...]))), jedd.internal.Jedd." +
                                               "v().literal(new java.lang.Object[...], new jedd.Attribute[.." +
                                               ".], new jedd.PhysicalDomain[...])); at /home/research/ccl/ol" +
                                               "hota/soot-trunk/src/soot/jimple/paddle/BDDCflow.jedd:47,15-2" +
                                               "6"),
                                              jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { Kind.STATIC },
                                                                                                                                                                                                                                                                                                                                                                                                                                   new jedd.Attribute[] { kind.v() },
                                                                                                                                                                                                                                                                                                                                                                                                                                   new jedd.PhysicalDomain[] { KD.v() })),
                                                                                                                                                                                                                                                                                                                                                                        jedd.internal.Jedd.v().literal(new Object[] { Kind.VIRTUAL },
                                                                                                                                                                                                                                                                                                                                                                                                       new jedd.Attribute[] { kind.v() },
                                                                                                                                                                                                                                                                                                                                                                                                       new jedd.PhysicalDomain[] { KD.v() }))),
                                                                                                                                                                                                                                                                                                               jedd.internal.Jedd.v().literal(new Object[] { Kind.INTERFACE },
                                                                                                                                                                                                                                                                                                                                              new jedd.Attribute[] { kind.v() },
                                                                                                                                                                                                                                                                                                                                              new jedd.PhysicalDomain[] { KD.v() }))),
                                                                                                                                                                                                                                                      jedd.internal.Jedd.v().literal(new Object[] { Kind.SPECIAL },
                                                                                                                                                                                                                                                                                     new jedd.Attribute[] { kind.v() },
                                                                                                                                                                                                                                                                                     new jedd.PhysicalDomain[] { KD.v() }))),
                                                                                                                                                                                             jedd.internal.Jedd.v().literal(new Object[] { Kind.CLINIT },
                                                                                                                                                                                                                            new jedd.Attribute[] { kind.v() },
                                                                                                                                                                                                                            new jedd.PhysicalDomain[] { KD.v() }))),
                                                                                                                                    jedd.internal.Jedd.v().literal(new Object[] { Kind.PRIVILEGED },
                                                                                                                                                                   new jedd.Attribute[] { kind.v() },
                                                                                                                                                                   new jedd.PhysicalDomain[] { KD.v() }))),
                                                                           jedd.internal.Jedd.v().literal(new Object[] { Kind.NEWINSTANCE },
                                                                                                          new jedd.Attribute[] { kind.v() },
                                                                                                          new jedd.PhysicalDomain[] { KD.v() })));
        callGraph.eq(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(PaddleScene.v().cg.edges().get(),
                                                                                                               new jedd.PhysicalDomain[] { C1.v(), C2.v() })),
                                                    wantedKinds,
                                                    new jedd.PhysicalDomain[] { KD.v() }));
    }
    
    private final jedd.internal.RelationContainer stmtMethod =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), method.v() },
                                          new jedd.PhysicalDomain[] { ST.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.stmt, soot.jimple.pad" +
                                           "dle.bdddomains.method> stmtMethod at /home/research/ccl/olho" +
                                           "ta/soot-trunk/src/soot/jimple/paddle/BDDCflow.jedd:59,12-26"));
    
    jedd.internal.RelationContainer stmtMethod() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), stmt.v() },
                                                   new jedd.PhysicalDomain[] { MT.v(), ST.v() },
                                                   ("return stmtMethod; at /home/research/ccl/olhota/soot-trunk/s" +
                                                    "rc/soot/jimple/paddle/BDDCflow.jedd:60,34-40"),
                                                   stmtMethod);
    }
    
    private final jedd.internal.RelationContainer callGraph =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), stmt.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), ST.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcm, soot.jimple.pad" +
                                           "dle.bdddomains.stmt, soot.jimple.paddle.bdddomains.tgtm> cal" +
                                           "lGraph at /home/research/ccl/olhota/soot-trunk/src/soot/jimp" +
                                           "le/paddle/BDDCflow.jedd:61,12-30"));
    
    jedd.internal.RelationContainer callGraph() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), srcm.v(), stmt.v() },
                                                   new jedd.PhysicalDomain[] { MT.v(), MS.v(), ST.v() },
                                                   ("return callGraph; at /home/research/ccl/olhota/soot-trunk/sr" +
                                                    "c/soot/jimple/paddle/BDDCflow.jedd:62,37-43"),
                                                   callGraph);
    }
}
