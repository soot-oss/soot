package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_srcm_stmt_tgtmSet extends Qvar_srcm_stmt_tgtm {
    public Qvar_srcm_stmt_tgtmSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _var, SootMethod _srcm, Unit _stmt, SootMethod _tgtm) {
        invalidate();
        Rvar_srcm_stmt_tgtm.Tuple in = new Rvar_srcm_stmt_tgtm.Tuple(_var, _srcm, _stmt, _tgtm);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rvar_srcm_stmt_tgtmSet reader = (Rvar_srcm_stmt_tgtmSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rvar_srcm_stmt_tgtm reader(String rname) {
        Rvar_srcm_stmt_tgtm ret = new Rvar_srcm_stmt_tgtmSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Rvar_srcm_stmt_tgtm revreader(String rname) {
        Rvar_srcm_stmt_tgtm ret = new Rvar_srcm_stmt_tgtmRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
