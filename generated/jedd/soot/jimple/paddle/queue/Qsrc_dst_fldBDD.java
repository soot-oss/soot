package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrc_dst_fldBDD extends Qsrc_dst_fld {
    public Qsrc_dst_fldBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _src, VarNode _dst, PaddleField _fld) {
        add(new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), fld.v() },
                                                new PhysicalDomain[] { V1.v(), V2.v(), FD.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " /home/research/ccl/olhota/soot-trunk/src/soot/jimple/paddle" +
                                                 "/queue/Qsrc_dst_fldBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _src, _dst, _fld },
                                                                               new Attribute[] { src.v(), dst.v(), fld.v() },
                                                                               new PhysicalDomain[] { V1.v(), V2.v(), FD.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in), jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrc_dst_fldBDD reader = (Rsrc_dst_fldBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), fld.v() },
                                                           new PhysicalDomain[] { V1.v(), V2.v(), FD.v() },
                                                           ("reader.add(in) at /home/research/ccl/olhota/soot-trunk/src/s" +
                                                            "oot/jimple/paddle/queue/Qsrc_dst_fldBDD.jedd:40,12-18"),
                                                           in));
        }
    }
    
    public Rsrc_dst_fld reader(String rname) {
        Rsrc_dst_fld ret = new Rsrc_dst_fldBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
