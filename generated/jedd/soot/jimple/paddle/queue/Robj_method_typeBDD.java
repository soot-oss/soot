package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Robj_method_typeBDD extends Robj_method_type {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { obj.v(), method.v(), type.v() },
                                          new PhysicalDomain[] { H1.v(), MS.v(), T1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                           "e.bdddomains.H1, soot.jimple.paddle.bdddomains.method:soot.j" +
                                           "imple.paddle.bdddomains.MS, soot.jimple.paddle.bdddomains.ty" +
                                           "pe:soot.jimple.paddle.bdddomains.T1> bdd at /home/research/c" +
                                           "cl/olhota/soot-trunk2/src/soot/jimple/paddle/queue/Robj_meth" +
                                           "od_typeBDD.jedd:31,12-40"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Robj_method_typeBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        this.add(new jedd.internal.RelationContainer(new Attribute[] { type.v(), method.v(), obj.v() },
                                                     new PhysicalDomain[] { T1.v(), MS.v(), H1.v() },
                                                     ("this.add(bdd) at /home/research/ccl/olhota/soot-trunk2/src/s" +
                                                      "oot/jimple/paddle/queue/Robj_method_typeBDD.jedd:33,94-97"),
                                                     bdd));
    }
    
    Robj_method_typeBDD(String name) {
        super(name);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
    }
    
    public Iterator iterator() {
        ;
        return new Iterator() {
            private Iterator it;
            
            public boolean hasNext() {
                if (it != null && it.hasNext()) return true;
                if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD()))
                    return true;
                return false;
            }
            
            public Object next() {
                if (it == null || !it.hasNext()) {
                    it =
                      new jedd.internal.RelationContainer(new Attribute[] { type.v(), method.v(), obj.v() },
                                                          new PhysicalDomain[] { T1.v(), MS.v(), H1.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/research/ccl/" +
                                                           "olhota/soot-trunk2/src/soot/jimple/paddle/queue/Robj_method_" +
                                                           "typeBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { obj.v(), method.v(), type.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((AllocNode) components[0], (SootMethod) components[1], (Type) components[2]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), method.v(), type.v() },
                                              new PhysicalDomain[] { H1.v(), MS.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.obj:soot.jimple.paddle.bdddom" +
                                               "ains.H1, soot.jimple.paddle.bdddomains.method:soot.jimple.pa" +
                                               "ddle.bdddomains.MS, soot.jimple.paddle.bdddomains.type:soot." +
                                               "jimple.paddle.bdddomains.T1> ret = bdd; at /home/research/cc" +
                                               "l/olhota/soot-trunk2/src/soot/jimple/paddle/queue/Robj_metho" +
                                               "d_typeBDD.jedd:55,37-40"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { type.v(), method.v(), obj.v() },
                                                   new PhysicalDomain[] { T1.v(), MS.v(), H1.v() },
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk2/src/soo" +
                                                    "t/jimple/paddle/queue/Robj_method_typeBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
