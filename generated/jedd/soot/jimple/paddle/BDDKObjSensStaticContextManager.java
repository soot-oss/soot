package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import jedd.*;
import java.util.*;

public class BDDKObjSensStaticContextManager extends AbsStaticContextManager {
    BDDKObjSensStaticContextManager(Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out, int k) {
        super(in, out);
    }
    
    public boolean update() {
        final jedd.internal.RelationContainer newEdges =
          new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.FD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.V2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T2> newEd" +
                                               "ges = jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().pro" +
                                               "ject(in.get(), new jedd.PhysicalDomain[...]), new jedd.Physi" +
                                               "calDomain[...], new jedd.PhysicalDomain[...]); at /home/olho" +
                                               "tak/soot-trunk2/src/soot/jimple/paddle/BDDKObjSensStaticCont" +
                                               "extManager.jedd:37,45-53"),
                                              jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().project(in.get(),
                                                                                                         new PhysicalDomain[] { V2.v() }),
                                                                          new PhysicalDomain[] { V1.v() },
                                                                          new PhysicalDomain[] { V2.v() }));
        out.add(new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), tgtm.v(), kind.v(), srcm.v(), tgtc.v(), stmt.v() },
                                                    new PhysicalDomain[] { V1.v(), T2.v(), FD.v(), T1.v(), V2.v(), ST.v() },
                                                    ("out.add(newEdges) at /home/olhotak/soot-trunk2/src/soot/jimp" +
                                                     "le/paddle/BDDKObjSensStaticContextManager.jedd:39,8-11"),
                                                    newEdges));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newEdges), jedd.internal.Jedd.v().falseBDD());
    }
}
