package soot.jimple.paddle;

import soot.*;
import soot.jimple.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public class BDDCflowStack {
    public static final boolean DEBUG = false;
    
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
    
    private void debug(String s) { if (DEBUG) G.v().out.println(s); }
    
    private jedd.internal.RelationContainer within(Shadow sh) {
        debug("Doing within " + sh);
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> ret = jedd.internal.Jedd.v().falseBDD(); at /home/" +
                                               "research/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                               "e/BDDCflowStack.jedd:60,15-18"),
                                              jedd.internal.Jedd.v().falseBDD());
        boolean inShadow = false;
        for (Iterator sIt = sh.method().getActiveBody().getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if (s == sh.popStmt()) inShadow = false;
            if (inShadow) {
                ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { s },
                                                           new jedd.Attribute[] { stmt.v() },
                                                           new jedd.PhysicalDomain[] { ST.v() }));
            }
            if (s == sh.pushStmt()) inShadow = true;
        }
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("return ret; at /home/research/ccl/olhota/olhotak/soot-trunk/" +
                                                    "src/soot/jimple/paddle/BDDCflowStack.jedd:70,8-14"),
                                                   ret);
    }
    
    private jedd.internal.RelationContainer targetsOf(final jedd.internal.RelationContainer calls) {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v() },
                                                   new jedd.PhysicalDomain[] { MT.v() },
                                                   ("return jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v()" +
                                                    ".read(jedd.internal.Jedd.v().project(cflow.callGraph(), new " +
                                                    "jedd.PhysicalDomain[...])), calls, new jedd.PhysicalDomain[." +
                                                    "..]); at /home/research/ccl/olhota/olhotak/soot-trunk/src/so" +
                                                    "ot/jimple/paddle/BDDCflowStack.jedd:74,8-14"),
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
                                                    "..]); at /home/research/ccl/olhota/olhotak/soot-trunk/src/so" +
                                                    "ot/jimple/paddle/BDDCflowStack.jedd:78,8-14"),
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
                                                    "...]); at /home/research/ccl/olhota/olhotak/soot-trunk/src/s" +
                                                    "oot/jimple/paddle/BDDCflowStack.jedd:82,8-14"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(cflow.stmtMethod()),
                                                                                  methods,
                                                                                  new jedd.PhysicalDomain[] { MT.v() }));
    }
    
    private jedd.internal.RelationContainer stmtsInShadow(final jedd.internal.RelationContainer methods) {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v(), shadow.v() },
                                                   new jedd.PhysicalDomain[] { ST.v(), V1.v() },
                                                   ("return jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v()" +
                                                    ".read(cflow.stmtMethod()), methods, new jedd.PhysicalDomain[" +
                                                    "...]); at /home/research/ccl/olhota/olhotak/soot-trunk/src/s" +
                                                    "oot/jimple/paddle/BDDCflowStack.jedd:86,8-14"),
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
                                               "; at /home/research/ccl/olhota/olhotak/soot-trunk/src/soot/j" +
                                               "imple/paddle/BDDCflowStack.jedd:90,23-26"),
                                              jedd.internal.Jedd.v().falseBDD());
        for (Iterator shIt =
               new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v() },
                                                   new jedd.PhysicalDomain[] { V1.v() },
                                                   ("shadows.iterator() at /home/research/ccl/olhota/olhotak/soot" +
                                                    "-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:91,29-36"),
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
                                                   "ternal.RelationContainer(...)); at /home/research/ccl/olhota" +
                                                   "/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack.jed" +
                                                   "d:96,29-36"),
                                                  targetsOfShadow(new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v(), stmt.v() },
                                                                                                      new jedd.PhysicalDomain[] { V1.v(), ST.v() },
                                                                                                      ("targetsOfShadow(ret) at /home/research/ccl/olhota/olhotak/so" +
                                                                                                       "ot-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:96,39-54"),
                                                                                                      ret)));
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(ret),
                                              ret.eqUnion(stmtsInShadow(new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v(), method.v() },
                                                                                                            new jedd.PhysicalDomain[] { V1.v(), MT.v() },
                                                                                                            ("stmtsInShadow(targets) at /home/research/ccl/olhota/olhotak/" +
                                                                                                             "soot-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:97,31-4" +
                                                                                                             "4"),
                                                                                                            targets)))))
                break;
        }
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v(), stmt.v() },
                                                   new jedd.PhysicalDomain[] { V1.v(), ST.v() },
                                                   ("return ret; at /home/research/ccl/olhota/olhotak/soot-trunk/" +
                                                    "src/soot/jimple/paddle/BDDCflowStack.jedd:99,8-14"),
                                                   ret);
    }
    
    private jedd.internal.RelationContainer mustCflow() {
        final jedd.internal.RelationContainer shadowStmts =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> shadowStmts = jedd.internal.Jedd.v().falseBDD(); a" +
                                               "t /home/research/ccl/olhota/olhotak/soot-trunk/src/soot/jimp" +
                                               "le/paddle/BDDCflowStack.jedd:120,15-26"),
                                              jedd.internal.Jedd.v().falseBDD());
        for (Iterator shIt =
               new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v() },
                                                   new jedd.PhysicalDomain[] { V1.v() },
                                                   ("shadows.iterator() at /home/research/ccl/olhota/olhotak/soot" +
                                                    "-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:121,29-36"),
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
                                               "/home/research/ccl/olhota/olhotak/soot-trunk/src/soot/jimple" +
                                               "/paddle/BDDCflowStack.jedd:127,17-24"),
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
                                               "mains.ST> ret = jedd.internal.Jedd.v().falseBDD(); at /home/" +
                                               "research/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                               "e/BDDCflowStack.jedd:132,15-18"),
                                              jedd.internal.Jedd.v().falseBDD());
        final jedd.internal.RelationContainer oldRet =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> oldRet; at /home/research/ccl/olhota/olhotak/soot-" +
                                               "trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:133,15-21"));
        do  {
            final jedd.internal.RelationContainer targetStmts =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                  new jedd.PhysicalDomain[] { ST.v() },
                                                  ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                                   "mains.ST> targetStmts = stmtsIn(new jedd.internal.RelationCo" +
                                                   "ntainer(...)); at /home/research/ccl/olhota/olhotak/soot-tru" +
                                                   "nk/src/soot/jimple/paddle/BDDCflowStack.jedd:135,19-30"),
                                                  stmtsIn(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v() },
                                                                                              new jedd.PhysicalDomain[] { MT.v() },
                                                                                              ("stmtsIn(targets) at /home/research/ccl/olhota/olhotak/soot-t" +
                                                                                               "runk/src/soot/jimple/paddle/BDDCflowStack.jedd:135,33-40"),
                                                                                              targets)));
            targetStmts.eqMinus(shadowStmts);
            targets.eq(targetsOf(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                                     new jedd.PhysicalDomain[] { ST.v() },
                                                                     ("targetsOf(targetStmts) at /home/research/ccl/olhota/olhotak/" +
                                                                      "soot-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:137,22-" +
                                                                      "31"),
                                                                     targetStmts)));
            oldRet.eq(ret);
            ret.eqUnion(targetStmts);
        }while(!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(oldRet), ret)); 
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("return jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().r" +
                                                    "ead(jedd.internal.Jedd.v().trueBDD()), ret); at /home/resear" +
                                                    "ch/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/BDDC" +
                                                    "flowStack.jedd:141,8-14"),
                                                   jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().trueBDD()),
                                                                                ret));
    }
    
    private final jedd.internal.RelationContainer shadows =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v() },
                                          new jedd.PhysicalDomain[] { V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.shadow:soot.jimple.pa" +
                                           "ddle.bdddomains.V1> shadows = jedd.internal.Jedd.v().falseBD" +
                                           "D() at /home/research/ccl/olhota/olhotak/soot-trunk/src/soot" +
                                           "/jimple/paddle/BDDCflowStack.jedd:144,12-23"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer pushes =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v(), stmt.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.shadow, soot.jimple.p" +
                                           "addle.bdddomains.stmt> pushes = jedd.internal.Jedd.v().false" +
                                           "BDD() at /home/research/ccl/olhota/olhotak/soot-trunk/src/so" +
                                           "ot/jimple/paddle/BDDCflowStack.jedd:145,12-26"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mustCflow =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                          new jedd.PhysicalDomain[] { ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.stmt> mustCflow = jed" +
                                           "d.internal.Jedd.v().trueBDD() at /home/research/ccl/olhota/o" +
                                           "lhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:" +
                                           "146,12-18"),
                                          jedd.internal.Jedd.v().trueBDD());
    
    private final jedd.internal.RelationContainer mayCflow =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v(), stmt.v() },
                                          new jedd.PhysicalDomain[] { V1.v(), ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.shadow, soot.jimple.p" +
                                           "addle.bdddomains.stmt> mayCflow = jedd.internal.Jedd.v().tru" +
                                           "eBDD() at /home/research/ccl/olhota/olhotak/soot-trunk/src/s" +
                                           "oot/jimple/paddle/BDDCflowStack.jedd:147,12-26"),
                                          jedd.internal.Jedd.v().trueBDD());
    
    private final jedd.internal.RelationContainer isValids =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                          new jedd.PhysicalDomain[] { ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.stmt> isValids = jedd" +
                                           ".internal.Jedd.v().falseBDD() at /home/research/ccl/olhota/o" +
                                           "lhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:" +
                                           "148,12-18"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer neverValid =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                          new jedd.PhysicalDomain[] { ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.stmt> neverValid = je" +
                                           "dd.internal.Jedd.v().falseBDD() at /home/research/ccl/olhota" +
                                           "/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack.jed" +
                                           "d:149,12-18"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer alwaysValid =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                          new jedd.PhysicalDomain[] { ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.stmt> alwaysValid = j" +
                                           "edd.internal.Jedd.v().falseBDD() at /home/research/ccl/olhot" +
                                           "a/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack.je" +
                                           "dd:150,12-18"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public String queryStats() {
        return "both: " +
               new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read" +
                                                    "(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().rea" +
                                                    "d(neverValid), alwaysValid)), isValids).size() at /home/rese" +
                                                    "arch/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/BD" +
                                                    "DCflowStack.jedd:154,55-59"),
                                                   jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(neverValid),
                                                                                                                                                 alwaysValid)),
                                                                                    isValids)).size() +
               " always but not never: " +
               new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read" +
                                                    "(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(al" +
                                                    "waysValid), neverValid)), isValids).size() at /home/research" +
                                                    "/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCfl" +
                                                    "owStack.jedd:155,74-78"),
                                                   jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(alwaysValid),
                                                                                                                                             neverValid)),
                                                                                    isValids)).size() +
               " never but not always: " +
               new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read" +
                                                    "(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(ne" +
                                                    "verValid), alwaysValid)), isValids).size() at /home/research" +
                                                    "/ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCfl" +
                                                    "owStack.jedd:156,74-78"),
                                                   jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(neverValid),
                                                                                                                                             alwaysValid)),
                                                                                    isValids)).size() +
               " not statically known: " +
        new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                            new jedd.PhysicalDomain[] { ST.v() },
                                            ("jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(jed" +
                                             "d.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(isVali" +
                                             "ds), neverValid)), alwaysValid).size() at /home/research/ccl" +
                                             "/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowSt" +
                                             "ack.jedd:157,72-76"),
                                            jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(isValids),
                                                                                                                                  neverValid)),
                                                                         alwaysValid)).size();
    }
    
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
                                                       ("return neverValid; at /home/research/ccl/olhota/olhotak/soot" +
                                                        "-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:176,29-35"),
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
                                               "omain[...]); at /home/research/ccl/olhota/olhotak/soot-trunk" +
                                               "/src/soot/jimple/paddle/BDDCflowStack.jedd:178,15-25"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(mayCflow,
                                                                                                                                     new jedd.PhysicalDomain[] { V1.v() })),
                                                                          isValids,
                                                                          new jedd.PhysicalDomain[] { ST.v() }));
        neverValid.eq(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(isValids), mayBeValid));
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("return neverValid; at /home/research/ccl/olhota/olhotak/soot" +
                                                    "-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:180,8-14"),
                                                   neverValid);
    }
    
    public Iterator neverValid() {
        debug("Computing neverValid");
        Iterator ret =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("computeNeverValid().iterator() at /home/research/ccl/olhota/" +
                                               "olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd" +
                                               ":187,43-51"),
                                              computeNeverValid()).iterator();
        debug("Done computing neverValid");
        return ret;
    }
    
    private jedd.internal.RelationContainer computeAlwaysValid() {
        if (bindsArgs) {
            alwaysValid.eq(jedd.internal.Jedd.v().falseBDD());
            return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                       new jedd.PhysicalDomain[] { ST.v() },
                                                       ("return alwaysValid; at /home/research/ccl/olhota/olhotak/soo" +
                                                        "t-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:195,12-18"),
                                                       alwaysValid);
        }
        if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(mustCflow), jedd.internal.Jedd.v().trueBDD()))
            mustCflow.eq(mustCflow());
        alwaysValid.eq(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(mustCflow),
                                                   isValids,
                                                   new jedd.PhysicalDomain[] { ST.v() }));
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("return alwaysValid; at /home/research/ccl/olhota/olhotak/soo" +
                                                    "t-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:199,8-14"),
                                                   alwaysValid);
    }
    
    public Iterator alwaysValid() {
        debug("Computing alwaysValid");
        Iterator ret =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("computeAlwaysValid().iterator() at /home/research/ccl/olhota" +
                                               "/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack.jed" +
                                               "d:207,44-52"),
                                              computeAlwaysValid()).iterator();
        debug("Done computing alwaysValid");
        return ret;
    }
    
    public Iterator unnecessaryShadows() {
        debug("Computing unnecessaryShadows");
        final jedd.internal.RelationContainer interestingIsValids =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> interestingIsValids = isValids; at /home/research/" +
                                               "ccl/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflo" +
                                               "wStack.jedd:219,15-34"),
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
                                               ", new jedd.PhysicalDomain[...]); at /home/research/ccl/olhot" +
                                               "a/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack.je" +
                                               "dd:223,17-33"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(mayCflow),
                                                                             interestingIsValids,
                                                                             new jedd.PhysicalDomain[] { ST.v() }));
        if (!bindsArgs) {
            necessaryShadows.eqMinus(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(mustCflow),
                                                                    pushes,
                                                                    new jedd.PhysicalDomain[] { ST.v() }));
        }
        Iterator ret =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { shadow.v() },
                                              new jedd.PhysicalDomain[] { V1.v() },
                                              ("jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(sha" +
                                               "dows), necessaryShadows).iterator() at /home/research/ccl/ol" +
                                               "hota/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflowStack" +
                                               ".jedd:227,52-60"),
                                              jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(shadows),
                                                                           necessaryShadows)).iterator();
        debug("Done computing unnecessaryShadows");
        return ret;
    }
}
