package soot.jimple.paddle;

import soot.*;
import soot.util.queue.*;
import java.util.*;
import soot.options.PaddleOptions;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.queue.*;
import jedd.*;

public final class PropBDD extends AbsPropagator {
    public PropBDD(Rsrcc_src_dstc_dst simple,
                   Rsrcc_src_fld_dstc_dst load,
                   Rsrcc_src_fld_dstc_dst store,
                   Robjc_obj_varc_var alloc,
                   Qvarc_var_objc_obj propout,
                   AbsPAG pag) {
        super(simple, load, store, alloc, propout, pag);
    }
    
    final jedd.internal.RelationContainer pointsTo =
      new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                          new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                          ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                           "mains.C1, soot.jimple.paddle.bdddomains.var, soot.jimple.pad" +
                                           "dle.bdddomains.objc:soot.jimple.paddle.bdddomains.C2, soot.j" +
                                           "imple.paddle.bdddomains.obj> pointsTo = jedd.internal.Jedd.v" +
                                           "().falseBDD() at /home/research/ccl/olhota/soot-trunk/src/so" +
                                           "ot/jimple/paddle/PropBDD.jedd:39,4-32"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    final jedd.internal.RelationContainer fieldPt =
      new jedd.internal.RelationContainer(new Attribute[] { basec.v(), base.v(), fld.v(), objc.v(), obj.v() },
                                          new PhysicalDomain[] { C1.v(), H1.v(), FD.v(), C2.v(), H2.v() },
                                          ("<soot.jimple.paddle.bdddomains.basec:soot.jimple.paddle.bddd" +
                                           "omains.C1, soot.jimple.paddle.bdddomains.base, soot.jimple.p" +
                                           "addle.bdddomains.fld, soot.jimple.paddle.bdddomains.objc:soo" +
                                           "t.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains" +
                                           ".obj> fieldPt = jedd.internal.Jedd.v().falseBDD() at /home/r" +
                                           "esearch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDD" +
                                           ".jedd:40,4-39"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public final void update() {
        final jedd.internal.RelationContainer allContexts =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v() },
                                              new PhysicalDomain[] { C1.v(), C2.v() },
                                              ("final <soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle" +
                                               ".bdddomains.C1, soot.jimple.paddle.bdddomains.objc:soot.jimp" +
                                               "le.paddle.bdddomains.C2> allContexts = jedd.internal.Jedd.v(" +
                                               ").trueBDD(); at /home/research/ccl/olhota/soot-trunk/src/soo" +
                                               "t/jimple/paddle/PropBDD.jedd:147,27-38"),
                                              jedd.internal.Jedd.v().trueBDD());
        final jedd.internal.RelationContainer oldPointsTo =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> oldPointsTo = jedd.intern" +
                                               "al.Jedd.v().falseBDD(); at /home/research/ccl/olhota/soot-tr" +
                                               "unk/src/soot/jimple/paddle/PropBDD.jedd:148,31-42"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer veryOldPointsTo =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> veryOldPointsTo = jedd.in" +
                                               "ternal.Jedd.v().falseBDD(); at /home/research/ccl/olhota/soo" +
                                               "t-trunk/src/soot/jimple/paddle/PropBDD.jedd:149,31-46"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer outputtedPointsTo =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> outputtedPointsTo = jedd." +
                                               "internal.Jedd.v().falseBDD(); at /home/research/ccl/olhota/s" +
                                               "oot-trunk/src/soot/jimple/paddle/PropBDD.jedd:150,31-48"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer objectsBeingStored =
          new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v(), varc.v(), var.v(), fld.v() },
                                              new PhysicalDomain[] { C3.v(), H2.v(), C1.v(), V2.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.objc:soot.jimple.paddle.bdddo" +
                                               "mains.C3, soot.jimple.paddle.bdddomains.obj:soot.jimple.padd" +
                                               "le.bdddomains.H2, soot.jimple.paddle.bdddomains.varc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.var" +
                                               ":soot.jimple.paddle.bdddomains.V2, soot.jimple.paddle.bdddom" +
                                               "ains.fld:soot.jimple.paddle.bdddomains.FD> objectsBeingStore" +
                                               "d; at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/p" +
                                               "addle/PropBDD.jedd:152,39-57"));
        final jedd.internal.RelationContainer loadsFromHeap =
          new jedd.internal.RelationContainer(new Attribute[] { basec.v(), base.v(), fld.v(), dstc.v(), dst.v() },
                                              new PhysicalDomain[] { C2.v(), H1.v(), FD.v(), C3.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.basec:soot.jimple.paddle.bddd" +
                                               "omains.C2, soot.jimple.paddle.bdddomains.base:soot.jimple.pa" +
                                               "ddle.bdddomains.H1, soot.jimple.paddle.bdddomains.fld:soot.j" +
                                               "imple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.ds" +
                                               "tc:soot.jimple.paddle.bdddomains.C3, soot.jimple.paddle.bddd" +
                                               "omains.dst:soot.jimple.paddle.bdddomains.V2> loadsFromHeap =" +
                                               " jedd.internal.Jedd.v().falseBDD(); at /home/research/ccl/ol" +
                                               "hota/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:155,41-5" +
                                               "4"),
                                              jedd.internal.Jedd.v().falseBDD());
        final AbsTypeManager typeManager = PaddleScene.v().tm;
        do  {
            veryOldPointsTo.eq(pointsTo);
            pointsTo.eqUnion(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(newAlloc.get()),
                                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(typeManager.get()),
                                                                                          allContexts,
                                                                                          new PhysicalDomain[] {  })));
            do  {
                oldPointsTo.eq(pointsTo);
                pointsTo.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(typeManager.get(),
                                                                                                                                                                                                                    new PhysicalDomain[] { V1.v() },
                                                                                                                                                                                                                    new PhysicalDomain[] { V2.v() })),
                                                                                                                                                         allContexts,
                                                                                                                                                         new PhysicalDomain[] {  })),
                                                                                                 jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().replace(pag.allSimple().get(),
                                                                                                                                                                                                                          new PhysicalDomain[] { C1.v() },
                                                                                                                                                                                                                          new PhysicalDomain[] { C3.v() }),
                                                                                                                                                                                           new PhysicalDomain[] { C2.v() },
                                                                                                                                                                                           new PhysicalDomain[] { C1.v() })),
                                                                                                                                jedd.internal.Jedd.v().replace(pointsTo,
                                                                                                                                                               new PhysicalDomain[] { C1.v() },
                                                                                                                                                               new PhysicalDomain[] { C3.v() }),
                                                                                                                                new PhysicalDomain[] { C3.v(), V1.v() })),
                                                                new PhysicalDomain[] { V2.v() },
                                                                new PhysicalDomain[] { V1.v() }));
            }while(!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pointsTo), oldPointsTo)); 
            ptout.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v(), varc.v(), objc.v() },
                                                          new PhysicalDomain[] { V1.v(), H1.v(), C1.v(), C2.v() },
                                                          ("ptout.add(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v(" +
                                                           ").read(pointsTo), outputtedPointsTo)) at /home/research/ccl/" +
                                                           "olhota/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:178,12" +
                                                           "-17"),
                                                          jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(pointsTo),
                                                                                       outputtedPointsTo)));
            outputtedPointsTo.eq(pointsTo);
            PaddleScene.v().updateCallGraph();
            objectsBeingStored.eq(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(pag.allStore().get()),
                                                                                                jedd.internal.Jedd.v().replace(pointsTo,
                                                                                                                               new PhysicalDomain[] { H1.v(), C2.v() },
                                                                                                                               new PhysicalDomain[] { H2.v(), C3.v() }),
                                                                                                new PhysicalDomain[] { C1.v(), V1.v() }),
                                                                 new PhysicalDomain[] { C2.v() },
                                                                 new PhysicalDomain[] { C1.v() }));
            fieldPt.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(objectsBeingStored),
                                                                                          jedd.internal.Jedd.v().replace(pointsTo,
                                                                                                                         new PhysicalDomain[] { V1.v() },
                                                                                                                         new PhysicalDomain[] { V2.v() }),
                                                                                          new PhysicalDomain[] { C1.v(), V2.v() }),
                                                           new PhysicalDomain[] { C3.v(), C2.v() },
                                                           new PhysicalDomain[] { C2.v(), C1.v() }));
            loadsFromHeap.eq(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(pag.allLoad().get(),
                                                                                                                       new PhysicalDomain[] { C2.v() },
                                                                                                                       new PhysicalDomain[] { C3.v() })),
                                                            pointsTo,
                                                            new PhysicalDomain[] { C1.v(), V1.v() }));
            pointsTo.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(loadsFromHeap,
                                                                                                                                                      new PhysicalDomain[] { C2.v(), V2.v() },
                                                                                                                                                      new PhysicalDomain[] { C1.v(), V1.v() })),
                                                                                           fieldPt,
                                                                                           new PhysicalDomain[] { C1.v(), H1.v(), FD.v() }),
                                                            new PhysicalDomain[] { C3.v(), H2.v() },
                                                            new PhysicalDomain[] { C1.v(), H1.v() }));
            pointsTo.eqIntersect(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(typeManager.get()),
                                                             allContexts,
                                                             new PhysicalDomain[] {  }));
            ptout.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v(), varc.v(), objc.v() },
                                                          new PhysicalDomain[] { V1.v(), H1.v(), C1.v(), C2.v() },
                                                          ("ptout.add(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v(" +
                                                           ").read(pointsTo), outputtedPointsTo)) at /home/research/ccl/" +
                                                           "olhota/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:198,12" +
                                                           "-17"),
                                                          jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(pointsTo),
                                                                                       outputtedPointsTo)));
            outputtedPointsTo.eq(pointsTo);
            PaddleScene.v().updateCallGraph();
            if (PaddleScene.v().options().verbose()) { G.v().out.println("Major iteration: "); }
        }while(!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pointsTo), veryOldPointsTo)); 
    }
}
