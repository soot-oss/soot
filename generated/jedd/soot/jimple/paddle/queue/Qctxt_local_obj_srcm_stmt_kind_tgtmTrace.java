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
    public Qctxt_local_obj_srcm_stmt_kind_tgtmTrace(String name) {
        super();
        this.name = name;
    }
    
    private String name;
    
    public void add(Context _ctxt,
                    Local _local,
                    AllocNode _obj,
                    SootMethod _srcm,
                    Unit _stmt,
                    Kind _kind,
                    SootMethod _tgtm) {
        System.out.print(name + ": ");
        System.out.print(_ctxt + ", ");
        System.out.print(_local + ", ");
        System.out.print(_obj + ", ");
        System.out.print(_srcm + ", ");
        System.out.print(_stmt + ", ");
        System.out.print(_kind + ", ");
        System.out.print(_tgtm + ", ");
        System.out.println();
        super.add(_ctxt, _local, _obj, _srcm, _stmt, _kind, _tgtm);
    }
}
