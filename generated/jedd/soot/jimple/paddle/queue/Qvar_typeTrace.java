package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_typeTrace extends Qvar_typeTrad {
    public Qvar_typeTrace(String name) { super(name); }
    
    public void add(VarNode _var, Type _type) {
        G.v().out.print(name + ": ");
        G.v().out.print(_var + ", ");
        G.v().out.print(_type + ", ");
        G.v().out.println();
        super.add(_var, _type);
    }
}
