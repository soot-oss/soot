package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rvar_srcm_stmt_tgtmIter extends Rvar_srcm_stmt_tgtm {
    protected Iterator r;
    
    public Rvar_srcm_stmt_tgtmIter(Iterator r, String name) {
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
                return new Tuple((VarNode) r.next(), (SootMethod) r.next(), (Unit) r.next(), (SootMethod) r.next());
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), srcm.v(), stmt.v(), tgtm.v() },
                                              new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                               "le.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                               "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.tgt" +
                                               "m:soot.jimple.paddle.bdddomains.T2> ret = jedd.internal.Jedd" +
                                               ".v().falseBDD(); at /home/olhotak/soot-trunk2/src/soot/jimpl" +
                                               "e/paddle/queue/Rvar_srcm_stmt_tgtmIter.jedd:46,44-47"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (r.hasNext()) {
            ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { r.next(), r.next(), r.next(), r.next() },
                                                       new Attribute[] { var.v(), srcm.v(), stmt.v(), tgtm.v() },
                                                       new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() }));
        }
        return new jedd.internal.RelationContainer(new Attribute[] { tgtm.v(), srcm.v(), var.v(), stmt.v() },
                                                   new PhysicalDomain[] { T2.v(), T1.v(), V1.v(), ST.v() },
                                                   ("return ret; at /home/olhotak/soot-trunk2/src/soot/jimple/pad" +
                                                    "dle/queue/Rvar_srcm_stmt_tgtmIter.jedd:50,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() { return r.hasNext(); }
}
