package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_srcm_stmt_type_signature_kindBDD extends Qvar_srcm_stmt_type_signature_kind {
    public Qvar_srcm_stmt_type_signature_kindBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var, SootMethod _srcm, Unit _stmt, Type _type, NumberedString _signature, Kind _kind) {
        add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), srcm.v(), stmt.v(), type.v(), signature.v(), kind.v() },
                                                new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), T1.v(), SG.v(), KD.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Qvar_s" +
                                                 "rcm_stmt_type_signature_kindBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _var, _srcm, _stmt, _type, _signature, _kind },
                                                                               new Attribute[] { var.v(), srcm.v(), stmt.v(), type.v(), signature.v(), kind.v() },
                                                                               new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), T1.v(), SG.v(), KD.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvar_srcm_stmt_type_signature_kindBDD reader = (Rvar_srcm_stmt_type_signature_kindBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), stmt.v(), kind.v(), var.v(), signature.v(), type.v() },
                                                           new PhysicalDomain[] { MS.v(), ST.v(), KD.v(), V1.v(), SG.v(), T1.v() },
                                                           ("reader.add(in) at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                            "ddle/queue/Qvar_srcm_stmt_type_signature_kindBDD.jedd:40,12-" +
                                                            "18"),
                                                           in));
        }
    }
    
    public Rvar_srcm_stmt_type_signature_kind reader(String rname) {
        Rvar_srcm_stmt_type_signature_kind ret = new Rvar_srcm_stmt_type_signature_kindBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
