package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rlocal_srcm_stmt_tgtmTrad extends Rlocal_srcm_stmt_tgtmIter {
    Rlocal_srcm_stmt_tgtmTrad(QueueReader r, String name) { super(r, name); }
    
    public Rlocal_srcm_stmt_tgtmTrad copy() {
        return new Rlocal_srcm_stmt_tgtmTrad((QueueReader) ((QueueReader) r).clone(), name);
    }
}
