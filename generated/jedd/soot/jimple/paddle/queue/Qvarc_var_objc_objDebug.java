package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qvarc_var_objc_objDebug extends Qvarc_var_objc_obj {
    public Qvarc_var_objc_objDebug(String name) { super(name); }
    
    private Qvarc_var_objc_objBDD bdd = new Qvarc_var_objc_objBDD(name + "bdd");
    
    private Qvarc_var_objc_objSet trad = new Qvarc_var_objc_objSet(name + "set");
    
    public void add(Context _varc, VarNode _var, Context _objc, AllocNode _obj) {
        invalidate();
        bdd.add(_varc, _var, _objc, _obj);
        trad.add(_varc, _var, _objc, _obj);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { objc.v(), varc.v(), var.v(), obj.v() },
                                              new PhysicalDomain[] { C2.v(), C1.v(), V1.v(), H1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/soot-trunk/src/soot/jimple/paddle/queue/Qvarc_var_objc" +
                                               "_objDebug.jedd:40,22-24"),
                                              in).iterator(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 4; i++) {
                add((Context) tuple[0], (VarNode) tuple[1], (Context) tuple[2], (AllocNode) tuple[3]);
            }
        }
    }
    
    public Rvarc_var_objc_obj reader(String rname) {
        return new Rvarc_var_objc_objDebug((Rvarc_var_objc_objBDD) bdd.reader(rname),
                                           (Rvarc_var_objc_objSet) trad.reader(rname),
                                           name + ":" +
                                           rname);
    }
}
