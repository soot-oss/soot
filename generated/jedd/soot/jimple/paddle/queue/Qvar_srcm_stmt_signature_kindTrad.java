package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qvar_srcm_stmt_signature_kindTrad extends Qvar_srcm_stmt_signature_kind {
    public Qvar_srcm_stmt_signature_kindTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(VarNode _var, SootMethod _srcm, Unit _stmt, NumberedString _signature, Kind _kind) {
        q.add(_var);
        q.add(_srcm);
        q.add(_stmt);
        q.add(_signature);
        q.add(_kind);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), kind.v(), signature.v(), stmt.v(), srcm.v() },
                                              new PhysicalDomain[] { V1.v(), FD.v(), H2.v(), ST.v(), T1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/queue/Qvar_srcm_stmt_signature_k" +
                                               "indTrad.jedd:41,22-24"),
                                              in).iterator(new Attribute[] { var.v(), srcm.v(), stmt.v(), signature.v(), kind.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 5; i++) {
                this.add((VarNode) tuple[0],
                         (SootMethod) tuple[1],
                         (Unit) tuple[2],
                         (NumberedString) tuple[3],
                         (Kind) tuple[4]);
            }
        }
    }
    
    public Rvar_srcm_stmt_signature_kind reader(String rname) {
        return new Rvar_srcm_stmt_signature_kindTrad(q.reader(), name + ":" + rname);
    }
}
