package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qsrcc_src_dstc_dstDebug extends Qsrcc_src_dstc_dst {
    public Qsrcc_src_dstc_dstDebug(String name) { super(name); }
    
    private Qsrcc_src_dstc_dstBDD bdd = new Qsrcc_src_dstc_dstBDD(name + "bdd");
    
    private Qsrcc_src_dstc_dstSet trad = new Qsrcc_src_dstc_dstSet(name + "set");
    
    public void add(Context _srcc, VarNode _src, Context _dstc, VarNode _dst) {
        bdd.add(_srcc, _src, _dstc, _dst);
        trad.add(_srcc, _src, _dstc, _dst);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), dstc.v(), dst.v(), src.v() },
                                              new PhysicalDomain[] { C1.v(), C2.v(), V2.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk2/src/soot/jimple/paddle/queue/Qsrcc_src_dstc_dstDebug.j" +
                                               "edd:39,22-24"),
                                              in).iterator(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 4; i++) {
                this.add((Context) tuple[0], (VarNode) tuple[1], (Context) tuple[2], (VarNode) tuple[3]);
            }
        }
    }
    
    public Rsrcc_src_dstc_dst reader(String rname) {
        return new Rsrcc_src_dstc_dstDebug((Rsrcc_src_dstc_dstBDD) bdd.reader(rname),
                                           (Rsrcc_src_dstc_dstSet) trad.reader(rname),
                                           name + ":" +
                                           rname);
    }
}
