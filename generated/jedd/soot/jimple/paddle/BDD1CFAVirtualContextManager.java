package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.*;

public class BDD1CFAVirtualContextManager extends AbsVirtualContextManager {
    BDD1CFAVirtualContextManager(Rctxt_var_obj_srcm_stmt_kind_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) {
        super(in, out);
    }
    
    public boolean update() {
        final jedd.internal.RelationContainer newOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.FD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.V2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T2> newOu" +
                                               "t = jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().repla" +
                                               "ce(jedd.internal.Jedd.v().project(in.get(), new jedd.Physica" +
                                               "lDomain[...]), new jedd.PhysicalDomain[...], new jedd.Physic" +
                                               "alDomain[...]), new jedd.PhysicalDomain[...], new jedd.Physi" +
                                               "calDomain[...]); at /home/olhotak/soot-trunk/src/soot/jimple" +
                                               "/paddle/BDD1CFAVirtualContextManager.jedd:35,45-51"),
                                              jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(in.get(),
                                                                                                                                        new jedd.PhysicalDomain[] { V1.v(), H1.v() }),
                                                                                                         new jedd.PhysicalDomain[] { V2.v() },
                                                                                                         new jedd.PhysicalDomain[] { V1.v() }),
                                                                          new jedd.PhysicalDomain[] { ST.v() },
                                                                          new jedd.PhysicalDomain[] { V2.v() }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v(), tgtm.v(), stmt.v(), tgtc.v(), srcm.v(), srcc.v() },
                                                    new jedd.PhysicalDomain[] { FD.v(), T2.v(), ST.v(), V2.v(), T1.v(), V1.v() },
                                                    ("out.add(newOut) at /home/olhotak/soot-trunk/src/soot/jimple/" +
                                                     "paddle/BDD1CFAVirtualContextManager.jedd:37,8-11"),
                                                    newOut));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newOut), jedd.internal.Jedd.v().falseBDD());
    }
}
