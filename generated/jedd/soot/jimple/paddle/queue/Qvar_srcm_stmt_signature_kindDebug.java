package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qvar_srcm_stmt_signature_kindDebug extends Qvar_srcm_stmt_signature_kind {
    public Qvar_srcm_stmt_signature_kindDebug(String name) { super(name); }
    
    private Qvar_srcm_stmt_signature_kindBDD bdd = new Qvar_srcm_stmt_signature_kindBDD(name + "bdd");
    
    private Qvar_srcm_stmt_signature_kindSet trad = new Qvar_srcm_stmt_signature_kindSet(name + "set");
    
    public void add(VarNode _var, SootMethod _srcm, Unit _stmt, NumberedString _signature, Kind _kind) {
        bdd.add(_var, _srcm, _stmt, _signature, _kind);
        trad.add(_var, _srcm, _stmt, _signature, _kind);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { kind.v(), signature.v(), srcm.v(), stmt.v(), var.v() },
                                              new PhysicalDomain[] { FD.v(), H2.v(), T1.v(), ST.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk2/src/soot/jimple/paddle/queue/Qvar_srcm_stmt_signature_" +
                                               "kindDebug.jedd:39,22-24"),
                                              in).iterator(new Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 5; i++) {
                this.add((VarNode) tuple[0],
                         (SootMethod) tuple[1],
                         (Unit) tuple[2],
                         (NumberedString) tuple[3],
                         (Kind) tuple[4]);
            }
        }
    }
    
    public Rvar_srcm_stmt_signature_kind reader(String rname) {
        return new Rvar_srcm_stmt_signature_kindDebug((Rvar_srcm_stmt_signature_kindBDD) bdd.reader(rname),
                                                      (Rvar_srcm_stmt_signature_kindSet) trad.reader(rname),
                                                      name + ":" +
                                                      rname);
    }
}
