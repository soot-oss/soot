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
    public Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _srcc, SootMethod _srcm, Unit _stmt, Kind _kind, Context _tgtc, SootMethod _tgtm) {
        add(new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                                new PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /home/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle" +
                                                 "/queue/Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _srcc, _srcm, _stmt, _kind, _tgtc, _tgtm },
                                                                               new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                                                               new PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD reader = (Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { tgtm.v(), srcm.v(), srcc.v(), kind.v(), stmt.v(), tgtc.v() },
                                                           new PhysicalDomain[] { MT.v(), MS.v(), C1.v(), KD.v(), ST.v(), C2.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                            "oot/jimple/paddle/queue/Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD.je" +
                                                            "dd:40,12-18"),
                                                           in));
        }
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm reader(String rname) {
        Rsrcc_srcm_stmt_kind_tgtc_tgtm ret = new Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
