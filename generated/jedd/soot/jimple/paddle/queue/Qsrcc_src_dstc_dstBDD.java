package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcc_src_dstc_dstBDD extends Qsrcc_src_dstc_dst {
    public Qsrcc_src_dstc_dstBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _srcc, VarNode _src, Context _dstc, VarNode _dst) {
        add(new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/queue/Qsrcc_" +
                                                 "src_dstc_dstBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _srcc, _src, _dstc, _dst },
                                                                               new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                                               new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrcc_src_dstc_dstBDD reader = (Rsrcc_src_dstc_dstBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { dstc.v(), dst.v(), src.v(), srcc.v() },
                                                           new PhysicalDomain[] { C2.v(), V2.v(), V1.v(), C1.v() },
                                                           ("reader.add(in) at /tmp/olhotak/soot-trunk/src/soot/jimple/pa" +
                                                            "ddle/queue/Qsrcc_src_dstc_dstBDD.jedd:40,12-18"),
                                                           in));
        }
    }
    
    public Rsrcc_src_dstc_dst reader(String rname) {
        Rsrcc_src_dstc_dst ret = new Rsrcc_src_dstc_dstBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
