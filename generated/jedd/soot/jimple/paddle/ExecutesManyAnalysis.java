package soot.jimple.paddle;

import soot.jimple.paddle.bdddomains.*;
import soot.*;
import java.util.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;

public class ExecutesManyAnalysis extends SceneTransformer {
    public ExecutesManyAnalysis(Singletons.Global g) { super(); }
    
    public static ExecutesManyAnalysis v() { return G.v().soot_jimple_paddle_ExecutesManyAnalysis(); }
    
    public class IntraProc extends ForwardFlowAnalysis {
        public IntraProc(UnitGraph g) {
            super(g);
            doAnalysis();
        }
        
        protected void copy(Object inO, Object outO) {
            FlowSet in = (FlowSet) inO;
            FlowSet out = (FlowSet) outO;
            in.copy(out);
        }
        
        protected void merge(Object inoutO, Object inO) {
            FlowSet inout = (FlowSet) inoutO;
            FlowSet in = (FlowSet) inO;
            inout.union(in);
        }
        
        protected void merge(Object in1O, Object in2O, Object outO) {
            FlowSet in1 = (FlowSet) in1O;
            FlowSet in2 = (FlowSet) in2O;
            FlowSet out = (FlowSet) outO;
            in1.union(in2, out);
        }
        
        protected void flowThrough(Object outValue, Object unit, Object inValue) {
            Unit u = (Unit) unit;
            FlowSet in = (FlowSet) inValue;
            FlowSet out = (FlowSet) outValue;
            in.copy(out);
            out.add(u);
        }
        
        protected Object entryInitialFlow() { return new ArraySparseSet(); }
        
        protected Object newInitialFlow() { return new ArraySparseSet(); }
        
        public boolean mayExecuteTwice(Unit u) { return ((FlowSet) getFlowBefore(u)).contains(u); }
    }
    
    
    protected void internalTransform(String phaseName, Map options) {
        final jedd.internal.RelationContainer allContexts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v() },
                                              new jedd.PhysicalDomain[] { C1.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1> allContexts = jedd.internal.Jedd.v().trueBDD(); at" +
                                               " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/ExecutesMany" +
                                               "Analysis.jedd:104,15-26"),
                                              jedd.internal.Jedd.v().trueBDD());
        for (Iterator clIt = Scene.v().getApplicationClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            for (Iterator mIt = cl.getMethods().iterator(); mIt.hasNext(); ) {
                final SootMethod m = (SootMethod) mIt.next();
                if (!m.hasActiveBody()) continue;
                if (!Results.v().reachableMethods().contains(null, m)) continue;
                IntraProc intra = this.new IntraProc(new ExceptionalUnitGraph(m.getActiveBody()));
                for (Iterator sIt = m.getActiveBody().getUnits().iterator(); sIt.hasNext(); ) {
                    final Unit s = (Unit) sIt.next();
                    Scene.v().getUnitNumberer().add(s);
                    stmtMethod.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { s, m },
                                                                      new jedd.Attribute[] { stmt.v(), method.v() },
                                                                      new jedd.PhysicalDomain[] { ST.v(), MT.v() }));
                    if (intra.mayExecuteTwice(s)) {
                        twiceUnit.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { s },
                                                                                                                                 new jedd.Attribute[] { stmt.v() },
                                                                                                                                 new jedd.PhysicalDomain[] { ST.v() })),
                                                                      allContexts,
                                                                      new jedd.PhysicalDomain[] {  }));
                    }
                }
            }
        }
        callGraph.eq(jedd.internal.Jedd.v().project(Results.v().callGraph().edges().get(),
                                                    new jedd.PhysicalDomain[] { KD.v() }));
        while (true) {
            final jedd.internal.RelationContainer oldUnit =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), stmt.v() },
                                                  new jedd.PhysicalDomain[] { C1.v(), ST.v() },
                                                  ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                                   "mains.C1, soot.jimple.paddle.bdddomains.stmt:soot.jimple.pad" +
                                                   "dle.bdddomains.ST> oldUnit = twiceUnit; at /tmp/olhotak/soot" +
                                                   "-trunk/src/soot/jimple/paddle/ExecutesManyAnalysis.jedd:126," +
                                                   "25-32"),
                                                  twiceUnit);
            final jedd.internal.RelationContainer oldMethod =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                                  new jedd.PhysicalDomain[] { C1.v(), MT.v() },
                                                  ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                                   "mains.C1, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                                   "addle.bdddomains.MT> oldMethod = twiceMethod; at /tmp/olhota" +
                                                   "k/soot-trunk/src/soot/jimple/paddle/ExecutesManyAnalysis.jed" +
                                                   "d:127,27-36"),
                                                  twiceMethod);
            twiceMethod.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(twiceUnit),
                                                                                              jedd.internal.Jedd.v().project(callGraph,
                                                                                                                             new jedd.PhysicalDomain[] { MS.v() }),
                                                                                              new jedd.PhysicalDomain[] { ST.v(), C1.v() }),
                                                               new jedd.PhysicalDomain[] { C2.v() },
                                                               new jedd.PhysicalDomain[] { C1.v() }));
            twiceMethod.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(twiceMethod,
                                                                                                                                                         new jedd.PhysicalDomain[] { MT.v() },
                                                                                                                                                         new jedd.PhysicalDomain[] { MS.v() })),
                                                                                              jedd.internal.Jedd.v().project(callGraph,
                                                                                                                             new jedd.PhysicalDomain[] { ST.v() }),
                                                                                              new jedd.PhysicalDomain[] { MS.v(), C1.v() }),
                                                               new jedd.PhysicalDomain[] { C2.v() },
                                                               new jedd.PhysicalDomain[] { C1.v() }));
            twiceUnit.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(twiceMethod),
                                                             stmtMethod,
                                                             new jedd.PhysicalDomain[] { MT.v() }));
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(oldUnit), twiceUnit) &&
                  jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(oldMethod), twiceMethod))
                break;
        }
    }
    
    public boolean executesMany(Unit s) {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(twiceUnit,
                                                                                                                                                                 new jedd.PhysicalDomain[] { C1.v() })),
                                                                                                      jedd.internal.Jedd.v().literal(new Object[] { s },
                                                                                                                                     new jedd.Attribute[] { stmt.v() },
                                                                                                                                     new jedd.PhysicalDomain[] { ST.v() }),
                                                                                                      new jedd.PhysicalDomain[] { ST.v() })),
                                              jedd.internal.Jedd.v().falseBDD());
    }
    
    public boolean executesMany(SootMethod m) {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(twiceMethod,
                                                                                                                                                                 new jedd.PhysicalDomain[] { C1.v() })),
                                                                                                      jedd.internal.Jedd.v().literal(new Object[] { m },
                                                                                                                                     new jedd.Attribute[] { method.v() },
                                                                                                                                     new jedd.PhysicalDomain[] { MT.v() }),
                                                                                                      new jedd.PhysicalDomain[] { MT.v() })),
                                              jedd.internal.Jedd.v().falseBDD());
    }
    
    protected final jedd.internal.RelationContainer stmtMethod =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), method.v() },
                                          new jedd.PhysicalDomain[] { ST.v(), MT.v() },
                                          ("protected <soot.jimple.paddle.bdddomains.stmt, soot.jimple.p" +
                                           "addle.bdddomains.method> stmtMethod at /tmp/olhotak/soot-tru" +
                                           "nk/src/soot/jimple/paddle/ExecutesManyAnalysis.jedd:143,14-2" +
                                           "8"));
    
    protected final jedd.internal.RelationContainer twiceUnit =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), stmt.v() },
                                          new jedd.PhysicalDomain[] { C1.v(), ST.v() },
                                          ("protected <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.p" +
                                           "addle.bdddomains.stmt> twiceUnit at /tmp/olhotak/soot-trunk/" +
                                           "src/soot/jimple/paddle/ExecutesManyAnalysis.jedd:144,14-26"));
    
    protected final jedd.internal.RelationContainer twiceMethod =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                          new jedd.PhysicalDomain[] { C1.v(), MT.v() },
                                          ("protected <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.p" +
                                           "addle.bdddomains.method> twiceMethod at /tmp/olhotak/soot-tr" +
                                           "unk/src/soot/jimple/paddle/ExecutesManyAnalysis.jedd:145,14-" +
                                           "28"));
    
    protected final jedd.internal.RelationContainer callGraph =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), tgtc.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { C1.v(), MS.v(), ST.v(), C2.v(), MT.v() },
                                          ("protected <soot.jimple.paddle.bdddomains.srcc, soot.jimple.p" +
                                           "addle.bdddomains.srcm, soot.jimple.paddle.bdddomains.stmt, s" +
                                           "oot.jimple.paddle.bdddomains.tgtc, soot.jimple.paddle.bdddom" +
                                           "ains.tgtm> callGraph at /tmp/olhotak/soot-trunk/src/soot/jim" +
                                           "ple/paddle/ExecutesManyAnalysis.jedd:146,14-44"));
}
