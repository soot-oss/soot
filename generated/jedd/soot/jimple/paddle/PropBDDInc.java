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
                                              new PhysicalDomain[] { C2.v(), V2.v(), C1.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ptFromLoad = jedd.interna" +
                                               "l.Jedd.v().falseBDD(); at /tmp/soot-trunk-saved/src/soot/jim" +
                                               "ple/paddle/PropBDDInc.jedd:40,31-41"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer veryOldPt =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C3.v(), V1.v(), C1.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C3, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> veryOldPt = pt; at /tmp/s" +
                                               "oot-trunk-saved/src/soot/jimple/paddle/PropBDDInc.jedd:42,31" +
                                               "-40"),
                                              pt);
        final jedd.internal.RelationContainer ptFromAlloc =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ptFromAlloc = jedd.intern" +
                                               "al.Jedd.v().intersect(jedd.internal.Jedd.v().read(newAlloc.g" +
                                               "et()), jedd.internal.Jedd.v().replace(typeFilter(), new jedd" +
                                               ".PhysicalDomain[...], new jedd.PhysicalDomain[...])); at /tm" +
                                               "p/soot-trunk-saved/src/soot/jimple/paddle/PropBDDInc.jedd:45" +
                                               ",12-23"),
                                              jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(newAlloc.get()),
                                                                               jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                              new PhysicalDomain[] { C2.v(), C1.v(), H2.v() },
                                                                                                              new PhysicalDomain[] { C1.v(), C2.v(), H1.v() })));
        final jedd.internal.RelationContainer ptFromSimple1 =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ptFromSimple1 = jedd.inte" +
                                               "rnal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.int" +
                                               "ernal.Jedd.v().replace(propSimple(new jedd.internal.Relation" +
                                               "Container(...), new jedd.internal.RelationContainer(...)), n" +
                                               "ew jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]))," +
                                               " jedd.internal.Jedd.v().replace(typeFilter(), new jedd.Physi" +
                                               "calDomain[...], new jedd.PhysicalDomain[...])); at /tmp/soot" +
                                               "-trunk-saved/src/soot/jimple/paddle/PropBDDInc.jedd:47,12-25"),
                                              jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propSimple(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v(), obj.v(), objc.v() },
                                                                                                                                                                                         new PhysicalDomain[] { V1.v(), C3.v(), H1.v(), C1.v() },
                                                                                                                                                                                         ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                                          "calDomain[...], new jedd.PhysicalDomain[...]), jedd.internal" +
                                                                                                                                                                                          ".Jedd.v().replace(newSimple.get(), new jedd.PhysicalDomain[." +
                                                                                                                                                                                          "..], new jedd.PhysicalDomain[...])) at /tmp/soot-trunk-saved" +
                                                                                                                                                                                          "/src/soot/jimple/paddle/PropBDDInc.jedd:47,28-38"),
                                                                                                                                                                                         jedd.internal.Jedd.v().replace(pt,
                                                                                                                                                                                                                        new PhysicalDomain[] { H2.v() },
                                                                                                                                                                                                                        new PhysicalDomain[] { H1.v() })),
                                                                                                                                                     new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                                                                                                                                                         new PhysicalDomain[] { C3.v(), V1.v(), C2.v(), V2.v() },
                                                                                                                                                                                         ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                                          "calDomain[...], new jedd.PhysicalDomain[...]), jedd.internal" +
                                                                                                                                                                                          ".Jedd.v().replace(newSimple.get(), new jedd.PhysicalDomain[." +
                                                                                                                                                                                          "..], new jedd.PhysicalDomain[...])) at /tmp/soot-trunk-saved" +
                                                                                                                                                                                          "/src/soot/jimple/paddle/PropBDDInc.jedd:47,28-38"),
                                                                                                                                                                                         jedd.internal.Jedd.v().replace(newSimple.get(),
                                                                                                                                                                                                                        new PhysicalDomain[] { C1.v() },
                                                                                                                                                                                                                        new PhysicalDomain[] { C3.v() }))),
                                                                                                                                          new PhysicalDomain[] { C3.v() },
                                                                                                                                          new PhysicalDomain[] { C1.v() })),
                                                                               jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                              new PhysicalDomain[] { C2.v(), C1.v(), H2.v() },
                                                                                                              new PhysicalDomain[] { C1.v(), C2.v(), H1.v() })));
        final jedd.internal.RelationContainer ptFromAllocAndSimple1 =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ptFromAllocAndSimple1 = j" +
                                               "edd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(ptFr" +
                                               "omAlloc), ptFromSimple1); at /tmp/soot-trunk-saved/src/soot/" +
                                               "jimple/paddle/PropBDDInc.jedd:49,12-33"),
                                              jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(ptFromAlloc),
                                                                           ptFromSimple1));
        final jedd.internal.RelationContainer ptFromSimple2 =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V2.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ptFromSimple2 = jedd.inte" +
                                               "rnal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd." +
                                               "internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propSi" +
                                               "mple(new jedd.internal.RelationContainer(...), new jedd.inte" +
                                               "rnal.RelationContainer(...)), new jedd.PhysicalDomain[...], " +
                                               "new jedd.PhysicalDomain[...])), jedd.internal.Jedd.v().repla" +
                                               "ce(typeFilter(), new jedd.PhysicalDomain[...], new jedd.Phys" +
                                               "icalDomain[...])), new jedd.PhysicalDomain[...], new jedd.Ph" +
                                               "ysicalDomain[...]); at /tmp/soot-trunk-saved/src/soot/jimple" +
                                               "/paddle/PropBDDInc.jedd:51,12-25"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propSimple(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v(), obj.v(), objc.v() },
                                                                                                                                                                                                                        new PhysicalDomain[] { V1.v(), C3.v(), H1.v(), C1.v() },
                                                                                                                                                                                                                        ("propSimple(jedd.internal.Jedd.v().replace(jedd.internal.Jedd" +
                                                                                                                                                                                                                         ".v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v(" +
                                                                                                                                                                                                                         ").replace(ptFromAllocAndSimple1, new jedd.PhysicalDomain[..." +
                                                                                                                                                                                                                         "], new jedd.PhysicalDomain[...])), ptFromLoad), new jedd.Phy" +
                                                                                                                                                                                                                         "sicalDomain[...], new jedd.PhysicalDomain[...]), jedd.intern" +
                                                                                                                                                                                                                         "al.Jedd.v().replace(pag.allSimple().get(), new jedd.Physical" +
                                                                                                                                                                                                                         "Domain[...], new jedd.PhysicalDomain[...])) at /tmp/soot-tru" +
                                                                                                                                                                                                                         "nk-saved/src/soot/jimple/paddle/PropBDDInc.jedd:51,28-38"),
                                                                                                                                                                                                                        jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ptFromAllocAndSimple1,
                                                                                                                                                                                                                                                                                                                                               new PhysicalDomain[] { V1.v(), C1.v(), C2.v() },
                                                                                                                                                                                                                                                                                                                                               new PhysicalDomain[] { V2.v(), C2.v(), C1.v() })),
                                                                                                                                                                                                                                                                                    ptFromLoad),
                                                                                                                                                                                                                                                       new PhysicalDomain[] { V2.v(), C2.v() },
                                                                                                                                                                                                                                                       new PhysicalDomain[] { V1.v(), C3.v() })),
                                                                                                                                                                                    new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                                                                                                                                                                                        new PhysicalDomain[] { C3.v(), V1.v(), C2.v(), V2.v() },
                                                                                                                                                                                                                        ("propSimple(jedd.internal.Jedd.v().replace(jedd.internal.Jedd" +
                                                                                                                                                                                                                         ".v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v(" +
                                                                                                                                                                                                                         ").replace(ptFromAllocAndSimple1, new jedd.PhysicalDomain[..." +
                                                                                                                                                                                                                         "], new jedd.PhysicalDomain[...])), ptFromLoad), new jedd.Phy" +
                                                                                                                                                                                                                         "sicalDomain[...], new jedd.PhysicalDomain[...]), jedd.intern" +
                                                                                                                                                                                                                         "al.Jedd.v().replace(pag.allSimple().get(), new jedd.Physical" +
                                                                                                                                                                                                                         "Domain[...], new jedd.PhysicalDomain[...])) at /tmp/soot-tru" +
                                                                                                                                                                                                                         "nk-saved/src/soot/jimple/paddle/PropBDDInc.jedd:51,28-38"),
                                                                                                                                                                                                                        jedd.internal.Jedd.v().replace(pag.allSimple().get(),
                                                                                                                                                                                                                                                       new PhysicalDomain[] { C1.v() },
                                                                                                                                                                                                                                                       new PhysicalDomain[] { C3.v() }))),
                                                                                                                                                                         new PhysicalDomain[] { V1.v() },
                                                                                                                                                                         new PhysicalDomain[] { V2.v() })),
                                                                                                              jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                                                             new PhysicalDomain[] { C2.v(), V1.v(), C1.v(), H2.v() },
                                                                                                                                             new PhysicalDomain[] { C3.v(), V2.v(), C2.v(), H1.v() })),
                                                                             new PhysicalDomain[] { C3.v() },
                                                                             new PhysicalDomain[] { C1.v() }));
        final jedd.internal.RelationContainer ptFromAllocAndSimple =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V2.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ptFromAllocAndSimple = je" +
                                               "dd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd." +
                                               "internal.Jedd.v().replace(ptFromAllocAndSimple1, new jedd.Ph" +
                                               "ysicalDomain[...], new jedd.PhysicalDomain[...])), ptFromSim" +
                                               "ple2); at /tmp/soot-trunk-saved/src/soot/jimple/paddle/PropB" +
                                               "DDInc.jedd:54,12-32"),
                                              jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ptFromAllocAndSimple1,
                                                                                                                                      new PhysicalDomain[] { V1.v() },
                                                                                                                                      new PhysicalDomain[] { V2.v() })),
                                                                           ptFromSimple2));
        pt.eqUnion(jedd.internal.Jedd.v().replace(ptFromAllocAndSimple,
                                                  new PhysicalDomain[] { V2.v(), C1.v(), H1.v(), C2.v() },
                                                  new PhysicalDomain[] { V1.v(), C3.v(), H2.v(), C1.v() }));
        outputPt(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v(), obj.v(), objc.v() },
                                                     new PhysicalDomain[] { V2.v(), C1.v(), H1.v(), C2.v() },
                                                     ("outputPt(ptFromAllocAndSimple) at /tmp/soot-trunk-saved/src/" +
                                                      "soot/jimple/paddle/PropBDDInc.jedd:56,8-16"),
                                                     ptFromAllocAndSimple));
        fieldPt.eqUnion(propStore(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v(), obj.v(), objc.v() },
                                                                      new PhysicalDomain[] { V1.v(), C3.v(), H2.v(), C1.v() },
                                                                      ("propStore(pt, pag.allStore().get(), jedd.internal.Jedd.v().r" +
                                                                       "eplace(pt, new jedd.PhysicalDomain[...], new jedd.PhysicalDo" +
                                                                       "main[...])) at /tmp/soot-trunk-saved/src/soot/jimple/paddle/" +
                                                                       "PropBDDInc.jedd:58,19-28"),
                                                                      pt),
                                  new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v(), fld.v() },
                                                                      new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v(), FD.v() },
                                                                      ("propStore(pt, pag.allStore().get(), jedd.internal.Jedd.v().r" +
                                                                       "eplace(pt, new jedd.PhysicalDomain[...], new jedd.PhysicalDo" +
                                                                       "main[...])) at /tmp/soot-trunk-saved/src/soot/jimple/paddle/" +
                                                                       "PropBDDInc.jedd:58,19-28"),
                                                                      pag.allStore().get()),
                                  new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v(), obj.v(), objc.v() },
                                                                      new PhysicalDomain[] { V1.v(), C2.v(), H2.v(), C1.v() },
                                                                      ("propStore(pt, pag.allStore().get(), jedd.internal.Jedd.v().r" +
                                                                       "eplace(pt, new jedd.PhysicalDomain[...], new jedd.PhysicalDo" +
                                                                       "main[...])) at /tmp/soot-trunk-saved/src/soot/jimple/paddle/" +
                                                                       "PropBDDInc.jedd:58,19-28"),
                                                                      jedd.internal.Jedd.v().replace(pt,
                                                                                                     new PhysicalDomain[] { C3.v() },
                                                                                                     new PhysicalDomain[] { C2.v() }))));
        ptFromLoad.eq(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propLoad(new jedd.internal.RelationContainer(new Attribute[] { basec.v(), fld.v(), base.v(), obj.v(), objc.v() },
                                                                                                                                                                                              new PhysicalDomain[] { C1.v(), FD.v(), H1.v(), H2.v(), C2.v() },
                                                                                                                                                                                              ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                                                                                               "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                                                                                               "ain[...]), pt) at /tmp/soot-trunk-saved/src/soot/jimple/padd" +
                                                                                                                                                                                               "le/PropBDDInc.jedd:59,21-29"),
                                                                                                                                                                                              fieldPt),
                                                                                                                                                          new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                                                                                                                                                                              new PhysicalDomain[] { C3.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                                                                                                                                                                              ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                                                                                               "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                                                                                               "ain[...]), pt) at /tmp/soot-trunk-saved/src/soot/jimple/padd" +
                                                                                                                                                                                               "le/PropBDDInc.jedd:59,21-29"),
                                                                                                                                                                                              jedd.internal.Jedd.v().replace(pag.allLoad().get(),
                                                                                                                                                                                                                             new PhysicalDomain[] { C1.v() },
                                                                                                                                                                                                                             new PhysicalDomain[] { C3.v() })),
                                                                                                                                                          new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v(), obj.v(), objc.v() },
                                                                                                                                                                                              new PhysicalDomain[] { V1.v(), C3.v(), H2.v(), C1.v() },
                                                                                                                                                                                              ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                                                                                               "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                                                                                               "ain[...]), pt) at /tmp/soot-trunk-saved/src/soot/jimple/padd" +
                                                                                                                                                                                               "le/PropBDDInc.jedd:59,21-29"),
                                                                                                                                                                                              pt)),
                                                                                                                                                 new PhysicalDomain[] { C3.v(), C2.v() },
                                                                                                                                                 new PhysicalDomain[] { C2.v(), C1.v() })),
                                                                                      jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                                     new PhysicalDomain[] { V1.v() },
                                                                                                                     new PhysicalDomain[] { V2.v() })),
                                                     new PhysicalDomain[] { H2.v() },
                                                     new PhysicalDomain[] { H1.v() }));
        pt.eqUnion(jedd.internal.Jedd.v().replace(ptFromLoad,
                                                  new PhysicalDomain[] { V2.v(), C2.v(), H1.v() },
                                                  new PhysicalDomain[] { V1.v(), C3.v(), H2.v() }));
        outputPt(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v(), obj.v(), objc.v() },
                                                     new PhysicalDomain[] { V2.v(), C1.v(), H1.v(), C2.v() },
                                                     ("outputPt(jedd.internal.Jedd.v().replace(ptFromLoad, new jedd" +
                                                      ".PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /tmp" +
                                                      "/soot-trunk-saved/src/soot/jimple/paddle/PropBDDInc.jedd:61," +
                                                      "8-16"),
                                                     jedd.internal.Jedd.v().replace(ptFromLoad,
                                                                                    new PhysicalDomain[] { C2.v(), C1.v() },
                                                                                    new PhysicalDomain[] { C1.v(), C2.v() })));
        if (PaddleScene.v().options().verbose()) { G.v().out.println("Major iteration: "); }
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pt), veryOldPt);
    }
}
