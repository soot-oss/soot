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
        this.add(new jedd.internal.RelationContainer(new Attribute[] { obj.v(), method.v(), type.v() },
                                                     new PhysicalDomain[] { H1.v(), MS.v(), T1.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/p" +
                                                      "addle/queue/Qobj_method_typeBDD.jedd:34,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _obj, _method, _type },
                                                                                    new Attribute[] { obj.v(), method.v(), type.v() },
                                                                                    new PhysicalDomain[] { H1.v(), MS.v(), T1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Robj_method_typeBDD reader = (Robj_method_typeBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { type.v(), method.v(), obj.v() },
                                                           new PhysicalDomain[] { T1.v(), MS.v(), H1.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                            "oot/jimple/paddle/queue/Qobj_method_typeBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Robj_method_type reader(String rname) {
        Robj_method_type ret = new Robj_method_typeBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
