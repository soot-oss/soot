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
    public Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace(String name) { super(name); }
    
    public void add(Context _srcc, SootMethod _srcm, Unit _stmt, Kind _kind, Context _tgtc, SootMethod _tgtm) {
        G.v().out.print(name + ": ");
        G.v().out.print(_srcc + ", ");
        G.v().out.print(_srcm + ", ");
        G.v().out.print(_stmt + ", ");
        G.v().out.print(_kind + ", ");
        G.v().out.print(_tgtc + ", ");
        G.v().out.print(_tgtm + ", ");
        G.v().out.println();
        super.add(_srcc, _srcm, _stmt, _kind, _tgtc, _tgtm);
    }
}
