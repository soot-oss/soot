package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.*;

public class BDDInsensitiveVirtualContextManager extends AbsVirtualContextManager {
    BDDInsensitiveVirtualContextManager(Rctxt_var_obj_srcm_stmt_kind_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out) {
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
                                               "t = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(" +
                                               "jedd.internal.Jedd.v().project(in.get(), new jedd.PhysicalDo" +
                                               "main[...])), jedd.internal.Jedd.v().literal(new java.lang.Ob" +
                                               "ject[...], new jedd.Attribute[...], new jedd.PhysicalDomain[" +
                                               "...]), new jedd.PhysicalDomain[...]); at /home/olhotak/soot-" +
                                               "trunk/src/soot/jimple/paddle/BDDInsensitiveVirtualContextMan" +
                                               "ager.jedd:35,45-51"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(in.get(),
                                                                                                                                     new jedd.PhysicalDomain[] { V1.v(), V2.v(), H1.v() })),
                                                                          jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                         new jedd.Attribute[] { srcc.v(), tgtc.v() },
                                                                                                         new jedd.PhysicalDomain[] { V1.v(), V2.v() }),
                                                                          new jedd.PhysicalDomain[] {  }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), tgtc.v(), stmt.v(), srcm.v(), kind.v(), srcc.v() },
                                                    new jedd.PhysicalDomain[] { T2.v(), V2.v(), ST.v(), T1.v(), FD.v(), V1.v() },
                                                    ("out.add(newOut) at /home/olhotak/soot-trunk/src/soot/jimple/" +
                                                     "paddle/BDDInsensitiveVirtualContextManager.jedd:38,8-11"),
                                                    newOut));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newOut), jedd.internal.Jedd.v().falseBDD());
    }
}
