package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qobj_typeNumTrace extends Qobj_type {
    public Qobj_typeNumTrace(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(AllocNode _obj, Type _type) {
        Robj_type.Tuple in = new Robj_type.Tuple(_obj, _type);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Robj_typeNumTrace reader = (Robj_typeNumTrace) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Robj_type reader(String rname) {
        Robj_type ret = new Robj_typeNumTrace(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
