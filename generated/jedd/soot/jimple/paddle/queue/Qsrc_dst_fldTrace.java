package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrc_dst_fldTrace extends Qsrc_dst_fldTrad {
    public Qsrc_dst_fldTrace(String name) { super(name); }
    
    public void add(VarNode _src, VarNode _dst, PaddleField _fld) {
        G.v().out.print(name + ": ");
        G.v().out.print(_src + ", ");
        G.v().out.print(_dst + ", ");
        G.v().out.print(_fld + ", ");
        G.v().out.println();
        super.add(_src, _dst, _fld);
    }
}
