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
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), T2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.FD, soot.jimple.paddle.bddd" +
                                               "omains.tgtm:soot.jimple.paddle.bdddomains.T2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtc:soot.jimple.paddle.bdddomains.V2> newOu" +
                                               "t = jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().proje" +
                                               "ct(in.get(), new jedd.PhysicalDomain[...]), new jedd.Physica" +
                                               "lDomain[...], new jedd.PhysicalDomain[...]); at /home/olhota" +
                                               "k/soot-trunk2/src/soot/jimple/paddle/BDDObjSensStaticContext" +
                                               "Manager.jedd:35,45-51"),
                                              jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().project(in.get(),
                                                                                                         new jedd.PhysicalDomain[] { V2.v() }),
                                                                          new jedd.PhysicalDomain[] { V1.v() },
                                                                          new jedd.PhysicalDomain[] { V2.v() }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), tgtm.v(), kind.v(), srcm.v(), tgtc.v(), stmt.v() },
                                                    new jedd.PhysicalDomain[] { V1.v(), T2.v(), FD.v(), T1.v(), V2.v(), ST.v() },
                                                    ("out.add(newOut) at /home/olhotak/soot-trunk2/src/soot/jimple" +
                                                     "/paddle/BDDObjSensStaticContextManager.jedd:37,8-11"),
                                                    newOut));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newOut), jedd.internal.Jedd.v().falseBDD());
    }
}
