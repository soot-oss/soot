package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Robjc_obj_varc_varTrad extends Robjc_obj_varc_varIter {
    Robjc_obj_varc_varTrad(QueueReader r, String name) { super(r, name); }
    
    public Robjc_obj_varc_varTrad copy() {
        return new Robjc_obj_varc_varTrad((QueueReader) ((QueueReader) r).clone(), name);
    }
}
