package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qobj_typeTrad extends Qobj_type {
    public Qobj_typeTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(AllocNode _obj, Type _type) {
        q.add(_obj);
        q.add(_type);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { type.v(), obj.v() },
                                              new PhysicalDomain[] { T2.v(), H1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/queue/Qobj_typeTrad.jedd:38,22-2" +
                                               "4"),
                                              in).iterator(new Attribute[] { obj.v(), type.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 2; i++) { this.add((AllocNode) tuple[0], (Type) tuple[1]); }
        }
    }
    
    public Robj_type reader(String rname) { return new Robj_typeTrad(q.reader(), name + ":" + rname); }
}
