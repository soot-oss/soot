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
    public Qsrc_fld_dstTrace(String name) {
        super();
        this.name = name;
    }
    
    private String name;
    
    public void add(VarNode _src, PaddleField _fld, VarNode _dst) {
        System.out.print(name + ": ");
        System.out.print(_src + ", ");
        System.out.print(_fld + ", ");
        System.out.print(_dst + ", ");
        System.out.println();
        super.add(_src, _fld, _dst);
    }
}
