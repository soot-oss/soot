package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rvarc_var_objc_objDebug extends Rvarc_var_objc_obj {
    protected Rvarc_var_objc_objBDD bdd;
    
    protected Rvarc_var_objc_objSet trad;
    
    public Rvarc_var_objc_objDebug(Rvarc_var_objc_objBDD bdd, Rvarc_var_objc_objSet trad, String name) {
        super(name);
        this.bdd = bdd;
        this.trad = trad;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            Iterator tradIt = trad.iterator();
            
            Iterator bddIt = bdd.iterator();
            
            Set tradSet = new HashSet();
            
            Set bddSet = new HashSet();
            
            public boolean hasNext() {
                if (tradIt.hasNext() != bddIt.hasNext())
                    throw new RuntimeException("they don\'t match: tradIt=" + tradIt.hasNext() + " bddIt=" +
                                               bddIt.hasNext());
                if (!tradIt.hasNext() && !tradSet.equals(bddSet))
                    throw new RuntimeException(name + "\ntradSet=" + tradSet + "\nbddSet=" + bddSet);
                return tradIt.hasNext();
            }
            
            public Object next() {
                Tuple bddt = (Tuple) bddIt.next();
                Tuple tradt = (Tuple) tradIt.next();
                tradSet.add(tradt);
                bddSet.add(bddt);
                return tradt;
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() { throw new RuntimeException("NYI"); }
    
    public boolean hasNext() { return trad.hasNext(); }
}
