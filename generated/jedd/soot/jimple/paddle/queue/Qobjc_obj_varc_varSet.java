package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qobjc_obj_varc_varSet extends Qobjc_obj_varc_var {
    public Qobjc_obj_varc_varSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _objc, AllocNode _obj, Context _varc, VarNode _var) {
        Robjc_obj_varc_var.Tuple in = new Robjc_obj_varc_var.Tuple(_objc, _obj, _varc, _var);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Robjc_obj_varc_varSet reader = (Robjc_obj_varc_varSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Robjc_obj_varc_var reader(String rname) {
        Robjc_obj_varc_var ret = new Robjc_obj_varc_varSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Robjc_obj_varc_var revreader(String rname) {
        Robjc_obj_varc_var ret = new Robjc_obj_varc_varRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
