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
        this.add(new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                     new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/queue/" +
                                                      "Qsrcc_src_dstc_dstBDD.jedd:34,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _srcc, _src, _dstc, _dst },
                                                                                    new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                                                                    new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrcc_src_dstc_dstBDD reader = (Rsrcc_src_dstc_dstBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { dst.v(), dstc.v(), src.v(), srcc.v() },
                                                           new PhysicalDomain[] { V2.v(), C2.v(), V1.v(), C1.v() },
                                                           ("reader.add(in) at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                                            "addle/queue/Qsrcc_src_dstc_dstBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Rsrcc_src_dstc_dst reader(String rname) {
        Rsrcc_src_dstc_dst ret = new Rsrcc_src_dstc_dstBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
