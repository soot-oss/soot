package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qobjc_obj_varc_varBDD extends Qobjc_obj_varc_var {
    public Qobjc_obj_varc_varBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _objc, AllocNode _obj, Context _varc, VarNode _var) {
        add(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v(), varc.v(), var.v() },
                                                new PhysicalDomain[] { C2.v(), H1.v(), C1.v(), V1.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /home/research/ccl/olhota/olhotak/soot-trunk/src/soot/jimpl" +
                                                 "e/paddle/queue/Qobjc_obj_varc_varBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _objc, _obj, _varc, _var },
                                                                               new Attribute[] { objc.v(), obj.v(), varc.v(), var.v() },
                                                                               new PhysicalDomain[] { C2.v(), H1.v(), C1.v(), V1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Robjc_obj_varc_varBDD reader = (Robjc_obj_varc_varBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                           new PhysicalDomain[] { C2.v(), V1.v(), C1.v(), H1.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/olhotak/soot-tru" +
                                                            "nk/src/soot/jimple/paddle/queue/Qobjc_obj_varc_varBDD.jedd:4" +
                                                            "0,12-18"),
                                                           in));
        }
    }
    
    public Robjc_obj_varc_var reader(String rname) {
        Robjc_obj_varc_var ret = new Robjc_obj_varc_varBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
