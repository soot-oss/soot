package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;

public class BDDInsensitiveStaticContextManager extends AbsStaticContextManager {
    BDDInsensitiveStaticContextManager(Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) {
        super(in, out);
    }
    
    public void update() {
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                                    new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), V2.v(), T2.v() },
                                                    ("out.add(in.get()) at /tmp/soot-trunk/src/soot/jimple/paddle/" +
                                                     "BDDInsensitiveStaticContextManager.jedd:34,8-11"),
                                                    in.get()));
    }
}
