package soot.jimple.paddle;

import soot.*;
import java.util.*;
import soot.jimple.paddle.bdddomains.*;
import jedd.*;

public class BDDPointsToSet extends PointsToSetReadOnly {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v() },
                                          new PhysicalDomain[] { C2.v(), H1.v() },
                                          ("private final <soot.jimple.paddle.bdddomains.objc, soot.jimp" +
                                           "le.paddle.bdddomains.obj:soot.jimple.paddle.bdddomains.H1> b" +
                                           "dd at /home/olhotak/soot-trunk2/src/soot/jimple/paddle/BDDPo" +
                                           "intsToSet.jedd:30,18-32"));
    
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
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), objc.v() },
                                              new PhysicalDomain[] { H1.v(), C2.v() },
                                              ("bdd.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-" +
                                               "trunk2/src/soot/jimple/paddle/BDDPointsToSet.jedd:44,22-25"),
                                              bdd).iterator(new Attribute[] { objc.v(), obj.v() });
        while (it.hasNext()) {
            Object[] item = (Object[]) it.next();
            v.visit(ContextAllocNode.make((Context) item[0], (AllocNode) item[1]));
        }
        return v.getReturnValue();
    }
    
    public boolean contains(ContextAllocNode n) {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().falseBDD()),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { n },
                                                                                                                                     new Attribute[] { obj.v() },
                                                                                                                                     new PhysicalDomain[] { H1.v() })),
                                                                          bdd,
                                                                          new PhysicalDomain[] { H1.v() }));
    }
}
