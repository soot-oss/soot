package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Rlocal_srcm_stmt_signature_kindDebug extends Rlocal_srcm_stmt_signature_kind {
    protected Rlocal_srcm_stmt_signature_kindBDD bdd;
    
    protected Rlocal_srcm_stmt_signature_kindSet trad;
    
    public Rlocal_srcm_stmt_signature_kindDebug(Rlocal_srcm_stmt_signature_kindBDD bdd,
                                                Rlocal_srcm_stmt_signature_kindSet trad) {
        super();
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
                    throw new RuntimeException("tradSet=" + tradSet + "\nbddSet=" + bddSet);
                if (!tradIt.hasNext()) System.out.println("DONE");
                return tradIt.hasNext();
            }
            
            public Object next() {
                Tuple bddt = (Tuple) bddIt.next();
                Tuple tradt = (Tuple) tradIt.next();
                tradSet.add(tradt);
                bddSet.add(bddt);
                System.out.println("bdd:" + bddt + "\ntrad:" + tradt);
                return bddt;
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() { throw new RuntimeException("NYI"); }
    
    public boolean hasNext() { return trad.hasNext(); }
}
