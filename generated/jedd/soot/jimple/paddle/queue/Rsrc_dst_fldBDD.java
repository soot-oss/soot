package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrc_dst_fldBDD extends Rsrc_dst_fld {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), fld.v() },
                                          new PhysicalDomain[] { V1.v(), V2.v(), FD.v() },
                                          ("private <soot.jimple.paddle.bdddomains.src:soot.jimple.paddl" +
                                           "e.bdddomains.V1, soot.jimple.paddle.bdddomains.dst:soot.jimp" +
                                           "le.paddle.bdddomains.V2, soot.jimple.paddle.bdddomains.fld:s" +
                                           "oot.jimple.paddle.bdddomains.FD> bdd at /home/research/ccl/o" +
                                           "lhota/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Rsrc_d" +
                                           "st_fldBDD.jedd:31,12-36"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rsrc_dst_fldBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { dst.v(), src.v(), fld.v() },
                                                new PhysicalDomain[] { V2.v(), V1.v(), FD.v() },
                                                ("add(bdd) at /home/research/ccl/olhota/olhotak/soot-trunk/src" +
                                                 "/soot/jimple/paddle/queue/Rsrc_dst_fldBDD.jedd:33,86-89"),
                                                bdd));
    }
    
    Rsrc_dst_fldBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { dst.v(), src.v(), fld.v() },
                                                          new PhysicalDomain[] { V2.v(), V1.v(), FD.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/research/ccl/" +
                                                           "olhota/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Rsrc_" +
                                                           "dst_fldBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { src.v(), dst.v(), fld.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((VarNode) components[0], (VarNode) components[1], (PaddleField) components[2]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), fld.v() },
                                              new PhysicalDomain[] { V1.v(), V2.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.dst:soot.jimple.paddl" +
                                               "e.bdddomains.V2, soot.jimple.paddle.bdddomains.fld:soot.jimp" +
                                               "le.paddle.bdddomains.FD> ret = bdd; at /home/research/ccl/ol" +
                                               "hota/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Rsrc_ds" +
                                               "t_fldBDD.jedd:55,33-36"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { dst.v(), src.v(), fld.v() },
                                                   new PhysicalDomain[] { V2.v(), V1.v(), FD.v() },
                                                   ("return ret; at /home/research/ccl/olhota/olhotak/soot-trunk/" +
                                                    "src/soot/jimple/paddle/queue/Rsrc_dst_fldBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
