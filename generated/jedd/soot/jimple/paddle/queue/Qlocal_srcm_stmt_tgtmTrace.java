package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qlocal_srcm_stmt_tgtmTrace extends Qlocal_srcm_stmt_tgtmTrad {
    public Qlocal_srcm_stmt_tgtmTrace(String name) {
        super();
        this.name = name;
    }
    
    private String name;
    
    public void add(Local _local, SootMethod _srcm, Unit _stmt, SootMethod _tgtm) {
        System.out.print(name + ": ");
        System.out.print(_local + ", ");
        System.out.print(_srcm + ", ");
        System.out.print(_stmt + ", ");
        System.out.print(_tgtm + ", ");
        System.out.println();
        super.add(_local, _srcm, _stmt, _tgtm);
    }
}
