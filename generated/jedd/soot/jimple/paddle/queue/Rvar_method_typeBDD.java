package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_method_typeBDD extends Rvar_method_type {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { var.v(), method.v(), type.v() },
                                          new PhysicalDomain[] { V1.v(), T1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                           "e.bdddomains.V1, soot.jimple.paddle.bdddomains.method:soot.j" +
                                           "imple.paddle.bdddomains.T1, soot.jimple.paddle.bdddomains.ty" +
                                           "pe:soot.jimple.paddle.bdddomains.T2> bdd at /home/olhotak/so" +
                                           "ot-trunk/src/soot/jimple/paddle/queue/Rvar_method_typeBDD.je" +
                                           "dd:31,12-40"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rvar_method_typeBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        this.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), method.v(), type.v() },
                                                     new PhysicalDomain[] { V1.v(), T1.v(), T2.v() },
                                                     ("this.add(bdd) at /home/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                      "ddle/queue/Rvar_method_typeBDD.jedd:33,94-97"),
                                                     bdd));
    }
    
    Rvar_method_typeBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { var.v(), method.v(), type.v() },
                                                          new PhysicalDomain[] { V1.v(), T1.v(), T2.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-" +
                                                           "trunk/src/soot/jimple/paddle/queue/Rvar_method_typeBDD.jedd:" +
                                                           "45,25-28"),
                                                          bdd).iterator(new Attribute[] { var.v(), method.v(), type.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((VarNode) components[0], (SootMethod) components[1], (Type) components[2]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), method.v(), type.v() },
                                              new PhysicalDomain[] { V1.v(), T1.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.method:soot.jimple.pa" +
                                               "ddle.bdddomains.T1, soot.jimple.paddle.bdddomains.type:soot." +
                                               "jimple.paddle.bdddomains.T2> ret = bdd; at /home/olhotak/soo" +
                                               "t-trunk/src/soot/jimple/paddle/queue/Rvar_method_typeBDD.jed" +
                                               "d:55,37-40"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { var.v(), method.v(), type.v() },
                                                   new PhysicalDomain[] { V1.v(), T1.v(), T2.v() },
                                                   ("return ret; at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                                    "le/queue/Rvar_method_typeBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
