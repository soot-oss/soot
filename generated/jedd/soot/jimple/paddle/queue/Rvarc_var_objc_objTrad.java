package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvarc_var_objc_objTrad extends Rvarc_var_objc_objIter {
    Rvarc_var_objc_objTrad(QueueReader r, String name) { super(r, name); }
    
    public Rvarc_var_objc_objTrad copy() {
        return new Rvarc_var_objc_objTrad((QueueReader) ((QueueReader) r).clone(), name);
    }
}
