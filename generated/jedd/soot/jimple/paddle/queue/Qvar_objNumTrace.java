package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_objNumTrace extends Qvar_obj {
    public Qvar_objNumTrace(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var, AllocNode _obj) {
        invalidate();
        Rvar_obj.Tuple in = new Rvar_obj.Tuple(_var, _obj);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvar_objNumTrace reader = (Rvar_objNumTrace) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rvar_obj reader(String rname) {
        Rvar_obj ret = new Rvar_objNumTrace(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
