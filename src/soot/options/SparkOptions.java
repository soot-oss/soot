
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
public class SparkOptions
{
    private Map options;

    public SparkOptions( Map options ) {
        this.options = options;
    }
    
    /** Enabled --  */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** Verbose --  */
    public boolean verbose() {
        return soot.PhaseOptions.getBoolean( options, "verbose" );
    }
    
    /** Ignore Types Entirely --  */
    public boolean ignore_types() {
        return soot.PhaseOptions.getBoolean( options, "ignore-types" );
    }
    
    /** Force Garbages Collections --  */
    public boolean force_gc() {
        return soot.PhaseOptions.getBoolean( options, "force-gc" );
    }
    
    /** Pre Jimplify --  */
    public boolean pre_jimplify() {
        return soot.PhaseOptions.getBoolean( options, "pre-jimplify" );
    }
    
    /** VTA --  */
    public boolean vta() {
        return soot.PhaseOptions.getBoolean( options, "vta" );
    }
    
    /** RTA --  */
    public boolean rta() {
        return soot.PhaseOptions.getBoolean( options, "rta" );
    }
    
    /** Field Based --  */
    public boolean field_based() {
        return soot.PhaseOptions.getBoolean( options, "field-based" );
    }
    
    /** Types For Sites --  */
    public boolean types_for_sites() {
        return soot.PhaseOptions.getBoolean( options, "types-for-sites" );
    }
    
    /** Merge String Buffer --  */
    public boolean merge_stringbuffer() {
        return soot.PhaseOptions.getBoolean( options, "merge-stringbuffer" );
    }
    
    /** Simulate Natives --  */
    public boolean simulate_natives() {
        return soot.PhaseOptions.getBoolean( options, "simulate-natives" );
    }
    
    /** Simple Edges Bidirectional --  */
    public boolean simple_edges_bidirectional() {
        return soot.PhaseOptions.getBoolean( options, "simple-edges-bidirectional" );
    }
    
    /** On Fly Call Graph --  */
    public boolean on_fly_cg() {
        return soot.PhaseOptions.getBoolean( options, "on-fly-cg" );
    }
    
    /** Parms As Fields --  */
    public boolean parms_as_fields() {
        return soot.PhaseOptions.getBoolean( options, "parms-as-fields" );
    }
    
    /** Returns As Fields --  */
    public boolean returns_as_fields() {
        return soot.PhaseOptions.getBoolean( options, "returns-as-fields" );
    }
    
    /** All Static Initializers Reachable --  */
    public boolean all_clinit() {
        return soot.PhaseOptions.getBoolean( options, "all-clinit" );
    }
    
    /** Simplify Offline --  */
    public boolean simplify_offline() {
        return soot.PhaseOptions.getBoolean( options, "simplify-offline" );
    }
    
    /** Simplify SCCs --  */
    public boolean simplify_sccs() {
        return soot.PhaseOptions.getBoolean( options, "simplify-sccs" );
    }
    
    /** Ignore Types For SCCs --  */
    public boolean ignore_types_for_sccs() {
        return soot.PhaseOptions.getBoolean( options, "ignore-types-for-sccs" );
    }
    
    /** Dump HTML --  */
    public boolean dump_html() {
        return soot.PhaseOptions.getBoolean( options, "dump-html" );
    }
    
    /** Dump PAG --  */
    public boolean dump_pag() {
        return soot.PhaseOptions.getBoolean( options, "dump-pag" );
    }
    
    /** Dump Solution --  */
    public boolean dump_solution() {
        return soot.PhaseOptions.getBoolean( options, "dump-solution" );
    }
    
    /** Topological Sort --  */
    public boolean topo_sort() {
        return soot.PhaseOptions.getBoolean( options, "topo-sort" );
    }
    
    /** Dump Types --  */
    public boolean dump_types() {
        return soot.PhaseOptions.getBoolean( options, "dump-types" );
    }
    
    /** Class Method Var --  */
    public boolean class_method_var() {
        return soot.PhaseOptions.getBoolean( options, "class-method-var" );
    }
    
    /** Dump Answer --  */
    public boolean dump_answer() {
        return soot.PhaseOptions.getBoolean( options, "dump-answer" );
    }
    
    /** Add Tags --  */
    public boolean add_tags() {
        return soot.PhaseOptions.getBoolean( options, "add-tags" );
    }
    
    /** Calculate Set Mass --  */
    public boolean set_mass() {
        return soot.PhaseOptions.getBoolean( options, "set-mass" );
    }
    
    public static final int propagator_iter = 1;
    public static final int propagator_worklist = 2;
    public static final int propagator_cycle = 3;
    public static final int propagator_merge = 4;
    public static final int propagator_alias = 5;
    public static final int propagator_none = 6;
    /** Propagator --  */
    public int propagator() {
        String s = soot.PhaseOptions.getString( options, "propagator" );
        
        if( s.equalsIgnoreCase( "iter" ) )
            return propagator_iter;
        
        if( s.equalsIgnoreCase( "worklist" ) )
            return propagator_worklist;
        
        if( s.equalsIgnoreCase( "cycle" ) )
            return propagator_cycle;
        
        if( s.equalsIgnoreCase( "merge" ) )
            return propagator_merge;
        
        if( s.equalsIgnoreCase( "alias" ) )
            return propagator_alias;
        
        if( s.equalsIgnoreCase( "none" ) )
            return propagator_none;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option propagator" );
    }
    
    public static final int set_impl_hash = 1;
    public static final int set_impl_bit = 2;
    public static final int set_impl_hybrid = 3;
    public static final int set_impl_array = 4;
    public static final int set_impl_double = 5;
    public static final int set_impl_shared = 6;
    /** Set Implementation --  */
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
        
        if( s.equalsIgnoreCase( "double" ) )
            return set_impl_double;
        
        if( s.equalsIgnoreCase( "shared" ) )
            return set_impl_shared;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option set-impl" );
    }
    
    public static final int double_set_old_hash = 1;
    public static final int double_set_old_bit = 2;
    public static final int double_set_old_hybrid = 3;
    public static final int double_set_old_array = 4;
    public static final int double_set_old_shared = 5;
    /** Double Set Old --  */
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
        
        if( s.equalsIgnoreCase( "shared" ) )
            return double_set_old_shared;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option double-set-old" );
    }
    
    public static final int double_set_new_hash = 1;
    public static final int double_set_new_bit = 2;
    public static final int double_set_new_hybrid = 3;
    public static final int double_set_new_array = 4;
    public static final int double_set_new_shared = 5;
    /** Double Set New --  */
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
        
        if( s.equalsIgnoreCase( "shared" ) )
            return double_set_new_shared;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option double-set-new" );
    }
    
}
        