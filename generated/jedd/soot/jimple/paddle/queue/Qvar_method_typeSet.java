package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_method_typeSet extends Qvar_method_type {
    public Qvar_method_typeSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var, SootMethod _method, Type _type) {
        Rvar_method_type.Tuple in = new Rvar_method_type.Tuple(_var, _method, _type);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvar_method_typeSet reader = (Rvar_method_typeSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rvar_method_type reader(String rname) {
        Rvar_method_type ret = new Rvar_method_typeSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Rvar_method_type revreader(String rname) {
        Rvar_method_type ret = new Rvar_method_typeRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
