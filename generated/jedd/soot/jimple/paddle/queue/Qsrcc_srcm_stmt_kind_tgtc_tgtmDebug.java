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
    public Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug(String name) { super(name); }
    
    private Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD bdd = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD(name + "bdd");
    
    private Qsrcc_srcm_stmt_kind_tgtc_tgtmSet trad = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet(name + "set");
    
    public void add(Context _srcc, SootMethod _srcm, Unit _stmt, Kind _kind, Context _tgtc, SootMethod _tgtm) {
        bdd.add(_srcc, _srcm, _stmt, _kind, _tgtc, _tgtm);
        trad.add(_srcc, _srcm, _stmt, _kind, _tgtc, _tgtm);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { tgtc.v(), tgtm.v(), kind.v(), stmt.v(), srcc.v(), srcm.v() },
                                              new PhysicalDomain[] { V2.v(), T2.v(), FD.v(), ST.v(), V1.v(), T1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/queue/Qsrcc_srcm_stmt_kind_tgtc_" +
                                               "tgtmDebug.jedd:39,22-24"),
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
    
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm reader(String rname) {
        return new Rsrcc_srcm_stmt_kind_tgtc_tgtmDebug((Rsrcc_srcm_stmt_kind_tgtc_tgtmBDD) bdd.reader(rname),
                                                       (Rsrcc_srcm_stmt_kind_tgtc_tgtmSet) trad.reader(rname),
                                                       name + ":" +
                                                       rname);
    }
}
