package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qsrc_dstDebug extends Qsrc_dst {
    private Qsrc_dstBDD bdd = new Qsrc_dstBDD();
    
    private Qsrc_dstSet trad = new Qsrc_dstSet();
    
    public void add(VarNode _src, VarNode _dst) {
        bdd.add(_src, _dst);
        trad.add(_src, _dst);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { dst.v(), src.v() },
                                              new PhysicalDomain[] { V2.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/soot-trunk/src/" +
                                               "soot/jimple/paddle/queue/Qsrc_dstDebug.jedd:38,22-24"),
                                              in).iterator(new Attribute[] { src.v(), dst.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 2; i++) { add((VarNode) tuple[0], (VarNode) tuple[1]); }
        }
    }
    
    public Rsrc_dst reader() { return new Rsrc_dstDebug((Rsrc_dstBDD) bdd.reader(), (Rsrc_dstSet) trad.reader()); }
    
    public Qsrc_dstDebug() { super(); }
}
