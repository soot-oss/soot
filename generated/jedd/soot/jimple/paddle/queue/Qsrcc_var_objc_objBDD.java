package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcc_var_objc_objBDD extends Qsrcc_var_objc_obj {
    public Qsrcc_var_objc_objBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _srcc, VarNode _var, Context _objc, AllocNode _obj) {
        this.add(new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), var.v(), objc.v(), obj.v() },
                                                     new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/queue/" +
                                                      "Qsrcc_var_objc_objBDD.jedd:34,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _srcc, _var, _objc, _obj },
                                                                                    new Attribute[] { srcc.v(), var.v(), objc.v(), obj.v() },
                                                                                    new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrcc_var_objc_objBDD reader = (Rsrcc_var_objc_objBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), obj.v(), var.v(), objc.v() },
                                                           new PhysicalDomain[] { C1.v(), H1.v(), V1.v(), C2.v() },
                                                           ("reader.add(in) at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                                            "addle/queue/Qsrcc_var_objc_objBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Rsrcc_var_objc_obj reader(String rname) {
        Rsrcc_var_objc_obj ret = new Rsrcc_var_objc_objBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
