package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcc_src_fld_dstc_dstTrace extends Qsrcc_src_fld_dstc_dstTrad {
    public Qsrcc_src_fld_dstc_dstTrace(String name) { super(name); }
    
    public void add(Context _srcc, VarNode _src, PaddleField _fld, Context _dstc, VarNode _dst) {
        G.v().out.print(name + ": ");
        G.v().out.print(_srcc + ", ");
        G.v().out.print(_src + ", ");
        G.v().out.print(_fld + ", ");
        G.v().out.print(_dstc + ", ");
        G.v().out.print(_dst + ", ");
        G.v().out.println();
        super.add(_srcc, _src, _fld, _dstc, _dst);
    }
}
