package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class QvarTrace extends QvarTrad {
    public QvarTrace(String name) {
        super();
        this.name = name;
    }
    
    private String name;
    
    public void add(VarNode _var) {
        System.out.print(name + ": ");
        System.out.print(_var + ", ");
        System.out.println();
        super.add(_var);
    }
}
