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
                                          new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                          ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                           "mains.C1, soot.jimple.paddle.bdddomains.var, soot.jimple.pad" +
                                           "dle.bdddomains.objc:soot.jimple.paddle.bdddomains.C2, soot.j" +
                                           "imple.paddle.bdddomains.obj> pt = jedd.internal.Jedd.v().fal" +
                                           "seBDD() at /home/research/ccl/olhota/soot-trunk/src/soot/jim" +
                                           "ple/paddle/PropBDD.jedd:39,16-44"),
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
                                           ".jedd:40,16-51"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    protected jedd.internal.RelationContainer typeFilter() {
        final jedd.internal.RelationContainer allContexts =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v() },
                                              new PhysicalDomain[] { C1.v(), C2.v() },
                                              ("final <soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle" +
                                               ".bdddomains.C1, soot.jimple.paddle.bdddomains.objc:soot.jimp" +
                                               "le.paddle.bdddomains.C2> allContexts = jedd.internal.Jedd.v(" +
                                               ").trueBDD(); at /home/research/ccl/olhota/soot-trunk/src/soo" +
                                               "t/jimple/paddle/PropBDD.jedd:43,27-38"),
                                              jedd.internal.Jedd.v().trueBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v(), varc.v(), objc.v() },
                                                   new PhysicalDomain[] { H2.v(), V1.v(), C1.v(), C2.v() },
                                                   ("return jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().re" +
                                                    "ad(soot.jimple.paddle.PaddleScene.v().tm.get()), allContexts" +
                                                    ", new jedd.PhysicalDomain[...]); at /home/research/ccl/olhot" +
                                                    "a/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:44,8-14"),
                                                   jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(PaddleScene.v().tm.get()),
                                                                               allContexts,
                                                                               new PhysicalDomain[] {  }));
    }
    
    protected final jedd.internal.RelationContainer outputtedPt =
      new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                          new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                          ("protected <soot.jimple.paddle.bdddomains.varc, soot.jimple.p" +
                                           "addle.bdddomains.var, soot.jimple.paddle.bdddomains.objc, so" +
                                           "ot.jimple.paddle.bdddomains.obj> outputtedPt = jedd.internal" +
                                           ".Jedd.v().falseBDD() at /home/research/ccl/olhota/soot-trunk" +
                                           "/src/soot/jimple/paddle/PropBDD.jedd:47,14-36"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    protected void outputPt(final jedd.internal.RelationContainer pt) {
        final jedd.internal.RelationContainer toOutput =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> toOutput = jedd.internal." +
                                               "Jedd.v().minus(jedd.internal.Jedd.v().read(pt), outputtedPt)" +
                                               "; at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/pa" +
                                               "ddle/PropBDD.jedd:49,31-39"),
                                              jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(pt),
                                                                           outputtedPt));
        if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(toOutput), jedd.internal.Jedd.v().falseBDD()))
            return;
        ptout.add(new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v(), obj.v(), var.v() },
                                                      new PhysicalDomain[] { C1.v(), C2.v(), H1.v(), V1.v() },
                                                      ("ptout.add(toOutput) at /home/research/ccl/olhota/soot-trunk/" +
                                                       "src/soot/jimple/paddle/PropBDD.jedd:51,8-13"),
                                                      toOutput));
        outputtedPt.eqUnion(toOutput);
        PaddleScene.v().updateCallGraph();
    }
    
    protected jedd.internal.RelationContainer propSimple(final jedd.internal.RelationContainer pt,
                                                         final jedd.internal.RelationContainer simple) {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C3.v(), V2.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C3, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ret = jedd.internal.Jedd." +
                                               "v().falseBDD(); at /home/research/ccl/olhota/soot-trunk/src/" +
                                               "soot/jimple/paddle/PropBDD.jedd:61,31-34"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (true) {
            pt.eq(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                                                             new PhysicalDomain[] { V1.v(), H2.v() },
                                                                                                                                             new PhysicalDomain[] { V2.v(), H1.v() })),
                                                                                  jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().replace(simple,
                                                                                                                                                                                                           new PhysicalDomain[] { C1.v() },
                                                                                                                                                                                                           new PhysicalDomain[] { C3.v() }),
                                                                                                                                                                            new PhysicalDomain[] { C2.v() },
                                                                                                                                                                            new PhysicalDomain[] { C1.v() })),
                                                                                                                 jedd.internal.Jedd.v().replace(pt,
                                                                                                                                                new PhysicalDomain[] { V2.v() },
                                                                                                                                                new PhysicalDomain[] { V1.v() }),
                                                                                                                 new PhysicalDomain[] { C3.v(), V1.v() })),
                                                 new PhysicalDomain[] { C1.v() },
                                                 new PhysicalDomain[] { C3.v() }));
            pt.eqMinus(ret);
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pt), jedd.internal.Jedd.v().falseBDD()))
                break;
            ret.eqUnion(pt);
        }
        return new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v(), obj.v(), var.v() },
                                                   new PhysicalDomain[] { C3.v(), C2.v(), H1.v(), V1.v() },
                                                   ("return jedd.internal.Jedd.v().replace(ret, new jedd.Physical" +
                                                    "Domain[...], new jedd.PhysicalDomain[...]); at /home/researc" +
                                                    "h/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:" +
                                                    "71,8-14"),
                                                   jedd.internal.Jedd.v().replace(ret,
                                                                                  new PhysicalDomain[] { V2.v() },
                                                                                  new PhysicalDomain[] { V1.v() }));
    }
    
    protected jedd.internal.RelationContainer propStore(final jedd.internal.RelationContainer pt,
                                                        final jedd.internal.RelationContainer store,
                                                        final jedd.internal.RelationContainer storePt) {
        final jedd.internal.RelationContainer objectsBeingStored =
          new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v(), varc.v(), var.v(), fld.v() },
                                              new PhysicalDomain[] { C3.v(), H2.v(), C2.v(), V2.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.objc:soot.jimple.paddle.bdddo" +
                                               "mains.C3, soot.jimple.paddle.bdddomains.obj:soot.jimple.padd" +
                                               "le.bdddomains.H2, soot.jimple.paddle.bdddomains.varc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.var" +
                                               ":soot.jimple.paddle.bdddomains.V2, soot.jimple.paddle.bdddom" +
                                               "ains.fld:soot.jimple.paddle.bdddomains.FD> objectsBeingStore" +
                                               "d = jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().re" +
                                               "ad(store), jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                               "calDomain[...], new jedd.PhysicalDomain[...]), new jedd.Phys" +
                                               "icalDomain[...]); at /home/research/ccl/olhota/soot-trunk/sr" +
                                               "c/soot/jimple/paddle/PropBDD.jedd:80,39-57"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(store),
                                                                             jedd.internal.Jedd.v().replace(pt,
                                                                                                            new PhysicalDomain[] { C2.v() },
                                                                                                            new PhysicalDomain[] { C3.v() }),
                                                                             new PhysicalDomain[] { C1.v(), V1.v() }));
        return new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v(), fld.v(), basec.v(), base.v() },
                                                   new PhysicalDomain[] { C2.v(), H2.v(), FD.v(), C1.v(), H1.v() },
                                                   ("return jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v()" +
                                                    ".compose(jedd.internal.Jedd.v().read(objectsBeingStored), st" +
                                                    "orePt, new jedd.PhysicalDomain[...]), new jedd.PhysicalDomai" +
                                                    "n[...], new jedd.PhysicalDomain[...]); at /home/research/ccl" +
                                                    "/olhota/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:84,8-" +
                                                    "14"),
                                                   jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(objectsBeingStored),
                                                                                                                 storePt,
                                                                                                                 new PhysicalDomain[] { C2.v(), V2.v() }),
                                                                                  new PhysicalDomain[] { C3.v() },
                                                                                  new PhysicalDomain[] { C2.v() }));
    }
    
    protected jedd.internal.RelationContainer propLoad(final jedd.internal.RelationContainer fpt,
                                                       final jedd.internal.RelationContainer load,
                                                       final jedd.internal.RelationContainer loadPt) {
        final jedd.internal.RelationContainer loadsFromHeap =
          new jedd.internal.RelationContainer(new Attribute[] { basec.v(), base.v(), fld.v(), dstc.v(), dst.v() },
                                              new PhysicalDomain[] { C1.v(), H1.v(), FD.v(), C3.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.basec:soot.jimple.paddle.bddd" +
                                               "omains.C1, soot.jimple.paddle.bdddomains.base:soot.jimple.pa" +
                                               "ddle.bdddomains.H1, soot.jimple.paddle.bdddomains.fld:soot.j" +
                                               "imple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.ds" +
                                               "tc:soot.jimple.paddle.bdddomains.C3, soot.jimple.paddle.bddd" +
                                               "omains.dst:soot.jimple.paddle.bdddomains.V2> loadsFromHeap; " +
                                               "at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/padd" +
                                               "le/PropBDD.jedd:94,41-54"));
        loadsFromHeap.eq(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(load,
                                                                                                                                                  new PhysicalDomain[] { C2.v() },
                                                                                                                                                  new PhysicalDomain[] { C3.v() })),
                                                                                       loadPt,
                                                                                       new PhysicalDomain[] { C1.v(), V1.v() }),
                                                        new PhysicalDomain[] { C2.v() },
                                                        new PhysicalDomain[] { C1.v() }));
        return new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                                   new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H2.v() },
                                                   ("return jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v()" +
                                                    ".compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v()." +
                                                    "replace(loadsFromHeap, new jedd.PhysicalDomain[...], new jed" +
                                                    "d.PhysicalDomain[...])), fpt, new jedd.PhysicalDomain[...])," +
                                                    " new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...])" +
                                                    "; at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/pa" +
                                                    "ddle/PropBDD.jedd:98,8-14"),
                                                   jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(loadsFromHeap,
                                                                                                                                                                            new PhysicalDomain[] { V2.v() },
                                                                                                                                                                            new PhysicalDomain[] { V1.v() })),
                                                                                                                 fpt,
                                                                                                                 new PhysicalDomain[] { C1.v(), H1.v(), FD.v() }),
                                                                                  new PhysicalDomain[] { C3.v() },
                                                                                  new PhysicalDomain[] { C1.v() }));
    }
    
    public boolean update() {
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
                                                   "D.jedd:106,35-44"),
                                                  pt);
            pt.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(newAlloc.get(),
                                                                                                                                                  new PhysicalDomain[] { H1.v() },
                                                                                                                                                  new PhysicalDomain[] { H2.v() })),
                                                                                       typeFilter()),
                                                      new PhysicalDomain[] { H2.v() },
                                                      new PhysicalDomain[] { H1.v() }));
            pt.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(propSimple(new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v(), obj.v(), var.v() },
                                                                                                                                                                  new PhysicalDomain[] { C3.v(), C2.v(), H1.v(), V2.v() },
                                                                                                                                                                  ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                   "calDomain[...], new jedd.PhysicalDomain[...]), pag.allSimple" +
                                                                                                                                                                   "().get()) at /home/research/ccl/olhota/soot-trunk/src/soot/j" +
                                                                                                                                                                   "imple/paddle/PropBDD.jedd:109,18-28"),
                                                                                                                                                                  jedd.internal.Jedd.v().replace(pt,
                                                                                                                                                                                                 new PhysicalDomain[] { C1.v(), V1.v() },
                                                                                                                                                                                                 new PhysicalDomain[] { C3.v(), V2.v() })),
                                                                                                                              new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                                                                                                                                  new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                                                                                                                                                  ("propSimple(jedd.internal.Jedd.v().replace(pt, new jedd.Physi" +
                                                                                                                                                                   "calDomain[...], new jedd.PhysicalDomain[...]), pag.allSimple" +
                                                                                                                                                                   "().get()) at /home/research/ccl/olhota/soot-trunk/src/soot/j" +
                                                                                                                                                                   "imple/paddle/PropBDD.jedd:109,18-28"),
                                                                                                                                                                  pag.allSimple().get()))),
                                                                                       jedd.internal.Jedd.v().replace(typeFilter(),
                                                                                                                      new PhysicalDomain[] { C1.v(), H2.v() },
                                                                                                                      new PhysicalDomain[] { C3.v(), H1.v() })),
                                                      new PhysicalDomain[] { C3.v() },
                                                      new PhysicalDomain[] { C1.v() }));
            outputPt(new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v(), obj.v(), var.v() },
                                                         new PhysicalDomain[] { C1.v(), C2.v(), H1.v(), V1.v() },
                                                         ("outputPt(pt) at /home/research/ccl/olhota/soot-trunk/src/soo" +
                                                          "t/jimple/paddle/PropBDD.jedd:110,12-20"),
                                                         pt));
            fieldPt.eqUnion(propStore(new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v(), obj.v(), var.v() },
                                                                          new PhysicalDomain[] { C1.v(), C2.v(), H2.v(), V1.v() },
                                                                          ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                           "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                           ".get(), jedd.internal.Jedd.v().replace(pt, new jedd.Physical" +
                                                                           "Domain[...], new jedd.PhysicalDomain[...])) at /home/researc" +
                                                                           "h/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:" +
                                                                           "112,23-32"),
                                                                          jedd.internal.Jedd.v().replace(pt,
                                                                                                         new PhysicalDomain[] { H1.v() },
                                                                                                         new PhysicalDomain[] { H2.v() })),
                                      new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v(), fld.v() },
                                                                          new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v(), FD.v() },
                                                                          ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                           "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                           ".get(), jedd.internal.Jedd.v().replace(pt, new jedd.Physical" +
                                                                           "Domain[...], new jedd.PhysicalDomain[...])) at /home/researc" +
                                                                           "h/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:" +
                                                                           "112,23-32"),
                                                                          pag.allStore().get()),
                                      new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v(), obj.v(), var.v() },
                                                                          new PhysicalDomain[] { C2.v(), C1.v(), H1.v(), V2.v() },
                                                                          ("propStore(jedd.internal.Jedd.v().replace(pt, new jedd.Physic" +
                                                                           "alDomain[...], new jedd.PhysicalDomain[...]), pag.allStore()" +
                                                                           ".get(), jedd.internal.Jedd.v().replace(pt, new jedd.Physical" +
                                                                           "Domain[...], new jedd.PhysicalDomain[...])) at /home/researc" +
                                                                           "h/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:" +
                                                                           "112,23-32"),
                                                                          jedd.internal.Jedd.v().replace(pt,
                                                                                                         new PhysicalDomain[] { C1.v(), C2.v(), V1.v() },
                                                                                                         new PhysicalDomain[] { C2.v(), C1.v(), V2.v() }))));
            pt.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(propLoad(new jedd.internal.RelationContainer(new Attribute[] { basec.v(), objc.v(), obj.v(), base.v(), fld.v() },
                                                                                                                                                                new PhysicalDomain[] { C1.v(), C2.v(), H2.v(), H1.v(), FD.v() },
                                                                                                                                                                ("propLoad(fieldPt, pag.allLoad().get(), pt) at /home/research" +
                                                                                                                                                                 "/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:1" +
                                                                                                                                                                 "13,18-26"),
                                                                                                                                                                fieldPt),
                                                                                                                            new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                                                                                                                                                new PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                                                                                                                                                ("propLoad(fieldPt, pag.allLoad().get(), pt) at /home/research" +
                                                                                                                                                                 "/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:1" +
                                                                                                                                                                 "13,18-26"),
                                                                                                                                                                pag.allLoad().get()),
                                                                                                                            new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v(), obj.v(), var.v() },
                                                                                                                                                                new PhysicalDomain[] { C1.v(), C2.v(), H1.v(), V1.v() },
                                                                                                                                                                ("propLoad(fieldPt, pag.allLoad().get(), pt) at /home/research" +
                                                                                                                                                                 "/ccl/olhota/soot-trunk/src/soot/jimple/paddle/PropBDD.jedd:1" +
                                                                                                                                                                 "13,18-26"),
                                                                                                                                                                pt))),
                                                                                       typeFilter()),
                                                      new PhysicalDomain[] { H2.v() },
                                                      new PhysicalDomain[] { H1.v() }));
            outputPt(new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v(), obj.v(), var.v() },
                                                         new PhysicalDomain[] { C1.v(), C2.v(), H1.v(), V1.v() },
                                                         ("outputPt(pt) at /home/research/ccl/olhota/soot-trunk/src/soo" +
                                                          "t/jimple/paddle/PropBDD.jedd:115,12-20"),
                                                         pt));
            if (PaddleScene.v().options().verbose()) { G.v().out.println("Major iteration: "); }
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(pt), veryOldPt)) break;
        }
        return true;
    }
}
