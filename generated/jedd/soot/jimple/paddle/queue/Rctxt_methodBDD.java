package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rctxt_methodBDD extends Rctxt_method {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), method.v() },
                                          new PhysicalDomain[] { C1.v(), MS.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt:soot.jimple.padd" +
                                           "le.bdddomains.C1, soot.jimple.paddle.bdddomains.method:soot." +
                                           "jimple.paddle.bdddomains.MS> bdd at /home/research/ccl/olhot" +
                                           "a/soot-trunk2/src/soot/jimple/paddle/queue/Rctxt_methodBDD.j" +
                                           "edd:31,12-32"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rctxt_methodBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        this.add(new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), method.v() },
                                                     new PhysicalDomain[] { C1.v(), MS.v() },
                                                     ("this.add(bdd) at /home/research/ccl/olhota/soot-trunk2/src/s" +
                                                      "oot/jimple/paddle/queue/Rctxt_methodBDD.jedd:33,82-85"),
                                                     bdd));
    }
    
    Rctxt_methodBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), method.v() },
                                                          new PhysicalDomain[] { C1.v(), MS.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/research/ccl/" +
                                                           "olhota/soot-trunk2/src/soot/jimple/paddle/queue/Rctxt_method" +
                                                           "BDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { ctxt.v(), method.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((Context) components[0], (SootMethod) components[1]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), method.v() },
                                              new PhysicalDomain[] { C1.v(), MS.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.MS> ret = bdd; at /home/research/ccl/olhota" +
                                               "/soot-trunk2/src/soot/jimple/paddle/queue/Rctxt_methodBDD.je" +
                                               "dd:55,29-32"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), method.v() },
                                                   new PhysicalDomain[] { C1.v(), MS.v() },
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk2/src/soo" +
                                                    "t/jimple/paddle/queue/Rctxt_methodBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
