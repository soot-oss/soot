package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcc_var_objc_objTrace extends Qsrcc_var_objc_objTrad {
    public Qsrcc_var_objc_objTrace(String name) { super(name); }
    
    public void add(Context _srcc, VarNode _var, Context _objc, AllocNode _obj) {
        G.v().out.print(name + ": ");
        G.v().out.print(_srcc + ", ");
        G.v().out.print(_var + ", ");
        G.v().out.print(_objc + ", ");
        G.v().out.print(_obj + ", ");
        G.v().out.println();
        super.add(_srcc, _var, _objc, _obj);
    }
}
