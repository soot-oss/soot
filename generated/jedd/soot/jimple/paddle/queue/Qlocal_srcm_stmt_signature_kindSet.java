package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qlocal_srcm_stmt_signature_kindSet extends Qlocal_srcm_stmt_signature_kind {
    public Qlocal_srcm_stmt_signature_kindSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Local _local, SootMethod _srcm, Unit _stmt, NumberedString _signature, Kind _kind) {
        Rlocal_srcm_stmt_signature_kind.Tuple in =
          new Rlocal_srcm_stmt_signature_kind.Tuple(_local, _srcm, _stmt, _signature, _kind);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rlocal_srcm_stmt_signature_kindSet reader = (Rlocal_srcm_stmt_signature_kindSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rlocal_srcm_stmt_signature_kind reader(String rname) {
        Rlocal_srcm_stmt_signature_kind ret = new Rlocal_srcm_stmt_signature_kindSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Rlocal_srcm_stmt_signature_kind revreader(String rname) {
        Rlocal_srcm_stmt_signature_kind ret = new Rlocal_srcm_stmt_signature_kindRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
