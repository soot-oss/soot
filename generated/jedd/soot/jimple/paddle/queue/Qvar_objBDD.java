package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_objBDD extends Qvar_obj {
    public Qvar_objBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var, AllocNode _obj) {
        add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                                new PhysicalDomain[] { V1.v(), H1.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Qvar_o" +
                                                 "bjBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _var, _obj },
                                                                               new Attribute[] { var.v(), obj.v() },
                                                                               new PhysicalDomain[] { V1.v(), H1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvar_objBDD reader = (Rvar_objBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                                           new PhysicalDomain[] { V1.v(), H1.v() },
                                                           ("reader.add(in) at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                            "ddle/queue/Qvar_objBDD.jedd:40,12-18"),
                                                           in));
        }
    }
    
    public Rvar_obj reader(String rname) {
        Rvar_obj ret = new Rvar_objBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
