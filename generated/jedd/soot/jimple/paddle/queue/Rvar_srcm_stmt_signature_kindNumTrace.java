package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rvar_srcm_stmt_signature_kindNumTrace extends Rvar_srcm_stmt_signature_kind {
    public Rvar_srcm_stmt_signature_kindNumTrace(String name) { super(name); }
    
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
                    G.v().out.println(name + ": " + bdd.size());
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
