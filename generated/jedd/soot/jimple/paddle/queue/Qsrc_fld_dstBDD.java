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
    public Qsrc_fld_dstBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _src, PaddleField _fld, VarNode _dst) {
        add(new jedd.internal.RelationContainer(new Attribute[] { src.v(), fld.v(), dst.v() },
                                                new PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /home/research/ccl/olhota/olhotak/soot-trunk/src/soot/jimpl" +
                                                 "e/paddle/queue/Qsrc_fld_dstBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _src, _fld, _dst },
                                                                               new Attribute[] { src.v(), fld.v(), dst.v() },
                                                                               new PhysicalDomain[] { V1.v(), FD.v(), V2.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrc_fld_dstBDD reader = (Rsrc_fld_dstBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { dst.v(), src.v(), fld.v() },
                                                           new PhysicalDomain[] { V2.v(), V1.v(), FD.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/olhotak/soot-tru" +
                                                            "nk/src/soot/jimple/paddle/queue/Qsrc_fld_dstBDD.jedd:40,12-1" +
                                                            "8"),
                                                           in));
        }
    }
    
    public Rsrc_fld_dst reader(String rname) {
        Rsrc_fld_dst ret = new Rsrc_fld_dstBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
