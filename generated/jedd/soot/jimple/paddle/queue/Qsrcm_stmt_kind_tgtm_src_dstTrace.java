package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcm_stmt_kind_tgtm_src_dstTrace extends Qsrcm_stmt_kind_tgtm_src_dstTrad {
    public Qsrcm_stmt_kind_tgtm_src_dstTrace(String name) { super(name); }
    
    public void add(SootMethod _srcm, Unit _stmt, Kind _kind, SootMethod _tgtm, VarNode _src, VarNode _dst) {
        G.v().out.print(name + ": ");
        G.v().out.print(_srcm + ", ");
        G.v().out.print(_stmt + ", ");
        G.v().out.print(_kind + ", ");
        G.v().out.print(_tgtm + ", ");
        G.v().out.print(_src + ", ");
        G.v().out.print(_dst + ", ");
        G.v().out.println();
        super.add(_srcm, _stmt, _kind, _tgtm, _src, _dst);
    }
}
