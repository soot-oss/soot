package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;

public class BDDObjSensStaticContextManager extends AbsStaticContextManager {
    BDDObjSensStaticContextManager(Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) {
        super(in, out);
    }
    
    public void update() {
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), tgtc.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                                    new jedd.PhysicalDomain[] { V1.v(), V2.v(), T1.v(), ST.v(), FD.v(), T2.v() },
                                                    ("out.add(jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().p" +
                                                     "roject(in.get(), new jedd.PhysicalDomain[...]), new jedd.Phy" +
                                                     "sicalDomain[...], new jedd.PhysicalDomain[...])) at /home/ol" +
                                                     "hotak/soot-trunk/src/soot/jimple/paddle/BDDObjSensStaticCont" +
                                                     "extManager.jedd:35,8-11"),
                                                    jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().project(in.get(),
                                                                                                               new jedd.PhysicalDomain[] { V2.v() }),
                                                                                new jedd.PhysicalDomain[] { V1.v() },
                                                                                new jedd.PhysicalDomain[] { V2.v() })));
    }
}
