package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qobj_varTrace extends Qobj_varTrad {
    public Qobj_varTrace(String name) { super(name); }
    
    public void add(AllocNode _obj, VarNode _var) {
        G.v().out.print(name + ": ");
        G.v().out.print(_obj + ", ");
        G.v().out.print(_var + ", ");
        G.v().out.println();
        super.add(_obj, _var);
    }
}
