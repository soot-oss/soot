package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qvar_srcm_stmt_type_signature_kindDebug extends Qvar_srcm_stmt_type_signature_kind {
    public Qvar_srcm_stmt_type_signature_kindDebug(String name) { super(name); }
    
    private Qvar_srcm_stmt_type_signature_kindBDD bdd = new Qvar_srcm_stmt_type_signature_kindBDD(name + "bdd");
    
    private Qvar_srcm_stmt_type_signature_kindSet trad = new Qvar_srcm_stmt_type_signature_kindSet(name + "set");
    
    public void add(VarNode _var, SootMethod _srcm, Unit _stmt, Type _type, NumberedString _signature, Kind _kind) {
        invalidate();
        bdd.add(_var, _srcm, _stmt, _type, _signature, _kind);
        trad.add(_var, _srcm, _stmt, _type, _signature, _kind);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), stmt.v(), kind.v(), var.v(), signature.v(), type.v() },
                                              new PhysicalDomain[] { MS.v(), ST.v(), KD.v(), V1.v(), SG.v(), T1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/olhotak/soot-tr" +
                                               "unk/src/soot/jimple/paddle/queue/Qvar_srcm_stmt_type_signatu" +
                                               "re_kindDebug.jedd:40,22-24"),
                                              in).iterator(new Attribute[] { var.v(), srcm.v(), stmt.v(), type.v(), signature.v(), kind.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 6; i++) {
                add((VarNode) tuple[0],
                    (SootMethod) tuple[1],
                    (Unit) tuple[2],
                    (Type) tuple[3],
                    (NumberedString) tuple[4],
                    (Kind) tuple[5]);
            }
        }
    }
    
    public Rvar_srcm_stmt_type_signature_kind reader(String rname) {
        return new Rvar_srcm_stmt_type_signature_kindDebug((Rvar_srcm_stmt_type_signature_kindBDD) bdd.reader(rname),
                                                           (Rvar_srcm_stmt_type_signature_kindSet) trad.reader(rname),
                                                           name + ":" +
                                                           rname);
    }
}
