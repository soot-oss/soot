package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rvarc_var_objc_objIter extends Rvarc_var_objc_obj {
    protected Iterator r;
    
    public Rvarc_var_objc_objIter(Iterator r, String name) {
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
                return new Tuple((Context) r.next(), (VarNode) r.next(), (Context) r.next(), (AllocNode) r.next());
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                              ("<soot.jimple.paddle.bdddomains.varc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.var:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.objc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.obj" +
                                               ":soot.jimple.paddle.bdddomains.H1> ret = jedd.internal.Jedd." +
                                               "v().falseBDD(); at /home/olhotak/soot-trunk2/src/soot/jimple" +
                                               "/paddle/queue/Rvarc_var_objc_objIter.jedd:46,43-46"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (r.hasNext()) {
            ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { r.next(), r.next(), r.next(), r.next() },
                                                       new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                                       new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() }));
        }
        return new jedd.internal.RelationContainer(new Attribute[] { obj.v(), objc.v(), var.v(), varc.v() },
                                                   new PhysicalDomain[] { H1.v(), C2.v(), V1.v(), C1.v() },
                                                   ("return ret; at /home/olhotak/soot-trunk2/src/soot/jimple/pad" +
                                                    "dle/queue/Rvarc_var_objc_objIter.jedd:50,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() { return r.hasNext(); }
}
