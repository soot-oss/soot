package soot.jimple.paddle;

import soot.*;
import soot.jimple.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public class BDDCflowStack {
    List shadows;
    
    BDDCflow cflow;
    
    public BDDCflowStack(BDDCflow cflow, List shadows) {
        super();
        this.shadows = shadows;
        this.cflow = cflow;
    }
    
    private jedd.internal.RelationContainer within(Shadow sh) {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> ret = jedd.internal.Jedd.v().falseBDD(); at /home/" +
                                               "research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDCfl" +
                                               "owStack.jedd:42,15-18"),
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
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                    "/jimple/paddle/BDDCflowStack.jedd:50,8-14"),
                                                   ret);
    }
    
    private jedd.internal.RelationContainer targetsOf(final jedd.internal.RelationContainer calls) {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v() },
                                                   new jedd.PhysicalDomain[] { MT.v() },
                                                   ("return jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v()" +
                                                    ".read(jedd.internal.Jedd.v().project(cflow.callGraph(), new " +
                                                    "jedd.PhysicalDomain[...])), calls, new jedd.PhysicalDomain[." +
                                                    "..]); at /home/research/ccl/olhota/soot-trunk/src/soot/jimpl" +
                                                    "e/paddle/BDDCflowStack.jedd:54,8-14"),
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
                                                    "...]); at /home/research/ccl/olhota/soot-trunk/src/soot/jimp" +
                                                    "le/paddle/BDDCflowStack.jedd:58,8-14"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(cflow.stmtMethod()),
                                                                                  methods,
                                                                                  new jedd.PhysicalDomain[] { MT.v() }));
    }
    
    private jedd.internal.RelationContainer mayCflow() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> ret = jedd.internal.Jedd.v().falseBDD(); at /home/" +
                                               "research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDCfl" +
                                               "owStack.jedd:62,15-18"),
                                              jedd.internal.Jedd.v().falseBDD());
        for (Iterator shIt = shadows.iterator(); shIt.hasNext(); ) {
            final Shadow sh = (Shadow) shIt.next();
            ret.eqUnion(this.within(sh));
        }
        while (true) {
            final jedd.internal.RelationContainer targets =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v() },
                                                  new jedd.PhysicalDomain[] { MT.v() },
                                                  ("<soot.jimple.paddle.bdddomains.method:soot.jimple.paddle.bdd" +
                                                   "domains.MT> targets = this.targetsOf(new jedd.internal.Relat" +
                                                   "ionContainer(...)); at /home/research/ccl/olhota/soot-trunk/" +
                                                   "src/soot/jimple/paddle/BDDCflowStack.jedd:68,21-28"),
                                                  this.targetsOf(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                                                                     new jedd.PhysicalDomain[] { ST.v() },
                                                                                                     ("this.targetsOf(ret) at /home/research/ccl/olhota/soot-trunk/" +
                                                                                                      "src/soot/jimple/paddle/BDDCflowStack.jedd:68,31-40"),
                                                                                                     ret)));
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(ret),
                                              ret.eqUnion(this.stmtsIn(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v() },
                                                                                                           new jedd.PhysicalDomain[] { MT.v() },
                                                                                                           ("this.stmtsIn(targets) at /home/research/ccl/olhota/soot-trun" +
                                                                                                            "k/src/soot/jimple/paddle/BDDCflowStack.jedd:69,31-38"),
                                                                                                           targets)))))
                break;
        }
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                    "/jimple/paddle/BDDCflowStack.jedd:71,8-14"),
                                                   ret);
    }
    
    private jedd.internal.RelationContainer mustCflow() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                              new jedd.PhysicalDomain[] { ST.v() },
                                              ("<soot.jimple.paddle.bdddomains.stmt:soot.jimple.paddle.bdddo" +
                                               "mains.ST> ret = jedd.internal.Jedd.v().falseBDD(); at /home/" +
                                               "research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDCfl" +
                                               "owStack.jedd:75,15-18"),
                                              jedd.internal.Jedd.v().falseBDD());
        for (Iterator shIt = shadows.iterator(); shIt.hasNext(); ) {
            final Shadow sh = (Shadow) shIt.next();
            if (sh.unconditional()) { ret.eqUnion(this.within(sh)); }
        }
        while (true) {
            final jedd.internal.RelationContainer methods =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v() },
                                                  new jedd.PhysicalDomain[] { MT.v() },
                                                  ("<soot.jimple.paddle.bdddomains.method:soot.jimple.paddle.bdd" +
                                                   "domains.MT> methods = jedd.internal.Jedd.v().minus(jedd.inte" +
                                                   "rnal.Jedd.v().read(this.targetsOf(new jedd.internal.Relation" +
                                                   "Container(...))), this.targetsOf(new jedd.internal.RelationC" +
                                                   "ontainer(...))); at /home/research/ccl/olhota/soot-trunk/src" +
                                                   "/soot/jimple/paddle/BDDCflowStack.jedd:83,21-28"),
                                                  jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(this.targetsOf(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                                                                                                                              new jedd.PhysicalDomain[] { ST.v() },
                                                                                                                                                              ("this.targetsOf(ret) at /home/research/ccl/olhota/soot-trunk/" +
                                                                                                                                                               "src/soot/jimple/paddle/BDDCflowStack.jedd:83,31-40"),
                                                                                                                                                              ret))),
                                                                               this.targetsOf(new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                                                                                                  new jedd.PhysicalDomain[] { ST.v() },
                                                                                                                                  ("this.targetsOf(jedd.internal.Jedd.v().minus(jedd.internal.Je" +
                                                                                                                                   "dd.v().read(jedd.internal.Jedd.v().trueBDD()), ret)) at /hom" +
                                                                                                                                   "e/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle/BDDC" +
                                                                                                                                   "flowStack.jedd:83,48-57"),
                                                                                                                                  jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().trueBDD()),
                                                                                                                                                               ret)))));
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(ret),
                                              ret.eqUnion(this.stmtsIn(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v() },
                                                                                                           new jedd.PhysicalDomain[] { MT.v() },
                                                                                                           ("this.stmtsIn(methods) at /home/research/ccl/olhota/soot-trun" +
                                                                                                            "k/src/soot/jimple/paddle/BDDCflowStack.jedd:84,31-38"),
                                                                                                           methods)))))
                break;
        }
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                                   new jedd.PhysicalDomain[] { ST.v() },
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                    "/jimple/paddle/BDDCflowStack.jedd:86,8-14"),
                                                   ret);
    }
    
    private final jedd.internal.RelationContainer mustCflow =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                          new jedd.PhysicalDomain[] { ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.stmt> mustCflow = jed" +
                                           "d.internal.Jedd.v().trueBDD() at /home/research/ccl/olhota/s" +
                                           "oot-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:89,12-18"),
                                          jedd.internal.Jedd.v().trueBDD());
    
    private final jedd.internal.RelationContainer mayCflow =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { stmt.v() },
                                          new jedd.PhysicalDomain[] { ST.v() },
                                          ("private <soot.jimple.paddle.bdddomains.stmt> mayCflow = jedd" +
                                           ".internal.Jedd.v().trueBDD() at /home/research/ccl/olhota/so" +
                                           "ot-trunk/src/soot/jimple/paddle/BDDCflowStack.jedd:90,12-18"),
                                          jedd.internal.Jedd.v().trueBDD());
    
    public boolean neverValid(Stmt s) {
        if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(mayCflow), jedd.internal.Jedd.v().trueBDD()))
            mayCflow.eq(this.mayCflow());
        return jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { s },
                                                                                                                                                                new jedd.Attribute[] { stmt.v() },
                                                                                                                                                                new jedd.PhysicalDomain[] { ST.v() })),
                                                                                                     mayCflow,
                                                                                                     new jedd.PhysicalDomain[] { ST.v() })),
                                             jedd.internal.Jedd.v().falseBDD());
    }
    
    public boolean alwaysValid(Stmt s) {
        if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(mustCflow), jedd.internal.Jedd.v().trueBDD()))
            mustCflow.eq(this.mustCflow());
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { s },
                                                                                                                                                                 new jedd.Attribute[] { stmt.v() },
                                                                                                                                                                 new jedd.PhysicalDomain[] { ST.v() })),
                                                                                                      mustCflow,
                                                                                                      new jedd.PhysicalDomain[] { ST.v() })),
                                              jedd.internal.Jedd.v().falseBDD());
    }
}
