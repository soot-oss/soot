package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import jedd.*;
import java.util.*;

public class BDDKCFAStaticContextManager extends AbsStaticContextManager {
    BDDKCFAStaticContextManager(Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out, int k) {
        super(in, out);
        this.k = k;
    }
    
    private Jedd.Shifter shifter;
    
    private int k;
    
    public boolean update() {
        if (shifter == null) {
            int[] from = new int[(k - 1) * ContextStringNumberer.SHIFT_WIDTH];
            int[] to = new int[(k - 1) * ContextStringNumberer.SHIFT_WIDTH];
            for (int i = 0; i < from.length; i++) {
                from[i] = i + C1.v().firstBit();
                to[i] = i + C2.v().firstBit() + ContextStringNumberer.SHIFT_WIDTH;
            }
            shifter = Jedd.v().makeShifter(from, to);
        }
        final jedd.internal.RelationContainer newEdges =
          new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new PhysicalDomain[] { C1.v(), T1.v(), ST.v(), FD.v(), C2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.FD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.C2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T2> newEd" +
                                               "ges = jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().rep" +
                                               "lace(jedd.internal.Jedd.v().project(jedd.internal.Jedd.v().r" +
                                               "eplace(in.get(), new jedd.PhysicalDomain[...], new jedd.Phys" +
                                               "icalDomain[...]), new jedd.PhysicalDomain[...]), new jedd.Ph" +
                                               "ysicalDomain[...], new jedd.PhysicalDomain[...]), new jedd.P" +
                                               "hysicalDomain[...], new jedd.PhysicalDomain[...]); at /home/" +
                                               "olhotak/soot-trunk/src/soot/jimple/paddle/BDDKCFAStaticConte" +
                                               "xtManager.jedd:48,51-59"),
                                              jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(jedd.internal.Jedd.v().replace(in.get(),
                                                                                                                                                                       new PhysicalDomain[] { ST.v() },
                                                                                                                                                                       new PhysicalDomain[] { C2.v() }),
                                                                                                                                        new PhysicalDomain[] { V2.v() }),
                                                                                                         new PhysicalDomain[] { V1.v() },
                                                                                                         new PhysicalDomain[] { C1.v() }),
                                                                          new PhysicalDomain[] { C2.v() },
                                                                          new PhysicalDomain[] { ST.v() }));
        newEdges.eq(jedd.internal.Jedd.v().cast((jedd.internal.RelationContainer)
                                                  new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), kind.v(), stmt.v(), srcc.v(), tgtm.v(), tgtc.v() },
                                                                                      new PhysicalDomain[] { T1.v(), FD.v(), ST.v(), C1.v(), T2.v(), C2.v() },
                                                                                      ("newEdges.applyShifter(shifter) at /home/olhotak/soot-trunk/s" +
                                                                                       "rc/soot/jimple/paddle/BDDKCFAStaticContextManager.jedd:51,12" +
                                                                                       "-20"),
                                                                                      newEdges).applyShifter(shifter),
                                                new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                                new PhysicalDomain[] { C1.v(), T1.v(), ST.v(), FD.v(), C2.v(), T2.v() }));
        out.add(new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), kind.v(), stmt.v(), srcc.v(), tgtm.v(), tgtc.v() },
                                                    new PhysicalDomain[] { T1.v(), FD.v(), ST.v(), V1.v(), T2.v(), V2.v() },
                                                    ("out.add(jedd.internal.Jedd.v().replace(newEdges, new jedd.Ph" +
                                                     "ysicalDomain[...], new jedd.PhysicalDomain[...])) at /home/o" +
                                                     "lhotak/soot-trunk/src/soot/jimple/paddle/BDDKCFAStaticContex" +
                                                     "tManager.jedd:52,8-11"),
                                                    jedd.internal.Jedd.v().replace(newEdges,
                                                                                   new PhysicalDomain[] { C1.v(), C2.v() },
                                                                                   new PhysicalDomain[] { V1.v(), V2.v() })));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newEdges), jedd.internal.Jedd.v().falseBDD());
    }
}
