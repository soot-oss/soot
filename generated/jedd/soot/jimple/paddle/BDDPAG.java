package soot.jimple.paddle;

import soot.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;
import jedd.*;

public class BDDPAG extends AbsPAG {
    BDDPAG(Rsrcc_src_dstc_dst simple,
           Rsrcc_src_fld_dstc_dst load,
           Rsrcc_src_dstc_dst_fld store,
           Robjc_obj_varc_var alloc) {
        super(simple, load, store, alloc);
    }
    
    public boolean update() {
        boolean ret = false;
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(simpleBDD), simpleBDD.eqUnion(simple.get())))
            ret = true;
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(allocBDD), allocBDD.eqUnion(alloc.get())))
            ret = true;
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(loadBDD),
                                           loadBDD.eqUnion(jedd.internal.Jedd.v().replace(load.get(),
                                                                                          new PhysicalDomain[] { V1.v(), V2.v() },
                                                                                          new PhysicalDomain[] { V2.v(), V1.v() }))))
            ret = true;
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(storeBDD),
                                           storeBDD.eqUnion(jedd.internal.Jedd.v().replace(store.get(),
                                                                                           new PhysicalDomain[] { C1.v(), C2.v() },
                                                                                           new PhysicalDomain[] { C2.v(), C1.v() }))))
            ret = true;
        return ret;
    }
    
    public Iterator simpleSources() {
        return new ContextVarNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v() },
                                                                              new PhysicalDomain[] { V1.v(), C2.v() },
                                                                              ("new soot.jimple.paddle.BDDPAG.ContextVarNodeIterator(...) at" +
                                                                               " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:" +
                                                                               "46,15-18"),
                                                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(simpleBDD,
                                                                                                                                            new PhysicalDomain[] { C2.v(), V2.v() }),
                                                                                                             new PhysicalDomain[] { C1.v() },
                                                                                                             new PhysicalDomain[] { C2.v() })));
    }
    
    public Iterator loadSources() {
        return new FieldRefIterator(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), var.v(), varc.v() },
                                                                        new PhysicalDomain[] { FD.v(), V2.v(), C1.v() },
                                                                        ("new soot.jimple.paddle.BDDPAG.FieldRefIterator(...) at /tmp/" +
                                                                         "olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:49,15-" +
                                                                         "18"),
                                                                        jedd.internal.Jedd.v().project(loadBDD,
                                                                                                       new PhysicalDomain[] { C2.v(), V1.v() })));
    }
    
    public Iterator storeSources() {
        return new ContextVarNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v() },
                                                                              new PhysicalDomain[] { V1.v(), C2.v() },
                                                                              ("new soot.jimple.paddle.BDDPAG.ContextVarNodeIterator(...) at" +
                                                                               " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:" +
                                                                               "52,15-18"),
                                                                              jedd.internal.Jedd.v().project(storeBDD,
                                                                                                             new PhysicalDomain[] { C1.v(), FD.v(), V2.v() })));
    }
    
    public Iterator allocSources() {
        return new ContextAllocNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v() },
                                                                                new PhysicalDomain[] { C2.v(), H1.v() },
                                                                                ("new soot.jimple.paddle.BDDPAG.ContextAllocNodeIterator(...) " +
                                                                                 "at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jed" +
                                                                                 "d:55,15-18"),
                                                                                jedd.internal.Jedd.v().project(allocBDD,
                                                                                                               new PhysicalDomain[] { V1.v(), C1.v() })));
    }
    
    public Iterator simpleInvSources() {
        return new ContextVarNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v() },
                                                                              new PhysicalDomain[] { C2.v(), V1.v() },
                                                                              ("new soot.jimple.paddle.BDDPAG.ContextVarNodeIterator(...) at" +
                                                                               " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:" +
                                                                               "58,15-18"),
                                                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(simpleBDD,
                                                                                                                                            new PhysicalDomain[] { V1.v(), C1.v() }),
                                                                                                             new PhysicalDomain[] { V2.v() },
                                                                                                             new PhysicalDomain[] { V1.v() })));
    }
    
    public Iterator loadInvSources() {
        return new ContextVarNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { varc.v(), var.v() },
                                                                              new PhysicalDomain[] { C2.v(), V1.v() },
                                                                              ("new soot.jimple.paddle.BDDPAG.ContextVarNodeIterator(...) at" +
                                                                               " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:" +
                                                                               "61,15-18"),
                                                                              jedd.internal.Jedd.v().project(loadBDD,
                                                                                                             new PhysicalDomain[] { FD.v(), V2.v(), C1.v() })));
    }
    
    public Iterator storeInvSources() {
        return new FieldRefIterator(new jedd.internal.RelationContainer(new Attribute[] { varc.v(), fld.v(), var.v() },
                                                                        new PhysicalDomain[] { C1.v(), FD.v(), V2.v() },
                                                                        ("new soot.jimple.paddle.BDDPAG.FieldRefIterator(...) at /tmp/" +
                                                                         "olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:64,15-" +
                                                                         "18"),
                                                                        jedd.internal.Jedd.v().project(storeBDD,
                                                                                                       new PhysicalDomain[] { V1.v(), C2.v() })));
    }
    
    public Iterator allocInvSources() {
        return new ContextVarNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v() },
                                                                              new PhysicalDomain[] { V1.v(), C2.v() },
                                                                              ("new soot.jimple.paddle.BDDPAG.ContextVarNodeIterator(...) at" +
                                                                               " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:" +
                                                                               "67,15-18"),
                                                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(allocBDD,
                                                                                                                                            new PhysicalDomain[] { C2.v(), H1.v() }),
                                                                                                             new PhysicalDomain[] { C1.v() },
                                                                                                             new PhysicalDomain[] { C2.v() })));
    }
    
    public Iterator simpleLookup(Context ctxt, VarNode key) {
        return new ContextVarNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v() },
                                                                              new PhysicalDomain[] { V1.v(), C2.v() },
                                                                              ("new soot.jimple.paddle.BDDPAG.ContextVarNodeIterator(...) at" +
                                                                               " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:" +
                                                                               "71,15-18"),
                                                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(simpleBDD),
                                                                                                                                            jedd.internal.Jedd.v().literal(new Object[] { ctxt, key },
                                                                                                                                                                           new Attribute[] { srcc.v(), src.v() },
                                                                                                                                                                           new PhysicalDomain[] { C1.v(), V1.v() }),
                                                                                                                                            new PhysicalDomain[] { C1.v(), V1.v() }),
                                                                                                             new PhysicalDomain[] { V2.v() },
                                                                                                             new PhysicalDomain[] { V1.v() })));
    }
    
    public Iterator loadLookup(Context ctxt, FieldRefNode key) {
        return new ContextVarNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v() },
                                                                              new PhysicalDomain[] { V1.v(), C2.v() },
                                                                              ("new soot.jimple.paddle.BDDPAG.ContextVarNodeIterator(...) at" +
                                                                               " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:" +
                                                                               "76,15-18"),
                                                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(loadBDD),
                                                                                                             jedd.internal.Jedd.v().literal(new Object[] { ctxt, key.base(), key.field() },
                                                                                                                                            new Attribute[] { srcc.v(), src.v(), fld.v() },
                                                                                                                                            new PhysicalDomain[] { C1.v(), V2.v(), FD.v() }),
                                                                                                             new PhysicalDomain[] { C1.v(), V2.v(), FD.v() })));
    }
    
    public Iterator storeLookup(Context ctxt, VarNode key) {
        return new FieldRefIterator(new jedd.internal.RelationContainer(new Attribute[] { varc.v(), fld.v(), var.v() },
                                                                        new PhysicalDomain[] { C1.v(), FD.v(), V2.v() },
                                                                        ("new soot.jimple.paddle.BDDPAG.FieldRefIterator(...) at /tmp/" +
                                                                         "olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:81,15-" +
                                                                         "18"),
                                                                        jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(storeBDD),
                                                                                                       jedd.internal.Jedd.v().literal(new Object[] { ctxt, key },
                                                                                                                                      new Attribute[] { srcc.v(), src.v() },
                                                                                                                                      new PhysicalDomain[] { C2.v(), V1.v() }),
                                                                                                       new PhysicalDomain[] { C2.v(), V1.v() })));
    }
    
    public Iterator allocLookup(Context ctxt, AllocNode key) {
        return new ContextVarNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v() },
                                                                              new PhysicalDomain[] { V1.v(), C2.v() },
                                                                              ("new soot.jimple.paddle.BDDPAG.ContextVarNodeIterator(...) at" +
                                                                               " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:" +
                                                                               "85,15-18"),
                                                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(allocBDD),
                                                                                                                                            jedd.internal.Jedd.v().literal(new Object[] { ctxt, key },
                                                                                                                                                                           new Attribute[] { objc.v(), obj.v() },
                                                                                                                                                                           new PhysicalDomain[] { C2.v(), H1.v() }),
                                                                                                                                            new PhysicalDomain[] { C2.v(), H1.v() }),
                                                                                                             new PhysicalDomain[] { C1.v() },
                                                                                                             new PhysicalDomain[] { C2.v() })));
    }
    
    public Iterator simpleInvLookup(Context ctxt, VarNode key) {
        return new ContextVarNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v() },
                                                                              new PhysicalDomain[] { V1.v(), C2.v() },
                                                                              ("new soot.jimple.paddle.BDDPAG.ContextVarNodeIterator(...) at" +
                                                                               " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:" +
                                                                               "89,15-18"),
                                                                              jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(simpleBDD),
                                                                                                                                            jedd.internal.Jedd.v().literal(new Object[] { ctxt, key },
                                                                                                                                                                           new Attribute[] { dstc.v(), dst.v() },
                                                                                                                                                                           new PhysicalDomain[] { C2.v(), V2.v() }),
                                                                                                                                            new PhysicalDomain[] { C2.v(), V2.v() }),
                                                                                                             new PhysicalDomain[] { C1.v() },
                                                                                                             new PhysicalDomain[] { C2.v() })));
    }
    
    public Iterator loadInvLookup(Context ctxt, VarNode key) {
        return new FieldRefIterator(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), var.v(), varc.v() },
                                                                        new PhysicalDomain[] { FD.v(), V2.v(), C1.v() },
                                                                        ("new soot.jimple.paddle.BDDPAG.FieldRefIterator(...) at /tmp/" +
                                                                         "olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:94,15-" +
                                                                         "18"),
                                                                        jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(loadBDD),
                                                                                                       jedd.internal.Jedd.v().literal(new Object[] { ctxt, key },
                                                                                                                                      new Attribute[] { dstc.v(), dst.v() },
                                                                                                                                      new PhysicalDomain[] { C2.v(), V1.v() }),
                                                                                                       new PhysicalDomain[] { C2.v(), V1.v() })));
    }
    
    public Iterator storeInvLookup(Context ctxt, FieldRefNode key) {
        return new ContextVarNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v() },
                                                                              new PhysicalDomain[] { V1.v(), C2.v() },
                                                                              ("new soot.jimple.paddle.BDDPAG.ContextVarNodeIterator(...) at" +
                                                                               " /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:" +
                                                                               "99,15-18"),
                                                                              jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(storeBDD),
                                                                                                             jedd.internal.Jedd.v().literal(new Object[] { ctxt, key.base(), key.field() },
                                                                                                                                            new Attribute[] { dstc.v(), dst.v(), fld.v() },
                                                                                                                                            new PhysicalDomain[] { C1.v(), V2.v(), FD.v() }),
                                                                                                             new PhysicalDomain[] { C1.v(), V2.v(), FD.v() })));
    }
    
    public Iterator allocInvLookup(Context ctxt, VarNode key) {
        return new ContextAllocNodeIterator(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v() },
                                                                                new PhysicalDomain[] { C2.v(), H1.v() },
                                                                                ("new soot.jimple.paddle.BDDPAG.ContextAllocNodeIterator(...) " +
                                                                                 "at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jed" +
                                                                                 "d:104,15-18"),
                                                                                jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(allocBDD),
                                                                                                               jedd.internal.Jedd.v().literal(new Object[] { ctxt, key },
                                                                                                                                              new Attribute[] { varc.v(), var.v() },
                                                                                                                                              new PhysicalDomain[] { C1.v(), V1.v() }),
                                                                                                               new PhysicalDomain[] { C1.v(), V1.v() })));
    }
    
    public Rsrcc_src_dstc_dst allSimple() {
        return new Rsrcc_src_dstc_dstBDD(new jedd.internal.RelationContainer(new Attribute[] { dstc.v(), dst.v(), src.v(), srcc.v() },
                                                                             new PhysicalDomain[] { C2.v(), V2.v(), V1.v(), C1.v() },
                                                                             ("new soot.jimple.paddle.queue.Rsrcc_src_dstc_dstBDD(...) at /" +
                                                                              "tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:10" +
                                                                              "8,51-54"),
                                                                             simpleBDD),
                                         "allsimple");
    }
    
    public Rsrcc_src_fld_dstc_dst allLoad() {
        return new Rsrcc_src_fld_dstc_dstBDD(new jedd.internal.RelationContainer(new Attribute[] { dstc.v(), fld.v(), dst.v(), src.v(), srcc.v() },
                                                                                 new PhysicalDomain[] { C2.v(), FD.v(), V2.v(), V1.v(), C1.v() },
                                                                                 ("new soot.jimple.paddle.queue.Rsrcc_src_fld_dstc_dstBDD(...) " +
                                                                                  "at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jed" +
                                                                                  "d:109,53-56"),
                                                                                 jedd.internal.Jedd.v().replace(loadBDD,
                                                                                                                new PhysicalDomain[] { V1.v(), V2.v() },
                                                                                                                new PhysicalDomain[] { V2.v(), V1.v() })),
                                             "allload");
    }
    
    public Rsrcc_src_dstc_dst_fld allStore() {
        return new Rsrcc_src_dstc_dst_fldBDD(new jedd.internal.RelationContainer(new Attribute[] { dstc.v(), fld.v(), dst.v(), src.v(), srcc.v() },
                                                                                 new PhysicalDomain[] { C2.v(), FD.v(), V2.v(), V1.v(), C1.v() },
                                                                                 ("new soot.jimple.paddle.queue.Rsrcc_src_dstc_dst_fldBDD(...) " +
                                                                                  "at /tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jed" +
                                                                                  "d:110,54-57"),
                                                                                 jedd.internal.Jedd.v().replace(storeBDD,
                                                                                                                new PhysicalDomain[] { C1.v(), C2.v() },
                                                                                                                new PhysicalDomain[] { C2.v(), C1.v() })),
                                             "allstore");
    }
    
    public Robjc_obj_varc_var allAlloc() {
        return new Robjc_obj_varc_varBDD(new jedd.internal.RelationContainer(new Attribute[] { objc.v(), var.v(), varc.v(), obj.v() },
                                                                             new PhysicalDomain[] { C2.v(), V1.v(), C1.v(), H1.v() },
                                                                             ("new soot.jimple.paddle.queue.Robjc_obj_varc_varBDD(...) at /" +
                                                                              "tmp/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:11" +
                                                                              "1,50-53"),
                                                                             allocBDD),
                                         "allalloc");
    }
    
    private static class FieldRefIterator implements Iterator {
        FieldRefIterator(final jedd.internal.RelationContainer bdd) {
            super();
            this.it =
              new jedd.internal.RelationContainer(new Attribute[] { fld.v(), var.v(), varc.v() },
                                                  new PhysicalDomain[] { FD.v(), V2.v(), C1.v() },
                                                  ("bdd.iterator(new jedd.Attribute[...]) at /tmp/olhotak/soot-t" +
                                                   "runk/src/soot/jimple/paddle/BDDPAG.jedd:115,22-25"),
                                                  bdd).iterator(new Attribute[] { varc.v(), var.v(), fld.v() });
        }
        
        private Iterator it;
        
        public boolean hasNext() { return it.hasNext(); }
        
        public Object next() {
            Object[] ret = (Object[]) it.next();
            return ContextFieldRefNode.make((Context) ret[0], ((VarNode) ret[1]).dot((PaddleField) ret[2]));
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
    }
    
    
    private static class ContextVarNodeIterator implements Iterator {
        ContextVarNodeIterator(final jedd.internal.RelationContainer bdd) {
            super();
            this.it =
              new jedd.internal.RelationContainer(new Attribute[] { var.v(), varc.v() },
                                                  new PhysicalDomain[] { V1.v(), C2.v() },
                                                  ("bdd.iterator(new jedd.Attribute[...]) at /tmp/olhotak/soot-t" +
                                                   "runk/src/soot/jimple/paddle/BDDPAG.jedd:129,22-25"),
                                                  bdd).iterator(new Attribute[] { varc.v(), var.v() });
        }
        
        private Iterator it;
        
        public boolean hasNext() { return it.hasNext(); }
        
        public Object next() {
            Object[] ret = (Object[]) it.next();
            return ContextVarNode.make((Context) ret[0], (Node) ret[1]);
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
    }
    
    
    private static class ContextAllocNodeIterator implements Iterator {
        ContextAllocNodeIterator(final jedd.internal.RelationContainer bdd) {
            super();
            this.it =
              new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v() },
                                                  new PhysicalDomain[] { C2.v(), H1.v() },
                                                  ("bdd.iterator(new jedd.Attribute[...]) at /tmp/olhotak/soot-t" +
                                                   "runk/src/soot/jimple/paddle/BDDPAG.jedd:142,22-25"),
                                                  bdd).iterator(new Attribute[] { objc.v(), obj.v() });
        }
        
        private Iterator it;
        
        public boolean hasNext() { return it.hasNext(); }
        
        public Object next() {
            Object[] ret = (Object[]) it.next();
            return ContextAllocNode.make((Context) ret[0], (Node) ret[1]);
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
    }
    
    
    private final jedd.internal.RelationContainer simpleBDD =
      new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                          new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcc, soot.jimple.pad" +
                                           "dle.bdddomains.src, soot.jimple.paddle.bdddomains.dstc, soot" +
                                           ".jimple.paddle.bdddomains.dst> simpleBDD = jedd.internal.Jed" +
                                           "d.v().falseBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/" +
                                           "paddle/BDDPAG.jedd:153,12-34"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer loadBDD =
      new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                          new PhysicalDomain[] { C1.v(), V2.v(), FD.v(), C2.v(), V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcc, soot.jimple.pad" +
                                           "dle.bdddomains.src, soot.jimple.paddle.bdddomains.fld, soot." +
                                           "jimple.paddle.bdddomains.dstc, soot.jimple.paddle.bdddomains" +
                                           ".dst> loadBDD = jedd.internal.Jedd.v().falseBDD() at /tmp/ol" +
                                           "hotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:154,12-3" +
                                           "9"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer storeBDD =
      new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() },
                                          new PhysicalDomain[] { C2.v(), V1.v(), FD.v(), C1.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcc, soot.jimple.pad" +
                                           "dle.bdddomains.src, soot.jimple.paddle.bdddomains.fld, soot." +
                                           "jimple.paddle.bdddomains.dstc, soot.jimple.paddle.bdddomains" +
                                           ".dst> storeBDD = jedd.internal.Jedd.v().falseBDD() at /tmp/o" +
                                           "lhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:155,12-" +
                                           "39"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allocBDD =
      new jedd.internal.RelationContainer(new Attribute[] { objc.v(), obj.v(), varc.v(), var.v() },
                                          new PhysicalDomain[] { C2.v(), H1.v(), C1.v(), V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.objc, soot.jimple.pad" +
                                           "dle.bdddomains.obj, soot.jimple.paddle.bdddomains.varc, soot" +
                                           ".jimple.paddle.bdddomains.var> allocBDD = jedd.internal.Jedd" +
                                           ".v().falseBDD() at /tmp/olhotak/soot-trunk/src/soot/jimple/p" +
                                           "addle/BDDPAG.jedd:156,12-34"),
                                          jedd.internal.Jedd.v().falseBDD());
}
