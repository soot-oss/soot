package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrc_fld_dstTrace extends Qsrc_fld_dstTrad {
    public Qsrc_fld_dstTrace(String name) { super(name); }
    
    public void add(VarNode _src, PaddleField _fld, VarNode _dst) {
        G.v().out.print(name + ": ");
        G.v().out.print(_src + ", ");
        G.v().out.print(_fld + ", ");
        G.v().out.print(_dst + ", ");
        G.v().out.println();
        super.add(_src, _fld, _dst);
    }
}
