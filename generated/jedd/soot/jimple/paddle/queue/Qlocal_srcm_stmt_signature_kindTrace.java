package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qlocal_srcm_stmt_signature_kindTrace extends Qlocal_srcm_stmt_signature_kindTrad {
    public Qlocal_srcm_stmt_signature_kindTrace(String name) { super(name); }
    
    public void add(Local _local, SootMethod _srcm, Unit _stmt, NumberedString _signature, Kind _kind) {
        G.v().out.print(name + ": ");
        G.v().out.print(_local + ", ");
        G.v().out.print(_srcm + ", ");
        G.v().out.print(_stmt + ", ");
        G.v().out.print(_signature + ", ");
        G.v().out.print(_kind + ", ");
        G.v().out.println();
        super.add(_local, _srcm, _stmt, _signature, _kind);
    }
}
