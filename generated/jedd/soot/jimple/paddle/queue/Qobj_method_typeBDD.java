package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qobj_method_typeBDD extends Qobj_method_type {
    public Qobj_method_typeBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(AllocNode _obj, SootMethod _method, Type _type) {
        add(new jedd.internal.RelationContainer(new Attribute[] { obj.v(), method.v(), type.v() },
                                                new PhysicalDomain[] { H1.v(), MS.v(), T1.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /home/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle" +
                                                 "/queue/Qobj_method_typeBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _obj, _method, _type },
                                                                               new Attribute[] { obj.v(), method.v(), type.v() },
                                                                               new PhysicalDomain[] { H1.v(), MS.v(), T1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Robj_method_typeBDD reader = (Robj_method_typeBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { method.v(), type.v(), obj.v() },
                                                           new PhysicalDomain[] { MS.v(), T1.v(), H1.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                            "oot/jimple/paddle/queue/Qobj_method_typeBDD.jedd:40,12-18"),
                                                           in));
        }
    }
    
    public Robj_method_type reader(String rname) {
        Robj_method_type ret = new Robj_method_typeBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
