package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;

public class BDDEdgeContextStripper extends AbsEdgeContextStripper {
    BDDEdgeContextStripper(Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) { super(in, out); }
    
    private final jedd.internal.RelationContainer seen =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcm, soot.jimple.pad" +
                                           "dle.bdddomains.stmt, soot.jimple.paddle.bdddomains.kind, soo" +
                                           "t.jimple.paddle.bdddomains.tgtm> seen = jedd.internal.Jedd.v" +
                                           "().falseBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/pad" +
                                           "dle/BDDEdgeContextStripper.jedd:33,12-36"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public boolean update() {
        final jedd.internal.RelationContainer newEdges =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcm:soot.jimple.paddle.bdddo" +
                                               "mains.MS, soot.jimple.paddle.bdddomains.stmt:soot.jimple.pad" +
                                               "dle.bdddomains.ST, soot.jimple.paddle.bdddomains.kind:soot.j" +
                                               "imple.paddle.bdddomains.KD, soot.jimple.paddle.bdddomains.tg" +
                                               "tm:soot.jimple.paddle.bdddomains.MT> newEdges = jedd.interna" +
                                               "l.Jedd.v().project(in.get(), new jedd.PhysicalDomain[...]); " +
                                               "at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDEdgeCon" +
                                               "textStripper.jedd:35,33-41"),
                                              jedd.internal.Jedd.v().project(in.get(),
                                                                             new jedd.PhysicalDomain[] { C2.v(), C1.v() }));
        newEdges.eqMinus(seen);
        seen.eqUnion(newEdges);
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), tgtc.v(), srcc.v() },
                                                    new jedd.PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v(), C2.v(), C1.v() },
                                                    ("out.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                                     "ead(newEdges), jedd.internal.Jedd.v().literal(new java.lang." +
                                                     "Object[...], new jedd.Attribute[...], new jedd.PhysicalDomai" +
                                                     "n[...]), new jedd.PhysicalDomain[...])) at /tmp/olhotak/soot" +
                                                     "-trunk/src/soot/jimple/paddle/BDDEdgeContextStripper.jedd:38" +
                                                     ",8-11"),
                                                    jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newEdges),
                                                                                jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                               new jedd.Attribute[] { srcc.v(), tgtc.v() },
                                                                                                               new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                new jedd.PhysicalDomain[] {  })));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newEdges), jedd.internal.Jedd.v().falseBDD());
    }
}
