package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_method_typeBDD extends Qvar_method_type {
    public Qvar_method_typeBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var, SootMethod _method, Type _type) {
        add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), method.v(), type.v() },
                                                new PhysicalDomain[] { V1.v(), MS.v(), T1.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /home/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle" +
                                                 "/queue/Qvar_method_typeBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _var, _method, _type },
                                                                               new Attribute[] { var.v(), method.v(), type.v() },
                                                                               new PhysicalDomain[] { V1.v(), MS.v(), T1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvar_method_typeBDD reader = (Rvar_method_typeBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), method.v(), type.v() },
                                                           new PhysicalDomain[] { V1.v(), MS.v(), T1.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                            "oot/jimple/paddle/queue/Qvar_method_typeBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Rvar_method_type reader(String rname) {
        Rvar_method_type ret = new Rvar_method_typeBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
