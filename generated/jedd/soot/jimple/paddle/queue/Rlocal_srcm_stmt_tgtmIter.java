package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rlocal_srcm_stmt_tgtmIter extends Rlocal_srcm_stmt_tgtm {
    protected Iterator r;
    
    public Rlocal_srcm_stmt_tgtmIter(Iterator r, String name) {
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
                return new Tuple((Local) r.next(), (SootMethod) r.next(), (Unit) r.next(), (SootMethod) r.next());
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { local.v(), srcm.v(), stmt.v(), tgtm.v() },
                                              new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.local:soot.jimple.paddle.bddd" +
                                               "omains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pa" +
                                               "ddle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot." +
                                               "jimple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.t" +
                                               "gtm:soot.jimple.paddle.bdddomains.T2> ret = jedd.internal.Je" +
                                               "dd.v().falseBDD(); at /home/olhotak/soot-trunk/src/soot/jimp" +
                                               "le/paddle/queue/Rlocal_srcm_stmt_tgtmIter.jedd:46,46-49"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (r.hasNext()) {
            ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { r.next(), r.next(), r.next(), r.next() },
                                                       new Attribute[] { local.v(), srcm.v(), stmt.v(), tgtm.v() },
                                                       new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() }));
        }
        return new jedd.internal.RelationContainer(new Attribute[] { stmt.v(), srcm.v(), local.v(), tgtm.v() },
                                                   new PhysicalDomain[] { ST.v(), T1.v(), V1.v(), T2.v() },
                                                   ("return ret; at /home/olhotak/soot-trunk/src/soot/jimple/padd" +
                                                    "le/queue/Rlocal_srcm_stmt_tgtmIter.jedd:50,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() { return r.hasNext(); }
}
