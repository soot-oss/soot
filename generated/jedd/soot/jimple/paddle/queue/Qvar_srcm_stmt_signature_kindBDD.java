package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_srcm_stmt_signature_kindBDD extends Qvar_srcm_stmt_signature_kind {
    public Qvar_srcm_stmt_signature_kindBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var, SootMethod _srcm, Unit _stmt, NumberedString _signature, Kind _kind) {
        add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                                new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), SG.v(), KD.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /home/research/ccl/olhota/soot-trunk2/src/soot/jimple/paddl" +
                                                 "e/queue/Qvar_srcm_stmt_signature_kindBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _var, _srcm, _stmt, _signature, _kind },
                                                                               new Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                                                               new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), SG.v(), KD.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvar_srcm_stmt_signature_kindBDD reader = (Rvar_srcm_stmt_signature_kindBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), signature.v(), var.v(), stmt.v(), kind.v() },
                                                           new PhysicalDomain[] { MS.v(), SG.v(), V1.v(), ST.v(), KD.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk2/src/" +
                                                            "soot/jimple/paddle/queue/Qvar_srcm_stmt_signature_kindBDD.je" +
                                                            "dd:39,12-18"),
                                                           in));
        }
    }
    
    public Rvar_srcm_stmt_signature_kind reader(String rname) {
        Rvar_srcm_stmt_signature_kind ret = new Rvar_srcm_stmt_signature_kindBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
