package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_typeNumTrace extends Qvar_type {
    public Qvar_typeNumTrace(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var, Type _type) {
        invalidate();
        Rvar_type.Tuple in = new Rvar_type.Tuple(_var, _type);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvar_typeNumTrace reader = (Rvar_typeNumTrace) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rvar_type reader(String rname) {
        Rvar_type ret = new Rvar_typeNumTrace(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
