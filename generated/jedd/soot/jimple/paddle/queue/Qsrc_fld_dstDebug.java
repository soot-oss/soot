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
    private Qsrc_fld_dstBDD bdd = new Qsrc_fld_dstBDD();
    
    private Qsrc_fld_dstSet trad = new Qsrc_fld_dstSet();
    
    public void add(VarNode _src, PaddleField _fld, VarNode _dst) {
        bdd.add(_src, _fld, _dst);
        trad.add(_src, _fld, _dst);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { fld.v(), src.v(), dst.v() },
                                              new PhysicalDomain[] { FD.v(), V1.v(), V2.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk/src/soot/jimple/paddle/queue/Qsrc_fld_dstDebug.jedd:38," +
                                               "22-24"),
                                              in).iterator(new Attribute[] { src.v(), fld.v(), dst.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 3; i++) { this.add((VarNode) tuple[0], (PaddleField) tuple[1], (VarNode) tuple[2]); }
        }
    }
    
    public Rsrc_fld_dst reader() {
        return new Rsrc_fld_dstDebug((Rsrc_fld_dstBDD) bdd.reader(), (Rsrc_fld_dstSet) trad.reader());
    }
    
    public Qsrc_fld_dstDebug() { super(); }
}
