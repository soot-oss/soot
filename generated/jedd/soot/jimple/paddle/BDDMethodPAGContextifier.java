package soot.jimple.paddle;

import soot.*;
import soot.util.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public class BDDMethodPAGContextifier extends AbsMethodPAGContextifier {
    private BDDNodeInfo ni;
    
    public BDDMethodPAGContextifier(BDDNodeInfo ni,
                                    Rsrc_dst simple,
                                    Rsrc_fld_dst load,
                                    Rsrc_dst_fld store,
                                    Robj_var alloc,
                                    Rctxt_method rcout,
                                    Qsrcc_src_dstc_dst csimple,
                                    Qsrcc_src_fld_dstc_dst cload,
                                    Qsrcc_src_dstc_dst_fld cstore,
                                    Qobjc_obj_varc_var calloc) {
        super(simple, load, store, alloc, rcout, csimple, cload, cstore, calloc);
        this.ni = ni;
    }
    
    public boolean update() {
        final jedd.internal.RelationContainer simpleOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.dstc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.dst" +
                                               ":soot.jimple.paddle.bdddomains.V2> simpleOut = jedd.internal" +
                                               ".Jedd.v().falseBDD(); at /tmp/olhotak/soot-trunk/src/soot/ji" +
                                               "mple/paddle/BDDMethodPAGContextifier.jedd:55,31-40"),
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
                                               "ternal.Jedd.v().falseBDD(); at /tmp/olhotak/soot-trunk/src/s" +
                                               "oot/jimple/paddle/BDDMethodPAGContextifier.jedd:56,36-43"),
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
                                               "nternal.Jedd.v().falseBDD(); at /tmp/olhotak/soot-trunk/src/" +
                                               "soot/jimple/paddle/BDDMethodPAGContextifier.jedd:57,36-44"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer allocOut =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { objc.v(), obj.v(), varc.v(), var.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), H1.v(), C1.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.objc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.obj:soot.jimple.padd" +
                                               "le.bdddomains.H1, soot.jimple.paddle.bdddomains.varc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.var" +
                                               ":soot.jimple.paddle.bdddomains.V1> allocOut = jedd.internal." +
                                               "Jedd.v().falseBDD(); at /tmp/olhotak/soot-trunk/src/soot/jim" +
                                               "ple/paddle/BDDMethodPAGContextifier.jedd:58,31-39"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer newSimple =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.dst:soot.jimple.paddl" +
                                               "e.bdddomains.V2> newSimple = simple.get(); at /tmp/olhotak/s" +
                                               "oot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.je" +
                                               "dd:60,19-28"),
                                              simple.get());
        csimple.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dst.v(), src.v(), dstc.v(), srcc.v() },
                                                        new jedd.PhysicalDomain[] { V2.v(), V1.v(), C2.v(), C1.v() },
                                                        ("csimple.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v" +
                                                         "().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                                         "ead(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(" +
                                                         "newSimple), jedd.internal.Jedd.v().replace(ni.globalSet(), n" +
                                                         "ew jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]), " +
                                                         "new jedd.PhysicalDomain[...])), ni.globalSet(), new jedd.Phy" +
                                                         "sicalDomain[...])), jedd.internal.Jedd.v().literal(new java." +
                                                         "lang.Object[...], new jedd.Attribute[...], new jedd.Physical" +
                                                         "Domain[...]), new jedd.PhysicalDomain[...])) at /tmp/olhotak" +
                                                         "/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier." +
                                                         "jedd:61,8-15"),
                                                        jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newSimple),
                                                                                                                                                                                                    jedd.internal.Jedd.v().replace(ni.globalSet(),
                                                                                                                                                                                                                                   new jedd.PhysicalDomain[] { V2.v() },
                                                                                                                                                                                                                                   new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                                                                                                                    new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                                                            ni.globalSet(),
                                                                                                                                            new jedd.PhysicalDomain[] { V2.v() })),
                                                                                    jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                   new jedd.Attribute[] { srcc.v(), dstc.v() },
                                                                                                                   new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                    new jedd.PhysicalDomain[] {  })));
        mpagSimple.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newSimple),
                                                       ni.localMap(),
                                                       new jedd.PhysicalDomain[] { V1.v() }));
        mpagSimple.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newSimple),
                                                       jedd.internal.Jedd.v().replace(ni.localMap(),
                                                                                      new jedd.PhysicalDomain[] { V1.v() },
                                                                                      new jedd.PhysicalDomain[] { V2.v() }),
                                                       new jedd.PhysicalDomain[] { V2.v() }));
        final jedd.internal.RelationContainer newStore =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.fld:soot.jimple.paddl" +
                                               "e.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:soot.jimp" +
                                               "le.paddle.bdddomains.V2> newStore = store.get(); at /tmp/olh" +
                                               "otak/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextif" +
                                               "ier.jedd:68,24-32"),
                                              store.get());
        cstore.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { fld.v(), dst.v(), src.v(), dstc.v(), srcc.v() },
                                                       new jedd.PhysicalDomain[] { FD.v(), V2.v(), V1.v(), C2.v(), C1.v() },
                                                       ("cstore.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().re" +
                                                        "ad(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(n" +
                                                        "ewStore), jedd.internal.Jedd.v().replace(ni.globalSet(), new" +
                                                        " jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]), ne" +
                                                        "w jedd.PhysicalDomain[...])), ni.globalSet(), new jedd.Physi" +
                                                        "calDomain[...])), jedd.internal.Jedd.v().literal(new java.la" +
                                                        "ng.Object[...], new jedd.Attribute[...], new jedd.PhysicalDo" +
                                                        "main[...]), new jedd.PhysicalDomain[...])) at /tmp/olhotak/s" +
                                                        "oot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.je" +
                                                        "dd:69,8-14"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newStore),
                                                                                                                                                                                                   jedd.internal.Jedd.v().replace(ni.globalSet(),
                                                                                                                                                                                                                                  new jedd.PhysicalDomain[] { V2.v() },
                                                                                                                                                                                                                                  new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                                                                                                                   new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                                                           ni.globalSet(),
                                                                                                                                           new jedd.PhysicalDomain[] { V2.v() })),
                                                                                   jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                  new jedd.Attribute[] { srcc.v(), dstc.v() },
                                                                                                                  new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                   new jedd.PhysicalDomain[] {  })));
        mpagStore.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newStore),
                                                      ni.localMap(),
                                                      new jedd.PhysicalDomain[] { V1.v() }));
        mpagStore.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newStore),
                                                      jedd.internal.Jedd.v().replace(ni.localMap(),
                                                                                     new jedd.PhysicalDomain[] { V1.v() },
                                                                                     new jedd.PhysicalDomain[] { V2.v() }),
                                                      new jedd.PhysicalDomain[] { V2.v() }));
        final jedd.internal.RelationContainer newLoad =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), FD.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V2, soot.jimple.paddle.bdddomains.fld:soot.jimple.paddl" +
                                               "e.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:soot.jimp" +
                                               "le.paddle.bdddomains.V1> newLoad = jedd.internal.Jedd.v().re" +
                                               "place(load.get(), new jedd.PhysicalDomain[...], new jedd.Phy" +
                                               "sicalDomain[...]); at /tmp/olhotak/soot-trunk/src/soot/jimpl" +
                                               "e/paddle/BDDMethodPAGContextifier.jedd:76,24-31"),
                                              jedd.internal.Jedd.v().replace(load.get(),
                                                                             new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                             new jedd.PhysicalDomain[] { V2.v(), V1.v() }));
        cload.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { fld.v(), dst.v(), src.v(), dstc.v(), srcc.v() },
                                                      new jedd.PhysicalDomain[] { FD.v(), V2.v(), V1.v(), C2.v(), C1.v() },
                                                      ("cload.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v()" +
                                                       ".read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().rea" +
                                                       "d(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join" +
                                                       "(jedd.internal.Jedd.v().read(newLoad), ni.globalSet(), new j" +
                                                       "edd.PhysicalDomain[...]), new jedd.PhysicalDomain[...], new " +
                                                       "jedd.PhysicalDomain[...])), ni.globalSet(), new jedd.Physica" +
                                                       "lDomain[...])), jedd.internal.Jedd.v().literal(new java.lang" +
                                                       ".Object[...], new jedd.Attribute[...], new jedd.PhysicalDoma" +
                                                       "in[...]), new jedd.PhysicalDomain[...])) at /tmp/olhotak/soo" +
                                                       "t-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd" +
                                                       ":77,8-13"),
                                                      jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newLoad),
                                                                                                                                                                                                                                 ni.globalSet(),
                                                                                                                                                                                                                                 new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                                                                                                     new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                                                                                                     new jedd.PhysicalDomain[] { V2.v(), V1.v() })),
                                                                                                                                          ni.globalSet(),
                                                                                                                                          new jedd.PhysicalDomain[] { V2.v() })),
                                                                                  jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                 new jedd.Attribute[] { srcc.v(), dstc.v() },
                                                                                                                 new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                  new jedd.PhysicalDomain[] {  })));
        mpagLoad.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(newLoad,
                                                                                                                new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                new jedd.PhysicalDomain[] { V2.v(), V1.v() })),
                                                     ni.localMap(),
                                                     new jedd.PhysicalDomain[] { V1.v() }));
        mpagLoad.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newLoad),
                                                                                    ni.localMap(),
                                                                                    new jedd.PhysicalDomain[] { V1.v() }),
                                                        new jedd.PhysicalDomain[] { V1.v(), V2.v() },
                                                        new jedd.PhysicalDomain[] { V2.v(), V1.v() }));
        final jedd.internal.RelationContainer newAlloc =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V2, soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                               "e.bdddomains.H1> newAlloc = jedd.internal.Jedd.v().replace(a" +
                                               "lloc.get(), new jedd.PhysicalDomain[...], new jedd.PhysicalD" +
                                               "omain[...]); at /tmp/olhotak/soot-trunk/src/soot/jimple/padd" +
                                               "le/BDDMethodPAGContextifier.jedd:84,19-27"),
                                              jedd.internal.Jedd.v().replace(alloc.get(),
                                                                             new jedd.PhysicalDomain[] { V1.v() },
                                                                             new jedd.PhysicalDomain[] { V2.v() }));
        calloc.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v(), objc.v(), varc.v() },
                                                       new jedd.PhysicalDomain[] { V1.v(), H1.v(), C2.v(), C1.v() },
                                                       ("calloc.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v(" +
                                                        ").read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().re" +
                                                        "ad(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().joi" +
                                                        "n(jedd.internal.Jedd.v().read(newAlloc), ni.globalSet(), new" +
                                                        " jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[...], ne" +
                                                        "w jedd.PhysicalDomain[...])), ni.globalallocSet(), new jedd." +
                                                        "PhysicalDomain[...])), jedd.internal.Jedd.v().literal(new ja" +
                                                        "va.lang.Object[...], new jedd.Attribute[...], new jedd.Physi" +
                                                        "calDomain[...]), new jedd.PhysicalDomain[...])) at /tmp/olho" +
                                                        "tak/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifi" +
                                                        "er.jedd:85,8-14"),
                                                       jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newAlloc),
                                                                                                                                                                                                                                  ni.globalSet(),
                                                                                                                                                                                                                                  new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                                                                                                      new jedd.PhysicalDomain[] { V2.v() },
                                                                                                                                                                                                      new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                                                           ni.globalallocSet(),
                                                                                                                                           new jedd.PhysicalDomain[] { H1.v() })),
                                                                                   jedd.internal.Jedd.v().literal(new Object[] { null, null },
                                                                                                                  new jedd.Attribute[] { varc.v(), objc.v() },
                                                                                                                  new jedd.PhysicalDomain[] { C1.v(), C2.v() }),
                                                                                   new jedd.PhysicalDomain[] {  })));
        mpagAlloc.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newAlloc),
                                                                                     jedd.internal.Jedd.v().replace(ni.localMap(),
                                                                                                                    new jedd.PhysicalDomain[] { V1.v() },
                                                                                                                    new jedd.PhysicalDomain[] { V2.v() }),
                                                                                     new jedd.PhysicalDomain[] { V2.v() }),
                                                         new jedd.PhysicalDomain[] { V2.v() },
                                                         new jedd.PhysicalDomain[] { V1.v() }));
        mpagAlloc.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newAlloc),
                                                                                     ni.localallocMap(),
                                                                                     new jedd.PhysicalDomain[] { H1.v() }),
                                                         new jedd.PhysicalDomain[] { V2.v() },
                                                         new jedd.PhysicalDomain[] { V1.v() }));
        final jedd.internal.RelationContainer localSet =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v() },
                                              new jedd.PhysicalDomain[] { V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1> localSet = jedd.internal.Jedd.v().project(ni.localM" +
                                               "ap(), new jedd.PhysicalDomain[...]); at /tmp/olhotak/soot-tr" +
                                               "unk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:92," +
                                               "14-22"),
                                              jedd.internal.Jedd.v().project(ni.localMap(),
                                                                             new jedd.PhysicalDomain[] { MS.v() }));
        final jedd.internal.RelationContainer contexts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), MS.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.MS> contexts = jedd.internal.Jedd.v().repla" +
                                               "ce(rcout.get(), new jedd.PhysicalDomain[...], new jedd.Physi" +
                                               "calDomain[...]); at /tmp/olhotak/soot-trunk/src/soot/jimple/" +
                                               "paddle/BDDMethodPAGContextifier.jedd:94,23-31"),
                                              jedd.internal.Jedd.v().replace(rcout.get(),
                                                                             new jedd.PhysicalDomain[] { C1.v() },
                                                                             new jedd.PhysicalDomain[] { C2.v() }));
        final jedd.internal.RelationContainer ctxtSimple =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), src.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V1.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.dst:soot.jim" +
                                               "ple.paddle.bdddomains.V2> ctxtSimple = jedd.internal.Jedd.v(" +
                                               ").compose(jedd.internal.Jedd.v().read(contexts), mpagSimple," +
                                               " new jedd.PhysicalDomain[...]); at /tmp/olhotak/soot-trunk/s" +
                                               "rc/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:96,25-35"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(contexts),
                                                                             mpagSimple,
                                                                             new jedd.PhysicalDomain[] { MS.v() }));
        simpleOut.eqUnion(jedd.internal.Jedd.v().copy(jedd.internal.Jedd.v().replace(ctxtSimple,
                                                                                     new jedd.PhysicalDomain[] { C2.v() },
                                                                                     new jedd.PhysicalDomain[] { C1.v() }),
                                                      new jedd.PhysicalDomain[] { C1.v() },
                                                      new jedd.PhysicalDomain[] { C2.v() }));
        final jedd.internal.RelationContainer ctxtStore =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:" +
                                               "soot.jimple.paddle.bdddomains.V2> ctxtStore = jedd.internal." +
                                               "Jedd.v().compose(jedd.internal.Jedd.v().read(contexts), mpag" +
                                               "Store, new jedd.PhysicalDomain[...]); at /tmp/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:99" +
                                               ",30-39"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(contexts),
                                                                             mpagStore,
                                                                             new jedd.PhysicalDomain[] { MS.v() }));
        storeOut.eqUnion(jedd.internal.Jedd.v().copy(ctxtStore,
                                                     new jedd.PhysicalDomain[] { C2.v() },
                                                     new jedd.PhysicalDomain[] { C1.v() }));
        final jedd.internal.RelationContainer ctxtLoad =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), src.v(), fld.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:" +
                                               "soot.jimple.paddle.bdddomains.V2> ctxtLoad = jedd.internal.J" +
                                               "edd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal" +
                                               ".Jedd.v().read(contexts), mpagLoad, new jedd.PhysicalDomain[" +
                                               "...]), new jedd.PhysicalDomain[...], new jedd.PhysicalDomain" +
                                               "[...]); at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BD" +
                                               "DMethodPAGContextifier.jedd:102,30-38"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(contexts),
                                                                                                            mpagLoad,
                                                                                                            new jedd.PhysicalDomain[] { MS.v() }),
                                                                             new jedd.PhysicalDomain[] { C2.v() },
                                                                             new jedd.PhysicalDomain[] { C1.v() }));
        loadOut.eqUnion(jedd.internal.Jedd.v().copy(ctxtLoad,
                                                    new jedd.PhysicalDomain[] { C1.v() },
                                                    new jedd.PhysicalDomain[] { C2.v() }));
        final jedd.internal.RelationContainer ctxtAlloc =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V1.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.obj:soot.jim" +
                                               "ple.paddle.bdddomains.H1> ctxtAlloc = jedd.internal.Jedd.v()" +
                                               ".compose(jedd.internal.Jedd.v().read(contexts), mpagAlloc, n" +
                                               "ew jedd.PhysicalDomain[...]); at /tmp/olhotak/soot-trunk/src" +
                                               "/soot/jimple/paddle/BDDMethodPAGContextifier.jedd:105,25-34"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(contexts),
                                                                             mpagAlloc,
                                                                             new jedd.PhysicalDomain[] { MS.v() }));
        allocOut.eqUnion(jedd.internal.Jedd.v().copy(ctxtAlloc,
                                                     new jedd.PhysicalDomain[] { C2.v() },
                                                     new jedd.PhysicalDomain[] { C1.v() }));
        final jedd.internal.RelationContainer globalDsts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.dstc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.dst" +
                                               ":soot.jimple.paddle.bdddomains.V2> globalDsts = jedd.interna" +
                                               "l.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut), ni.g" +
                                               "lobalSet(), new jedd.PhysicalDomain[...]); at /tmp/olhotak/s" +
                                               "oot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.je" +
                                               "dd:108,31-41"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut),
                                                                          ni.globalSet(),
                                                                          new jedd.PhysicalDomain[] { V2.v() }));
        simpleOut.eqMinus(globalDsts);
        simpleOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalDsts,
                                                                                                                 new jedd.PhysicalDomain[] { C2.v() })),
                                                      jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                     new jedd.Attribute[] { dstc.v() },
                                                                                     new jedd.PhysicalDomain[] { C2.v() }),
                                                      new jedd.PhysicalDomain[] {  }));
        final jedd.internal.RelationContainer globalSrcs =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.dstc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.dst" +
                                               ":soot.jimple.paddle.bdddomains.V2> globalSrcs = jedd.interna" +
                                               "l.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut), jedd" +
                                               ".internal.Jedd.v().replace(ni.globalSet(), new jedd.Physical" +
                                               "Domain[...], new jedd.PhysicalDomain[...]), new jedd.Physica" +
                                               "lDomain[...]); at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                               "ddle/BDDMethodPAGContextifier.jedd:111,31-41"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(simpleOut),
                                                                          jedd.internal.Jedd.v().replace(ni.globalSet(),
                                                                                                         new jedd.PhysicalDomain[] { V2.v() },
                                                                                                         new jedd.PhysicalDomain[] { V1.v() }),
                                                                          new jedd.PhysicalDomain[] { V1.v() }));
        simpleOut.eqMinus(globalSrcs);
        simpleOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalSrcs,
                                                                                                                 new jedd.PhysicalDomain[] { C1.v() })),
                                                      jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                     new jedd.Attribute[] { srcc.v() },
                                                                                     new jedd.PhysicalDomain[] { C1.v() }),
                                                      new jedd.PhysicalDomain[] {  }));
        csimple.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dstc.v(), dst.v(), src.v(), srcc.v() },
                                                        new jedd.PhysicalDomain[] { C2.v(), V2.v(), V1.v(), C1.v() },
                                                        ("csimple.add(simpleOut) at /tmp/olhotak/soot-trunk/src/soot/j" +
                                                         "imple/paddle/BDDMethodPAGContextifier.jedd:114,8-15"),
                                                        simpleOut));
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
                                               "reOut), ni.globalSet(), new jedd.PhysicalDomain[...]); at /t" +
                                               "mp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGCon" +
                                               "textifier.jedd:116,36-51"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(storeOut),
                                                                          ni.globalSet(),
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
                                               "reOut), jedd.internal.Jedd.v().replace(ni.globalSet(), new j" +
                                               "edd.PhysicalDomain[...], new jedd.PhysicalDomain[...]), new " +
                                               "jedd.PhysicalDomain[...]); at /tmp/olhotak/soot-trunk/src/so" +
                                               "ot/jimple/paddle/BDDMethodPAGContextifier.jedd:119,36-51"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(storeOut),
                                                                          jedd.internal.Jedd.v().replace(ni.globalSet(),
                                                                                                         new jedd.PhysicalDomain[] { V2.v() },
                                                                                                         new jedd.PhysicalDomain[] { V1.v() }),
                                                                          new jedd.PhysicalDomain[] { V1.v() }));
        storeOut.eqMinus(globalStoreSrcs);
        storeOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalStoreSrcs,
                                                                                                                new jedd.PhysicalDomain[] { C1.v() })),
                                                     jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                    new jedd.Attribute[] { srcc.v() },
                                                                                    new jedd.PhysicalDomain[] { C1.v() }),
                                                     new jedd.PhysicalDomain[] {  }));
        cstore.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dstc.v(), fld.v(), dst.v(), src.v(), srcc.v() },
                                                       new jedd.PhysicalDomain[] { C2.v(), FD.v(), V2.v(), V1.v(), C1.v() },
                                                       ("cstore.add(storeOut) at /tmp/olhotak/soot-trunk/src/soot/jim" +
                                                        "ple/paddle/BDDMethodPAGContextifier.jedd:122,8-14"),
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
                                               "Out), ni.globalSet(), new jedd.PhysicalDomain[...]); at /tmp" +
                                               "/olhotak/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGConte" +
                                               "xtifier.jedd:124,36-50"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(loadOut),
                                                                          ni.globalSet(),
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
                                               "Out), jedd.internal.Jedd.v().replace(ni.globalSet(), new jed" +
                                               "d.PhysicalDomain[...], new jedd.PhysicalDomain[...]), new je" +
                                               "dd.PhysicalDomain[...]); at /tmp/olhotak/soot-trunk/src/soot" +
                                               "/jimple/paddle/BDDMethodPAGContextifier.jedd:127,36-50"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(loadOut),
                                                                          jedd.internal.Jedd.v().replace(ni.globalSet(),
                                                                                                         new jedd.PhysicalDomain[] { V2.v() },
                                                                                                         new jedd.PhysicalDomain[] { V1.v() }),
                                                                          new jedd.PhysicalDomain[] { V1.v() }));
        loadOut.eqMinus(globalLoadSrcs);
        loadOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalLoadSrcs,
                                                                                                               new jedd.PhysicalDomain[] { C1.v() })),
                                                    jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                   new jedd.Attribute[] { srcc.v() },
                                                                                   new jedd.PhysicalDomain[] { C1.v() }),
                                                    new jedd.PhysicalDomain[] {  }));
        cload.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { dstc.v(), fld.v(), dst.v(), src.v(), srcc.v() },
                                                      new jedd.PhysicalDomain[] { C2.v(), FD.v(), V2.v(), V1.v(), C1.v() },
                                                      ("cload.add(loadOut) at /tmp/olhotak/soot-trunk/src/soot/jimpl" +
                                                       "e/paddle/BDDMethodPAGContextifier.jedd:130,8-13"),
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
                                               "jedd.internal.Jedd.v().replace(ni.globalSet(), new jedd.Phys" +
                                               "icalDomain[...], new jedd.PhysicalDomain[...]), new jedd.Phy" +
                                               "sicalDomain[...]); at /tmp/olhotak/soot-trunk/src/soot/jimpl" +
                                               "e/paddle/BDDMethodPAGContextifier.jedd:132,31-46"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(allocOut),
                                                                          jedd.internal.Jedd.v().replace(ni.globalSet(),
                                                                                                         new jedd.PhysicalDomain[] { V2.v() },
                                                                                                         new jedd.PhysicalDomain[] { V1.v() }),
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
                                               "ni.globalallocSet(), new jedd.PhysicalDomain[...]); at /tmp/" +
                                               "olhotak/soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContex" +
                                               "tifier.jedd:135,31-46"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(allocOut),
                                                                          ni.globalallocSet(),
                                                                          new jedd.PhysicalDomain[] { H1.v() }));
        allocOut.eqMinus(globalAllocSrcs);
        allocOut.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(globalAllocSrcs,
                                                                                                                new jedd.PhysicalDomain[] { C2.v() })),
                                                     jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                    new jedd.Attribute[] { objc.v() },
                                                                                    new jedd.PhysicalDomain[] { C2.v() }),
                                                     new jedd.PhysicalDomain[] {  }));
        calloc.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                       new jedd.PhysicalDomain[] { C2.v(), V1.v(), C1.v(), H1.v() },
                                                       ("calloc.add(allocOut) at /tmp/olhotak/soot-trunk/src/soot/jim" +
                                                        "ple/paddle/BDDMethodPAGContextifier.jedd:138,8-14"),
                                                       allocOut));
        return false;
    }
    
    private final jedd.internal.RelationContainer mpagSimple =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), src.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), V1.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.src, soot.jimple.paddle.bdddomains.dst> mpa" +
                                           "gSimple = jedd.internal.Jedd.v().falseBDD() at /tmp/olhotak/" +
                                           "soot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.j" +
                                           "edd:142,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mpagStore =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), src.v(), fld.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), V1.v(), FD.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.src, soot.jimple.paddle.bdddomains.fld, soo" +
                                           "t.jimple.paddle.bdddomains.dst> mpagStore = jedd.internal.Je" +
                                           "dd.v().falseBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple" +
                                           "/paddle/BDDMethodPAGContextifier.jedd:143,12-35"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mpagLoad =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), src.v(), fld.v(), dst.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), V1.v(), FD.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.src, soot.jimple.paddle.bdddomains.fld, soo" +
                                           "t.jimple.paddle.bdddomains.dst> mpagLoad = jedd.internal.Jed" +
                                           "d.v().falseBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/" +
                                           "paddle/BDDMethodPAGContextifier.jedd:144,12-35"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mpagAlloc =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), var.v(), obj.v() },
                                          new jedd.PhysicalDomain[] { MS.v(), V1.v(), H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method, soot.jimple.p" +
                                           "addle.bdddomains.var, soot.jimple.paddle.bdddomains.obj> mpa" +
                                           "gAlloc = jedd.internal.Jedd.v().falseBDD() at /tmp/olhotak/s" +
                                           "oot-trunk/src/soot/jimple/paddle/BDDMethodPAGContextifier.je" +
                                           "dd:145,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
}
