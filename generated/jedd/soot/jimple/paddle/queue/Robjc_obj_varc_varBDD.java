package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Robjc_obj_varc_varBDD extends Robjc_obj_varc_var {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v(), varc.v(), var.v() },
                                          new PhysicalDomain[] { C2.v(), H1.v(), C1.v(), V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.objc:soot.jimple.padd" +
                                           "le.bdddomains.C2, soot.jimple.paddle.bdddomains.obj:soot.jim" +
                                           "ple.paddle.bdddomains.H1, soot.jimple.paddle.bdddomains.varc" +
                                           ":soot.jimple.paddle.bdddomains.C1, soot.jimple.paddle.bdddom" +
                                           "ains.var:soot.jimple.paddle.bdddomains.V1> bdd at /home/olho" +
                                           "tak/soot-trunk/src/soot/jimple/paddle/queue/Robjc_obj_varc_v" +
                                           "arBDD.jedd:31,12-46"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Robjc_obj_varc_varBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), obj.v(), varc.v() },
                                                new PhysicalDomain[] { C2.v(), V1.v(), H1.v(), C1.v() },
                                                ("add(bdd) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/" +
                                                 "queue/Robjc_obj_varc_varBDD.jedd:33,102-105"),
                                                bdd));
    }
    
    Robjc_obj_varc_varBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), obj.v(), varc.v() },
                                                          new PhysicalDomain[] { C2.v(), V1.v(), H1.v(), C1.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-" +
                                                           "trunk/src/soot/jimple/paddle/queue/Robjc_obj_varc_varBDD.jed" +
                                                           "d:45,25-28"),
                                                          bdd).iterator(new Attribute[] { objc.v(), obj.v(), varc.v(), var.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((Context) components[0],
                                 (AllocNode) components[1],
                                 (Context) components[2],
                                 (VarNode) components[3]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v(), varc.v(), var.v() },
                                              new PhysicalDomain[] { C2.v(), H1.v(), C1.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.objc:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.obj:soot.jimple.padd" +
                                               "le.bdddomains.H1, soot.jimple.paddle.bdddomains.varc:soot.ji" +
                                               "mple.paddle.bdddomains.C1, soot.jimple.paddle.bdddomains.var" +
                                               ":soot.jimple.paddle.bdddomains.V1> ret = bdd; at /home/olhot" +
                                               "ak/soot-trunk/src/soot/jimple/paddle/queue/Robjc_obj_varc_va" +
                                               "rBDD.jedd:55,43-46"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), obj.v(), varc.v() },
                                                   new PhysicalDomain[] { C2.v(), V1.v(), H1.v(), C1.v() },
                                                   ("return ret; at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                                    "le/queue/Robjc_obj_varc_varBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
