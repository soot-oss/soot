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
    public QvarTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(VarNode _var) {
        q.add(_var);
        invalidate();
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                              new PhysicalDomain[] { V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/soot-jedd/src/soot/jimple/paddle/queue/QvarTrad.jedd:3" +
                                               "8,22-24"),
                                              in).iterator(new Attribute[] { var.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 1; i++) { add((VarNode) tuple[0]); }
        }
    }
    
    public Rvar reader(String rname) { return new RvarTrad(q.reader(), name + ":" + rname); }
}
