package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rvar_srcm_stmt_dtp_signature_kindIter extends Rvar_srcm_stmt_dtp_signature_kind {
    protected Iterator r;
    
    public Rvar_srcm_stmt_dtp_signature_kindIter(Iterator r, String name) {
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
                return new Tuple((VarNode) r.next(),
                                 (SootMethod) r.next(),
                                 (Unit) r.next(),
                                 (Type) r.next(),
                                 (NumberedString) r.next(),
                                 (Kind) r.next());
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), srcm.v(), stmt.v(), dtp.v(), signature.v(), kind.v() },
                                              new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), T1.v(), SG.v(), KD.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                               "le.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                               "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.dtp" +
                                               ":soot.jimple.paddle.bdddomains.T1, soot.jimple.paddle.bdddom" +
                                               "ains.signature:soot.jimple.paddle.bdddomains.SG, soot.jimple" +
                                               ".paddle.bdddomains.kind:soot.jimple.paddle.bdddomains.KD> re" +
                                               "t = jedd.internal.Jedd.v().falseBDD(); at /home/research/ccl" +
                                               "/olhota/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Rvar" +
                                               "_srcm_stmt_dtp_signature_kindIter.jedd:46,66-69"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (r.hasNext()) {
            ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { r.next(), r.next(), r.next(), r.next(), r.next(), r.next() },
                                                       new Attribute[] { var.v(), srcm.v(), stmt.v(), dtp.v(), signature.v(), kind.v() },
                                                       new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), T1.v(), SG.v(), KD.v() }));
        }
        return new jedd.internal.RelationContainer(new Attribute[] { kind.v(), dtp.v(), srcm.v(), var.v(), signature.v(), stmt.v() },
                                                   new PhysicalDomain[] { KD.v(), T1.v(), MS.v(), V1.v(), SG.v(), ST.v() },
                                                   ("return ret; at /home/research/ccl/olhota/olhotak/soot-trunk/" +
                                                    "src/soot/jimple/paddle/queue/Rvar_srcm_stmt_dtp_signature_ki" +
                                                    "ndIter.jedd:50,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() { return r.hasNext(); }
}
