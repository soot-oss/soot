package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD extends Qsrcc_srcm_stmt_kind_tgtc_tgtm {
    private LinkedList readers = new LinkedList();
    
    public void add(Context _srcc, SootMethod _srcm, Unit _stmt, Kind _kind, Context _tgtc, SootMethod _tgtm) {
        add(new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                                new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), V2.v(), T2.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /tmp/soot-trunk/src/soot/jimple/paddle/queue/Qsrcc_srcm_stm" +
                                                 "t_kind_tgtc_tgtmBDD.jedd:33,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _srcc, _srcm, _stmt, _kind, _tgtc, _tgtm },
                                                                               new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                                                               new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), FD.v(), V2.v(), T2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD reader = (Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { kind.v(), tgtc.v(), tgtm.v(), srcm.v(), srcc.v(), stmt.v() },
                                                           new PhysicalDomain[] { FD.v(), V2.v(), T2.v(), T1.v(), V1.v(), ST.v() },
                                                           ("reader.add(in) at /tmp/soot-trunk/src/soot/jimple/paddle/que" +
                                                            "ue/Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD.jedd:38,12-18"),
                                                           in));
        }
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm reader() {
        Rsrcc_srcm_stmt_kind_tgtc_tgtm ret = new Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
        readers.add(ret);
        return ret;
    }
    
    public Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD() { super(); }
}
