package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class QvarDebug extends Qvar {
    public QvarDebug(String name) { super(name); }
    
    private QvarBDD bdd = new QvarBDD(name + "bdd");
    
    private QvarSet trad = new QvarSet(name + "set");
    
    public void add(VarNode _var) {
        invalidate();
        bdd.add(_var);
        trad.add(_var);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                              new PhysicalDomain[] { V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/soot-trunk-save" +
                                               "d/src/soot/jimple/paddle/queue/QvarDebug.jedd:40,22-24"),
                                              in).iterator(new Attribute[] { var.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 1; i++) { add((VarNode) tuple[0]); }
        }
    }
    
    public Rvar reader(String rname) {
        return new RvarDebug((RvarBDD) bdd.reader(rname), (RvarSet) trad.reader(rname), name + ":" + rname);
    }
}
