package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qvar_srcm_stmt_tgtmDebug extends Qvar_srcm_stmt_tgtm {
    public Qvar_srcm_stmt_tgtmDebug(String name) { super(name); }
    
    private Qvar_srcm_stmt_tgtmBDD bdd = new Qvar_srcm_stmt_tgtmBDD(name + "bdd");
    
    private Qvar_srcm_stmt_tgtmSet trad = new Qvar_srcm_stmt_tgtmSet(name + "set");
    
    public void add(VarNode _var, SootMethod _srcm, Unit _stmt, SootMethod _tgtm) {
        invalidate();
        bdd.add(_var, _srcm, _stmt, _tgtm);
        trad.add(_var, _srcm, _stmt, _tgtm);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { tgtm.v(), var.v(), srcm.v(), stmt.v() },
                                              new PhysicalDomain[] { MT.v(), V1.v(), MS.v(), ST.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/soot-jedd/src/soot/jimple/paddle/queue/Qvar_srcm_stmt_" +
                                               "tgtmDebug.jedd:40,22-24"),
                                              in).iterator(new Attribute[] { var.v(), srcm.v(), stmt.v(), tgtm.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 4; i++) {
                add((VarNode) tuple[0], (SootMethod) tuple[1], (Unit) tuple[2], (SootMethod) tuple[3]);
            }
        }
    }
    
    public Rvar_srcm_stmt_tgtm reader(String rname) {
        return new Rvar_srcm_stmt_tgtmDebug((Rvar_srcm_stmt_tgtmBDD) bdd.reader(rname),
                                            (Rvar_srcm_stmt_tgtmSet) trad.reader(rname),
                                            name + ":" +
                                            rname);
    }
}
