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
                                               "ges = jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v()." +
                                               "project(in.get(), new jedd.PhysicalDomain[...]), new jedd.Ph" +
                                               "ysicalDomain[...], new jedd.PhysicalDomain[...]); at /home/r" +
                                               "esearch/ccl/olhota/soot-jedd/src/soot/jimple/paddle/BDDKObjS" +
                                               "ensVirtualContextManager.jedd:49,51-59"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(in.get(),
                                                                                                            new PhysicalDomain[] { V1.v() }),
                                                                             new PhysicalDomain[] { H1.v() },
                                                                             new PhysicalDomain[] { C2.v() }));
        newEdges.eq(jedd.internal.Jedd.v().cast((jedd.internal.RelationContainer)
                                                  new jedd.internal.RelationContainer(new Attribute[] { tgtc.v(), tgtm.v(), srcc.v(), srcm.v(), kind.v(), stmt.v() },
                                                                                      new PhysicalDomain[] { C2.v(), MT.v(), C1.v(), MS.v(), KD.v(), ST.v() },
                                                                                      ("newEdges.applyShifter(shifter) at /home/research/ccl/olhota/" +
                                                                                       "soot-jedd/src/soot/jimple/paddle/BDDKObjSensVirtualContextMa" +
                                                                                       "nager.jedd:52,12-20"),
                                                                                      newEdges).applyShifter(shifter),
                                                new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                                new PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() }));
        out.add(new jedd.internal.RelationContainer(new Attribute[] { tgtc.v(), tgtm.v(), srcc.v(), srcm.v(), kind.v(), stmt.v() },
                                                    new PhysicalDomain[] { C2.v(), MT.v(), C1.v(), MS.v(), KD.v(), ST.v() },
                                                    ("out.add(newEdges) at /home/research/ccl/olhota/soot-jedd/src" +
                                                     "/soot/jimple/paddle/BDDKObjSensVirtualContextManager.jedd:53" +
                                                     ",8-11"),
                                                    newEdges));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newEdges), jedd.internal.Jedd.v().falseBDD());
    }
}
