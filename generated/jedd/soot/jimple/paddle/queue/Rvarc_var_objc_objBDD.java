package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvarc_var_objc_objBDD extends Rvarc_var_objc_obj {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                          new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.varc:soot.jimple.padd" +
                                           "le.bdddomains.C1, soot.jimple.paddle.bdddomains.var:soot.jim" +
                                           "ple.paddle.bdddomains.V1, soot.jimple.paddle.bdddomains.objc" +
                                           ":soot.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddom" +
                                           "ains.obj:soot.jimple.paddle.bdddomains.H1> bdd at /home/olho" +
                                           "tak/soot-trunk/src/soot/jimple/paddle/queue/Rvarc_var_objc_o" +
                                           "bjBDD.jedd:31,12-46"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rvarc_var_objc_objBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        this.add(new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v(), objc.v(), varc.v() },
                                                     new PhysicalDomain[] { H1.v(), V1.v(), C2.v(), C1.v() },
                                                     ("this.add(bdd) at /home/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                      "ddle/queue/Rvarc_var_objc_objBDD.jedd:33,102-105"),
                                                     bdd));
    }
    
    Rvarc_var_objc_objBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v(), objc.v(), varc.v() },
                                                          new PhysicalDomain[] { H1.v(), V1.v(), C2.v(), C1.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-" +
                                                           "trunk/src/soot/jimple/paddle/queue/Rvarc_var_objc_objBDD.jed" +
                                                           "d:45,25-28"),
                                                          bdd).iterator(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((Context) components[0],
                                 (VarNode) components[1],
                                 (Context) components[2],
                                 (AllocNode) components[3]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ret = bdd; at /home/olhot" +
                                               "ak/soot-trunk/src/soot/jimple/paddle/queue/Rvarc_var_objc_ob" +
                                               "jBDD.jedd:55,43-46"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v(), objc.v(), varc.v() },
                                                   new PhysicalDomain[] { H1.v(), V1.v(), C2.v(), C1.v() },
                                                   ("return ret; at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                                    "le/queue/Rvarc_var_objc_objBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
