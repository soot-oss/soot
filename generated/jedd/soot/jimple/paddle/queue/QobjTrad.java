package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class QobjTrad extends Qobj {
    public QobjTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(AllocNode _obj) {
        q.add(_obj);
        invalidate();
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v() },
                                              new PhysicalDomain[] { H1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/olhotak/soot-trunk/src/soot/jimple/paddle/queue/QobjTr" +
                                               "ad.jedd:38,22-24"),
                                              in).iterator(new Attribute[] { obj.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 1; i++) { add((AllocNode) tuple[0]); }
        }
    }
    
    public Robj reader(String rname) { return new RobjTrad(q.reader(), name + ":" + rname); }
}
