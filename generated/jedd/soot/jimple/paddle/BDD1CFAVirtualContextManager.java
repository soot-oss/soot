package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.*;

public class BDD1CFAVirtualContextManager extends AbsVirtualContextManager {
    BDD1CFAVirtualContextManager(Rctxt_local_obj_srcm_stmt_kind_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) {
        super(in, out);
    }
    
    public void update() {
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), tgtc.v(), kind.v(), tgtm.v() },
                                                    new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), V2.v(), FD.v(), T2.v() },
                                                    ("out.add(jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().r" +
                                                     "eplace(jedd.internal.Jedd.v().project(in.get(), new jedd.Phy" +
                                                     "sicalDomain[...]), new jedd.PhysicalDomain[...], new jedd.Ph" +
                                                     "ysicalDomain[...]), new jedd.PhysicalDomain[...], new jedd.P" +
                                                     "hysicalDomain[...])) at /home/olhotak/soot-trunk/src/soot/ji" +
                                                     "mple/paddle/BDD1CFAVirtualContextManager.jedd:35,8-11"),
                                                    jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(in.get(),
                                                                                                                                              new jedd.PhysicalDomain[] { V1.v(), H1.v() }),
                                                                                                               new jedd.PhysicalDomain[] { V2.v() },
                                                                                                               new jedd.PhysicalDomain[] { V1.v() }),
                                                                                new jedd.PhysicalDomain[] { ST.v() },
                                                                                new jedd.PhysicalDomain[] { V2.v() })));
    }
}
