/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.options.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import jedd.*;

/** This class puts all of the pieces of Paddle together and connects them
 * with queues.
 * @author Ondrej Lhotak
 */
public class PaddleScene 
{ 
    public PaddleScene( Singletons.Global g ) {}
    public static PaddleScene v() { return G.v().soot_jimple_paddle_PaddleScene(); }

    public AbsReachableMethods rm;
    public AbsStaticCallBuilder scgb;
    public AbsCallGraph cicg;

    public AbsContextCallGraphBuilder cscgb;
    public AbsCallGraph cg;
    public AbsStaticContextManager scm;
    public AbsReachableMethods rc;

    public AbsMethodPAGBuilder mpb;
    public AbsPAGBuilder pagb;

    public AbsPAG pag;
    public AbsPropagator prop;
    public AbsP2Sets p2sets;

    public AbsVirtualCalls vcr;
    public AbsVirtualContextManager vcm;
    public AbsContextStripper cs;

    public AbsTypeManager tm;

    public Qctxt_method rmout;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm scgbout;
    public Qlocal_srcm_stmt_signature_kind receivers;
    public Qlocal_srcm_stmt_tgtm specials;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm cicgout;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm cscgbout;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm cmout;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm cgout;
    public Qctxt_method rcout;

    public Qsrc_dst simple;
    public Qsrc_fld_dst load;
    public Qsrc_fld_dst store;
    public Qobj_var alloc;

    public Qsrc_dst pagsimple;
    public Qsrc_fld_dst pagload;
    public Qsrc_fld_dst pagstore;
    public Qobj_var pagalloc;

    public Qvar_obj paout;

    public Qctxt_local_obj_srcm_stmt_kind_tgtm vcrout;

    public Qsrcc_srcm_stmt_kind_tgtc_tgtm vcmout;

    private NodeFactory nodeFactory;
    private NodeManager nodeManager = new NodeManager();
    private PaddleOptions options;
    public P2SetFactory setFactory;
    public P2SetFactory newSetFactory;
    public P2SetFactory oldSetFactory;

    public NodeFactory nodeFactory() { return nodeFactory; }
    public NodeManager nodeManager() { return nodeManager; }
    public PaddleOptions options() { return options; }

    public void setup( PaddleOptions opts ) {
        options = opts;
        switch( options.backend() ) {
            case PaddleOptions.backend_buddy:
                Jedd.v().setBackend("buddy"); 
                break;
            case PaddleOptions.backend_cudd:
                Jedd.v().setBackend("cudd"); 
                break;
            case PaddleOptions.backend_sable:
                Jedd.v().setBackend("sablejbdd"); 
                break;
            case PaddleOptions.backend_javabdd:
                Jedd.v().setBackend("javabdd"); 
                break;
            case PaddleOptions.backend_none:
                break;
            default:
                throw new RuntimeException( "Unhandled option: "+options.backend() );
        }
        if( options.backend() != PaddleOptions.backend_none ) {
            PhysicalDomain[] vs = { V1.v(), V2.v(), V3.v() };
            PhysicalDomain[] ts = { T1.v(), T2.v(), T3.v() };
            Object[] order = { ts, FD.v(), vs, H1.v(), H2.v(), ST.v() };
            Jedd.v().setOrder( order, true );
        }
        if( options.profile() ) {
            Jedd.v().enableProfiling();
        }
        if( options.bdd() ) {
            buildBDD();
        } else {
            buildTrad();
        }

        newSetFactory = HybridPointsToSet.getFactory();
        oldSetFactory = HybridPointsToSet.getFactory();
        setFactory = DoublePointsToSet.getFactory( newSetFactory, oldSetFactory );
    }

    public void solve() {
        for( Iterator mIt = Scene.v().getEntryPoints().iterator(); mIt.hasNext(); ) {
            final SootMethod m = (SootMethod) mIt.next();
            rm.add( m );
            rc.add( m );
        }
        updateFrontEnd();
        prop.update();
        if( options.profile() ) {
            try {
                Jedd.v().outputProfile( new PrintStream( new GZIPOutputStream(
                    new FileOutputStream( new File( "profile.sql.gz")))));
            } catch( IOException e ) {
                throw new RuntimeException( "Couldn't output Jedd profile "+e );
            }
        }
    }

    private void buildQueuesSet() {
        rmout = new Qctxt_methodSet();
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet();
        receivers = new Qlocal_srcm_stmt_signature_kindSet();
        specials = new Qlocal_srcm_stmt_tgtmSet();
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet();
        cscgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet();
        cmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet();
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet();
        rcout = new Qctxt_methodSet();

        simple = new Qsrc_dstSet();
        load = new Qsrc_fld_dstSet();
        store = new Qsrc_fld_dstSet();
        alloc = new Qobj_varSet();

        pagsimple = new Qsrc_dstSet();
        pagload = new Qsrc_fld_dstSet();
        pagstore = new Qsrc_fld_dstSet();
        pagalloc = new Qobj_varSet();

        paout = new Qvar_objSet();

        vcrout = new Qctxt_local_obj_srcm_stmt_kind_tgtmSet();
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet();
    }

    private void buildQueuesBDD() {
        rmout = new Qctxt_methodBDD();
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
        receivers = new Qlocal_srcm_stmt_signature_kindBDD();
        specials = new Qlocal_srcm_stmt_tgtmBDD();
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
        cscgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
        cmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
        rcout = new Qctxt_methodBDD();

        simple = new Qsrc_dstBDD();
        load = new Qsrc_fld_dstBDD();
        store = new Qsrc_fld_dstBDD();
        alloc = new Qobj_varBDD();

        pagsimple = new Qsrc_dstBDD();
        pagload = new Qsrc_fld_dstBDD();
        pagstore = new Qsrc_fld_dstBDD();
        pagalloc = new Qobj_varBDD();

        paout = new Qvar_objBDD();

        vcrout = new Qctxt_local_obj_srcm_stmt_kind_tgtmBDD();
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
    }

    private void buildBDD() {
        buildQueues();
        nodeFactory = new NodeFactory( simple, load, store, alloc );

        cicg = new BDDCallGraph( scgbout.reader(), cicgout );
        rm = new BDDReachableMethods( cicgout.reader(), rmout, cicg );
        scgb = new TradStaticCallBuilder( rmout.reader(), scgbout, receivers, specials );

        cg = new BDDCallGraph( 
                new Rsrcc_srcm_stmt_kind_tgtc_tgtmMerge(
                    cmout.reader(), vcmout.reader() ),
                cgout );
        rc = new BDDReachableMethods( cgout.reader(), rcout, cg );
        cscgb = new BDDContextCallGraphBuilder( rcout.reader(), cscgbout, cicg );
        scm = new BDDInsensitiveStaticContextManager( cscgbout.reader(), cmout );

        mpb = new TradMethodPAGBuilder( rcout.reader(), simple, load, store, alloc );
        pagb = new TradPAGBuilder( cgout.reader(), simple, load, store, alloc );

        pag = new BDDPAG( simple.reader(), load.reader(), store.reader(),
                alloc.reader(), pagsimple, pagload, pagstore, pagalloc );
        makePropagator();

        vcr = new BDDVirtualCalls( paout.reader(), receivers.reader(), specials.reader(), vcrout, cscgbout );
        vcm = new BDDInsensitiveVirtualContextManager( vcrout.reader(), vcmout );
        cs = new BDDContextStripper( vcmout.reader(), cicgout );

        tm = new BDDTypeManager( 
                new RvarIter( PaddleNumberers.v().varNodeNumberer().iterator() ),
                new RobjIter( PaddleNumberers.v().allocNodeNumberer().iterator() ), 
                options.ignore_types() ? null : new BDDHierarchy() );
    }

    private void buildQueuesTrad() {
        rmout = new Qctxt_methodTrad();
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad();
        receivers = new Qlocal_srcm_stmt_signature_kindTrad();
        specials = new Qlocal_srcm_stmt_tgtmTrad();
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad();
        cscgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad();
        cmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad();
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad();
        rcout = new Qctxt_methodTrad();

        simple = new Qsrc_dstTrad();
        load = new Qsrc_fld_dstTrad();
        store = new Qsrc_fld_dstTrad();
        alloc = new Qobj_varTrad();

        pagsimple = new Qsrc_dstTrad();
        pagload = new Qsrc_fld_dstTrad();
        pagstore = new Qsrc_fld_dstTrad();
        pagalloc = new Qobj_varTrad();

        paout = new Qvar_objTrad();

        vcrout = new Qctxt_local_obj_srcm_stmt_kind_tgtmTrad();
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad();
    }

    private void buildQueuesDebug() {
        rmout = new Qctxt_methodBDD();
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();

        receivers = new Qlocal_srcm_stmt_signature_kindBDD();
        specials = new Qlocal_srcm_stmt_tgtmBDD();

        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
        cscgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
        cmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
        rcout = new Qctxt_methodBDD();

        simple = new Qsrc_dstBDD();
        load = new Qsrc_fld_dstBDD();
        store = new Qsrc_fld_dstBDD();
        alloc = new Qobj_varBDD();

        pagsimple = new Qsrc_dstBDD();
        pagload = new Qsrc_fld_dstBDD();
        pagstore = new Qsrc_fld_dstBDD();
        pagalloc = new Qobj_varBDD();

        paout = new Qvar_objBDD();

        vcrout = new Qctxt_local_obj_srcm_stmt_kind_tgtmBDD();
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD();
    }

    private void buildQueuesTrace() {
        rmout = new Qctxt_methodTrace("rmout");
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("scgbout");
        receivers = new Qlocal_srcm_stmt_signature_kindTrace("receivers");
        specials = new Qlocal_srcm_stmt_tgtmTrace("specials");
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("cicgout");
        cscgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("cscgbout");
        cmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("cmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("cgout");
        rcout = new Qctxt_methodTrace("rcout");

        simple = new Qsrc_dstTrace("simple");
        load = new Qsrc_fld_dstTrace("load");
        store = new Qsrc_fld_dstTrace("store");
        alloc = new Qobj_varTrace("alloc");

        pagsimple = new Qsrc_dstTrace("pagsimple");
        pagload = new Qsrc_fld_dstTrace("pagload");
        pagstore = new Qsrc_fld_dstTrace("pagstore");
        pagalloc = new Qobj_varTrace("pagalloc");

        paout = new Qvar_objTrace("paout");

        vcrout = new Qctxt_local_obj_srcm_stmt_kind_tgtmTrace("vcrout");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("vcmout");
    }

    private void buildQueues() {
        if( options.debugq() ) buildQueuesDebug();
        else if( options.bddq() ) buildQueuesBDD();
        else if( options.trace() ) buildQueuesTrace();
        else buildQueuesTrad();
    }
    private void buildTrad() {
        buildQueues();

        nodeFactory = new NodeFactory( simple, load, store, alloc );

        cicg = new TradCallGraph( scgbout.reader(), cicgout );
        rm = new TradReachableMethods( cicgout.reader(), rmout, cicg );
        scgb = new TradStaticCallBuilder( rmout.reader(), scgbout, receivers, specials );

        cg = new TradCallGraph( 
                new Rsrcc_srcm_stmt_kind_tgtc_tgtmMerge(
                    cmout.reader(), vcmout.reader() ),
                cgout );
        rc = new TradReachableMethods( cgout.reader(), rcout, cg );
        cscgb = new TradContextCallGraphBuilder( rcout.reader(), cscgbout, cicg );
        scm = new TradInsensitiveStaticContextManager( cscgbout.reader(), cmout );

        mpb = new TradMethodPAGBuilder( rcout.reader(), simple, load, store, alloc );
        pagb = new TradPAGBuilder( cgout.reader(), simple, load, store, alloc );

        pag = new TradPAG( simple.reader(), load.reader(), store.reader(),
                alloc.reader(), pagsimple, pagload, pagstore, pagalloc );
        makePropagator();

        vcr = new TradVirtualCalls( paout.reader(), receivers.reader(), specials.reader(), vcrout, cscgbout );
        vcm = new TradInsensitiveVirtualContextManager( vcrout.reader(), vcmout );
        cs = new TradContextStripper( vcmout.reader(), cicgout );

        tm = new TradTypeManager( 
                new RvarIter( PaddleNumberers.v().varNodeNumberer().iterator() ),
                new RobjIter( PaddleNumberers.v().allocNodeNumberer().iterator() ), 
                options.ignore_types() ? null : Scene.v().getOrMakeFastHierarchy() );
    }

    private void makePropagator() {
        switch( options.propagator() ) {
            case PaddleOptions.propagator_worklist:
                prop = new PropWorklist( pagsimple.reader(), pagload.reader(),
                    pagstore.reader(), pagalloc.reader(), paout, pag );
                p2sets = new TradP2Sets();
                break;
            case PaddleOptions.propagator_iter:
                prop = new PropIter( pagsimple.reader(), pagload.reader(),
                    pagstore.reader(), pagalloc.reader(), paout, pag );
                p2sets = new TradP2Sets();
                break;
            case PaddleOptions.propagator_alias:
                prop = new PropAlias( pagsimple.reader(), pagload.reader(),
                    pagstore.reader(), pagalloc.reader(), paout, pag );
                p2sets = new TradP2Sets();
                break;
            case PaddleOptions.propagator_bdd:
                prop = new PropBDD( pagsimple.reader(), pagload.reader(),
                    pagstore.reader(), pagalloc.reader(), paout, pag );
                p2sets = new BDDP2Sets( (PropBDD) prop );
                break;
            default:
                throw new RuntimeException( "Unimplemented propagator specified" );
        }
    }

    private void updateFrontEnd() {
        while(true) {
            rm.update();
            scgb.update();
            if( !cicg.update() ) break;
        }
        while(true) {
            rc.update();
            cscgb.update();
            scm.update();
            if( !cg.update() ) break;
        }
        mpb.update();
        pagb.update();
        pag.update();
    }

    private void updateBackEnd() {
        vcr.update();
        vcm.update();
        cs.update();
    }

    void updateCallGraph() {
        updateBackEnd();
        updateFrontEnd();
    }
}


