package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrc_dstBDD extends Qsrc_dst {
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _src, VarNode _dst) {
        add(new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v() },
                                                new PhysicalDomain[] { V1.v(), V2.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /tmp/soot-trunk/src/soot/jimple/paddle/queue/Qsrc_dstBDD.je" +
                                                 "dd:33,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _src, _dst },
                                                                               new Attribute[] { src.v(), dst.v() },
                                                                               new PhysicalDomain[] { V1.v(), V2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrc_dstBDD reader = (Rsrc_dstBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { dst.v(), src.v() },
                                                           new PhysicalDomain[] { V2.v(), V1.v() },
                                                           ("reader.add(in) at /tmp/soot-trunk/src/soot/jimple/paddle/que" +
                                                            "ue/Qsrc_dstBDD.jedd:38,12-18"),
                                                           in));
        }
    }
    
    public Rsrc_dst reader() {
        Rsrc_dst ret = new Rsrc_dstBDD();
        readers.add(ret);
        return ret;
    }
    
    public Qsrc_dstBDD() { super(); }
}
