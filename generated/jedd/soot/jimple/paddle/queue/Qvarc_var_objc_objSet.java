package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvarc_var_objc_objSet extends Qvarc_var_objc_obj {
    public Qvarc_var_objc_objSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _varc, VarNode _var, Context _objc, AllocNode _obj) {
        invalidate();
        Rvarc_var_objc_obj.Tuple in = new Rvarc_var_objc_obj.Tuple(_varc, _var, _objc, _obj);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvarc_var_objc_objSet reader = (Rvarc_var_objc_objSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rvarc_var_objc_obj reader(String rname) {
        Rvarc_var_objc_obj ret = new Rvarc_var_objc_objSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Rvarc_var_objc_obj revreader(String rname) {
        Rvarc_var_objc_obj ret = new Rvarc_var_objc_objRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
