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
    private QvarBDD bdd = new QvarBDD();
    
    private QvarSet trad = new QvarSet();
    
    public void add(VarNode _var) {
        bdd.add(_var);
        trad.add(_var);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                              new PhysicalDomain[] { V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/soot-trunk/src/" +
                                               "soot/jimple/paddle/queue/QvarDebug.jedd:38,22-24"),
                                              in).iterator(new Attribute[] { var.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 1; i++) { add((VarNode) tuple[0]); }
        }
    }
    
    public Rvar reader() { return new RvarDebug((RvarBDD) bdd.reader(), (RvarSet) trad.reader()); }
    
    public QvarDebug() { super(); }
}
