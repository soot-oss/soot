package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class QvarBDD extends Qvar {
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var) {
        this.add(new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                                     new PhysicalDomain[] { V1.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/queue/" +
                                                      "QvarBDD.jedd:33,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _var },
                                                                                    new Attribute[] { var.v() },
                                                                                    new PhysicalDomain[] { V1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            RvarBDD reader = (RvarBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                                           new PhysicalDomain[] { V1.v() },
                                                           ("reader.add(in) at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                                            "addle/queue/QvarBDD.jedd:38,12-18"),
                                                           in));
        }
    }
    
    public Rvar reader() {
        Rvar ret = new RvarBDD();
        readers.add(ret);
        return ret;
    }
    
    public QvarBDD() { super(); }
}
