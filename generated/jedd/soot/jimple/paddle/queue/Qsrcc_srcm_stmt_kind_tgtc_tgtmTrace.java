package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace extends Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad {
    public Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace(String name) {
        super();
        this.name = name;
    }
    
    private String name;
    
    public void add(Context _srcc, SootMethod _srcm, Unit _stmt, Kind _kind, Context _tgtc, SootMethod _tgtm) {
        System.out.print(name + ": ");
        System.out.print(_srcc + ", ");
        System.out.print(_srcm + ", ");
        System.out.print(_stmt + ", ");
        System.out.print(_kind + ", ");
        System.out.print(_tgtc + ", ");
        System.out.print(_tgtm + ", ");
        System.out.println();
        super.add(_srcc, _srcm, _stmt, _kind, _tgtc, _tgtm);
    }
}
