package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qobj_typeSet extends Qobj_type {
    public Qobj_typeSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(AllocNode _obj, Type _type) {
        invalidate();
        Robj_type.Tuple in = new Robj_type.Tuple(_obj, _type);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Robj_typeSet reader = (Robj_typeSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Robj_type reader(String rname) {
        Robj_type ret = new Robj_typeSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Robj_type revreader(String rname) {
        Robj_type ret = new Robj_typeRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
