package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qsrc_dst_fldDebug extends Qsrc_dst_fld {
    public Qsrc_dst_fldDebug(String name) { super(name); }
    
    private Qsrc_dst_fldBDD bdd = new Qsrc_dst_fldBDD(name + "bdd");
    
    private Qsrc_dst_fldSet trad = new Qsrc_dst_fldSet(name + "set");
    
    public void add(VarNode _src, VarNode _dst, PaddleField _fld) {
        invalidate();
        bdd.add(_src, _dst, _fld);
        trad.add(_src, _dst, _fld);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { fld.v(), src.v(), dst.v() },
                                              new PhysicalDomain[] { FD.v(), V1.v(), V2.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/soot-jedd/src/soot/jimple/paddle/queue/Qsrc_dst_fldDeb" +
                                               "ug.jedd:40,22-24"),
                                              in).iterator(new Attribute[] { src.v(), dst.v(), fld.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 3; i++) { add((VarNode) tuple[0], (VarNode) tuple[1], (PaddleField) tuple[2]); }
        }
    }
    
    public Rsrc_dst_fld reader(String rname) {
        return new Rsrc_dst_fldDebug((Rsrc_dst_fldBDD) bdd.reader(rname),
                                     (Rsrc_dst_fldSet) trad.reader(rname),
                                     name + ":" +
                                     rname);
    }
}
