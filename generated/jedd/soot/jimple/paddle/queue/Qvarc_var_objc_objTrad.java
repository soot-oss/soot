package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qvarc_var_objc_objTrad extends Qvarc_var_objc_obj {
    public Qvarc_var_objc_objTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(Context _varc, VarNode _var, Context _objc, AllocNode _obj) {
        q.add(_varc);
        q.add(_var);
        q.add(_objc);
        q.add(_obj);
        invalidate();
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { objc.v(), varc.v(), obj.v(), var.v() },
                                              new PhysicalDomain[] { C2.v(), C1.v(), H1.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/soot-jedd/src/soot/jimple/paddle/queue/Qvarc_var_objc_" +
                                               "objTrad.jedd:41,22-24"),
                                              in).iterator(new Attribute[] { varc.v(), var.v(), objc.v(), obj.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 4; i++) {
                add((Context) tuple[0], (VarNode) tuple[1], (Context) tuple[2], (AllocNode) tuple[3]);
            }
        }
    }
    
    public Rvarc_var_objc_obj reader(String rname) {
        return new Rvarc_var_objc_objTrad(q.reader(), name + ":" + rname);
    }
}
