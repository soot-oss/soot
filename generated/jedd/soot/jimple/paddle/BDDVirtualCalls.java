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
                                                                      new jedd.PhysicalDomain[] { T1.v(), H2.v(), T3.v() }));
            }
        }
    }
    
    private int lastVarNode = 1;
    
    private int lastAllocNode = 1;
    
    private final jedd.internal.RelationContainer varNodes =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), type.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                           "e.bdddomains.V1, soot.jimple.paddle.bdddomains.type> varNode" +
                                           "s at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDVirt" +
                                           "ualCalls.jedd:54,12-26"));
    
    private final jedd.internal.RelationContainer allocNodes =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), type.v() },
                                          new jedd.PhysicalDomain[] { H1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj, soot.jimple.padd" +
                                           "le.bdddomains.type> allocNodes at /home/olhotak/soot-trunk/s" +
                                           "rc/soot/jimple/paddle/BDDVirtualCalls.jedd:55,12-23"));
    
    private final jedd.internal.RelationContainer virtual =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v() },
                                          new jedd.PhysicalDomain[] { FD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.kind> virtual = jedd." +
                                           "internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.int" +
                                           "ernal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.intern" +
                                           "al.Jedd.v().literal(new java.lang.Object[...], new jedd.Attr" +
                                           "ibute[...], new jedd.PhysicalDomain[...])), jedd.internal.Je" +
                                           "dd.v().literal(new java.lang.Object[...], new jedd.Attribute" +
                                           "[...], new jedd.PhysicalDomain[...]))), jedd.internal.Jedd.v" +
                                           "().literal(new java.lang.Object[...], new jedd.Attribute[..." +
                                           "], new jedd.PhysicalDomain[...])) at /home/olhotak/soot-trun" +
                                           "k/src/soot/jimple/paddle/BDDVirtualCalls.jedd:56,12-18"),
                                          jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { Kind.VIRTUAL },
                                                                                                                                                                                           new jedd.Attribute[] { kind.v() },
                                                                                                                                                                                           new jedd.PhysicalDomain[] { FD.v() })),
                                                                                                                                jedd.internal.Jedd.v().literal(new Object[] { Kind.INTERFACE },
                                                                                                                                                               new jedd.Attribute[] { kind.v() },
                                                                                                                                                               new jedd.PhysicalDomain[] { FD.v() }))),
                                                                       jedd.internal.Jedd.v().literal(new Object[] { Kind.PRIVILEGED },
                                                                                                      new jedd.Attribute[] { kind.v() },
                                                                                                      new jedd.PhysicalDomain[] { FD.v() })));
    
    private final jedd.internal.RelationContainer threads =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v() },
                                          new jedd.PhysicalDomain[] { T1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.type> threads = jedd." +
                                           "internal.Jedd.v().falseBDD() at /home/olhotak/soot-trunk/src" +
                                           "/soot/jimple/paddle/BDDVirtualCalls.jedd:57,12-18"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private void updateNodes() {
        for (; lastVarNode <= PaddleNumberers.v().varNodeNumberer().size(); lastVarNode++) {
            VarNode vn = (VarNode) PaddleNumberers.v().varNodeNumberer().get(lastVarNode);
            varNodes.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { vn, vn.getType() },
                                                            new jedd.Attribute[] { var.v(), type.v() },
                                                            new jedd.PhysicalDomain[] { V1.v(), T2.v() }));
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
                                           ".falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                           "le/BDDVirtualCalls.jedd:96,12-23"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer nonStringConstants =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v() },
                                          new jedd.PhysicalDomain[] { H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                           "e.bdddomains.H1> nonStringConstants = jedd.internal.Jedd.v()" +
                                           ".falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                           "le/BDDVirtualCalls.jedd:97,12-20"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final NumberedString sigClinit = Scene.v().getSubSigNumberer().findOrAdd("void <clinit>()");
    
    private final jedd.internal.RelationContainer targets =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v(), signature.v(), method.v() },
                                          new jedd.PhysicalDomain[] { T2.v(), H2.v(), T3.v() },
                                          ("private <soot.jimple.paddle.bdddomains.type, soot.jimple.pad" +
                                           "dle.bdddomains.signature, soot.jimple.paddle.bdddomains.meth" +
                                           "od> targets = jedd.internal.Jedd.v().falseBDD() at /home/olh" +
                                           "otak/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:" +
                                           "101,12-37"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer declaresMethod =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v(), signature.v(), method.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), H2.v(), T3.v() },
                                          ("private <soot.jimple.paddle.bdddomains.type, soot.jimple.pad" +
                                           "dle.bdddomains.signature, soot.jimple.paddle.bdddomains.meth" +
                                           "od:soot.jimple.paddle.bdddomains.T3> declaresMethod = jedd.i" +
                                           "nternal.Jedd.v().falseBDD() at /home/olhotak/soot-trunk/src/" +
                                           "soot/jimple/paddle/BDDVirtualCalls.jedd:102,12-40"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private BDDHierarchy hier = new BDDHierarchy();
    
    private final jedd.internal.RelationContainer newPt =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v() },
                                          new jedd.PhysicalDomain[] { C1.v(), V1.v(), H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.var, soot.jimple.paddle.bdddomains.obj> newPt" +
                                           " = jedd.internal.Jedd.v().falseBDD() at /home/olhotak/soot-t" +
                                           "runk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:105,12-28"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allPt =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v() },
                                          new jedd.PhysicalDomain[] { C1.v(), V1.v(), H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.var, soot.jimple.paddle.bdddomains.obj> allPt" +
                                           " = jedd.internal.Jedd.v().falseBDD() at /home/olhotak/soot-t" +
                                           "runk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:106,12-28"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer newRcv =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.signature, soot.jimple.paddle.bddd" +
                                           "omains.kind> newRcv = jedd.internal.Jedd.v().falseBDD() at /" +
                                           "home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDVirtualCal" +
                                           "ls.jedd:107,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allRcv =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.signature, soot.jimple.paddle.bddd" +
                                           "omains.kind> allRcv = jedd.internal.Jedd.v().falseBDD() at /" +
                                           "home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDVirtualCal" +
                                           "ls.jedd:108,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer newSpc =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.tgtm> newSpc = jedd.internal.Jedd." +
                                           "v().falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                           "addle/BDDVirtualCalls.jedd:109,12-35"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allSpc =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.tgtm> allSpc = jedd.internal.Jedd." +
                                           "v().falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                           "addle/BDDVirtualCalls.jedd:110,12-35"),
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
    
    private void updateClinits() {
        final jedd.internal.RelationContainer clinits =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), kind.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                               "le.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                               "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.kin" +
                                               "d:soot.jimple.paddle.bdddomains.FD> clinits = jedd.internal." +
                                               "Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd" +
                                               ".v().project(allRcv, new jedd.PhysicalDomain[...])), jedd.in" +
                                               "ternal.Jedd.v().literal(new java.lang.Object[...], new jedd." +
                                               "Attribute[...], new jedd.PhysicalDomain[...]), new jedd.Phys" +
                                               "icalDomain[...]); at /home/olhotak/soot-trunk/src/soot/jimpl" +
                                               "e/paddle/BDDVirtualCalls.jedd:137,32-39"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(allRcv,
                                                                                                                                     new jedd.PhysicalDomain[] { H2.v() })),
                                                                          jedd.internal.Jedd.v().literal(new Object[] { Kind.CLINIT },
                                                                                                         new jedd.Attribute[] { kind.v() },
                                                                                                         new jedd.PhysicalDomain[] { FD.v() }),
                                                                          new jedd.PhysicalDomain[] { FD.v() }));
        final jedd.internal.RelationContainer tgtMethods =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), var.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.tgtm:soot.ji" +
                                               "mple.paddle.bdddomains.T2> tgtMethods = jedd.internal.Jedd.v" +
                                               "().compose(jedd.internal.Jedd.v().read(newPt), stringConstan" +
                                               "ts, new jedd.PhysicalDomain[...]); at /home/olhotak/soot-tru" +
                                               "nk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:140,26-36"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(newPt),
                                                                             stringConstants,
                                                                             new jedd.PhysicalDomain[] { H1.v() }));
        final jedd.internal.RelationContainer newStatics =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.FD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.V2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T2> newSt" +
                                               "atics = jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v(" +
                                               ").join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().co" +
                                               "mpose(jedd.internal.Jedd.v().read(tgtMethods), clinits, new " +
                                               "jedd.PhysicalDomain[...])), jedd.internal.Jedd.v().literal(n" +
                                               "ew java.lang.Object[...], new jedd.Attribute[...], new jedd." +
                                               "PhysicalDomain[...]), new jedd.PhysicalDomain[...]), new jed" +
                                               "d.PhysicalDomain[...], new jedd.PhysicalDomain[...]); at /ho" +
                                               "me/olhotak/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls" +
                                               ".jedd:143,45-55"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(tgtMethods),
                                                                                                                                                                    clinits,
                                                                                                                                                                    new jedd.PhysicalDomain[] { V1.v() })),
                                                                                                         jedd.internal.Jedd.v().literal(new Object[] { null },
                                                                                                                                        new jedd.Attribute[] { tgtc.v() },
                                                                                                                                        new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                         new jedd.PhysicalDomain[] {  }),
                                                                             new jedd.PhysicalDomain[] { C1.v() },
                                                                             new jedd.PhysicalDomain[] { V1.v() }));
        statics.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), kind.v(), stmt.v(), srcc.v(), tgtm.v(), tgtc.v() },
                                                        new jedd.PhysicalDomain[] { T1.v(), FD.v(), ST.v(), V1.v(), T2.v(), V2.v() },
                                                        ("statics.add(newStatics) at /home/olhotak/soot-trunk/src/soot" +
                                                         "/jimple/paddle/BDDVirtualCalls.jedd:146,8-15"),
                                                        newStatics));
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newStatics), jedd.internal.Jedd.v().falseBDD()))
            change = true;
    }
    
    private final jedd.internal.RelationContainer resolvedSpecials =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { C1.v(), V1.v(), H1.v(), T1.v(), ST.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.var, soot.jimple.paddle.bdddomains.obj, soot." +
                                           "jimple.paddle.bdddomains.srcm, soot.jimple.paddle.bdddomains" +
                                           ".stmt, soot.jimple.paddle.bdddomains.tgtm> resolvedSpecials " +
                                           "= jedd.internal.Jedd.v().falseBDD() at /home/olhotak/soot-tr" +
                                           "unk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:149,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private void updateSpecials() {
        final jedd.internal.RelationContainer newSpecials =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), H1.v(), T1.v(), ST.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.obj:soot.jim" +
                                               "ple.paddle.bdddomains.H1, soot.jimple.paddle.bdddomains.srcm" +
                                               ":soot.jimple.paddle.bdddomains.T1, soot.jimple.paddle.bdddom" +
                                               "ains.stmt:soot.jimple.paddle.bdddomains.ST, soot.jimple.padd" +
                                               "le.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T2> newSpec" +
                                               "ials = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().re" +
                                               "ad(newPt), allSpc, new jedd.PhysicalDomain[...]); at /home/o" +
                                               "lhotak/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jed" +
                                               "d:152,43-54"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newPt),
                                                                          allSpc,
                                                                          new jedd.PhysicalDomain[] { V1.v() }));
        newSpecials.eqMinus(resolvedSpecials);
        resolvedSpecials.eqUnion(newSpecials);
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), ctxt.v(), tgtm.v(), obj.v(), kind.v() },
                                                    new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), V2.v(), T2.v(), H1.v(), FD.v() },
                                                    ("out.add(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().r" +
                                                     "ead(jedd.internal.Jedd.v().replace(newSpecials, new jedd.Phy" +
                                                     "sicalDomain[...], new jedd.PhysicalDomain[...])), jedd.inter" +
                                                     "nal.Jedd.v().literal(new java.lang.Object[...], new jedd.Att" +
                                                     "ribute[...], new jedd.PhysicalDomain[...]), new jedd.Physica" +
                                                     "lDomain[...])) at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                                     "addle/BDDVirtualCalls.jedd:158,8-11"),
                                                    jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(newSpecials,
                                                                                                                                           new jedd.PhysicalDomain[] { C1.v() },
                                                                                                                                           new jedd.PhysicalDomain[] { V2.v() })),
                                                                                jedd.internal.Jedd.v().literal(new Object[] { Kind.SPECIAL },
                                                                                                               new jedd.Attribute[] { kind.v() },
                                                                                                               new jedd.PhysicalDomain[] { FD.v() }),
                                                                                new jedd.PhysicalDomain[] {  })));
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newSpecials), jedd.internal.Jedd.v().falseBDD()))
            change = true;
    }
    
    private final jedd.internal.RelationContainer allVirtRcv =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.signature, soot.jimple.paddle.bddd" +
                                           "omains.kind> allVirtRcv = jedd.internal.Jedd.v().falseBDD() " +
                                           "at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDVirtua" +
                                           "lCalls.jedd:162,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allThreadRcv =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, soot" +
                                           ".jimple.paddle.bdddomains.signature, soot.jimple.paddle.bddd" +
                                           "omains.kind> allThreadRcv = jedd.internal.Jedd.v().falseBDD(" +
                                           ") at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDVirt" +
                                           "ualCalls.jedd:163,12-46"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private void updateVirtuals() {
        final jedd.internal.RelationContainer newVirtRcv =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                               "le.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                               "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.sig" +
                                               "nature:soot.jimple.paddle.bdddomains.H2, soot.jimple.paddle." +
                                               "bdddomains.kind:soot.jimple.paddle.bdddomains.FD> newVirtRcv" +
                                               " = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(n" +
                                               "ewRcv), virtual, new jedd.PhysicalDomain[...]); at /home/olh" +
                                               "otak/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:" +
                                               "166,43-53"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newRcv),
                                                                          virtual,
                                                                          new jedd.PhysicalDomain[] { FD.v() }));
        allVirtRcv.eqUnion(newVirtRcv);
        final jedd.internal.RelationContainer newThreadRcv =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                               "le.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                               "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.sig" +
                                               "nature:soot.jimple.paddle.bdddomains.H2, soot.jimple.paddle." +
                                               "bdddomains.kind:soot.jimple.paddle.bdddomains.FD> newThreadR" +
                                               "cv = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read" +
                                               "(newRcv), jedd.internal.Jedd.v().literal(new java.lang.Objec" +
                                               "t[...], new jedd.Attribute[...], new jedd.PhysicalDomain[..." +
                                               "]), new jedd.PhysicalDomain[...]); at /home/olhotak/soot-tru" +
                                               "nk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:170,43-55"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(newRcv),
                                                                          jedd.internal.Jedd.v().literal(new Object[] { Kind.THREAD },
                                                                                                         new jedd.Attribute[] { kind.v() },
                                                                                                         new jedd.PhysicalDomain[] { FD.v() }),
                                                                          new jedd.PhysicalDomain[] { FD.v() }));
        allThreadRcv.eqUnion(newThreadRcv);
        final jedd.internal.RelationContainer callSiteTargets =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), stmt.v(), type.v(), kind.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v(), FD.v(), T3.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                               "le.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                               "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.typ" +
                                               "e:soot.jimple.paddle.bdddomains.T2, soot.jimple.paddle.bdddo" +
                                               "mains.kind:soot.jimple.paddle.bdddomains.FD, soot.jimple.pad" +
                                               "dle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T3> callSi" +
                                               "teTargets = jedd.internal.Jedd.v().compose(jedd.internal.Jed" +
                                               "d.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v" +
                                               "().read(newVirtRcv), newThreadRcv)), targets, new jedd.Physi" +
                                               "calDomain[...]); at /home/olhotak/soot-trunk/src/soot/jimple" +
                                               "/paddle/BDDVirtualCalls.jedd:174,44-59"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(newVirtRcv),
                                                                                                                                      newThreadRcv)),
                                                                             targets,
                                                                             new jedd.PhysicalDomain[] { H2.v() }));
        final jedd.internal.RelationContainer newTargets =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v(), signature.v(), method.v() },
                                              new jedd.PhysicalDomain[] { T2.v(), H2.v(), T3.v() },
                                              ("<soot.jimple.paddle.bdddomains.type:soot.jimple.paddle.bdddo" +
                                               "mains.T2, soot.jimple.paddle.bdddomains.signature:soot.jimpl" +
                                               "e.paddle.bdddomains.H2, soot.jimple.paddle.bdddomains.method" +
                                               ":soot.jimple.paddle.bdddomains.T3> newTargets = jedd.interna" +
                                               "l.Jedd.v().falseBDD(); at /home/olhotak/soot-trunk/src/soot/" +
                                               "jimple/paddle/BDDVirtualCalls.jedd:178,34-44"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer ptTypes =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), type.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.type:soot.jimple.padd" +
                                               "le.bdddomains.T2> ptTypes = jedd.internal.Jedd.v().compose(j" +
                                               "edd.internal.Jedd.v().read(allocNodes), jedd.internal.Jedd.v" +
                                               "().project(newPt, new jedd.PhysicalDomain[...]), new jedd.Ph" +
                                               "ysicalDomain[...]); at /home/olhotak/soot-trunk/src/soot/jim" +
                                               "ple/paddle/BDDVirtualCalls.jedd:181,20-27"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(allocNodes),
                                                                             jedd.internal.Jedd.v().project(newPt,
                                                                                                            new jedd.PhysicalDomain[] { C1.v() }),
                                                                             new jedd.PhysicalDomain[] { H1.v() }));
        final jedd.internal.RelationContainer newTypes =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v(), signature.v() },
                                              new jedd.PhysicalDomain[] { T2.v(), H2.v() },
                                              ("<soot.jimple.paddle.bdddomains.type:soot.jimple.paddle.bdddo" +
                                               "mains.T2, soot.jimple.paddle.bdddomains.signature:soot.jimpl" +
                                               "e.paddle.bdddomains.H2> newTypes = jedd.internal.Jedd.v().co" +
                                               "mpose(jedd.internal.Jedd.v().read(ptTypes), jedd.internal.Je" +
                                               "dd.v().project(newVirtRcv, new jedd.PhysicalDomain[...]), ne" +
                                               "w jedd.PhysicalDomain[...]); at /home/olhotak/soot-trunk/src" +
                                               "/soot/jimple/paddle/BDDVirtualCalls.jedd:183,26-34"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(ptTypes),
                                                                             jedd.internal.Jedd.v().project(newVirtRcv,
                                                                                                            new jedd.PhysicalDomain[] { T1.v(), ST.v(), FD.v() }),
                                                                             new jedd.PhysicalDomain[] { V1.v() }));
        newTypes.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(ptTypes,
                                                                                                                                                                                                          new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                                                                                                          new jedd.PhysicalDomain[] { T1.v() })),
                                                                                                                                               threads,
                                                                                                                                               new jedd.PhysicalDomain[] { T1.v() }),
                                                                                                                   new jedd.PhysicalDomain[] { T1.v() },
                                                                                                                   new jedd.PhysicalDomain[] { T2.v() })),
                                                        jedd.internal.Jedd.v().project(newThreadRcv,
                                                                                       new jedd.PhysicalDomain[] { T1.v(), ST.v(), FD.v() }),
                                                        new jedd.PhysicalDomain[] { V1.v() }));
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
                                               "d.PhysicalDomain[...]); at /home/olhotak/soot-trunk/src/soot" +
                                               "/jimple/paddle/BDDVirtualCalls.jedd:198,32-41"),
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
                                                   "), declaresMethod, new jedd.PhysicalDomain[...]); at /home/o" +
                                                   "lhotak/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jed" +
                                                   "d:203,44-52"),
                                                  jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(toResolve),
                                                                              declaresMethod,
                                                                              new jedd.PhysicalDomain[] { T1.v(), H2.v() }));
            toResolve.eqMinus(jedd.internal.Jedd.v().project(resolved, new jedd.PhysicalDomain[] { T3.v() }));
            newTargets.eqUnion(jedd.internal.Jedd.v().project(resolved, new jedd.PhysicalDomain[] { T1.v() }));
            toResolve.eq(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(toResolve),
                                                                                       jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(hier.extend()),
                                                                                                                                                   hier.array()),
                                                                                                                      new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                      new jedd.PhysicalDomain[] { T3.v() }),
                                                                                       new jedd.PhysicalDomain[] { T1.v() }),
                                                        new jedd.PhysicalDomain[] { T3.v() },
                                                        new jedd.PhysicalDomain[] { T1.v() }));
        }while(!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(toResolve),
                                              jedd.internal.Jedd.v().falseBDD())); 
        newTargets.eqMinus(targets);
        targets.eqUnion(newTargets);
        final jedd.internal.RelationContainer typedPt =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), obj.v(), type.v(), ctxt.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), H1.v(), T1.v(), C1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                               "e.bdddomains.H1, soot.jimple.paddle.bdddomains.type:soot.jim" +
                                               "ple.paddle.bdddomains.T1, soot.jimple.paddle.bdddomains.ctxt" +
                                               ":soot.jimple.paddle.bdddomains.C1> typedPt = jedd.internal.J" +
                                               "edd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Je" +
                                               "dd.v().read(allocNodes), newPt, new jedd.PhysicalDomain[...]" +
                                               "), new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[..." +
                                               "]); at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDVi" +
                                               "rtualCalls.jedd:220,31-38"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(allocNodes),
                                                                                                         newPt,
                                                                                                         new jedd.PhysicalDomain[] { H1.v() }),
                                                                             new jedd.PhysicalDomain[] { T2.v() },
                                                                             new jedd.PhysicalDomain[] { T1.v() }));
        typedPt.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(typedPt),
                                                                                      hier.anySub(),
                                                                                      new jedd.PhysicalDomain[] { T1.v() }),
                                                       new jedd.PhysicalDomain[] { T2.v() },
                                                       new jedd.PhysicalDomain[] { T1.v() }));
        final jedd.internal.RelationContainer varCtxtPt =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), supt.v(), obj.v(), type.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), V1.v(), T2.v(), H1.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.supt:soot.ji" +
                                               "mple.paddle.bdddomains.T2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1, soot.jimple.paddle.bdddom" +
                                               "ains.type:soot.jimple.paddle.bdddomains.T1> varCtxtPt = jedd" +
                                               ".internal.Jedd.v().join(jedd.internal.Jedd.v().read(typedPt)" +
                                               ", varNodes, new jedd.PhysicalDomain[...]); at /home/olhotak/" +
                                               "soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls.jedd:226,3" +
                                               "7-46"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(typedPt),
                                                                          varNodes,
                                                                          new jedd.PhysicalDomain[] { V1.v() }));
        varCtxtPt.eq(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(varCtxtPt),
                                                 hier.subtypeRelation(),
                                                 new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
        callSiteTargets.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(allVirtRcv),
                                                                                                                        allThreadRcv)),
                                                               newTargets,
                                                               new jedd.PhysicalDomain[] { H2.v() }));
        final jedd.internal.RelationContainer newVirtuals =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), var.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { V2.v(), V1.v(), H1.v(), T1.v(), ST.v(), FD.v(), T3.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.V2, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.obj:soot.jim" +
                                               "ple.paddle.bdddomains.H1, soot.jimple.paddle.bdddomains.srcm" +
                                               ":soot.jimple.paddle.bdddomains.T1, soot.jimple.paddle.bdddom" +
                                               "ains.stmt:soot.jimple.paddle.bdddomains.ST, soot.jimple.padd" +
                                               "le.bdddomains.kind:soot.jimple.paddle.bdddomains.FD, soot.ji" +
                                               "mple.paddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T3" +
                                               "> newVirtuals = jedd.internal.Jedd.v().replace(jedd.internal" +
                                               ".Jedd.v().project(jedd.internal.Jedd.v().join(jedd.internal." +
                                               "Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.J" +
                                               "edd.v().project(varCtxtPt, new jedd.PhysicalDomain[...]), ne" +
                                               "w jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...])), " +
                                               "callSiteTargets, new jedd.PhysicalDomain[...]), new jedd.Phy" +
                                               "sicalDomain[...]), new jedd.PhysicalDomain[...], new jedd.Ph" +
                                               "ysicalDomain[...]); at /home/olhotak/soot-trunk/src/soot/jim" +
                                               "ple/paddle/BDDVirtualCalls.jedd:237,49-60"),
                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(varCtxtPt,
                                                                                                                                                                                                                                  new jedd.PhysicalDomain[] { T2.v() }),
                                                                                                                                                                                                   new jedd.PhysicalDomain[] { T1.v() },
                                                                                                                                                                                                   new jedd.PhysicalDomain[] { T2.v() })),
                                                                                                                                        callSiteTargets,
                                                                                                                                        new jedd.PhysicalDomain[] { T2.v(), V1.v() }),
                                                                                                            new jedd.PhysicalDomain[] { T2.v() }),
                                                                             new jedd.PhysicalDomain[] { C1.v() },
                                                                             new jedd.PhysicalDomain[] { V2.v() }));
        out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), srcm.v(), kind.v(), stmt.v(), ctxt.v(), tgtm.v(), obj.v() },
                                                    new jedd.PhysicalDomain[] { V1.v(), T1.v(), FD.v(), ST.v(), V2.v(), T2.v(), H1.v() },
                                                    ("out.add(jedd.internal.Jedd.v().replace(newVirtuals, new jedd" +
                                                     ".PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /hom" +
                                                     "e/olhotak/soot-trunk/src/soot/jimple/paddle/BDDVirtualCalls." +
                                                     "jedd:241,8-11"),
                                                    jedd.internal.Jedd.v().replace(newVirtuals,
                                                                                   new jedd.PhysicalDomain[] { T3.v() },
                                                                                   new jedd.PhysicalDomain[] { T2.v() })));
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newVirtuals), jedd.internal.Jedd.v().falseBDD()))
            change = true;
    }
}
