package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_objTrace extends Qvar_objTrad {
    public Qvar_objTrace(String name) { super(name); }
    
    public void add(VarNode _var, AllocNode _obj) {
        G.v().out.print(name + ": ");
        G.v().out.print(_var + ", ");
        G.v().out.print(_obj + ", ");
        G.v().out.println();
        super.add(_var, _obj);
    }
}
