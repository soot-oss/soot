package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrcm_stmt_kind_tgtm_src_dstMerge extends Rsrcm_stmt_kind_tgtm_src_dst {
    void add(final jedd.internal.RelationContainer tuple) { throw new RuntimeException(); }
    
    private Rsrcm_stmt_kind_tgtm_src_dst in1;
    
    private Rsrcm_stmt_kind_tgtm_src_dst in2;
    
    public Rsrcm_stmt_kind_tgtm_src_dstMerge(Rsrcm_stmt_kind_tgtm_src_dst in1, Rsrcm_stmt_kind_tgtm_src_dst in2) {
        super(in1.name + "+" + in2.name);
        this.in1 = in1;
        this.in2 = in2;
    }
    
    public Iterator iterator() {
        ;
        final Iterator it1 = in1.iterator();
        final Iterator it2 = in2.iterator();
        return new Iterator() {
            public boolean hasNext() { return it1.hasNext() || it2.hasNext(); }
            
            public Object next() {
                if (it1.hasNext()) return it1.next();
                return it2.next();
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        return new jedd.internal.RelationContainer(new Attribute[] { dst.v(), tgtm.v(), stmt.v(), src.v(), srcm.v(), kind.v() },
                                                   new PhysicalDomain[] { V2.v(), T2.v(), ST.v(), V1.v(), T1.v(), FD.v() },
                                                   ("return jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().r" +
                                                    "ead(in1.get()), in2.get()); at /home/olhotak/soot-trunk/src/" +
                                                    "soot/jimple/paddle/queue/Rsrcm_stmt_kind_tgtm_src_dstMerge.j" +
                                                    "edd:52,8-14"),
                                                   jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(in1.get()),
                                                                                in2.get()));
    }
    
    public boolean hasNext() { return in1.hasNext() || in2.hasNext(); }
}
