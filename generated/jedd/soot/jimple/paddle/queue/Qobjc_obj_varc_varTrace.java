package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qobjc_obj_varc_varTrace extends Qobjc_obj_varc_varTrad {
    public Qobjc_obj_varc_varTrace(String name) { super(name); }
    
    public void add(Context _objc, AllocNode _obj, Context _varc, VarNode _var) {
        G.v().out.print(name + ": ");
        G.v().out.print(_objc + ", ");
        G.v().out.print(_obj + ", ");
        G.v().out.print(_varc + ", ");
        G.v().out.print(_var + ", ");
        G.v().out.println();
        super.add(_objc, _obj, _varc, _var);
    }
}
