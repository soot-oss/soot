package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qsrc_fld_dstTrad extends Qsrc_fld_dst {
    public Qsrc_fld_dstTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(VarNode _src, PaddleField _fld, VarNode _dst) {
        q.add(_src);
        q.add(_fld);
        q.add(_dst);
        invalidate();
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), fld.v() },
                                              new PhysicalDomain[] { V1.v(), V2.v(), FD.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/soot-trunk/src/soot/jimple/paddle/queue/Qsrc_fld_dstTr" +
                                               "ad.jedd:40,22-24"),
                                              in).iterator(new Attribute[] { src.v(), fld.v(), dst.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 3; i++) { add((VarNode) tuple[0], (PaddleField) tuple[1], (VarNode) tuple[2]); }
        }
    }
    
    public Rsrc_fld_dst reader(String rname) { return new Rsrc_fld_dstTrad(q.reader(), name + ":" + rname); }
}
