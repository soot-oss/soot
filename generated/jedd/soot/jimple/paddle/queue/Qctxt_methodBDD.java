package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qctxt_methodBDD extends Qctxt_method {
    public Qctxt_methodBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _ctxt, SootMethod _method) {
        this.add(new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), method.v() },
                                                     new PhysicalDomain[] { C1.v(), MS.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/p" +
                                                      "addle/queue/Qctxt_methodBDD.jedd:34,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _ctxt, _method },
                                                                                    new Attribute[] { ctxt.v(), method.v() },
                                                                                    new PhysicalDomain[] { C1.v(), MS.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rctxt_methodBDD reader = (Rctxt_methodBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { method.v(), ctxt.v() },
                                                           new PhysicalDomain[] { MS.v(), C1.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                            "oot/jimple/paddle/queue/Qctxt_methodBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Rctxt_method reader(String rname) {
        Rctxt_method ret = new Rctxt_methodBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
