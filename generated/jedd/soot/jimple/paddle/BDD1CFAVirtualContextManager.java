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
                                              new jedd.PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.C2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT> newOu" +
                                               "t = jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().proje" +
                                               "ct(in.get(), new jedd.PhysicalDomain[...]), new jedd.Physica" +
                                               "lDomain[...], new jedd.PhysicalDomain[...]); at /home/resear" +
                                               "ch/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/BDD1" +
                                               "CFAVirtualContextManager.jedd:35,45-51"),
                                              jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().project(in.get(),
                                                                                                         new jedd.PhysicalDomain[] { V1.v(), H1.v() }),
                                                                          new jedd.PhysicalDomain[] { ST.v() },
                                                                          new jedd.PhysicalDomain[] { C2.v() }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v(), srcm.v(), stmt.v(), srcc.v(), tgtc.v(), tgtm.v() },
                                                    new jedd.PhysicalDomain[] { KD.v(), MS.v(), ST.v(), C1.v(), C2.v(), MT.v() },
                                                    ("out.add(newOut) at /home/research/ccl/olhota/olhotak/soot-tr" +
                                                     "unk/src/soot/jimple/paddle/BDD1CFAVirtualContextManager.jedd" +
                                                     ":37,8-11"),
                                                    newOut));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newOut), jedd.internal.Jedd.v().falseBDD());
    }
}
