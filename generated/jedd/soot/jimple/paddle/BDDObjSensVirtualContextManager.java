package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.*;

public class BDDObjSensVirtualContextManager extends AbsVirtualContextManager {
    BDDObjSensVirtualContextManager(Rctxt_local_obj_srcm_stmt_kind_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) {
        super(in, out);
    }
    
    public void update() {
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), tgtc.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                                    new jedd.PhysicalDomain[] { V1.v(), V2.v(), T1.v(), ST.v(), FD.v(), T2.v() },
                                                    ("out.add(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v(" +
                                                     ").project(in.get(), new jedd.PhysicalDomain[...]), new jedd." +
                                                     "PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /home" +
                                                     "/olhotak/soot-trunk/src/soot/jimple/paddle/BDDObjSensVirtual" +
                                                     "ContextManager.jedd:35,8-11"),
                                                    jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(in.get(),
                                                                                                                  new jedd.PhysicalDomain[] { V1.v() }),
                                                                                   new jedd.PhysicalDomain[] { V2.v(), H1.v() },
                                                                                   new jedd.PhysicalDomain[] { V1.v(), V2.v() })));
    }
}
