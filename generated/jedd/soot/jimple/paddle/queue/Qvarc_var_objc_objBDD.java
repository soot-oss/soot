package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvarc_var_objc_objBDD extends Qvarc_var_objc_obj {
    public Qvarc_var_objc_objBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _varc, VarNode _var, Context _objc, AllocNode _obj) {
        this.add(new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                                     new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/p" +
                                                      "addle/queue/Qvarc_var_objc_objBDD.jedd:34,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _varc, _var, _objc, _obj },
                                                                                    new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() },
                                                                                    new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), H1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvarc_var_objc_objBDD reader = (Rvarc_var_objc_objBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v(), varc.v(), var.v() },
                                                           new PhysicalDomain[] { C2.v(), H1.v(), C1.v(), V1.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                            "oot/jimple/paddle/queue/Qvarc_var_objc_objBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Rvarc_var_objc_obj reader(String rname) {
        Rvarc_var_objc_obj ret = new Rvarc_var_objc_objBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
