package soot.jimple.paddle;

import soot.*;
import soot.util.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public class BDDVirtualCalls extends AbsVirtualCalls {
    BDDVirtualCalls(Rvar_obj pt,
                    Rlocal_srcm_stmt_signature_kind receivers,
                    Rlocal_srcm_stmt_tgtm specials,
                    Qctxt_local_obj_srcm_stmt_kind_tgtm out,
                    Qsrcc_srcm_stmt_kind_tgtc_tgtm statics) {
        super(pt, receivers, specials, out, statics);
        for (Iterator clIt = Scene.v().getClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            for (Iterator mIt = cl.getMethods().iterator(); mIt.hasNext(); ) {
                final SootMethod m = (SootMethod) mIt.next();
                if (m.isAbstract()) continue;
                declaresMethod.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { m.getDeclaringClass().getType(), m.getNumberedSubSignature(), m },
                                                                      new jedd.Attribute[] { type.v(), signature.v(), method.v() },
                                                                      new jedd.PhysicalDomain[] { T1.v(), H2.v(), T3.v() }));
            }
        }
    }
    
    private int lastVarNode = 1;
    
    private int lastAllocNode = 1;
    
    private final jedd.internal.RelationContainer varNodes =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), local.v(), var.v(), type.v() },
                                          new jedd.PhysicalDomain[] { V3.v(), V2.v(), V1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt:soot.jimple.padd" +
                                           "le.bdddomains.V3, soot.jimple.paddle.bdddomains.local:soot.j" +
                                           "imple.paddle.bdddomains.V2, soot.jimple.paddle.bdddomains.va" +
                                           "r:soot.jimple.paddle.bdddomains.V1, soot.jimple.paddle.bdddo" +
                                           "mains.type> varNodes at /tmp/soot-trunk/src/soot/jimple/padd" +
                                           "le/BDDVirtualCalls.jedd:54,12-45"));
    
    private final jedd.internal.RelationContainer allocNodes =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), type.v() },
                                          new jedd.PhysicalDomain[] { H1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj, soot.jimple.padd" +
                                           "le.bdddomains.type> allocNodes at /tmp/soot-trunk/src/soot/j" +
                                           "imple/paddle/BDDVirtualCalls.jedd:55,12-23"));
    
    private final jedd.internal.RelationContainer virtual =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v() },
                                          new jedd.PhysicalDomain[] { FD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.kind> virtual = jedd." +
                                           "internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.int" +
                                           "ernal.Jedd.v().literal(new java.lang.Object[...], new jedd.A" +
                                           "ttribute[...], new jedd.PhysicalDomain[...])), jedd.internal" +
                                           ".Jedd.v().literal(new java.lang.Object[...], new jedd.Attrib" +
                                           "ute[...], new jedd.PhysicalDomain[...])) at /tmp/soot-trunk/" +
                                           "src/soot/jimple/paddle/BDDVirtualCalls.jedd:56,12-18"),
                                          jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { Kind.VIRTUAL },
                                                                                                                                  new jedd.Attribute[] { kind.v() },
                                                                                                                                  new jedd.PhysicalDomain[] { FD.v() })),
                                                                       jedd.internal.Jedd.v().literal(new Object[] { Kind.INTERFACE },
                                                                                                      new jedd.Attribute[] { kind.v() },
                                                                                                      new jedd.PhysicalDomain[] { FD.v() })));
    
    private final jedd.internal.RelationContainer threads =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v() },
                                          new jedd.PhysicalDomain[] { T1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.type> threads = jedd." +
                                           "internal.Jedd.v().falseBDD() at /tmp/soot-trunk/src/soot/jim" +
                                           "ple/paddle/BDDVirtualCalls.jedd:57,12-18"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private void updateNodes() {
        for (; lastVarNode <= PaddleNumberers.v().varNodeNumberer().size(); lastVarNode++) {
            VarNode vn = (VarNode) PaddleNumberers.v().varNodeNumberer().get(lastVarNode);
            if (vn.getVariable() instanceof Local) {
                varNodes.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { vn.context(), vn.getVariable(), vn, vn.getType() },
                                                                new jedd.Attribute[] { ctxt.v(), local.v(), var.v(), type.v() },
                                                                new jedd.PhysicalDomain[] { V3.v(), V2.v(), V1.v(), T2.v() }));
            }
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
                                                                               new jedd.PhysicalDomain[] { H1.v(), T2.v() }));
                    }
                }
            } else {
                nonStringConstants.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { an },
                                                                          new jedd.Attribute[] { obj.v() },
                                                                          new jedd.PhysicalDomain[] { H1.v() }));
            }
        }
        threads.eq(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(hier.subtypeRelation()),
                                                  jedd.internal.Jedd.v().literal(new Object[] { clRunnable },
                                                                                 new jedd.Attribute[] { type.v() },
                                                                                 new jedd.PhysicalDomain[] { T2.v() }),
                                                  new jedd.PhysicalDomain[] { T2.v() }));
    }
    
    protected final RefType clRunnable = RefType.v("java.lang.Runnable");
    
    private final jedd.internal.RelationContainer stringConstants =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { H1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj, soot.jimple.padd" +
                                           "le.bdddomains.tgtm> stringConstants = jedd.internal.Jedd.v()" +
                                           ".falseBDD() at /tmp/soot-trunk/src/soot/jimple/paddle/BDDVir" +
                                           "tualCalls.jedd:102,12-23"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer nonStringConstants =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v() },
                                          new jedd.PhysicalDomain[] { H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                           "e.bdddomains.H1> nonStringConstants = jedd.internal.Jedd.v()" +
                                           ".falseBDD() at /tmp/soot-trunk/src/soot/jimple/paddle/BDDVir" +
                                           "tualCalls.jedd:103,12-20"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final NumberedString sigClinit = Scene.v().getSubSigNumberer().findOrAdd("void <clinit>()");
    
    private final jedd.internal.RelationContainer targets =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v(), signature.v(), method.v() },
                                          new jedd.PhysicalDomain[] { T2.v(), H2.v(), T3.v() },
                                          ("private <soot.jimple.paddle.bdddomains.type, soot.jimple.pad" +
                                           "dle.bdddomains.signature, soot.jimple.paddle.bdddomains.meth" +
                                           "od> targets = jedd.internal.Jedd.v().falseBDD() at /tmp/soot" +
                                           "-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:107,12-37"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer declaresMethod =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v(), signature.v(), method.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), H2.v(), T3.v() },
                                          ("private <soot.jimple.paddle.bdddomains.type, soot.jimple.pad" +
                                           "dle.bdddomains.signature, soot.jimple.paddle.bdddomains.meth" +
                                           "od:soot.jimple.paddle.bdddomains.T3> declaresMethod = jedd.i" +
                                           "nternal.Jedd.v().falseBDD() at /tmp/soot-trunk/src/soot/jimp" +
                                           "le/paddle/BDDVirtualCalls.jedd:108,12-40"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private BDDHierarchy hier = new BDDHierarchy();
    
    private final jedd.internal.RelationContainer newPt =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.obj> newPt = jedd.internal.Jedd.v().falseBDD()" +
                                           " at /tmp/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.j" +
                                           "edd:111,12-22"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allPt =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.obj> allPt = jedd.internal.Jedd.v().falseBDD()" +
                                           " at /tmp/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.j" +
                                           "edd:112,12-22"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer newRcv =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { local.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.local, soot.jimple.pa" +
                                           "ddle.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, so" +
                                           "ot.jimple.paddle.bdddomains.signature, soot.jimple.paddle.bd" +
                                           "ddomains.kind> newRcv = jedd.internal.Jedd.v().falseBDD() at" +
                                           " /tmp/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jedd" +
                                           ":113,12-48"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allRcv =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { local.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.local, soot.jimple.pa" +
                                           "ddle.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, so" +
                                           "ot.jimple.paddle.bdddomains.signature, soot.jimple.paddle.bd" +
                                           "ddomains.kind> allRcv = jedd.internal.Jedd.v().falseBDD() at" +
                                           " /tmp/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jedd" +
                                           ":114,12-48"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer newSpc =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { local.v(), srcm.v(), stmt.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.local, soot.jimple.pa" +
                                           "ddle.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, so" +
                                           "ot.jimple.paddle.bdddomains.tgtm> newSpc = jedd.internal.Jed" +
                                           "d.v().falseBDD() at /tmp/soot-trunk/src/soot/jimple/paddle/B" +
                                           "DDVirtualCalls.jedd:115,12-37"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allSpc =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { local.v(), srcm.v(), stmt.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.local, soot.jimple.pa" +
                                           "ddle.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, so" +
                                           "ot.jimple.paddle.bdddomains.tgtm> allSpc = jedd.internal.Jed" +
                                           "d.v().falseBDD() at /tmp/soot-trunk/src/soot/jimple/paddle/B" +
                                           "DDVirtualCalls.jedd:116,12-37"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public void update() {
        updateNodes();
        newPt.eq(pt.get());
        allPt.eqUnion(newPt);
        newRcv.eq(receivers.get());
        allRcv.eqUnion(newRcv);
        newSpc.eq(specials.get());
        allSpc.eqUnion(newSpc);
        updateClinits();
        updateVirtuals();
        updateSpecials();
    }
    
    private void updateClinits() {
        final jedd.internal.RelationContainer clinits =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { local.v(), srcm.v(), stmt.v(), kind.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), T1.v(), ST.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.local:soot.jimple.paddle.bddd" +
                                               "omains.V2, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pa" +
                                               "ddle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot." +
                                               "jimple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.k" +
                                               "ind:soot.jimple.paddle.bdddomains.FD> clinits = jedd.interna" +
                                               "l.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Je" +
                                               "dd.v().project(jedd.internal.Jedd.v().replace(allRcv, new je" +
                                               "dd.PhysicalDomain[...], new jedd.PhysicalDomain[...]), new j" +
                                               "edd.PhysicalDomain[...])), jedd.internal.Jedd.v().literal(ne" +
                                               "w java.lang.Object[...], new jedd.Attribute[...], new jedd.P" +
                                               "hysicalDomain[...]), new jedd.PhysicalDomain[...]); at /tmp/" +
                                               "soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:138,3" +
                                               "4-41"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(jedd.internal.Jedd.v().replace(allRcv,
                                                                                                                                                                    new jedd.PhysicalDomain[] { V1.v() },
                                                                                                                                                                    new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                                     new jedd.PhysicalDomain[] { H2.v() })),
                                                                          jedd.internal.Jedd.v().literal(new Object[] { Kind.CLINIT },
                                                                                                         new jedd.Attribute[] { kind.v() },
                                                                                                         new jedd.PhysicalDomain[] { FD.v() }),
                                                                          new jedd.PhysicalDomain[] { FD.v() }));
        final jedd.internal.RelationContainer ctxtLocalPt =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), local.v(), obj.v() },
                                              new jedd.PhysicalDomain[] { V3.v(), V2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.V3, soot.jimple.paddle.bdddomains.local:soot.jimple.pa" +
                                               "ddle.bdddomains.V2, soot.jimple.paddle.bdddomains.obj:soot.j" +
                                               "imple.paddle.bdddomains.H1> ctxtLocalPt = jedd.internal.Jedd" +
                                               ".v().compose(jedd.internal.Jedd.v().read(newPt), jedd.intern" +
                                               "al.Jedd.v().project(varNodes, new jedd.PhysicalDomain[...])," +
                                               " new jedd.PhysicalDomain[...]); at /tmp/soot-trunk/src/soot/" +
                                               "jimple/paddle/BDDVirtualCalls.jedd:141,27-38"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(newPt),
                                                                             jedd.internal.Jedd.v().project(varNodes,
                                                                                                            new jedd.PhysicalDomain[] { T2.v() }),
                                                                             new jedd.PhysicalDomain[] { V1.v() }));
        final jedd.internal.RelationContainer tgtMethods =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), local.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { V3.v(), V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.V3, soot.jimple.paddle.bdddomains.local:soot.jimple.pa" +
                                               "ddle.bdddomains.V2, soot.jimple.paddle.bdddomains.tgtm:soot." +
                                               "jimple.paddle.bdddomains.T2> tgtMethods = jedd.internal.Jedd" +
                                               ".v().compose(jedd.internal.Jedd.v().read(ctxtLocalPt), strin" +
                                               "gConstants, new jedd.PhysicalDomain[...]); at /tmp/soot-trun" +
                                               "k/src/soot/jimple/paddle/BDDVirtualCalls.jedd:143,28-38"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(ctxtLocalPt),
                                                                             stringConstants,
                                                                             new jedd.PhysicalDomain[] { H1.v() }));
        statics.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v(), tgtm.v(), srcm.v(), srcc.v(), stmt.v(), tgtc.v() },
                                                        new jedd.PhysicalDomain[] { FD.v(), T2.v(), T1.v(), V1.v(), ST.v(), V2.v() },
                                                        ("statics.add(jedd.internal.Jedd.v().replace(jedd.internal.Jed" +
                                                         "d.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v(" +
                                                         ").compose(jedd.internal.Jedd.v().read(tgtMethods), clinits, " +
                                                         "new jedd.PhysicalDomain[...])), jedd.internal.Jedd.v().liter" +
                                                         "al(new java.lang.Object[...], new jedd.Attribute[...], new j" +
                                                         "edd.PhysicalDomain[...]), new jedd.PhysicalDomain[...]), new" +
                                                         " jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...])) at" +
                                                         " /tmp/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jedd" +
                                                         ":145,8-15"),
                                                        jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(tgtMethods),
                                                                                                                                                                              clinits,
                                                                                                                                                                              new jedd.PhysicalDomain[] { V2.v() })),
                                                                                                                   jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                                                  new jedd.Attribute[] { tgtc.v() },
                                                                                                                                                  new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                   new jedd.PhysicalDomain[] {  }),
                                                                                       new jedd.PhysicalDomain[] { V3.v() },
                                                                                       new jedd.PhysicalDomain[] { V1.v() })));
    }
    
    private final jedd.internal.RelationContainer resolvedSpecials =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), local.v(), obj.v(), srcm.v(), stmt.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { V3.v(), V2.v(), H1.v(), T1.v(), ST.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.local, soot.jimple.paddle.bdddomains.obj, soo" +
                                           "t.jimple.paddle.bdddomains.srcm, soot.jimple.paddle.bdddomai" +
                                           "ns.stmt, soot.jimple.paddle.bdddomains.tgtm> resolvedSpecial" +
                                           "s = jedd.internal.Jedd.v().falseBDD() at /tmp/soot-trunk/src" +
                                           "/soot/jimple/paddle/BDDVirtualCalls.jedd:148,12-48"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private void updateSpecials() {
        final jedd.internal.RelationContainer ctxtLocalPt =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), local.v(), obj.v() },
                                              new jedd.PhysicalDomain[] { V3.v(), V2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V3, soot.jimple.paddle.bdddomains.local:soot.jimple.pa" +
                                               "ddle.bdddomains.V2, soot.jimple.paddle.bdddomains.obj:soot.j" +
                                               "imple.paddle.bdddomains.H1> ctxtLocalPt = jedd.internal.Jedd" +
                                               ".v().compose(jedd.internal.Jedd.v().read(newPt), jedd.intern" +
                                               "al.Jedd.v().project(varNodes, new jedd.PhysicalDomain[...])," +
                                               " new jedd.PhysicalDomain[...]); at /tmp/soot-trunk/src/soot/" +
                                               "jimple/paddle/BDDVirtualCalls.jedd:151,27-38"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(newPt),
                                                                             jedd.internal.Jedd.v().project(varNodes,
                                                                                                            new jedd.PhysicalDomain[] { T2.v() }),
                                                                             new jedd.PhysicalDomain[] { V1.v() }));
        final jedd.internal.RelationContainer newSpecials =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), local.v(), obj.v(), srcm.v(), stmt.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { V3.v(), V2.v(), H1.v(), T1.v(), ST.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V3, soot.jimple.paddle.bdddomains.local:soot.jimple.pa" +
                                               "ddle.bdddomains.V2, soot.jimple.paddle.bdddomains.obj:soot.j" +
                                               "imple.paddle.bdddomains.H1, soot.jimple.paddle.bdddomains.sr" +
                                               "cm:soot.jimple.paddle.bdddomains.T1, soot.jimple.paddle.bddd" +
                                               "omains.stmt:soot.jimple.paddle.bdddomains.ST, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T2> newSp" +
                                               "ecials = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v()." +
                                               "read(ctxtLocalPt), jedd.internal.Jedd.v().replace(allSpc, ne" +
                                               "w jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...]), n" +
                                               "ew jedd.PhysicalDomain[...]); at /tmp/soot-trunk/src/soot/ji" +
                                               "mple/paddle/BDDVirtualCalls.jedd:152,45-56"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(ctxtLocalPt),
                                                                          jedd.internal.Jedd.v().replace(allSpc,
                                                                                                         new jedd.PhysicalDomain[] { V1.v() },
                                                                                                         new jedd.PhysicalDomain[] { V2.v() }),
                                                                          new jedd.PhysicalDomain[] { V2.v() }));
        newSpecials.eqMinus(resolvedSpecials);
        resolvedSpecials.eqUnion(newSpecials);
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), local.v(), ctxt.v(), tgtm.v(), srcm.v(), stmt.v(), kind.v() },
                                                    new jedd.PhysicalDomain[] { H1.v(), V1.v(), V2.v(), T2.v(), T1.v(), ST.v(), FD.v() },
                                                    ("out.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                                     "ead(jedd.internal.Jedd.v().replace(newSpecials, new jedd.Phy" +
                                                     "sicalDomain[...], new jedd.PhysicalDomain[...])), jedd.inter" +
                                                     "nal.Jedd.v().literal(new java.lang.Object[...], new jedd.Att" +
                                                     "ribute[...], new jedd.PhysicalDomain[...]), new jedd.Physica" +
                                                     "lDomain[...])) at /tmp/soot-trunk/src/soot/jimple/paddle/BDD" +
                                                     "VirtualCalls.jedd:158,8-11"),
                                                    jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(newSpecials,
                                                                                                                                           new jedd.PhysicalDomain[] { V2.v(), V3.v() },
                                                                                                                                           new jedd.PhysicalDomain[] { V1.v(), V2.v() })),
                                                                                jedd.internal.Jedd.v().literal(new Object[] { Kind.SPECIAL },
                                                                                                               new jedd.Attribute[] { kind.v() },
                                                                                                               new jedd.PhysicalDomain[] { FD.v() }),
                                                                                new jedd.PhysicalDomain[] {  })));
    }
    
    private void updateVirtuals() {
        final jedd.internal.RelationContainer rcv =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { local.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.local:soot.jimple.paddle.bddd" +
                                               "omains.V2, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pa" +
                                               "ddle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot." +
                                               "jimple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.s" +
                                               "ignature:soot.jimple.paddle.bdddomains.H2, soot.jimple.paddl" +
                                               "e.bdddomains.kind:soot.jimple.paddle.bdddomains.FD> rcv = je" +
                                               "dd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jed" +
                                               "d.internal.Jedd.v().read(allRcv), virtual, new jedd.Physical" +
                                               "Domain[...]), new jedd.PhysicalDomain[...], new jedd.Physica" +
                                               "lDomain[...]); at /tmp/soot-trunk/src/soot/jimple/paddle/BDD" +
                                               "VirtualCalls.jedd:162,45-48"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(allRcv),
                                                                                                         virtual,
                                                                                                         new jedd.PhysicalDomain[] { FD.v() }),
                                                                             new jedd.PhysicalDomain[] { V1.v() },
                                                                             new jedd.PhysicalDomain[] { V2.v() }));
        final jedd.internal.RelationContainer threadRcv =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { local.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.local:soot.jimple.paddle.bddd" +
                                               "omains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pa" +
                                               "ddle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot." +
                                               "jimple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.s" +
                                               "ignature:soot.jimple.paddle.bdddomains.H2, soot.jimple.paddl" +
                                               "e.bdddomains.kind:soot.jimple.paddle.bdddomains.FD> threadRc" +
                                               "v = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(" +
                                               "allRcv), jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                               "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                               "), new jedd.PhysicalDomain[...]); at /tmp/soot-trunk/src/soo" +
                                               "t/jimple/paddle/BDDVirtualCalls.jedd:165,45-54"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(allRcv),
                                                                          jedd.internal.Jedd.v().literal(new Object[] { Kind.THREAD },
                                                                                                         new jedd.Attribute[] { kind.v() },
                                                                                                         new jedd.PhysicalDomain[] { FD.v() }),
                                                                          new jedd.PhysicalDomain[] { FD.v() }));
        final jedd.internal.RelationContainer ptTypes =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { local.v(), type.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.local:soot.jimple.paddle.bddd" +
                                               "omains.V2, soot.jimple.paddle.bdddomains.type:soot.jimple.pa" +
                                               "ddle.bdddomains.T2> ptTypes = jedd.internal.Jedd.v().compose" +
                                               "(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().compose(" +
                                               "jedd.internal.Jedd.v().read(allocNodes), newPt, new jedd.Phy" +
                                               "sicalDomain[...])), jedd.internal.Jedd.v().project(varNodes," +
                                               " new jedd.PhysicalDomain[...]), new jedd.PhysicalDomain[...]" +
                                               "); at /tmp/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls" +
                                               ".jedd:169,22-29"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(allocNodes),
                                                                                                                                        newPt,
                                                                                                                                        new jedd.PhysicalDomain[] { H1.v() })),
                                                                             jedd.internal.Jedd.v().project(varNodes,
                                                                                                            new jedd.PhysicalDomain[] { V3.v(), T2.v() }),
                                                                             new jedd.PhysicalDomain[] { V1.v() }));
        final jedd.internal.RelationContainer newTypes =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v(), signature.v() },
                                              new jedd.PhysicalDomain[] { T2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.type:soot.jimple.paddle.bdddo" +
                                               "mains.T2, soot.jimple.paddle.bdddomains.signature:soot.jimpl" +
                                               "e.paddle.bdddomains.H2> newTypes = jedd.internal.Jedd.v().co" +
                                               "mpose(jedd.internal.Jedd.v().read(ptTypes), jedd.internal.Je" +
                                               "dd.v().project(rcv, new jedd.PhysicalDomain[...]), new jedd." +
                                               "PhysicalDomain[...]); at /tmp/soot-trunk/src/soot/jimple/pad" +
                                               "dle/BDDVirtualCalls.jedd:172,26-34"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(ptTypes),
                                                                             jedd.internal.Jedd.v().project(rcv,
                                                                                                            new jedd.PhysicalDomain[] { FD.v(), T1.v(), ST.v() }),
                                                                             new jedd.PhysicalDomain[] { V2.v() }));
        newTypes.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ptTypes,
                                                                                                                                                                                                          new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                                                                                                          new jedd.PhysicalDomain[] { T1.v() })),
                                                                                                                                               threads,
                                                                                                                                               new jedd.PhysicalDomain[] { T1.v() })),
                                                                                       jedd.internal.Jedd.v().project(jedd.internal.Jedd.v().replace(threadRcv,
                                                                                                                                                     new jedd.PhysicalDomain[] { V1.v() },
                                                                                                                                                     new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                      new jedd.PhysicalDomain[] { FD.v(), T1.v(), ST.v() }),
                                                                                       new jedd.PhysicalDomain[] { V2.v() }),
                                                        new jedd.PhysicalDomain[] { T1.v() },
                                                        new jedd.PhysicalDomain[] { T2.v() }));
        hier.update();
        newTypes.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(newTypes,
                                                                                                                   new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                   new jedd.PhysicalDomain[] { T1.v() })),
                                                        hier.anySub(),
                                                        new jedd.PhysicalDomain[] { T1.v() }));
        newTypes.eqMinus(jedd.internal.Jedd.v().project(targets, new jedd.PhysicalDomain[] { T3.v() }));
        final jedd.internal.RelationContainer toResolve =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), signature.v(), supt.v() },
                                              new jedd.PhysicalDomain[] { T2.v(), H2.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.subt:soot.jimple.paddle.bdddo" +
                                               "mains.T2, soot.jimple.paddle.bdddomains.signature:soot.jimpl" +
                                               "e.paddle.bdddomains.H2, soot.jimple.paddle.bdddomains.supt:s" +
                                               "oot.jimple.paddle.bdddomains.T1> toResolve = jedd.internal.J" +
                                               "edd.v().copy(newTypes, new jedd.PhysicalDomain[...], new jed" +
                                               "d.PhysicalDomain[...]); at /tmp/soot-trunk/src/soot/jimple/p" +
                                               "addle/BDDVirtualCalls.jedd:187,32-41"),
                                              jedd.internal.Jedd.v().copy(newTypes,
                                                                          new jedd.PhysicalDomain[] { T2.v() },
                                                                          new jedd.PhysicalDomain[] { T1.v() }));
        do  {
            final jedd.internal.RelationContainer resolved =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), signature.v(), supt.v(), method.v() },
                                                  new jedd.PhysicalDomain[] { T2.v(), H2.v(), T1.v(), T3.v() },
                                                  ("<soot.jimple.paddle.bdddomains.subt:soot.jimple.paddle.bdddo" +
                                                   "mains.T2, soot.jimple.paddle.bdddomains.signature:soot.jimpl" +
                                                   "e.paddle.bdddomains.H2, soot.jimple.paddle.bdddomains.supt:s" +
                                                   "oot.jimple.paddle.bdddomains.T1, soot.jimple.paddle.bdddomai" +
                                                   "ns.method:soot.jimple.paddle.bdddomains.T3> resolved = jedd." +
                                                   "internal.Jedd.v().join(jedd.internal.Jedd.v().read(toResolve" +
                                                   "), declaresMethod, new jedd.PhysicalDomain[...]); at /tmp/so" +
                                                   "ot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:192,44-" +
                                                   "52"),
                                                  jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(toResolve),
                                                                              declaresMethod,
                                                                              new jedd.PhysicalDomain[] { T1.v(), H2.v() }));
            toResolve.eqMinus(jedd.internal.Jedd.v().project(resolved, new jedd.PhysicalDomain[] { T3.v() }));
            targets.eqUnion(jedd.internal.Jedd.v().project(resolved, new jedd.PhysicalDomain[] { T1.v() }));
            toResolve.eq(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(toResolve),
                                                                                       jedd.internal.Jedd.v().replace(hier.extend(),
                                                                                                                      new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                      new jedd.PhysicalDomain[] { T3.v() }),
                                                                                       new jedd.PhysicalDomain[] { T1.v() }),
                                                        new jedd.PhysicalDomain[] { T3.v() },
                                                        new jedd.PhysicalDomain[] { T1.v() }));
        }while(!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(toResolve),
                                              jedd.internal.Jedd.v().falseBDD())); 
        final jedd.internal.RelationContainer typedPt =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v(), type.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), H1.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                               "e.bdddomains.H1, soot.jimple.paddle.bdddomains.type:soot.jim" +
                                               "ple.paddle.bdddomains.T1> typedPt = jedd.internal.Jedd.v().j" +
                                               "oin(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().repla" +
                                               "ce(allocNodes, new jedd.PhysicalDomain[...], new jedd.Physic" +
                                               "alDomain[...])), newPt, new jedd.PhysicalDomain[...]); at /t" +
                                               "mp/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:20" +
                                               "5,25-32"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(allocNodes,
                                                                                                                                     new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                                     new jedd.PhysicalDomain[] { T1.v() })),
                                                                          newPt,
                                                                          new jedd.PhysicalDomain[] { H1.v() }));
        typedPt.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(typedPt),
                                                                                      hier.anySub(),
                                                                                      new jedd.PhysicalDomain[] { T1.v() }),
                                                       new jedd.PhysicalDomain[] { T2.v() },
                                                       new jedd.PhysicalDomain[] { T1.v() }));
        final jedd.internal.RelationContainer localCtxtPt =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), local.v(), supt.v(), obj.v(), type.v() },
                                              new jedd.PhysicalDomain[] { V3.v(), V2.v(), T2.v(), H1.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V3, soot.jimple.paddle.bdddomains.local:soot.jimple.pa" +
                                               "ddle.bdddomains.V2, soot.jimple.paddle.bdddomains.supt:soot." +
                                               "jimple.paddle.bdddomains.T2, soot.jimple.paddle.bdddomains.o" +
                                               "bj:soot.jimple.paddle.bdddomains.H1, soot.jimple.paddle.bddd" +
                                               "omains.type:soot.jimple.paddle.bdddomains.T1> localCtxtPt = " +
                                               "jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(t" +
                                               "ypedPt), varNodes, new jedd.PhysicalDomain[...]); at /tmp/so" +
                                               "ot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:211,39-" +
                                               "50"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(typedPt),
                                                                             varNodes,
                                                                             new jedd.PhysicalDomain[] { V1.v() }));
        localCtxtPt.eq(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(localCtxtPt),
                                                   hier.subtypeRelation(),
                                                   new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
        final jedd.internal.RelationContainer callSiteTargets =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { local.v(), srcm.v(), stmt.v(), type.v(), kind.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v(), FD.v(), T3.v() },
                                              ("<soot.jimple.paddle.bdddomains.local:soot.jimple.paddle.bddd" +
                                               "omains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pa" +
                                               "ddle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot." +
                                               "jimple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.t" +
                                               "ype:soot.jimple.paddle.bdddomains.T2, soot.jimple.paddle.bdd" +
                                               "domains.kind:soot.jimple.paddle.bdddomains.FD, soot.jimple.p" +
                                               "addle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T3> call" +
                                               "SiteTargets = jedd.internal.Jedd.v().compose(jedd.internal.J" +
                                               "edd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd" +
                                               ".v().read(jedd.internal.Jedd.v().replace(rcv, new jedd.Physi" +
                                               "calDomain[...], new jedd.PhysicalDomain[...])), threadRcv))," +
                                               " targets, new jedd.PhysicalDomain[...]); at /tmp/soot-trunk/" +
                                               "src/soot/jimple/paddle/BDDVirtualCalls.jedd:219,46-61"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(rcv,
                                                                                                                                                                                                 new jedd.PhysicalDomain[] { V2.v() },
                                                                                                                                                                                                 new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                                                      threadRcv)),
                                                                             targets,
                                                                             new jedd.PhysicalDomain[] { H2.v() }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), local.v(), ctxt.v(), kind.v(), tgtm.v(), srcm.v(), stmt.v() },
                                                    new jedd.PhysicalDomain[] { H1.v(), V1.v(), V2.v(), FD.v(), T2.v(), T1.v(), ST.v() },
                                                    ("out.add(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v(" +
                                                     ").project(jedd.internal.Jedd.v().replace(jedd.internal.Jedd." +
                                                     "v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v()." +
                                                     "replace(jedd.internal.Jedd.v().project(localCtxtPt, new jedd" +
                                                     ".PhysicalDomain[...]), new jedd.PhysicalDomain[...], new jed" +
                                                     "d.PhysicalDomain[...])), callSiteTargets, new jedd.PhysicalD" +
                                                     "omain[...]), new jedd.PhysicalDomain[...], new jedd.Physical" +
                                                     "Domain[...]), new jedd.PhysicalDomain[...]), new jedd.Physic" +
                                                     "alDomain[...], new jedd.PhysicalDomain[...])) at /tmp/soot-t" +
                                                     "runk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:222,8-11"),
                                                    jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(localCtxtPt,
                                                                                                                                                                                                                                                                       new jedd.PhysicalDomain[] { T2.v() }),
                                                                                                                                                                                                                                        new jedd.PhysicalDomain[] { V2.v(), T1.v() },
                                                                                                                                                                                                                                        new jedd.PhysicalDomain[] { V1.v(), T2.v() })),
                                                                                                                                                                             callSiteTargets,
                                                                                                                                                                             new jedd.PhysicalDomain[] { T2.v(), V1.v() }),
                                                                                                                                                 new jedd.PhysicalDomain[] { V3.v() },
                                                                                                                                                 new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                  new jedd.PhysicalDomain[] { T2.v() }),
                                                                                   new jedd.PhysicalDomain[] { T3.v() },
                                                                                   new jedd.PhysicalDomain[] { T2.v() })));
    }
}
