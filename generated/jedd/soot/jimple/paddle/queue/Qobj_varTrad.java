package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qobj_varTrad extends Qobj_var {
    public Qobj_varTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(AllocNode _obj, VarNode _var) {
        q.add(_obj);
        q.add(_var);
        invalidate();
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                              new PhysicalDomain[] { V1.v(), H1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Qobj_v" +
                                               "arTrad.jedd:39,22-24"),
                                              in).iterator(new Attribute[] { obj.v(), var.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 2; i++) { add((AllocNode) tuple[0], (VarNode) tuple[1]); }
        }
    }
    
    public Robj_var reader(String rname) { return new Robj_varTrad(q.reader(), name + ":" + rname); }
}
