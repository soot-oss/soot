package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qctxt_methodSet extends Qctxt_method {
    public Qctxt_methodSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _ctxt, SootMethod _method) {
        invalidate();
        Rctxt_method.Tuple in = new Rctxt_method.Tuple(_ctxt, _method);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rctxt_methodSet reader = (Rctxt_methodSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rctxt_method reader(String rname) {
        Rctxt_method ret = new Rctxt_methodSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Rctxt_method revreader(String rname) {
        Rctxt_method ret = new Rctxt_methodRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
