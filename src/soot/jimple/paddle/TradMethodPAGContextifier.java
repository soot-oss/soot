/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.paddle;
import soot.*;
import soot.util.*;
import soot.jimple.paddle.queue.*;
import java.util.*;

/** Instantiates the pointer flow edges of methods in specific contexts.
 * @author Ondrej Lhotak
 */
public class TradMethodPAGContextifier extends AbsMethodPAGContextifier
{ 
    public TradMethodPAGContextifier(
        Rsrc_dst simple,
        Rsrc_fld_dst load,
        Rsrc_fld_dst store,
        Robj_var alloc,

        Rvar_method_type locals,
        Rvar_type globals,
        Robj_method_type localallocs,
        Robj_type globalallocs,

        Rctxt_method rcout,
        Rsrcm_stmt_kind_tgtm_src_dst parms,
        Rsrcm_stmt_kind_tgtm_src_dst rets,
        Rsrcc_srcm_stmt_kind_tgtc_tgtm calls,

        Qsrcc_src_dstc_dst csimple,
        Qsrcc_src_fld_dstc_dst cload,
        Qsrcc_src_fld_dstc_dst cstore,
        Qobjc_obj_varc_var calloc ) 
    {
        super(
            simple, load, store, alloc,
            locals, globals, localallocs, globalallocs,
            rcout, parms, rets, calls,
            csimple, cload, cstore, calloc );
    }

    public void update() {
        for( Iterator tIt = locals.iterator(); tIt.hasNext(); ) {
            final Rvar_method_type.Tuple t = (Rvar_method_type.Tuple) tIt.next();
            localMap.put(t.var(), t.method());
        }
        for( Iterator tIt = globals.iterator(); tIt.hasNext(); ) {
            final Rvar_type.Tuple t = (Rvar_type.Tuple) tIt.next();
            globalSet.add(t.var());
        }
        for( Iterator tIt = localallocs.iterator(); tIt.hasNext(); ) {
            final Robj_method_type.Tuple t = (Robj_method_type.Tuple) tIt.next();
            localallocMap.put(t.obj(), t.method());
        }
        for( Iterator tIt = globalallocs.iterator(); tIt.hasNext(); ) {
            final Robj_type.Tuple t = (Robj_type.Tuple) tIt.next();
            globalallocSet.add(t.obj());
        }
        for( Iterator tIt = simple.iterator(); tIt.hasNext(); ) {
            final Rsrc_dst.Tuple t = (Rsrc_dst.Tuple) tIt.next();
            if( global(t.src()) ) {
                if( global(t.dst()) ) {
                    addSimple( null, t.src(), null, t.dst() );
                } else {
                    mpag( method( t.dst() ) ).simple.add(t);
                }
            } else {
                if( global(t.dst()) ) {
                    mpag( method( t.src() ) ).simple.add(t);
                } else {
                    SootMethod m = method(t.src());
                    if( m != method(t.dst()) ) throw new RuntimeException(t.toString());
                    mpag(m).simple.add(t);
                }
            }
        }
        for( Iterator tIt = store.iterator(); tIt.hasNext(); ) {
            final Rsrc_fld_dst.Tuple t = (Rsrc_fld_dst.Tuple) tIt.next();
            if( global(t.src()) ) {
                if( global(t.dst()) ) {
                    addStore( null, t.src(), t.fld(), null, t.dst() );
                } else {
                    mpag( method( t.dst() ) ).store.add(t);
                }
            } else {
                if( global(t.dst()) ) {
                    mpag( method( t.src() ) ).store.add(t);
                } else {
                    SootMethod m = method(t.src());
                    if( m != method(t.dst()) ) throw new RuntimeException(t.toString());
                    mpag(m).store.add(t);
                }
            }
        }
        for( Iterator tIt = load.iterator(); tIt.hasNext(); ) {
            final Rsrc_fld_dst.Tuple t = (Rsrc_fld_dst.Tuple) tIt.next();
            if( global(t.src()) ) {
                if( global(t.dst()) ) {
                    addLoad( null, t.src(), t.fld(), null, t.dst() );
                } else {
                    mpag( method( t.dst() ) ).load.add(t);
                }
            } else {
                if( global(t.dst()) ) {
                    mpag( method( t.src() ) ).load.add(t);
                } else {
                    SootMethod m = method(t.src());
                    if( m != method(t.dst()) ) throw new RuntimeException(t.toString());
                    mpag(m).load.add(t);
                }
            }
        }
        for( Iterator tIt = alloc.iterator(); tIt.hasNext(); ) {
            final Robj_var.Tuple t = (Robj_var.Tuple) tIt.next();
            if( global(t.obj()) ) {
                if( global(t.var()) ) {
                    addAlloc( null, t.obj(), null, t.var() );
                } else {
                    mpag( method( t.var() ) ).alloc.add(t);
                }
            } else {
                if( global(t.var()) ) {
                    mpag( method( t.obj() ) ).alloc.add(t);
                } else {
                    SootMethod m = method(t.obj());
                    if( m != method(t.var()) ) throw new RuntimeException(t.toString());
                    mpag(m).alloc.add(t);
                }
            }
        }
        for( Iterator tIt = rcout.iterator(); tIt.hasNext(); ) {
            final Rctxt_method.Tuple t = (Rctxt_method.Tuple) tIt.next();
            MethodPAG mpag = mpag(t.method());
            for( Iterator eIt = mpag.rsimple.copy().iterator(); eIt.hasNext(); ) {
                final Rsrc_dst.Tuple e = (Rsrc_dst.Tuple) eIt.next();
                addSimple( t.ctxt(), e.src(), t.ctxt(), e.dst() );
            }
            for( Iterator eIt = mpag.rstore.copy().iterator(); eIt.hasNext(); ) {
                final Rsrc_fld_dst.Tuple e = (Rsrc_fld_dst.Tuple) eIt.next();
                addStore( t.ctxt(), e.src(), e.fld(), t.ctxt(), e.dst() );
            }
            for( Iterator eIt = mpag.rload.copy().iterator(); eIt.hasNext(); ) {
                final Rsrc_fld_dst.Tuple e = (Rsrc_fld_dst.Tuple) eIt.next();
                addLoad( t.ctxt(), e.src(), e.fld(), t.ctxt(), e.dst() );
            }
            for( Iterator eIt = mpag.ralloc.copy().iterator(); eIt.hasNext(); ) {
                final Robj_var.Tuple e = (Robj_var.Tuple) eIt.next();
                addAlloc( t.ctxt(), e.obj(), t.ctxt(), e.var() );
            }
        }
        for( Iterator tIt = parms.iterator(); tIt.hasNext(); ) {
            final Rsrcm_stmt_kind_tgtm_src_dst.Tuple t = (Rsrcm_stmt_kind_tgtm_src_dst.Tuple) tIt.next();
            Cons edge = new Cons(new Cons(t.srcm(), t.stmt()),
                                 new Cons(t.kind(), t.tgtm()));
            Collection parmColl = (Collection) parmMap.get(edge);
            if( parmColl == null ) parmMap.put(edge, parmColl = new ArrayList());
            parmColl.add( new Cons(t.src(), t.dst()));
        }
        for( Iterator tIt = rets.iterator(); tIt.hasNext(); ) {
            final Rsrcm_stmt_kind_tgtm_src_dst.Tuple t = (Rsrcm_stmt_kind_tgtm_src_dst.Tuple) tIt.next();
            Cons edge = new Cons(new Cons(t.srcm(), t.stmt()),
                                 new Cons(t.kind(), t.tgtm()));
            Collection retColl = (Collection) retMap.get(edge);
            if( retColl == null ) retMap.put(edge, retColl = new ArrayList());
            retColl.add( new Cons(t.src(), t.dst()));
        }
        for( Iterator tIt = calls.iterator(); tIt.hasNext(); ) {
            final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) tIt.next();
            Cons edge = new Cons(new Cons(t.srcm(), t.stmt()),
                                 new Cons(t.kind(), t.tgtm()));
            Collection parmColl = (Collection) parmMap.get(edge);
            if( parmColl != null ) {
                for( Iterator callIt = parmColl.iterator(); callIt.hasNext(); ) {
                    final Cons call = (Cons) callIt.next();
                    addSimple( t.srcc(), (VarNode)call.car(),
                                 t.tgtc(), (VarNode)call.cdr() );
                }
            }
            Collection retColl = (Collection) retMap.get(edge);
            if( retColl != null ) {
                for( Iterator retIt = retColl.iterator(); retIt.hasNext(); ) {
                    final Cons ret = (Cons) retIt.next();
                    addSimple( t.tgtc(), (VarNode)ret.car(),
                                 t.srcc(), (VarNode)ret.cdr() );
                }
            }
        }
    }

    private void addSimple( Context srcc, VarNode src, Context dstc, VarNode dst ) {
        if( src instanceof GlobalVarNode ) srcc = null;
        if( dst instanceof GlobalVarNode ) dstc = null;
        csimple.add( srcc, src, dstc, dst );
    }

    private void addStore( Context srcc, VarNode src, PaddleField fld, Context dstc, VarNode dst ) {
        if( src instanceof GlobalVarNode ) srcc = null;
        if( dst instanceof GlobalVarNode ) dstc = null;
        cstore.add( srcc, src, fld, dstc, dst );
    }

    private void addLoad( Context srcc, VarNode src, PaddleField fld, Context dstc, VarNode dst ) {
        if( src instanceof GlobalVarNode ) srcc = null;
        if( dst instanceof GlobalVarNode ) dstc = null;
        cload.add( srcc, src, fld, dstc, dst );
    }

    private void addAlloc( Context objc, AllocNode obj, Context varc, VarNode var ) {
        if( obj instanceof GlobalAllocNode ) objc = null;
        if( var instanceof GlobalVarNode ) varc = null;
        calloc.add( objc, obj, varc, var );
    }

    private SootMethod method(VarNode v) {
        SootMethod ret = (SootMethod) localMap.get(v);
        if( ret == null ) throw new RuntimeException("no method: "+v );
        return ret;
    }
    private boolean global(VarNode v) {
        return globalSet.contains(v);
    }

    private SootMethod method(AllocNode v) {
        SootMethod ret = (SootMethod) localallocMap.get(v);
        if( ret == null ) throw new RuntimeException("no method: "+v );
        return ret;
    }
    private boolean global(AllocNode v) {
        return globalallocSet.contains(v);
    }

    private MethodPAG mpag(SootMethod m) {
        MethodPAG ret = (MethodPAG) methodPAGs.get(m);
        if( ret == null ) methodPAGs.put( m, ret = new MethodPAG() );
        return ret;
    }

    private static class MethodPAG {
        Qsrc_dstTrad simple = new Qsrc_dstTrad("mpagsimple");
        Qsrc_fld_dstTrad load = new Qsrc_fld_dstTrad("mpagload");
        Qsrc_fld_dstTrad store = new Qsrc_fld_dstTrad("mpagstore");
        Qobj_varTrad alloc = new Qobj_varTrad("mpagalloc");
        Rsrc_dstTrad rsimple = (Rsrc_dstTrad) simple.reader("mpag");
        Rsrc_fld_dstTrad rload = (Rsrc_fld_dstTrad) load.reader("mpag");
        Rsrc_fld_dstTrad rstore = (Rsrc_fld_dstTrad) store.reader("mpag");
        Robj_varTrad ralloc = (Robj_varTrad) alloc.reader("mpag");
    }

    private LargeNumberedMap localMap =
        new LargeNumberedMap(PaddleNumberers.v().varNodeNumberer());
    private NumberedSet globalSet =
        new NumberedSet(PaddleNumberers.v().varNodeNumberer());
    private LargeNumberedMap localallocMap =
        new LargeNumberedMap(PaddleNumberers.v().allocNodeNumberer());
    private NumberedSet globalallocSet =
        new NumberedSet(PaddleNumberers.v().allocNodeNumberer());

    private LargeNumberedMap methodPAGs = new LargeNumberedMap(Scene.v().getMethodNumberer());
    private Map parmMap = new HashMap();
    private Map retMap = new HashMap();
}

