package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_typeBDD extends Rvar_type {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                          new PhysicalDomain[] { V1.v(), T2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                           "e.bdddomains.V1, soot.jimple.paddle.bdddomains.type:soot.jim" +
                                           "ple.paddle.bdddomains.T2> bdd at /home/olhotak/soot-trunk2/s" +
                                           "rc/soot/jimple/paddle/queue/Rvar_typeBDD.jedd:31,12-29"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rvar_typeBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        this.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                                     new PhysicalDomain[] { V1.v(), T2.v() },
                                                     ("this.add(bdd) at /home/olhotak/soot-trunk2/src/soot/jimple/p" +
                                                      "addle/queue/Rvar_typeBDD.jedd:33,76-79"),
                                                     bdd));
    }
    
    Rvar_typeBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                                          new PhysicalDomain[] { V1.v(), T2.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-" +
                                                           "trunk2/src/soot/jimple/paddle/queue/Rvar_typeBDD.jedd:45,25-" +
                                                           "28"),
                                                          bdd).iterator(new Attribute[] { var.v(), type.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((VarNode) components[0], (Type) components[1]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                              new PhysicalDomain[] { V1.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.type:soot.jimple.padd" +
                                               "le.bdddomains.T2> ret = bdd; at /home/olhotak/soot-trunk2/sr" +
                                               "c/soot/jimple/paddle/queue/Rvar_typeBDD.jedd:55,26-29"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                                   new PhysicalDomain[] { V1.v(), T2.v() },
                                                   ("return ret; at /home/olhotak/soot-trunk2/src/soot/jimple/pad" +
                                                    "dle/queue/Rvar_typeBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
