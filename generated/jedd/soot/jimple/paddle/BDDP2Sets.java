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
        return new BDDPointsToSet(new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), objc.v() },
                                                                      new jedd.PhysicalDomain[] { H1.v(), C2.v() },
                                                                      ("new soot.jimple.paddle.BDDPointsToSet(...) at /home/olhotak/" +
                                                                       "soot-trunk/src/soot/jimple/paddle/BDDP2Sets.jedd:39,15-18"),
                                                                      jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(prop.pointsTo),
                                                                                                     jedd.internal.Jedd.v().literal(new Object[] { ctxt, v },
                                                                                                                                    new jedd.Attribute[] { varc.v(), var.v() },
                                                                                                                                    new jedd.PhysicalDomain[] { C1.v(), V2.v() }),
                                                                                                     new jedd.PhysicalDomain[] { C1.v(), V2.v() })));
    }
    
    public PointsToSetReadOnly get(Context ctxt, AllocDotField adf) {
        return new BDDPointsToSet(new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), objc.v() },
                                                                      new jedd.PhysicalDomain[] { H1.v(), C2.v() },
                                                                      ("new soot.jimple.paddle.BDDPointsToSet(...) at /home/olhotak/" +
                                                                       "soot-trunk/src/soot/jimple/paddle/BDDP2Sets.jedd:43,15-18"),
                                                                      jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(prop.fieldPt),
                                                                                                                                    jedd.internal.Jedd.v().literal(new Object[] { ctxt, adf.base(), adf.field() },
                                                                                                                                                                   new jedd.Attribute[] { basec.v(), base.v(), fld.v() },
                                                                                                                                                                   new jedd.PhysicalDomain[] { C1.v(), H1.v(), FD.v() }),
                                                                                                                                    new jedd.PhysicalDomain[] { C1.v(), H1.v(), FD.v() }),
                                                                                                     new jedd.PhysicalDomain[] { H2.v() },
                                                                                                     new jedd.PhysicalDomain[] { H1.v() })));
    }
    
    public PointsToSetInternal make(Context c, VarNode v) { throw new RuntimeException("Not implemented"); }
    
    public PointsToSetInternal make(Context c, AllocDotField adf) { throw new RuntimeException("Not implemented"); }
}
