package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_typeBDD extends Qvar_type {
    public Qvar_typeBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var, Type _type) {
        this.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                                     new PhysicalDomain[] { V1.v(), T2.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/olhotak/soot-trunk2/src/soot/jimple/paddle/queue" +
                                                      "/Qvar_typeBDD.jedd:34,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _var, _type },
                                                                                    new Attribute[] { var.v(), type.v() },
                                                                                    new PhysicalDomain[] { V1.v(), T2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvar_typeBDD reader = (Rvar_typeBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { type.v(), var.v() },
                                                           new PhysicalDomain[] { T2.v(), V1.v() },
                                                           ("reader.add(in) at /home/olhotak/soot-trunk2/src/soot/jimple/" +
                                                            "paddle/queue/Qvar_typeBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Rvar_type reader(String rname) {
        Rvar_type ret = new Rvar_typeBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
