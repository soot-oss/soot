package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class RvarBDD extends Rvar {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                          new PhysicalDomain[] { V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                           "e.bdddomains.V1> bdd at /tmp/olhotak/soot-trunk/src/soot/jim" +
                                           "ple/paddle/queue/RvarBDD.jedd:31,12-20"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public RvarBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                                new PhysicalDomain[] { V1.v() },
                                                ("add(bdd) at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/q" +
                                                 "ueue/RvarBDD.jedd:33,62-65"),
                                                bdd));
    }
    
    RvarBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                                          new PhysicalDomain[] { V1.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /tmp/olhotak/soot-t" +
                                                           "runk/src/soot/jimple/paddle/queue/RvarBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { var.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((VarNode) components[0]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                              new PhysicalDomain[] { V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1> ret = bdd; at /tmp/olhotak/soot-trunk/src/soot/jimp" +
                                               "le/paddle/queue/RvarBDD.jedd:55,17-20"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                                   new PhysicalDomain[] { V1.v() },
                                                   ("return ret; at /tmp/olhotak/soot-trunk/src/soot/jimple/paddl" +
                                                    "e/queue/RvarBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
