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
                                          new PhysicalDomain[] { V1.v(), T1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                           "e.bdddomains.V1, soot.jimple.paddle.bdddomains.type:soot.jim" +
                                           "ple.paddle.bdddomains.T1> bdd at /tmp/olhotak/soot-trunk/src" +
                                           "/soot/jimple/paddle/queue/Rvar_typeBDD.jedd:31,12-29"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rvar_typeBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                                new PhysicalDomain[] { V1.v(), T1.v() },
                                                ("add(bdd) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/q" +
                                                 "ueue/Rvar_typeBDD.jedd:33,76-79"),
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
                                                          new PhysicalDomain[] { V1.v(), T1.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /tmp/olhotak/soot-t" +
                                                           "runk/src/soot/jimple/paddle/queue/Rvar_typeBDD.jedd:45,25-28"),
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
                                              new PhysicalDomain[] { V1.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.type:soot.jimple.padd" +
                                               "le.bdddomains.T1> ret = bdd; at /tmp/olhotak/soot-trunk/src/" +
                                               "soot/jimple/paddle/queue/Rvar_typeBDD.jedd:55,26-29"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                                   new PhysicalDomain[] { V1.v(), T1.v() },
                                                   ("return ret; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                    "e/queue/Rvar_typeBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
