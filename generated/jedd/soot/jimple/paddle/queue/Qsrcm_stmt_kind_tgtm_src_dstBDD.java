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
        add(new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                                new PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v(), V1.v(), V2.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /home/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle" +
                                                 "/queue/Qsrcm_stmt_kind_tgtm_src_dstBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _srcm, _stmt, _kind, _tgtm, _src, _dst },
                                                                               new Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                                                               new PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v(), V1.v(), V2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrcm_stmt_kind_tgtm_src_dstBDD reader = (Rsrcm_stmt_kind_tgtm_src_dstBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { tgtm.v(), src.v(), srcm.v(), dst.v(), kind.v(), stmt.v() },
                                                           new PhysicalDomain[] { MT.v(), V1.v(), MS.v(), V2.v(), KD.v(), ST.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                            "oot/jimple/paddle/queue/Qsrcm_stmt_kind_tgtm_src_dstBDD.jedd" +
                                                            ":40,12-18"),
                                                           in));
        }
    }
    
    public Rsrcm_stmt_kind_tgtm_src_dst reader(String rname) {
        Rsrcm_stmt_kind_tgtm_src_dst ret = new Rsrcm_stmt_kind_tgtm_src_dstBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
