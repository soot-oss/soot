package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrc_dstTrace extends Qsrc_dstTrad {
    public Qsrc_dstTrace(String name) { super(name); }
    
    public void add(VarNode _src, VarNode _dst) {
        G.v().out.print(name + ": ");
        G.v().out.print(_src + ", ");
        G.v().out.print(_dst + ", ");
        G.v().out.println();
        super.add(_src, _dst);
    }
}
