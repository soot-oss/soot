package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class QobjBDD extends Qobj {
    public QobjBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(AllocNode _obj) {
        add(new jedd.internal.RelationContainer(new Attribute[] { obj.v() },
                                                new PhysicalDomain[] { H1.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/queue/QobjBD" +
                                                 "D.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _obj },
                                                                               new Attribute[] { obj.v() },
                                                                               new PhysicalDomain[] { H1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            RobjBDD reader = (RobjBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { obj.v() },
                                                           new PhysicalDomain[] { H1.v() },
                                                           ("reader.add(in) at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                            "ddle/queue/QobjBDD.jedd:40,12-18"),
                                                           in));
        }
    }
    
    public Robj reader(String rname) {
        Robj ret = new RobjBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
