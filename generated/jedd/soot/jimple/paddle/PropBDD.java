package soot.jimple.paddle;

import soot.*;
import soot.util.queue.*;
import java.util.*;
import soot.options.PaddleOptions;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.queue.*;
import jedd.*;

public class PropBDD extends AbsPropagator {
    public PropBDD(Rsrcc_src_dstc_dst simple,
                   Rsrcc_src_fld_dstc_dst load,
                   Rsrcc_src_dstc_dst_fld store,
                   Robjc_obj_varc_var alloc,
                   Qvarc_var_objc_obj propout,
                   AbsPAG pag) {
        super(simple, load, store, alloc, propout, pag);
    }
    
    final jedd.internal.RelationContainer pt =
      new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                          new PhysicalDomain[] { C2.v(), V1.v(), C1.v(), H2.v() },
                                          ("<soot.jimple.paddle.bdddomains.varc, soot.jimple.paddle.bddd" +
                                           "omains.var, soot.jimple.paddle.bdddomains.objc, soot.jimple." +
                                           "paddle.bdddomains.obj> pt = jedd.internal.Jedd.v().falseBDD(" +
                                           ") at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/PropBDD." +
                                           "jedd:39,16-38"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    final jedd.internal.RelationContainer fieldPt =
      new jedd.internal.RelationContainer(new Attribute[] { basec.v(), base.v(), fld.v(), objc.v(), obj.v() },
                                          new PhysicalDomain[] { C1.v(), H1.v(), FD.v(), C2.v(), H2.v() },
                                          ("<soot.jimple.paddle.bdddomains.basec:soot.jimple.paddle.bddd" +
                                           "omains.C1, soot.jimple.paddle.bdddomains.base, soot.jimple.p" +
                                           "addle.bdddomains.fld, soot.jimple.paddle.bdddomains.objc:soo" +
                                           "t.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains" +
                                           ".obj> fieldPt = jedd.internal.Jedd.v().falseBDD() at /tmp/ol" +
                                           "hotak/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:40,16-5" +
                                           "1"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    protected jedd.internal.RelationContainer typeFilter() {
        final jedd.internal.RelationContainer allContexts =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v() },
                                              new PhysicalDomain[] { C2.v(), C1.v() },
                                              ("final <soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle" +
                                               ".bdddomains.C2, soot.jimple.paddle.bdddomains.objc:soot.jimp" +
                                               "le.paddle.bdddomains.C1> allContexts = jedd.internal.Jedd.v(" +
                                               ").trueBDD(); at /tmp/olhotak/soot-trunk/src/soot/jimple/padd" +
                                               "le/PropBDD.jedd:43,27-38"),
                                              jedd.internal.Jedd.v().trueBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v(), objc.v(), varc.v() },
                                                   new PhysicalDomain[] { V2.v(), H2.v(), C1.v(), C2.v() },
                                                   ("return jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v()" +
                                                    ".join(jedd.internal.Jedd.v().read(soot.jimple.paddle.PaddleS" +
                                                    "cene.v().tm.get()), allContexts, new jedd.PhysicalDomain[..." +
                                                    "]), new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[.." +
                                                    ".]); at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/PropB" +
                                                    "DD.jedd:44,8-14"),
                                                   jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(PaddleScene.v().tm.get()),
                                                                                                              allContexts,
                                                                                                              new PhysicalDomain[] {  }),
                                                                                  new PhysicalDomain[] { V1.v() },
                                                                                  new PhysicalDomain[] { V2.v() }));
    }
    
    protected final jedd.internal.RelationContainer outputtedPt =
      new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                          new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H2.v() },
                                          ("protected <soot.jimple.paddle.bdddomains.varc, soot.jimple.p" +
                                           "addle.bdddomains.var, soot.jimple.paddle.bdddomains.objc, so" +
                                           "ot.jimple.paddle.bdddomains.obj> outputtedPt = jedd.internal" +
                                           ".Jedd.v().falseBDD() at /tmp/olhotak/soot-trunk/src/soot/jim" +
                                           "ple/paddle/PropBDD.jedd:47,14-36"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    protected void outputPt(final jedd.internal.RelationContainer pt) {
        final jedd.internal.RelationContainer toOutput =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> toOutput = jedd.internal." +
                                               "Jedd.v().minus(jedd.internal.Jedd.v().read(jedd.internal.Jed" +
                                               "d.v().replace(pt, new jedd.PhysicalDomain[...], new jedd.Phy" +
                                               "sicalDomain[...])), outputtedPt); at /tmp/olhotak/soot-trunk" +
                                               "/src/soot/jimple/paddle/PropBDD.jedd:49,31-39"),
                                              jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(pt,
                                                                                                                                      new PhysicalDomain[] { C1.v(), C2.v() },
                                                                                                                                      new PhysicalDomain[] { C2.v(), C1.v() })),
                                                                           outputtedPt));
        if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(toOutput), jedd.internal.Jedd.v().falseBDD()))
            return;
        ptout.add(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                      new PhysicalDomain[] { C2.v(), V1.v(), C1.v(), H1.v() },
                                                      ("ptout.add(jedd.internal.Jedd.v().replace(toOutput, new jedd." +
                                                       "PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /tmp/" +
                                                       "olhotak/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:51,8-" +
                                                       "13"),
                                                      jedd.internal.Jedd.v().replace(toOutput,
                                                                                     new PhysicalDomain[] { H2.v() },
                                                                                     new PhysicalDomain[] { H1.v() })));
        outputtedPt.eqUnion(toOutput);
    }
    
    protected jedd.internal.RelationContainer propSimple(final jedd.internal.RelationContainer pt,
                                                         final jedd.internal.RelationContainer simple) {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C2.v(), V1.v(), C1.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> ret = jedd.internal.Jedd." +
                                               "v().falseBDD(); at /tmp/olhotak/soot-trunk/src/soot/jimple/p" +
                                               "addle/PropBDD.jedd:61,31-34"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (true) {
            pt.eq(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(typeFilter()),
                                                                                  jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(simple,
                                                                                                                                                                            new PhysicalDomain[] { C1.v() },
                                                                                                                                                                            new PhysicalDomain[] { C3.v() })),
                                                                                                                 pt,
                                                                                                                 new PhysicalDomain[] { C3.v(), V1.v() })),
                                                 new PhysicalDomain[] { V2.v(), C2.v() },
                                                 new PhysicalDomain[] { V1.v(), C3.v() }));
            pt.eqMinus(jedd.internal.Jedd.v().replace(ret,
                                                      new PhysicalDomain[] { C2.v() },
                                                      new PhysicalDomain[] { C3.v() }));
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pt), jedd.internal.Jedd.v().falseBDD()))
                break;
            ret.eqUnion(jedd.internal.Jedd.v().replace(pt,
                                                       new PhysicalDomain[] { C3.v() },
                                                       new PhysicalDomain[] { C2.v() }));
        }
        return new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                   new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H2.v() },
                                                   ("return ret; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                    "e/PropBDD.jedd:71,8-14"),
                                                   ret);
    }
    
    protected jedd.internal.RelationContainer propStore(final jedd.internal.RelationContainer pt,
                                                        final jedd.internal.RelationContainer store,
                                                        final jedd.internal.RelationContainer storePt) {
        final jedd.internal.RelationContainer objectsBeingStored =
          new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v(), varc.v(), var.v(), fld.v() },
                                              new PhysicalDomain[] { C3.v(), H2.v(), C2.v(), V1.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.objc:soot.jimple.paddle.bdddo" +
                                               "mains.C3, soot.jimple.paddle.bdddomains.obj:soot.jimple.padd" +
                                               "le.bdddomains.H2, soot.jimple.paddle.bdddomains.varc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.var" +
                                               ":soot.jimple.paddle.bdddomains.V1, soot.jimple.paddle.bdddom" +
                                               "ains.fld:soot.jimple.paddle.bdddomains.FD> objectsBeingStore" +
                                               "d = jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().co" +
                                               "mpose(jedd.internal.Jedd.v().read(store), pt, new jedd.Physi" +
                                               "calDomain[...]), new jedd.PhysicalDomain[...], new jedd.Phys" +
                                               "icalDomain[...]); at /tmp/olhotak/soot-trunk/src/soot/jimple" +
                                               "/paddle/PropBDD.jedd:80,39-57"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(store),
                                                                                                            pt,
                                                                                                            new PhysicalDomain[] { C1.v(), V1.v() }),
                                                                             new PhysicalDomain[] { V2.v() },
                                                                             new PhysicalDomain[] { V1.v() }));
        return new jedd.internal.RelationContainer(new Attribute[] { fld.v(), objc.v(), obj.v(), base.v(), basec.v() },
                                                   new PhysicalDomain[] { FD.v(), C2.v(), H2.v(), H1.v(), C1.v() },
                                                   ("return jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v()" +
                                                    ".compose(jedd.internal.Jedd.v().read(objectsBeingStored), je" +
                                                    "dd.internal.Jedd.v().replace(storePt, new jedd.PhysicalDomai" +
                                                    "n[...], new jedd.PhysicalDomain[...]), new jedd.PhysicalDoma" +
                                                    "in[...]), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                    "ain[...]); at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle" +
                                                    "/PropBDD.jedd:84,8-14"),
                                                   jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(objectsBeingStored),
                                                                                                                 jedd.internal.Jedd.v().replace(storePt,
                                                                                                                                                new PhysicalDomain[] { H2.v() },
                                                                                                                                                new PhysicalDomain[] { H1.v() }),
                                                                                                                 new PhysicalDomain[] { C2.v(), V1.v() }),
                                                                                  new PhysicalDomain[] { C3.v() },
                                                                                  new PhysicalDomain[] { C2.v() }));
    }
    
    protected jedd.internal.RelationContainer propLoad(final jedd.internal.RelationContainer fpt,
                                                       final jedd.internal.RelationContainer load,
                                                       final jedd.internal.RelationContainer loadPt) {
        final jedd.internal.RelationContainer loadsFromHeap =
          new jedd.internal.RelationContainer(new Attribute[] { basec.v(), base.v(), fld.v(), dstc.v(), dst.v() },
                                              new PhysicalDomain[] { C1.v(), H2.v(), FD.v(), C3.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.basec:soot.jimple.paddle.bddd" +
                                               "omains.C1, soot.jimple.paddle.bdddomains.base:soot.jimple.pa" +
                                               "ddle.bdddomains.H2, soot.jimple.paddle.bdddomains.fld:soot.j" +
                                               "imple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.ds" +
                                               "tc:soot.jimple.paddle.bdddomains.C3, soot.jimple.paddle.bddd" +
                                               "omains.dst:soot.jimple.paddle.bdddomains.V2> loadsFromHeap; " +
                                               "at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/PropBDD.je" +
                                               "dd:94,41-54"));
        loadsFromHeap.eq(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(load),
                                                        loadPt,
                                                        new PhysicalDomain[] { C2.v(), V1.v() }));
        return new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v(), objc.v(), obj.v() },
                                                   new PhysicalDomain[] { V1.v(), C2.v(), C1.v(), H2.v() },
                                                   ("return jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v()" +
                                                    ".compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v()." +
                                                    "replace(loadsFromHeap, new jedd.PhysicalDomain[...], new jed" +
                                                    "d.PhysicalDomain[...])), fpt, new jedd.PhysicalDomain[...])," +
                                                    " new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...])" +
                                                    "; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/PropBDD." +
                                                    "jedd:98,8-14"),
                                                   jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(loadsFromHeap,
                                                                                                                                                                            new PhysicalDomain[] { V2.v(), H2.v() },
                                                                                                                                                                            new PhysicalDomain[] { V1.v(), H1.v() })),
                                                                                                                 fpt,
                                                                                                                 new PhysicalDomain[] { C1.v(), H1.v(), FD.v() }),
                                                                                  new PhysicalDomain[] { C3.v(), C2.v() },
                                                                                  new PhysicalDomain[] { C2.v(), C1.v() }));
    }
    
    public boolean update() {
        final jedd.internal.RelationContainer veryOldPt =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C2.v(), V1.v(), C1.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H2> veryOldPt = pt; at /tmp/o" +
                                               "lhotak/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:104,31" +
                                               "-40"),
                                              pt);
        pt.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(newAlloc.get()),
                                                                                   jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                                  new PhysicalDomain[] { C2.v(), V2.v(), C1.v(), H2.v() },
                                                                                                                  new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() })),
                                                  new PhysicalDomain[] { C2.v(), C1.v(), H1.v() },
                                                  new PhysicalDomain[] { C1.v(), C2.v(), H2.v() }));
        pt.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(propSimple(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                                                                                                                                                             new PhysicalDomain[] { C1.v(), V1.v(), C3.v(), H2.v() },
                                                                                                                                                                                             ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                                              "calDomain[...], new jedd.PhysicalDomain[...]), pag.allSimple" +
                                                                                                                                                                                              "().get()) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/" +
                                                                                                                                                                                              "PropBDD.jedd:107,14-24"),
                                                                                                                                                                                             jedd.internal.Jedd.v().replace(pt,
                                                                                                                                                                                                                            new PhysicalDomain[] { C2.v() },
                                                                                                                                                                                                                            new PhysicalDomain[] { C3.v() })),
                                                                                                                                                         new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                                                                                                                                                             new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                                                                                                                                                                             ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                                              "calDomain[...], new jedd.PhysicalDomain[...]), pag.allSimple" +
                                                                                                                                                                                              "().get()) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/" +
                                                                                                                                                                                              "PropBDD.jedd:107,14-24"),
                                                                                                                                                                                             pag.allSimple().get())),
                                                                                                                                              new PhysicalDomain[] { V1.v() },
                                                                                                                                              new PhysicalDomain[] { V2.v() })),
                                                                                   typeFilter()),
                                                  new PhysicalDomain[] { V2.v() },
                                                  new PhysicalDomain[] { V1.v() }));
        outputPt(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                     new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H2.v() },
                                                     ("outputPt(pt) at /tmp/olhotak/soot-trunk/src/soot/jimple/padd" +
                                                      "le/PropBDD.jedd:108,8-16"),
                                                     pt));
        fieldPt.eqUnion(propStore(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                                      new PhysicalDomain[] { C3.v(), V1.v(), C1.v(), H2.v() },
                                                                      ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                       "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                       ".get(), pt) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                                       "e/PropBDD.jedd:110,19-28"),
                                                                      jedd.internal.Jedd.v().replace(pt,
                                                                                                     new PhysicalDomain[] { C1.v(), C2.v() },
                                                                                                     new PhysicalDomain[] { C3.v(), C1.v() })),
                                  new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v(), fld.v() },
                                                                      new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v(), FD.v() },
                                                                      ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                       "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                       ".get(), pt) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                                       "e/PropBDD.jedd:110,19-28"),
                                                                      pag.allStore().get()),
                                  new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                                      new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H2.v() },
                                                                      ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                       "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                       ".get(), pt) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                                       "e/PropBDD.jedd:110,19-28"),
                                                                      pt)));
        pt.eqUnion(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(propLoad(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), objc.v(), base.v(), basec.v(), obj.v() },
                                                                                                                             new PhysicalDomain[] { FD.v(), C2.v(), H1.v(), C1.v(), H2.v() },
                                                                                                                             ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                              "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                              "ain[...]), pt) at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                                                                                              "ddle/PropBDD.jedd:111,14-22"),
                                                                                                                             fieldPt),
                                                                                         new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                                                                                                             new PhysicalDomain[] { C2.v(), V1.v(), FD.v(), C3.v(), V2.v() },
                                                                                                                             ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                              "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                              "ain[...]), pt) at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                                                                                              "ddle/PropBDD.jedd:111,14-22"),
                                                                                                                             jedd.internal.Jedd.v().replace(pag.allLoad().get(),
                                                                                                                                                            new PhysicalDomain[] { C1.v(), C2.v() },
                                                                                                                                                            new PhysicalDomain[] { C2.v(), C3.v() })),
                                                                                         new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                                                                                             new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H2.v() },
                                                                                                                             ("propLoad(fieldPt, jedd.internal.Jedd.v().replace(pag.allLoad" +
                                                                                                                              "().get(), new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                                                                                                              "ain[...]), pt) at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                                                                                              "ddle/PropBDD.jedd:111,14-22"),
                                                                                                                             pt))),
                                                    jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                   new PhysicalDomain[] { V2.v() },
                                                                                   new PhysicalDomain[] { V1.v() })));
        outputPt(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                     new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H2.v() },
                                                     ("outputPt(pt) at /tmp/olhotak/soot-trunk/src/soot/jimple/padd" +
                                                      "le/PropBDD.jedd:113,8-16"),
                                                     pt));
        if (PaddleScene.v().options().verbose()) { G.v().out.println("Major iteration: "); }
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pt), veryOldPt);
    }
}
