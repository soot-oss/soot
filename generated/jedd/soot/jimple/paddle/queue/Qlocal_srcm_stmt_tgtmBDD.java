package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qlocal_srcm_stmt_tgtmBDD extends Qlocal_srcm_stmt_tgtm {
    private LinkedList readers = new LinkedList();
    
    public void add(Local _local, SootMethod _srcm, Unit _stmt, SootMethod _tgtm) {
        add(new jedd.internal.RelationContainer(new Attribute[] { local.v(), srcm.v(), stmt.v(), tgtm.v() },
                                                new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /tmp/soot-trunk/src/soot/jimple/paddle/queue/Qlocal_srcm_st" +
                                                 "mt_tgtmBDD.jedd:33,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _local, _srcm, _stmt, _tgtm },
                                                                               new Attribute[] { local.v(), srcm.v(), stmt.v(), tgtm.v() },
                                                                               new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), T2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rlocal_srcm_stmt_tgtmBDD reader = (Rlocal_srcm_stmt_tgtmBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { local.v(), tgtm.v(), srcm.v(), stmt.v() },
                                                           new PhysicalDomain[] { V1.v(), T2.v(), T1.v(), ST.v() },
                                                           ("reader.add(in) at /tmp/soot-trunk/src/soot/jimple/paddle/que" +
                                                            "ue/Qlocal_srcm_stmt_tgtmBDD.jedd:38,12-18"),
                                                           in));
        }
    }
    
    public Rlocal_srcm_stmt_tgtm reader() {
        Rlocal_srcm_stmt_tgtm ret = new Rlocal_srcm_stmt_tgtmBDD();
        readers.add(ret);
        return ret;
    }
    
    public Qlocal_srcm_stmt_tgtmBDD() { super(); }
}
