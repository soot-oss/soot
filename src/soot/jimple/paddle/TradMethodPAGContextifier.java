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
    private TradNodeInfo ni;
    public TradMethodPAGContextifier(
        TradNodeInfo ni,
        Rsrc_dst simple,
        Rsrc_fld_dst load,
        Rsrc_dst_fld store,
        Robj_var alloc,

        Rctxt_method rcout,

        Qsrcc_src_dstc_dst csimple,
        Qsrcc_src_fld_dstc_dst cload,
        Qsrcc_src_dstc_dst_fld cstore,
        Qobjc_obj_varc_var calloc ) 
    {
        super(
            simple, load, store, alloc,
            rcout,
            csimple, cload, cstore, calloc );
        this.ni = ni;
    }

    public boolean update() {
        boolean change = false;
        if( !PaddleScene.v().depMan.checkPrec(this) ) throw new RuntimeException();
        for( Iterator tIt = simple.iterator(); tIt.hasNext(); ) {
            final Rsrc_dst.Tuple t = (Rsrc_dst.Tuple) tIt.next();
            if( ni.global(t.src()) ) {
                if( ni.global(t.dst()) ) {
                    addSimple( null, t.src(), null, t.dst() );
                    change = true;
                } else {
                    mpag( ni.method( t.dst() ) ).simple.add(t);
                }
            } else {
                if( ni.global(t.dst()) ) {
                    mpag( ni.method( t.src() ) ).simple.add(t);
                } else {
                    SootMethod m = ni.method(t.src());
                    if( m != ni.method(t.dst()) ) throw new RuntimeException(t.toString());
                    mpag(m).simple.add(t);
                }
            }
        }
        for( Iterator tIt = store.iterator(); tIt.hasNext(); ) {
            final Rsrc_dst_fld.Tuple t = (Rsrc_dst_fld.Tuple) tIt.next();
            if( ni.global(t.src()) ) {
                if( ni.global(t.dst()) ) {
                    addStore( null, t.src(), t.fld(), null, t.dst() );
                    change = true;
                } else {
                    mpag( ni.method( t.dst() ) ).store.add(t);
                }
            } else {
                if( ni.global(t.dst()) ) {
                    mpag( ni.method( t.src() ) ).store.add(t);
                } else {
                    SootMethod m = ni.method(t.src());
                    if( m != ni.method(t.dst()) ) throw new RuntimeException(t.toString());
                    mpag(m).store.add(t);
                }
            }
        }
        for( Iterator tIt = load.iterator(); tIt.hasNext(); ) {
            final Rsrc_fld_dst.Tuple t = (Rsrc_fld_dst.Tuple) tIt.next();
            if( ni.global(t.src()) ) {
                if( ni.global(t.dst()) ) {
                    addLoad( null, t.src(), t.fld(), null, t.dst() );
                    change = true;
                } else {
                    mpag( ni.method( t.dst() ) ).load.add(t);
                }
            } else {
                if( ni.global(t.dst()) ) {
                    mpag( ni.method( t.src() ) ).load.add(t);
                } else {
                    SootMethod m = ni.method(t.src());
                    if( m != ni.method(t.dst()) ) throw new RuntimeException(t.toString());
                    mpag(m).load.add(t);
                }
            }
        }
        for( Iterator tIt = alloc.iterator(); tIt.hasNext(); ) {
            final Robj_var.Tuple t = (Robj_var.Tuple) tIt.next();
            if( ni.global(t.obj()) ) {
                if( ni.global(t.var()) ) {
                    addAlloc( null, t.obj(), null, t.var() );
                    change = true;
                } else {
                    mpag( ni.method( t.var() ) ).alloc.add(t);
                }
            } else {
                if( ni.global(t.var()) ) {
                    mpag( ni.method( t.obj() ) ).alloc.add(t);
                } else {
                    SootMethod m = ni.method(t.obj());
                    if( m != ni.method(t.var()) ) throw new RuntimeException(t.toString());
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
                change = true;
            }
            for( Iterator eIt = mpag.rstore.copy().iterator(); eIt.hasNext(); ) {
                final Rsrc_dst_fld.Tuple e = (Rsrc_dst_fld.Tuple) eIt.next();
                addStore( t.ctxt(), e.src(), e.fld(), t.ctxt(), e.dst() );
                change = true;
            }
            for( Iterator eIt = mpag.rload.copy().iterator(); eIt.hasNext(); ) {
                final Rsrc_fld_dst.Tuple e = (Rsrc_fld_dst.Tuple) eIt.next();
                addLoad( t.ctxt(), e.src(), e.fld(), t.ctxt(), e.dst() );
                change = true;
            }
            for( Iterator eIt = mpag.ralloc.copy().iterator(); eIt.hasNext(); ) {
                final Robj_var.Tuple e = (Robj_var.Tuple) eIt.next();
                addAlloc( t.ctxt(), e.obj(), t.ctxt(), e.var() );
                change = true;
            }
        }
        return change;
    }

    private void addSimple( Context srcc, VarNode src, Context dstc, VarNode dst ) {
        if( src instanceof GlobalVarNode ) srcc = null;
        if( dst instanceof GlobalVarNode ) dstc = null;
        csimple.add( srcc, src, dstc, dst );
    }

    private void addStore( Context srcc, VarNode src, PaddleField fld, Context dstc, VarNode dst ) {
        if( src instanceof GlobalVarNode ) srcc = null;
        if( dst instanceof GlobalVarNode ) dstc = null;
        cstore.add( srcc, src, dstc, dst, fld );
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

    private MethodPAG mpag(SootMethod m) {
        MethodPAG ret = (MethodPAG) methodPAGs.get(m);
        if( ret == null ) methodPAGs.put( m, ret = new MethodPAG() );
        return ret;
    }

    private static class MethodPAG {
        Qsrc_dstTrad simple = new Qsrc_dstTrad("mpagsimple");
        Qsrc_fld_dstTrad load = new Qsrc_fld_dstTrad("mpagload");
        Qsrc_dst_fldTrad store = new Qsrc_dst_fldTrad("mpagstore");
        Qobj_varTrad alloc = new Qobj_varTrad("mpagalloc");
        Rsrc_dstTrad rsimple = (Rsrc_dstTrad) simple.reader("mpag");
        Rsrc_fld_dstTrad rload = (Rsrc_fld_dstTrad) load.reader("mpag");
        Rsrc_dst_fldTrad rstore = (Rsrc_dst_fldTrad) store.reader("mpag");
        Robj_varTrad ralloc = (Robj_varTrad) alloc.reader("mpag");
    }

    private LargeNumberedMap methodPAGs = new LargeNumberedMap(Scene.v().getMethodNumberer());
}

