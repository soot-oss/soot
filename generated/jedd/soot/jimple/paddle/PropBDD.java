package soot.jimple.paddle;

import soot.*;
import soot.util.queue.*;
import java.util.*;
import soot.options.PaddleOptions;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.queue.*;
import jedd.*;

public final class PropBDD extends AbsPropagator {
    public PropBDD(Rsrc_dst simple,
                   Rsrc_fld_dst load,
                   Rsrc_fld_dst store,
                   Robj_var alloc,
                   Qvar_obj propout,
                   AbsPAG pag) {
        super(simple, load, store, alloc, propout, pag);
    }
    
    final jedd.internal.RelationContainer pointsTo =
      new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                          new PhysicalDomain[] { V1.v(), H1.v() },
                                          ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                           "ains.V1, soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                           "e.bdddomains.H1> pointsTo = jedd.internal.Jedd.v().falseBDD(" +
                                           ") at /tmp/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:39," +
                                           "4-20"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    final jedd.internal.RelationContainer fieldPt =
      new jedd.internal.RelationContainer(new Attribute[] { base.v(), fld.v(), obj.v() },
                                          new PhysicalDomain[] { H1.v(), FD.v(), H2.v() },
                                          ("<soot.jimple.paddle.bdddomains.base:soot.jimple.paddle.bdddo" +
                                           "mains.H1, soot.jimple.paddle.bdddomains.fld:soot.jimple.padd" +
                                           "le.bdddomains.FD, soot.jimple.paddle.bdddomains.obj:soot.jim" +
                                           "ple.paddle.bdddomains.H2> fieldPt = jedd.internal.Jedd.v().f" +
                                           "alseBDD() at /tmp/soot-trunk/src/soot/jimple/paddle/PropBDD." +
                                           "jedd:40,4-29"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public final void update() {
        final jedd.internal.RelationContainer oldPointsTo =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                              new PhysicalDomain[] { V1.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                               "e.bdddomains.H1> oldPointsTo = jedd.internal.Jedd.v().falseB" +
                                               "DD(); at /tmp/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd" +
                                               ":147,25-36"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer veryOldPointsTo =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                              new PhysicalDomain[] { V1.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                               "e.bdddomains.H1> veryOldPointsTo = jedd.internal.Jedd.v().fa" +
                                               "lseBDD(); at /tmp/soot-trunk/src/soot/jimple/paddle/PropBDD." +
                                               "jedd:148,25-40"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer outputtedPointsTo =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                              new PhysicalDomain[] { V1.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                               "e.bdddomains.H1> outputtedPointsTo = jedd.internal.Jedd.v()." +
                                               "falseBDD(); at /tmp/soot-trunk/src/soot/jimple/paddle/PropBD" +
                                               "D.jedd:149,25-42"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer objectsBeingStored =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v(), fld.v() },
                                              new PhysicalDomain[] { H2.v(), V1.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.obj:soot.jimple.paddle.bdddom" +
                                               "ains.H2, soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                               "e.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jimp" +
                                               "le.paddle.bdddomains.FD> objectsBeingStored; at /tmp/soot-tr" +
                                               "unk/src/soot/jimple/paddle/PropBDD.jedd:151,33-51"));
        final jedd.internal.RelationContainer oldStorePt =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v(), fld.v() },
                                              new PhysicalDomain[] { H2.v(), V1.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.obj:soot.jimple.paddle.bdddom" +
                                               "ains.H2, soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                               "e.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jimp" +
                                               "le.paddle.bdddomains.FD> oldStorePt = jedd.internal.Jedd.v()" +
                                               ".falseBDD(); at /tmp/soot-trunk/src/soot/jimple/paddle/PropB" +
                                               "DD.jedd:152,33-43"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer loadsFromHeap =
          new jedd.internal.RelationContainer(new Attribute[] { base.v(), fld.v(), dst.v() },
                                              new PhysicalDomain[] { H1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.base:soot.jimple.paddle.bdddo" +
                                               "mains.H1, soot.jimple.paddle.bdddomains.fld:soot.jimple.padd" +
                                               "le.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:soot.jim" +
                                               "ple.paddle.bdddomains.V2> loadsFromHeap = jedd.internal.Jedd" +
                                               ".v().falseBDD(); at /tmp/soot-trunk/src/soot/jimple/paddle/P" +
                                               "ropBDD.jedd:155,34-47"),
                                              jedd.internal.Jedd.v().falseBDD());
        final AbsTypeManager typeManager = PaddleScene.v().tm;
        do  {
            veryOldPointsTo.eq(pointsTo);
            pointsTo.eqUnion(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(newAlloc.get()),
                                                              typeManager.get()));
            do  {
                oldPointsTo.eq(pointsTo);
                pointsTo.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(typeManager.get(),
                                                                                                                                                            new PhysicalDomain[] { V1.v() },
                                                                                                                                                            new PhysicalDomain[] { V2.v() })),
                                                                                                 jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(pag.allSimple().get()),
                                                                                                                                pointsTo,
                                                                                                                                new PhysicalDomain[] { V1.v() })),
                                                                new PhysicalDomain[] { V2.v() },
                                                                new PhysicalDomain[] { V1.v() }));
            }while(!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pointsTo), oldPointsTo)); 
            ptout.add(new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v() },
                                                          new PhysicalDomain[] { H1.v(), V1.v() },
                                                          ("ptout.add(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v(" +
                                                           ").read(pointsTo), outputtedPointsTo)) at /tmp/soot-trunk/src" +
                                                           "/soot/jimple/paddle/PropBDD.jedd:176,12-17"),
                                                          jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(pointsTo),
                                                                                       outputtedPointsTo)));
            outputtedPointsTo.eq(pointsTo);
            PaddleScene.v().updateCallGraph();
            objectsBeingStored.eq(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(pag.allStore().get()),
                                                                                                jedd.internal.Jedd.v().replace(pointsTo,
                                                                                                                               new PhysicalDomain[] { H1.v() },
                                                                                                                               new PhysicalDomain[] { H2.v() }),
                                                                                                new PhysicalDomain[] { V1.v() }),
                                                                 new PhysicalDomain[] { V2.v() },
                                                                 new PhysicalDomain[] { V1.v() }));
            fieldPt.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(objectsBeingStored),
                                                           pointsTo,
                                                           new PhysicalDomain[] { V1.v() }));
            loadsFromHeap.eq(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(pag.allLoad().get()),
                                                            pointsTo,
                                                            new PhysicalDomain[] { V1.v() }));
            pointsTo.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(loadsFromHeap,
                                                                                                                                                      new PhysicalDomain[] { V2.v() },
                                                                                                                                                      new PhysicalDomain[] { V1.v() })),
                                                                                           fieldPt,
                                                                                           new PhysicalDomain[] { H1.v(), FD.v() }),
                                                            new PhysicalDomain[] { H2.v() },
                                                            new PhysicalDomain[] { H1.v() }));
            pointsTo.eqIntersect(typeManager.get());
            ptout.add(new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v() },
                                                          new PhysicalDomain[] { H1.v(), V1.v() },
                                                          ("ptout.add(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v(" +
                                                           ").read(pointsTo), outputtedPointsTo)) at /tmp/soot-trunk/src" +
                                                           "/soot/jimple/paddle/PropBDD.jedd:193,12-17"),
                                                          jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(pointsTo),
                                                                                       outputtedPointsTo)));
            outputtedPointsTo.eq(pointsTo);
            PaddleScene.v().updateCallGraph();
            if (PaddleScene.v().options().verbose()) { G.v().out.println("Major iteration: "); }
        }while(!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pointsTo), veryOldPointsTo)); 
    }
}
