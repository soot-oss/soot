package soot.jimple.paddle;

import soot.*;
import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public final class BDDHierarchy {
    public jedd.internal.RelationContainer subtypeRelation() {
        this.update();
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                                   new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                                   ("return closure; at /home/olhotak/soot-trunk/src/soot/jimple/" +
                                                    "paddle/BDDHierarchy.jedd:36,8-14"),
                                                   closure);
    }
    
    public void update() {
        ArrayNumberer tn = Scene.v().getTypeNumberer();
        for (int i = maxType + 1; i < tn.size(); i++) { this.processNewType((Type) tn.get(i)); }
        this.updateClosure();
    }
    
    private final jedd.internal.RelationContainer identity =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.subt:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.supt:soot.ji" +
                                           "mple.paddle.bdddomains.T2> identity = jedd.internal.Jedd.v()" +
                                           ".falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                           "le/BDDHierarchy.jedd:53,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer extend =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.subt:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.supt:soot.ji" +
                                           "mple.paddle.bdddomains.T2> extend = jedd.internal.Jedd.v().f" +
                                           "alseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/paddle" +
                                           "/BDDHierarchy.jedd:58,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public jedd.internal.RelationContainer extend() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                                   new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                                   ("return extend; at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                                    "addle/BDDHierarchy.jedd:59,35-41"),
                                                   extend);
    }
    
    private final jedd.internal.RelationContainer implement =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.subt:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.supt:soot.ji" +
                                           "mple.paddle.bdddomains.T2> implement = jedd.internal.Jedd.v(" +
                                           ").falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/pad" +
                                           "dle/BDDHierarchy.jedd:64,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer array =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.subt:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.supt:soot.ji" +
                                           "mple.paddle.bdddomains.T2> array = jedd.internal.Jedd.v().fa" +
                                           "lseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/paddle/" +
                                           "BDDHierarchy.jedd:72,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public jedd.internal.RelationContainer array() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                                   new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                                   ("return array; at /home/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                    "ddle/BDDHierarchy.jedd:73,34-40"),
                                                   array);
    }
    
    private final jedd.internal.RelationContainer anySub =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { anyst.v(), type.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.anyst:soot.jimple.pad" +
                                           "dle.bdddomains.T1, soot.jimple.paddle.bdddomains.type:soot.j" +
                                           "imple.paddle.bdddomains.T2> anySub = jedd.internal.Jedd.v()." +
                                           "falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                           "e/BDDHierarchy.jedd:78,12-31"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public jedd.internal.RelationContainer anySub() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { anyst.v(), type.v() },
                                                   new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                                   ("return anySub; at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                                    "addle/BDDHierarchy.jedd:79,36-42"),
                                                   anySub);
    }
    
    private final jedd.internal.RelationContainer closure =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.subt:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.supt:soot.ji" +
                                           "mple.paddle.bdddomains.T2> closure = jedd.internal.Jedd.v()." +
                                           "falseBDD() at /home/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                           "e/BDDHierarchy.jedd:84,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private RefType jlo = RefType.v("java.lang.Object");
    
    private Type jloArray(int dimensions) {
        if (dimensions == 0) return jlo;
        return ArrayType.v(jlo, dimensions);
    }
    
    private int maxType = 0;
    
    private void processNewType(Type t) {
        if (t instanceof RefType) {
            RefType rt = (RefType) t;
            SootClass sc = rt.getSootClass();
            if (sc == null) return;
            identity.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { rt, rt },
                                                            new jedd.Attribute[] { subt.v(), supt.v() },
                                                            new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
            if (sc.hasSuperclass()) {
                extend.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { rt, sc.getSuperclass().getType() },
                                                              new jedd.Attribute[] { subt.v(), supt.v() },
                                                              new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
            }
            for (Iterator ifaceIt = sc.getInterfaces().iterator(); ifaceIt.hasNext(); ) {
                final SootClass iface = (SootClass) ifaceIt.next();
                implement.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { rt, iface.getType() },
                                                                 new jedd.Attribute[] { subt.v(), supt.v() },
                                                                 new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
            }
        } else
            if (t instanceof ArrayType) {
                identity.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { t, t },
                                                                new jedd.Attribute[] { subt.v(), supt.v() },
                                                                new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
                ArrayType at = (ArrayType) t;
                if (at.baseType instanceof PrimType) {
                    array.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { at, this.jloArray(at.numDimensions -
                                                                                                    1) },
                                                                 new jedd.Attribute[] { subt.v(), supt.v() },
                                                                 new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
                } else
                    if (at.baseType instanceof RefType) {
                        RefType rt = (RefType) at.baseType;
                        if (rt.equals(jlo)) {
                            array.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { at, this.jloArray(at.numDimensions -
                                                                                                            1) },
                                                                         new jedd.Attribute[] { subt.v(), supt.v() },
                                                                         new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
                        } else {
                            array.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { at, this.jloArray(at.numDimensions) },
                                                                         new jedd.Attribute[] { subt.v(), supt.v() },
                                                                         new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
                        }
                    } else
                        throw new RuntimeException("unhandled: " + at.baseType);
            } else
                if (t instanceof AnySubType) {
                    AnySubType as = (AnySubType) t;
                    anySub.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { as, as.getBase() },
                                                                  new jedd.Attribute[] { anyst.v(), type.v() },
                                                                  new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
                    anySub.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { as, NullType.v() },
                                                                  new jedd.Attribute[] { anyst.v(), type.v() },
                                                                  new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
                } else
                    if (t instanceof NullType) {
                        identity.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { t, t },
                                                                        new jedd.Attribute[] { subt.v(), supt.v() },
                                                                        new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
                    }
        if (t.getNumber() > maxType) maxType = t.getNumber();
    }
    
    private void updateClosure() {
        closure.eqUnion(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(extend),
                                                                                                                                                                       implement)),
                                                                                                              array)),
                                                     identity));
        while (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(closure),
                                              closure.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(closure),
                                                                                                                            jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().replace(closure,
                                                                                                                                                                                          new jedd.PhysicalDomain[] { T1.v() },
                                                                                                                                                                                          new jedd.PhysicalDomain[] { T3.v() }),
                                                                                                                                                           new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                                                           new jedd.PhysicalDomain[] { T1.v() }),
                                                                                                                            new jedd.PhysicalDomain[] { T1.v() }),
                                                                                             new jedd.PhysicalDomain[] { T3.v() },
                                                                                             new jedd.PhysicalDomain[] { T1.v() }))))
            ;
        anySub.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(anySub,
                                                                                                                 new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                 new jedd.PhysicalDomain[] { T3.v() })),
                                                      jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().replace(closure,
                                                                                                                    new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                    new jedd.PhysicalDomain[] { T3.v() }),
                                                                                     new jedd.PhysicalDomain[] { T1.v() },
                                                                                     new jedd.PhysicalDomain[] { T2.v() }),
                                                      new jedd.PhysicalDomain[] { T3.v() }));
        closure.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(anySub),
                                                                                      jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().replace(closure,
                                                                                                                                                    new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                                                    new jedd.PhysicalDomain[] { T3.v() }),
                                                                                                                     new jedd.PhysicalDomain[] { T1.v() },
                                                                                                                     new jedd.PhysicalDomain[] { T2.v() }),
                                                                                      new jedd.PhysicalDomain[] { T2.v() }),
                                                       new jedd.PhysicalDomain[] { T3.v() },
                                                       new jedd.PhysicalDomain[] { T2.v() }));
    }
    
    public BDDHierarchy() { super(); }
}
