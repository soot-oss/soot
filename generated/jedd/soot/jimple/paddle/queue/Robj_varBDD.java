package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Robj_varBDD extends Robj_var {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v() },
                                          new PhysicalDomain[] { H1.v(), V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj:soot.jimple.paddl" +
                                           "e.bdddomains.H1, soot.jimple.paddle.bdddomains.var:soot.jimp" +
                                           "le.paddle.bdddomains.V1> bdd at /home/olhotak/soot-trunk/src" +
                                           "/soot/jimple/paddle/queue/Robj_varBDD.jedd:31,12-28"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Robj_varBDD(final jedd.internal.RelationContainer bdd) {
        this();
        this.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                                     new PhysicalDomain[] { V1.v(), H1.v() },
                                                     ("this.add(bdd) at /home/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                      "ddle/queue/Robj_varBDD.jedd:33,57-60"),
                                                     bdd));
    }
    
    Robj_varBDD() {
        super();
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
                      new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                                          new PhysicalDomain[] { V1.v(), H1.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-" +
                                                           "trunk/src/soot/jimple/paddle/queue/Robj_varBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { obj.v(), var.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((AllocNode) components[0], (VarNode) components[1]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v() },
                                              new PhysicalDomain[] { H1.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.obj:soot.jimple.paddle.bdddom" +
                                               "ains.H1, soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                               "e.bdddomains.V1> ret = bdd; at /home/olhotak/soot-trunk/src/" +
                                               "soot/jimple/paddle/queue/Robj_varBDD.jedd:55,25-28"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                                   new PhysicalDomain[] { V1.v(), H1.v() },
                                                   ("return ret; at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                                    "le/queue/Robj_varBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
