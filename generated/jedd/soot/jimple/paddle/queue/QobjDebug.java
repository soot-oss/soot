package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class QobjDebug extends Qobj {
    private QobjBDD bdd = new QobjBDD();
    
    private QobjSet trad = new QobjSet();
    
    public void add(AllocNode _obj) {
        bdd.add(_obj);
        trad.add(_obj);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v() },
                                              new PhysicalDomain[] { H1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/queue/QobjDebug.jedd:38,22-24"),
                                              in).iterator(new Attribute[] { obj.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 1; i++) { this.add((AllocNode) tuple[0]); }
        }
    }
    
    public Robj reader() { return new RobjDebug((RobjBDD) bdd.reader(), (RobjSet) trad.reader()); }
    
    public QobjDebug() { super(); }
}
