package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qobj_varDebug extends Qobj_var {
    public Qobj_varDebug(String name) { super(name); }
    
    private Qobj_varBDD bdd = new Qobj_varBDD(name + "bdd");
    
    private Qobj_varSet trad = new Qobj_varSet(name + "set");
    
    public void add(AllocNode _obj, VarNode _var) {
        invalidate();
        bdd.add(_obj, _var);
        trad.add(_obj, _var);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v() },
                                              new PhysicalDomain[] { H1.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/olhotak/soot-tr" +
                                               "unk/src/soot/jimple/paddle/queue/Qobj_varDebug.jedd:40,22-24"),
                                              in).iterator(new Attribute[] { obj.v(), var.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 2; i++) { add((AllocNode) tuple[0], (VarNode) tuple[1]); }
        }
    }
    
    public Robj_var reader(String rname) {
        return new Robj_varDebug((Robj_varBDD) bdd.reader(rname), (Robj_varSet) trad.reader(rname), name + ":" + rname);
    }
}
