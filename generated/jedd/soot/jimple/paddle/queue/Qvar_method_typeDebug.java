package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qvar_method_typeDebug extends Qvar_method_type {
    public Qvar_method_typeDebug(String name) { super(name); }
    
    private Qvar_method_typeBDD bdd = new Qvar_method_typeBDD(name + "bdd");
    
    private Qvar_method_typeSet trad = new Qvar_method_typeSet(name + "set");
    
    public void add(VarNode _var, SootMethod _method, Type _type) {
        invalidate();
        bdd.add(_var, _method, _type);
        trad.add(_var, _method, _type);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { method.v(), type.v(), var.v() },
                                              new PhysicalDomain[] { MS.v(), T1.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/soot-jedd/src/soot/jimple/paddle/queue/Qvar_method_typ" +
                                               "eDebug.jedd:40,22-24"),
                                              in).iterator(new Attribute[] { var.v(), method.v(), type.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 3; i++) { add((VarNode) tuple[0], (SootMethod) tuple[1], (Type) tuple[2]); }
        }
    }
    
    public Rvar_method_type reader(String rname) {
        return new Rvar_method_typeDebug((Rvar_method_typeBDD) bdd.reader(rname),
                                         (Rvar_method_typeSet) trad.reader(rname),
                                         name + ":" +
                                         rname);
    }
}
