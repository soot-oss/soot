package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rsrcm_stmt_kind_tgtm_src_dstSet extends Rsrcm_stmt_kind_tgtm_src_dst {
    public Rsrcm_stmt_kind_tgtm_src_dstSet(String name) { super(name); }
    
    protected LinkedList bdd = new LinkedList();
    
    void add(Tuple tuple) { bdd.addLast(tuple); }
    
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
                    bdd = new LinkedList();
                }
                return it.next();
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() { throw new RuntimeException(); }
    
    public boolean hasNext() { return !bdd.isEmpty(); }
}
