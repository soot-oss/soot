package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rsrcc_src_fld_dstc_dstIter extends Rsrcc_src_fld_dstc_dst {
    protected Iterator r;
    
    public Rsrcc_src_fld_dstc_dstIter(Iterator r, String name) {
        super(name);
        this.r = r;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            public boolean hasNext() {
                boolean ret = r.hasNext();
                return ret;
            }
            
            public Object next() {
                return new Tuple((Context) r.next(),
                                 (VarNode) r.next(),
                                 (PaddleField) r.next(),
                                 (Context) r.next(),
                                 (VarNode) r.next());
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.fld:soot.jim" +
                                               "ple.paddle.bdddomains.FD, soot.jimple.paddle.bdddomains.dstc" +
                                               ":soot.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddom" +
                                               "ains.dst:soot.jimple.paddle.bdddomains.V2> ret = jedd.intern" +
                                               "al.Jedd.v().falseBDD(); at /home/research/ccl/olhota/soot-tr" +
                                               "unk2/src/soot/jimple/paddle/queue/Rsrcc_src_fld_dstc_dstIter" +
                                               ".jedd:46,51-54"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (r.hasNext()) {
            ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { r.next(), r.next(), r.next(), r.next(), r.next() },
                                                       new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                                       new PhysicalDomain[] { C1.v(), V1.v(), FD.v(), C2.v(), V2.v() }));
        }
        return new jedd.internal.RelationContainer(new Attribute[] { fld.v(), dstc.v(), src.v(), srcc.v(), dst.v() },
                                                   new PhysicalDomain[] { FD.v(), C2.v(), V1.v(), C1.v(), V2.v() },
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk2/src/soo" +
                                                    "t/jimple/paddle/queue/Rsrcc_src_fld_dstc_dstIter.jedd:50,8-1" +
                                                    "4"),
                                                   ret);
    }
    
    public boolean hasNext() { return r.hasNext(); }
}
