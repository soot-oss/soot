/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003, 2004 Ondrej Lhotak
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
    public AbsMethodPAGContextifier mpc;
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
    public Qvar_srcm_stmt_signature_kind receivers;
    public Qvar_srcm_stmt_tgtm specials;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm cicgout;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm staticcalls;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm scmout;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm cgout;
    public Qctxt_method rcout;
    public Qctxt_method csout;

    public Qvar_method_type locals;
    public Qvar_type globals;
    public Qobj_method_type localallocs;
    public Qobj_type globalallocs;

    public Qsrc_dst simple;
    public Qsrc_fld_dst load;
    public Qsrc_fld_dst store;
    public Qobj_var alloc;

    public Qsrcc_src_dstc_dst csimple;
    public Qsrcc_src_fld_dstc_dst cload;
    public Qsrcc_src_fld_dstc_dst cstore;
    public Qobjc_obj_varc_var calloc;

    public Qvarc_var_objc_obj paout;

    public Qctxt_var_obj_srcm_stmt_kind_tgtm virtualcalls;

    public Qsrcc_srcm_stmt_kind_tgtc_tgtm vcmout;

    private NodeFactory nodeFactory;
    private NodeManager nodeManager;
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
            PhysicalDomain[] cs = { C1.v(), C2.v(), C3.v() };
            Object[] order = { cs, ts, FD.v(), vs, H1.v(), H2.v(), ST.v() };
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

        makeSetFactories();
    }
    private void makeSetFactories() {
        switch( options.set_impl() ) {
            case SparkOptions.set_impl_hash:
                setFactory = HashPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_hybrid:
                setFactory = HybridPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_shared:
                setFactory = SharedPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_array:
                setFactory = SortedArraySet.getFactory();
                break;
            case SparkOptions.set_impl_bit:
                setFactory = BitPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_double:
                switch( options.double_set_old() ) {
                    case SparkOptions.double_set_old_hash:
                        oldSetFactory = HashPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_old_hybrid:
                        oldSetFactory = HybridPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_old_shared:
                        oldSetFactory = SharedPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_old_array:
                        oldSetFactory = SortedArraySet.getFactory();
                        break;
                    case SparkOptions.double_set_old_bit:
                        oldSetFactory = BitPointsToSet.getFactory();
                        break;
                    default:
                        throw new RuntimeException();
                }
                switch( options.double_set_new() ) {
                    case SparkOptions.double_set_new_hash:
                        newSetFactory = HashPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_new_hybrid:
                        newSetFactory = HybridPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_new_shared:
                        newSetFactory = SharedPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_new_array:
                        newSetFactory = SortedArraySet.getFactory();
                        break;
                    case SparkOptions.double_set_new_bit:
                        newSetFactory = BitPointsToSet.getFactory();
                        break;
                    default:
                        throw new RuntimeException();
                }
                setFactory = DoublePointsToSet.getFactory( newSetFactory, oldSetFactory );
                break;
            default:
                throw new RuntimeException();
        }
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
        receivers = new Qvar_srcm_stmt_signature_kindSet("receivers");
        specials = new Qvar_srcm_stmt_tgtmSet("specials");
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet("cicgout");
        staticcalls = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet("staticcalls");
        scmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet("scmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet("cgout");
        rcout = new Qctxt_methodSet("rcout");
        csout = new Qctxt_methodSet("csout");

        locals = new Qvar_method_typeSet("locals");
        globals = new Qvar_typeSet("globals");
        localallocs = new Qobj_method_typeSet("localallocs");
        globalallocs = new Qobj_typeSet("globalallocs");

        simple = new Qsrc_dstSet("simple");
        load = new Qsrc_fld_dstSet("load");
        store = new Qsrc_fld_dstSet("store");
        alloc = new Qobj_varSet("alloc");

        csimple = new Qsrcc_src_dstc_dstSet("csimple");
        cload = new Qsrcc_src_fld_dstc_dstSet("cload");
        cstore = new Qsrcc_src_fld_dstc_dstSet("cstore");
        calloc = new Qobjc_obj_varc_varSet("calloc");

        paout = new Qvarc_var_objc_objSet("paout");

        virtualcalls = new Qctxt_var_obj_srcm_stmt_kind_tgtmSet("virtualcalls");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmSet("vcmout");

    }

    private void buildQueuesBDD() {
        rmout = new Qctxt_methodBDD("rmout");
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("scgbout");
        receivers = new Qvar_srcm_stmt_signature_kindBDD("receivers");
        specials = new Qvar_srcm_stmt_tgtmBDD("specials");
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("cicgout");
        staticcalls = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("staticcalls");
        scmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("scmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("cgout");
        rcout = new Qctxt_methodBDD("rcout");
        csout = new Qctxt_methodBDD("csout");

        locals = new Qvar_method_typeBDD("locals");
        globals = new Qvar_typeBDD("globals");
        localallocs = new Qobj_method_typeBDD("localallocs");
        globalallocs = new Qobj_typeBDD("globalallocs");

        simple = new Qsrc_dstBDD("simple");
        load = new Qsrc_fld_dstBDD("load");
        store = new Qsrc_fld_dstBDD("store");
        alloc = new Qobj_varBDD("alloc");

        csimple = new Qsrcc_src_dstc_dstBDD("csimple");
        cload = new Qsrcc_src_fld_dstc_dstBDD("cload");
        cstore = new Qsrcc_src_fld_dstc_dstBDD("cstore");
        calloc = new Qobjc_obj_varc_varBDD("calloc");

        paout = new Qvarc_var_objc_objBDD("paout");

        virtualcalls = new Qctxt_var_obj_srcm_stmt_kind_tgtmBDD("virtualcalls");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmBDD("vcmout");
    }

    private void buildBDD() {
        buildQueues();
        nodeManager = new NodeManager( locals, globals, localallocs, globalallocs );
        nodeFactory = new NodeFactory( simple, load, store, alloc );

        cicg = new BDDCallGraph( scgbout.reader("cicg"), cicgout );
        rm = new BDDReachableMethods( cicgout.reader("rm"), csout.reader("rm"), rmout, cicg );
        scgb = new TradStaticCallBuilder( rmout.reader("scgb"), scgbout, receivers, specials );

        cg = new BDDCallGraph( 
                new Rsrcc_srcm_stmt_kind_tgtc_tgtmMerge(
                    scmout.reader("cg"), vcmout.reader("cg") ),
                cgout );
        rc = new BDDReachableMethods( cgout.reader("rc"), null, rcout, cg );
        cscgb = new BDDContextCallGraphBuilder( rcout.reader("cscgb"), cicgout.reader("cscgb"), staticcalls, cicg );

        mpb = new TradMethodPAGBuilder( rmout.reader("mpb"), simple, load, store, alloc );
        mpc = new BDDMethodPAGContextifier(
                simple.reader("mpc"),
                store.reader("mpc"),
                load.reader("mpc"),
                alloc.reader("mpc"),
                locals.reader("mpc"),
                globals.reader("mpc"),
                localallocs.reader("mpc"),
                globalallocs.reader("mpc"),
                rcout.reader("mpc"),
                csimple, cstore, cload, calloc );
        pagb = new TradPAGBuilder( cgout.reader("pagb"), csimple, cload, cstore, calloc );

        pag = new BDDPAG( csimple.reader("pag"), cload.reader("pag"),
                cstore.reader("pag"), calloc.reader("pag") );
        makePropagator();

        vcr = new BDDVirtualCalls( paout.reader("vcr"), receivers.reader("vcr"), specials.reader("vcr"), virtualcalls, staticcalls );
        cs = new BDDContextStripper( rcout.reader("cs"), csout );

        tm = new BDDTypeManager(
                locals.reader("tm"),
                globals.reader("tm"),
                localallocs.reader("tm"),
                globalallocs.reader("tm"),
                options.ignore_types() ? null : new BDDHierarchy() );

        switch( cgoptions.context() ) {
            case CGOptions.context_insens:
                scm = new BDDInsensitiveStaticContextManager( staticcalls.reader("scm"), scmout );
                vcm = new BDDInsensitiveVirtualContextManager( virtualcalls.reader("vcm"), vcmout );
                break;
            case CGOptions.context_1cfa:
                scm = new BDD1CFAStaticContextManager( staticcalls.reader("scm"), scmout );
                vcm = new BDD1CFAVirtualContextManager( virtualcalls.reader("vcm"), vcmout );
                break;
            case CGOptions.context_objsens:
                scm = new BDDObjSensStaticContextManager( staticcalls.reader("scm"), scmout );
                vcm = new BDDObjSensVirtualContextManager( virtualcalls.reader("vcm"), vcmout );
                break;
            case CGOptions.context_kcfa:
                scm = new TradKCFAStaticContextManager( staticcalls.reader("scm"), scmout, cgoptions.k() );
                vcm = new TradKCFAVirtualContextManager( virtualcalls.reader("vcm"), vcmout, cgoptions.k() );
                break;
            case CGOptions.context_kobjsens:
                scm = new TradKObjSensStaticContextManager( staticcalls.reader("scm"), scmout, cgoptions.k() );
                vcm = new TradKObjSensVirtualContextManager( virtualcalls.reader("vcm"), vcmout, cgoptions.k() );
                break;
            default:
                throw new RuntimeException( "Unhandled kind of context-sensitivity" );
        }
    }

    private void buildQueuesTrad() {
        rmout = new Qctxt_methodTrad("rmout");
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("scgbout");
        receivers = new Qvar_srcm_stmt_signature_kindTrad("receivers");
        specials = new Qvar_srcm_stmt_tgtmTrad("specials");
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("cicgout");
        staticcalls = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("staticcalls");
        scmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("scmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("cgout");
        rcout = new Qctxt_methodTrad("rcout");
        csout = new Qctxt_methodTrad("csout");

        locals = new Qvar_method_typeTrad("locals");
        globals = new Qvar_typeTrad("globals");
        localallocs = new Qobj_method_typeTrad("localallocs");
        globalallocs = new Qobj_typeTrad("globalallocs");

        simple = new Qsrc_dstTrad("simple");
        load = new Qsrc_fld_dstTrad("load");
        store = new Qsrc_fld_dstTrad("store");
        alloc = new Qobj_varTrad("alloc");

        csimple = new Qsrcc_src_dstc_dstTrad("csimple");
        cload = new Qsrcc_src_fld_dstc_dstTrad("cload");
        cstore = new Qsrcc_src_fld_dstc_dstTrad("cstore");
        calloc = new Qobjc_obj_varc_varTrad("calloc");

        paout = new Qvarc_var_objc_objTrad("paout");

        virtualcalls = new Qctxt_var_obj_srcm_stmt_kind_tgtmTrad("virtualcalls");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("vcmout");
    }

    private void buildQueuesDebug() {
        rmout = new Qctxt_methodDebug("rmout");
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("scgbout");

        receivers = new Qvar_srcm_stmt_signature_kindDebug("receivers");
        specials = new Qvar_srcm_stmt_tgtmDebug("specials");

        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("cicgout");
        staticcalls = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("staticcalls");
        scmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("scmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("cgout");
        rcout = new Qctxt_methodDebug("rcout");
        csout = new Qctxt_methodDebug("csout");

        locals = new Qvar_method_typeDebug("locals");
        globals = new Qvar_typeDebug("globals");
        localallocs = new Qobj_method_typeDebug("localallocs");
        globalallocs = new Qobj_typeDebug("globalallocs");

        simple = new Qsrc_dstDebug("simple");
        load = new Qsrc_fld_dstDebug("load");
        store = new Qsrc_fld_dstDebug("store");
        alloc = new Qobj_varDebug("alloc");

        csimple = new Qsrcc_src_dstc_dstDebug("csimple");
        cload = new Qsrcc_src_fld_dstc_dstDebug("cload");
        cstore = new Qsrcc_src_fld_dstc_dstDebug("cstore");
        calloc = new Qobjc_obj_varc_varDebug("calloc");

        paout = new Qvarc_var_objc_objDebug("paout");

        virtualcalls = new Qctxt_var_obj_srcm_stmt_kind_tgtmDebug("virtualcalls");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmDebug("vcmout");
    }

    private void buildQueuesTrace() {
        rmout = new Qctxt_methodTrace("rmout");
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("scgbout");
        receivers = new Qvar_srcm_stmt_signature_kindTrace("receivers");
        specials = new Qvar_srcm_stmt_tgtmTrace("specials");
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("cicgout");
        staticcalls = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("staticcalls");
        scmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("scmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("cgout");
        rcout = new Qctxt_methodTrace("rcout");
        csout = new Qctxt_methodTrace("csout");

        locals = new Qvar_method_typeTrace("locals");
        globals = new Qvar_typeTrace("globals");
        localallocs = new Qobj_method_typeTrace("localallocs");
        globalallocs = new Qobj_typeTrace("globalallocs");

        simple = new Qsrc_dstTrace("simple");
        load = new Qsrc_fld_dstTrace("load");
        store = new Qsrc_fld_dstTrace("store");
        alloc = new Qobj_varTrace("alloc");

        csimple = new Qsrcc_src_dstc_dstTrace("csimple");
        cload = new Qsrcc_src_fld_dstc_dstTrace("cload");
        cstore = new Qsrcc_src_fld_dstc_dstTrace("cstore");
        calloc = new Qobjc_obj_varc_varTrace("calloc");

        paout = new Qvarc_var_objc_objTrace("paout");

        virtualcalls = new Qctxt_var_obj_srcm_stmt_kind_tgtmTrace("virtualcalls");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrace("vcmout");
    }
    

    private void buildQueuesNumTrace() {
        rmout = new Qctxt_methodNumTrace("rmout");
        scgbout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("scgbout");
        receivers = new Qvar_srcm_stmt_signature_kindNumTrace("receivers");
        specials = new Qvar_srcm_stmt_tgtmNumTrace("specials");
        cicgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("cicgout");
        staticcalls = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("staticcalls");
        scmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("scmout");
        cgout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("cgout");
        rcout = new Qctxt_methodNumTrace("rcout");
        csout = new Qctxt_methodNumTrace("csout");

        locals = new Qvar_method_typeNumTrace("locals");
        globals = new Qvar_typeNumTrace("globals");
        localallocs = new Qobj_method_typeNumTrace("localallocs");
        globalallocs = new Qobj_typeNumTrace("globalallocs");

        simple = new Qsrc_dstNumTrace("simple");
        load = new Qsrc_fld_dstNumTrace("load");
        store = new Qsrc_fld_dstNumTrace("store");
        alloc = new Qobj_varNumTrace("alloc");

        csimple = new Qsrcc_src_dstc_dstNumTrace("csimple");
        cload = new Qsrcc_src_fld_dstc_dstNumTrace("cload");
        cstore = new Qsrcc_src_fld_dstc_dstNumTrace("cstore");
        calloc = new Qobjc_obj_varc_varNumTrace("calloc");

        paout = new Qvarc_var_objc_objNumTrace("paout");

        virtualcalls = new Qctxt_var_obj_srcm_stmt_kind_tgtmNumTrace("virtualcalls");
        vcmout = new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace("vcmout");
    }

    private void buildQueues() {
        if( options.debugq() ) buildQueuesDebug();
        else if( options.bddq() ) buildQueuesBDD();
        else if( options.trace() ) buildQueuesTrace();
        else if( options.numtrace() ) buildQueuesNumTrace();
        else buildQueuesTrad();
    }
    private void buildTrad() {
        buildQueues();

        nodeManager = new NodeManager( locals, globals, localallocs, globalallocs );
        nodeFactory = new NodeFactory( simple, load, store, alloc );

        cicg = new TradCallGraph( scgbout.reader("cicg"), cicgout );
        rm = new TradReachableMethods( cicgout.reader("rm"), csout.reader("rm"), rmout, cicg );
        scgb = new TradStaticCallBuilder( rmout.reader("scgb"), scgbout, receivers, specials );

        cg = new TradCallGraph( 
                new Rsrcc_srcm_stmt_kind_tgtc_tgtmMerge(
                    scmout.reader("cg"), vcmout.reader("cg") ),
                cgout );
        rc = new TradReachableMethods( cgout.reader("rc"), null, rcout, cg );
        cscgb = new TradContextCallGraphBuilder( rcout.reader("cscgb"), cicgout.reader("cscgb"), staticcalls, cicg );

        mpb = new TradMethodPAGBuilder( rmout.reader("mpb"), simple, load, store, alloc );
        mpc = new TradMethodPAGContextifier(
                simple.reader("mpc"),
                store.reader("mpc"),
                load.reader("mpc"),
                alloc.reader("mpc"),
                locals.reader("mpc"),
                globals.reader("mpc"),
                localallocs.reader("mpc"),
                globalallocs.reader("mpc"),
                rcout.reader("mpc"),
                csimple, cstore, cload, calloc );
        pagb = new TradPAGBuilder( cgout.reader("pagb"), csimple, cload, cstore, calloc );

        pag = new TradPAG( csimple.reader("pag"), cload.reader("pag"),
                cstore.reader("pag"), calloc.reader("pag") );
        makePropagator();

        vcr = new TradVirtualCalls( paout.reader("vcr"), receivers.reader("vcr"), specials.reader("vcr"), virtualcalls, staticcalls );
        cs = new TradContextStripper( rcout.reader("cs"), csout );

        tm = new TradTypeManager(
                locals.reader("tm"),
                globals.reader("tm"),
                localallocs.reader("tm"),
                globalallocs.reader("tm"),
                options.ignore_types() ? null : Scene.v().getOrMakeFastHierarchy() );
        switch( cgoptions.context() ) {
            case CGOptions.context_insens:
                scm = new TradInsensitiveStaticContextManager( staticcalls.reader("scm"), scmout );
                vcm = new TradInsensitiveVirtualContextManager( virtualcalls.reader("vcm"), vcmout );
                break;
            case CGOptions.context_1cfa:
                scm = new Trad1CFAStaticContextManager( staticcalls.reader("scm"), scmout );
                vcm = new Trad1CFAVirtualContextManager( virtualcalls.reader("vcm"), vcmout );
                break;
            case CGOptions.context_objsens:
                scm = new TradObjSensStaticContextManager( staticcalls.reader("scm"), scmout );
                vcm = new TradObjSensVirtualContextManager( virtualcalls.reader("vcm"), vcmout );
                break;
            case CGOptions.context_kcfa:
                scm = new TradKCFAStaticContextManager( staticcalls.reader("scm"), scmout, cgoptions.k() );
                vcm = new TradKCFAVirtualContextManager( virtualcalls.reader("vcm"), vcmout, cgoptions.k() );
                break;
            case CGOptions.context_kobjsens:
                scm = new TradKObjSensStaticContextManager( staticcalls.reader("scm"), scmout, cgoptions.k() );
                vcm = new TradKObjSensVirtualContextManager( virtualcalls.reader("vcm"), vcmout, cgoptions.k() );
                break;
            default:
                throw new RuntimeException( "Unhandled kind of context-sensitivity" );
        }
    }

    private void makePropagator() {
        switch( options.propagator() ) {
            case PaddleOptions.propagator_worklist:
                prop = new PropWorklist( csimple.reader("prop"), cload.reader("prop"),
                    cstore.reader("prop"), calloc.reader("prop"), paout, pag );
                p2sets = new TradP2Sets();
                break;
            case PaddleOptions.propagator_iter:
                prop = new PropIter( csimple.reader("prop"), cload.reader("prop"),
                    cstore.reader("prop"), calloc.reader("prop"), paout, pag );
                p2sets = new TradP2Sets();
                break;
            case PaddleOptions.propagator_alias:
                prop = new PropAlias( csimple.reader("prop"), cload.reader("prop"),
                    cstore.reader("prop"), calloc.reader("prop"), paout, pag );
                p2sets = new TradP2Sets();
                break;
            case PaddleOptions.propagator_bdd:
                prop = new PropBDD( csimple.reader("prop"), cload.reader("prop"),
                    cstore.reader("prop"), calloc.reader("prop"), paout, pag );
                p2sets = new BDDP2Sets( (PropBDD) prop );
                break;
            default:
                throw new RuntimeException( "Unimplemented propagator specified" );
        }
    }

    private void updateFrontEnd() {
        boolean change;
        do {
            change = false;
            change = change | rm.update();
            change = change | rc.update();
            change = change | cs.update();
            change = change | scgb.update();
            change = change | cicg.update();
            change = change | cscgb.update();
            change = change | vcr.update();
            change = change | vcm.update();
            change = change | scm.update();
            change = change | cg.update();
        } while( change );
        mpb.update();
        mpc.update();
        pagb.update();
        pag.update();
    }

    void updateCallGraph() {
        updateFrontEnd();
    }
}


