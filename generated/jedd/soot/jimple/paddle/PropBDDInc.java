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
    
    private final jedd.internal.RelationContainer ptFromLoad =
      new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                          new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.varc, soot.jimple.pad" +
                                           "dle.bdddomains.var, soot.jimple.paddle.bdddomains.objc, soot" +
                                           ".jimple.paddle.bdddomains.obj> ptFromLoad = jedd.internal.Je" +
                                           "dd.v().falseBDD() at /home/research/ccl/olhota/olhotak/soot-" +
                                           "trunk/src/soot/jimple/paddle/PropBDDInc.jedd:39,12-34"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public final boolean update() {
        final jedd.internal.RelationContainer veryOldPt =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V2.v(), C2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> veryOldPt = pt; at /home/" +
                                               "research/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                               "e/PropBDDInc.jedd:43,31-40"),
                                              pt);
        final jedd.internal.RelationContainer ptFromAlloc =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C3.v(), V2.v(), C2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C3, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> ptFromAlloc = jedd.intern" +
                                               "al.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.inter" +
                                               "nal.Jedd.v().replace(newAlloc.get(), new jedd.PhysicalDomain" +
                                               "[...], new jedd.PhysicalDomain[...])), jedd.internal.Jedd.v(" +
                                               ").replace(typeFilter(), new jedd.PhysicalDomain[...], new je" +
                                               "dd.PhysicalDomain[...])); at /home/research/ccl/olhota/olhot" +
                                               "ak/soot-trunk/src/soot/jimple/paddle/PropBDDInc.jedd:46,12-2" +
                                               "3"),
                                              jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(newAlloc.get(),
                                                                                                                                          new PhysicalDomain[] { H1.v(), C1.v(), V1.v() },
                                                                                                                                          new PhysicalDomain[] { H2.v(), C3.v(), V2.v() })),
                                                                               jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                              new PhysicalDomain[] { C1.v(), V1.v(), H1.v() },
                                                                                                              new PhysicalDomain[] { C3.v(), V2.v(), H2.v() })));
        final jedd.internal.RelationContainer ptFromSimple1 =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C3.v(), V2.v(), C2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C3, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> ptFromSimple1 = jedd.inte" +
                                               "rnal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd." +
                                               "internal.Jedd.v().read(propSimple(new jedd.internal.Relation" +
                                               "Container(...), new jedd.internal.RelationContainer(...))), " +
                                               "jedd.internal.Jedd.v().replace(typeFilter(), new jedd.Physic" +
                                               "alDomain[...], new jedd.PhysicalDomain[...])), new jedd.Phys" +
                                               "icalDomain[...], new jedd.PhysicalDomain[...]); at /home/res" +
                                               "earch/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/P" +
                                               "ropBDDInc.jedd:48,12-25"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(propSimple(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                                                                                                                                                         new PhysicalDomain[] { C2.v(), V2.v(), C3.v(), H2.v() },
                                                                                                                                                                                         ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                                          "calDomain[...], new jedd.PhysicalDomain[...]), jedd.internal" +
                                                                                                                                                                                          ".Jedd.v().replace(newSimple.get(), new jedd.PhysicalDomain[." +
                                                                                                                                                                                          "..], new jedd.PhysicalDomain[...])) at /home/research/ccl/ol" +
                                                                                                                                                                                          "hota/olhotak/soot-trunk/src/soot/jimple/paddle/PropBDDInc.je" +
                                                                                                                                                                                          "dd:48,28-38"),
                                                                                                                                                                                         jedd.internal.Jedd.v().replace(pt,
                                                                                                                                                                                                                        new PhysicalDomain[] { C1.v() },
                                                                                                                                                                                                                        new PhysicalDomain[] { C3.v() })),
                                                                                                                                                     new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                                                                                                                                                         new PhysicalDomain[] { C3.v(), V1.v(), C1.v(), V2.v() },
                                                                                                                                                                                         ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                                          "calDomain[...], new jedd.PhysicalDomain[...]), jedd.internal" +
                                                                                                                                                                                          ".Jedd.v().replace(newSimple.get(), new jedd.PhysicalDomain[." +
                                                                                                                                                                                          "..], new jedd.PhysicalDomain[...])) at /home/research/ccl/ol" +
                                                                                                                                                                                          "hota/olhotak/soot-trunk/src/soot/jimple/paddle/PropBDDInc.je" +
                                                                                                                                                                                          "dd:48,28-38"),
                                                                                                                                                                                         jedd.internal.Jedd.v().replace(newSimple.get(),
                                                                                                                                                                                                                        new PhysicalDomain[] { C1.v(), C2.v() },
                                                                                                                                                                                                                        new PhysicalDomain[] { C3.v(), C1.v() })))),
                                                                                                              jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                                                             new PhysicalDomain[] { V1.v(), H1.v() },
                                                                                                                                             new PhysicalDomain[] { V2.v(), H2.v() })),
                                                                             new PhysicalDomain[] { C1.v() },
                                                                             new PhysicalDomain[] { C3.v() }));
        final jedd.internal.RelationContainer ptFromAllocAndSimple1 =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C3.v(), V2.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C3, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ptFromAllocAndSimple1 = j" +
                                               "edd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(j" +
                                               "edd.internal.Jedd.v().read(ptFromAlloc), ptFromSimple1), new" +
                                               " jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]); at" +
                                               " /home/research/ccl/olhota/olhotak/soot-trunk/src/soot/jimpl" +
                                               "e/paddle/PropBDDInc.jedd:50,12-33"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(ptFromAlloc),
                                                                                                          ptFromSimple1),
                                                                             new PhysicalDomain[] { H2.v() },
                                                                             new PhysicalDomain[] { H1.v() }));
        final jedd.internal.RelationContainer ptFromSimple2 =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V2.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ptFromSimple2 = jedd.inte" +
                                               "rnal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.int" +
                                               "ernal.Jedd.v().replace(propSimple(new jedd.internal.Relation" +
                                               "Container(...), new jedd.internal.RelationContainer(...)), n" +
                                               "ew jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]))," +
                                               " jedd.internal.Jedd.v().replace(typeFilter(), new jedd.Physi" +
                                               "calDomain[...], new jedd.PhysicalDomain[...])); at /home/res" +
                                               "earch/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/P" +
                                               "ropBDDInc.jedd:52,12-25"),
                                              jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propSimple(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                                                                                                                                                         new PhysicalDomain[] { C2.v(), V2.v(), C3.v(), H2.v() },
                                                                                                                                                                                         ("propSimple(jedd.internal.Jedd.v().replace(jedd.internal.Jedd" +
                                                                                                                                                                                          ".v().union(jedd.internal.Jedd.v().read(ptFromAllocAndSimple1" +
                                                                                                                                                                                          "), jedd.internal.Jedd.v().replace(ptFromLoad, new jedd.Physi" +
                                                                                                                                                                                          "calDomain[...], new jedd.PhysicalDomain[...])), new jedd.Phy" +
                                                                                                                                                                                          "sicalDomain[...], new jedd.PhysicalDomain[...]), jedd.intern" +
                                                                                                                                                                                          "al.Jedd.v().replace(pag.allSimple().get(), new jedd.Physical" +
                                                                                                                                                                                          "Domain[...], new jedd.PhysicalDomain[...])) at /home/researc" +
                                                                                                                                                                                          "h/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/PropB" +
                                                                                                                                                                                          "DDInc.jedd:52,28-38"),
                                                                                                                                                                                         jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(ptFromAllocAndSimple1),
                                                                                                                                                                                                                                                     jedd.internal.Jedd.v().replace(ptFromLoad,
                                                                                                                                                                                                                                                                                    new PhysicalDomain[] { V1.v(), C1.v() },
                                                                                                                                                                                                                                                                                    new PhysicalDomain[] { V2.v(), C3.v() })),
                                                                                                                                                                                                                        new PhysicalDomain[] { H1.v() },
                                                                                                                                                                                                                        new PhysicalDomain[] { H2.v() })),
                                                                                                                                                     new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                                                                                                                                                         new PhysicalDomain[] { C3.v(), V1.v(), C1.v(), V2.v() },
                                                                                                                                                                                         ("propSimple(jedd.internal.Jedd.v().replace(jedd.internal.Jedd" +
                                                                                                                                                                                          ".v().union(jedd.internal.Jedd.v().read(ptFromAllocAndSimple1" +
                                                                                                                                                                                          "), jedd.internal.Jedd.v().replace(ptFromLoad, new jedd.Physi" +
                                                                                                                                                                                          "calDomain[...], new jedd.PhysicalDomain[...])), new jedd.Phy" +
                                                                                                                                                                                          "sicalDomain[...], new jedd.PhysicalDomain[...]), jedd.intern" +
                                                                                                                                                                                          "al.Jedd.v().replace(pag.allSimple().get(), new jedd.Physical" +
                                                                                                                                                                                          "Domain[...], new jedd.PhysicalDomain[...])) at /home/researc" +
                                                                                                                                                                                          "h/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/PropB" +
                                                                                                                                                                                          "DDInc.jedd:52,28-38"),
                                                                                                                                                                                         jedd.internal.Jedd.v().replace(pag.allSimple().get(),
                                                                                                                                                                                                                        new PhysicalDomain[] { C1.v(), C2.v() },
                                                                                                                                                                                                                        new PhysicalDomain[] { C3.v(), C1.v() }))),
                                                                                                                                          new PhysicalDomain[] { H2.v() },
                                                                                                                                          new PhysicalDomain[] { H1.v() })),
                                                                               jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                              new PhysicalDomain[] { V1.v() },
                                                                                                              new PhysicalDomain[] { V2.v() })));
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
                                               "ple2); at /home/research/ccl/olhota/olhotak/soot-trunk/src/s" +
                                               "oot/jimple/paddle/PropBDDInc.jedd:55,12-32"),
                                              jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ptFromAllocAndSimple1,
                                                                                                                                      new PhysicalDomain[] { C3.v() },
                                                                                                                                      new PhysicalDomain[] { C1.v() })),
                                                                           ptFromSimple2));
        pt.eqUnion(jedd.internal.Jedd.v().replace(ptFromAllocAndSimple,
                                                  new PhysicalDomain[] { H1.v() },
                                                  new PhysicalDomain[] { H2.v() }));
        outputPt(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                     new PhysicalDomain[] { C2.v(), V2.v(), C1.v(), H1.v() },
                                                     ("outputPt(ptFromAllocAndSimple) at /home/research/ccl/olhota/" +
                                                      "olhotak/soot-trunk/src/soot/jimple/paddle/PropBDDInc.jedd:57" +
                                                      ",8-16"),
                                                     ptFromAllocAndSimple));
        fieldPt.eqUnion(propStore(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                                      new PhysicalDomain[] { C2.v(), V2.v(), C1.v(), H2.v() },
                                                                      ("propStore(pt, pag.allStore().get(), jedd.internal.Jedd.v().r" +
                                                                       "eplace(pt, new jedd.PhysicalDomain[...], new jedd.PhysicalDo" +
                                                                       "main[...])) at /home/research/ccl/olhota/olhotak/soot-trunk/" +
                                                                       "src/soot/jimple/paddle/PropBDDInc.jedd:59,19-28"),
                                                                      pt),
                                  new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v(), fld.v() },
                                                                      new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v(), FD.v() },
                                                                      ("propStore(pt, pag.allStore().get(), jedd.internal.Jedd.v().r" +
                                                                       "eplace(pt, new jedd.PhysicalDomain[...], new jedd.PhysicalDo" +
                                                                       "main[...])) at /home/research/ccl/olhota/olhotak/soot-trunk/" +
                                                                       "src/soot/jimple/paddle/PropBDDInc.jedd:59,19-28"),
                                                                      pag.allStore().get()),
                                  new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                                      new PhysicalDomain[] { C2.v(), V2.v(), C1.v(), H1.v() },
                                                                      ("propStore(pt, pag.allStore().get(), jedd.internal.Jedd.v().r" +
                                                                       "eplace(pt, new jedd.PhysicalDomain[...], new jedd.PhysicalDo" +
                                                                       "main[...])) at /home/research/ccl/olhota/olhotak/soot-trunk/" +
                                                                       "src/soot/jimple/paddle/PropBDDInc.jedd:59,19-28"),
                                                                      jedd.internal.Jedd.v().replace(pt,
                                                                                                     new PhysicalDomain[] { H2.v() },
                                                                                                     new PhysicalDomain[] { H1.v() }))));
        ptFromLoad.eq(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propLoad(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), fld.v(), base.v(), obj.v(), basec.v() },
                                                                                                                                                               new PhysicalDomain[] { C2.v(), FD.v(), H1.v(), H2.v(), C1.v() },
                                                                                                                                                               ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                                                                "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                                                                "ain[...]), pt) at /home/research/ccl/olhota/olhotak/soot-tru" +
                                                                                                                                                                "nk/src/soot/jimple/paddle/PropBDDInc.jedd:60,21-29"),
                                                                                                                                                               fieldPt),
                                                                                                                           new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                                                                                                                                               new PhysicalDomain[] { C1.v(), V2.v(), FD.v(), C2.v(), V1.v() },
                                                                                                                                                               ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                                                                "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                                                                "ain[...]), pt) at /home/research/ccl/olhota/olhotak/soot-tru" +
                                                                                                                                                                "nk/src/soot/jimple/paddle/PropBDDInc.jedd:60,21-29"),
                                                                                                                                                               jedd.internal.Jedd.v().replace(pag.allLoad().get(),
                                                                                                                                                                                              new PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                                                                                              new PhysicalDomain[] { V2.v(), V1.v() })),
                                                                                                                           new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                                                                                                                               new PhysicalDomain[] { C2.v(), V2.v(), C1.v(), H2.v() },
                                                                                                                                                               ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                                                                "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                                                                "ain[...]), pt) at /home/research/ccl/olhota/olhotak/soot-tru" +
                                                                                                                                                                "nk/src/soot/jimple/paddle/PropBDDInc.jedd:60,21-29"),
                                                                                                                                                               pt)),
                                                                                                                  new PhysicalDomain[] { H2.v() },
                                                                                                                  new PhysicalDomain[] { H1.v() })),
                                                       typeFilter()));
        pt.eqUnion(jedd.internal.Jedd.v().replace(ptFromLoad,
                                                  new PhysicalDomain[] { V1.v(), H1.v() },
                                                  new PhysicalDomain[] { V2.v(), H2.v() }));
        outputPt(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                     new PhysicalDomain[] { C2.v(), V2.v(), C1.v(), H1.v() },
                                                     ("outputPt(jedd.internal.Jedd.v().replace(ptFromLoad, new jedd" +
                                                      ".PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /hom" +
                                                      "e/research/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/pad" +
                                                      "dle/PropBDDInc.jedd:62,8-16"),
                                                     jedd.internal.Jedd.v().replace(ptFromLoad,
                                                                                    new PhysicalDomain[] { V1.v() },
                                                                                    new PhysicalDomain[] { V2.v() })));
        if (PaddleScene.v().options().verbose()) { G.v().out.println("Major iteration: "); }
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pt), veryOldPt);
    }
}
