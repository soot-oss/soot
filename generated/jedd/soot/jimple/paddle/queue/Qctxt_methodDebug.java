package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qctxt_methodDebug extends Qctxt_method {
    private Qctxt_methodBDD bdd = new Qctxt_methodBDD();
    
    private Qctxt_methodSet trad = new Qctxt_methodSet();
    
    public void add(Context _ctxt, SootMethod _method) {
        bdd.add(_ctxt, _method);
        trad.add(_ctxt, _method);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { ctxt.v(), method.v() },
                                              new PhysicalDomain[] { V1.v(), T1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/soot-trunk/src/" +
                                               "soot/jimple/paddle/queue/Qctxt_methodDebug.jedd:38,22-24"),
                                              in).iterator(new Attribute[] { ctxt.v(), method.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 2; i++) { add((Context) tuple[0], (SootMethod) tuple[1]); }
        }
    }
    
    public Rctxt_method reader() {
        return new Rctxt_methodDebug((Rctxt_methodBDD) bdd.reader(), (Rctxt_methodSet) trad.reader());
    }
    
    public Qctxt_methodDebug() { super(); }
}
