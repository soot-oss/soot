package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.*;

public class BDDObjSensVirtualContextManager extends AbsVirtualContextManager {
    BDDObjSensVirtualContextManager(Rctxt_var_obj_srcm_stmt_kind_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) {
        super(in, out);
    }
    
    public boolean update() {
        final jedd.internal.RelationContainer newOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v(), tgtc.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), T2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.FD, soot.jimple.paddle.bddd" +
                                               "omains.tgtm:soot.jimple.paddle.bdddomains.T2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtc:soot.jimple.paddle.bdddomains.H1> newOu" +
                                               "t = jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().pr" +
                                               "oject(in.get(), new jedd.PhysicalDomain[...]), new jedd.Phys" +
                                               "icalDomain[...], new jedd.PhysicalDomain[...]); at /home/olh" +
                                               "otak/soot-trunk/src/soot/jimple/paddle/BDDObjSensVirtualCont" +
                                               "extManager.jedd:35,45-51"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(in.get(),
                                                                                                            new jedd.PhysicalDomain[] { V1.v() }),
                                                                             new jedd.PhysicalDomain[] { V2.v() },
                                                                             new jedd.PhysicalDomain[] { V1.v() }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtc.v(), srcc.v(), tgtm.v(), kind.v(), stmt.v(), srcm.v() },
                                                    new jedd.PhysicalDomain[] { V2.v(), V1.v(), T2.v(), FD.v(), ST.v(), T1.v() },
                                                    ("out.add(jedd.internal.Jedd.v().replace(newOut, new jedd.Phys" +
                                                     "icalDomain[...], new jedd.PhysicalDomain[...])) at /home/olh" +
                                                     "otak/soot-trunk/src/soot/jimple/paddle/BDDObjSensVirtualCont" +
                                                     "extManager.jedd:37,8-11"),
                                                    jedd.internal.Jedd.v().replace(newOut,
                                                                                   new jedd.PhysicalDomain[] { H1.v() },
                                                                                   new jedd.PhysicalDomain[] { V2.v() })));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newOut), jedd.internal.Jedd.v().falseBDD());
    }
}
