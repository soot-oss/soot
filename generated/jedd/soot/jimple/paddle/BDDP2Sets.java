package soot.jimple.paddle;

import soot.*;
import soot.jimple.paddle.queue.*;
import java.util.*;
import soot.jimple.paddle.bdddomains.*;

public class BDDP2Sets extends AbsP2Sets {
    PropBDD prop;
    
    public BDDP2Sets(PropBDD prop) {
        super();
        this.prop = prop;
    }
    
    public PointsToSetReadOnly get(Context ctxt, VarNode v) {
        return new BDDPointsToSet(new jedd.internal.RelationContainer(new jedd.Attribute[] { objc.v(), obj.v() },
                                                                      new jedd.PhysicalDomain[] { C3.v(), H1.v() },
                                                                      ("new soot.jimple.paddle.BDDPointsToSet(...) at /tmp/olhotak/s" +
                                                                       "oot-trunk/src/soot/jimple/paddle/BDDP2Sets.jedd:39,15-18"),
                                                                      jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(prop.pt),
                                                                                                                                    jedd.internal.Jedd.v().literal(new Object[] { ctxt, v },
                                                                                                                                                                   new jedd.Attribute[] { varc.v(), var.v() },
                                                                                                                                                                   new jedd.PhysicalDomain[] { C2.v(), V2.v() }),
                                                                                                                                    new jedd.PhysicalDomain[] { C2.v(), V2.v() }),
                                                                                                     new jedd.PhysicalDomain[] { H2.v() },
                                                                                                     new jedd.PhysicalDomain[] { H1.v() })));
    }
    
    public PointsToSetReadOnly get(Context ctxt, AllocDotField adf) {
        return new BDDPointsToSet(new jedd.internal.RelationContainer(new jedd.Attribute[] { objc.v(), obj.v() },
                                                                      new jedd.PhysicalDomain[] { C3.v(), H1.v() },
                                                                      ("new soot.jimple.paddle.BDDPointsToSet(...) at /tmp/olhotak/s" +
                                                                       "oot-trunk/src/soot/jimple/paddle/BDDP2Sets.jedd:43,15-18"),
                                                                      jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().replace(prop.fieldPt,
                                                                                                                                                                                               new jedd.PhysicalDomain[] { C2.v() },
                                                                                                                                                                                               new jedd.PhysicalDomain[] { C3.v() })),
                                                                                                                                    jedd.internal.Jedd.v().literal(new Object[] { ctxt, adf.base(), adf.field() },
                                                                                                                                                                   new jedd.Attribute[] { basec.v(), base.v(), fld.v() },
                                                                                                                                                                   new jedd.PhysicalDomain[] { C1.v(), H1.v(), FD.v() }),
                                                                                                                                    new jedd.PhysicalDomain[] { C1.v(), H1.v(), FD.v() }),
                                                                                                     new jedd.PhysicalDomain[] { H2.v() },
                                                                                                     new jedd.PhysicalDomain[] { H1.v() })));
    }
    
    public PointsToSetInternal make(Context c, VarNode v) { throw new RuntimeException("Not implemented"); }
    
    public PointsToSetInternal make(Context c, AllocDotField adf) { throw new RuntimeException("Not implemented"); }
    
    public Rvarc_var_objc_obj getReader(VarNode vn) {
        Rvarc_var_objc_objBDD ret =
          new Rvarc_var_objc_objBDD(new jedd.internal.RelationContainer(new jedd.Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                                        new jedd.PhysicalDomain[] { C2.v(), V1.v(), C1.v(), H1.v() },
                                                                        ("new soot.jimple.paddle.queue.Rvarc_var_objc_objBDD(...) at /" +
                                                                         "tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDP2Sets.jedd" +
                                                                         ":55,36-39"),
                                                                        jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(prop.pt),
                                                                                                                                   jedd.internal.Jedd.v().literal(new Object[] { vn },
                                                                                                                                                                  new jedd.Attribute[] { var.v() },
                                                                                                                                                                  new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                                                   new jedd.PhysicalDomain[] { V2.v() }),
                                                                                                       new jedd.PhysicalDomain[] { C3.v(), V2.v(), C2.v(), H2.v() },
                                                                                                       new jedd.PhysicalDomain[] { C2.v(), V1.v(), C1.v(), H1.v() })),
                                    "getReader");
        return ret;
    }
}
