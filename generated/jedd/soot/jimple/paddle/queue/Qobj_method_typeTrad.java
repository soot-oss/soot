package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qobj_method_typeTrad extends Qobj_method_type {
    public Qobj_method_typeTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(AllocNode _obj, SootMethod _method, Type _type) {
        q.add(_obj);
        q.add(_method);
        q.add(_type);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { method.v(), obj.v(), type.v() },
                                              new PhysicalDomain[] { T1.v(), H1.v(), T2.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/queue/Qobj_method_typeTrad.jedd:" +
                                               "39,22-24"),
                                              in).iterator(new Attribute[] { obj.v(), method.v(), type.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 3; i++) { this.add((AllocNode) tuple[0], (SootMethod) tuple[1], (Type) tuple[2]); }
        }
    }
    
    public Robj_method_type reader(String rname) { return new Robj_method_typeTrad(q.reader(), name + ":" + rname); }
}
