package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import jedd.*;
import java.util.*;
import soot.*;

public class BDDKObjSensVirtualContextManager extends AbsVirtualContextManager {
    BDDKObjSensVirtualContextManager(Rctxt_var_obj_srcm_stmt_kind_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out, int k) {
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
                                              new PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.C2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT> newEd" +
                                               "ges = jedd.internal.Jedd.v().project(jedd.internal.Jedd.v()." +
                                               "replace(in.get(), new jedd.PhysicalDomain[...], new jedd.Phy" +
                                               "sicalDomain[...]), new jedd.PhysicalDomain[...]); at /tmp/ol" +
                                               "hotak/soot-trunk/src/soot/jimple/paddle/BDDKObjSensVirtualCo" +
                                               "ntextManager.jedd:49,51-59"),
                                              jedd.internal.Jedd.v().project(jedd.internal.Jedd.v().replace(in.get(),
                                                                                                            new PhysicalDomain[] { H1.v() },
                                                                                                            new PhysicalDomain[] { C2.v() }),
                                                                             new PhysicalDomain[] { V1.v() }));
        newEdges.eq(jedd.internal.Jedd.v().cast((jedd.internal.RelationContainer)
                                                  new jedd.internal.RelationContainer(new Attribute[] { stmt.v(), srcm.v(), tgtm.v(), tgtc.v(), kind.v(), srcc.v() },
                                                                                      new PhysicalDomain[] { ST.v(), MS.v(), MT.v(), C2.v(), KD.v(), C1.v() },
                                                                                      ("newEdges.applyShifter(shifter) at /tmp/olhotak/soot-trunk/sr" +
                                                                                       "c/soot/jimple/paddle/BDDKObjSensVirtualContextManager.jedd:5" +
                                                                                       "2,12-20"),
                                                                                      newEdges).applyShifter(shifter),
                                                new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                                new PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() }));
        out.add(new jedd.internal.RelationContainer(new Attribute[] { stmt.v(), srcm.v(), tgtm.v(), tgtc.v(), kind.v(), srcc.v() },
                                                    new PhysicalDomain[] { ST.v(), MS.v(), MT.v(), C2.v(), KD.v(), C1.v() },
                                                    ("out.add(newEdges) at /tmp/olhotak/soot-trunk/src/soot/jimple" +
                                                     "/paddle/BDDKObjSensVirtualContextManager.jedd:53,8-11"),
                                                    newEdges));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newEdges), jedd.internal.Jedd.v().falseBDD());
    }
}
