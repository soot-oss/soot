package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rsrc_fld_dstIter extends Rsrc_fld_dst {
    protected Iterator r;
    
    public Rsrc_fld_dstIter(Iterator r, String name) {
        super(name);
        this.r = r;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            public boolean hasNext() {
                boolean ret = r.hasNext();
                return ret;
            }
            
            public Object next() { return new Tuple((VarNode) r.next(), (PaddleField) r.next(), (VarNode) r.next()); }
            
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
                                               "le.paddle.bdddomains.V2> ret = jedd.internal.Jedd.v().falseB" +
                                               "DD(); at /tmp/soot-trunk-saved/src/soot/jimple/paddle/queue/" +
                                               "Rsrc_fld_dstIter.jedd:46,33-36"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (r.hasNext()) {
            ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { r.next(), r.next(), r.next() },
                                                       new Attribute[] { src.v(), fld.v(), dst.v() },
                                                       new PhysicalDomain[] { V1.v(), FD.v(), V2.v() }));
        }
        return new jedd.internal.RelationContainer(new Attribute[] { fld.v(), dst.v(), src.v() },
                                                   new PhysicalDomain[] { FD.v(), V2.v(), V1.v() },
                                                   ("return ret; at /tmp/soot-trunk-saved/src/soot/jimple/paddle/" +
                                                    "queue/Rsrc_fld_dstIter.jedd:50,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() { return r.hasNext(); }
}
