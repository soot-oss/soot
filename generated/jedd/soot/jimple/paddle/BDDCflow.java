package soot.jimple.paddle;

import soot.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public class BDDCflow {
    private AbsCallGraph cg;
    
    BDDCflow(AbsCallGraph cg) {
        super();
        this.cg = cg;
    }
    
    private final jedd.internal.RelationContainer entryPoints =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v() },
                                          new jedd.PhysicalDomain[] { T1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.method> entryPoints =" +
                                           " jedd.internal.Jedd.v().falseBDD() at /home/olhotak/soot-tru" +
                                           "nk/src/soot/jimple/paddle/BDDCflow.jedd:36,12-20"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public void addEntryPoints(Collection c) {
        for (Iterator mIt = c.iterator(); mIt.hasNext(); ) {
            final MethodOrMethodContext m = (MethodOrMethodContext) mIt.next();
            this.addEntryPoint(m);
        }
    }
    
    public void addEntryPoint(MethodOrMethodContext m) {
        entryPoints.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { m.method() },
                                                           new jedd.Attribute[] { method.v() },
                                                           new jedd.PhysicalDomain[] { T1.v() }));
    }
    
    private final jedd.internal.RelationContainer mayCflow =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.tgtm:soot.ji" +
                                           "mple.paddle.bdddomains.T2> mayCflow = jedd.internal.Jedd.v()" +
                                           ".falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                           "le/BDDCflow.jedd:49,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer mustCflow =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.tgtm:soot.ji" +
                                           "mple.paddle.bdddomains.T2> mustCflow = jedd.internal.Jedd.v(" +
                                           ").trueBDD() at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                           "le/BDDCflow.jedd:51,12-30"),
                                          jedd.internal.Jedd.v().trueBDD());
    
    private final jedd.internal.RelationContainer notMustPreds =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), tgtm.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.tgtm:soot.ji" +
                                           "mple.paddle.bdddomains.T2> notMustPreds = jedd.internal.Jedd" +
                                           ".v().falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/" +
                                           "paddle/BDDCflow.jedd:52,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public void update() {
        cg.update();
        final jedd.internal.RelationContainer edges =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), V2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.FD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.V2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.T2> edges" +
                                               " = cg.edges().get(); at /home/olhotak/soot-trunk/src/soot/ji" +
                                               "mple/paddle/BDDCflow.jedd:56,45-50"),
                                              cg.edges().get());
        final jedd.internal.RelationContainer wantedKinds =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { kind.v() },
                                              new jedd.PhysicalDomain[] { FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.kind:soot.jimple.paddle.bdddo" +
                                               "mains.FD> wantedKinds = jedd.internal.Jedd.v().trueBDD(); at" +
                                               " /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflow.je" +
                                               "dd:58,15-26"),
                                              jedd.internal.Jedd.v().trueBDD());
        wantedKinds.eqMinus(jedd.internal.Jedd.v().literal(new Object[] { Kind.THREAD },
                                                           new jedd.Attribute[] { kind.v() },
                                                           new jedd.PhysicalDomain[] { FD.v() }));
        final jedd.internal.RelationContainer wantedEdges =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcm:soot.jimple.paddle.bdddo" +
                                               "mains.T1, soot.jimple.paddle.bdddomains.tgtm:soot.jimple.pad" +
                                               "dle.bdddomains.T2> wantedEdges = jedd.internal.Jedd.v().comp" +
                                               "ose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().proje" +
                                               "ct(edges, new jedd.PhysicalDomain[...])), wantedKinds, new j" +
                                               "edd.PhysicalDomain[...]); at /home/olhotak/soot-trunk/src/so" +
                                               "ot/jimple/paddle/BDDCflow.jedd:61,27-38"),
                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().project(edges,
                                                                                                                                        new jedd.PhysicalDomain[] { V2.v(), ST.v(), V1.v() })),
                                                                             wantedKinds,
                                                                             new jedd.PhysicalDomain[] { FD.v() }));
        final jedd.internal.RelationContainer edgeClosure =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { T2.v(), T3.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcm:soot.jimple.paddle.bdddo" +
                                               "mains.T2, soot.jimple.paddle.bdddomains.tgtm:soot.jimple.pad" +
                                               "dle.bdddomains.T3> edgeClosure = jedd.internal.Jedd.v().repl" +
                                               "ace(wantedEdges, new jedd.PhysicalDomain[...], new jedd.Phys" +
                                               "icalDomain[...]); at /home/olhotak/soot-trunk/src/soot/jimpl" +
                                               "e/paddle/BDDCflow.jedd:64,27-38"),
                                              jedd.internal.Jedd.v().replace(wantedEdges,
                                                                             new jedd.PhysicalDomain[] { T2.v(), T1.v() },
                                                                             new jedd.PhysicalDomain[] { T3.v(), T2.v() }));
        while (true) {
            final jedd.internal.RelationContainer oldClosure =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), tgtm.v() },
                                                  new jedd.PhysicalDomain[] { T2.v(), T3.v() },
                                                  ("<soot.jimple.paddle.bdddomains.srcm:soot.jimple.paddle.bdddo" +
                                                   "mains.T2, soot.jimple.paddle.bdddomains.tgtm:soot.jimple.pad" +
                                                   "dle.bdddomains.T3> oldClosure = edgeClosure; at /home/olhota" +
                                                   "k/soot-trunk/src/soot/jimple/paddle/BDDCflow.jedd:68,25-35"),
                                                  edgeClosure);
            edgeClosure.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(edgeClosure,
                                                                                                                          new jedd.PhysicalDomain[] { T3.v() },
                                                                                                                          new jedd.PhysicalDomain[] { T1.v() })),
                                                               jedd.internal.Jedd.v().replace(edgeClosure,
                                                                                              new jedd.PhysicalDomain[] { T2.v() },
                                                                                              new jedd.PhysicalDomain[] { T1.v() }),
                                                               new jedd.PhysicalDomain[] { T1.v() }));
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(edgeClosure), oldClosure)) break;
            System.out.println("edgeClosure has size: " +
                               new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), srcm.v() },
                                                                   new jedd.PhysicalDomain[] { T3.v(), T2.v() },
                                                                   ("edgeClosure.size() at /home/olhotak/soot-trunk/src/soot/jimp" +
                                                                    "le/paddle/BDDCflow.jedd:71,57-68"),
                                                                   edgeClosure).size());
            System.out.println("edgeClosure has nodes: " +
                               new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), srcm.v() },
                                                                   new jedd.PhysicalDomain[] { T3.v(), T2.v() },
                                                                   ("edgeClosure.numNodes() at /home/olhotak/soot-trunk/src/soot/" +
                                                                    "jimple/paddle/BDDCflow.jedd:72,58-69"),
                                                                   edgeClosure).numNodes());
        }
        mayCflow.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(edgeClosure,
                                                                                                                new jedd.PhysicalDomain[] { T3.v(), T2.v() },
                                                                                                                new jedd.PhysicalDomain[] { T2.v(), T1.v() })),
                                                     entryPoints,
                                                     new jedd.PhysicalDomain[] { T1.v() }));
        while (true) {
            final jedd.internal.RelationContainer oldMayCflow =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), tgtm.v() },
                                                  new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                                  ("<soot.jimple.paddle.bdddomains.srcm:soot.jimple.paddle.bdddo" +
                                                   "mains.T1, soot.jimple.paddle.bdddomains.tgtm:soot.jimple.pad" +
                                                   "dle.bdddomains.T2> oldMayCflow = mayCflow; at /home/olhotak/" +
                                                   "soot-trunk/src/soot/jimple/paddle/BDDCflow.jedd:78,25-36"),
                                                  mayCflow);
            mayCflow.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(edgeClosure,
                                                                                                                                                   new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                                                   new jedd.PhysicalDomain[] { T1.v() })),
                                                                                        jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(mayCflow,
                                                                                                                                                      new jedd.PhysicalDomain[] { T1.v() }),
                                                                                                                       new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                       new jedd.PhysicalDomain[] { T1.v() }),
                                                                                        new jedd.PhysicalDomain[] { T1.v() }),
                                                            new jedd.PhysicalDomain[] { T3.v() },
                                                            new jedd.PhysicalDomain[] { T2.v() }));
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(mayCflow), oldMayCflow)) break;
            System.out.println("mayCflow has size: " +
                               new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), srcm.v() },
                                                                   new jedd.PhysicalDomain[] { T2.v(), T1.v() },
                                                                   ("mayCflow.size() at /home/olhotak/soot-trunk/src/soot/jimple/" +
                                                                    "paddle/BDDCflow.jedd:81,54-62"),
                                                                   mayCflow).size());
            System.out.println("mayCflow has nodes: " +
                               new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), srcm.v() },
                                                                   new jedd.PhysicalDomain[] { T2.v(), T1.v() },
                                                                   ("mayCflow.numNodes() at /home/olhotak/soot-trunk/src/soot/jim" +
                                                                    "ple/paddle/BDDCflow.jedd:82,55-63"),
                                                                   mayCflow).numNodes());
        }
        final jedd.internal.RelationContainer allSources =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v() },
                                              new jedd.PhysicalDomain[] { T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcm:soot.jimple.paddle.bdddo" +
                                               "mains.T1> allSources = jedd.internal.Jedd.v().trueBDD(); at " +
                                               "/home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflow.jed" +
                                               "d:112,15-25"),
                                              jedd.internal.Jedd.v().trueBDD());
        final jedd.internal.RelationContainer identity =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcm:soot.jimple.paddle.bdddo" +
                                               "mains.T1, soot.jimple.paddle.bdddomains.tgtm:soot.jimple.pad" +
                                               "dle.bdddomains.T2> identity = jedd.internal.Jedd.v().copy(al" +
                                               "lSources, new jedd.PhysicalDomain[...], new jedd.PhysicalDom" +
                                               "ain[...]); at /home/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                               "e/BDDCflow.jedd:113,21-29"),
                                              jedd.internal.Jedd.v().copy(allSources,
                                                                          new jedd.PhysicalDomain[] { T1.v() },
                                                                          new jedd.PhysicalDomain[] { T2.v() }));
        notMustPreds.eqUnion(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(entryPoints,
                                                                                                                    new jedd.PhysicalDomain[] { T1.v() },
                                                                                                                    new jedd.PhysicalDomain[] { T2.v() })),
                                                         allSources,
                                                         new jedd.PhysicalDomain[] {  }));
        while (true) {
            final jedd.internal.RelationContainer oldNotMustPreds =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { srcm.v(), tgtm.v() },
                                                  new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                                  ("<soot.jimple.paddle.bdddomains.srcm:soot.jimple.paddle.bdddo" +
                                                   "mains.T1, soot.jimple.paddle.bdddomains.tgtm:soot.jimple.pad" +
                                                   "dle.bdddomains.T2> oldNotMustPreds = notMustPreds; at /home/" +
                                                   "olhotak/soot-trunk/src/soot/jimple/paddle/BDDCflow.jedd:117," +
                                                   "25-40"),
                                                  notMustPreds);
            notMustPreds.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(wantedEdges,
                                                                                                                           new jedd.PhysicalDomain[] { T1.v() },
                                                                                                                           new jedd.PhysicalDomain[] { T3.v() })),
                                                                jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(notMustPreds),
                                                                                                                            identity),
                                                                                               new jedd.PhysicalDomain[] { T2.v() },
                                                                                               new jedd.PhysicalDomain[] { T3.v() }),
                                                                new jedd.PhysicalDomain[] { T3.v() }));
            if (jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(oldNotMustPreds), notMustPreds)) break;
            System.out.println("notMustPreds has size: " +
                               new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), srcm.v() },
                                                                   new jedd.PhysicalDomain[] { T2.v(), T1.v() },
                                                                   ("notMustPreds.size() at /home/olhotak/soot-trunk/src/soot/jim" +
                                                                    "ple/paddle/BDDCflow.jedd:120,58-70"),
                                                                   notMustPreds).size());
            System.out.println("notMustPreds has nodes: " +
                               new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), srcm.v() },
                                                                   new jedd.PhysicalDomain[] { T2.v(), T1.v() },
                                                                   ("notMustPreds.numNodes() at /home/olhotak/soot-trunk/src/soot" +
                                                                    "/jimple/paddle/BDDCflow.jedd:121,59-71"),
                                                                   notMustPreds).numNodes());
        }
        mustCflow.eq(jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().trueBDD()),
                                                  notMustPreds));
        System.out.println("mustCflow has size: " +
                           new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), srcm.v() },
                                                               new jedd.PhysicalDomain[] { T2.v(), T1.v() },
                                                               ("mustCflow.size() at /home/olhotak/soot-trunk/src/soot/jimple" +
                                                                "/paddle/BDDCflow.jedd:125,51-60"),
                                                               mustCflow).size());
        System.out.println("mustCflow has nodes: " +
                           new jedd.internal.RelationContainer(new jedd.Attribute[] { tgtm.v(), srcm.v() },
                                                               new jedd.PhysicalDomain[] { T2.v(), T1.v() },
                                                               ("mustCflow.numNodes() at /home/olhotak/soot-trunk/src/soot/ji" +
                                                                "mple/paddle/BDDCflow.jedd:126,52-61"),
                                                               mustCflow).numNodes());
    }
    
    public boolean mustCflow(SootMethod src, SootMethod tgt) {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { src, tgt },
                                                                                                                                                                      new jedd.Attribute[] { srcm.v(), tgtm.v() },
                                                                                                                                                                      new jedd.PhysicalDomain[] { T1.v(), T2.v() })),
                                                                                                           mustCflow)),
                                              jedd.internal.Jedd.v().falseBDD());
    }
    
    public boolean mayCflow(SootMethod src, SootMethod tgt) {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { src, tgt },
                                                                                                                                                                      new jedd.Attribute[] { srcm.v(), tgtm.v() },
                                                                                                                                                                      new jedd.PhysicalDomain[] { T1.v(), T2.v() })),
                                                                                                           mayCflow)),
                                              jedd.internal.Jedd.v().falseBDD());
    }
}
