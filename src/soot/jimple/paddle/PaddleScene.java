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
    private PaddleNativeHelper nativeHelper;
    private CGOptions cgoptions = 
        new CGOptions(PhaseOptions.v().getPhaseOptions("cg"));
    public P2SetFactory setFactory;
    public P2SetFactory newSetFactory;
    public P2SetFactory oldSetFactory;

    public NodeFactory nodeFactory() { return nodeFactory; }
    public NodeManager nodeManager() { return nodeManager; }
    public PaddleOptions options() { return options; }
    public PaddleNativeHelper nativeHelper() { 
        if( nativeHelper == null ) nativeHelper = new PaddleNativeHelper();
        return nativeHelper;
    }

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
        rmout = new Qctxt_methodSet("rmout");
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet("scgbout");
        receivers = new Qlocal_srcm_stmt_signature_kindSet("receivers");
        specials = new Qlocal_srcm_stmt_tgtmSet("specials");
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet("cicgout");
        cscgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet("cscgbout");
        cmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet("cmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet("cgout");
        rcout = new Qctxt_methodSet("rcout");

        simple = new Qsrc_dstSet("simple");
        load = new Qsrc_fld_dstSet("load");
        store = new Qsrc_fld_dstSet("store");
        alloc = new Qobj_varSet("alloc");

        pagsimple = new Qsrc_dstSet("pagsimple");
        pagload = new Qsrc_fld_dstSet("pagload");
        pagstore = new Qsrc_fld_dstSet("pagstore");
        pagalloc = new Qobj_varSet("pagalloc");

        paout = new Qvar_objSet("paout");

        vcrout = new Qctxt_local_obj_srcm_stmt_kind_tgtmSet("vcrout");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet("vcmout");
    }

    private void buildQueuesBDD() {
        rmout = new Qctxt_methodBDD("rmout");
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("scgbout");
        receivers = new Qlocal_srcm_stmt_signature_kindBDD("receivers");
        specials = new Qlocal_srcm_stmt_tgtmBDD("specials");
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("cicgout");
        cscgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("cscgbout");
        cmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("cmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("cgout");
        rcout = new Qctxt_methodBDD("rcout");

        simple = new Qsrc_dstBDD("simple");
        load = new Qsrc_fld_dstBDD("load");
        store = new Qsrc_fld_dstBDD("store");
        alloc = new Qobj_varBDD("alloc");

        pagsimple = new Qsrc_dstBDD("pagsimple");
        pagload = new Qsrc_fld_dstBDD("pagload");
        pagstore = new Qsrc_fld_dstBDD("pagstore");
        pagalloc = new Qobj_varBDD("pagalloc");

        paout = new Qvar_objBDD("paout");

        vcrout = new Qctxt_local_obj_srcm_stmt_kind_tgtmBDD("vcrout");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("vcmout");
    }

    private void buildBDD() {
        buildQueues();
        nodeFactory = new NodeFactory( simple, load, store, alloc );

        cicg = new BDDCallGraph( scgbout.reader("cicg"), cicgout );
        rm = new BDDReachableMethods( cicgout.reader("rm"), rmout, cicg );
        scgb = new TradStaticCallBuilder( rmout.reader("scgb"), scgbout, receivers, specials );

        cg = new BDDCallGraph( 
                new Rsrcc_srcm_stmt_kind_tgtc_tgtmMerge(
                    cmout.reader("cg"), vcmout.reader("cg") ),
                cgout );
        rc = new BDDReachableMethods( cgout.reader("rc"), rcout, cg );
        cscgb = new BDDContextCallGraphBuilder( rcout.reader("cscgb"), cscgbout, cicg );

        mpb = new TradMethodPAGBuilder( rcout.reader("mpb"), simple, load, store, alloc );
        pagb = new TradPAGBuilder( cgout.reader("pagb"), simple, load, store, alloc );

        pag = new BDDPAG( simple.reader("pag"), load.reader("pag"), store.reader("pag"),
                alloc.reader("pag"), pagsimple, pagload, pagstore, pagalloc );
        makePropagator();

        vcr = new BDDVirtualCalls( paout.reader("vcr"), receivers.reader("vcr"), specials.reader("vcr"), vcrout, cscgbout );
        cs = new BDDContextStripper( vcmout.reader("cs"), cicgout );

        tm = new BDDTypeManager( 
                new RvarIter( PaddleNumberers.v().varNodeNumberer().iterator(), "tm" ),
                new RobjIter( PaddleNumberers.v().allocNodeNumberer().iterator(), "tm" ), 
                options.ignore_types() ? null : new BDDHierarchy() );

        switch( cgoptions.context() ) {
            case CGOptions.context_insens:
                scm = new BDDInsensitiveStaticContextManager( cscgbout.reader("scm"), cmout );
                vcm = new BDDInsensitiveVirtualContextManager( vcrout.reader("vcm"), vcmout );
                break;
            case CGOptions.context_1cfa:
                scm = new BDD1CFAStaticContextManager( cscgbout.reader("scm"), cmout );
                vcm = new BDD1CFAVirtualContextManager( vcrout.reader("vcm"), vcmout );
                break;
            case CGOptions.context_objsens:
                scm = new BDDObjSensStaticContextManager( cscgbout.reader("scm"), cmout );
                vcm = new BDDObjSensVirtualContextManager( vcrout.reader("vcm"), vcmout );
                break;
            case CGOptions.context_kcfa:
                scm = new TradKCFAStaticContextManager( cscgbout.reader("scm"), cmout, cgoptions.k() );
                vcm = new TradKCFAVirtualContextManager( vcrout.reader("vcm"), vcmout, cgoptions.k() );
                break;
            case CGOptions.context_kobjsens:
                scm = new TradKObjSensStaticContextManager( cscgbout.reader("scm"), cmout, cgoptions.k() );
                vcm = new TradKObjSensVirtualContextManager( vcrout.reader("vcm"), vcmout, cgoptions.k() );
                break;
            default:
                throw new RuntimeException( "Unhandled kind of context-sensitivity" );
        }
    }

    private void buildQueuesTrad() {
        rmout = new Qctxt_methodTrad("rmout");
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("scgbout");
        receivers = new Qlocal_srcm_stmt_signature_kindTrad("receivers");
        specials = new Qlocal_srcm_stmt_tgtmTrad("specials");
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("cicgout");
        cscgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("cscgbout");
        cmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("cmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("cgout");
        rcout = new Qctxt_methodTrad("rcout");

        simple = new Qsrc_dstTrad("simple");
        load = new Qsrc_fld_dstTrad("load");
        store = new Qsrc_fld_dstTrad("store");
        alloc = new Qobj_varTrad("alloc");

        pagsimple = new Qsrc_dstTrad("pagsimple");
        pagload = new Qsrc_fld_dstTrad("pagload");
        pagstore = new Qsrc_fld_dstTrad("pagstore");
        pagalloc = new Qobj_varTrad("pagalloc");

        paout = new Qvar_objTrad("paout");

        vcrout = new Qctxt_local_obj_srcm_stmt_kind_tgtmTrad("vcrout");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("vcmout");
    }

    private void buildQueuesDebug() {
        rmout = new Qctxt_methodDebug("rmout");
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("scgbout");

        receivers = new Qlocal_srcm_stmt_signature_kindDebug("receivers");
        specials = new Qlocal_srcm_stmt_tgtmDebug("specials");

        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("cicgout");
        cscgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("cscgbout");
        cmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("cmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("cgout");
        rcout = new Qctxt_methodDebug("rcout");

        simple = new Qsrc_dstDebug("simple");
        load = new Qsrc_fld_dstDebug("load");
        store = new Qsrc_fld_dstDebug("store");
        alloc = new Qobj_varDebug("alloc");

        pagsimple = new Qsrc_dstDebug("pagsimple");
        pagload = new Qsrc_fld_dstDebug("pagload");
        pagstore = new Qsrc_fld_dstDebug("pagstore");
        pagalloc = new Qobj_varDebug("pagalloc");

        paout = new Qvar_objDebug("paout");

        vcrout = new Qctxt_local_obj_srcm_stmt_kind_tgtmDebug("vcrout");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("vcmout");
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
    

    /*
    private void buildQueuesTrace() {
        rmout = new Qctxt_methodNumTrace("rmout");
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("scgbout");
        receivers = new Qlocal_srcm_stmt_signature_kindNumTrace("receivers");
        specials = new Qlocal_srcm_stmt_tgtmNumTrace("specials");
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("cicgout");
        cscgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("cscgbout");
        cmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("cmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("cgout");
        rcout = new Qctxt_methodSet("rcout");

        simple = new Qsrc_dstNumTrace("simple");
        load = new Qsrc_fld_dstNumTrace("load");
        store = new Qsrc_fld_dstNumTrace("store");
        alloc = new Qobj_varNumTrace("alloc");

        pagsimple = new Qsrc_dstNumTrace("pagsimple");
        pagload = new Qsrc_fld_dstNumTrace("pagload");
        pagstore = new Qsrc_fld_dstNumTrace("pagstore");
        pagalloc = new Qobj_varNumTrace("pagalloc");

        paout = new Qvar_objNumTrace("paout");

        vcrout = new Qctxt_local_obj_srcm_stmt_kind_tgtmNumTrace("vcrout");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("vcmout");
    }
    */

    private void buildQueues() {
        if( options.debugq() ) buildQueuesDebug();
        else if( options.bddq() ) buildQueuesBDD();
        else if( options.trace() ) buildQueuesTrace();
        else buildQueuesTrad();
    }
    private void buildTrad() {
        buildQueues();

        nodeFactory = new NodeFactory( simple, load, store, alloc );

        cicg = new TradCallGraph( scgbout.reader("cicg"), cicgout );
        rm = new TradReachableMethods( cicgout.reader("rm"), rmout, cicg );
        scgb = new TradStaticCallBuilder( rmout.reader("scgb"), scgbout, receivers, specials );

        cg = new TradCallGraph( 
                new Rsrcc_srcm_stmt_kind_tgtc_tgtmMerge(
                    cmout.reader("cg"), vcmout.reader("cg") ),
                cgout );
        rc = new TradReachableMethods( cgout.reader("rc"), rcout, cg );
        cscgb = new TradContextCallGraphBuilder( rcout.reader("cscgb"), cscgbout, cicg );

        mpb = new TradMethodPAGBuilder( rcout.reader("mpb"), simple, load, store, alloc );
        pagb = new TradPAGBuilder( cgout.reader("pagb"), simple, load, store, alloc );

        pag = new TradPAG( simple.reader("pag"), load.reader("pag"), store.reader("pag"),
                alloc.reader("pag"), pagsimple, pagload, pagstore, pagalloc );
        makePropagator();

        vcr = new TradVirtualCalls( paout.reader("vcr"), receivers.reader("vcr"), specials.reader("vcr"), vcrout, cscgbout );
        cs = new TradContextStripper( vcmout.reader("cs"), cicgout );

        tm = new TradTypeManager( 
                new RvarIter( PaddleNumberers.v().varNodeNumberer().iterator(), "tm"
                    ),
                new RobjIter( PaddleNumberers.v().allocNodeNumberer().iterator(), "tm" ), 
                options.ignore_types() ? null : Scene.v().getOrMakeFastHierarchy() );
        switch( cgoptions.context() ) {
            case CGOptions.context_insens:
                scm = new TradInsensitiveStaticContextManager( cscgbout.reader("scm"), cmout );
                vcm = new TradInsensitiveVirtualContextManager( vcrout.reader("vcm"), vcmout );
                break;
            case CGOptions.context_1cfa:
                scm = new Trad1CFAStaticContextManager( cscgbout.reader("scm"), cmout );
                vcm = new Trad1CFAVirtualContextManager( vcrout.reader("vcm"), vcmout );
                break;
            case CGOptions.context_objsens:
                scm = new TradObjSensStaticContextManager( cscgbout.reader("scm"), cmout );
                vcm = new TradObjSensVirtualContextManager( vcrout.reader("vcm"), vcmout );
                break;
            case CGOptions.context_kcfa:
                scm = new TradKCFAStaticContextManager( cscgbout.reader("scm"), cmout, cgoptions.k() );
                vcm = new TradKCFAVirtualContextManager( vcrout.reader("vcm"), vcmout, cgoptions.k() );
                break;
            case CGOptions.context_kobjsens:
                scm = new TradKObjSensStaticContextManager( cscgbout.reader("scm"), cmout, cgoptions.k() );
                vcm = new TradKObjSensVirtualContextManager( vcrout.reader("vcm"), vcmout, cgoptions.k() );
                break;
            default:
                throw new RuntimeException( "Unhandled kind of context-sensitivity" );
        }
    }

    private void makePropagator() {
        switch( options.propagator() ) {
            case PaddleOptions.propagator_worklist:
                prop = new PropWorklist( pagsimple.reader("prop"), pagload.reader("prop"),
                    pagstore.reader("prop"), pagalloc.reader("prop"), paout, pag );
                p2sets = new TradP2Sets();
                break;
            case PaddleOptions.propagator_iter:
                prop = new PropIter( pagsimple.reader("prop"), pagload.reader("prop"),
                    pagstore.reader("prop"), pagalloc.reader("prop"), paout, pag );
                p2sets = new TradP2Sets();
                break;
            case PaddleOptions.propagator_alias:
                prop = new PropAlias( pagsimple.reader("prop"), pagload.reader("prop"),
                    pagstore.reader("prop"), pagalloc.reader("prop"), paout, pag );
                p2sets = new TradP2Sets();
                break;
            case PaddleOptions.propagator_bdd:
                prop = new PropBDD( pagsimple.reader("prop"), pagload.reader("prop"),
                    pagstore.reader("prop"), pagalloc.reader("prop"), paout, pag );
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


