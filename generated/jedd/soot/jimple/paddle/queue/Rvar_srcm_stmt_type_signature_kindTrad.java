package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_srcm_stmt_type_signature_kindTrad extends Rvar_srcm_stmt_type_signature_kindIter {
    Rvar_srcm_stmt_type_signature_kindTrad(QueueReader r, String name) { super(r, name); }
    
    public Rvar_srcm_stmt_type_signature_kindTrad copy() {
        return new Rvar_srcm_stmt_type_signature_kindTrad((QueueReader) ((QueueReader) r).clone(), name);
    }
}
