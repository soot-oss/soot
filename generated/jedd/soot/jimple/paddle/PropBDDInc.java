package soot.jimple.paddle;

import soot.*;
import soot.util.queue.*;
import java.util.*;
import soot.options.PaddleOptions;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.queue.*;
import jedd.*;

public final class PropBDDInc extends PropBDD {
    public PropBDDInc(Rsrcc_src_dstc_dst simple,
                      Rsrcc_src_fld_dstc_dst load,
                      Rsrcc_src_dstc_dst_fld store,
                      Robjc_obj_varc_var alloc,
                      Qvarc_var_objc_obj propout,
                      AbsPAG pag) {
        super(simple, load, store, alloc, propout, pag);
    }
    
    public final boolean update() {
        final jedd.internal.RelationContainer ptFromLoad =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> ptFromLoad = jedd.interna" +
                                               "l.Jedd.v().falseBDD(); at /home/research/ccl/olhota/soot-tru" +
                                               "nk/src/soot/jimple/paddle/PropBDDInc.jedd:40,31-41"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer veryOldPt =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V2.v(), C2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> veryOldPt = pt; at /home/" +
                                               "research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBD" +
                                               "DInc.jedd:42,31-40"),
                                              pt);
        final jedd.internal.RelationContainer ptFromAlloc =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V2.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ptFromAlloc = jedd.intern" +
                                               "al.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.in" +
                                               "ternal.Jedd.v().read(newAlloc.get()), jedd.internal.Jedd.v()" +
                                               ".replace(typeFilter(), new jedd.PhysicalDomain[...], new jed" +
                                               "d.PhysicalDomain[...])), new jedd.PhysicalDomain[...], new j" +
                                               "edd.PhysicalDomain[...]); at /home/research/ccl/olhota/soot-" +
                                               "trunk/src/soot/jimple/paddle/PropBDDInc.jedd:45,12-23"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(newAlloc.get()),
                                                                                                              jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                                                             new PhysicalDomain[] { C3.v(), V2.v(), H2.v() },
                                                                                                                                             new PhysicalDomain[] { C1.v(), V1.v(), H1.v() })),
                                                                             new PhysicalDomain[] { V1.v() },
                                                                             new PhysicalDomain[] { V2.v() }));
        final jedd.internal.RelationContainer ptFromSimple1 =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V2.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ptFromSimple1 = jedd.inte" +
                                               "rnal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.int" +
                                               "ernal.Jedd.v().replace(propSimple(new jedd.internal.Relation" +
                                               "Container(...), new jedd.internal.RelationContainer(...)), n" +
                                               "ew jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]))," +
                                               " jedd.internal.Jedd.v().replace(typeFilter(), new jedd.Physi" +
                                               "calDomain[...], new jedd.PhysicalDomain[...])); at /home/res" +
                                               "earch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDIn" +
                                               "c.jedd:47,12-25"),
                                              jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propSimple(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), varc.v(), var.v(), obj.v() },
                                                                                                                                                                                         new PhysicalDomain[] { C2.v(), C1.v(), V1.v(), H2.v() },
                                                                                                                                                                                         ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                                          "calDomain[...], new jedd.PhysicalDomain[...]), newSimple.get" +
                                                                                                                                                                                          "()) at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/" +
                                                                                                                                                                                          "paddle/PropBDDInc.jedd:47,28-38"),
                                                                                                                                                                                         jedd.internal.Jedd.v().replace(pt,
                                                                                                                                                                                                                        new PhysicalDomain[] { V2.v() },
                                                                                                                                                                                                                        new PhysicalDomain[] { V1.v() })),
                                                                                                                                                     new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                                                                                                                                                         new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                                                                                                                                                                         ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                                          "calDomain[...], new jedd.PhysicalDomain[...]), newSimple.get" +
                                                                                                                                                                                          "()) at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/" +
                                                                                                                                                                                          "paddle/PropBDDInc.jedd:47,28-38"),
                                                                                                                                                                                         newSimple.get())),
                                                                                                                                          new PhysicalDomain[] { H2.v() },
                                                                                                                                          new PhysicalDomain[] { H1.v() })),
                                                                               jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                              new PhysicalDomain[] { C3.v(), H2.v() },
                                                                                                              new PhysicalDomain[] { C1.v(), H1.v() })));
        final jedd.internal.RelationContainer ptFromAllocAndSimple1 =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C3.v(), V2.v(), C2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C3, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> ptFromAllocAndSimple1 = j" +
                                               "edd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(j" +
                                               "edd.internal.Jedd.v().read(ptFromAlloc), ptFromSimple1), new" +
                                               " jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]); at" +
                                               " /home/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle" +
                                               "/PropBDDInc.jedd:49,12-33"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(ptFromAlloc),
                                                                                                          ptFromSimple1),
                                                                             new PhysicalDomain[] { C1.v(), H1.v() },
                                                                             new PhysicalDomain[] { C3.v(), H2.v() }));
        final jedd.internal.RelationContainer ptFromSimple2 =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C3.v(), V2.v(), C2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C3, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> ptFromSimple2 = jedd.inte" +
                                               "rnal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.int" +
                                               "ernal.Jedd.v().replace(propSimple(new jedd.internal.Relation" +
                                               "Container(...), new jedd.internal.RelationContainer(...)), n" +
                                               "ew jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]))," +
                                               " typeFilter()); at /home/research/ccl/olhota/soot-trunk/src/" +
                                               "soot/jimple/paddle/PropBDDInc.jedd:51,12-25"),
                                              jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propSimple(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), varc.v(), var.v(), obj.v() },
                                                                                                                                                                                         new PhysicalDomain[] { C2.v(), C1.v(), V1.v(), H2.v() },
                                                                                                                                                                                         ("propSimple(jedd.internal.Jedd.v().replace(jedd.internal.Jedd" +
                                                                                                                                                                                          ".v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v(" +
                                                                                                                                                                                          ").replace(ptFromAllocAndSimple1, new jedd.PhysicalDomain[..." +
                                                                                                                                                                                          "], new jedd.PhysicalDomain[...])), jedd.internal.Jedd.v().re" +
                                                                                                                                                                                          "place(ptFromLoad, new jedd.PhysicalDomain[...], new jedd.Phy" +
                                                                                                                                                                                          "sicalDomain[...])), new jedd.PhysicalDomain[...], new jedd.P" +
                                                                                                                                                                                          "hysicalDomain[...]), pag.allSimple().get()) at /home/researc" +
                                                                                                                                                                                          "h/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDInc.je" +
                                                                                                                                                                                          "dd:51,28-38"),
                                                                                                                                                                                         jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ptFromAllocAndSimple1,
                                                                                                                                                                                                                                                                                                                new PhysicalDomain[] { V2.v() },
                                                                                                                                                                                                                                                                                                                new PhysicalDomain[] { V1.v() })),
                                                                                                                                                                                                                                                     jedd.internal.Jedd.v().replace(ptFromLoad,
                                                                                                                                                                                                                                                                                    new PhysicalDomain[] { C1.v() },
                                                                                                                                                                                                                                                                                    new PhysicalDomain[] { C3.v() })),
                                                                                                                                                                                                                        new PhysicalDomain[] { C3.v() },
                                                                                                                                                                                                                        new PhysicalDomain[] { C1.v() })),
                                                                                                                                                     new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                                                                                                                                                         new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                                                                                                                                                                         ("propSimple(jedd.internal.Jedd.v().replace(jedd.internal.Jedd" +
                                                                                                                                                                                          ".v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v(" +
                                                                                                                                                                                          ").replace(ptFromAllocAndSimple1, new jedd.PhysicalDomain[..." +
                                                                                                                                                                                          "], new jedd.PhysicalDomain[...])), jedd.internal.Jedd.v().re" +
                                                                                                                                                                                          "place(ptFromLoad, new jedd.PhysicalDomain[...], new jedd.Phy" +
                                                                                                                                                                                          "sicalDomain[...])), new jedd.PhysicalDomain[...], new jedd.P" +
                                                                                                                                                                                          "hysicalDomain[...]), pag.allSimple().get()) at /home/researc" +
                                                                                                                                                                                          "h/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDInc.je" +
                                                                                                                                                                                          "dd:51,28-38"),
                                                                                                                                                                                         pag.allSimple().get())),
                                                                                                                                          new PhysicalDomain[] { C1.v() },
                                                                                                                                          new PhysicalDomain[] { C3.v() })),
                                                                               typeFilter()));
        final jedd.internal.RelationContainer ptFromAllocAndSimple =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C3.v(), V2.v(), C2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C3, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> ptFromAllocAndSimple = je" +
                                               "dd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(ptFro" +
                                               "mAllocAndSimple1), ptFromSimple2); at /home/research/ccl/olh" +
                                               "ota/soot-trunk/src/soot/jimple/paddle/PropBDDInc.jedd:54,12-" +
                                               "32"),
                                              jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(ptFromAllocAndSimple1),
                                                                           ptFromSimple2));
        pt.eqUnion(jedd.internal.Jedd.v().replace(ptFromAllocAndSimple,
                                                  new PhysicalDomain[] { C3.v() },
                                                  new PhysicalDomain[] { C1.v() }));
        outputPt(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), varc.v(), var.v(), obj.v() },
                                                     new PhysicalDomain[] { C2.v(), C3.v(), V2.v(), H1.v() },
                                                     ("outputPt(jedd.internal.Jedd.v().replace(ptFromAllocAndSimple" +
                                                      ", new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/p" +
                                                      "addle/PropBDDInc.jedd:56,8-16"),
                                                     jedd.internal.Jedd.v().replace(ptFromAllocAndSimple,
                                                                                    new PhysicalDomain[] { H2.v() },
                                                                                    new PhysicalDomain[] { H1.v() })));
        fieldPt.eqUnion(propStore(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), varc.v(), var.v(), obj.v() },
                                                                      new PhysicalDomain[] { C3.v(), C1.v(), V1.v(), H2.v() },
                                                                      ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                       "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                       ".get(), pt) at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                                       "/jimple/paddle/PropBDDInc.jedd:58,19-28"),
                                                                      jedd.internal.Jedd.v().replace(pt,
                                                                                                     new PhysicalDomain[] { C2.v(), V2.v() },
                                                                                                     new PhysicalDomain[] { C3.v(), V1.v() })),
                                  new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v(), fld.v() },
                                                                      new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v(), FD.v() },
                                                                      ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                       "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                       ".get(), pt) at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                                       "/jimple/paddle/PropBDDInc.jedd:58,19-28"),
                                                                      pag.allStore().get()),
                                  new jedd.internal.RelationContainer(new Attribute[] { objc.v(), varc.v(), var.v(), obj.v() },
                                                                      new PhysicalDomain[] { C2.v(), C1.v(), V2.v(), H2.v() },
                                                                      ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                       "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                       ".get(), pt) at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                                       "/jimple/paddle/PropBDDInc.jedd:58,19-28"),
                                                                      pt)));
        ptFromLoad.eq(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propLoad(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), base.v(), objc.v(), basec.v(), obj.v() },
                                                                                                                                                               new PhysicalDomain[] { FD.v(), H1.v(), C2.v(), C1.v(), H2.v() },
                                                                                                                                                               ("propLoad(fieldPt, pag.allLoad().get(), pt) at /home/research" +
                                                                                                                                                                "/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDInc.jed" +
                                                                                                                                                                "d:59,21-29"),
                                                                                                                                                               fieldPt),
                                                                                                                           new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                                                                                                                                               new PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                                                                                                                                               ("propLoad(fieldPt, pag.allLoad().get(), pt) at /home/research" +
                                                                                                                                                                "/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDInc.jed" +
                                                                                                                                                                "d:59,21-29"),
                                                                                                                                                               pag.allLoad().get()),
                                                                                                                           new jedd.internal.RelationContainer(new Attribute[] { objc.v(), varc.v(), var.v(), obj.v() },
                                                                                                                                                               new PhysicalDomain[] { C2.v(), C1.v(), V2.v(), H2.v() },
                                                                                                                                                               ("propLoad(fieldPt, pag.allLoad().get(), pt) at /home/research" +
                                                                                                                                                                "/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDInc.jed" +
                                                                                                                                                                "d:59,21-29"),
                                                                                                                                                               pt)),
                                                                                                                  new PhysicalDomain[] { C3.v() },
                                                                                                                  new PhysicalDomain[] { C1.v() })),
                                                       jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                      new PhysicalDomain[] { C3.v(), V2.v() },
                                                                                      new PhysicalDomain[] { C1.v(), V1.v() })));
        pt.eqUnion(jedd.internal.Jedd.v().replace(ptFromLoad,
                                                  new PhysicalDomain[] { V1.v() },
                                                  new PhysicalDomain[] { V2.v() }));
        outputPt(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), varc.v(), var.v(), obj.v() },
                                                     new PhysicalDomain[] { C2.v(), C3.v(), V2.v(), H1.v() },
                                                     ("outputPt(jedd.internal.Jedd.v().replace(ptFromLoad, new jedd" +
                                                      ".PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /hom" +
                                                      "e/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/Prop" +
                                                      "BDDInc.jedd:61,8-16"),
                                                     jedd.internal.Jedd.v().replace(ptFromLoad,
                                                                                    new PhysicalDomain[] { C1.v(), V1.v(), H2.v() },
                                                                                    new PhysicalDomain[] { C3.v(), V2.v(), H1.v() })));
        if (PaddleScene.v().options().verbose()) { G.v().out.println("Major iteration: "); }
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pt), veryOldPt);
    }
}
