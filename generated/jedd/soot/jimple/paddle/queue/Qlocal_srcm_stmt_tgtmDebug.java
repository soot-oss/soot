package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qlocal_srcm_stmt_tgtmDebug extends Qlocal_srcm_stmt_tgtm {
    public Qlocal_srcm_stmt_tgtmDebug(String name) { super(name); }
    
    private Qlocal_srcm_stmt_tgtmBDD bdd = new Qlocal_srcm_stmt_tgtmBDD(name + "bdd");
    
    private Qlocal_srcm_stmt_tgtmSet trad = new Qlocal_srcm_stmt_tgtmSet(name + "set");
    
    public void add(Local _local, SootMethod _srcm, Unit _stmt, SootMethod _tgtm) {
        bdd.add(_local, _srcm, _stmt, _tgtm);
        trad.add(_local, _srcm, _stmt, _tgtm);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { stmt.v(), srcm.v(), local.v(), tgtm.v() },
                                              new PhysicalDomain[] { ST.v(), T1.v(), V1.v(), T2.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/queue/Qlocal_srcm_stmt_tgtmDebug" +
                                               ".jedd:39,22-24"),
                                              in).iterator(new Attribute[] { local.v(), srcm.v(), stmt.v(), tgtm.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 4; i++) {
                this.add((Local) tuple[0], (SootMethod) tuple[1], (Unit) tuple[2], (SootMethod) tuple[3]);
            }
        }
    }
    
    public Rlocal_srcm_stmt_tgtm reader(String rname) {
        return new Rlocal_srcm_stmt_tgtmDebug((Rlocal_srcm_stmt_tgtmBDD) bdd.reader(rname),
                                              (Rlocal_srcm_stmt_tgtmSet) trad.reader(rname),
                                              name + ":" +
                                              rname);
    }
}
