package soot.jimple.paddle;

import soot.*;
import soot.util.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public class BDDMethodPAGContextifier extends AbsMethodPAGContextifier {
    public BDDMethodPAGContextifier(Rsrc_dst simple,
                                    Rsrc_fld_dst load,
                                    Rsrc_fld_dst store,
                                    Robj_var alloc,
                                    Rvar_method_type locals,
                                    Rvar_type globals,
                                    Robj_method_type localallocs,
                                    Robj_type globalallocs,
                                    Rctxt_method rcout,
                                    Rsrcm_stmt_kind_tgtm_src_dst parms,
                                    Rsrcm_stmt_kind_tgtm_src_dst rets,
                                    Rsrcc_srcm_stmt_kind_tgtc_tgtm calls,
                                    Qsrcc_src_dstc_dst csimple,
                                    Qsrcc_src_fld_dstc_dst cload,
                                    Qsrcc_src_fld_dstc_dst cstore,
                                    Qobjc_obj_varc_var calloc) {
        super(simple,
              load,
              store,
              alloc,
              locals,
              globals,
              localallocs,
              globalallocs,
              rcout,
              parms,
              rets,
              calls,
              csimple,
              cload,
              cstore,
              calloc);
    }
    
    public boolean update() {
        final jedd.internal.RelationContainer simpleOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V2.v(), C1.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.dstc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.dst" +
                                               ":soot.jimple.paddle.bdddomains.V1> simpleOut = jedd.internal" +
                                               ".Jedd.v().falseBDD(); at /home/research/ccl/olhota/soot-trun" +
                                               "k/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:61,31" +
                                               "-40"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer loadOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dstc" +
                                               ":soot.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddom" +
                                               "ains.dst:soot.jimple.paddle.bdddomains.V2> loadOut = jedd.in" +
                                               "ternal.Jedd.v().falseBDD(); at /home/research/ccl/olhota/soo" +
                                               "t-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd" +
                                               ":62,36-43"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer storeOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dstc" +
                                               ":soot.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddom" +
                                               "ains.dst:soot.jimple.paddle.bdddomains.V2> storeOut = jedd.i" +
                                               "nternal.Jedd.v().falseBDD(); at /home/research/ccl/olhota/so" +
                                               "ot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jed" +
                                               "d:63,36-44"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer allocOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { objc.v(), obj.v(), varc.v(), var.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), H1.v(), C1.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.objc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.obj:soot.jimple.padd" +
                                               "le.bdddomains.H1, soot.jimple.paddle.bdddomains.varc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.var" +
                                               ":soot.jimple.paddle.bdddomains.V1> allocOut = jedd.internal." +
                                               "Jedd.v().falseBDD(); at /home/research/ccl/olhota/soot-trunk" +
                                               "/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:64,31-" +
                                               "39"),
                                              jedd.internal.Jedd.v().falseBDD());
        localMap.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(locals.get(),
                                                                                       new jedd.PhysicalDomain[] { T1.v() }),
                                                        new jedd.PhysicalDomain[] { V1.v() },
                                                        new jedd.PhysicalDomain[] { V2.v() }));
        globalSet.eqUnion(jedd.internal.Jedd.v().project(globals.get(), new jedd.PhysicalDomain[] { T1.v() }));
        localallocMap.eqUnion(jedd.internal.Jedd.v().project(localallocs.get(), new jedd.PhysicalDomain[] { T1.v() }));
        globalallocSet.eqUnion(jedd.internal.Jedd.v().project(globalallocs.get(),
                                                              new jedd.PhysicalDomain[] { T1.v() }));
        final jedd.internal.RelationContainer newSimple =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.dst:soot.jimple.paddl" +
                                               "e.bdddomains.V2> newSimple = simple.get(); at /home/research" +
                                               "/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGCo" +
                                               "ntextifier.jedd:71,19-28"),
                                              simple.get());
        csimple.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), dst.v(), srcc.v(), dstc.v() },
                                                        new jedd.PhysicalDomain[] { V1.v(), V2.v(), C1.v(), C2.v() },
                                                        ("csimple.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v" +
                                                         "().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                                         "ead(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(" +
                                                         "newSimple), globalSet, new jedd.PhysicalDomain[...])), jedd." +
                                                         "internal.Jedd.v().replace(globalSet, new jedd.PhysicalDomain" +
                                                         "[...], new jedd.PhysicalDomain[...]), new jedd.PhysicalDomai" +
                                                         "n[...])), jedd.internal.Jedd.v().literal(new java.lang.Objec" +
                                                         "t[...], new jedd.Attribute[...], new jedd.PhysicalDomain[..." +
                                                         "]), new jedd.PhysicalDomain[...])) at /home/research/ccl/olh" +
                                                         "ota/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifi" +
                                                         "er.jedd:72,8-15"),
                                                        jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newSimple),
                                                                                                                                                                                                    globalSet,
                                                                                                                                                                                                    new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                                                            jedd.internal.Jedd.v().replace(globalSet,
                                                                                                                                                                           new jedd.PhysicalDomain[] { V1.v() },
                                                                                                                                                                           new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                                            new jedd.PhysicalDomain[] { V2.v() })),
                                                                                    jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                   new jedd.Attribute[] { srcc.v(), dstc.v() },
                                                                                                                   new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                    new jedd.PhysicalDomain[] {  })));
        mpagSimple.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(newSimple,
                                                                                                                  new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                  new jedd.PhysicalDomain[] { V2.v(), V1.v() })),
                                                       localMap,
                                                       new jedd.PhysicalDomain[] { V2.v() }));
        mpagSimple.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newSimple),
                                                                                      localMap,
                                                                                      new jedd.PhysicalDomain[] { V2.v() }),
                                                          new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                          new jedd.PhysicalDomain[] { V2.v(), V1.v() }));
        final jedd.internal.RelationContainer newStore =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.fld:soot.jimple.paddl" +
                                               "e.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:soot.jimp" +
                                               "le.paddle.bdddomains.V2> newStore = store.get(); at /home/re" +
                                               "search/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDMetho" +
                                               "dPAGContextifier.jedd:79,24-32"),
                                              store.get());
        cstore.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), dst.v(), fld.v(), srcc.v(), dstc.v() },
                                                       new jedd.PhysicalDomain[] { V1.v(), V2.v(), FD.v(), C1.v(), C2.v() },
                                                       ("cstore.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().re" +
                                                        "ad(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(n" +
                                                        "ewStore), globalSet, new jedd.PhysicalDomain[...])), jedd.in" +
                                                        "ternal.Jedd.v().replace(globalSet, new jedd.PhysicalDomain[." +
                                                        "..], new jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[" +
                                                        "...])), jedd.internal.Jedd.v().literal(new java.lang.Object[" +
                                                        "...], new jedd.Attribute[...], new jedd.PhysicalDomain[...])" +
                                                        ", new jedd.PhysicalDomain[...])) at /home/research/ccl/olhot" +
                                                        "a/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier" +
                                                        ".jedd:80,8-14"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newStore),
                                                                                                                                                                                                   globalSet,
                                                                                                                                                                                                   new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                                                           jedd.internal.Jedd.v().replace(globalSet,
                                                                                                                                                                          new jedd.PhysicalDomain[] { V1.v() },
                                                                                                                                                                          new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                                           new jedd.PhysicalDomain[] { V2.v() })),
                                                                                   jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                  new jedd.Attribute[] { srcc.v(), dstc.v() },
                                                                                                                  new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                   new jedd.PhysicalDomain[] {  })));
        mpagStore.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newStore),
                                                      jedd.internal.Jedd.v().replace(localMap,
                                                                                     new jedd.PhysicalDomain[] { V2.v() },
                                                                                     new jedd.PhysicalDomain[] { V1.v() }),
                                                      new jedd.PhysicalDomain[] { V1.v() }));
        mpagStore.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newStore),
                                                      localMap,
                                                      new jedd.PhysicalDomain[] { V2.v() }));
        final jedd.internal.RelationContainer newLoad =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.fld:soot.jimple.paddl" +
                                               "e.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:soot.jimp" +
                                               "le.paddle.bdddomains.V2> newLoad = load.get(); at /home/rese" +
                                               "arch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDMethodP" +
                                               "AGContextifier.jedd:87,24-31"),
                                              load.get());
        cload.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), dst.v(), fld.v(), srcc.v(), dstc.v() },
                                                      new jedd.PhysicalDomain[] { V1.v(), V2.v(), FD.v(), C1.v(), C2.v() },
                                                      ("cload.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v()" +
                                                       ".read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().rea" +
                                                       "d(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(ne" +
                                                       "wLoad), globalSet, new jedd.PhysicalDomain[...])), jedd.inte" +
                                                       "rnal.Jedd.v().replace(globalSet, new jedd.PhysicalDomain[..." +
                                                       "], new jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[.." +
                                                       ".])), jedd.internal.Jedd.v().literal(new java.lang.Object[.." +
                                                       ".], new jedd.Attribute[...], new jedd.PhysicalDomain[...]), " +
                                                       "new jedd.PhysicalDomain[...])) at /home/research/ccl/olhota/" +
                                                       "soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.j" +
                                                       "edd:88,8-13"),
                                                      jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newLoad),
                                                                                                                                                                                                  globalSet,
                                                                                                                                                                                                  new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                                                          jedd.internal.Jedd.v().replace(globalSet,
                                                                                                                                                                         new jedd.PhysicalDomain[] { V1.v() },
                                                                                                                                                                         new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                                          new jedd.PhysicalDomain[] { V2.v() })),
                                                                                  jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                 new jedd.Attribute[] { srcc.v(), dstc.v() },
                                                                                                                 new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                  new jedd.PhysicalDomain[] {  })));
        mpagLoad.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newLoad),
                                                     jedd.internal.Jedd.v().replace(localMap,
                                                                                    new jedd.PhysicalDomain[] { V2.v() },
                                                                                    new jedd.PhysicalDomain[] { V1.v() }),
                                                     new jedd.PhysicalDomain[] { V1.v() }));
        mpagLoad.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newLoad),
                                                     localMap,
                                                     new jedd.PhysicalDomain[] { V2.v() }));
        final jedd.internal.RelationContainer newAlloc =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V2, soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                               "e.bdddomains.H1> newAlloc = jedd.internal.Jedd.v().replace(a" +
                                               "lloc.get(), new jedd.PhysicalDomain[...], new jedd.PhysicalD" +
                                               "omain[...]); at /home/research/ccl/olhota/soot-trunk/src/soo" +
                                               "t/jimple/paddle/BDDMethodPAGContextifier.jedd:95,19-27"),
                                              jedd.internal.Jedd.v().replace(alloc.get(),
                                                                             new jedd.PhysicalDomain[] { V1.v() },
                                                                             new jedd.PhysicalDomain[] { V2.v() }));
        calloc.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v(), objc.v(), varc.v() },
                                                       new jedd.PhysicalDomain[] { V1.v(), H1.v(), C2.v(), C1.v() },
                                                       ("calloc.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().re" +
                                                        "ad(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(j" +
                                                        "edd.internal.Jedd.v().replace(newAlloc, new jedd.PhysicalDom" +
                                                        "ain[...], new jedd.PhysicalDomain[...])), globalSet, new jed" +
                                                        "d.PhysicalDomain[...])), globalallocSet, new jedd.PhysicalDo" +
                                                        "main[...])), jedd.internal.Jedd.v().literal(new java.lang.Ob" +
                                                        "ject[...], new jedd.Attribute[...], new jedd.PhysicalDomain[" +
                                                        "...]), new jedd.PhysicalDomain[...])) at /home/research/ccl/" +
                                                        "olhota/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContext" +
                                                        "ifier.jedd:96,8-14"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(newAlloc,
                                                                                                                                                                                                                                                              new jedd.PhysicalDomain[] { V2.v() },
                                                                                                                                                                                                                                                              new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                                                                                                                   globalSet,
                                                                                                                                                                                                   new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                                                           globalallocSet,
                                                                                                                                           new jedd.PhysicalDomain[] { H1.v() })),
                                                                                   jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                  new jedd.Attribute[] { varc.v(), objc.v() },
                                                                                                                  new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                   new jedd.PhysicalDomain[] {  })));
        mpagAlloc.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newAlloc),
                                                                                     localMap,
                                                                                     new jedd.PhysicalDomain[] { V2.v() }),
                                                         new jedd.PhysicalDomain[] { V2.v() },
                                                         new jedd.PhysicalDomain[] { V1.v() }));
        mpagAlloc.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newAlloc),
                                                                                     localallocMap,
                                                                                     new jedd.PhysicalDomain[] { H1.v() }),
                                                         new jedd.PhysicalDomain[] { V2.v() },
                                                         new jedd.PhysicalDomain[] { V1.v() }));
        final jedd.internal.RelationContainer localSet =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v() },
                                              new jedd.PhysicalDomain[] { V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V2> localSet = jedd.internal.Jedd.v().project(localMap," +
                                               " new jedd.PhysicalDomain[...]); at /home/research/ccl/olhota" +
                                               "/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier." +
                                               "jedd:103,14-22"),
                                              jedd.internal.Jedd.v().project(localMap,
                                                                             new jedd.PhysicalDomain[] { MS.v() }));
        final jedd.internal.RelationContainer contexts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), MS.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.MS> contexts = rcout.get(); at /home/resear" +
                                               "ch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDMethodPAG" +
                                               "Contextifier.jedd:105,23-31"),
                                              rcout.get());
        final jedd.internal.RelationContainer ctxtSimple =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), src.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V2.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.dst:soot.jim" +
                                               "ple.paddle.bdddomains.V1> ctxtSimple = jedd.internal.Jedd.v(" +
                                               ").replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd." +
                                               "v().read(contexts), mpagSimple, new jedd.PhysicalDomain[...]" +
                                               "), new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[..." +
                                               "]); at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/" +
                                               "paddle/BDDMethodPAGContextifier.jedd:107,25-35"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(contexts),
                                                                                                            mpagSimple,
                                                                                                            new jedd.PhysicalDomain[] { MS.v() }),
                                                                             new jedd.PhysicalDomain[] { C1.v() },
                                                                             new jedd.PhysicalDomain[] { C2.v() }));
        simpleOut.eqUnion(jedd.internal.Jedd.v().copy(ctxtSimple,
                                                      new jedd.PhysicalDomain[] { C2.v() },
                                                      new jedd.PhysicalDomain[] { C1.v() }));
        final jedd.internal.RelationContainer ctxtStore =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:" +
                                               "soot.jimple.paddle.bdddomains.V2> ctxtStore = jedd.internal." +
                                               "Jedd.v().compose(jedd.internal.Jedd.v().read(contexts), mpag" +
                                               "Store, new jedd.PhysicalDomain[...]); at /home/research/ccl/" +
                                               "olhota/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContext" +
                                               "ifier.jedd:110,30-39"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(contexts),
                                                                             mpagStore,
                                                                             new jedd.PhysicalDomain[] { MS.v() }));
        storeOut.eqUnion(jedd.internal.Jedd.v().copy(ctxtStore,
                                                     new jedd.PhysicalDomain[] { C1.v() },
                                                     new jedd.PhysicalDomain[] { C2.v() }));
        final jedd.internal.RelationContainer ctxtLoad =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:" +
                                               "soot.jimple.paddle.bdddomains.V2> ctxtLoad = jedd.internal.J" +
                                               "edd.v().compose(jedd.internal.Jedd.v().read(contexts), mpagL" +
                                               "oad, new jedd.PhysicalDomain[...]); at /home/research/ccl/ol" +
                                               "hota/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextif" +
                                               "ier.jedd:113,30-38"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(contexts),
                                                                             mpagLoad,
                                                                             new jedd.PhysicalDomain[] { MS.v() }));
        loadOut.eqUnion(jedd.internal.Jedd.v().copy(ctxtLoad,
                                                    new jedd.PhysicalDomain[] { C1.v() },
                                                    new jedd.PhysicalDomain[] { C2.v() }));
        final jedd.internal.RelationContainer ctxtAlloc =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.obj:soot.jim" +
                                               "ple.paddle.bdddomains.H1> ctxtAlloc = jedd.internal.Jedd.v()" +
                                               ".compose(jedd.internal.Jedd.v().read(contexts), mpagAlloc, n" +
                                               "ew jedd.PhysicalDomain[...]); at /home/research/ccl/olhota/s" +
                                               "oot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.je" +
                                               "dd:116,25-34"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(contexts),
                                                                             mpagAlloc,
                                                                             new jedd.PhysicalDomain[] { MS.v() }));
        allocOut.eqUnion(jedd.internal.Jedd.v().copy(ctxtAlloc,
                                                     new jedd.PhysicalDomain[] { C1.v() },
                                                     new jedd.PhysicalDomain[] { C2.v() }));
        allParms.eqUnion(jedd.internal.Jedd.v().replace(parms.get(),
                                                        new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                        new jedd.PhysicalDomain[] { V2.v(), V1.v() }));
        allRets.eqUnion(jedd.internal.Jedd.v().replace(rets.get(),
                                                       new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                       new jedd.PhysicalDomain[] { V2.v(), V1.v() }));
        final jedd.internal.RelationContainer newCalls =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.C2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT> newCa" +
                                               "lls = calls.get(); at /home/research/ccl/olhota/soot-trunk/s" +
                                               "rc/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:122,45-5" +
                                               "3"),
                                              calls.get());
        simpleOut.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(newCalls),
                                                                                        allParms,
                                                                                        new jedd.PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v() }),
                                                         new jedd.PhysicalDomain[] { C1.v(), C2.v() },
                                                         new jedd.PhysicalDomain[] { C2.v(), C1.v() }));
        simpleOut.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(newCalls),
                                                         allRets,
                                                         new jedd.PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v() }));
        final jedd.internal.RelationContainer globalDsts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V2.v(), C1.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.dstc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.dst" +
                                               ":soot.jimple.paddle.bdddomains.V1> globalDsts = jedd.interna" +
                                               "l.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut), glob" +
                                               "alSet, new jedd.PhysicalDomain[...]); at /home/research/ccl/" +
                                               "olhota/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContext" +
                                               "ifier.jedd:130,31-41"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut),
                                                                          globalSet,
                                                                          new jedd.PhysicalDomain[] { V1.v() }));
        simpleOut.eqMinus(globalDsts);
        simpleOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalDsts,
                                                                                                                 new jedd.PhysicalDomain[] { C1.v() })),
                                                      jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                     new jedd.Attribute[] { dstc.v() },
                                                                                     new jedd.PhysicalDomain[] { C1.v() }),
                                                      new jedd.PhysicalDomain[] {  }));
        final jedd.internal.RelationContainer globalSrcs =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V2.v(), C1.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.dstc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.dst" +
                                               ":soot.jimple.paddle.bdddomains.V1> globalSrcs = jedd.interna" +
                                               "l.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut), jedd" +
                                               ".internal.Jedd.v().replace(globalSet, new jedd.PhysicalDomai" +
                                               "n[...], new jedd.PhysicalDomain[...]), new jedd.PhysicalDoma" +
                                               "in[...]); at /home/research/ccl/olhota/soot-trunk/src/soot/j" +
                                               "imple/paddle/BDDMethodPAGContextifier.jedd:133,31-41"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut),
                                                                          jedd.internal.Jedd.v().replace(globalSet,
                                                                                                         new jedd.PhysicalDomain[] { V1.v() },
                                                                                                         new jedd.PhysicalDomain[] { V2.v() }),
                                                                          new jedd.PhysicalDomain[] { V2.v() }));
        simpleOut.eqMinus(globalSrcs);
        simpleOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalSrcs,
                                                                                                                 new jedd.PhysicalDomain[] { C2.v() })),
                                                      jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                     new jedd.Attribute[] { srcc.v() },
                                                                                     new jedd.PhysicalDomain[] { C2.v() }),
                                                      new jedd.PhysicalDomain[] {  }));
        csimple.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), srcc.v(), dstc.v(), dst.v() },
                                                        new jedd.PhysicalDomain[] { V1.v(), C1.v(), C2.v(), V2.v() },
                                                        ("csimple.add(jedd.internal.Jedd.v().replace(simpleOut, new je" +
                                                         "dd.PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /h" +
                                                         "ome/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BD" +
                                                         "DMethodPAGContextifier.jedd:136,8-15"),
                                                        jedd.internal.Jedd.v().replace(simpleOut,
                                                                                       new jedd.PhysicalDomain[] { V2.v(), C2.v(), C1.v(), V1.v() },
                                                                                       new jedd.PhysicalDomain[] { V1.v(), C1.v(), C2.v(), V2.v() })));
        final jedd.internal.RelationContainer globalStoreDsts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dstc" +
                                               ":soot.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddom" +
                                               "ains.dst:soot.jimple.paddle.bdddomains.V2> globalStoreDsts =" +
                                               " jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(sto" +
                                               "reOut), jedd.internal.Jedd.v().replace(globalSet, new jedd.P" +
                                               "hysicalDomain[...], new jedd.PhysicalDomain[...]), new jedd." +
                                               "PhysicalDomain[...]); at /home/research/ccl/olhota/soot-trun" +
                                               "k/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:138,3" +
                                               "6-51"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(storeOut),
                                                                          jedd.internal.Jedd.v().replace(globalSet,
                                                                                                         new jedd.PhysicalDomain[] { V1.v() },
                                                                                                         new jedd.PhysicalDomain[] { V2.v() }),
                                                                          new jedd.PhysicalDomain[] { V2.v() }));
        storeOut.eqMinus(globalStoreDsts);
        storeOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalStoreDsts,
                                                                                                                new jedd.PhysicalDomain[] { C2.v() })),
                                                     jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                    new jedd.Attribute[] { dstc.v() },
                                                                                    new jedd.PhysicalDomain[] { C2.v() }),
                                                     new jedd.PhysicalDomain[] {  }));
        final jedd.internal.RelationContainer globalStoreSrcs =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dstc" +
                                               ":soot.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddom" +
                                               "ains.dst:soot.jimple.paddle.bdddomains.V2> globalStoreSrcs =" +
                                               " jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(sto" +
                                               "reOut), globalSet, new jedd.PhysicalDomain[...]); at /home/r" +
                                               "esearch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDMeth" +
                                               "odPAGContextifier.jedd:141,36-51"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(storeOut),
                                                                          globalSet,
                                                                          new jedd.PhysicalDomain[] { V1.v() }));
        storeOut.eqMinus(globalStoreSrcs);
        storeOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalStoreSrcs,
                                                                                                                new jedd.PhysicalDomain[] { C1.v() })),
                                                     jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                    new jedd.Attribute[] { srcc.v() },
                                                                                    new jedd.PhysicalDomain[] { C1.v() }),
                                                     new jedd.PhysicalDomain[] {  }));
        cstore.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), srcc.v(), dstc.v(), dst.v(), fld.v() },
                                                       new jedd.PhysicalDomain[] { V1.v(), C1.v(), C2.v(), V2.v(), FD.v() },
                                                       ("cstore.add(storeOut) at /home/research/ccl/olhota/soot-trunk" +
                                                        "/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:144,8-" +
                                                        "14"),
                                                       storeOut));
        final jedd.internal.RelationContainer globalLoadDsts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dstc" +
                                               ":soot.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddom" +
                                               "ains.dst:soot.jimple.paddle.bdddomains.V2> globalLoadDsts = " +
                                               "jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(load" +
                                               "Out), jedd.internal.Jedd.v().replace(globalSet, new jedd.Phy" +
                                               "sicalDomain[...], new jedd.PhysicalDomain[...]), new jedd.Ph" +
                                               "ysicalDomain[...]); at /home/research/ccl/olhota/soot-trunk/" +
                                               "src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:146,36-" +
                                               "50"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(loadOut),
                                                                          jedd.internal.Jedd.v().replace(globalSet,
                                                                                                         new jedd.PhysicalDomain[] { V1.v() },
                                                                                                         new jedd.PhysicalDomain[] { V2.v() }),
                                                                          new jedd.PhysicalDomain[] { V2.v() }));
        loadOut.eqMinus(globalLoadDsts);
        loadOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalLoadDsts,
                                                                                                               new jedd.PhysicalDomain[] { C2.v() })),
                                                    jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                   new jedd.Attribute[] { dstc.v() },
                                                                                   new jedd.PhysicalDomain[] { C2.v() }),
                                                    new jedd.PhysicalDomain[] {  }));
        final jedd.internal.RelationContainer globalLoadSrcs =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dstc" +
                                               ":soot.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddom" +
                                               "ains.dst:soot.jimple.paddle.bdddomains.V2> globalLoadSrcs = " +
                                               "jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(load" +
                                               "Out), globalSet, new jedd.PhysicalDomain[...]); at /home/res" +
                                               "earch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDMethod" +
                                               "PAGContextifier.jedd:149,36-50"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(loadOut),
                                                                          globalSet,
                                                                          new jedd.PhysicalDomain[] { V1.v() }));
        loadOut.eqMinus(globalLoadSrcs);
        loadOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalLoadSrcs,
                                                                                                               new jedd.PhysicalDomain[] { C1.v() })),
                                                    jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                   new jedd.Attribute[] { srcc.v() },
                                                                                   new jedd.PhysicalDomain[] { C1.v() }),
                                                    new jedd.PhysicalDomain[] {  }));
        cload.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), srcc.v(), dstc.v(), dst.v(), fld.v() },
                                                      new jedd.PhysicalDomain[] { V1.v(), C1.v(), C2.v(), V2.v(), FD.v() },
                                                      ("cload.add(loadOut) at /home/research/ccl/olhota/soot-trunk/s" +
                                                       "rc/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:152,8-13"),
                                                      loadOut));
        final jedd.internal.RelationContainer globalAllocDsts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { objc.v(), obj.v(), varc.v(), var.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), H1.v(), C1.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.objc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.obj:soot.jimple.padd" +
                                               "le.bdddomains.H1, soot.jimple.paddle.bdddomains.varc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.var" +
                                               ":soot.jimple.paddle.bdddomains.V1> globalAllocDsts = jedd.in" +
                                               "ternal.Jedd.v().join(jedd.internal.Jedd.v().read(allocOut), " +
                                               "globalSet, new jedd.PhysicalDomain[...]); at /home/research/" +
                                               "ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGCon" +
                                               "textifier.jedd:154,31-46"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(allocOut),
                                                                          globalSet,
                                                                          new jedd.PhysicalDomain[] { V1.v() }));
        allocOut.eqMinus(globalAllocDsts);
        allocOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalAllocDsts,
                                                                                                                new jedd.PhysicalDomain[] { C1.v() })),
                                                     jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                    new jedd.Attribute[] { varc.v() },
                                                                                    new jedd.PhysicalDomain[] { C1.v() }),
                                                     new jedd.PhysicalDomain[] {  }));
        final jedd.internal.RelationContainer globalAllocSrcs =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { objc.v(), obj.v(), varc.v(), var.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), H1.v(), C1.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.objc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.obj:soot.jimple.padd" +
                                               "le.bdddomains.H1, soot.jimple.paddle.bdddomains.varc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.var" +
                                               ":soot.jimple.paddle.bdddomains.V1> globalAllocSrcs = jedd.in" +
                                               "ternal.Jedd.v().join(jedd.internal.Jedd.v().read(allocOut), " +
                                               "globalallocSet, new jedd.PhysicalDomain[...]); at /home/rese" +
                                               "arch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDMethodP" +
                                               "AGContextifier.jedd:157,31-46"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(allocOut),
                                                                          globalallocSet,
                                                                          new jedd.PhysicalDomain[] { H1.v() }));
        allocOut.eqMinus(globalAllocSrcs);
        allocOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalAllocSrcs,
                                                                                                                new jedd.PhysicalDomain[] { C2.v() })),
                                                     jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                    new jedd.Attribute[] { objc.v() },
                                                                                    new jedd.PhysicalDomain[] { C2.v() }),
                                                     new jedd.PhysicalDomain[] {  }));
        calloc.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), objc.v(), varc.v(), obj.v() },
                                                       new jedd.PhysicalDomain[] { V1.v(), C2.v(), C1.v(), H1.v() },
                                                       ("calloc.add(allocOut) at /home/research/ccl/olhota/soot-trunk" +
                                                        "/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:160,8-" +
                                                        "14"),
                                                       allocOut));
        return false;
    }
    
    private final jedd.internal.RelationContainer localMap =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), method.v() },
                                          new jedd.PhysicalDomain[] { V2.v(), MS.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.method> localMap = jedd.internal.Jedd.v().fals" +
                                           "eBDD() at /home/research/ccl/olhota/soot-trunk/src/soot/jimp" +
                                           "le/paddle/BDDMethodPAGContextifier.jedd:164,12-25"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer globalSet =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v() },
                                          new jedd.PhysicalDomain[] { V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var> globalSet = jedd" +
                                           ".internal.Jedd.v().falseBDD() at /home/research/ccl/olhota/s" +
                                           "oot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.je" +
                                           "dd:165,12-17"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer localallocMap =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), method.v() },
                                          new jedd.PhysicalDomain[] { H1.v(), MS.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj, soot.jimple.padd" +
                                           "le.bdddomains.method> localallocMap = jedd.internal.Jedd.v()" +
                                           ".falseBDD() at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                           "/jimple/paddle/BDDMethodPAGContextifier.jedd:166,12-25"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer globalallocSet =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v() },
                                          new jedd.PhysicalDomain[] { H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj> globalallocSet =" +
                                           " jedd.internal.Jedd.v().falseBDD() at /home/research/ccl/olh" +
                                           "ota/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifi" +
                                           "er.jedd:167,12-17"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mpagSimple =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), src.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), V2.v(), V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.src, soot.jimple.paddle.bdddomains.dst> mpa" +
                                           "gSimple = jedd.internal.Jedd.v().falseBDD() at /home/researc" +
                                           "h/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGC" +
                                           "ontextifier.jedd:169,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mpagStore =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), src.v(), fld.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), V1.v(), FD.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.src, soot.jimple.paddle.bdddomains.fld, soo" +
                                           "t.jimple.paddle.bdddomains.dst> mpagStore = jedd.internal.Je" +
                                           "dd.v().falseBDD() at /home/research/ccl/olhota/soot-trunk/sr" +
                                           "c/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:170,12-35"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mpagLoad =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), src.v(), fld.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), V1.v(), FD.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.src, soot.jimple.paddle.bdddomains.fld, soo" +
                                           "t.jimple.paddle.bdddomains.dst> mpagLoad = jedd.internal.Jed" +
                                           "d.v().falseBDD() at /home/research/ccl/olhota/soot-trunk/src" +
                                           "/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:171,12-35"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mpagAlloc =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), var.v(), obj.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), V1.v(), H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.var, soot.jimple.paddle.bdddomains.obj> mpa" +
                                           "gAlloc = jedd.internal.Jedd.v().falseBDD() at /home/research" +
                                           "/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGCo" +
                                           "ntextifier.jedd:172,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allParms =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v(), V2.v(), V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcm, soot.jimple.pad" +
                                           "dle.bdddomains.stmt, soot.jimple.paddle.bdddomains.kind, soo" +
                                           "t.jimple.paddle.bdddomains.tgtm, soot.jimple.paddle.bdddomai" +
                                           "ns.src, soot.jimple.paddle.bdddomains.dst> allParms = jedd.i" +
                                           "nternal.Jedd.v().falseBDD() at /home/research/ccl/olhota/soo" +
                                           "t-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd" +
                                           ":174,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allRets =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v(), V2.v(), V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcm, soot.jimple.pad" +
                                           "dle.bdddomains.stmt, soot.jimple.paddle.bdddomains.kind, soo" +
                                           "t.jimple.paddle.bdddomains.tgtm, soot.jimple.paddle.bdddomai" +
                                           "ns.src, soot.jimple.paddle.bdddomains.dst> allRets = jedd.in" +
                                           "ternal.Jedd.v().falseBDD() at /home/research/ccl/olhota/soot" +
                                           "-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:" +
                                           "175,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
}
