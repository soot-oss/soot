package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug extends Qsrcc_srcm_stmt_kind_tgtc_tgtm {
    private Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD bdd = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
    
    private Qsrcc_srcm_stmt_kind_tgtc_tgtmSet trad = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet();
    
    public void add(Context _srcc, SootMethod _srcm, Unit _stmt, Kind _kind, Context _tgtc, SootMethod _tgtm) {
        bdd.add(_srcc, _srcm, _stmt, _kind, _tgtc, _tgtm);
        trad.add(_srcc, _srcm, _stmt, _kind, _tgtc, _tgtm);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { stmt.v(), srcm.v(), tgtc.v(), kind.v(), tgtm.v(), srcc.v() },
                                              new PhysicalDomain[] { ST.v(), T1.v(), V2.v(), FD.v(), T2.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/queue/Qsrcc_srcm_stmt_kind_tgtc_" +
                                               "tgtmDebug.jedd:38,22-24"),
                                              in).iterator(new Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 6; i++) {
                this.add((Context) tuple[0],
                         (SootMethod) tuple[1],
                         (Unit) tuple[2],
                         (Kind) tuple[3],
                         (Context) tuple[4],
                         (SootMethod) tuple[5]);
            }
        }
    }
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm reader() {
        return new Rsrcc_srcm_stmt_kind_tgtc_tgtmDebug((Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD) bdd.reader(),
                                                       (Rsrcc_srcm_stmt_kind_tgtc_tgtmSet) trad.reader());
    }
    
    public Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug() { super(); }
}
