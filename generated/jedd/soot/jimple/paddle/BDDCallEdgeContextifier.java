package soot.jimple.paddle;

import soot.*;
import soot.util.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public class BDDCallEdgeContextifier extends AbsCallEdgeContextifier {
    private BDDNodeInfo ni;
    
    public BDDCallEdgeContextifier(BDDNodeInfo ni,
                                   Rsrcm_stmt_kind_tgtm_src_dst parms,
                                   Rsrcm_stmt_kind_tgtm_src_dst rets,
                                   Rsrcc_srcm_stmt_kind_tgtc_tgtm calls,
                                   Qsrcc_src_dstc_dst csimple) {
        super(parms, rets, calls, csimple);
        this.ni = ni;
    }
    
    public boolean update() {
        final jedd.internal.RelationContainer simpleOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V1.v(), C1.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.dstc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.dst" +
                                               ":soot.jimple.paddle.bdddomains.V2> simpleOut = jedd.internal" +
                                               ".Jedd.v().falseBDD(); at /tmp/soot-trunk-saved/src/soot/jimp" +
                                               "le/paddle/BDDCallEdgeContextifier.jedd:47,31-40"),
                                              jedd.internal.Jedd.v().falseBDD());
        allParms.eqUnion(parms.get());
        allRets.eqUnion(rets.get());
        final jedd.internal.RelationContainer newCalls =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.C2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT> newCa" +
                                               "lls = calls.get(); at /tmp/soot-trunk-saved/src/soot/jimple/" +
                                               "paddle/BDDCallEdgeContextifier.jedd:52,45-53"),
                                              calls.get());
        simpleOut.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(newCalls),
                                                                                        allParms,
                                                                                        new jedd.PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v() }),
                                                         new jedd.PhysicalDomain[] { C1.v(), C2.v() },
                                                         new jedd.PhysicalDomain[] { C2.v(), C1.v() }));
        simpleOut.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(newCalls),
                                                         allRets,
                                                         new jedd.PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v() }));
        final jedd.internal.RelationContainer globalDsts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V1.v(), C1.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.dstc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.dst" +
                                               ":soot.jimple.paddle.bdddomains.V2> globalDsts = jedd.interna" +
                                               "l.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut), ni.g" +
                                               "lobalSet(), new jedd.PhysicalDomain[...]); at /tmp/soot-trun" +
                                               "k-saved/src/soot/jimple/paddle/BDDCallEdgeContextifier.jedd:" +
                                               "61,31-41"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut),
                                                                          ni.globalSet(),
                                                                          new jedd.PhysicalDomain[] { V2.v() }));
        simpleOut.eqMinus(globalDsts);
        simpleOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalDsts,
                                                                                                                 new jedd.PhysicalDomain[] { C1.v() })),
                                                      jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                     new jedd.Attribute[] { dstc.v() },
                                                                                     new jedd.PhysicalDomain[] { C1.v() }),
                                                      new jedd.PhysicalDomain[] {  }));
        final jedd.internal.RelationContainer globalSrcs =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V1.v(), C1.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.dstc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.dst" +
                                               ":soot.jimple.paddle.bdddomains.V2> globalSrcs = jedd.interna" +
                                               "l.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut), jedd" +
                                               ".internal.Jedd.v().replace(ni.globalSet(), new jedd.Physical" +
                                               "Domain[...], new jedd.PhysicalDomain[...]), new jedd.Physica" +
                                               "lDomain[...]); at /tmp/soot-trunk-saved/src/soot/jimple/padd" +
                                               "le/BDDCallEdgeContextifier.jedd:64,31-41"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut),
                                                                          jedd.internal.Jedd.v().replace(ni.globalSet(),
                                                                                                         new jedd.PhysicalDomain[] { V2.v() },
                                                                                                         new jedd.PhysicalDomain[] { V1.v() }),
                                                                          new jedd.PhysicalDomain[] { V1.v() }));
        simpleOut.eqMinus(globalSrcs);
        simpleOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalSrcs,
                                                                                                                 new jedd.PhysicalDomain[] { C2.v() })),
                                                      jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                     new jedd.Attribute[] { srcc.v() },
                                                                                     new jedd.PhysicalDomain[] { C2.v() }),
                                                      new jedd.PhysicalDomain[] {  }));
        csimple.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), dst.v(), dstc.v(), src.v() },
                                                        new jedd.PhysicalDomain[] { C1.v(), V2.v(), C2.v(), V1.v() },
                                                        ("csimple.add(jedd.internal.Jedd.v().replace(simpleOut, new je" +
                                                         "dd.PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /t" +
                                                         "mp/soot-trunk-saved/src/soot/jimple/paddle/BDDCallEdgeContex" +
                                                         "tifier.jedd:67,8-15"),
                                                        jedd.internal.Jedd.v().replace(simpleOut,
                                                                                       new jedd.PhysicalDomain[] { C2.v(), C1.v() },
                                                                                       new jedd.PhysicalDomain[] { C1.v(), C2.v() })));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(simpleOut),
                                              jedd.internal.Jedd.v().falseBDD());
    }
    
    private final jedd.internal.RelationContainer allParms =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v(), V1.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcm, soot.jimple.pad" +
                                           "dle.bdddomains.stmt, soot.jimple.paddle.bdddomains.kind, soo" +
                                           "t.jimple.paddle.bdddomains.tgtm, soot.jimple.paddle.bdddomai" +
                                           "ns.src, soot.jimple.paddle.bdddomains.dst> allParms = jedd.i" +
                                           "nternal.Jedd.v().falseBDD() at /tmp/soot-trunk-saved/src/soo" +
                                           "t/jimple/paddle/BDDCallEdgeContextifier.jedd:72,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allRets =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v(), V1.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcm, soot.jimple.pad" +
                                           "dle.bdddomains.stmt, soot.jimple.paddle.bdddomains.kind, soo" +
                                           "t.jimple.paddle.bdddomains.tgtm, soot.jimple.paddle.bdddomai" +
                                           "ns.src, soot.jimple.paddle.bdddomains.dst> allRets = jedd.in" +
                                           "ternal.Jedd.v().falseBDD() at /tmp/soot-trunk-saved/src/soot" +
                                           "/jimple/paddle/BDDCallEdgeContextifier.jedd:73,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
}
