package soot.jimple.paddle;

import soot.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;
import jedd.*;

public class BDDPAG extends AbsPAG {
    BDDPAG(Rsrc_dst simple,
           Rsrc_fld_dst load,
           Rsrc_fld_dst store,
           Robj_var alloc,
           Qsrc_dst simpleout,
           Qsrc_fld_dst loadout,
           Qsrc_fld_dst storeout,
           Qobj_var allocout) {
        super(simple, load, store, alloc, simpleout, loadout, storeout, allocout);
    }
    
    public void update() {
        final jedd.internal.RelationContainer newSimple =
          new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v() },
                                              new PhysicalDomain[] { V1.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.dst:soot.jimple.paddl" +
                                               "e.bdddomains.V2> newSimple = jedd.internal.Jedd.v().minus(je" +
                                               "dd.internal.Jedd.v().read(simple.get()), simpleBDD); at /hom" +
                                               "e/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:44,1" +
                                               "9-28"),
                                              jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(simple.get()),
                                                                           simpleBDD));
        simpleout.add(new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v() },
                                                          new PhysicalDomain[] { V1.v(), V2.v() },
                                                          ("simpleout.add(newSimple) at /home/olhotak/soot-trunk/src/soo" +
                                                           "t/jimple/paddle/BDDPAG.jedd:45,8-17"),
                                                          newSimple));
        simpleBDD.eqUnion(newSimple);
        final jedd.internal.RelationContainer newAlloc =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v() },
                                              new PhysicalDomain[] { H1.v(), V1.v() },
                                              ("<soot.jimple.paddle.bdddomains.obj:soot.jimple.paddle.bdddom" +
                                               "ains.H1, soot.jimple.paddle.bdddomains.var:soot.jimple.paddl" +
                                               "e.bdddomains.V1> newAlloc = jedd.internal.Jedd.v().minus(jed" +
                                               "d.internal.Jedd.v().read(alloc.get()), allocBDD); at /home/o" +
                                               "lhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:48,19-2" +
                                               "7"),
                                              jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(alloc.get()),
                                                                           allocBDD));
        allocout.add(new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                                         new PhysicalDomain[] { V1.v(), H1.v() },
                                                         ("allocout.add(newAlloc) at /home/olhotak/soot-trunk/src/soot/" +
                                                          "jimple/paddle/BDDPAG.jedd:49,8-16"),
                                                         newAlloc));
        allocBDD.eqUnion(newAlloc);
        final jedd.internal.RelationContainer newLoad =
          new jedd.internal.RelationContainer(new Attribute[] { src.v(), fld.v(), dst.v() },
                                              new PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.fld:soot.jimple.paddl" +
                                               "e.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:soot.jimp" +
                                               "le.paddle.bdddomains.V2> newLoad = jedd.internal.Jedd.v().mi" +
                                               "nus(jedd.internal.Jedd.v().read(load.get()), loadBDD); at /h" +
                                               "ome/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:52" +
                                               ",24-31"),
                                              jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(load.get()),
                                                                           loadBDD));
        loadout.add(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), src.v(), dst.v() },
                                                        new PhysicalDomain[] { FD.v(), V1.v(), V2.v() },
                                                        ("loadout.add(newLoad) at /home/olhotak/soot-trunk/src/soot/ji" +
                                                         "mple/paddle/BDDPAG.jedd:53,8-15"),
                                                        newLoad));
        loadBDD.eqUnion(newLoad);
        final jedd.internal.RelationContainer newStore =
          new jedd.internal.RelationContainer(new Attribute[] { src.v(), fld.v(), dst.v() },
                                              new PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.src:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.fld:soot.jimple.paddl" +
                                               "e.bdddomains.FD, soot.jimple.paddle.bdddomains.dst:soot.jimp" +
                                               "le.paddle.bdddomains.V2> newStore = jedd.internal.Jedd.v().m" +
                                               "inus(jedd.internal.Jedd.v().read(store.get()), storeBDD); at" +
                                               " /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd" +
                                               ":56,24-32"),
                                              jedd.internal.Jedd.v().minus(jedd.internal.Jedd.v().read(store.get()),
                                                                           storeBDD));
        storeout.add(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), src.v(), dst.v() },
                                                         new PhysicalDomain[] { FD.v(), V1.v(), V2.v() },
                                                         ("storeout.add(newStore) at /home/olhotak/soot-trunk/src/soot/" +
                                                          "jimple/paddle/BDDPAG.jedd:57,8-16"),
                                                         newStore));
        storeBDD.eqUnion(newStore);
    }
    
    public Iterator simpleSources() {
        return new jedd.internal.RelationContainer(new Attribute[] { src.v() },
                                                   new PhysicalDomain[] { V1.v() },
                                                   ("jedd.internal.Jedd.v().project(simpleBDD, new jedd.PhysicalD" +
                                                    "omain[...]).iterator() at /home/olhotak/soot-trunk/src/soot/" +
                                                    "jimple/paddle/BDDPAG.jedd:62,35-43"),
                                                   jedd.internal.Jedd.v().project(simpleBDD,
                                                                                  new PhysicalDomain[] { V2.v() })).iterator();
    }
    
    public Iterator loadSources() {
        return new FieldRefIterator(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), var.v() },
                                                                        new PhysicalDomain[] { FD.v(), V2.v() },
                                                                        ("new soot.jimple.paddle.BDDPAG.FieldRefIterator(...) at /home" +
                                                                         "/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:65,15" +
                                                                         "-18"),
                                                                        jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(loadBDD,
                                                                                                                                      new PhysicalDomain[] { V2.v() }),
                                                                                                       new PhysicalDomain[] { V1.v() },
                                                                                                       new PhysicalDomain[] { V2.v() })));
    }
    
    public Iterator storeSources() {
        return new jedd.internal.RelationContainer(new Attribute[] { src.v() },
                                                   new PhysicalDomain[] { V1.v() },
                                                   ("jedd.internal.Jedd.v().project(storeBDD, new jedd.PhysicalDo" +
                                                    "main[...]).iterator() at /home/olhotak/soot-trunk/src/soot/j" +
                                                    "imple/paddle/BDDPAG.jedd:68,42-50"),
                                                   jedd.internal.Jedd.v().project(storeBDD,
                                                                                  new PhysicalDomain[] { FD.v(), V2.v() })).iterator();
    }
    
    public Iterator allocSources() {
        return new jedd.internal.RelationContainer(new Attribute[] { obj.v() },
                                                   new PhysicalDomain[] { H1.v() },
                                                   ("jedd.internal.Jedd.v().project(allocBDD, new jedd.PhysicalDo" +
                                                    "main[...]).iterator() at /home/olhotak/soot-trunk/src/soot/j" +
                                                    "imple/paddle/BDDPAG.jedd:71,34-42"),
                                                   jedd.internal.Jedd.v().project(allocBDD,
                                                                                  new PhysicalDomain[] { V1.v() })).iterator();
    }
    
    public Iterator simpleInvSources() {
        return new jedd.internal.RelationContainer(new Attribute[] { dst.v() },
                                                   new PhysicalDomain[] { V2.v() },
                                                   ("jedd.internal.Jedd.v().project(simpleBDD, new jedd.PhysicalD" +
                                                    "omain[...]).iterator() at /home/olhotak/soot-trunk/src/soot/" +
                                                    "jimple/paddle/BDDPAG.jedd:74,35-43"),
                                                   jedd.internal.Jedd.v().project(simpleBDD,
                                                                                  new PhysicalDomain[] { V1.v() })).iterator();
    }
    
    public Iterator loadInvSources() {
        return new jedd.internal.RelationContainer(new Attribute[] { dst.v() },
                                                   new PhysicalDomain[] { V2.v() },
                                                   ("jedd.internal.Jedd.v().project(loadBDD, new jedd.PhysicalDom" +
                                                    "ain[...]).iterator() at /home/olhotak/soot-trunk/src/soot/ji" +
                                                    "mple/paddle/BDDPAG.jedd:77,40-48"),
                                                   jedd.internal.Jedd.v().project(loadBDD,
                                                                                  new PhysicalDomain[] { V1.v(), FD.v() })).iterator();
    }
    
    public Iterator storeInvSources() {
        return new FieldRefIterator(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), var.v() },
                                                                        new PhysicalDomain[] { FD.v(), V2.v() },
                                                                        ("new soot.jimple.paddle.BDDPAG.FieldRefIterator(...) at /home" +
                                                                         "/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:80,15" +
                                                                         "-18"),
                                                                        jedd.internal.Jedd.v().project(storeBDD,
                                                                                                       new PhysicalDomain[] { V1.v() })));
    }
    
    public Iterator allocInvSources() {
        return new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                                   new PhysicalDomain[] { V1.v() },
                                                   ("jedd.internal.Jedd.v().project(allocBDD, new jedd.PhysicalDo" +
                                                    "main[...]).iterator() at /home/olhotak/soot-trunk/src/soot/j" +
                                                    "imple/paddle/BDDPAG.jedd:83,34-42"),
                                                   jedd.internal.Jedd.v().project(allocBDD,
                                                                                  new PhysicalDomain[] { H1.v() })).iterator();
    }
    
    public Iterator simpleLookup(VarNode key) {
        return new jedd.internal.RelationContainer(new Attribute[] { dst.v() },
                                                   new PhysicalDomain[] { V2.v() },
                                                   ("jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(s" +
                                                    "impleBDD), jedd.internal.Jedd.v().literal(new java.lang.Obje" +
                                                    "ct[...], new jedd.Attribute[...], new jedd.PhysicalDomain[.." +
                                                    ".]), new jedd.PhysicalDomain[...]).iterator() at /home/olhot" +
                                                    "ak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:87,55-63"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(simpleBDD),
                                                                                  jedd.internal.Jedd.v().literal(new Object[] { key },
                                                                                                                 new Attribute[] { src.v() },
                                                                                                                 new PhysicalDomain[] { V1.v() }),
                                                                                  new PhysicalDomain[] { V1.v() })).iterator();
    }
    
    public Iterator loadLookup(FieldRefNode key) {
        return new jedd.internal.RelationContainer(new Attribute[] { dst.v() },
                                                   new PhysicalDomain[] { V2.v() },
                                                   ("jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(l" +
                                                    "oadBDD), jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                    "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                    "), new jedd.PhysicalDomain[...]).iterator() at /home/olhotak" +
                                                    "/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:91,69-77"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(loadBDD),
                                                                                  jedd.internal.Jedd.v().literal(new Object[] { key.getBase(), key.getField() },
                                                                                                                 new Attribute[] { src.v(), fld.v() },
                                                                                                                 new PhysicalDomain[] { V1.v(), FD.v() }),
                                                                                  new PhysicalDomain[] { V1.v(), FD.v() })).iterator();
    }
    
    public Iterator storeLookup(VarNode key) {
        return new FieldRefIterator(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), var.v() },
                                                                        new PhysicalDomain[] { FD.v(), V2.v() },
                                                                        ("new soot.jimple.paddle.BDDPAG.FieldRefIterator(...) at /home" +
                                                                         "/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:94,15" +
                                                                         "-18"),
                                                                        jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(storeBDD),
                                                                                                       jedd.internal.Jedd.v().literal(new Object[] { key },
                                                                                                                                      new Attribute[] { src.v() },
                                                                                                                                      new PhysicalDomain[] { V1.v() }),
                                                                                                       new PhysicalDomain[] { V1.v() })));
    }
    
    public Iterator allocLookup(AllocNode key) {
        return new jedd.internal.RelationContainer(new Attribute[] { var.v() },
                                                   new PhysicalDomain[] { V1.v() },
                                                   ("jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(a" +
                                                    "llocBDD), jedd.internal.Jedd.v().literal(new java.lang.Objec" +
                                                    "t[...], new jedd.Attribute[...], new jedd.PhysicalDomain[..." +
                                                    "]), new jedd.PhysicalDomain[...]).iterator() at /home/olhota" +
                                                    "k/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:98,54-62"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(allocBDD),
                                                                                  jedd.internal.Jedd.v().literal(new Object[] { key },
                                                                                                                 new Attribute[] { obj.v() },
                                                                                                                 new PhysicalDomain[] { H1.v() }),
                                                                                  new PhysicalDomain[] { H1.v() })).iterator();
    }
    
    public Iterator simpleInvLookup(VarNode key) {
        return new jedd.internal.RelationContainer(new Attribute[] { src.v() },
                                                   new PhysicalDomain[] { V1.v() },
                                                   ("jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(s" +
                                                    "impleBDD), jedd.internal.Jedd.v().literal(new java.lang.Obje" +
                                                    "ct[...], new jedd.Attribute[...], new jedd.PhysicalDomain[.." +
                                                    ".]), new jedd.PhysicalDomain[...]).iterator() at /home/olhot" +
                                                    "ak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:101,55-63"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(simpleBDD),
                                                                                  jedd.internal.Jedd.v().literal(new Object[] { key },
                                                                                                                 new Attribute[] { dst.v() },
                                                                                                                 new PhysicalDomain[] { V2.v() }),
                                                                                  new PhysicalDomain[] { V2.v() })).iterator();
    }
    
    public Iterator loadInvLookup(VarNode key) {
        return new FieldRefIterator(new jedd.internal.RelationContainer(new Attribute[] { var.v(), fld.v() },
                                                                        new PhysicalDomain[] { V2.v(), FD.v() },
                                                                        ("new soot.jimple.paddle.BDDPAG.FieldRefIterator(...) at /home" +
                                                                         "/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:104,1" +
                                                                         "5-18"),
                                                                        jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(loadBDD),
                                                                                                                                      jedd.internal.Jedd.v().literal(new Object[] { key },
                                                                                                                                                                     new Attribute[] { dst.v() },
                                                                                                                                                                     new PhysicalDomain[] { V2.v() }),
                                                                                                                                      new PhysicalDomain[] { V2.v() }),
                                                                                                       new PhysicalDomain[] { V1.v() },
                                                                                                       new PhysicalDomain[] { V2.v() })));
    }
    
    public Iterator storeInvLookup(FieldRefNode key) {
        return new jedd.internal.RelationContainer(new Attribute[] { src.v() },
                                                   new PhysicalDomain[] { V1.v() },
                                                   ("jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(s" +
                                                    "toreBDD), jedd.internal.Jedd.v().literal(new java.lang.Objec" +
                                                    "t[...], new jedd.Attribute[...], new jedd.PhysicalDomain[..." +
                                                    "]), new jedd.PhysicalDomain[...]).iterator() at /home/olhota" +
                                                    "k/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:109,69-77"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(storeBDD),
                                                                                  jedd.internal.Jedd.v().literal(new Object[] { key.getBase(), key.getField() },
                                                                                                                 new Attribute[] { dst.v(), fld.v() },
                                                                                                                 new PhysicalDomain[] { V2.v(), FD.v() }),
                                                                                  new PhysicalDomain[] { V2.v(), FD.v() })).iterator();
    }
    
    public Iterator allocInvLookup(VarNode key) {
        return new jedd.internal.RelationContainer(new Attribute[] { obj.v() },
                                                   new PhysicalDomain[] { H1.v() },
                                                   ("jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(a" +
                                                    "llocBDD), jedd.internal.Jedd.v().literal(new java.lang.Objec" +
                                                    "t[...], new jedd.Attribute[...], new jedd.PhysicalDomain[..." +
                                                    "]), new jedd.PhysicalDomain[...]).iterator() at /home/olhota" +
                                                    "k/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:112,54-62"),
                                                   jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(allocBDD),
                                                                                  jedd.internal.Jedd.v().literal(new Object[] { key },
                                                                                                                 new Attribute[] { var.v() },
                                                                                                                 new PhysicalDomain[] { V1.v() }),
                                                                                  new PhysicalDomain[] { V1.v() })).iterator();
    }
    
    public Rsrc_dst allSimple() {
        return new Rsrc_dstBDD(new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v() },
                                                                   new PhysicalDomain[] { V1.v(), V2.v() },
                                                                   ("new soot.jimple.paddle.queue.Rsrc_dstBDD(...) at /home/olhot" +
                                                                    "ak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:115,41-44"),
                                                                   simpleBDD));
    }
    
    public Rsrc_fld_dst allLoad() {
        return new Rsrc_fld_dstBDD(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), src.v(), dst.v() },
                                                                       new PhysicalDomain[] { FD.v(), V1.v(), V2.v() },
                                                                       ("new soot.jimple.paddle.queue.Rsrc_fld_dstBDD(...) at /home/o" +
                                                                        "lhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:116,43-" +
                                                                        "46"),
                                                                       loadBDD));
    }
    
    public Rsrc_fld_dst allStore() {
        return new Rsrc_fld_dstBDD(new jedd.internal.RelationContainer(new Attribute[] { fld.v(), src.v(), dst.v() },
                                                                       new PhysicalDomain[] { FD.v(), V1.v(), V2.v() },
                                                                       ("new soot.jimple.paddle.queue.Rsrc_fld_dstBDD(...) at /home/o" +
                                                                        "lhotak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:117,44-" +
                                                                        "47"),
                                                                       storeBDD));
    }
    
    public Robj_var allAlloc() {
        return new Robj_varBDD(new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                                                   new PhysicalDomain[] { V1.v(), H1.v() },
                                                                   ("new soot.jimple.paddle.queue.Robj_varBDD(...) at /home/olhot" +
                                                                    "ak/soot-trunk/src/soot/jimple/paddle/BDDPAG.jedd:118,40-43"),
                                                                   allocBDD));
    }
    
    private static class FieldRefIterator implements Iterator {
        FieldRefIterator(final jedd.internal.RelationContainer bdd) {
            super();
            this.it =
              new jedd.internal.RelationContainer(new Attribute[] { fld.v(), var.v() },
                                                  new PhysicalDomain[] { FD.v(), V2.v() },
                                                  ("bdd.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-" +
                                                   "trunk/src/soot/jimple/paddle/BDDPAG.jedd:122,22-25"),
                                                  bdd).iterator(new Attribute[] { var.v(), fld.v() });
        }
        
        private Iterator it;
        
        public boolean hasNext() { return it.hasNext(); }
        
        public Object next() {
            Object[] ret = (Object[]) it.next();
            return ((VarNode) ret[0]).dot((PaddleField) ret[1]);
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
    }
    
    
    private final jedd.internal.RelationContainer simpleBDD =
      new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v() },
                                          new PhysicalDomain[] { V1.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.src, soot.jimple.padd" +
                                           "le.bdddomains.dst> simpleBDD = jedd.internal.Jedd.v().falseB" +
                                           "DD() at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDP" +
                                           "AG.jedd:133,12-22"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer loadBDD =
      new jedd.internal.RelationContainer(new Attribute[] { src.v(), fld.v(), dst.v() },
                                          new PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.src, soot.jimple.padd" +
                                           "le.bdddomains.fld, soot.jimple.paddle.bdddomains.dst> loadBD" +
                                           "D = jedd.internal.Jedd.v().falseBDD() at /home/olhotak/soot-" +
                                           "trunk/src/soot/jimple/paddle/BDDPAG.jedd:134,12-27"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer storeBDD =
      new jedd.internal.RelationContainer(new Attribute[] { src.v(), fld.v(), dst.v() },
                                          new PhysicalDomain[] { V1.v(), FD.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.src, soot.jimple.padd" +
                                           "le.bdddomains.fld, soot.jimple.paddle.bdddomains.dst> storeB" +
                                           "DD = jedd.internal.Jedd.v().falseBDD() at /home/olhotak/soot" +
                                           "-trunk/src/soot/jimple/paddle/BDDPAG.jedd:135,12-27"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    private final jedd.internal.RelationContainer allocBDD =
      new jedd.internal.RelationContainer(new Attribute[] { obj.v(), var.v() },
                                          new PhysicalDomain[] { H1.v(), V1.v() },
                                          ("private <soot.jimple.paddle.bdddomains.obj, soot.jimple.padd" +
                                           "le.bdddomains.var> allocBDD = jedd.internal.Jedd.v().falseBD" +
                                           "D() at /home/olhotak/soot-trunk/src/soot/jimple/paddle/BDDPA" +
                                           "G.jedd:136,12-22"),
                                          jedd.internal.Jedd.v().falseBDD());
}
