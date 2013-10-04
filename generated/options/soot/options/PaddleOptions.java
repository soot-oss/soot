
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

/* THIS FILE IS AUTO-GENERATED FROM soot_options.xml. DO NOT MODIFY. */

package soot.options;
import java.util.*;

/** Option parser for Paddle. */
public class PaddleOptions
{
    private Map<String, String> options;

    public PaddleOptions( Map<String, String> options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** Verbose --
    
     * Print detailed information about the execution of Paddle.
    
     * When this option is set to true, Paddle prints detailed 
     * information about its execution. 
     */
    public boolean verbose() {
        return soot.PhaseOptions.getBoolean( options, "verbose" );
    }
    
    /** Use BDDs --
    
     * Use BDD version of Paddle.
    
     * Causes 
     * Paddle to use BDD versions of its components 
     */
    public boolean bdd() {
        return soot.PhaseOptions.getBoolean( options, "bdd" );
    }
    
    /** Dynamic reordering --
    
     * .
    
     * Allows the BDD package 
     * to perform dynamic variable ordering. 
     */
    public boolean dynamic_order() {
        return soot.PhaseOptions.getBoolean( options, "dynamic-order" );
    }
    
    /** Profile --
    
     * Profile BDDs using JeddProfiler.
    
     * Turns on JeddProfiler for profiling BDD operations. 
     */
    public boolean profile() {
        return soot.PhaseOptions.getBoolean( options, "profile" );
    }
    
    /** Verbose GC --
    
     * Print memory usage at each BDD garbage collection..
    
     * Print memory usage at each BDD garbage collection. 
     */
    public boolean verbosegc() {
        return soot.PhaseOptions.getBoolean( options, "verbosegc" );
    }
    
    /** Ignore Types Entirely --
    
     * Make Paddle completely ignore declared types of variables.
    
     * When this option is set to true, all parts of Paddle completely 
     * ignore declared types of variables and casts. 
     */
    public boolean ignore_types() {
        return soot.PhaseOptions.getBoolean( options, "ignore-types" );
    }
    
    /** Pre Jimplify --
    
     * Jimplify all methods before starting Paddle.
    
     * When this option is set to true, Paddle converts all available 
     * methods to Jimple before starting the points-to analysis. This 
     * allows the Jimplification time to be separated from the 
     * points-to time. However, it increases the total time and memory 
     * requirement, because all methods are Jimplified, rather than 
     * only those deemed reachable by the points-to analysis. 
     */
    public boolean pre_jimplify() {
        return soot.PhaseOptions.getBoolean( options, "pre-jimplify" );
    }
    
    /** Context-sensitive Heap Locations --
    
     * Treat allocation sites context-sensitively.
    
     * When this option is set to true, the context-sensitivity level 
     * that is set for the context-sensitive call graph and for pointer 
     * variables is also used to model heap locations 
     * context-sensitively. When this option is false, heap locations 
     * are modelled context-insensitively regardless of the 
     * context-sensitivity level. 
     */
    public boolean context_heap() {
        return soot.PhaseOptions.getBoolean( options, "context-heap" );
    }
    
    /** RTA --
    
     * Emulate Rapid Type Analysis.
    
     * Setting RTA to true sets types-for-sites to true, and causes 
     * Paddle to use a single points-to set for all variables, giving 
     * Rapid Type Analysis. 
     */
    public boolean rta() {
        return soot.PhaseOptions.getBoolean( options, "rta" );
    }
    
    /** Field Based --
    
     * Use a field-based rather than field-sensitive representation.
    
     * When this option is set to true, fields are represented by 
     * variable (Green) nodes, and the object that the field belongs to 
     * is ignored (all objects are lumped together), giving a 
     * field-based analysis. Otherwise, fields are represented by field 
     * reference (Red) nodes, and the objects that they belong to are 
     * distinguished, giving a field-sensitive analysis. 
     */
    public boolean field_based() {
        return soot.PhaseOptions.getBoolean( options, "field-based" );
    }
    
    /** Types For Sites --
    
     * Represent objects by their actual type rather than allocation 
     * site.
    
     * When this option is set to true, types rather than allocation 
     * sites are used as the elements of the points-to sets. 
     */
    public boolean types_for_sites() {
        return soot.PhaseOptions.getBoolean( options, "types-for-sites" );
    }
    
    /** Merge String Buffer --
    
     * Represent all StringBuffers as one object.
    
     * When this option is set to true, all allocation sites creating 
     * java.lang.StringBuffer objects are grouped together as a single 
     * allocation site. Allocation sites creating a 
     * java.lang.StringBuilder object are also grouped together as a 
     * single allocation site. 
     */
    public boolean merge_stringbuffer() {
        return soot.PhaseOptions.getBoolean( options, "merge-stringbuffer" );
    }
    
    /** Propagate All String Constants --
    
     * Propagate all string constants, not just class names.
    
     * When this option is set to false, Paddle only distinguishes 
     * string constants that may be the name of a class loaded 
     * dynamically using reflection, and all other string constants are 
     * lumped together into a single string constant node. Setting this 
     * option to true causes all string constants to be propagated 
     * individually. 
     */
    public boolean string_constants() {
        return soot.PhaseOptions.getBoolean( options, "string-constants" );
    }
    
    /** Simulate Natives --
    
     * Simulate effects of native methods in standard class library.
    
     * When this option is set to true, the effects of native methods 
     * in the standard Java class library are simulated. 
     */
    public boolean simulate_natives() {
        return soot.PhaseOptions.getBoolean( options, "simulate-natives" );
    }
    
    /** Global Nodes in Simulated Natives --
    
     * Use global node to model variables in simulations of native 
     * methods.
    
     * The simulations of native methods such as System.arraycopy() 
     * use temporary local variable nodes. Setting this switch to true 
     * causes them to use global variable nodes instead, reducing 
     * precision. The switch exists only to make it possible to measure 
     * this effect on precision; there is no other practical reason to 
     * set it to true. 
     */
    public boolean global_nodes_in_natives() {
        return soot.PhaseOptions.getBoolean( options, "global-nodes-in-natives" );
    }
    
    /** Simple Edges Bidirectional --
    
     * Equality-based analysis between variable nodes.
    
     * When this option is set to true, all edges connecting variable 
     * (Green) nodes are made bidirectional, as in Steensgaard's 
     * analysis. 
     */
    public boolean simple_edges_bidirectional() {
        return soot.PhaseOptions.getBoolean( options, "simple-edges-bidirectional" );
    }
    
    /** this Pointer Assignment Edge --
    
     * Use pointer assignment edges to model this parameters.
    
     * When constructing a call graph on-the-fly during points-to 
     * analysis, Paddle normally propagates only those receivers that 
     * cause a method to be invoked to the this pointer of the method. 
     * When this option is set to true, however, Paddle instead models 
     * flow of receivers as an assignnment edge from the receiver at 
     * the call site to the this pointer of the method, reducing 
     * precision. 
     */
    public boolean this_edges() {
        return soot.PhaseOptions.getBoolean( options, "this-edges" );
    }
    
    /** Precise newInstance --
    
     * Make newInstance only allocate objects of dynamic classes.
    
     * Normally, newInstance() calls are treated as if they may 
     * return an object of any type. Setting this option to true 
     * causes them to be treated as if they return only objects of 
     * the type of some dynamic class. 
     */
    public boolean precise_newinstance() {
        return soot.PhaseOptions.getBoolean( options, "precise-newinstance" );
    }
    
    /** Print Context Counts --
    
     * Print number of contexts for each method.
    
     * Causes Paddle to print the number of contexts for each method 
     * and call edge, and the number of equivalence classes of contexts 
     * for each variable node. 
     */
    public boolean context_counts() {
        return soot.PhaseOptions.getBoolean( options, "context-counts" );
    }
    
    /** Print Context Counts (Totals only) --
    
     * Print total number of contexts.
    
     * Causes Paddle to print the number of contexts and number of 
     * context equivalence classes. 
     */
    public boolean total_context_counts() {
        return soot.PhaseOptions.getBoolean( options, "total-context-counts" );
    }
    
    /** Method Context Counts (Totals only) --
    
     * Print number of contexts for each method.
    
     * Causes Paddle to print the number of contexts and number of 
     * context equivalence classes split out by method. Requires 
     * total-context-counts to also be turned on. 
     */
    public boolean method_context_counts() {
        return soot.PhaseOptions.getBoolean( options, "method-context-counts" );
    }
    
    /** Calculate Set Mass --
    
     * Calculate statistics about points-to set sizes.
    
     * When this option is set to true, Paddle computes and prints 
     * various cryptic statistics about the size of the points-to sets 
     * computed. 
     */
    public boolean set_mass() {
        return soot.PhaseOptions.getBoolean( options, "set-mass" );
    }
    
    /** Number nodes --
    
     * Print node numbers in dumps.
    
     * When printing debug information about nodes, this option causes 
     * the node number of each node to be printed. 
     */
    public boolean number_nodes() {
        return soot.PhaseOptions.getBoolean( options, "number-nodes" );
    }
    
    /** Variable ordering --
    
     * .
    
     * Selects one of the BDD 
     * variable orderings hard-coded in Paddle. 
     */
    public int order() {
        return soot.PhaseOptions.getInt( options, "order" );
    }
    
    /** BDD Nodes --
    
     * Number of BDD nodes to allocate (0=unlimited).
    
     * This option specifies the number of BDD nodes to be used by the 
     * BDD backend. A value of 0 causes the backend to start with one 
     * million nodes, and allocate more as required. A value other than 
     * zero causes the backend to start with the specified size, and 
     * prevents it from ever allocating any more nodes. 
     */
    public int bdd_nodes() {
        return soot.PhaseOptions.getInt( options, "bdd-nodes" );
    }
    
    /** Context length (k) --
    
     * .
    
     * The maximum length of 
     * call string or receiver object string used as context. 
     * 
     */
    public int k() {
        return soot.PhaseOptions.getInt( options, "k" );
    }
    
    public static final int conf_ofcg = 1;
    public static final int conf_cha = 2;
    public static final int conf_cha_aot = 3;
    public static final int conf_ofcg_aot = 4;
    public static final int conf_cha_context_aot = 5;
    public static final int conf_ofcg_context_aot = 6;
    public static final int conf_cha_context = 7;
    public static final int conf_ofcg_context = 8;
    /** Configuration --
    
     * Select Paddle configuration.
    
     * Selects the configuration of points-to analysis and call graph 
     * construction to be used in Paddle. 
     */
    public int conf() {
        String s = soot.PhaseOptions.getString( options, "conf" );
        
        if( s.equalsIgnoreCase( "ofcg" ) )
            return conf_ofcg;
        
        if( s.equalsIgnoreCase( "cha" ) )
            return conf_cha;
        
        if( s.equalsIgnoreCase( "cha-aot" ) )
            return conf_cha_aot;
        
        if( s.equalsIgnoreCase( "ofcg-aot" ) )
            return conf_ofcg_aot;
        
        if( s.equalsIgnoreCase( "cha-context-aot" ) )
            return conf_cha_context_aot;
        
        if( s.equalsIgnoreCase( "ofcg-context-aot" ) )
            return conf_ofcg_context_aot;
        
        if( s.equalsIgnoreCase( "cha-context" ) )
            return conf_cha_context;
        
        if( s.equalsIgnoreCase( "ofcg-context" ) )
            return conf_ofcg_context;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option conf" );
    }
    
    public static final int q_auto = 1;
    public static final int q_trad = 2;
    public static final int q_bdd = 3;
    public static final int q_debug = 4;
    public static final int q_trace = 5;
    public static final int q_numtrace = 6;
    /** Worklist Implementation --
    
     * Select queue implementation.
    
     * Select the implementation of worklists to be used in Paddle. 
     * 
     */
    public int q() {
        String s = soot.PhaseOptions.getString( options, "q" );
        
        if( s.equalsIgnoreCase( "auto" ) )
            return q_auto;
        
        if( s.equalsIgnoreCase( "trad" ) )
            return q_trad;
        
        if( s.equalsIgnoreCase( "bdd" ) )
            return q_bdd;
        
        if( s.equalsIgnoreCase( "debug" ) )
            return q_debug;
        
        if( s.equalsIgnoreCase( "trace" ) )
            return q_trace;
        
        if( s.equalsIgnoreCase( "numtrace" ) )
            return q_numtrace;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option q" );
    }
    
    public static final int backend_auto = 1;
    public static final int backend_buddy = 2;
    public static final int backend_cudd = 3;
    public static final int backend_sable = 4;
    public static final int backend_javabdd = 5;
    public static final int backend_none = 6;
    /** Backend --
    
     * Select BDD backend.
    
     * This option tells Paddle which implementation of BDDs to use. 
     * 
     */
    public int backend() {
        String s = soot.PhaseOptions.getString( options, "backend" );
        
        if( s.equalsIgnoreCase( "auto" ) )
            return backend_auto;
        
        if( s.equalsIgnoreCase( "buddy" ) )
            return backend_buddy;
        
        if( s.equalsIgnoreCase( "cudd" ) )
            return backend_cudd;
        
        if( s.equalsIgnoreCase( "sable" ) )
            return backend_sable;
        
        if( s.equalsIgnoreCase( "javabdd" ) )
            return backend_javabdd;
        
        if( s.equalsIgnoreCase( "none" ) )
            return backend_none;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option backend" );
    }
    
    public static final int context_insens = 1;
    public static final int context_1cfa = 2;
    public static final int context_kcfa = 3;
    public static final int context_objsens = 4;
    public static final int context_kobjsens = 5;
    public static final int context_uniqkobjsens = 6;
    public static final int context_threadkobjsens = 7;
    /** Context abstraction --
    
     * Select context-sensitivity level.
    
     * This option tells Paddle which level of context-sensitivity to 
     * use in constructing the call graph. 
     */
    public int context() {
        String s = soot.PhaseOptions.getString( options, "context" );
        
        if( s.equalsIgnoreCase( "insens" ) )
            return context_insens;
        
        if( s.equalsIgnoreCase( "1cfa" ) )
            return context_1cfa;
        
        if( s.equalsIgnoreCase( "kcfa" ) )
            return context_kcfa;
        
        if( s.equalsIgnoreCase( "objsens" ) )
            return context_objsens;
        
        if( s.equalsIgnoreCase( "kobjsens" ) )
            return context_kobjsens;
        
        if( s.equalsIgnoreCase( "uniqkobjsens" ) )
            return context_uniqkobjsens;
        
        if( s.equalsIgnoreCase( "threadkobjsens" ) )
            return context_threadkobjsens;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option context" );
    }
    
    public static final int propagator_auto = 1;
    public static final int propagator_iter = 2;
    public static final int propagator_worklist = 3;
    public static final int propagator_alias = 4;
    public static final int propagator_bdd = 5;
    public static final int propagator_incbdd = 6;
    /** Propagator --
    
     * Select propagation algorithm.
    
     * This option tells Paddle which propagation algorithm to use. 
     * 
     */
    public int propagator() {
        String s = soot.PhaseOptions.getString( options, "propagator" );
        
        if( s.equalsIgnoreCase( "auto" ) )
            return propagator_auto;
        
        if( s.equalsIgnoreCase( "iter" ) )
            return propagator_iter;
        
        if( s.equalsIgnoreCase( "worklist" ) )
            return propagator_worklist;
        
        if( s.equalsIgnoreCase( "alias" ) )
            return propagator_alias;
        
        if( s.equalsIgnoreCase( "bdd" ) )
            return propagator_bdd;
        
        if( s.equalsIgnoreCase( "incbdd" ) )
            return propagator_incbdd;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option propagator" );
    }
    
    public static final int set_impl_hash = 1;
    public static final int set_impl_bit = 2;
    public static final int set_impl_hybrid = 3;
    public static final int set_impl_array = 4;
    public static final int set_impl_heintze = 5;
    public static final int set_impl_double = 6;
    /** Set Implementation --
    
     * Select points-to set implementation.
    
     * Select an implementation of points-to sets for Paddle to use. 
     */
    public int set_impl() {
        String s = soot.PhaseOptions.getString( options, "set-impl" );
        
        if( s.equalsIgnoreCase( "hash" ) )
            return set_impl_hash;
        
        if( s.equalsIgnoreCase( "bit" ) )
            return set_impl_bit;
        
        if( s.equalsIgnoreCase( "hybrid" ) )
            return set_impl_hybrid;
        
        if( s.equalsIgnoreCase( "array" ) )
            return set_impl_array;
        
        if( s.equalsIgnoreCase( "heintze" ) )
            return set_impl_heintze;
        
        if( s.equalsIgnoreCase( "double" ) )
            return set_impl_double;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option set-impl" );
    }
    
    public static final int double_set_old_hash = 1;
    public static final int double_set_old_bit = 2;
    public static final int double_set_old_hybrid = 3;
    public static final int double_set_old_array = 4;
    public static final int double_set_old_heintze = 5;
    /** Double Set Old --
    
     * Select implementation of points-to set for old part of double 
     * set.
    
     * Select an implementation for sets of old objects in the double 
     * points-to set implementation. This option has no effect unless 
     * Set Implementation is set to double. 
     */
    public int double_set_old() {
        String s = soot.PhaseOptions.getString( options, "double-set-old" );
        
        if( s.equalsIgnoreCase( "hash" ) )
            return double_set_old_hash;
        
        if( s.equalsIgnoreCase( "bit" ) )
            return double_set_old_bit;
        
        if( s.equalsIgnoreCase( "hybrid" ) )
            return double_set_old_hybrid;
        
        if( s.equalsIgnoreCase( "array" ) )
            return double_set_old_array;
        
        if( s.equalsIgnoreCase( "heintze" ) )
            return double_set_old_heintze;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option double-set-old" );
    }
    
    public static final int double_set_new_hash = 1;
    public static final int double_set_new_bit = 2;
    public static final int double_set_new_hybrid = 3;
    public static final int double_set_new_array = 4;
    public static final int double_set_new_heintze = 5;
    /** Double Set New --
    
     * Select implementation of points-to set for new part of double 
     * set.
    
     * Select an implementation for sets of new objects in the double 
     * points-to set implementation. This option has no effect unless 
     * Set Implementation is set to double. 
     */
    public int double_set_new() {
        String s = soot.PhaseOptions.getString( options, "double-set-new" );
        
        if( s.equalsIgnoreCase( "hash" ) )
            return double_set_new_hash;
        
        if( s.equalsIgnoreCase( "bit" ) )
            return double_set_new_bit;
        
        if( s.equalsIgnoreCase( "hybrid" ) )
            return double_set_new_hybrid;
        
        if( s.equalsIgnoreCase( "array" ) )
            return double_set_new_array;
        
        if( s.equalsIgnoreCase( "heintze" ) )
            return double_set_new_heintze;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option double-set-new" );
    }
    
}
        