package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qsrcc_src_fld_dstc_dstDebug extends Qsrcc_src_fld_dstc_dst {
    public Qsrcc_src_fld_dstc_dstDebug(String name) { super(name); }
    
    private Qsrcc_src_fld_dstc_dstBDD bdd = new Qsrcc_src_fld_dstc_dstBDD(name + "bdd");
    
    private Qsrcc_src_fld_dstc_dstSet trad = new Qsrcc_src_fld_dstc_dstSet(name + "set");
    
    public void add(Context _srcc, VarNode _src, PaddleField _fld, Context _dstc, VarNode _dst) {
        bdd.add(_srcc, _src, _fld, _dstc, _dst);
        trad.add(_srcc, _src, _fld, _dstc, _dst);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), fld.v(), src.v(), dstc.v(), dst.v() },
                                              new PhysicalDomain[] { C1.v(), FD.v(), V1.v(), C2.v(), V2.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/soot-trunk/src/soot/jimple/paddle/queue/Qsrcc_src_fld_" +
                                               "dstc_dstDebug.jedd:39,22-24"),
                                              in).iterator(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 5; i++) {
                this.add((Context) tuple[0],
                         (VarNode) tuple[1],
                         (PaddleField) tuple[2],
                         (Context) tuple[3],
                         (VarNode) tuple[4]);
            }
        }
    }
    
    public Rsrcc_src_fld_dstc_dst reader(String rname) {
        return new Rsrcc_src_fld_dstc_dstDebug((Rsrcc_src_fld_dstc_dstBDD) bdd.reader(rname),
                                               (Rsrcc_src_fld_dstc_dstSet) trad.reader(rname),
                                               name + ":" +
                                               rname);
    }
}
