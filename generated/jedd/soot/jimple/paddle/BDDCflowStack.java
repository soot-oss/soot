package soot.jimple.paddle;

import soot.*;
import soot.jimple.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public class BDDCflowStack {
    BDDCflow cflow;
    
    boolean bindsArgs;
    
    public BDDCflowStack(BDDCflow cflow, Collection shadows, Collection isValids, boolean bindsArgs) {
        super();
        this.cflow = cflow;
        this.bindsArgs = bindsArgs;
        for (Iterator sIt = isValids.iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            Scene.v().getUnitNumberer().add(s);
            this.isValids.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { s },
                                                                 new jedd.Attribute[] { stmt.v() },
                                                                 new jedd.PhysicalDomain[] { ST.v() }));
        }
        for (Iterator sIt = shadows.iterator(); sIt.hasNext(); ) {
            final Shadow s = (Shadow) sIt.next();
            ShadowNumberer.v().add(s);
            Scene.v().getUnitNumberer().add(s.pushStmt());
            Scene.v().getUnitNumberer().add(s.popStmt());
            this.shadows.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { s },
                                                                new jedd.Attribute[] { shadow.v() },
                                                                new jedd.PhysicalDomain[] { V1.v() }));
            this.pushes.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { s, s.pushStmt() },
                                                               new jedd.Attribute[] { shadow.v(), stmt.v() },
                                                               new jedd.PhysicalDomain[] { V1.v(), ST.v() }));
        }
    }
    
    private static final boolean DEBUG = false;
    
    private void debug(String s) { if (DEBUG) System.err.println(s); }
    
    private jedd.internal.RelationContainer within(Shadow sh) {
        debug("Doing within " + sh);
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> ret = jedd.internal.Jedd.v().falseBDD(); at /tmp/o" +
                                               "lhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:" +
                                               "60,15-18"),
                                              jedd.internal.Jedd.v().falseBDD());
        boolean inShadow = false;
        for (Iterator sIt = sh.method().getActiveBody().getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if (s == sh.popStmt()) inShadow = false;
            if (inShadow) {
                ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { s },
                                                           new jedd.Attribute[] { stmt.v() },
                                                           new jedd.PhysicalDomain[] { ST.v() }));
                debug("within: " + s);
            }
            if (s == sh.pushStmt()) inShadow = true;
        }
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("return ret; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                    "e/BDDCflowStack.jedd:71,8-14"),
                                                   ret);
    }
    
    private jedd.internal.RelationContainer targetsOf(final jedd.internal.RelationContainer calls) {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v() },
                                                   new jedd.PhysicalDomain[] { MT.v() },
                                                   ("return jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v()" +
                                                    ".read(jedd.internal.Jedd.v().project(cflow.callGraph(), new " +
                                                    "jedd.PhysicalDomain[...])), calls, new jedd.PhysicalDomain[." +
                                                    "..]); at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDC" +
                                                    "flowStack.jedd:75,8-14"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(cflow.callGraph(),
                                                                                                                                             new jedd.PhysicalDomain[] { MS.v() })),
                                                                                  calls,
                                                                                  new jedd.PhysicalDomain[] { ST.v() }));
    }
    
    private jedd.internal.RelationContainer targetsOfShadow(final jedd.internal.RelationContainer calls) {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), shadow.v() },
                                                   new jedd.PhysicalDomain[] { MT.v(), V1.v() },
                                                   ("return jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v()" +
                                                    ".read(jedd.internal.Jedd.v().project(cflow.callGraph(), new " +
                                                    "jedd.PhysicalDomain[...])), calls, new jedd.PhysicalDomain[." +
                                                    "..]); at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDC" +
                                                    "flowStack.jedd:79,8-14"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(cflow.callGraph(),
                                                                                                                                             new jedd.PhysicalDomain[] { MS.v() })),
                                                                                  calls,
                                                                                  new jedd.PhysicalDomain[] { ST.v() }));
    }
    
    private jedd.internal.RelationContainer stmtsIn(final jedd.internal.RelationContainer methods) {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("return jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v()" +
                                                    ".read(cflow.stmtMethod()), methods, new jedd.PhysicalDomain[" +
                                                    "...]); at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDD" +
                                                    "CflowStack.jedd:83,8-14"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(cflow.stmtMethod()),
                                                                                  methods,
                                                                                  new jedd.PhysicalDomain[] { MT.v() }));
    }
    
    private jedd.internal.RelationContainer stmtsInShadow(final jedd.internal.RelationContainer methods) {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), shadow.v() },
                                                   new jedd.PhysicalDomain[] { ST.v(), V1.v() },
                                                   ("return jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v()" +
                                                    ".read(cflow.stmtMethod()), methods, new jedd.PhysicalDomain[" +
                                                    "...]); at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDD" +
                                                    "CflowStack.jedd:87,8-14"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(cflow.stmtMethod()),
                                                                                  methods,
                                                                                  new jedd.PhysicalDomain[] { MT.v() }));
    }
    
    private jedd.internal.RelationContainer mayCflow() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v(), stmt.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.shadow:soot.jimple.paddle.bdd" +
                                               "domains.V1, soot.jimple.paddle.bdddomains.stmt:soot.jimple.p" +
                                               "addle.bdddomains.ST> ret = jedd.internal.Jedd.v().falseBDD()" +
                                               "; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflow" +
                                               "Stack.jedd:91,23-26"),
                                              jedd.internal.Jedd.v().falseBDD());
        for (Iterator shIt =
               new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v() },
                                                   new jedd.PhysicalDomain[] { V1.v() },
                                                   ("shadows.iterator() at /tmp/olhotak/soot-trunk/src/soot/jimpl" +
                                                    "e/paddle/BDDCflowStack.jedd:92,29-36"),
                                                   shadows).iterator();
             shIt.hasNext();
             ) {
            final Shadow sh = (Shadow) shIt.next();
            ret.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { sh },
                                                                                                               new jedd.Attribute[] { shadow.v() },
                                                                                                               new jedd.PhysicalDomain[] { V1.v() })),
                                                    within(sh),
                                                    new jedd.PhysicalDomain[] {  }));
        }
        while (true) {
            final jedd.internal.RelationContainer targets =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v(), method.v() },
                                                  new jedd.PhysicalDomain[] { V1.v(), MT.v() },
                                                  ("<soot.jimple.paddle.bdddomains.shadow:soot.jimple.paddle.bdd" +
                                                   "domains.V1, soot.jimple.paddle.bdddomains.method:soot.jimple" +
                                                   ".paddle.bdddomains.MT> targets = targetsOfShadow(new jedd.in" +
                                                   "ternal.RelationContainer(...)); at /tmp/olhotak/soot-trunk/s" +
                                                   "rc/soot/jimple/paddle/BDDCflowStack.jedd:97,29-36"),
                                                  targetsOfShadow(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), shadow.v() },
                                                                                                      new jedd.PhysicalDomain[] { ST.v(), V1.v() },
                                                                                                      ("targetsOfShadow(ret) at /tmp/olhotak/soot-trunk/src/soot/jim" +
                                                                                                       "ple/paddle/BDDCflowStack.jedd:97,39-54"),
                                                                                                      ret)));
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(ret),
                                              ret.eqUnion(stmtsInShadow(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), shadow.v() },
                                                                                                            new jedd.PhysicalDomain[] { MT.v(), V1.v() },
                                                                                                            ("stmtsInShadow(targets) at /tmp/olhotak/soot-trunk/src/soot/j" +
                                                                                                             "imple/paddle/BDDCflowStack.jedd:98,31-44"),
                                                                                                            targets)))))
                break;
        }
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), shadow.v() },
                                                   new jedd.PhysicalDomain[] { ST.v(), V1.v() },
                                                   ("return ret; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                    "e/BDDCflowStack.jedd:100,8-14"),
                                                   ret);
    }
    
    private jedd.internal.RelationContainer mustCflow() {
        final jedd.internal.RelationContainer shadowStmts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> shadowStmts = jedd.internal.Jedd.v().falseBDD(); a" +
                                               "t /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowSta" +
                                               "ck.jedd:121,15-26"),
                                              jedd.internal.Jedd.v().falseBDD());
        for (Iterator shIt =
               new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v() },
                                                   new jedd.PhysicalDomain[] { V1.v() },
                                                   ("shadows.iterator() at /tmp/olhotak/soot-trunk/src/soot/jimpl" +
                                                    "e/paddle/BDDCflowStack.jedd:122,29-36"),
                                                   shadows).iterator();
             shIt.hasNext();
             ) {
            final Shadow sh = (Shadow) shIt.next();
            if (sh.unconditional()) { shadowStmts.eqUnion(within(sh)); }
        }
        final jedd.internal.RelationContainer targets =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v() },
                                              new jedd.PhysicalDomain[] { MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.method:soot.jimple.paddle.bdd" +
                                               "domains.MT> targets = jedd.internal.Jedd.v().falseBDD(); at " +
                                               "/tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack" +
                                               ".jedd:128,17-24"),
                                              jedd.internal.Jedd.v().falseBDD());
        for (Iterator mIt = Scene.v().getEntryPoints().iterator(); mIt.hasNext(); ) {
            final SootMethod m = (SootMethod) mIt.next();
            targets.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { m },
                                                           new jedd.Attribute[] { method.v() },
                                                           new jedd.PhysicalDomain[] { MT.v() }));
        }
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> ret = jedd.internal.Jedd.v().falseBDD(); at /tmp/o" +
                                               "lhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:" +
                                               "133,15-18"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer oldRet =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> oldRet; at /tmp/olhotak/soot-trunk/src/soot/jimple" +
                                               "/paddle/BDDCflowStack.jedd:134,15-21"));
        do  {
            final jedd.internal.RelationContainer targetStmts =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                  new jedd.PhysicalDomain[] { ST.v() },
                                                  ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                                   "mains.ST> targetStmts = stmtsIn(new jedd.internal.RelationCo" +
                                                   "ntainer(...)); at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                   "ddle/BDDCflowStack.jedd:136,19-30"),
                                                  stmtsIn(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v() },
                                                                                              new jedd.PhysicalDomain[] { MT.v() },
                                                                                              ("stmtsIn(targets) at /tmp/olhotak/soot-trunk/src/soot/jimple/" +
                                                                                               "paddle/BDDCflowStack.jedd:136,33-40"),
                                                                                              targets)));
            targetStmts.eqMinus(shadowStmts);
            targets.eq(targetsOf(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                                     new jedd.PhysicalDomain[] { ST.v() },
                                                                     ("targetsOf(targetStmts) at /tmp/olhotak/soot-trunk/src/soot/j" +
                                                                      "imple/paddle/BDDCflowStack.jedd:138,22-31"),
                                                                     targetStmts)));
            oldRet.eq(ret);
            ret.eqUnion(targetStmts);
        }while(!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(oldRet), ret)); 
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("return ret; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                    "e/BDDCflowStack.jedd:142,8-14"),
                                                   ret);
    }
    
    private final jedd.internal.RelationContainer shadows =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v() },
                                          new jedd.PhysicalDomain[] { V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.shadow:soot.jimple.pa" +
                                           "ddle.bdddomains.V1> shadows = jedd.internal.Jedd.v().falseBD" +
                                           "D() at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCfl" +
                                           "owStack.jedd:145,12-23"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer pushes =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v(), stmt.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.shadow, soot.jimple.p" +
                                           "addle.bdddomains.stmt> pushes = jedd.internal.Jedd.v().false" +
                                           "BDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDC" +
                                           "flowStack.jedd:146,12-26"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mustCflow =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                          new jedd.PhysicalDomain[] { ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.stmt> mustCflow = jed" +
                                           "d.internal.Jedd.v().trueBDD() at /tmp/olhotak/soot-trunk/src" +
                                           "/soot/jimple/paddle/BDDCflowStack.jedd:147,12-18"),
                                          jedd.internal.Jedd.v().trueBDD());
    
    private final jedd.internal.RelationContainer mayCflow =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v(), stmt.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.shadow, soot.jimple.p" +
                                           "addle.bdddomains.stmt> mayCflow = jedd.internal.Jedd.v().tru" +
                                           "eBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDD" +
                                           "CflowStack.jedd:148,12-26"),
                                          jedd.internal.Jedd.v().trueBDD());
    
    private final jedd.internal.RelationContainer isValids =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                          new jedd.PhysicalDomain[] { ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.stmt> isValids = jedd" +
                                           ".internal.Jedd.v().falseBDD() at /tmp/olhotak/soot-trunk/src" +
                                           "/soot/jimple/paddle/BDDCflowStack.jedd:149,12-18"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer neverValid =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                          new jedd.PhysicalDomain[] { ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.stmt> neverValid = je" +
                                           "dd.internal.Jedd.v().falseBDD() at /tmp/olhotak/soot-trunk/s" +
                                           "rc/soot/jimple/paddle/BDDCflowStack.jedd:150,12-18"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer alwaysValid =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                          new jedd.PhysicalDomain[] { ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.stmt> alwaysValid = j" +
                                           "edd.internal.Jedd.v().falseBDD() at /tmp/olhotak/soot-trunk/" +
                                           "src/soot/jimple/paddle/BDDCflowStack.jedd:151,12-18"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public boolean neverValid(Stmt s) {
        if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(mayCflow), jedd.internal.Jedd.v().trueBDD()))
            mayCflow.eq(mayCflow());
        return jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { s },
                                                                                                                                                                new jedd.Attribute[] { stmt.v() },
                                                                                                                                                                new jedd.PhysicalDomain[] { ST.v() })),
                                                                                                     jedd.internal.Jedd.v().project(mayCflow,
                                                                                                                                    new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                     new jedd.PhysicalDomain[] { ST.v() })),
                                             jedd.internal.Jedd.v().falseBDD());
    }
    
    public boolean alwaysValid(Stmt s) {
        if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(mustCflow), jedd.internal.Jedd.v().trueBDD()))
            mustCflow.eq(mustCflow());
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { s },
                                                                                                                                                                 new jedd.Attribute[] { stmt.v() },
                                                                                                                                                                 new jedd.PhysicalDomain[] { ST.v() })),
                                                                                                      mustCflow,
                                                                                                      new jedd.PhysicalDomain[] { ST.v() })),
                                              jedd.internal.Jedd.v().falseBDD());
    }
    
    private jedd.internal.RelationContainer computeNeverValid() {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(neverValid), jedd.internal.Jedd.v().falseBDD()))
            return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                       new jedd.PhysicalDomain[] { ST.v() },
                                                       ("return neverValid; at /tmp/olhotak/soot-trunk/src/soot/jimpl" +
                                                        "e/paddle/BDDCflowStack.jedd:170,29-35"),
                                                       neverValid);
        if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(mayCflow), jedd.internal.Jedd.v().trueBDD()))
            mayCflow.eq(mayCflow());
        final jedd.internal.RelationContainer mayBeValid =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> mayBeValid = jedd.internal.Jedd.v().join(jedd.inte" +
                                               "rnal.Jedd.v().read(jedd.internal.Jedd.v().project(mayCflow, " +
                                               "new jedd.PhysicalDomain[...])), isValids, new jedd.PhysicalD" +
                                               "omain[...]); at /tmp/olhotak/soot-trunk/src/soot/jimple/padd" +
                                               "le/BDDCflowStack.jedd:172,15-25"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(mayCflow,
                                                                                                                                     new jedd.PhysicalDomain[] { V1.v() })),
                                                                          isValids,
                                                                          new jedd.PhysicalDomain[] { ST.v() }));
        neverValid.eq(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(isValids), mayBeValid));
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("return neverValid; at /tmp/olhotak/soot-trunk/src/soot/jimpl" +
                                                    "e/paddle/BDDCflowStack.jedd:174,8-14"),
                                                   neverValid);
    }
    
    public Iterator neverValid() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("computeNeverValid().iterator() at /tmp/olhotak/soot-trunk/sr" +
                                                    "c/soot/jimple/paddle/BDDCflowStack.jedd:180,35-43"),
                                                   computeNeverValid()).iterator();
    }
    
    private jedd.internal.RelationContainer computeAlwaysValid() {
        if (bindsArgs) {
            alwaysValid.eq(jedd.internal.Jedd.v().falseBDD());
            return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                       new jedd.PhysicalDomain[] { ST.v() },
                                                       ("return alwaysValid; at /tmp/olhotak/soot-trunk/src/soot/jimp" +
                                                        "le/paddle/BDDCflowStack.jedd:186,12-18"),
                                                       alwaysValid);
        }
        if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(mustCflow), jedd.internal.Jedd.v().trueBDD()))
            mustCflow.eq(mustCflow());
        alwaysValid.eq(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(mustCflow),
                                                   isValids,
                                                   new jedd.PhysicalDomain[] { ST.v() }));
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("return alwaysValid; at /tmp/olhotak/soot-trunk/src/soot/jimp" +
                                                    "le/paddle/BDDCflowStack.jedd:190,8-14"),
                                                   alwaysValid);
    }
    
    public Iterator alwaysValid() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("computeAlwaysValid().iterator() at /tmp/olhotak/soot-trunk/s" +
                                                    "rc/soot/jimple/paddle/BDDCflowStack.jedd:197,36-44"),
                                                   computeAlwaysValid()).iterator();
    }
    
    public Iterator unnecessaryShadows() {
        final jedd.internal.RelationContainer interestingIsValids =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> interestingIsValids = isValids; at /tmp/olhotak/so" +
                                               "ot-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:206,15-34"),
                                              isValids);
        interestingIsValids.eqMinus(computeAlwaysValid());
        interestingIsValids.eqMinus(computeNeverValid());
        if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(mayCflow), jedd.internal.Jedd.v().trueBDD()))
            mayCflow.eq(mayCflow());
        final jedd.internal.RelationContainer necessaryShadows =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v() },
                                              new jedd.PhysicalDomain[] { V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.shadow:soot.jimple.paddle.bdd" +
                                               "domains.V1> necessaryShadows = jedd.internal.Jedd.v().compos" +
                                               "e(jedd.internal.Jedd.v().read(mayCflow), interestingIsValids" +
                                               ", new jedd.PhysicalDomain[...]); at /tmp/olhotak/soot-trunk/" +
                                               "src/soot/jimple/paddle/BDDCflowStack.jedd:210,17-33"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(mayCflow),
                                                                             interestingIsValids,
                                                                             new jedd.PhysicalDomain[] { ST.v() }));
        if (!bindsArgs) {
            necessaryShadows.eqMinus(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(mustCflow),
                                                                    pushes,
                                                                    new jedd.PhysicalDomain[] { ST.v() }));
        }
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v() },
                                                   new jedd.PhysicalDomain[] { V1.v() },
                                                   ("jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(sha" +
                                                    "dows), necessaryShadows).iterator() at /tmp/olhotak/soot-tru" +
                                                    "nk/src/soot/jimple/paddle/BDDCflowStack.jedd:214,44-52"),
                                                   jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(shadows),
                                                                                necessaryShadows)).iterator();
    }
}
