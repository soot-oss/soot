package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qobj_method_typeTrace extends Qobj_method_typeTrad {
    public Qobj_method_typeTrace(String name) { super(name); }
    
    public void add(AllocNode _obj, SootMethod _method, Type _type) {
        G.v().out.print(name + ": ");
        G.v().out.print(_obj + ", ");
        G.v().out.print(_method + ", ");
        G.v().out.print(_type + ", ");
        G.v().out.println();
        super.add(_obj, _method, _type);
    }
}
