package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qsrc_fld_dstDebug extends Qsrc_fld_dst {
    public Qsrc_fld_dstDebug(String name) { super(name); }
    
    private Qsrc_fld_dstBDD bdd = new Qsrc_fld_dstBDD(name + "bdd");
    
    private Qsrc_fld_dstSet trad = new Qsrc_fld_dstSet(name + "set");
    
    public void add(VarNode _src, PaddleField _fld, VarNode _dst) {
        invalidate();
        bdd.add(_src, _fld, _dst);
        trad.add(_src, _fld, _dst);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { fld.v(), dst.v(), src.v() },
                                              new PhysicalDomain[] { FD.v(), V2.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/olhotak/soot-tr" +
                                               "unk/src/soot/jimple/paddle/queue/Qsrc_fld_dstDebug.jedd:40,2" +
                                               "2-24"),
                                              in).iterator(new Attribute[] { src.v(), fld.v(), dst.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 3; i++) { add((VarNode) tuple[0], (PaddleField) tuple[1], (VarNode) tuple[2]); }
        }
    }
    
    public Rsrc_fld_dst reader(String rname) {
        return new Rsrc_fld_dstDebug((Rsrc_fld_dstBDD) bdd.reader(rname),
                                     (Rsrc_fld_dstSet) trad.reader(rname),
                                     name + ":" +
                                     rname);
    }
}
