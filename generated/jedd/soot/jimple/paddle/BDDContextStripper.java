package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.*;

public class BDDContextStripper extends AbsContextStripper {
    BDDContextStripper(Rctxt_method in, Qctxt_method out) { super(in, out); }
    
    boolean update() {
        final jedd.internal.RelationContainer answer =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.T1> answer = jedd.internal.Jedd.v().join(je" +
                                               "dd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(in." +
                                               "get(), new jedd.PhysicalDomain[...])), jedd.internal.Jedd.v(" +
                                               ").literal(new java.lang.Object[...], new jedd.Attribute[...]" +
                                               ", new jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[..." +
                                               "]); at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/" +
                                               "paddle/BDDContextStripper.jedd:35,23-29"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(in.get(),
                                                                                                                                     new jedd.PhysicalDomain[] { V1.v() })),
                                                                          jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                         new jedd.Attribute[] { ctxt.v() },
                                                                                                         new jedd.PhysicalDomain[] { V1.v() }),
                                                                          new jedd.PhysicalDomain[] {  }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                    new jedd.PhysicalDomain[] { T1.v(), V1.v() },
                                                    ("out.add(answer) at /home/research/ccl/olhota/soot-trunk/src/" +
                                                     "soot/jimple/paddle/BDDContextStripper.jedd:36,8-11"),
                                                    answer));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(answer), jedd.internal.Jedd.v().falseBDD());
    }
}
