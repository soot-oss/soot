package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qobj_typeBDD extends Qobj_type {
    public Qobj_typeBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(AllocNode _obj, Type _type) {
        this.add(new jedd.internal.RelationContainer(new Attribute[] { obj.v(), type.v() },
                                                     new PhysicalDomain[] { H1.v(), T2.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/queue/" +
                                                      "Qobj_typeBDD.jedd:34,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _obj, _type },
                                                                                    new Attribute[] { obj.v(), type.v() },
                                                                                    new PhysicalDomain[] { H1.v(), T2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Robj_typeBDD reader = (Robj_typeBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { obj.v(), type.v() },
                                                           new PhysicalDomain[] { H1.v(), T2.v() },
                                                           ("reader.add(in) at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                                            "addle/queue/Qobj_typeBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Robj_type reader(String rname) {
        Robj_type ret = new Robj_typeBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
