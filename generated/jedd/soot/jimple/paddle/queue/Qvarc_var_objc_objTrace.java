package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvarc_var_objc_objTrace extends Qvarc_var_objc_objTrad {
    public Qvarc_var_objc_objTrace(String name) { super(name); }
    
    public void add(Context _varc, VarNode _var, Context _objc, AllocNode _obj) {
        G.v().out.print(name + ": ");
        G.v().out.print(_varc + ", ");
        G.v().out.print(_var + ", ");
        G.v().out.print(_objc + ", ");
        G.v().out.print(_obj + ", ");
        G.v().out.println();
        super.add(_varc, _var, _objc, _obj);
    }
}
