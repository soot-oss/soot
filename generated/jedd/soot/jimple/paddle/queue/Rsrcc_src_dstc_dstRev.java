package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrcc_src_dstc_dstRev extends Rsrcc_src_dstc_dstSet {
    public Rsrcc_src_dstc_dstRev(String name) { super(name); }
    
    void add(Tuple tuple) { bdd.addFirst(tuple); }
}
