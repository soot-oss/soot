package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qobjc_obj_varc_varTrad extends Qobjc_obj_varc_var {
    public Qobjc_obj_varc_varTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(Context _objc, AllocNode _obj, Context _varc, VarNode _var) {
        q.add(_objc);
        q.add(_obj);
        q.add(_varc);
        q.add(_var);
        invalidate();
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { varc.v(), objc.v(), obj.v(), var.v() },
                                              new PhysicalDomain[] { C1.v(), C2.v(), H1.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/soot-trunk/src/soot/jimple/paddle/queue/Qobjc_obj_varc" +
                                               "_varTrad.jedd:41,22-24"),
                                              in).iterator(new Attribute[] { objc.v(), obj.v(), varc.v(), var.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 4; i++) {
                add((Context) tuple[0], (AllocNode) tuple[1], (Context) tuple[2], (VarNode) tuple[3]);
            }
        }
    }
    
    public Robjc_obj_varc_var reader(String rname) {
        return new Robjc_obj_varc_varTrad(q.reader(), name + ":" + rname);
    }
}
