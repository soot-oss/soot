package soot.jimple.paddle;

import soot.*;
import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public final class BDDHierarchy {
    public jedd.internal.RelationContainer subtypeRelation() {
        update();
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                                   new jedd.PhysicalDomain[] { T2.v(), T1.v() },
                                                   ("return jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v()" +
                                                    ".union(jedd.internal.Jedd.v().read(closure), anySub), new je" +
                                                    "dd.PhysicalDomain[...], new jedd.PhysicalDomain[...]); at /t" +
                                                    "mp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDHierarchy.je" +
                                                    "dd:36,8-14"),
                                                   jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(closure),
                                                                                                               anySub),
                                                                                  new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                                                                  new jedd.PhysicalDomain[] { T2.v(), T1.v() }));
    }
    
    public boolean update() {
        boolean ret = false;
        ret = updateTypes() | ret;
        ret = updateClosure() | ret;
        return ret;
    }
    
    private boolean updateTypes() {
        boolean ret = false;
        ArrayNumberer tn = Scene.v().getTypeNumberer();
        for (int i = maxType + 1; i <= tn.size(); i++) {
            processNewType((Type) tn.get(i));
            ret = true;
        }
        return ret;
    }
    
    private final jedd.internal.RelationContainer identity =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.subt:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.supt:soot.ji" +
                                           "mple.paddle.bdddomains.T2> identity = jedd.internal.Jedd.v()" +
                                           ".falseBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                           "e/BDDHierarchy.jedd:62,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer extend =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.subt:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.supt:soot.ji" +
                                           "mple.paddle.bdddomains.T2> extend = jedd.internal.Jedd.v().f" +
                                           "alseBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/" +
                                           "BDDHierarchy.jedd:67,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public jedd.internal.RelationContainer extend() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                                   new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                                   ("return extend; at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                    "ddle/BDDHierarchy.jedd:68,35-41"),
                                                   extend);
    }
    
    private final jedd.internal.RelationContainer implement =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.subt:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.supt:soot.ji" +
                                           "mple.paddle.bdddomains.T2> implement = jedd.internal.Jedd.v(" +
                                           ").falseBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/padd" +
                                           "le/BDDHierarchy.jedd:73,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer array =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.subt:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.supt:soot.ji" +
                                           "mple.paddle.bdddomains.T2> array = jedd.internal.Jedd.v().fa" +
                                           "lseBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/B" +
                                           "DDHierarchy.jedd:81,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public jedd.internal.RelationContainer array() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                                   new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                                   ("return array; at /tmp/olhotak/soot-trunk/src/soot/jimple/pad" +
                                                    "dle/BDDHierarchy.jedd:82,34-40"),
                                                   array);
    }
    
    private final jedd.internal.RelationContainer arrayElem =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { arrayt.v(), elemt.v() },
                                          new jedd.PhysicalDomain[] { T3.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.arrayt:soot.jimple.pa" +
                                           "ddle.bdddomains.T3, soot.jimple.paddle.bdddomains.elemt> arr" +
                                           "ayElem = jedd.internal.Jedd.v().falseBDD() at /tmp/olhotak/s" +
                                           "oot-trunk/src/soot/jimple/paddle/BDDHierarchy.jedd:84,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer anySub =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { anyst.v(), type.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.anyst:soot.jimple.pad" +
                                           "dle.bdddomains.T1, soot.jimple.paddle.bdddomains.type:soot.j" +
                                           "imple.paddle.bdddomains.T2> anySub = jedd.internal.Jedd.v()." +
                                           "falseBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle" +
                                           "/BDDHierarchy.jedd:89,12-31"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public jedd.internal.RelationContainer anySub() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { anyst.v(), type.v() },
                                                   new jedd.PhysicalDomain[] { T3.v(), T2.v() },
                                                   ("return jedd.internal.Jedd.v().replace(anySub, new jedd.Physi" +
                                                    "calDomain[...], new jedd.PhysicalDomain[...]); at /tmp/olhot" +
                                                    "ak/soot-trunk/src/soot/jimple/paddle/BDDHierarchy.jedd:90,36" +
                                                    "-42"),
                                                   jedd.internal.Jedd.v().replace(anySub,
                                                                                  new jedd.PhysicalDomain[] { T1.v() },
                                                                                  new jedd.PhysicalDomain[] { T3.v() }));
    }
    
    private final jedd.internal.RelationContainer oldAnySub =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { anyst.v(), type.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.anyst, soot.jimple.pa" +
                                           "ddle.bdddomains.type> oldAnySub = jedd.internal.Jedd.v().fal" +
                                           "seBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BD" +
                                           "DHierarchy.jedd:91,12-25"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer nullType =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.subt, soot.jimple.pad" +
                                           "dle.bdddomains.supt> nullType = jedd.internal.Jedd.v().false" +
                                           "BDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDH" +
                                           "ierarchy.jedd:94,12-24"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer closure =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { subt.v(), supt.v() },
                                          new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.subt:soot.jimple.padd" +
                                           "le.bdddomains.T1, soot.jimple.paddle.bdddomains.supt:soot.ji" +
                                           "mple.paddle.bdddomains.T2> closure = jedd.internal.Jedd.v()." +
                                           "falseBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle" +
                                           "/BDDHierarchy.jedd:99,12-30"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer concreteNonArray =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v() },
                                          new jedd.PhysicalDomain[] { T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.type> concreteNonArra" +
                                           "y = jedd.internal.Jedd.v().falseBDD() at /tmp/olhotak/soot-t" +
                                           "runk/src/soot/jimple/paddle/BDDHierarchy.jedd:101,12-18"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer concrete =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v() },
                                          new jedd.PhysicalDomain[] { T3.v() },
                                          ("private <soot.jimple.paddle.bdddomains.type> concrete = jedd" +
                                           ".internal.Jedd.v().falseBDD() at /tmp/olhotak/soot-trunk/src" +
                                           "/soot/jimple/paddle/BDDHierarchy.jedd:102,12-18"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public jedd.internal.RelationContainer concrete() {
        updateTypes();
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { type.v() },
                                                   new jedd.PhysicalDomain[] { T3.v() },
                                                   ("return concrete; at /tmp/olhotak/soot-trunk/src/soot/jimple/" +
                                                    "paddle/BDDHierarchy.jedd:105,8-14"),
                                                   concrete);
    }
    
    private RefType jlo = RefType.v("java.lang.Object");
    
    private RefType jis = RefType.v("java.io.Serializable");
    
    private RefType jlc = RefType.v("java.lang.Cloneable");
    
    private Type array(int dimensions, Type base) {
        Type ret;
        if (dimensions == 0) ret = base; else ret = ArrayType.v(base, dimensions);
        Scene.v().getTypeNumberer().add(ret);
        return ret;
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
            if (sc.isConcrete()) {
                concrete.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { rt },
                                                                new jedd.Attribute[] { type.v() },
                                                                new jedd.PhysicalDomain[] { T3.v() }));
                concreteNonArray.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { rt },
                                                                        new jedd.Attribute[] { type.v() },
                                                                        new jedd.PhysicalDomain[] { T2.v() }));
            }
        } else
            if (t instanceof ArrayType) {
                identity.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { t, t },
                                                                new jedd.Attribute[] { subt.v(), supt.v() },
                                                                new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
                ArrayType at = (ArrayType) t;
                if (at.baseType instanceof PrimType) {
                    array.eqUnion(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { at, array(at.numDimensions -
                                                                                                                                                                                                              1,
                                                                                                                                                                                                            jlo) },
                                                                                                                                                                                   new jedd.Attribute[] { subt.v(), supt.v() },
                                                                                                                                                                                   new jedd.PhysicalDomain[] { T1.v(), T2.v() })),
                                                                                                                        jedd.internal.Jedd.v().literal(new Object[] { at, array(at.numDimensions -
                                                                                                                                                                                  1,
                                                                                                                                                                                jis) },
                                                                                                                                                       new jedd.Attribute[] { subt.v(), supt.v() },
                                                                                                                                                       new jedd.PhysicalDomain[] { T1.v(), T2.v() }))),
                                                               jedd.internal.Jedd.v().literal(new Object[] { at, array(at.numDimensions -
                                                                                                                         1,
                                                                                                                       jlc) },
                                                                                              new jedd.Attribute[] { subt.v(), supt.v() },
                                                                                              new jedd.PhysicalDomain[] { T1.v(), T2.v() })));
                } else
                    if (at.baseType instanceof RefType) {
                        RefType rt = (RefType) at.baseType;
                        if (rt.equals(jlo)) {
                            array.eqUnion(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().literal(new Object[] { at, array(at.numDimensions -
                                                                                                                                                                                                                      1,
                                                                                                                                                                                                                    jlo) },
                                                                                                                                                                                           new jedd.Attribute[] { subt.v(), supt.v() },
                                                                                                                                                                                           new jedd.PhysicalDomain[] { T1.v(), T2.v() })),
                                                                                                                                jedd.internal.Jedd.v().literal(new Object[] { at, array(at.numDimensions -
                                                                                                                                                                                          1,
                                                                                                                                                                                        jis) },
                                                                                                                                                               new jedd.Attribute[] { subt.v(), supt.v() },
                                                                                                                                                               new jedd.PhysicalDomain[] { T1.v(), T2.v() }))),
                                                                       jedd.internal.Jedd.v().literal(new Object[] { at, array(at.numDimensions -
                                                                                                                                 1,
                                                                                                                               jlc) },
                                                                                                      new jedd.Attribute[] { subt.v(), supt.v() },
                                                                                                      new jedd.PhysicalDomain[] { T1.v(), T2.v() })));
                        } else {
                            array.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { at, array(at.numDimensions,
                                                                                                  jlo) },
                                                                         new jedd.Attribute[] { subt.v(), supt.v() },
                                                                         new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
                        }
                    } else
                        throw new RuntimeException("unhandled: " + at.baseType);
                arrayElem.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { at, at.getArrayElementType() },
                                                                 new jedd.Attribute[] { arrayt.v(), elemt.v() },
                                                                 new jedd.PhysicalDomain[] { T3.v(), T2.v() }));
                concrete.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { at },
                                                                new jedd.Attribute[] { type.v() },
                                                                new jedd.PhysicalDomain[] { T3.v() }));
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
        nullType.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { NullType.v(), t },
                                                        new jedd.Attribute[] { subt.v(), supt.v() },
                                                        new jedd.PhysicalDomain[] { T1.v(), T2.v() }));
        if (t.getNumber() > maxType) maxType = t.getNumber();
    }
    
    private boolean updateClosure() {
        boolean ret = false;
        boolean closureChanged =
          !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(closure),
                                         closure.eqUnion(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(extend),
                                                                                                                                                                                                                                                                 implement)),
                                                                                                                                                                                                        array)),
                                                                                                                                               identity)),
                                                                                      nullType)));
        if (closureChanged) {
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
            ret = true;
        }
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(anySub), oldAnySub) || closureChanged) {
            ret = true;
            anySub.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(anySub,
                                                                                                                     new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                     new jedd.PhysicalDomain[] { T3.v() })),
                                                          jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().replace(closure,
                                                                                                                        new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                        new jedd.PhysicalDomain[] { T3.v() }),
                                                                                         new jedd.PhysicalDomain[] { T1.v() },
                                                                                         new jedd.PhysicalDomain[] { T2.v() }),
                                                          new jedd.PhysicalDomain[] { T3.v() }));
            anySub.eq(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(anySub),
                                                  concreteNonArray,
                                                  new jedd.PhysicalDomain[] { T2.v() }));
            closure.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(anySub),
                                                                                          jedd.internal.Jedd.v().replace(closure,
                                                                                                                         new jedd.PhysicalDomain[] { T1.v(), T2.v() },
                                                                                                                         new jedd.PhysicalDomain[] { T2.v(), T3.v() }),
                                                                                          new jedd.PhysicalDomain[] { T2.v() }),
                                                           new jedd.PhysicalDomain[] { T3.v() },
                                                           new jedd.PhysicalDomain[] { T2.v() }));
        }
        oldAnySub.eq(anySub);
        while (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(closure),
                                              closure.eqUnion(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(closure),
                                                                                                                                                                                                                      jedd.internal.Jedd.v().replace(arrayElem,
                                                                                                                                                                                                                                                     new jedd.PhysicalDomain[] { T2.v() },
                                                                                                                                                                                                                                                     new jedd.PhysicalDomain[] { T1.v() }),
                                                                                                                                                                                                                      new jedd.PhysicalDomain[] { T1.v() }),
                                                                                                                                                                                       new jedd.PhysicalDomain[] { T3.v() },
                                                                                                                                                                                       new jedd.PhysicalDomain[] { T1.v() })),
                                                                                                                            arrayElem,
                                                                                                                            new jedd.PhysicalDomain[] { T2.v() }),
                                                                                             new jedd.PhysicalDomain[] { T3.v() },
                                                                                             new jedd.PhysicalDomain[] { T2.v() }))))
            ;
        return ret;
    }
    
    public BDDHierarchy() { super(); }
}
