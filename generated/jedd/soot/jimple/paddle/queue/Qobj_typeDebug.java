package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qobj_typeDebug extends Qobj_type {
    public Qobj_typeDebug(String name) { super(name); }
    
    private Qobj_typeBDD bdd = new Qobj_typeBDD(name + "bdd");
    
    private Qobj_typeSet trad = new Qobj_typeSet(name + "set");
    
    public void add(AllocNode _obj, Type _type) {
        bdd.add(_obj, _type);
        trad.add(_obj, _type);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), type.v() },
                                              new PhysicalDomain[] { H1.v(), T2.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk2/src/soot/jimple/paddle/queue/Qobj_typeDebug.jedd:39,22" +
                                               "-24"),
                                              in).iterator(new Attribute[] { obj.v(), type.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 2; i++) { this.add((AllocNode) tuple[0], (Type) tuple[1]); }
        }
    }
    
    public Robj_type reader(String rname) {
        return new Robj_typeDebug((Robj_typeBDD) bdd.reader(rname),
                                  (Robj_typeSet) trad.reader(rname),
                                  name + ":" +
                                  rname);
    }
}
