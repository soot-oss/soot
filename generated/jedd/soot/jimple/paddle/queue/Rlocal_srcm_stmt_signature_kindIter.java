package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rlocal_srcm_stmt_signature_kindIter extends Rlocal_srcm_stmt_signature_kind {
    protected Iterator r;
    
    public Rlocal_srcm_stmt_signature_kindIter(Iterator r) {
        super();
        this.r = r;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            public boolean hasNext() {
                boolean ret = r.hasNext();
                return ret;
            }
            
            public Object next() {
                return new Tuple((Local) r.next(),
                                 (SootMethod) r.next(),
                                 (Unit) r.next(),
                                 (NumberedString) r.next(),
                                 (Kind) r.next());
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { local.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                              new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                              ("<soot.jimple.paddle.bdddomains.local:soot.jimple.paddle.bddd" +
                                               "omains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pa" +
                                               "ddle.bdddomains.T1, soot.jimple.paddle.bdddomains.stmt:soot." +
                                               "jimple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.s" +
                                               "ignature:soot.jimple.paddle.bdddomains.H2, soot.jimple.paddl" +
                                               "e.bdddomains.kind:soot.jimple.paddle.bdddomains.FD> ret = je" +
                                               "dd.internal.Jedd.v().falseBDD(); at /tmp/soot-trunk/src/soot" +
                                               "/jimple/paddle/queue/Rlocal_srcm_stmt_signature_kindIter.jed" +
                                               "d:46,60-63"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (r.hasNext()) {
            ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { r.next(), r.next(), r.next(), r.next(), r.next() },
                                                       new Attribute[] { local.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                                       new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() }));
        }
        return new jedd.internal.RelationContainer(new Attribute[] { kind.v(), local.v(), signature.v(), srcm.v(), stmt.v() },
                                                   new PhysicalDomain[] { FD.v(), V1.v(), H2.v(), T1.v(), ST.v() },
                                                   ("return ret; at /tmp/soot-trunk/src/soot/jimple/paddle/queue/" +
                                                    "Rlocal_srcm_stmt_signature_kindIter.jedd:50,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() { return r.hasNext(); }
}
