package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rctxt_local_obj_srcm_stmt_kind_tgtmMerge extends Rctxt_local_obj_srcm_stmt_kind_tgtm {
    void add(final jedd.internal.RelationContainer tuple) { throw new RuntimeException(); }
    
    private Rctxt_local_obj_srcm_stmt_kind_tgtm in1;
    
    private Rctxt_local_obj_srcm_stmt_kind_tgtm in2;
    
    public Rctxt_local_obj_srcm_stmt_kind_tgtmMerge(Rctxt_local_obj_srcm_stmt_kind_tgtm in1,
                                                    Rctxt_local_obj_srcm_stmt_kind_tgtm in2) {
        super();
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
        return new jedd.internal.RelationContainer(new Attribute[] { stmt.v(), local.v(), srcm.v(), ctxt.v(), kind.v(), tgtm.v(), obj.v() },
                                                   new PhysicalDomain[] { ST.v(), V1.v(), T1.v(), V2.v(), FD.v(), T2.v(), H1.v() },
                                                   ("return jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().r" +
                                                    "ead(in1.get()), in2.get()); at /home/olhotak/soot-trunk/src/" +
                                                    "soot/jimple/paddle/queue/Rctxt_local_obj_srcm_stmt_kind_tgtm" +
                                                    "Merge.jedd:51,8-14"),
                                                   jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(in1.get()),
                                                                                in2.get()));
    }
    
    public boolean hasNext() { return in1.hasNext() || in2.hasNext(); }
}
