package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrc_fld_dstBDD extends Rsrc_fld_dst {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { src.v(), fld.v(), dst.v() },
                                          new PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.src:soot.jimple.paddl" +
                                           "e.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jimp" +
                                           "le.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:s" +
                                           "oot.jimple.paddle.bdddomains.V2> bdd at /home/research/ccl/o" +
                                           "lhota/soot-trunk/src/soot/jimple/paddle/queue/Rsrc_fld_dstBD" +
                                           "D.jedd:31,12-36"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rsrc_fld_dstBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), src.v(), dst.v() },
                                                new PhysicalDomain[] { FD.v(), V1.v(), V2.v() },
                                                ("add(bdd) at /home/research/ccl/olhota/soot-trunk/src/soot/ji" +
                                                 "mple/paddle/queue/Rsrc_fld_dstBDD.jedd:33,86-89"),
                                                bdd));
    }
    
    Rsrc_fld_dstBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { fld.v(), src.v(), dst.v() },
                                                          new PhysicalDomain[] { FD.v(), V1.v(), V2.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/research/ccl/" +
                                                           "olhota/soot-trunk/src/soot/jimple/paddle/queue/Rsrc_fld_dstB" +
                                                           "DD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { src.v(), fld.v(), dst.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((VarNode) components[0], (PaddleField) components[1], (VarNode) components[2]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { src.v(), fld.v(), dst.v() },
                                              new PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.fld:soot.jimple.paddl" +
                                               "e.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:soot.jimp" +
                                               "le.paddle.bdddomains.V2> ret = bdd; at /home/research/ccl/ol" +
                                               "hota/soot-trunk/src/soot/jimple/paddle/queue/Rsrc_fld_dstBDD" +
                                               ".jedd:55,33-36"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { fld.v(), src.v(), dst.v() },
                                                   new PhysicalDomain[] { FD.v(), V1.v(), V2.v() },
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                    "/jimple/paddle/queue/Rsrc_fld_dstBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
