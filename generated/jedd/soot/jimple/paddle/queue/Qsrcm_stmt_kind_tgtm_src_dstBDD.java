package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcm_stmt_kind_tgtm_src_dstBDD extends Qsrcm_stmt_kind_tgtm_src_dst {
    public Qsrcm_stmt_kind_tgtm_src_dstBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(SootMethod _srcm, Unit _stmt, Kind _kind, SootMethod _tgtm, VarNode _src, VarNode _dst) {
        this.add(new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                                     new PhysicalDomain[] { T1.v(), ST.v(), FD.v(), T2.v(), V1.v(), V2.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/queue/" +
                                                      "Qsrcm_stmt_kind_tgtm_src_dstBDD.jedd:34,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _srcm, _stmt, _kind, _tgtm, _src, _dst },
                                                                                    new Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                                                                    new PhysicalDomain[] { T1.v(), ST.v(), FD.v(), T2.v(), V1.v(), V2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrcm_stmt_kind_tgtm_src_dstBDD reader = (Rsrcm_stmt_kind_tgtm_src_dstBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), kind.v(), stmt.v(), dst.v(), src.v(), tgtm.v() },
                                                           new PhysicalDomain[] { T1.v(), FD.v(), ST.v(), V2.v(), V1.v(), T2.v() },
                                                           ("reader.add(in) at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                                            "addle/queue/Qsrcm_stmt_kind_tgtm_src_dstBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Rsrcm_stmt_kind_tgtm_src_dst reader(String rname) {
        Rsrcm_stmt_kind_tgtm_src_dst ret = new Rsrcm_stmt_kind_tgtm_src_dstBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
