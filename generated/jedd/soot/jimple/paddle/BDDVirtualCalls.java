package soot.jimple.paddle;

import soot.*;
import soot.util.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public class BDDVirtualCalls extends AbsVirtualCalls {
    BDDVirtualCalls(Rvarc_var_objc_obj pt,
                    Rvar_srcm_stmt_signature_kind receivers,
                    Rvar_srcm_stmt_tgtm specials,
                    Qctxt_var_obj_srcm_stmt_kind_tgtm out,
                    Qsrcc_srcm_stmt_kind_tgtc_tgtm statics) {
        super(pt, receivers, specials, out, statics);
        for (Iterator clIt = Scene.v().getClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            for (Iterator mIt = cl.getMethods().iterator(); mIt.hasNext(); ) {
                final SootMethod m = (SootMethod) mIt.next();
                if (m.isAbstract()) continue;
                declaresMethod.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { m.getDeclaringClass().getType(), m.getNumberedSubSignature(), m },
                                                                      new jedd.Attribute[] { type.v(), signature.v(), method.v() },
                                                                      new jedd.PhysicalDomain[] { T3.v(), SG.v(), MT.v() }));
            }
        }
    }
    
    private int lastVarNode = 1;
    
    private int lastAllocNode = 1;
    
    private final jedd.internal.RelationContainer varNodes =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), type.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                           "e.bdddomains.V1, soot.jimple.paddle.bdddomains.type> varNode" +
                                           "s at /home/research/ccl/olhota/soot-trunk2/src/soot/jimple/p" +
                                           "addle/BDDVirtualCalls.jedd:54,12-26"));
    
    private final jedd.internal.RelationContainer allocNodes =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), type.v() },
                                          new jedd.PhysicalDomain[] { H1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj, soot.jimple.padd" +
                                           "le.bdddomains.type> allocNodes at /home/research/ccl/olhota/" +
                                           "soot-trunk2/src/soot/jimple/paddle/BDDVirtualCalls.jedd:55,1" +
                                           "2-23"));
    
    private final jedd.internal.RelationContainer virtual =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v() },
                                          new jedd.PhysicalDomain[] { KD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.kind> virtual = jedd." +
                                           "internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.int" +
                                           "ernal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.intern" +
                                           "al.Jedd.v().literal(new java.lang.Object[...], new jedd.Attr" +
                                           "ibute[...], new jedd.PhysicalDomain[...])), jedd.internal.Je" +
                                           "dd.v().literal(new java.lang.Object[...], new jedd.Attribute" +
                                           "[...], new jedd.PhysicalDomain[...]))), jedd.internal.Jedd.v" +
                                           "().literal(new java.lang.Object[...], new jedd.Attribute[..." +
                                           "], new jedd.PhysicalDomain[...])) at /home/research/ccl/olho" +
                                           "ta/soot-trunk2/src/soot/jimple/paddle/BDDVirtualCalls.jedd:5" +
                                           "6,12-18"),
                                          jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { Kind.VIRTUAL },
                                                                                                                                                                                           new jedd.Attribute[] { kind.v() },
                                                                                                                                                                                           new jedd.PhysicalDomain[] { KD.v() })),
                                                                                                                                jedd.internal.Jedd.v().literal(new Object[] { Kind.INTERFACE },
                                                                                                                                                               new jedd.Attribute[] { kind.v() },
                                                                                                                                                               new jedd.PhysicalDomain[] { KD.v() }))),
                                                                       jedd.internal.Jedd.v().literal(new Object[] { Kind.PRIVILEGED },
                                                                                                      new jedd.Attribute[] { kind.v() },
                                                                                                      new jedd.PhysicalDomain[] { KD.v() })));
    
    private final jedd.internal.RelationContainer threads =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v() },
                                          new jedd.PhysicalDomain[] { T3.v() },
                                          ("private <soot.jimple.paddle.bdddomains.type> threads = jedd." +
                                           "internal.Jedd.v().falseBDD() at /home/research/ccl/olhota/so" +
                                           "ot-trunk2/src/soot/jimple/paddle/BDDVirtualCalls.jedd:57,12-" +
                                           "18"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private void updateNodes() {
        for (; lastVarNode <= PaddleNumberers.v().varNodeNumberer().size(); lastVarNode++) {
            VarNode vn = (VarNode) PaddleNumberers.v().varNodeNumberer().get(lastVarNode);
            varNodes.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { vn, vn.getType() },
                                                            new jedd.Attribute[] { var.v(), type.v() },
                                                            new jedd.PhysicalDomain[] { V1.v(), T1.v() }));
        }
        for (; lastAllocNode <= PaddleNumberers.v().allocNodeNumberer().size(); lastAllocNode++) {
            AllocNode an = (AllocNode) PaddleNumberers.v().allocNodeNumberer().get(lastAllocNode);
            allocNodes.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { an, an.getType() },
                                                              new jedd.Attribute[] { obj.v(), type.v() },
                                                              new jedd.PhysicalDomain[] { H1.v(), T2.v() }));
            if (an instanceof StringConstantNode) {
                StringConstantNode scn = (StringConstantNode) an;
                String constant = scn.getString();
                if (constant.charAt(0) == '[') {
                    if (constant.length() > 1 && constant.charAt(1) == 'L' &&
                          constant.charAt(constant.length() - 1) == ';') {
                        constant = constant.substring(2, constant.length() - 1);
                    } else
                        constant = null;
                }
                if (constant != null && Scene.v().containsClass(constant)) {
                    SootClass cls = Scene.v().getSootClass(constant);
                    for (Iterator methodIt = EntryPoints.v().clinitsOf(cls).iterator(); methodIt.hasNext(); ) {
                        final SootMethod method = (SootMethod) methodIt.next();
                        stringConstants.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { an, method },
                                                                               new jedd.Attribute[] { obj.v(), tgtm.v() },
                                                                               new jedd.PhysicalDomain[] { H1.v(), MT.v() }));
                    }
                }
            } else {
                nonStringConstants.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { an },
                                                                          new jedd.Attribute[] { obj.v() },
                                                                          new jedd.PhysicalDomain[] { H1.v() }));
            }
        }
        threads.eq(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(hier.subtypeRelation(),
                                                                                                             new jedd.PhysicalDomain[] { T2.v() },
                                                                                                             new jedd.PhysicalDomain[] { T3.v() })),
                                                  jedd.internal.Jedd.v().literal(new Object[] { clRunnable },
                                                                                 new jedd.Attribute[] { type.v() },
                                                                                 new jedd.PhysicalDomain[] { T1.v() }),
                                                  new jedd.PhysicalDomain[] { T1.v() }));
    }
    
    protected final RefType clRunnable = RefType.v("java.lang.Runnable");
    
    private final jedd.internal.RelationContainer stringConstants =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { H1.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj, soot.jimple.padd" +
                                           "le.bdddomains.tgtm> stringConstants = jedd.internal.Jedd.v()" +
                                           ".falseBDD() at /home/research/ccl/olhota/soot-trunk2/src/soo" +
                                           "t/jimple/paddle/BDDVirtualCalls.jedd:96,12-23"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer nonStringConstants =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v() },
                                          new jedd.PhysicalDomain[] { H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                           "e.bdddomains.H1> nonStringConstants = jedd.internal.Jedd.v()" +
                                           ".falseBDD() at /home/research/ccl/olhota/soot-trunk2/src/soo" +
                                           "t/jimple/paddle/BDDVirtualCalls.jedd:97,12-20"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final NumberedString sigClinit = Scene.v().getSubSigNumberer().findOrAdd("void <clinit>()");
    
    private final jedd.internal.RelationContainer targets =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v(), signature.v(), method.v() },
                                          new jedd.PhysicalDomain[] { T3.v(), SG.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.type, soot.jimple.pad" +
                                           "dle.bdddomains.signature, soot.jimple.paddle.bdddomains.meth" +
                                           "od> targets = jedd.internal.Jedd.v().falseBDD() at /home/res" +
                                           "earch/ccl/olhota/soot-trunk2/src/soot/jimple/paddle/BDDVirtu" +
                                           "alCalls.jedd:101,12-37"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer declaresMethod =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v(), signature.v(), method.v() },
                                          new jedd.PhysicalDomain[] { T3.v(), SG.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.type, soot.jimple.pad" +
                                           "dle.bdddomains.signature, soot.jimple.paddle.bdddomains.meth" +
                                           "od> declaresMethod = jedd.internal.Jedd.v().falseBDD() at /h" +
                                           "ome/research/ccl/olhota/soot-trunk2/src/soot/jimple/paddle/B" +
                                           "DDVirtualCalls.jedd:102,12-37"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private BDDHierarchy hier = new BDDHierarchy();
    
    private final jedd.internal.RelationContainer newPt =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v() },
                                          new jedd.PhysicalDomain[] { C1.v(), V1.v(), H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.var, soot.jimple.paddle.bdddomains.obj> newPt" +
                                           " = jedd.internal.Jedd.v().falseBDD() at /home/research/ccl/o" +
                                           "lhota/soot-trunk2/src/soot/jimple/paddle/BDDVirtualCalls.jed" +
                                           "d:105,12-28"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allPt =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v() },
                                          new jedd.PhysicalDomain[] { C1.v(), V1.v(), H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.var, soot.jimple.paddle.bdddomains.obj> allPt" +
                                           " = jedd.internal.Jedd.v().falseBDD() at /home/research/ccl/o" +
                                           "lhota/soot-trunk2/src/soot/jimple/paddle/BDDVirtualCalls.jed" +
                                           "d:106,12-28"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer newRcv =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), MS.v(), ST.v(), SG.v(), KD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.signature, soot.jimple.paddle.bddd" +
                                           "omains.kind> newRcv = jedd.internal.Jedd.v().falseBDD() at /" +
                                           "home/research/ccl/olhota/soot-trunk2/src/soot/jimple/paddle/" +
                                           "BDDVirtualCalls.jedd:107,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allRcv =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), MS.v(), ST.v(), SG.v(), KD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.signature, soot.jimple.paddle.bddd" +
                                           "omains.kind> allRcv = jedd.internal.Jedd.v().falseBDD() at /" +
                                           "home/research/ccl/olhota/soot-trunk2/src/soot/jimple/paddle/" +
                                           "BDDVirtualCalls.jedd:108,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer newSpc =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), MS.v(), ST.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.tgtm> newSpc = jedd.internal.Jedd." +
                                           "v().falseBDD() at /home/research/ccl/olhota/soot-trunk2/src/" +
                                           "soot/jimple/paddle/BDDVirtualCalls.jedd:109,12-35"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allSpc =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), MS.v(), ST.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.tgtm> allSpc = jedd.internal.Jedd." +
                                           "v().falseBDD() at /home/research/ccl/olhota/soot-trunk2/src/" +
                                           "soot/jimple/paddle/BDDVirtualCalls.jedd:110,12-35"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private boolean change;
    
    public boolean update() {
        change = false;
        this.updateNodes();
        newPt.eq(jedd.internal.Jedd.v().project(pt.get(), new jedd.PhysicalDomain[] { C2.v() }));
        allPt.eqUnion(newPt);
        newRcv.eq(receivers.get());
        allRcv.eqUnion(newRcv);
        newSpc.eq(specials.get());
        allSpc.eqUnion(newSpc);
        this.updateClinits();
        this.updateVirtuals();
        this.updateSpecials();
        return change;
    }
    
    private final jedd.internal.RelationContainer clinits =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), kind.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), MS.v(), ST.v(), KD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.kind> clinits at /home/research/cc" +
                                           "l/olhota/soot-trunk2/src/soot/jimple/paddle/BDDVirtualCalls." +
                                           "jedd:136,12-35"));
    
    private void updateClinits() {
        clinits.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(newRcv,
                                                                                                               new jedd.PhysicalDomain[] { SG.v() })),
                                                    jedd.internal.Jedd.v().literal(new Object[] { Kind.CLINIT },
                                                                                   new jedd.Attribute[] { kind.v() },
                                                                                   new jedd.PhysicalDomain[] { KD.v() }),
                                                    new jedd.PhysicalDomain[] { KD.v() }));
        final jedd.internal.RelationContainer tgtMethods =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), var.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.tgtm:soot.ji" +
                                               "mple.paddle.bdddomains.MT> tgtMethods = jedd.internal.Jedd.v" +
                                               "().compose(jedd.internal.Jedd.v().read(newPt), stringConstan" +
                                               "ts, new jedd.PhysicalDomain[...]); at /home/research/ccl/olh" +
                                               "ota/soot-trunk2/src/soot/jimple/paddle/BDDVirtualCalls.jedd:" +
                                               "140,26-36"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(newPt),
                                                                             stringConstants,
                                                                             new jedd.PhysicalDomain[] { H1.v() }));
        final jedd.internal.RelationContainer newStatics =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.C2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT> newSt" +
                                               "atics = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                               "ead(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().re" +
                                               "ad(tgtMethods), clinits, new jedd.PhysicalDomain[...])), jed" +
                                               "d.internal.Jedd.v().literal(new java.lang.Object[...], new j" +
                                               "edd.Attribute[...], new jedd.PhysicalDomain[...]), new jedd." +
                                               "PhysicalDomain[...]); at /home/research/ccl/olhota/soot-trun" +
                                               "k2/src/soot/jimple/paddle/BDDVirtualCalls.jedd:143,45-55"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(tgtMethods),
                                                                                                                                     clinits,
                                                                                                                                     new jedd.PhysicalDomain[] { V1.v() })),
                                                                          jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                         new jedd.Attribute[] { tgtc.v() },
                                                                                                         new jedd.PhysicalDomain[] { C2.v() }),
                                                                          new jedd.PhysicalDomain[] {  }));
        statics.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), tgtm.v(), srcm.v(), tgtc.v(), kind.v(), srcc.v() },
                                                        new jedd.PhysicalDomain[] { ST.v(), MT.v(), MS.v(), C2.v(), KD.v(), C1.v() },
                                                        ("statics.add(newStatics) at /home/research/ccl/olhota/soot-tr" +
                                                         "unk2/src/soot/jimple/paddle/BDDVirtualCalls.jedd:146,8-15"),
                                                        newStatics));
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newStatics), jedd.internal.Jedd.v().falseBDD()))
            change = true;
    }
    
    private final jedd.internal.RelationContainer resolvedSpecials =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { C2.v(), V1.v(), H1.v(), MS.v(), ST.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.var, soot.jimple.paddle.bdddomains.obj, soot." +
                                           "jimple.paddle.bdddomains.srcm, soot.jimple.paddle.bdddomains" +
                                           ".stmt, soot.jimple.paddle.bdddomains.tgtm> resolvedSpecials " +
                                           "= jedd.internal.Jedd.v().falseBDD() at /home/research/ccl/ol" +
                                           "hota/soot-trunk2/src/soot/jimple/paddle/BDDVirtualCalls.jedd" +
                                           ":149,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private void updateSpecials() {
        final jedd.internal.RelationContainer newSpecials =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V1.v(), H1.v(), MS.v(), ST.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.obj:soot.jim" +
                                               "ple.paddle.bdddomains.H1, soot.jimple.paddle.bdddomains.srcm" +
                                               ":soot.jimple.paddle.bdddomains.MS, soot.jimple.paddle.bdddom" +
                                               "ains.stmt:soot.jimple.paddle.bdddomains.ST, soot.jimple.padd" +
                                               "le.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT> newSpec" +
                                               "ials = jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v()" +
                                               ".join(jedd.internal.Jedd.v().read(newPt), allSpc, new jedd.P" +
                                               "hysicalDomain[...]), new jedd.PhysicalDomain[...], new jedd." +
                                               "PhysicalDomain[...]); at /home/research/ccl/olhota/soot-trun" +
                                               "k2/src/soot/jimple/paddle/BDDVirtualCalls.jedd:152,43-54"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newPt),
                                                                                                         allSpc,
                                                                                                         new jedd.PhysicalDomain[] { V1.v() }),
                                                                             new jedd.PhysicalDomain[] { C1.v() },
                                                                             new jedd.PhysicalDomain[] { C2.v() }));
        newSpecials.eqMinus(resolvedSpecials);
        resolvedSpecials.eqUnion(newSpecials);
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), stmt.v(), var.v(), tgtm.v(), srcm.v(), obj.v(), kind.v() },
                                                    new jedd.PhysicalDomain[] { C2.v(), ST.v(), V1.v(), MT.v(), MS.v(), H1.v(), KD.v() },
                                                    ("out.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                                     "ead(newSpecials), jedd.internal.Jedd.v().literal(new java.la" +
                                                     "ng.Object[...], new jedd.Attribute[...], new jedd.PhysicalDo" +
                                                     "main[...]), new jedd.PhysicalDomain[...])) at /home/research" +
                                                     "/ccl/olhota/soot-trunk2/src/soot/jimple/paddle/BDDVirtualCal" +
                                                     "ls.jedd:158,8-11"),
                                                    jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newSpecials),
                                                                                jedd.internal.Jedd.v().literal(new Object[] { Kind.SPECIAL },
                                                                                                               new jedd.Attribute[] { kind.v() },
                                                                                                               new jedd.PhysicalDomain[] { KD.v() }),
                                                                                new jedd.PhysicalDomain[] {  })));
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newSpecials), jedd.internal.Jedd.v().falseBDD()))
            change = true;
    }
    
    private final jedd.internal.RelationContainer callSiteTargets =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), type.v(), kind.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), MS.v(), ST.v(), T2.v(), KD.v(), MT.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.type, soot.jimple.paddle.bdddomain" +
                                           "s.kind, soot.jimple.paddle.bdddomains.tgtm> callSiteTargets " +
                                           "= jedd.internal.Jedd.v().falseBDD() at /home/research/ccl/ol" +
                                           "hota/soot-trunk2/src/soot/jimple/paddle/BDDVirtualCalls.jedd" +
                                           ":162,12-47"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer rcvSigs =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), signature.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), SG.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.signature> rcvSigs = jedd.internal.Jedd.v().fa" +
                                           "lseBDD() at /home/research/ccl/olhota/soot-trunk2/src/soot/j" +
                                           "imple/paddle/BDDVirtualCalls.jedd:163,12-28"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer threadRcvSigs =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), signature.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), SG.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.signature> threadRcvSigs = jedd.internal.Jedd." +
                                           "v().falseBDD() at /home/research/ccl/olhota/soot-trunk2/src/" +
                                           "soot/jimple/paddle/BDDVirtualCalls.jedd:164,12-28"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer rcv =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), MS.v(), ST.v(), SG.v(), KD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.signature, soot.jimple.paddle.bddd" +
                                           "omains.kind> rcv = jedd.internal.Jedd.v().falseBDD() at /hom" +
                                           "e/research/ccl/olhota/soot-trunk2/src/soot/jimple/paddle/BDD" +
                                           "VirtualCalls.jedd:165,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private void updateVirtuals() {
        final jedd.internal.RelationContainer newVirtRcv =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), MS.v(), ST.v(), SG.v(), KD.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                               "le.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                               "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.sig" +
                                               "nature:soot.jimple.paddle.bdddomains.SG, soot.jimple.paddle." +
                                               "bdddomains.kind:soot.jimple.paddle.bdddomains.KD> newVirtRcv" +
                                               " = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(n" +
                                               "ewRcv), jedd.internal.Jedd.v().union(jedd.internal.Jedd.v()." +
                                               "read(virtual), jedd.internal.Jedd.v().literal(new java.lang." +
                                               "Object[...], new jedd.Attribute[...], new jedd.PhysicalDomai" +
                                               "n[...])), new jedd.PhysicalDomain[...]); at /home/research/c" +
                                               "cl/olhota/soot-trunk2/src/soot/jimple/paddle/BDDVirtualCalls" +
                                               ".jedd:167,43-53"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newRcv),
                                                                          jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(virtual),
                                                                                                       jedd.internal.Jedd.v().literal(new Object[] { Kind.THREAD },
                                                                                                                                      new jedd.Attribute[] { kind.v() },
                                                                                                                                      new jedd.PhysicalDomain[] { KD.v() })),
                                                                          new jedd.PhysicalDomain[] { KD.v() }));
        rcv.eqUnion(newVirtRcv);
        final jedd.internal.RelationContainer sigs =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), signature.v(), kind.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), SG.v(), KD.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.signature:soot.jimple" +
                                               ".paddle.bdddomains.SG, soot.jimple.paddle.bdddomains.kind:so" +
                                               "ot.jimple.paddle.bdddomains.KD> sigs = jedd.internal.Jedd.v(" +
                                               ").project(newVirtRcv, new jedd.PhysicalDomain[...]); at /hom" +
                                               "e/research/ccl/olhota/soot-trunk2/src/soot/jimple/paddle/BDD" +
                                               "VirtualCalls.jedd:171,31-35"),
                                              jedd.internal.Jedd.v().project(newVirtRcv,
                                                                             new jedd.PhysicalDomain[] { ST.v(), MS.v() }));
        rcvSigs.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(sigs),
                                                       virtual,
                                                       new jedd.PhysicalDomain[] { KD.v() }));
        threadRcvSigs.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(sigs),
                                                             jedd.internal.Jedd.v().literal(new Object[] { Kind.THREAD },
                                                                                            new jedd.Attribute[] { kind.v() },
                                                                                            new jedd.PhysicalDomain[] { KD.v() }),
                                                             new jedd.PhysicalDomain[] { KD.v() }));
        final jedd.internal.RelationContainer ptTypes =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), type.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.type:soot.jimple.padd" +
                                               "le.bdddomains.T2> ptTypes = jedd.internal.Jedd.v().compose(j" +
                                               "edd.internal.Jedd.v().read(allocNodes), jedd.internal.Jedd.v" +
                                               "().project(newPt, new jedd.PhysicalDomain[...]), new jedd.Ph" +
                                               "ysicalDomain[...]); at /home/research/ccl/olhota/soot-trunk2" +
                                               "/src/soot/jimple/paddle/BDDVirtualCalls.jedd:176,20-27"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(allocNodes),
                                                                             jedd.internal.Jedd.v().project(newPt,
                                                                                                            new jedd.PhysicalDomain[] { C1.v() }),
                                                                             new jedd.PhysicalDomain[] { H1.v() }));
        final jedd.internal.RelationContainer newTypes =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v(), signature.v() },
                                              new jedd.PhysicalDomain[] { T3.v(), SG.v() },
                                              ("<soot.jimple.paddle.bdddomains.type:soot.jimple.paddle.bdddo" +
                                               "mains.T3, soot.jimple.paddle.bdddomains.signature:soot.jimpl" +
                                               "e.paddle.bdddomains.SG> newTypes = jedd.internal.Jedd.v().re" +
                                               "place(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v()." +
                                               "read(ptTypes), rcvSigs, new jedd.PhysicalDomain[...]), new j" +
                                               "edd.PhysicalDomain[...], new jedd.PhysicalDomain[...]); at /" +
                                               "home/research/ccl/olhota/soot-trunk2/src/soot/jimple/paddle/" +
                                               "BDDVirtualCalls.jedd:178,26-34"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(ptTypes),
                                                                                                            rcvSigs,
                                                                                                            new jedd.PhysicalDomain[] { V1.v() }),
                                                                             new jedd.PhysicalDomain[] { T2.v() },
                                                                             new jedd.PhysicalDomain[] { T3.v() }));
        newTypes.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ptTypes,
                                                                                                                                                                           new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                                                                           new jedd.PhysicalDomain[] { T3.v() })),
                                                                                                                threads,
                                                                                                                new jedd.PhysicalDomain[] { T3.v() })),
                                                        threadRcvSigs,
                                                        new jedd.PhysicalDomain[] { V1.v() }));
        hier.update();
        newTypes.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(newTypes),
                                                                                       hier.anySub(),
                                                                                       new jedd.PhysicalDomain[] { T3.v() }),
                                                        new jedd.PhysicalDomain[] { T2.v() },
                                                        new jedd.PhysicalDomain[] { T3.v() }));
        newTypes.eq(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(newTypes,
                                                                                                                                          new jedd.PhysicalDomain[] { T3.v() },
                                                                                                                                          new jedd.PhysicalDomain[] { T2.v() })),
                                                                               hier.concrete(),
                                                                               new jedd.PhysicalDomain[] { T2.v() }),
                                                   new jedd.PhysicalDomain[] { T2.v() },
                                                   new jedd.PhysicalDomain[] { T3.v() }));
        newTypes.eqMinus(jedd.internal.Jedd.v().project(targets, new jedd.PhysicalDomain[] { MT.v() }));
        final jedd.internal.RelationContainer toResolve =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), signature.v(), supt.v() },
                                              new jedd.PhysicalDomain[] { T2.v(), SG.v(), T3.v() },
                                              ("<soot.jimple.paddle.bdddomains.subt:soot.jimple.paddle.bdddo" +
                                               "mains.T2, soot.jimple.paddle.bdddomains.signature:soot.jimpl" +
                                               "e.paddle.bdddomains.SG, soot.jimple.paddle.bdddomains.supt:s" +
                                               "oot.jimple.paddle.bdddomains.T3> toResolve = jedd.internal.J" +
                                               "edd.v().copy(newTypes, new jedd.PhysicalDomain[...], new jed" +
                                               "d.PhysicalDomain[...]); at /home/research/ccl/olhota/soot-tr" +
                                               "unk2/src/soot/jimple/paddle/BDDVirtualCalls.jedd:194,32-41"),
                                              jedd.internal.Jedd.v().copy(newTypes,
                                                                          new jedd.PhysicalDomain[] { T3.v() },
                                                                          new jedd.PhysicalDomain[] { T2.v() }));
        final jedd.internal.RelationContainer newTargets =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v(), signature.v(), method.v() },
                                              new jedd.PhysicalDomain[] { T2.v(), SG.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.type:soot.jimple.paddle.bdddo" +
                                               "mains.T2, soot.jimple.paddle.bdddomains.signature:soot.jimpl" +
                                               "e.paddle.bdddomains.SG, soot.jimple.paddle.bdddomains.method" +
                                               ":soot.jimple.paddle.bdddomains.MT> newTargets = jedd.interna" +
                                               "l.Jedd.v().falseBDD(); at /home/research/ccl/olhota/soot-tru" +
                                               "nk2/src/soot/jimple/paddle/BDDVirtualCalls.jedd:196,34-44"),
                                              jedd.internal.Jedd.v().falseBDD());
        do  {
            final jedd.internal.RelationContainer resolved =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), signature.v(), supt.v(), method.v() },
                                                  new jedd.PhysicalDomain[] { T2.v(), SG.v(), T3.v(), MT.v() },
                                                  ("<soot.jimple.paddle.bdddomains.subt:soot.jimple.paddle.bdddo" +
                                                   "mains.T2, soot.jimple.paddle.bdddomains.signature:soot.jimpl" +
                                                   "e.paddle.bdddomains.SG, soot.jimple.paddle.bdddomains.supt:s" +
                                                   "oot.jimple.paddle.bdddomains.T3, soot.jimple.paddle.bdddomai" +
                                                   "ns.method:soot.jimple.paddle.bdddomains.MT> resolved = jedd." +
                                                   "internal.Jedd.v().join(jedd.internal.Jedd.v().read(toResolve" +
                                                   "), declaresMethod, new jedd.PhysicalDomain[...]); at /home/r" +
                                                   "esearch/ccl/olhota/soot-trunk2/src/soot/jimple/paddle/BDDVir" +
                                                   "tualCalls.jedd:201,44-52"),
                                                  jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(toResolve),
                                                                              declaresMethod,
                                                                              new jedd.PhysicalDomain[] { T3.v(), SG.v() }));
            toResolve.eqMinus(jedd.internal.Jedd.v().project(resolved, new jedd.PhysicalDomain[] { MT.v() }));
            newTargets.eqUnion(jedd.internal.Jedd.v().project(resolved, new jedd.PhysicalDomain[] { T3.v() }));
            toResolve.eq(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(toResolve,
                                                                                                                   new jedd.PhysicalDomain[] { T3.v() },
                                                                                                                   new jedd.PhysicalDomain[] { T1.v() })),
                                                        jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(hier.extend()),
                                                                                                                    hier.array()),
                                                                                       new jedd.PhysicalDomain[] { T2.v() },
                                                                                       new jedd.PhysicalDomain[] { T3.v() }),
                                                        new jedd.PhysicalDomain[] { T1.v() }));
        }while(!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(toResolve),
                                              jedd.internal.Jedd.v().falseBDD())); 
        final jedd.internal.RelationContainer typedPt =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v(), type.v(), ctxt.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), H1.v(), T2.v(), C1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                               "e.bdddomains.H1, soot.jimple.paddle.bdddomains.type:soot.jim" +
                                               "ple.paddle.bdddomains.T2, soot.jimple.paddle.bdddomains.ctxt" +
                                               ":soot.jimple.paddle.bdddomains.C1> typedPt = jedd.internal.J" +
                                               "edd.v().join(jedd.internal.Jedd.v().read(allocNodes), newPt," +
                                               " new jedd.PhysicalDomain[...]); at /home/research/ccl/olhota" +
                                               "/soot-trunk2/src/soot/jimple/paddle/BDDVirtualCalls.jedd:215" +
                                               ",31-38"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(allocNodes),
                                                                          newPt,
                                                                          new jedd.PhysicalDomain[] { H1.v() }));
        typedPt.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(typedPt,
                                                                                                                  new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                  new jedd.PhysicalDomain[] { T3.v() })),
                                                       hier.anySub(),
                                                       new jedd.PhysicalDomain[] { T3.v() }));
        final jedd.internal.RelationContainer varCtxtPt =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), dtp.v(), obj.v(), type.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), T1.v(), H1.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.dtp:soot.jim" +
                                               "ple.paddle.bdddomains.T1, soot.jimple.paddle.bdddomains.obj:" +
                                               "soot.jimple.paddle.bdddomains.H1, soot.jimple.paddle.bdddoma" +
                                               "ins.type:soot.jimple.paddle.bdddomains.T2> varCtxtPt = jedd." +
                                               "internal.Jedd.v().join(jedd.internal.Jedd.v().read(typedPt)," +
                                               " varNodes, new jedd.PhysicalDomain[...]); at /home/research/" +
                                               "ccl/olhota/soot-trunk2/src/soot/jimple/paddle/BDDVirtualCall" +
                                               "s.jedd:219,36-45"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(typedPt),
                                                                          varNodes,
                                                                          new jedd.PhysicalDomain[] { V1.v() }));
        varCtxtPt.eq(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(varCtxtPt),
                                                 hier.subtypeRelation(),
                                                 new jedd.PhysicalDomain[] { T2.v(), T1.v() }));
        varCtxtPt.eq(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(varCtxtPt),
                                                 hier.concrete(),
                                                 new jedd.PhysicalDomain[] { T2.v() }));
        callSiteTargets.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(newVirtRcv),
                                                                                              targets,
                                                                                              new jedd.PhysicalDomain[] { SG.v() }),
                                                               new jedd.PhysicalDomain[] { T3.v() },
                                                               new jedd.PhysicalDomain[] { T2.v() }));
        callSiteTargets.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(rcv),
                                                               newTargets,
                                                               new jedd.PhysicalDomain[] { SG.v() }));
        final jedd.internal.RelationContainer newVirtuals =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), V1.v(), H1.v(), MS.v(), ST.v(), KD.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.obj:soot.jim" +
                                               "ple.paddle.bdddomains.H1, soot.jimple.paddle.bdddomains.srcm" +
                                               ":soot.jimple.paddle.bdddomains.MS, soot.jimple.paddle.bdddom" +
                                               "ains.stmt:soot.jimple.paddle.bdddomains.ST, soot.jimple.padd" +
                                               "le.bdddomains.kind:soot.jimple.paddle.bdddomains.KD, soot.ji" +
                                               "mple.paddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT" +
                                               "> newVirtuals = jedd.internal.Jedd.v().project(jedd.internal" +
                                               ".Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jed" +
                                               "d.v().replace(jedd.internal.Jedd.v().project(varCtxtPt, new " +
                                               "jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[...], new" +
                                               " jedd.PhysicalDomain[...])), callSiteTargets, new jedd.Physi" +
                                               "calDomain[...]), new jedd.PhysicalDomain[...]); at /home/res" +
                                               "earch/ccl/olhota/soot-trunk2/src/soot/jimple/paddle/BDDVirtu" +
                                               "alCalls.jedd:234,49-60"),
                                              jedd.internal.Jedd.v().project(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(varCtxtPt,
                                                                                                                                                                                                   new jedd.PhysicalDomain[] { T1.v() }),
                                                                                                                                                                    new jedd.PhysicalDomain[] { C1.v() },
                                                                                                                                                                    new jedd.PhysicalDomain[] { C2.v() })),
                                                                                                         callSiteTargets,
                                                                                                         new jedd.PhysicalDomain[] { T2.v(), V1.v() }),
                                                                             new jedd.PhysicalDomain[] { T2.v() }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), ctxt.v(), tgtm.v(), var.v(), srcm.v(), kind.v(), obj.v() },
                                                    new jedd.PhysicalDomain[] { ST.v(), C2.v(), MT.v(), V1.v(), MS.v(), KD.v(), H1.v() },
                                                    ("out.add(newVirtuals) at /home/research/ccl/olhota/soot-trunk" +
                                                     "2/src/soot/jimple/paddle/BDDVirtualCalls.jedd:238,8-11"),
                                                    newVirtuals));
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newVirtuals), jedd.internal.Jedd.v().falseBDD()))
            change = true;
    }
}
