package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_method_typeTrace extends Qvar_method_typeTrad {
    public Qvar_method_typeTrace(String name) { super(name); }
    
    public void add(VarNode _var, SootMethod _method, Type _type) {
        G.v().out.print(name + ": ");
        G.v().out.print(_var + ", ");
        G.v().out.print(_method + ", ");
        G.v().out.print(_type + ", ");
        G.v().out.println();
        super.add(_var, _method, _type);
    }
}
