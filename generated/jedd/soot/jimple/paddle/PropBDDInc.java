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
                      Rsrcc_src_fld_dstc_dst store,
                      Robjc_obj_varc_var alloc,
                      Qvarc_var_objc_obj propout,
                      AbsPAG pag) {
        super(simple, load, store, alloc, propout, pag);
    }
    
    public final boolean update() {
        final jedd.internal.RelationContainer ptFromLoad =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V2.v(), C2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> ptFromLoad = jedd.interna" +
                                               "l.Jedd.v().falseBDD(); at /home/research/ccl/olhota/soot-tru" +
                                               "nk/src/soot/jimple/paddle/PropBDDInc.jedd:40,31-41"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (true) {
            final jedd.internal.RelationContainer veryOldPt =
              new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                                  new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                                  ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                                   "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                                   "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                                   "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                                   ":soot.jimple.paddle.bdddomains.H1> veryOldPt = pt; at /home/" +
                                                   "research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBD" +
                                                   "DInc.jedd:43,35-44"),
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
                                                   ".PhysicalDomain[...], new jedd.PhysicalDomain[...])); at /ho" +
                                                   "me/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/Pro" +
                                                   "pBDDInc.jedd:46,16-27"),
                                                  jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(newAlloc.get()),
                                                                                   jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                                  new PhysicalDomain[] { C3.v(), V2.v(), H2.v() },
                                                                                                                  new PhysicalDomain[] { C1.v(), V1.v(), H1.v() })));
            final jedd.internal.RelationContainer ptFromSimple1 =
              new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                                  new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                                  ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                                   "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                                   "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                                   "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                                   ":soot.jimple.paddle.bdddomains.H1> ptFromSimple1 = jedd.inte" +
                                                   "rnal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd." +
                                                   "internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propSi" +
                                                   "mple(new jedd.internal.RelationContainer(...), new jedd.inte" +
                                                   "rnal.RelationContainer(...)), new jedd.PhysicalDomain[...], " +
                                                   "new jedd.PhysicalDomain[...])), jedd.internal.Jedd.v().repla" +
                                                   "ce(typeFilter(), new jedd.PhysicalDomain[...], new jedd.Phys" +
                                                   "icalDomain[...])), new jedd.PhysicalDomain[...], new jedd.Ph" +
                                                   "ysicalDomain[...]); at /home/research/ccl/olhota/soot-trunk/" +
                                                   "src/soot/jimple/paddle/PropBDDInc.jedd:48,16-29"),
                                                  jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propSimple(new jedd.internal.RelationContainer(new Attribute[] { var.v(), objc.v(), varc.v(), obj.v() },
                                                                                                                                                                                                                            new PhysicalDomain[] { V2.v(), C2.v(), C1.v(), H2.v() },
                                                                                                                                                                                                                            ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                                                                             "calDomain[...], new jedd.PhysicalDomain[...]), newSimple.get" +
                                                                                                                                                                                                                             "()) at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/" +
                                                                                                                                                                                                                             "paddle/PropBDDInc.jedd:48,32-42"),
                                                                                                                                                                                                                            jedd.internal.Jedd.v().replace(pt,
                                                                                                                                                                                                                                                           new PhysicalDomain[] { V1.v(), H1.v() },
                                                                                                                                                                                                                                                           new PhysicalDomain[] { V2.v(), H2.v() })),
                                                                                                                                                                                        new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                                                                                                                                                                                            new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                                                                                                                                                                                                            ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                                                                             "calDomain[...], new jedd.PhysicalDomain[...]), newSimple.get" +
                                                                                                                                                                                                                             "()) at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/" +
                                                                                                                                                                                                                             "paddle/PropBDDInc.jedd:48,32-42"),
                                                                                                                                                                                                                            newSimple.get())),
                                                                                                                                                                             new PhysicalDomain[] { V1.v(), H1.v() },
                                                                                                                                                                             new PhysicalDomain[] { V2.v(), H2.v() })),
                                                                                                                  jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                                                                 new PhysicalDomain[] { C3.v() },
                                                                                                                                                 new PhysicalDomain[] { C1.v() })),
                                                                                 new PhysicalDomain[] { V2.v(), H2.v() },
                                                                                 new PhysicalDomain[] { V1.v(), H1.v() }));
            final jedd.internal.RelationContainer ptFromAllocAndSimple1 =
              new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                                  new PhysicalDomain[] { C3.v(), V1.v(), C2.v(), H2.v() },
                                                  ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                                   "mains.C3, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                                   "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                                   "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                                   ":soot.jimple.paddle.bdddomains.H2> ptFromAllocAndSimple1 = j" +
                                                   "edd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(j" +
                                                   "edd.internal.Jedd.v().read(ptFromAlloc), ptFromSimple1), new" +
                                                   " jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]); at" +
                                                   " /home/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle" +
                                                   "/PropBDDInc.jedd:50,16-37"),
                                                  jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(ptFromAlloc),
                                                                                                              ptFromSimple1),
                                                                                 new PhysicalDomain[] { C1.v(), H1.v() },
                                                                                 new PhysicalDomain[] { C3.v(), H2.v() }));
            final jedd.internal.RelationContainer ptFromSimple2 =
              new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                                  new PhysicalDomain[] { C3.v(), V1.v(), C2.v(), H1.v() },
                                                  ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                                   "mains.C3, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                                   "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                                   "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                                   ":soot.jimple.paddle.bdddomains.H1> ptFromSimple2 = jedd.inte" +
                                                   "rnal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.int" +
                                                   "ernal.Jedd.v().replace(propSimple(new jedd.internal.Relation" +
                                                   "Container(...), new jedd.internal.RelationContainer(...)), n" +
                                                   "ew jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]))," +
                                                   " jedd.internal.Jedd.v().replace(typeFilter(), new jedd.Physi" +
                                                   "calDomain[...], new jedd.PhysicalDomain[...])); at /home/res" +
                                                   "earch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDIn" +
                                                   "c.jedd:52,16-29"),
                                                  jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propSimple(new jedd.internal.RelationContainer(new Attribute[] { var.v(), objc.v(), varc.v(), obj.v() },
                                                                                                                                                                                             new PhysicalDomain[] { V2.v(), C2.v(), C1.v(), H2.v() },
                                                                                                                                                                                             ("propSimple(jedd.internal.Jedd.v().replace(jedd.internal.Jedd" +
                                                                                                                                                                                              ".v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v(" +
                                                                                                                                                                                              ").replace(ptFromAllocAndSimple1, new jedd.PhysicalDomain[..." +
                                                                                                                                                                                              "], new jedd.PhysicalDomain[...])), jedd.internal.Jedd.v().re" +
                                                                                                                                                                                              "place(ptFromLoad, new jedd.PhysicalDomain[...], new jedd.Phy" +
                                                                                                                                                                                              "sicalDomain[...])), new jedd.PhysicalDomain[...], new jedd.P" +
                                                                                                                                                                                              "hysicalDomain[...]), pag.allSimple().get()) at /home/researc" +
                                                                                                                                                                                              "h/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDInc.je" +
                                                                                                                                                                                              "dd:52,32-42"),
                                                                                                                                                                                             jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ptFromAllocAndSimple1,
                                                                                                                                                                                                                                                                                                                    new PhysicalDomain[] { V1.v() },
                                                                                                                                                                                                                                                                                                                    new PhysicalDomain[] { V2.v() })),
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
                                                                                                                                                                                              "dd:52,32-42"),
                                                                                                                                                                                             pag.allSimple().get())),
                                                                                                                                              new PhysicalDomain[] { C1.v() },
                                                                                                                                              new PhysicalDomain[] { C3.v() })),
                                                                                   jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                                  new PhysicalDomain[] { V2.v(), H2.v() },
                                                                                                                  new PhysicalDomain[] { V1.v(), H1.v() })));
            final jedd.internal.RelationContainer ptFromAllocAndSimple =
              new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                                  new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                                  ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                                   "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                                   "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                                   "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                                   ":soot.jimple.paddle.bdddomains.H1> ptFromAllocAndSimple = je" +
                                                   "dd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(je" +
                                                   "dd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ptF" +
                                                   "romAllocAndSimple1, new jedd.PhysicalDomain[...], new jedd.P" +
                                                   "hysicalDomain[...])), ptFromSimple2), new jedd.PhysicalDomai" +
                                                   "n[...], new jedd.PhysicalDomain[...]); at /home/research/ccl" +
                                                   "/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDInc.jedd:55" +
                                                   ",16-36"),
                                                  jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ptFromAllocAndSimple1,
                                                                                                                                                                         new PhysicalDomain[] { H2.v() },
                                                                                                                                                                         new PhysicalDomain[] { H1.v() })),
                                                                                                              ptFromSimple2),
                                                                                 new PhysicalDomain[] { C3.v() },
                                                                                 new PhysicalDomain[] { C1.v() }));
            pt.eqUnion(ptFromAllocAndSimple);
            outputPt(new jedd.internal.RelationContainer(new Attribute[] { var.v(), objc.v(), varc.v(), obj.v() },
                                                         new PhysicalDomain[] { V2.v(), C2.v(), C1.v(), H1.v() },
                                                         ("outputPt(jedd.internal.Jedd.v().replace(ptFromAllocAndSimple" +
                                                          ", new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]" +
                                                          ")) at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/p" +
                                                          "addle/PropBDDInc.jedd:57,12-20"),
                                                         jedd.internal.Jedd.v().replace(ptFromAllocAndSimple,
                                                                                        new PhysicalDomain[] { V1.v() },
                                                                                        new PhysicalDomain[] { V2.v() })));
            fieldPt.eqUnion(propStore(new jedd.internal.RelationContainer(new Attribute[] { var.v(), objc.v(), varc.v(), obj.v() },
                                                                          new PhysicalDomain[] { V1.v(), C3.v(), C1.v(), H1.v() },
                                                                          ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                           "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                           ".get(), jedd.internal.Jedd.v().replace(pt, new jedd.Physical" +
                                                                           "Domain[...], new jedd.PhysicalDomain[...])) at /home/researc" +
                                                                           "h/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDInc.je" +
                                                                           "dd:59,23-32"),
                                                                          jedd.internal.Jedd.v().replace(pt,
                                                                                                         new PhysicalDomain[] { C2.v() },
                                                                                                         new PhysicalDomain[] { C3.v() })),
                                      new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                                                          new PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                                                          ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                           "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                           ".get(), jedd.internal.Jedd.v().replace(pt, new jedd.Physical" +
                                                                           "Domain[...], new jedd.PhysicalDomain[...])) at /home/researc" +
                                                                           "h/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDInc.je" +
                                                                           "dd:59,23-32"),
                                                                          pag.allStore().get()),
                                      new jedd.internal.RelationContainer(new Attribute[] { var.v(), objc.v(), varc.v(), obj.v() },
                                                                          new PhysicalDomain[] { V2.v(), C2.v(), C1.v(), H1.v() },
                                                                          ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                           "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                           ".get(), jedd.internal.Jedd.v().replace(pt, new jedd.Physical" +
                                                                           "Domain[...], new jedd.PhysicalDomain[...])) at /home/researc" +
                                                                           "h/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDDInc.je" +
                                                                           "dd:59,23-32"),
                                                                          jedd.internal.Jedd.v().replace(pt,
                                                                                                         new PhysicalDomain[] { V1.v() },
                                                                                                         new PhysicalDomain[] { V2.v() }))));
            ptFromLoad.eq(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propLoad(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), base.v(), basec.v(), fld.v(), obj.v() },
                                                                                                                                                                   new PhysicalDomain[] { C2.v(), H1.v(), C1.v(), FD.v(), H2.v() },
                                                                                                                                                                   ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                                                                    "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                                                                    "ain[...]), pt) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                                                                                                                                    "oot/jimple/paddle/PropBDDInc.jedd:60,25-33"),
                                                                                                                                                                   fieldPt),
                                                                                                                               new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                                                                                                                                                   new PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C3.v(), V2.v() },
                                                                                                                                                                   ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                                                                    "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                                                                    "ain[...]), pt) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                                                                                                                                    "oot/jimple/paddle/PropBDDInc.jedd:60,25-33"),
                                                                                                                                                                   jedd.internal.Jedd.v().replace(pag.allLoad().get(),
                                                                                                                                                                                                  new PhysicalDomain[] { C2.v() },
                                                                                                                                                                                                  new PhysicalDomain[] { C3.v() })),
                                                                                                                               new jedd.internal.RelationContainer(new Attribute[] { var.v(), objc.v(), varc.v(), obj.v() },
                                                                                                                                                                   new PhysicalDomain[] { V1.v(), C2.v(), C1.v(), H1.v() },
                                                                                                                                                                   ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                                                                    "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                                                                    "ain[...]), pt) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                                                                                                                                    "oot/jimple/paddle/PropBDDInc.jedd:60,25-33"),
                                                                                                                                                                   pt)),
                                                                                                                      new PhysicalDomain[] { C3.v() },
                                                                                                                      new PhysicalDomain[] { C1.v() })),
                                                           jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                          new PhysicalDomain[] { C3.v() },
                                                                                          new PhysicalDomain[] { C1.v() })));
            pt.eqUnion(jedd.internal.Jedd.v().replace(ptFromLoad,
                                                      new PhysicalDomain[] { V2.v(), H2.v() },
                                                      new PhysicalDomain[] { V1.v(), H1.v() }));
            outputPt(new jedd.internal.RelationContainer(new Attribute[] { var.v(), objc.v(), varc.v(), obj.v() },
                                                         new PhysicalDomain[] { V2.v(), C2.v(), C1.v(), H1.v() },
                                                         ("outputPt(jedd.internal.Jedd.v().replace(ptFromLoad, new jedd" +
                                                          ".PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /hom" +
                                                          "e/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/Prop" +
                                                          "BDDInc.jedd:62,12-20"),
                                                         jedd.internal.Jedd.v().replace(ptFromLoad,
                                                                                        new PhysicalDomain[] { H2.v() },
                                                                                        new PhysicalDomain[] { H1.v() })));
            if (PaddleScene.v().options().verbose()) { G.v().out.println("Major iteration: "); }
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pt), veryOldPt)) break;
        }
        return true;
    }
}
