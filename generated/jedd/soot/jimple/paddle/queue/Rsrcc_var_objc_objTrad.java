package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrcc_var_objc_objTrad extends Rsrcc_var_objc_objIter {
    Rsrcc_var_objc_objTrad(QueueReader r, String name) { super(r, name); }
    
    public Rsrcc_var_objc_objTrad copy() {
        return new Rsrcc_var_objc_objTrad((QueueReader) ((QueueReader) r).clone(), name);
    }
}
