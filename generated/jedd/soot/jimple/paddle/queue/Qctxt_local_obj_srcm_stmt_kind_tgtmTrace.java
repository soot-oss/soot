package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qctxt_local_obj_srcm_stmt_kind_tgtmTrace extends Qctxt_local_obj_srcm_stmt_kind_tgtmTrad {
    public Qctxt_local_obj_srcm_stmt_kind_tgtmTrace(String name) { super(name); }
    
    public void add(Context _ctxt,
                    Local _local,
                    AllocNode _obj,
                    SootMethod _srcm,
                    Unit _stmt,
                    Kind _kind,
                    SootMethod _tgtm) {
        G.v().out.print(name + ": ");
        G.v().out.print(_ctxt + ", ");
        G.v().out.print(_local + ", ");
        G.v().out.print(_obj + ", ");
        G.v().out.print(_srcm + ", ");
        G.v().out.print(_stmt + ", ");
        G.v().out.print(_kind + ", ");
        G.v().out.print(_tgtm + ", ");
        G.v().out.println();
        super.add(_ctxt, _local, _obj, _srcm, _stmt, _kind, _tgtm);
    }
}
