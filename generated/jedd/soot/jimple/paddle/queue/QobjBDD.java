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
        this.add(new jedd.internal.RelationContainer(new Attribute[] { obj.v() },
                                                     new PhysicalDomain[] { H1.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/p" +
                                                      "addle/queue/QobjBDD.jedd:34,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _obj },
                                                                                    new Attribute[] { obj.v() },
                                                                                    new PhysicalDomain[] { H1.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            RobjBDD reader = (RobjBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { obj.v() },
                                                           new PhysicalDomain[] { H1.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                            "oot/jimple/paddle/queue/QobjBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Robj reader(String rname) {
        Robj ret = new RobjBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
