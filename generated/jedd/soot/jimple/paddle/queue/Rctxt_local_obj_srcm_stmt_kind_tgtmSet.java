package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rctxt_local_obj_srcm_stmt_kind_tgtmSet extends Rctxt_local_obj_srcm_stmt_kind_tgtm {
    private HashSet bdd = new HashSet();
    
    void add(Tuple tuple) { bdd.add(tuple); }
    
    public Iterator iterator() {
        ;
        return new Iterator() {
            private Iterator it;
            
            public boolean hasNext() {
                if (it != null && it.hasNext()) return true;
                if (!bdd.isEmpty()) return true;
                return false;
            }
            
            public Object next() {
                if (it == null || !it.hasNext()) {
                    it = bdd.iterator();
                    bdd = new HashSet();
                }
                return it.next();
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() { throw new RuntimeException(); }
    
    public boolean hasNext() { return !bdd.isEmpty(); }
    
    public Rctxt_local_obj_srcm_stmt_kind_tgtmSet() { super(); }
}
