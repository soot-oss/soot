package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qobj_method_typeNumTrace extends Qobj_method_type {
    public Qobj_method_typeNumTrace(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(AllocNode _obj, SootMethod _method, Type _type) {
        invalidate();
        Robj_method_type.Tuple in = new Robj_method_type.Tuple(_obj, _method, _type);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Robj_method_typeNumTrace reader = (Robj_method_typeNumTrace) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Robj_method_type reader(String rname) {
        Robj_method_type ret = new Robj_method_typeNumTrace(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
