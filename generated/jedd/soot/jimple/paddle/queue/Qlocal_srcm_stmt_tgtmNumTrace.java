package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qlocal_srcm_stmt_tgtmNumTrace extends Qlocal_srcm_stmt_tgtm {
    public Qlocal_srcm_stmt_tgtmNumTrace(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Local _local, SootMethod _srcm, Unit _stmt, SootMethod _tgtm) {
        Rlocal_srcm_stmt_tgtm.Tuple in = new Rlocal_srcm_stmt_tgtm.Tuple(_local, _srcm, _stmt, _tgtm);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rlocal_srcm_stmt_tgtmNumTrace reader = (Rlocal_srcm_stmt_tgtmNumTrace) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rlocal_srcm_stmt_tgtm reader(String rname) {
        Rlocal_srcm_stmt_tgtm ret = new Rlocal_srcm_stmt_tgtmNumTrace(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
