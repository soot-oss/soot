package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrc_fld_dstBDD extends Qsrc_fld_dst {
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _src, PaddleField _fld, VarNode _dst) {
        add(new jedd.internal.RelationContainer(new Attribute[] { src.v(), fld.v(), dst.v() },
                                                new PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /tmp/soot-trunk/src/soot/jimple/paddle/queue/Qsrc_fld_dstBD" +
                                                 "D.jedd:33,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _src, _fld, _dst },
                                                                               new Attribute[] { src.v(), fld.v(), dst.v() },
                                                                               new PhysicalDomain[] { V1.v(), FD.v(), V2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrc_fld_dstBDD reader = (Rsrc_fld_dstBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), dst.v(), src.v() },
                                                           new PhysicalDomain[] { FD.v(), V2.v(), V1.v() },
                                                           ("reader.add(in) at /tmp/soot-trunk/src/soot/jimple/paddle/que" +
                                                            "ue/Qsrc_fld_dstBDD.jedd:38,12-18"),
                                                           in));
        }
    }
    
    public Rsrc_fld_dst reader() {
        Rsrc_fld_dst ret = new Rsrc_fld_dstBDD();
        readers.add(ret);
        return ret;
    }
    
    public Qsrc_fld_dstBDD() { super(); }
}
