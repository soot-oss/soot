package soot.jimple.paddle;

import soot.*;
import soot.util.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

public class BDDNodeInfo extends AbsNodeInfo {
    public BDDNodeInfo(Rvar_method_type locals,
                       Rvar_type globals,
                       Robj_method_type localallocs,
                       Robj_type globalallocs) {
        super(locals, globals, localallocs, globalallocs);
    }
    
    public boolean update() {
        boolean ret = false;
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(localMap),
                                           localMap.eqUnion(jedd.internal.Jedd.v().project(jedd.internal.Jedd.v().replace(locals.get(),
                                                                                                                          new jedd.PhysicalDomain[] { V1.v() },
                                                                                                                          new jedd.PhysicalDomain[] { V2.v() }),
                                                                                           new jedd.PhysicalDomain[] { T1.v() }))))
            ret = true;
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(globalSet),
                                           globalSet.eqUnion(jedd.internal.Jedd.v().project(globals.get(),
                                                                                            new jedd.PhysicalDomain[] { T1.v() }))))
            ret = true;
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(localallocMap),
                                           localallocMap.eqUnion(jedd.internal.Jedd.v().project(localallocs.get(),
                                                                                                new jedd.PhysicalDomain[] { T1.v() }))))
            ret = true;
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(globalallocSet),
                                           globalallocSet.eqUnion(jedd.internal.Jedd.v().project(globalallocs.get(),
                                                                                                 new jedd.PhysicalDomain[] { T1.v() }))))
            ret = true;
        return ret;
    }
    
    private final jedd.internal.RelationContainer localMap =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v(), method.v() },
                                          new jedd.PhysicalDomain[] { V2.v(), MS.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var, soot.jimple.padd" +
                                           "le.bdddomains.method> localMap = jedd.internal.Jedd.v().fals" +
                                           "eBDD() at /home/research/ccl/olhota/soot-trunk/src/soot/jimp" +
                                           "le/paddle/BDDNodeInfo.jedd:53,12-25"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer globalSet =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v() },
                                          new jedd.PhysicalDomain[] { V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.var> globalSet = jedd" +
                                           ".internal.Jedd.v().falseBDD() at /home/research/ccl/olhota/s" +
                                           "oot-trunk/src/soot/jimple/paddle/BDDNodeInfo.jedd:54,12-17"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer localallocMap =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), method.v() },
                                          new jedd.PhysicalDomain[] { H1.v(), MS.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj, soot.jimple.padd" +
                                           "le.bdddomains.method> localallocMap = jedd.internal.Jedd.v()" +
                                           ".falseBDD() at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                           "/jimple/paddle/BDDNodeInfo.jedd:55,12-25"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer globalallocSet =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v() },
                                          new jedd.PhysicalDomain[] { H1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj> globalallocSet =" +
                                           " jedd.internal.Jedd.v().falseBDD() at /home/research/ccl/olh" +
                                           "ota/soot-trunk/src/soot/jimple/paddle/BDDNodeInfo.jedd:56,12" +
                                           "-17"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public jedd.internal.RelationContainer localMap() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), var.v() },
                                                   new jedd.PhysicalDomain[] { MS.v(), V2.v() },
                                                   ("return localMap; at /home/research/ccl/olhota/soot-trunk/src" +
                                                    "/soot/jimple/paddle/BDDNodeInfo.jedd:58,38-44"),
                                                   localMap);
    }
    
    public jedd.internal.RelationContainer globalSet() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { var.v() },
                                                   new jedd.PhysicalDomain[] { V1.v() },
                                                   ("return globalSet; at /home/research/ccl/olhota/soot-trunk/sr" +
                                                    "c/soot/jimple/paddle/BDDNodeInfo.jedd:59,31-37"),
                                                   globalSet);
    }
    
    public jedd.internal.RelationContainer localallocMap() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v(), method.v() },
                                                   new jedd.PhysicalDomain[] { H1.v(), MS.v() },
                                                   ("return localallocMap; at /home/research/ccl/olhota/soot-trun" +
                                                    "k/src/soot/jimple/paddle/BDDNodeInfo.jedd:60,43-49"),
                                                   localallocMap);
    }
    
    public jedd.internal.RelationContainer globalallocSet() {
        return new jedd.internal.RelationContainer(new jedd.Attribute[] { obj.v() },
                                                   new jedd.PhysicalDomain[] { H1.v() },
                                                   ("return globalallocSet; at /home/research/ccl/olhota/soot-tru" +
                                                    "nk/src/soot/jimple/paddle/BDDNodeInfo.jedd:61,36-42"),
                                                   globalallocSet);
    }
}
