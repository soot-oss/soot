
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

/** Option parser for Spark. */
public class BDDSparkOptions extends AbstractSparkOptions
{
    private Map options;

    public BDDSparkOptions( Map options ) {
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
    
     * Print detailed information about the execution of Spark.
    
     * When this option is set to true, Spark prints detailed 
     * information about its execution. 
     */
    public boolean verbose() {
        return soot.PhaseOptions.getBoolean( options, "verbose" );
    }
    
    /** Ignore Types Entirely --
    
     * Make Spark completely ignore declared types of variables.
    
     * When this option is set to true, all parts of Spark completely 
     * ignore declared types of variables and casts. 
     */
    public boolean ignore_types() {
        return soot.PhaseOptions.getBoolean( options, "ignore-types" );
    }
    
    /** Force Garbage Collections --
    
     * Force garbage collection for measuring memory usage.
    
     * When this option is set to true, calls to System.gc() will be 
     * made at various points to allow memory usage to be measured. 
     * 
     */
    public boolean force_gc() {
        return soot.PhaseOptions.getBoolean( options, "force-gc" );
    }
    
    /** Pre Jimplify --
    
     * Jimplify all methods before starting Spark.
    
     * When this option is set to true, Spark converts all available 
     * methods to Jimple before starting the points-to analysis. This 
     * allows the Jimplification time to be separated from the 
     * points-to time. However, it increases the total time and memory 
     * requirement, because all methods are Jimplified, rather than 
     * only those deemed reachable by the points-to analysis. 
     */
    public boolean pre_jimplify() {
        return soot.PhaseOptions.getBoolean( options, "pre-jimplify" );
    }
    
    /** VTA --
    
     * Emulate Variable Type Analysis.
    
     * Setting VTA to true has the effect of setting field-based, 
     * types-for-sites, and simplify-sccs to true, and on-fly-cg to 
     * false, to simulate Variable Type Analysis, described in our 
     * OOPSLA 2000 paper. Note that the algorithm differs from the 
     * original VTA in that it handles array elements more precisely. 
     * 
     */
    public boolean vta() {
        return soot.PhaseOptions.getBoolean( options, "vta" );
    }
    
    /** RTA --
    
     * Emulate Rapid Type Analysis.
    
     * Setting RTA to true sets types-for-sites to true, and causes 
     * Spark to use a single points-to set for all variables, giving 
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
     * allocation site. 
     */
    public boolean merge_stringbuffer() {
        return soot.PhaseOptions.getBoolean( options, "merge-stringbuffer" );
    }
    
    /** Simulate Natives --
    
     * Simulate effects of native methods in standard class library.
    
     * When this option is set to true, the effects of native methods 
     * in the standard Java class library are simulated. 
     */
    public boolean simulate_natives() {
        return soot.PhaseOptions.getBoolean( options, "simulate-natives" );
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
    
    /** On Fly Call Graph --
    
     * Build call graph as receiver types become known.
    
     * When this option is set to true, the call graph is computed 
     * on-the-fly as points-to information is computed. Otherwise, an 
     * initial CHA approximation to the call graph is used. 
     */
    public boolean on_fly_cg() {
        return soot.PhaseOptions.getBoolean( options, "on-fly-cg" );
    }
    
    /** Parms As Fields --
    
     * Represent method parameters as fields of this.
    
     * When this option is set to true, parameters to methods are 
     * represented as fields (Red nodes) of the this object; otherwise, 
     * parameters are represented as variable (Green) nodes. 
     */
    public boolean parms_as_fields() {
        return soot.PhaseOptions.getBoolean( options, "parms-as-fields" );
    }
    
    /** Returns As Fields --
    
     * Represent method return values as fields of this.
    
     * When this option is set to true, return values from methods are 
     * represented as fields (Red nodes) of the this object; otherwise, 
     * return values are represented as variable (Green) nodes. 
     * 
     */
    public boolean returns_as_fields() {
        return soot.PhaseOptions.getBoolean( options, "returns-as-fields" );
    }
    
    /** Simplify Offline --
    
     * Collapse single-entry subgraphs of the PAG.
    
     * When this option is set to true, variable (Green) nodes which 
     * form single-entry subgraphs (so they must have the same 
     * points-to set) are merged before propagation begins. 
     */
    public boolean simplify_offline() {
        return soot.PhaseOptions.getBoolean( options, "simplify-offline" );
    }
    
    /** Simplify SCCs --
    
     * Collapse strongly-connected components of the PAG.
    
     * When this option is set to true, variable (Green) nodes which 
     * form strongly-connected components (so they must have the same 
     * points-to set) are merged before propagation begins. 
     */
    public boolean simplify_sccs() {
        return soot.PhaseOptions.getBoolean( options, "simplify-sccs" );
    }
    
    /** Ignore Types For SCCs --
    
     * Ignore declared types when determining node equivalence for SCCs.
    
     * When this option is set to true, when collapsing 
     * strongly-connected components, nodes forming SCCs are collapsed 
     * regardless of their declared type. The collapsed SCC is given 
     * the most general type of all the nodes in the component. When 
     * this option is set to false, only edges connecting nodes of the 
     * same type are considered when detecting SCCs. This option has 
     * no effect unless simplify-sccs is true. 
     */
    public boolean ignore_types_for_sccs() {
        return soot.PhaseOptions.getBoolean( options, "ignore-types-for-sccs" );
    }
    
    /** Dump HTML --
    
     * Dump pointer assignment graph to HTML for debugging.
    
     * When this option is set to true, a browseable HTML 
     * representation of the pointer assignment graph is output to a 
     * file called pag.jar after the analysis completes. Note that this 
     * representation is typically very large. 
     */
    public boolean dump_html() {
        return soot.PhaseOptions.getBoolean( options, "dump-html" );
    }
    
    /** Dump PAG --
    
     * Dump pointer assignment graph for other solvers.
    
     * When this option is set to true, a representation of the 
     * pointer assignment graph suitable for processing with other 
     * solvers (such as the BDD-based solver) is output before the 
     * analysis begins. 
     */
    public boolean dump_pag() {
        return soot.PhaseOptions.getBoolean( options, "dump-pag" );
    }
    
    /** Dump Solution --
    
     * Dump final solution for comparison with other solvers.
    
     * When this option is set to true, a representation of the 
     * resulting points-to sets is dumped. The format is similar to 
     * that of the Dump PAG option, and is therefore suitable for 
     * comparison with the results of other solvers. 
     */
    public boolean dump_solution() {
        return soot.PhaseOptions.getBoolean( options, "dump-solution" );
    }
    
    /** Topological Sort --
    
     * Sort variable nodes in dump.
    
     * When this option is set to true, the representation dumped by 
     * the Dump PAG option is dumped with the variable (green) nodes in 
     * (pseudo-)topological order. This option has no effect unless 
     * Dump PAG is true. 
     */
    public boolean topo_sort() {
        return soot.PhaseOptions.getBoolean( options, "topo-sort" );
    }
    
    /** Dump Types --
    
     * Include declared types in dump.
    
     * When this option is set to true, the representation dumped by 
     * the Dump PAG option includes type information for all nodes. 
     * This option has no effect unless Dump PAG is true. 
     */
    public boolean dump_types() {
        return soot.PhaseOptions.getBoolean( options, "dump-types" );
    }
    
    /** Class Method Var --
    
     * In dump, label variables by class and method.
    
     * When this option is set to true, the representation dumped by 
     * the Dump PAG option represents nodes by numbering each class, 
     * method, and variable within the method separately, rather than 
     * assigning a single integer to each node. This option has no 
     * effect unless Dump PAG is true. Setting Class Method Var to 
     * true has the effect of setting Topological Sort to false. 
     * 
     */
    public boolean class_method_var() {
        return soot.PhaseOptions.getBoolean( options, "class-method-var" );
    }
    
    /** Dump Answer --
    
     * Dump computed reaching types for comparison with other solvers.
    
     * When this option is set to true, the computed reaching types 
     * for each variable are dumped to a file, so that they can be 
     * compared with the results of other analyses (such as the old 
     * VTA). 
     */
    public boolean dump_answer() {
        return soot.PhaseOptions.getBoolean( options, "dump-answer" );
    }
    
    /** Add Tags --
    
     * Output points-to results in tags for viewing with the Jimple.
    
     * When this option is set to true, the results of the 
     * analysis are encoded within tags and printed with the resulting 
     * Jimple code. 
     */
    public boolean add_tags() {
        return soot.PhaseOptions.getBoolean( options, "add-tags" );
    }
    
    /** Calculate Set Mass --
    
     * Calculate statistics about points-to set sizes.
    
     * When this option is set to true, Spark computes and prints 
     * various cryptic statistics about the size of the points-to sets 
     * computed. 
     */
    public boolean set_mass() {
        return soot.PhaseOptions.getBoolean( options, "set-mass" );
    }
    
}
        