package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qobj_method_typeSet extends Qobj_method_type {
    public Qobj_method_typeSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(AllocNode _obj, SootMethod _method, Type _type) {
        Robj_method_type.Tuple in = new Robj_method_type.Tuple(_obj, _method, _type);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Robj_method_typeSet reader = (Robj_method_typeSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Robj_method_type reader(String rname) {
        Robj_method_type ret = new Robj_method_typeSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Robj_method_type revreader(String rname) {
        Robj_method_type ret = new Robj_method_typeRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
