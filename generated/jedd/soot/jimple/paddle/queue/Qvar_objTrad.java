package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qvar_objTrad extends Qvar_obj {
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(VarNode _var, AllocNode _obj) {
        q.add(_var);
        q.add(_obj);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v() },
                                              new PhysicalDomain[] { H1.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/soot-trunk/src/" +
                                               "soot/jimple/paddle/queue/Qvar_objTrad.jedd:37,22-24"),
                                              in).iterator(new Attribute[] { var.v(), obj.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 2; i++) { add((VarNode) tuple[0], (AllocNode) tuple[1]); }
        }
    }
    
    public Rvar_obj reader() { return new Rvar_objTrad(q.reader()); }
    
    public Qvar_objTrad() { super(); }
}
