package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_srcm_stmt_type_signature_kindRev extends Rvar_srcm_stmt_type_signature_kindSet {
    public Rvar_srcm_stmt_type_signature_kindRev(String name) { super(name); }
    
    void add(Tuple tuple) { bdd.addFirst(tuple); }
}
