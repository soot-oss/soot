package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcc_src_dstc_dst_fldBDD extends Qsrcc_src_dstc_dst_fld {
    public Qsrcc_src_dstc_dst_fldBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _srcc, VarNode _src, Context _dstc, VarNode _dst, PaddleField _fld) {
        add(new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v(), fld.v() },
                                                new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v(), FD.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /home/research/ccl/olhota/olhotak/soot-trunk/src/soot/jimpl" +
                                                 "e/paddle/queue/Qsrcc_src_dstc_dst_fldBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _srcc, _src, _dstc, _dst, _fld },
                                                                               new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v(), fld.v() },
                                                                               new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v(), FD.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrcc_src_dstc_dst_fldBDD reader = (Rsrcc_src_dstc_dst_fldBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { dstc.v(), dst.v(), src.v(), srcc.v(), fld.v() },
                                                           new PhysicalDomain[] { C2.v(), V2.v(), V1.v(), C1.v(), FD.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/olhotak/soot-tru" +
                                                            "nk/src/soot/jimple/paddle/queue/Qsrcc_src_dstc_dst_fldBDD.je" +
                                                            "dd:40,12-18"),
                                                           in));
        }
    }
    
    public Rsrcc_src_dstc_dst_fld reader(String rname) {
        Rsrcc_src_dstc_dst_fld ret = new Rsrcc_src_dstc_dst_fldBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
