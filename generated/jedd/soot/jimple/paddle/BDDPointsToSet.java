package soot.jimple.paddle;

import soot.*;
import java.util.*;
import soot.jimple.paddle.bdddomains.*;

public class BDDPointsToSet extends PointsToSetReadOnly {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v() },
                                          new jedd.PhysicalDomain[] { H1.v() },
                                          ("private final <soot.jimple.paddle.bdddomains.obj:soot.jimple" +
                                           ".paddle.bdddomains.H1> bdd at /tmp/soot-trunk/src/soot/jimpl" +
                                           "e/paddle/BDDPointsToSet.jedd:29,18-26"));
    
    public BDDPointsToSet(final jedd.internal.RelationContainer bdd) {
        super(null);
        this.bdd.eq(bdd);
    }
    
    public boolean isEmpty() {
        return jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
    
    public boolean hasNonEmptyIntersection(PointsToSet other) {
        BDDPointsToSet o = (BDDPointsToSet) other;
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(bdd),
                                                                                                           o.bdd)),
                                              jedd.internal.Jedd.v().falseBDD());
    }
    
    public boolean forall(P2SetVisitor v) {
        Iterator it =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v() },
                                              new jedd.PhysicalDomain[] { H1.v() },
                                              ("bdd.iterator() at /tmp/soot-trunk/src/soot/jimple/paddle/BDD" +
                                               "PointsToSet.jedd:43,22-25"),
                                              bdd).iterator();
        while (it.hasNext()) {
            AllocNode an = (AllocNode) it.next();
            v.visit(an);
        }
        return v.getReturnValue();
    }
    
    public boolean contains(Node n) {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().falseBDD()),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { n },
                                                                                                                                     new jedd.Attribute[] { obj.v() },
                                                                                                                                     new jedd.PhysicalDomain[] { H1.v() })),
                                                                          bdd,
                                                                          new jedd.PhysicalDomain[] { H1.v() }));
    }
}
