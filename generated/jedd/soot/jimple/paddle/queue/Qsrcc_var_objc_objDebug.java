package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qsrcc_var_objc_objDebug extends Qsrcc_var_objc_obj {
    public Qsrcc_var_objc_objDebug(String name) { super(name); }
    
    private Qsrcc_var_objc_objBDD bdd = new Qsrcc_var_objc_objBDD(name + "bdd");
    
    private Qsrcc_var_objc_objSet trad = new Qsrcc_var_objc_objSet(name + "set");
    
    public void add(Context _srcc, VarNode _var, Context _objc, AllocNode _obj) {
        bdd.add(_srcc, _var, _objc, _obj);
        trad.add(_srcc, _var, _objc, _obj);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), obj.v(), var.v(), objc.v() },
                                              new PhysicalDomain[] { C1.v(), H1.v(), V1.v(), C2.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/queue/Qsrcc_var_objc_objDebug.je" +
                                               "dd:39,22-24"),
                                              in).iterator(new Attribute[] { srcc.v(), var.v(), objc.v(), obj.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 4; i++) {
                this.add((Context) tuple[0], (VarNode) tuple[1], (Context) tuple[2], (AllocNode) tuple[3]);
            }
        }
    }
    
    public Rsrcc_var_objc_obj reader(String rname) {
        return new Rsrcc_var_objc_objDebug((Rsrcc_var_objc_objBDD) bdd.reader(rname),
                                           (Rsrcc_var_objc_objSet) trad.reader(rname),
                                           name + ":" +
                                           rname);
    }
}
