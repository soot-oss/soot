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
              csimple,
              cload,
              cstore,
              calloc);
    }
    
    public void update() {
        localMap.eqUnion(jedd.internal.Jedd.v().project(locals.get(), new jedd.PhysicalDomain[] { T2.v() }));
        globalSet.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(globals.get(),
                                                                                        new jedd.PhysicalDomain[] { T2.v() }),
                                                         new jedd.PhysicalDomain[] { V1.v() },
                                                         new jedd.PhysicalDomain[] { V2.v() }));
        localallocMap.eqUnion(jedd.internal.Jedd.v().project(localallocs.get(), new jedd.PhysicalDomain[] { T2.v() }));
        globalallocSet.eqUnion(jedd.internal.Jedd.v().project(globalallocs.get(),
                                                              new jedd.PhysicalDomain[] { T2.v() }));
        final jedd.internal.RelationContainer newSimple =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V2, soot.jimple.paddle.bdddomains.dst:soot.jimple.paddl" +
                                               "e.bdddomains.V1> newSimple = jedd.internal.Jedd.v().replace(" +
                                               "simple.get(), new jedd.PhysicalDomain[...], new jedd.Physica" +
                                               "lDomain[...]); at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                               "addle/BDDMethodPAGContextifier.jedd:63,19-28"),
                                              jedd.internal.Jedd.v().replace(simple.get(),
                                                                             new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                             new jedd.PhysicalDomain[] { V2.v(), V1.v() }));
        csimple.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), dstc.v(), srcc.v() },
                                                        new jedd.PhysicalDomain[] { V2.v(), V1.v(), C2.v(), C1.v() },
                                                        ("csimple.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v" +
                                                         "().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                                         "ead(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().jo" +
                                                         "in(jedd.internal.Jedd.v().read(newSimple), globalSet, new je" +
                                                         "dd.PhysicalDomain[...]), new jedd.PhysicalDomain[...], new j" +
                                                         "edd.PhysicalDomain[...])), globalSet, new jedd.PhysicalDomai" +
                                                         "n[...])), jedd.internal.Jedd.v().literal(new java.lang.Objec" +
                                                         "t[...], new jedd.Attribute[...], new jedd.PhysicalDomain[..." +
                                                         "]), new jedd.PhysicalDomain[...])) at /home/olhotak/soot-tru" +
                                                         "nk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:64,8" +
                                                         "-15"),
                                                        jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newSimple),
                                                                                                                                                                                                                                   globalSet,
                                                                                                                                                                                                                                   new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                                                                                                       new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                                                                                                       new jedd.PhysicalDomain[] { V2.v(), V1.v() })),
                                                                                                                                            globalSet,
                                                                                                                                            new jedd.PhysicalDomain[] { V2.v() })),
                                                                                    jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                   new jedd.Attribute[] { srcc.v(), dstc.v() },
                                                                                                                   new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                    new jedd.PhysicalDomain[] {  })));
        mpagSimple.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newSimple),
                                                       jedd.internal.Jedd.v().replace(localMap,
                                                                                      new jedd.PhysicalDomain[] { V1.v() },
                                                                                      new jedd.PhysicalDomain[] { V2.v() }),
                                                       new jedd.PhysicalDomain[] { V2.v() }));
        mpagSimple.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newSimple),
                                                       localMap,
                                                       new jedd.PhysicalDomain[] { V1.v() }));
        final jedd.internal.RelationContainer newStore =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.fld:soot.jimple.paddl" +
                                               "e.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:soot.jimp" +
                                               "le.paddle.bdddomains.V2> newStore = store.get(); at /home/ol" +
                                               "hotak/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContexti" +
                                               "fier.jedd:71,24-32"),
                                              store.get());
        cstore.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), fld.v(), dstc.v(), srcc.v() },
                                                       new jedd.PhysicalDomain[] { V2.v(), V1.v(), FD.v(), C2.v(), C1.v() },
                                                       ("cstore.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().re" +
                                                        "ad(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(n" +
                                                        "ewStore), jedd.internal.Jedd.v().replace(globalSet, new jedd" +
                                                        ".PhysicalDomain[...], new jedd.PhysicalDomain[...]), new jed" +
                                                        "d.PhysicalDomain[...])), globalSet, new jedd.PhysicalDomain[" +
                                                        "...])), jedd.internal.Jedd.v().literal(new java.lang.Object[" +
                                                        "...], new jedd.Attribute[...], new jedd.PhysicalDomain[...])" +
                                                        ", new jedd.PhysicalDomain[...])) at /home/olhotak/soot-trunk" +
                                                        "/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:72,8-1" +
                                                        "4"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newStore),
                                                                                                                                                                                                   jedd.internal.Jedd.v().replace(globalSet,
                                                                                                                                                                                                                                  new jedd.PhysicalDomain[] { V2.v() },
                                                                                                                                                                                                                                  new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                                                                                                                   new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                                                           globalSet,
                                                                                                                                           new jedd.PhysicalDomain[] { V2.v() })),
                                                                                   jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                  new jedd.Attribute[] { srcc.v(), dstc.v() },
                                                                                                                  new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                   new jedd.PhysicalDomain[] {  })));
        mpagStore.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newStore),
                                                                                     localMap,
                                                                                     new jedd.PhysicalDomain[] { V1.v() }),
                                                         new jedd.PhysicalDomain[] { V2.v(), V1.v() },
                                                         new jedd.PhysicalDomain[] { V1.v(), V2.v() }));
        mpagStore.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(newStore,
                                                                                                                 new jedd.PhysicalDomain[] { V2.v(), V1.v() },
                                                                                                                 new jedd.PhysicalDomain[] { V1.v(), V2.v() })),
                                                      localMap,
                                                      new jedd.PhysicalDomain[] { V1.v() }));
        final jedd.internal.RelationContainer newLoad =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), FD.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V2, soot.jimple.paddle.bdddomains.fld:soot.jimple.paddl" +
                                               "e.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:soot.jimp" +
                                               "le.paddle.bdddomains.V1> newLoad = jedd.internal.Jedd.v().re" +
                                               "place(load.get(), new jedd.PhysicalDomain[...], new jedd.Phy" +
                                               "sicalDomain[...]); at /home/olhotak/soot-trunk/src/soot/jimp" +
                                               "le/paddle/BDDMethodPAGContextifier.jedd:79,24-31"),
                                              jedd.internal.Jedd.v().replace(load.get(),
                                                                             new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                             new jedd.PhysicalDomain[] { V2.v(), V1.v() }));
        cload.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), fld.v(), dstc.v(), srcc.v() },
                                                      new jedd.PhysicalDomain[] { V2.v(), V1.v(), FD.v(), C2.v(), C1.v() },
                                                      ("cload.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v()" +
                                                       ".read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v()." +
                                                       "join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join" +
                                                       "(jedd.internal.Jedd.v().read(newLoad), globalSet, new jedd.P" +
                                                       "hysicalDomain[...])), jedd.internal.Jedd.v().replace(globalS" +
                                                       "et, new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[.." +
                                                       ".]), new jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[" +
                                                       "...], new jedd.PhysicalDomain[...])), jedd.internal.Jedd.v()" +
                                                       ".literal(new java.lang.Object[...], new jedd.Attribute[...]," +
                                                       " new jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[...]" +
                                                       ")) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDMet" +
                                                       "hodPAGContextifier.jedd:80,8-13"),
                                                      jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newLoad),
                                                                                                                                                                                                                                 globalSet,
                                                                                                                                                                                                                                 new jedd.PhysicalDomain[] { V2.v() })),
                                                                                                                                                                         jedd.internal.Jedd.v().replace(globalSet,
                                                                                                                                                                                                        new jedd.PhysicalDomain[] { V2.v() },
                                                                                                                                                                                                        new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                                                                                         new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                                                             new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                                             new jedd.PhysicalDomain[] { V2.v(), V1.v() })),
                                                                                  jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                 new jedd.Attribute[] { srcc.v(), dstc.v() },
                                                                                                                 new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                  new jedd.PhysicalDomain[] {  })));
        mpagLoad.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(newLoad,
                                                                                                                new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                new jedd.PhysicalDomain[] { V2.v(), V1.v() })),
                                                     localMap,
                                                     new jedd.PhysicalDomain[] { V1.v() }));
        mpagLoad.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newLoad),
                                                                                    localMap,
                                                                                    new jedd.PhysicalDomain[] { V1.v() }),
                                                        new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                        new jedd.PhysicalDomain[] { V2.v(), V1.v() }));
        final jedd.internal.RelationContainer newAlloc =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                               "e.bdddomains.H1> newAlloc = alloc.get(); at /home/olhotak/so" +
                                               "ot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jed" +
                                               "d:87,19-27"),
                                              alloc.get());
        calloc.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v(), objc.v(), varc.v() },
                                                       new jedd.PhysicalDomain[] { V1.v(), H1.v(), C2.v(), C1.v() },
                                                       ("calloc.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().re" +
                                                        "ad(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(n" +
                                                        "ewAlloc), jedd.internal.Jedd.v().replace(globalSet, new jedd" +
                                                        ".PhysicalDomain[...], new jedd.PhysicalDomain[...]), new jed" +
                                                        "d.PhysicalDomain[...])), globalallocSet, new jedd.PhysicalDo" +
                                                        "main[...])), jedd.internal.Jedd.v().literal(new java.lang.Ob" +
                                                        "ject[...], new jedd.Attribute[...], new jedd.PhysicalDomain[" +
                                                        "...]), new jedd.PhysicalDomain[...])) at /home/olhotak/soot-" +
                                                        "trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:8" +
                                                        "8,8-14"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newAlloc),
                                                                                                                                                                                                   jedd.internal.Jedd.v().replace(globalSet,
                                                                                                                                                                                                                                  new jedd.PhysicalDomain[] { V2.v() },
                                                                                                                                                                                                                                  new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                                                                                                                   new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                                                           globalallocSet,
                                                                                                                                           new jedd.PhysicalDomain[] { H1.v() })),
                                                                                   jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                  new jedd.Attribute[] { varc.v(), objc.v() },
                                                                                                                  new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                   new jedd.PhysicalDomain[] {  })));
        mpagAlloc.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newAlloc),
                                                      localMap,
                                                      new jedd.PhysicalDomain[] { V1.v() }));
        mpagAlloc.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newAlloc),
                                                      localallocMap,
                                                      new jedd.PhysicalDomain[] { H1.v() }));
        final jedd.internal.RelationContainer localSet =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v() },
                                              new jedd.PhysicalDomain[] { V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1> localSet = jedd.internal.Jedd.v().project(localMap," +
                                               " new jedd.PhysicalDomain[...]); at /home/olhotak/soot-trunk/" +
                                               "src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:95,14-2" +
                                               "2"),
                                              jedd.internal.Jedd.v().project(localMap,
                                                                             new jedd.PhysicalDomain[] { T1.v() }));
        final jedd.internal.RelationContainer localLocal =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V2, soot.jimple.paddle.bdddomains.dst:soot.jimple.paddl" +
                                               "e.bdddomains.V1> localLocal = jedd.internal.Jedd.v().join(je" +
                                               "dd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(loc" +
                                               "alSet, new jedd.PhysicalDomain[...], new jedd.PhysicalDomain" +
                                               "[...])), localSet, new jedd.PhysicalDomain[...]); at /home/o" +
                                               "lhotak/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContext" +
                                               "ifier.jedd:96,19-29"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(localSet,
                                                                                                                                     new jedd.PhysicalDomain[] { V1.v() },
                                                                                                                                     new jedd.PhysicalDomain[] { V2.v() })),
                                                                          localSet,
                                                                          new jedd.PhysicalDomain[] {  }));
        final jedd.internal.RelationContainer contexts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.T1> contexts = jedd.internal.Jedd.v().repla" +
                                               "ce(rcout.get(), new jedd.PhysicalDomain[...], new jedd.Physi" +
                                               "calDomain[...]); at /home/olhotak/soot-trunk/src/soot/jimple" +
                                               "/paddle/BDDMethodPAGContextifier.jedd:98,23-31"),
                                              jedd.internal.Jedd.v().replace(rcout.get(),
                                                                             new jedd.PhysicalDomain[] { V1.v() },
                                                                             new jedd.PhysicalDomain[] { C2.v() }));
        final jedd.internal.RelationContainer ctxtSimple =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), src.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V2.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.dst:soot.jim" +
                                               "ple.paddle.bdddomains.V1> ctxtSimple = jedd.internal.Jedd.v(" +
                                               ").compose(jedd.internal.Jedd.v().read(contexts), mpagSimple," +
                                               " new jedd.PhysicalDomain[...]); at /home/olhotak/soot-trunk/" +
                                               "src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:100,25-" +
                                               "35"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(contexts),
                                                                             mpagSimple,
                                                                             new jedd.PhysicalDomain[] { T1.v() }));
        csimple.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), srcc.v(), dstc.v() },
                                                        new jedd.PhysicalDomain[] { V2.v(), V1.v(), C1.v(), C2.v() },
                                                        ("csimple.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v" +
                                                         "().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                                         "ead(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().re" +
                                                         "place(ctxtSimple, new jedd.PhysicalDomain[...], new jedd.Phy" +
                                                         "sicalDomain[...]), new jedd.PhysicalDomain[...], new jedd.Ph" +
                                                         "ysicalDomain[...])), globalSet, new jedd.PhysicalDomain[...]" +
                                                         ")), jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                         ", new jedd.Attribute[...], new jedd.PhysicalDomain[...]), ne" +
                                                         "w jedd.PhysicalDomain[...])) at /home/olhotak/soot-trunk/src" +
                                                         "/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:101,8-15"),
                                                        jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().replace(ctxtSimple,
                                                                                                                                                                                                                                      new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                                                                                                                                      new jedd.PhysicalDomain[] { V2.v(), V1.v() }),
                                                                                                                                                                                                       new jedd.PhysicalDomain[] { C2.v() },
                                                                                                                                                                                                       new jedd.PhysicalDomain[] { C1.v() })),
                                                                                                                                            globalSet,
                                                                                                                                            new jedd.PhysicalDomain[] { V2.v() })),
                                                                                    jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                   new jedd.Attribute[] { dstc.v() },
                                                                                                                   new jedd.PhysicalDomain[] { C2.v() }),
                                                                                    new jedd.PhysicalDomain[] {  })));
        csimple.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), dstc.v(), srcc.v() },
                                                        new jedd.PhysicalDomain[] { V2.v(), V1.v(), C2.v(), C1.v() },
                                                        ("csimple.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v" +
                                                         "().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                                         "ead(jedd.internal.Jedd.v().replace(ctxtSimple, new jedd.Phys" +
                                                         "icalDomain[...], new jedd.PhysicalDomain[...])), jedd.intern" +
                                                         "al.Jedd.v().replace(globalSet, new jedd.PhysicalDomain[...]," +
                                                         " new jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[...]" +
                                                         ")), jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                         ", new jedd.Attribute[...], new jedd.PhysicalDomain[...]), ne" +
                                                         "w jedd.PhysicalDomain[...])) at /home/olhotak/soot-trunk/src" +
                                                         "/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:103,8-15"),
                                                        jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ctxtSimple,
                                                                                                                                                                                                       new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                                                                                                       new jedd.PhysicalDomain[] { V2.v(), V1.v() })),
                                                                                                                                            jedd.internal.Jedd.v().replace(globalSet,
                                                                                                                                                                           new jedd.PhysicalDomain[] { V2.v() },
                                                                                                                                                                           new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                                                            new jedd.PhysicalDomain[] { V1.v() })),
                                                                                    jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                   new jedd.Attribute[] { srcc.v() },
                                                                                                                   new jedd.PhysicalDomain[] { C1.v() }),
                                                                                    new jedd.PhysicalDomain[] {  })));
        csimple.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), dstc.v(), srcc.v() },
                                                        new jedd.PhysicalDomain[] { V2.v(), V1.v(), C2.v(), C1.v() },
                                                        ("csimple.add(jedd.internal.Jedd.v().replace(jedd.internal.Jed" +
                                                         "d.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v(" +
                                                         ").copy(ctxtSimple, new jedd.PhysicalDomain[...], new jedd.Ph" +
                                                         "ysicalDomain[...])), localLocal, new jedd.PhysicalDomain[..." +
                                                         "]), new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[.." +
                                                         ".])) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDM" +
                                                         "ethodPAGContextifier.jedd:105,8-15"),
                                                        jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().copy(ctxtSimple,
                                                                                                                                                                           new jedd.PhysicalDomain[] { C2.v() },
                                                                                                                                                                           new jedd.PhysicalDomain[] { C1.v() })),
                                                                                                                   localLocal,
                                                                                                                   new jedd.PhysicalDomain[] { V2.v(), V1.v() }),
                                                                                       new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                       new jedd.PhysicalDomain[] { V2.v(), V1.v() })));
        final jedd.internal.RelationContainer ctxtStore =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V2.v(), FD.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V2, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:" +
                                               "soot.jimple.paddle.bdddomains.V1> ctxtStore = jedd.internal." +
                                               "Jedd.v().compose(jedd.internal.Jedd.v().read(contexts), mpag" +
                                               "Store, new jedd.PhysicalDomain[...]); at /home/olhotak/soot-" +
                                               "trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:1" +
                                               "08,30-39"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(contexts),
                                                                             mpagStore,
                                                                             new jedd.PhysicalDomain[] { T1.v() }));
        cstore.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), srcc.v(), fld.v(), dstc.v() },
                                                       new jedd.PhysicalDomain[] { V2.v(), V1.v(), C1.v(), FD.v(), C2.v() },
                                                       ("cstore.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().re" +
                                                        "ad(jedd.internal.Jedd.v().replace(ctxtStore, new jedd.Physic" +
                                                        "alDomain[...], new jedd.PhysicalDomain[...])), globalSet, ne" +
                                                        "w jedd.PhysicalDomain[...])), jedd.internal.Jedd.v().literal" +
                                                        "(new java.lang.Object[...], new jedd.Attribute[...], new jed" +
                                                        "d.PhysicalDomain[...]), new jedd.PhysicalDomain[...])) at /h" +
                                                        "ome/olhotak/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGCo" +
                                                        "ntextifier.jedd:109,8-14"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ctxtStore,
                                                                                                                                                                                                      new jedd.PhysicalDomain[] { V1.v(), C2.v(), V2.v() },
                                                                                                                                                                                                      new jedd.PhysicalDomain[] { V2.v(), C1.v(), V1.v() })),
                                                                                                                                           globalSet,
                                                                                                                                           new jedd.PhysicalDomain[] { V2.v() })),
                                                                                   jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                  new jedd.Attribute[] { dstc.v() },
                                                                                                                  new jedd.PhysicalDomain[] { C2.v() }),
                                                                                   new jedd.PhysicalDomain[] {  })));
        cstore.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), dstc.v(), fld.v(), srcc.v() },
                                                       new jedd.PhysicalDomain[] { V2.v(), V1.v(), C2.v(), FD.v(), C1.v() },
                                                       ("cstore.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v()" +
                                                        ".join(jedd.internal.Jedd.v().read(ctxtStore), globalSet, new" +
                                                        " jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[...], ne" +
                                                        "w jedd.PhysicalDomain[...])), jedd.internal.Jedd.v().literal" +
                                                        "(new java.lang.Object[...], new jedd.Attribute[...], new jed" +
                                                        "d.PhysicalDomain[...]), new jedd.PhysicalDomain[...])) at /h" +
                                                        "ome/olhotak/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGCo" +
                                                        "ntextifier.jedd:111,8-14"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(ctxtStore),
                                                                                                                                                                          globalSet,
                                                                                                                                                                          new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                                              new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                                              new jedd.PhysicalDomain[] { V2.v(), V1.v() })),
                                                                                   jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                  new jedd.Attribute[] { srcc.v() },
                                                                                                                  new jedd.PhysicalDomain[] { C1.v() }),
                                                                                   new jedd.PhysicalDomain[] {  })));
        cstore.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), dstc.v(), fld.v(), srcc.v() },
                                                       new jedd.PhysicalDomain[] { V2.v(), V1.v(), C2.v(), FD.v(), C1.v() },
                                                       ("cstore.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().re" +
                                                        "place(ctxtStore, new jedd.PhysicalDomain[...], new jedd.Phys" +
                                                        "icalDomain[...]), new jedd.PhysicalDomain[...], new jedd.Phy" +
                                                        "sicalDomain[...])), jedd.internal.Jedd.v().replace(localLoca" +
                                                        "l, new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[..." +
                                                        "]), new jedd.PhysicalDomain[...])) at /home/olhotak/soot-tru" +
                                                        "nk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:113," +
                                                        "8-14"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().replace(ctxtStore,
                                                                                                                                                                          new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                                                                          new jedd.PhysicalDomain[] { V2.v(), V1.v() }),
                                                                                                                                           new jedd.PhysicalDomain[] { C2.v() },
                                                                                                                                           new jedd.PhysicalDomain[] { C1.v() })),
                                                                                   jedd.internal.Jedd.v().replace(localLocal,
                                                                                                                  new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                  new jedd.PhysicalDomain[] { V2.v(), V1.v() }),
                                                                                   new jedd.PhysicalDomain[] { V1.v(), V2.v() })));
        final jedd.internal.RelationContainer ctxtLoad =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:" +
                                               "soot.jimple.paddle.bdddomains.V2> ctxtLoad = jedd.internal.J" +
                                               "edd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Je" +
                                               "dd.v().replace(contexts, new jedd.PhysicalDomain[...], new j" +
                                               "edd.PhysicalDomain[...])), mpagLoad, new jedd.PhysicalDomain" +
                                               "[...]); at /home/olhotak/soot-trunk/src/soot/jimple/paddle/B" +
                                               "DDMethodPAGContextifier.jedd:116,30-38"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(contexts,
                                                                                                                                        new jedd.PhysicalDomain[] { C2.v() },
                                                                                                                                        new jedd.PhysicalDomain[] { C1.v() })),
                                                                             mpagLoad,
                                                                             new jedd.PhysicalDomain[] { T1.v() }));
        cload.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), srcc.v(), fld.v(), dstc.v() },
                                                      new jedd.PhysicalDomain[] { V2.v(), V1.v(), C1.v(), FD.v(), C2.v() },
                                                      ("cload.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v()" +
                                                       ".read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().rea" +
                                                       "d(ctxtLoad), globalSet, new jedd.PhysicalDomain[...])), jedd" +
                                                       ".internal.Jedd.v().literal(new java.lang.Object[...], new je" +
                                                       "dd.Attribute[...], new jedd.PhysicalDomain[...]), new jedd.P" +
                                                       "hysicalDomain[...])) at /home/olhotak/soot-trunk/src/soot/ji" +
                                                       "mple/paddle/BDDMethodPAGContextifier.jedd:117,8-13"),
                                                      jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(ctxtLoad),
                                                                                                                                          globalSet,
                                                                                                                                          new jedd.PhysicalDomain[] { V2.v() })),
                                                                                  jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                 new jedd.Attribute[] { dstc.v() },
                                                                                                                 new jedd.PhysicalDomain[] { C2.v() }),
                                                                                  new jedd.PhysicalDomain[] {  })));
        cload.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), dstc.v(), fld.v(), srcc.v() },
                                                      new jedd.PhysicalDomain[] { V2.v(), V1.v(), C2.v(), FD.v(), C1.v() },
                                                      ("cload.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v()" +
                                                       ".read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v()." +
                                                       "join(jedd.internal.Jedd.v().read(ctxtLoad), jedd.internal.Je" +
                                                       "dd.v().replace(globalSet, new jedd.PhysicalDomain[...], new " +
                                                       "jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[...]), ne" +
                                                       "w jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...])), " +
                                                       "jedd.internal.Jedd.v().literal(new java.lang.Object[...], ne" +
                                                       "w jedd.Attribute[...], new jedd.PhysicalDomain[...]), new je" +
                                                       "dd.PhysicalDomain[...])) at /home/olhotak/soot-trunk/src/soo" +
                                                       "t/jimple/paddle/BDDMethodPAGContextifier.jedd:119,8-13"),
                                                      jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(ctxtLoad),
                                                                                                                                                                         jedd.internal.Jedd.v().replace(globalSet,
                                                                                                                                                                                                        new jedd.PhysicalDomain[] { V2.v() },
                                                                                                                                                                                                        new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                                                                                         new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                                                             new jedd.PhysicalDomain[] { C1.v() },
                                                                                                                                             new jedd.PhysicalDomain[] { C2.v() })),
                                                                                  jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                 new jedd.Attribute[] { srcc.v() },
                                                                                                                 new jedd.PhysicalDomain[] { C1.v() }),
                                                                                  new jedd.PhysicalDomain[] {  })));
        cload.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), dstc.v(), fld.v(), srcc.v() },
                                                      new jedd.PhysicalDomain[] { V2.v(), V1.v(), C2.v(), FD.v(), C1.v() },
                                                      ("cload.add(jedd.internal.Jedd.v().replace(jedd.internal.Jedd." +
                                                       "v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v()." +
                                                       "replace(jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().r" +
                                                       "eplace(ctxtLoad, new jedd.PhysicalDomain[...], new jedd.Phys" +
                                                       "icalDomain[...]), new jedd.PhysicalDomain[...], new jedd.Phy" +
                                                       "sicalDomain[...]), new jedd.PhysicalDomain[...], new jedd.Ph" +
                                                       "ysicalDomain[...])), localLocal, new jedd.PhysicalDomain[..." +
                                                       "]), new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[.." +
                                                       ".])) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDM" +
                                                       "ethodPAGContextifier.jedd:121,8-13"),
                                                      jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().replace(ctxtLoad,
                                                                                                                                                                                                                                       new jedd.PhysicalDomain[] { C1.v() },
                                                                                                                                                                                                                                       new jedd.PhysicalDomain[] { C2.v() }),
                                                                                                                                                                                                        new jedd.PhysicalDomain[] { C2.v() },
                                                                                                                                                                                                        new jedd.PhysicalDomain[] { C1.v() }),
                                                                                                                                                                            new jedd.PhysicalDomain[] { V2.v(), V1.v() },
                                                                                                                                                                            new jedd.PhysicalDomain[] { V1.v(), V2.v() })),
                                                                                                                 localLocal,
                                                                                                                 new jedd.PhysicalDomain[] { V2.v(), V1.v() }),
                                                                                     new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                     new jedd.PhysicalDomain[] { V2.v(), V1.v() })));
        final jedd.internal.RelationContainer localLocalalloc =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                               "e.bdddomains.H1> localLocalalloc = jedd.internal.Jedd.v().jo" +
                                               "in(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().projec" +
                                               "t(localMap, new jedd.PhysicalDomain[...])), jedd.internal.Je" +
                                               "dd.v().project(localallocMap, new jedd.PhysicalDomain[...])," +
                                               " new jedd.PhysicalDomain[...]); at /home/olhotak/soot-trunk/" +
                                               "src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:124,19-" +
                                               "34"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(localMap,
                                                                                                                                     new jedd.PhysicalDomain[] { T1.v() })),
                                                                          jedd.internal.Jedd.v().project(localallocMap,
                                                                                                         new jedd.PhysicalDomain[] { T1.v() }),
                                                                          new jedd.PhysicalDomain[] {  }));
        final jedd.internal.RelationContainer ctxtAlloc =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V1.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.obj:soot.jim" +
                                               "ple.paddle.bdddomains.H1> ctxtAlloc = jedd.internal.Jedd.v()" +
                                               ".compose(jedd.internal.Jedd.v().read(contexts), mpagAlloc, n" +
                                               "ew jedd.PhysicalDomain[...]); at /home/olhotak/soot-trunk/sr" +
                                               "c/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:126,25-34"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(contexts),
                                                                             mpagAlloc,
                                                                             new jedd.PhysicalDomain[] { T1.v() }));
        calloc.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), objc.v(), obj.v(), varc.v() },
                                                       new jedd.PhysicalDomain[] { V1.v(), C2.v(), H1.v(), C1.v() },
                                                       ("calloc.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().re" +
                                                        "ad(ctxtAlloc), jedd.internal.Jedd.v().replace(globalSet, new" +
                                                        " jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]), ne" +
                                                        "w jedd.PhysicalDomain[...])), jedd.internal.Jedd.v().literal" +
                                                        "(new java.lang.Object[...], new jedd.Attribute[...], new jed" +
                                                        "d.PhysicalDomain[...]), new jedd.PhysicalDomain[...])) at /h" +
                                                        "ome/olhotak/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGCo" +
                                                        "ntextifier.jedd:127,8-14"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(ctxtAlloc),
                                                                                                                                           jedd.internal.Jedd.v().replace(globalSet,
                                                                                                                                                                          new jedd.PhysicalDomain[] { V2.v() },
                                                                                                                                                                          new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                                                           new jedd.PhysicalDomain[] { V1.v() })),
                                                                                   jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                  new jedd.Attribute[] { varc.v() },
                                                                                                                  new jedd.PhysicalDomain[] { C1.v() }),
                                                                                   new jedd.PhysicalDomain[] {  })));
        calloc.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v(), varc.v(), objc.v() },
                                                       new jedd.PhysicalDomain[] { V1.v(), H1.v(), C1.v(), C2.v() },
                                                       ("calloc.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().re" +
                                                        "ad(jedd.internal.Jedd.v().replace(ctxtAlloc, new jedd.Physic" +
                                                        "alDomain[...], new jedd.PhysicalDomain[...])), globalallocSe" +
                                                        "t, new jedd.PhysicalDomain[...])), jedd.internal.Jedd.v().li" +
                                                        "teral(new java.lang.Object[...], new jedd.Attribute[...], ne" +
                                                        "w jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[...])) " +
                                                        "at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDMethod" +
                                                        "PAGContextifier.jedd:129,8-14"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ctxtAlloc,
                                                                                                                                                                                                      new jedd.PhysicalDomain[] { C2.v() },
                                                                                                                                                                                                      new jedd.PhysicalDomain[] { C1.v() })),
                                                                                                                                           globalallocSet,
                                                                                                                                           new jedd.PhysicalDomain[] { H1.v() })),
                                                                                   jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                  new jedd.Attribute[] { objc.v() },
                                                                                                                  new jedd.PhysicalDomain[] { C2.v() }),
                                                                                   new jedd.PhysicalDomain[] {  })));
        calloc.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), objc.v(), obj.v(), varc.v() },
                                                       new jedd.PhysicalDomain[] { V1.v(), C2.v(), H1.v(), C1.v() },
                                                       ("calloc.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().copy(ctxtAlloc, new jedd.Physi" +
                                                        "calDomain[...], new jedd.PhysicalDomain[...])), localLocalal" +
                                                        "loc, new jedd.PhysicalDomain[...])) at /home/olhotak/soot-tr" +
                                                        "unk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:131" +
                                                        ",8-14"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().copy(ctxtAlloc,
                                                                                                                                           new jedd.PhysicalDomain[] { C2.v() },
                                                                                                                                           new jedd.PhysicalDomain[] { C1.v() })),
                                                                                   localLocalalloc,
                                                                                   new jedd.PhysicalDomain[] { V1.v(), H1.v() })));
    }
    
    private final jedd.internal.RelationContainer localMap =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), method.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.method> localMap = jedd.internal.Jedd.v().fals" +
                                           "eBDD() at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BD" +
                                           "DMethodPAGContextifier.jedd:135,12-25"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer globalSet =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v() },
                                          new jedd.PhysicalDomain[] { V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var> globalSet = jedd" +
                                           ".internal.Jedd.v().falseBDD() at /home/olhotak/soot-trunk/sr" +
                                           "c/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:136,12-17"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer localallocMap =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), method.v() },
                                          new jedd.PhysicalDomain[] { H1.v(), T1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj, soot.jimple.padd" +
                                           "le.bdddomains.method> localallocMap = jedd.internal.Jedd.v()" +
                                           ".falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                           "le/BDDMethodPAGContextifier.jedd:137,12-25"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer globalallocSet =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v() },
                                          new jedd.PhysicalDomain[] { H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj> globalallocSet =" +
                                           " jedd.internal.Jedd.v().falseBDD() at /home/olhotak/soot-tru" +
                                           "nk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:138," +
                                           "12-17"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mpagSimple =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), src.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), V2.v(), V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.src, soot.jimple.paddle.bdddomains.dst> mpa" +
                                           "gSimple = jedd.internal.Jedd.v().falseBDD() at /home/olhotak" +
                                           "/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier." +
                                           "jedd:140,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mpagStore =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), src.v(), fld.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), V2.v(), FD.v(), V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.src, soot.jimple.paddle.bdddomains.fld, soo" +
                                           "t.jimple.paddle.bdddomains.dst> mpagStore = jedd.internal.Je" +
                                           "dd.v().falseBDD() at /home/olhotak/soot-trunk/src/soot/jimpl" +
                                           "e/paddle/BDDMethodPAGContextifier.jedd:141,12-35"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mpagLoad =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), src.v(), fld.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), V1.v(), FD.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.src, soot.jimple.paddle.bdddomains.fld, soo" +
                                           "t.jimple.paddle.bdddomains.dst> mpagLoad = jedd.internal.Jed" +
                                           "d.v().falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple" +
                                           "/paddle/BDDMethodPAGContextifier.jedd:142,12-35"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mpagAlloc =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), var.v(), obj.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), V1.v(), H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.var, soot.jimple.paddle.bdddomains.obj> mpa" +
                                           "gAlloc = jedd.internal.Jedd.v().falseBDD() at /home/olhotak/" +
                                           "soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.j" +
                                           "edd:143,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
}
