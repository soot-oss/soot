package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qobjc_obj_varc_varDebug extends Qobjc_obj_varc_var {
    public Qobjc_obj_varc_varDebug(String name) { super(name); }
    
    private Qobjc_obj_varc_varBDD bdd = new Qobjc_obj_varc_varBDD(name + "bdd");
    
    private Qobjc_obj_varc_varSet trad = new Qobjc_obj_varc_varSet(name + "set");
    
    public void add(Context _objc, AllocNode _obj, Context _varc, VarNode _var) {
        invalidate();
        bdd.add(_objc, _obj, _varc, _var);
        trad.add(_objc, _obj, _varc, _var);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v(), obj.v(), objc.v() },
                                              new PhysicalDomain[] { V1.v(), C1.v(), H1.v(), C2.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/soot-trunk-save" +
                                               "d/src/soot/jimple/paddle/queue/Qobjc_obj_varc_varDebug.jedd:" +
                                               "40,22-24"),
                                              in).iterator(new Attribute[] { objc.v(), obj.v(), varc.v(), var.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 4; i++) {
                add((Context) tuple[0], (AllocNode) tuple[1], (Context) tuple[2], (VarNode) tuple[3]);
            }
        }
    }
    
    public Robjc_obj_varc_var reader(String rname) {
        return new Robjc_obj_varc_varDebug((Robjc_obj_varc_varBDD) bdd.reader(rname),
                                           (Robjc_obj_varc_varSet) trad.reader(rname),
                                           name + ":" +
                                           rname);
    }
}
