package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qvar_srcm_stmt_dtp_signature_kindDebug extends Qvar_srcm_stmt_dtp_signature_kind {
    public Qvar_srcm_stmt_dtp_signature_kindDebug(String name) { super(name); }
    
    private Qvar_srcm_stmt_dtp_signature_kindBDD bdd = new Qvar_srcm_stmt_dtp_signature_kindBDD(name + "bdd");
    
    private Qvar_srcm_stmt_dtp_signature_kindSet trad = new Qvar_srcm_stmt_dtp_signature_kindSet(name + "set");
    
    public void add(VarNode _var, SootMethod _srcm, Unit _stmt, Type _dtp, NumberedString _signature, Kind _kind) {
        invalidate();
        bdd.add(_var, _srcm, _stmt, _dtp, _signature, _kind);
        trad.add(_var, _srcm, _stmt, _dtp, _signature, _kind);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { kind.v(), dtp.v(), srcm.v(), var.v(), signature.v(), stmt.v() },
                                              new PhysicalDomain[] { KD.v(), T1.v(), MS.v(), V1.v(), SG.v(), ST.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Qvar_s" +
                                               "rcm_stmt_dtp_signature_kindDebug.jedd:40,22-24"),
                                              in).iterator(new Attribute[] { var.v(), srcm.v(), stmt.v(), dtp.v(), signature.v(), kind.v() });
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
    
    public Rvar_srcm_stmt_dtp_signature_kind reader(String rname) {
        return new Rvar_srcm_stmt_dtp_signature_kindDebug((Rvar_srcm_stmt_dtp_signature_kindBDD) bdd.reader(rname),
                                                          (Rvar_srcm_stmt_dtp_signature_kindSet) trad.reader(rname),
                                                          name + ":" +
                                                          rname);
    }
}
