package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrc_dstBDD extends Rsrc_dst {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v() },
                                          new PhysicalDomain[] { V1.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.src:soot.jimple.paddl" +
                                           "e.bdddomains.V1, soot.jimple.paddle.bdddomains.dst:soot.jimp" +
                                           "le.paddle.bdddomains.V2> bdd at /home/research/ccl/olhota/ol" +
                                           "hotak/soot-trunk/src/soot/jimple/paddle/queue/Rsrc_dstBDD.je" +
                                           "dd:31,12-28"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rsrc_dstBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { dst.v(), src.v() },
                                                new PhysicalDomain[] { V2.v(), V1.v() },
                                                ("add(bdd) at /home/research/ccl/olhota/olhotak/soot-trunk/src" +
                                                 "/soot/jimple/paddle/queue/Rsrc_dstBDD.jedd:33,74-77"),
                                                bdd));
    }
    
    Rsrc_dstBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { dst.v(), src.v() },
                                                          new PhysicalDomain[] { V2.v(), V1.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/research/ccl/" +
                                                           "olhota/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Rsrc_" +
                                                           "dstBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { src.v(), dst.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((VarNode) components[0], (VarNode) components[1]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v() },
                                              new PhysicalDomain[] { V1.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.dst:soot.jimple.paddl" +
                                               "e.bdddomains.V2> ret = bdd; at /home/research/ccl/olhota/olh" +
                                               "otak/soot-trunk/src/soot/jimple/paddle/queue/Rsrc_dstBDD.jed" +
                                               "d:55,25-28"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { dst.v(), src.v() },
                                                   new PhysicalDomain[] { V2.v(), V1.v() },
                                                   ("return ret; at /home/research/ccl/olhota/olhotak/soot-trunk/" +
                                                    "src/soot/jimple/paddle/queue/Rsrc_dstBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
