/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

/* THIS FILE IS AUTO-GENERATED FROM options. DO NOT MODIFY */



package soot.jimple.spark;
import java.util.*;
import soot.Options;

/** Various options regulating the functioning of Spark.
 * @author Ondrej Lhotak
 */
public class SparkOptions {
    public SparkOptions( Map options ) {
        this.options = options;
    }


/*********************************************************************
*** General options
*********************************************************************/

    /**
     * If set to true, Spark prints detailed information.
     * Default value is false
     */
    public boolean verbose() {
        return Options.getBoolean( options, "verbose" );
    }

    /**
     * If set to true, all parts of Spark completely ignore declared types of
     * variables and casts.
     * Default value is false
     */
    public boolean ignoreTypesEntirely() {
        return Options.getBoolean( options, "ignoreTypesEntirely" );
    }


/*********************************************************************
*** Pointer assignment graph building options
*********************************************************************/

    /**
     * If set to true, parameters to methods are represented as fields (Red
     * nodes) of the 'this' object; otherwise, parameters are represented as
     * variable (Green) nodes.
     * Default value is false
     */
    public boolean parmsAsFields() {
        return Options.getBoolean( options, "parmsAsFields" );
    }

    /**
     * If set to true, return values from methods are represented as fields
     * (Red nodes) of the 'this' object; otherwise, return values are
     * represented as variable (Green) nodes.
     * Default value is false
     */
    public boolean returnsAsFields() {
        return Options.getBoolean( options, "returnsAsFields" );
    }

    /**
     * If set to true, fields are represented by variable (Green) nodes, and
     * the object that the field belongs to is ignored (all fields are lumped
     * together). Otherwise, fields are represented by field reference (Red)
     * nodes, and the objects that they belong to are distinguished.
     * Default value is false
     */
    public boolean collapseObjects() {
        return Options.getBoolean( options, "collapseObjects" );
    }

    /**
     * If set to true, types rather than allocation sites are used as the
     * elements of the points-to sets.
     * Default value is false
     */
    public boolean typesForSites() {
        return Options.getBoolean( options, "typesForSites" );
    }

    /**
     * If set to true, all allocation sites creating java.lang.StringBuffer
     * objects are grouped together as a single allocation site.
     * Default value is true
     */
    public boolean mergeStringBuffer() {
        return Options.getBoolean( options, "mergeStringBuffer" );
    }

    /**
     * If set to true, effects of native methods are simulated.
     * Default value is false
     */
    public boolean simulateNatives() {
        return Options.getBoolean( options, "simulateNatives" );
    }

    /**
     * If set to true, all edges connecting variable (Green) nodes are made
     * bidirectional, as in Steensgaard's analysis.
     * Default value is false
     */
    public boolean simpleEdgesBidirectional() {
        return Options.getBoolean( options, "simpleEdgesBidirectional" );
    }

    /**
     * If set to true, the call graph is computed on-the-fly as points-to
     * information is computed. Otherwise, an initial approximation
     * to the call graph is used.
     * Default value is false
     */
    public boolean onFlyCallGraph() {
        return Options.getBoolean( options, "onFlyCallGraph" );
    }


/*********************************************************************
*** Pointer assignment graph simplification options
*********************************************************************/

    /**
     * If set to true, variable (Green) nodes which are connected by simple paths
     * (so they must have the same points-to set) are merged together.
     * Default value is true
     */
    public boolean collapseEBBs() {
        return Options.getBoolean( options, "collapseEBBs" );
    }

    /**
     * If set to true, variable (Green) nodes which are form strongly-connected
     * components (so they must have the same points-to set) are merged
     * together.
     * Default value is true
     */
    public boolean collapseSCCs() {
        return Options.getBoolean( options, "collapseSCCs" );
    }

    /**
     * If set to true, when collapsing strongly-connected components, nodes
     * forming SCCs are collapsed regardless of their type. The collapsed SCC
     * is given the most general type of all the nodes in the component. If no
     * node has the most general type, then the SCC is given no type, and may
     * point to objects of any type.
     * 
     * If set to false, only edges connecting nodes of the same type are
     * considered when detecting SCCs.
     * 
     * This option has no effect if collapseSCCs is false.
     * Default value is false
     */
    public boolean ignoreTypesForSCCs() {
        return Options.getBoolean( options, "ignoreTypesForSCCs" );
    }


/*********************************************************************
*** Points-to set flowing options
*********************************************************************/

    /**
     * Tells Spark which propagation algorithm to use.
     * 
     * Iter is a dumb, iterative algorithm, that propagates everything until the
     * graph does not change.
     * 
     * Worklist is a worklist-based algorithm that tries
     * to do as little work as possible.
     * 
     * Merge is an algorithm that merges all yellow nodes with their corresponding
     * red nodes.
     * 
     * Alias is a relevant aliases based algorithm.
     * 
     * None means that propagation is not done; the graph is only built and
     * simplified.
     * Default value is worklist
     */
    public void propagator( Switch_propagator sw ) {
        String s = Options.getString( options, "propagator" );
        if( false );
        else if( s.equalsIgnoreCase("iter") ) sw.case_iter();
        else if( s.equalsIgnoreCase("worklist") ) sw.case_worklist();
        else if( s.equalsIgnoreCase("merge") ) sw.case_merge();
        else if( s.equalsIgnoreCase("alias") ) sw.case_alias();
        else if( s.equalsIgnoreCase("none") ) sw.case_none();
        else throw new RuntimeException( "Invalid value \""+s+"\" of option propagator" );
    }
    public static abstract class Switch_propagator {
        public abstract void case_iter();
        public abstract void case_worklist();
        public abstract void case_merge();
        public abstract void case_alias();
        public abstract void case_none();
    }

    /**
     * Selects an implementation of a points-to set that Spark should use.
     * 
     * Hash is an implementation based on Java's built-in hash-set.
     * 
     * Bit is an implementation using a bit vector.
     * 
     * Hybrid is an implementation that keeps an explicit list of up to
     * 16 elements, and switches to using a bit-vector when the set gets
     * larger than this.
     * 
     * FastType is a variation of Hybrid that uses a bit vector to perform fast
     * subtype tests.
     * 
     * Array is an implementation that keeps the elements of the points-to set
     * in an array that is always maintained in sorted order. Set membership is
     * tested using binary search, and set union and intersection are computed
     * using an algorithm based on the merge step from merge sort.
     * 
     * Double is an implementation that itself uses a pair of sets for each
     * points-to set. The first set in the pair stores new pointed-to objects
     * that have not yet been propagated, while the second set stores old
     * pointed-to objects that have been propagated and need not be reconsidered.
     * Default value is double
     */
    public void setImpl( Switch_setImpl sw ) {
        String s = Options.getString( options, "setImpl" );
        if( false );
        else if( s.equalsIgnoreCase("double") ) sw.case_double();
        else if( s.equalsIgnoreCase("hash") ) sw.case_hash();
        else if( s.equalsIgnoreCase("bit") ) sw.case_bit();
        else if( s.equalsIgnoreCase("hybrid") ) sw.case_hybrid();
        else if( s.equalsIgnoreCase("fasttype") ) sw.case_fasttype();
        else if( s.equalsIgnoreCase("array") ) sw.case_array();
        else throw new RuntimeException( "Invalid value \""+s+"\" of option setImpl" );
    }
    public static abstract class Switch_setImpl {
        public abstract void case_double();
        public abstract void case_hash();
        public abstract void case_bit();
        public abstract void case_hybrid();
        public abstract void case_fasttype();
        public abstract void case_array();
    }

    /**
     * Selects an implementation for the sets of old objects in the double
     * points-to set implementation.
     * 
     * This option has no effect if setImpl is not set to double.
     * Default value is hybrid
     */
    public void doubleSetOld( Switch_doubleSetOld sw ) {
        String s = Options.getString( options, "doubleSetOld" );
        if( false );
        else if( s.equalsIgnoreCase("hash") ) sw.case_hash();
        else if( s.equalsIgnoreCase("bit") ) sw.case_bit();
        else if( s.equalsIgnoreCase("hybrid") ) sw.case_hybrid();
        else if( s.equalsIgnoreCase("fasttype") ) sw.case_fasttype();
        else if( s.equalsIgnoreCase("array") ) sw.case_array();
        else throw new RuntimeException( "Invalid value \""+s+"\" of option doubleSetOld" );
    }
    public static abstract class Switch_doubleSetOld {
        public abstract void case_hash();
        public abstract void case_bit();
        public abstract void case_hybrid();
        public abstract void case_fasttype();
        public abstract void case_array();
    }

    /**
     * Selects an implementation for the sets of new objects in the double
     * points-to set implementation.
     * 
     * This option has no effect if setImpl is not set to double.
     * Default value is hybrid
     */
    public void doubleSetNew( Switch_doubleSetNew sw ) {
        String s = Options.getString( options, "doubleSetNew" );
        if( false );
        else if( s.equalsIgnoreCase("array") ) sw.case_array();
        else if( s.equalsIgnoreCase("hash") ) sw.case_hash();
        else if( s.equalsIgnoreCase("bit") ) sw.case_bit();
        else if( s.equalsIgnoreCase("hybrid") ) sw.case_hybrid();
        else if( s.equalsIgnoreCase("fasttype") ) sw.case_fasttype();
        else throw new RuntimeException( "Invalid value \""+s+"\" of option doubleSetNew" );
    }
    public static abstract class Switch_doubleSetNew {
        public abstract void case_array();
        public abstract void case_hash();
        public abstract void case_bit();
        public abstract void case_hybrid();
        public abstract void case_fasttype();
    }


/*********************************************************************
*** Output options
*********************************************************************/

    /**
     * If set to true, a browseable HTML representation of the pointer assignment
     * graph is output after the analysis completes.
     * Default value is false
     */
    public boolean dumpHTML() {
        return Options.getBoolean( options, "dumpHTML" );
    }

    /**
     * If set to true, a representation of the pointer assignment graph
     * suitable for processing with other solvers (such as the BDD one) is
     * output before the analysis begins.
     * Default value is false
     */
    public boolean dumpPAG() {
        return Options.getBoolean( options, "dumpPAG" );
    }

    /**
     * If set to true, a representation of the resulting points-to
     * sets is dumped. The format is similar to that of the dumpPAG
     * option, and is therefore suitable for comparison with the results of other
     * solvers.
     * Default value is false
     */
    public boolean dumpSolution() {
        return Options.getBoolean( options, "dumpSolution" );
    }

    /**
     * If set to true, the representation dumped by the dumpPAG option
     * is dumped with the green nodes in (pseudo-)topological order.
     * 
     * This option has no effect if dumpPAG is false.
     * Default value is false
     */
    public boolean topoSort() {
        return Options.getBoolean( options, "topoSort" );
    }

    /**
     * If set to true, the representation dumped by the dumpPAG option
     * includes type information for all nodes.
     * 
     * This option has no effect if dumpPAG is false.
     * Default value is true
     */
    public boolean dumpTypes() {
        return Options.getBoolean( options, "dumpTypes" );
    }

    /**
     * If set to true, the computed reaching types for each variable are
     * dumped to a file, so that they can be compared with the results of
     * other analyses.
     * Default value is false
     */
    public boolean dumpAnswer() {
        return Options.getBoolean( options, "dumpAnswer" );
    }

    public static String getDeclaredOptions() {
        return
        " verbose ignoreTypesEntirely parmsAsFields returnsAsFields collapseObjects typesForSites mergeStringBuffer simulateNatives simpleEdgesBidirectional onFlyCallGraph collapseEBBs collapseSCCs ignoreTypesForSCCs propagator setImpl doubleSetOld doubleSetNew dumpHTML dumpPAG dumpSolution topoSort dumpTypes dumpAnswer";
    }
    public static String getDefaultOptions() {
        return
        " verbose:false ignoreTypesEntirely:false parmsAsFields:false returnsAsFields:false collapseObjects:false typesForSites:false mergeStringBuffer:true simulateNatives:false simpleEdgesBidirectional:false onFlyCallGraph:false collapseEBBs:true collapseSCCs:true ignoreTypesForSCCs:false propagator:worklist setImpl:double doubleSetOld:hybrid doubleSetNew:hybrid dumpHTML:false dumpPAG:false dumpSolution:false topoSort:false dumpTypes:true dumpAnswer:false";
    }

    protected Map options;
}

