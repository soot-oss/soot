package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rctxt_var_obj_srcm_stmt_kind_tgtmMerge extends Rctxt_var_obj_srcm_stmt_kind_tgtm {
    void add(final jedd.internal.RelationContainer tuple) { throw new RuntimeException(); }
    
    private Rctxt_var_obj_srcm_stmt_kind_tgtm in1;
    
    private Rctxt_var_obj_srcm_stmt_kind_tgtm in2;
    
    public Rctxt_var_obj_srcm_stmt_kind_tgtmMerge(Rctxt_var_obj_srcm_stmt_kind_tgtm in1,
                                                  Rctxt_var_obj_srcm_stmt_kind_tgtm in2) {
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
        return new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), kind.v(), var.v(), obj.v(), stmt.v(), srcm.v(), tgtm.v() },
                                                   new PhysicalDomain[] { V2.v(), FD.v(), V1.v(), H1.v(), ST.v(), T1.v(), T2.v() },
                                                   ("return jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().r" +
                                                    "ead(in1.get()), in2.get()); at /home/olhotak/soot-trunk2/src" +
                                                    "/soot/jimple/paddle/queue/Rctxt_var_obj_srcm_stmt_kind_tgtmM" +
                                                    "erge.jedd:52,8-14"),
                                                   jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(in1.get()),
                                                                                in2.get()));
    }
    
    public boolean hasNext() { return in1.hasNext() || in2.hasNext(); }
}
