package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;

public class BDDObjSensStaticContextManager extends AbsStaticContextManager {
    BDDObjSensStaticContextManager(Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) {
        super(in, out);
    }
    
    public boolean update() {
        final jedd.internal.RelationContainer newOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v(), tgtc.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), MT.v(), C2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bddd" +
                                               "omains.tgtm:soot.jimple.paddle.bdddomains.MT, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtc:soot.jimple.paddle.bdddomains.C2> newOu" +
                                               "t = jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().proje" +
                                               "ct(in.get(), new jedd.PhysicalDomain[...]), new jedd.Physica" +
                                               "lDomain[...], new jedd.PhysicalDomain[...]); at /home/resear" +
                                               "ch/ccl/olhota/soot-jedd/src/soot/jimple/paddle/BDDObjSensSta" +
                                               "ticContextManager.jedd:35,45-51"),
                                              jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().project(in.get(),
                                                                                                         new jedd.PhysicalDomain[] { C2.v() }),
                                                                          new jedd.PhysicalDomain[] { C1.v() },
                                                                          new jedd.PhysicalDomain[] { C2.v() }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtc.v(), tgtm.v(), srcc.v(), srcm.v(), kind.v(), stmt.v() },
                                                    new jedd.PhysicalDomain[] { C2.v(), MT.v(), C1.v(), MS.v(), KD.v(), ST.v() },
                                                    ("out.add(newOut) at /home/research/ccl/olhota/soot-jedd/src/s" +
                                                     "oot/jimple/paddle/BDDObjSensStaticContextManager.jedd:37,8-1" +
                                                     "1"),
                                                    newOut));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newOut), jedd.internal.Jedd.v().falseBDD());
    }
}
