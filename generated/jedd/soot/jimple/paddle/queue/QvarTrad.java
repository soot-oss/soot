package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class QvarTrad extends Qvar {
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(VarNode _var) { q.add(_var); }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                              new PhysicalDomain[] { V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/soot-trunk/src/" +
                                               "soot/jimple/paddle/queue/QvarTrad.jedd:36,22-24"),
                                              in).iterator(new Attribute[] { var.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 1; i++) { add((VarNode) tuple[0]); }
        }
    }
    
    public Rvar reader() { return new RvarTrad(q.reader()); }
    
    public QvarTrad() { super(); }
}
