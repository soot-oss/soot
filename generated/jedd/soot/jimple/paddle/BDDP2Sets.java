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
    
    public PointsToSetReadOnly get(VarNode v) {
        return new BDDPointsToSet(new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v() },
                                                                      new jedd.PhysicalDomain[] { H1.v() },
                                                                      ("new soot.jimple.paddle.BDDPointsToSet(...) at /home/olhotak/" +
                                                                       "soot-trunk/src/soot/jimple/paddle/BDDP2Sets.jedd:39,15-18"),
                                                                      jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(prop.pointsTo),
                                                                                                     jedd.internal.Jedd.v().literal(new Object[] { v },
                                                                                                                                    new jedd.Attribute[] { var.v() },
                                                                                                                                    new jedd.PhysicalDomain[] { V1.v() }),
                                                                                                     new jedd.PhysicalDomain[] { V1.v() })));
    }
    
    public PointsToSetReadOnly get(AllocDotField adf) {
        return new BDDPointsToSet(new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v() },
                                                                      new jedd.PhysicalDomain[] { H1.v() },
                                                                      ("new soot.jimple.paddle.BDDPointsToSet(...) at /home/olhotak/" +
                                                                       "soot-trunk/src/soot/jimple/paddle/BDDP2Sets.jedd:42,15-18"),
                                                                      jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(prop.fieldPt),
                                                                                                                                    jedd.internal.Jedd.v().literal(new Object[] { adf.getBase(), adf.getField() },
                                                                                                                                                                   new jedd.Attribute[] { base.v(), fld.v() },
                                                                                                                                                                   new jedd.PhysicalDomain[] { H1.v(), FD.v() }),
                                                                                                                                    new jedd.PhysicalDomain[] { H1.v(), FD.v() }),
                                                                                                     new jedd.PhysicalDomain[] { H2.v() },
                                                                                                     new jedd.PhysicalDomain[] { H1.v() })));
    }
    
    public PointsToSetInternal make(VarNode v) { throw new RuntimeException("Not implemented"); }
    
    public PointsToSetInternal make(AllocDotField adf) { throw new RuntimeException("Not implemented"); }
}
