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
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var, AllocNode _obj) {
        this.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                                     new PhysicalDomain[] { V1.v(), H1.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/queue/" +
                                                      "Qvar_objBDD.jedd:33,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _var, _obj },
                                                                                    new Attribute[] { var.v(), obj.v() },
                                                                                    new PhysicalDomain[] { V1.v(), H1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvar_objBDD reader = (Rvar_objBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v() },
                                                           new PhysicalDomain[] { H1.v(), V1.v() },
                                                           ("reader.add(in) at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                                            "addle/queue/Qvar_objBDD.jedd:38,12-18"),
                                                           in));
        }
    }
    
    public Rvar_obj reader() {
        Rvar_obj ret = new Rvar_objBDD();
        readers.add(ret);
        return ret;
    }
    
    public Qvar_objBDD() { super(); }
}
