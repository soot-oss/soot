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
    public QvarBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var) {
        add(new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                                new PhysicalDomain[] { V1.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /home/research/ccl/olhota/soot-trunk2/src/soot/jimple/paddl" +
                                                 "e/queue/QvarBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _var },
                                                                               new Attribute[] { var.v() },
                                                                               new PhysicalDomain[] { V1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            RvarBDD reader = (RvarBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                                           new PhysicalDomain[] { V1.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk2/src/" +
                                                            "soot/jimple/paddle/queue/QvarBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Rvar reader(String rname) {
        Rvar ret = new RvarBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
