package soot.jimple.paddle;

import soot.*;
import soot.util.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;
import jedd.*;

public class BDDTypeManager extends AbsTypeManager {
    BDDTypeManager(Rvar_method_type locals,
                   Rvar_type globals,
                   Robj_method_type localallocs,
                   Robj_type globalallocs,
                   BDDHierarchy fh) {
        super(locals, globals, localallocs, globalallocs);
        this.fh = fh;
    }
    
    final jedd.internal.RelationContainer result =
      new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                          new PhysicalDomain[] { V1.v(), H2.v() },
                                          ("<soot.jimple.paddle.bdddomains.var, soot.jimple.paddle.bdddo" +
                                           "mains.obj> result = jedd.internal.Jedd.v().falseBDD() at /tm" +
                                           "p/olhotak/soot-trunk/src/soot/jimple/paddle/BDDTypeManager.j" +
                                           "edd:37,4-14"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    final jedd.internal.RelationContainer allVars =
      new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                          new PhysicalDomain[] { V1.v(), T1.v() },
                                          ("<soot.jimple.paddle.bdddomains.var, soot.jimple.paddle.bdddo" +
                                           "mains.type> allVars = jedd.internal.Jedd.v().falseBDD() at /" +
                                           "tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDTypeManager" +
                                           ".jedd:38,4-15"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    final jedd.internal.RelationContainer allObjs =
      new jedd.internal.RelationContainer(new Attribute[] { obj.v(), type.v() },
                                          new PhysicalDomain[] { H2.v(), T2.v() },
                                          ("<soot.jimple.paddle.bdddomains.obj, soot.jimple.paddle.bdddo" +
                                           "mains.type> allObjs = jedd.internal.Jedd.v().falseBDD() at /" +
                                           "tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDTypeManager" +
                                           ".jedd:39,4-15"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public boolean update() {
        if (fh == null) {
            result.eq(jedd.internal.Jedd.v().trueBDD());
            return true;
        }
        final jedd.internal.RelationContainer newVars =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                              new PhysicalDomain[] { V1.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.type:soot.jimple.padd" +
                                               "le.bdddomains.T1> newVars; at /tmp/olhotak/soot-trunk/src/so" +
                                               "ot/jimple/paddle/BDDTypeManager.jedd:46,20-27"));
        newVars.eq(jedd.internal.Jedd.v().project(locals.get(), new PhysicalDomain[] { MS.v() }));
        newVars.eqUnion(globals.get());
        allVars.eqUnion(newVars);
        final jedd.internal.RelationContainer newObjs =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), type.v() },
                                              new PhysicalDomain[] { H2.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.obj:soot.jimple.paddle.bdddom" +
                                               "ains.H2, soot.jimple.paddle.bdddomains.type:soot.jimple.padd" +
                                               "le.bdddomains.T2> newObjs; at /tmp/olhotak/soot-trunk/src/so" +
                                               "ot/jimple/paddle/BDDTypeManager.jedd:51,20-27"));
        newObjs.eq(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(jedd.internal.Jedd.v().replace(localallocs.get(),
                                                                                                                new PhysicalDomain[] { T1.v() },
                                                                                                                new PhysicalDomain[] { T2.v() }),
                                                                                 new PhysicalDomain[] { MS.v() }),
                                                  new PhysicalDomain[] { H1.v() },
                                                  new PhysicalDomain[] { H2.v() }));
        newObjs.eqUnion(jedd.internal.Jedd.v().replace(globalallocs.get(),
                                                       new PhysicalDomain[] { H1.v(), T1.v() },
                                                       new PhysicalDomain[] { H2.v(), T2.v() }));
        allObjs.eqUnion(newObjs);
        final jedd.internal.RelationContainer subtypeRelation =
          new jedd.internal.RelationContainer(new Attribute[] { subt.v(), supt.v() },
                                              new PhysicalDomain[] { T2.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.subt:soot.jimple.paddle.bdddo" +
                                               "mains.T2, soot.jimple.paddle.bdddomains.supt:soot.jimple.pad" +
                                               "dle.bdddomains.T1> subtypeRelation = fh.subtypeRelation(); a" +
                                               "t /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDTypeMana" +
                                               "ger.jedd:56,21-36"),
                                              fh.subtypeRelation());
        result.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(subtypeRelation),
                                                                                                                 newVars,
                                                                                                                 new PhysicalDomain[] { T1.v() })),
                                                      allObjs,
                                                      new PhysicalDomain[] { T2.v() }));
        result.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(subtypeRelation),
                                                                                                                 allVars,
                                                                                                                 new PhysicalDomain[] { T1.v() })),
                                                      newObjs,
                                                      new PhysicalDomain[] { T2.v() }));
        return true;
    }
    
    public BitVector get(Type type) { throw new RuntimeException("Not implemented"); }
    
    public jedd.internal.RelationContainer get() {
        update();
        return new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                                   new PhysicalDomain[] { V1.v(), H2.v() },
                                                   ("return result; at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                    "ddle/BDDTypeManager.jedd:66,8-14"),
                                                   result);
    }
    
    public boolean castNeverFails(Type from, Type to) {
        if (fh == null) return true;
        if (to == null) return true;
        if (to == from) return true;
        if (from == null) return false;
        if (to.equals(from)) return true;
        if (to instanceof AnySubType) throw new RuntimeException("oops from=" + from + " to=" + to);
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().falseBDD()),
                                              jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(fh.subtypeRelation()),
                                                                               jedd.internal.Jedd.v().literal(new Object[] { from, to },
                                                                                                              new Attribute[] { subt.v(), supt.v() },
                                                                                                              new PhysicalDomain[] { T2.v(), T1.v() })));
    }
    
    private BDDHierarchy fh;
}
