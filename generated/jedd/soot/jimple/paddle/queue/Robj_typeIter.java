package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Robj_typeIter extends Robj_type {
    protected Iterator r;
    
    public Robj_typeIter(Iterator r, String name) {
        super(name);
        this.r = r;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            public boolean hasNext() {
                boolean ret = r.hasNext();
                return ret;
            }
            
            public Object next() { return new Tuple((AllocNode) r.next(), (Type) r.next()); }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), type.v() },
                                              new PhysicalDomain[] { H1.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.obj:soot.jimple.paddle.bdddom" +
                                               "ains.H1, soot.jimple.paddle.bdddomains.type:soot.jimple.padd" +
                                               "le.bdddomains.T2> ret = jedd.internal.Jedd.v().falseBDD(); a" +
                                               "t /home/research/ccl/olhota/soot-trunk/src/soot/jimple/paddl" +
                                               "e/queue/Robj_typeIter.jedd:46,26-29"),
                                              jedd.internal.Jedd.v().falseBDD());
        while (r.hasNext()) {
            ret.eqUnion(jedd.internal.Jedd.v().literal(new Object[] { r.next(), r.next() },
                                                       new Attribute[] { obj.v(), type.v() },
                                                       new PhysicalDomain[] { H1.v(), T2.v() }));
        }
        return new jedd.internal.RelationContainer(new Attribute[] { obj.v(), type.v() },
                                                   new PhysicalDomain[] { H1.v(), T2.v() },
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                    "/jimple/paddle/queue/Robj_typeIter.jedd:50,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() { return r.hasNext(); }
}
