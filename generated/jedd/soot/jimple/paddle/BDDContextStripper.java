package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.*;

public class BDDContextStripper extends AbsContextStripper {
    BDDContextStripper(Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) { super(in, out); }
    
    void update() {
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v(), tgtm.v(), srcm.v(), stmt.v(), tgtc.v(), srcc.v() },
                                                    new jedd.PhysicalDomain[] { FD.v(), T2.v(), T1.v(), ST.v(), V2.v(), V1.v() },
                                                    ("out.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                                     "ead(jedd.internal.Jedd.v().project(in.get(), new jedd.Physic" +
                                                     "alDomain[...])), jedd.internal.Jedd.v().literal(new java.lan" +
                                                     "g.Object[...], new jedd.Attribute[...], new jedd.PhysicalDom" +
                                                     "ain[...]), new jedd.PhysicalDomain[...])) at /tmp/soot-trunk" +
                                                     "/src/soot/jimple/paddle/BDDContextStripper.jedd:35,8-11"),
                                                    jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(in.get(),
                                                                                                                                           new jedd.PhysicalDomain[] { V2.v(), V1.v() })),
                                                                                jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                               new jedd.Attribute[] { srcc.v(), tgtc.v() },
                                                                                                               new jedd.PhysicalDomain[] { V1.v(), V2.v() }),
                                                                                new jedd.PhysicalDomain[] {  })));
    }
}
