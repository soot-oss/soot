package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rsrc_dst_fldIter extends Rsrc_dst_fld {
    protected Iterator r;
    
    public Rsrc_dst_fldIter(Iterator r, String name) {
        super(name);
        this.r = r;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            public boolean hasNext() {
                boolean ret = r.hasNext();
                return ret;
            }
            
            public Object next() { return new Tuple((VarNode) r.next(), (VarNode) r.next(), (PaddleField) r.next()); }
            
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
                                               "le.paddle.bdddomains.FD> ret = jedd.internal.Jedd.v().falseB" +
                                               "DD(); at /home/research/ccl/olhota/soot-trunk/src/soot/jimpl" +
                                               "e/paddle/queue/Rsrc_dst_fldIter.jedd:46,33-36"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (r.hasNext()) {
            ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { r.next(), r.next(), r.next() },
                                                       new Attribute[] { src.v(), dst.v(), fld.v() },
                                                       new PhysicalDomain[] { V1.v(), V2.v(), FD.v() }));
        }
        return new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), fld.v() },
                                                   new PhysicalDomain[] { V1.v(), V2.v(), FD.v() },
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                    "/jimple/paddle/queue/Rsrc_dst_fldIter.jedd:50,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() { return r.hasNext(); }
}
