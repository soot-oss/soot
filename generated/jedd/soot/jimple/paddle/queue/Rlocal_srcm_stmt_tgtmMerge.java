package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rlocal_srcm_stmt_tgtmMerge extends Rlocal_srcm_stmt_tgtm {
    void add(final jedd.internal.RelationContainer tuple) { throw new RuntimeException(); }
    
    private Rlocal_srcm_stmt_tgtm in1;
    
    private Rlocal_srcm_stmt_tgtm in2;
    
    public Rlocal_srcm_stmt_tgtmMerge(Rlocal_srcm_stmt_tgtm in1, Rlocal_srcm_stmt_tgtm in2) {
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
        return new jedd.internal.RelationContainer(new Attribute[] { local.v(), tgtm.v(), srcm.v(), stmt.v() },
                                                   new PhysicalDomain[] { V1.v(), T2.v(), T1.v(), ST.v() },
                                                   ("return jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().r" +
                                                    "ead(in1.get()), in2.get()); at /tmp/soot-trunk/src/soot/jimp" +
                                                    "le/paddle/queue/Rlocal_srcm_stmt_tgtmMerge.jedd:51,8-14"),
                                                   jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(in1.get()),
                                                                                in2.get()));
    }
    
    public boolean hasNext() { return in1.hasNext() || in2.hasNext(); }
}
