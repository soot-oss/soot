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
                                                 " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Qvar_m" +
                                                 "ethod_typeBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _var, _method, _type },
                                                                               new Attribute[] { var.v(), method.v(), type.v() },
                                                                               new PhysicalDomain[] { V1.v(), MS.v(), T1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvar_method_typeBDD reader = (Rvar_method_typeBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { method.v(), var.v(), type.v() },
                                                           new PhysicalDomain[] { MS.v(), V1.v(), T1.v() },
                                                           ("reader.add(in) at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                            "ddle/queue/Qvar_method_typeBDD.jedd:40,12-18"),
                                                           in));
        }
    }
    
    public Rvar_method_type reader(String rname) {
        Rvar_method_type ret = new Rvar_method_typeBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
