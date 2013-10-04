
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

/** Option parser for Static Inliner. */
public class SIOptions
{
    private Map<String, String> options;

    public SIOptions( Map<String, String> options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** Reconstruct Jimple body after inlining --
    
     * .
    
     * When a method with array parameters is inlined, its variables 
     * may need to be assigned different types than they had in the 
     * original method to produce compilable code. When this option is 
     * set, Soot re-runs the Jimple Body pack on each method body which 
     * has had another method inlined into it so that the typing 
     * algorithm can reassign the types. 
     */
    public boolean rerun_jb() {
        return soot.PhaseOptions.getBoolean( options, "rerun-jb" );
    }
    
    /** Insert Null Checks --
    
     * .
    
     * Insert, before the inlined body of the target method, a check 
     * that throws a NullPointerException if the receiver object is 
     * null. This ensures that inlining will not eliminate exceptions 
     * which would have occurred in its absence. 
     */
    public boolean insert_null_checks() {
        return soot.PhaseOptions.getBoolean( options, "insert-null-checks" );
    }
    
    /** Insert Redundant Casts --
    
     * .
    
     * Insert extra casts for the Java bytecode verifier. The 
     * verifier may complain if the inlined method uses this and the 
     * declared type of the receiver of the call being inlined is 
     * different from the type implementing the target method being 
     * inlined. Say, for example, that Singer is an interface declaring 
     * the sing() method and that the call graph shows that all 
     * receiver objects at a particular call site, singer.sing() (with 
     * singer declared as a Singer) are in fact Bird objects (Bird 
     * being a class that implements Singer). The implementation of 
     * Bird.sing() may perform operations on this which are only 
     * allowed on Birds, rather than Singers. The Insert Redundant 
     * Casts option ensures that this cannot lead to verification 
     * errors, by inserting a cast of bird to the Bird type before 
     * inlining the body of Bird.sing().
     */
    public boolean insert_redundant_casts() {
        return soot.PhaseOptions.getBoolean( options, "insert-redundant-casts" );
    }
    
    /** Max Container Size --
    
     * .
    
     * Determines the maximum number of Jimple statements for a 
     * container method. If a method has more than this number of 
     * Jimple statements, then no methods will be inlined into it. 
     * 
     */
    public int max_container_size() {
        return soot.PhaseOptions.getInt( options, "max-container-size" );
    }
    
    /** Max Inlinee Size --
    
     * .
    
     * Determines the maximum number of Jimple statements for an 
     * inlinee method. If a method has more than this number of Jimple 
     * statements, then it will not be inlined into other methods. 
     * 
     */
    public int max_inlinee_size() {
        return soot.PhaseOptions.getInt( options, "max-inlinee-size" );
    }
    
    /** Expansion Factor --
    
     * .
    
     * Determines the maximum allowed expansion of a method. Inlining 
     * will cause the method to grow by a factor of no more than the 
     * Expansion Factor. 
     */
    public float expansion_factor() {
        return soot.PhaseOptions.getFloat( options, "expansion-factor" );
    }
    
    public static final int allowed_modifier_changes_unsafe = 1;
    public static final int allowed_modifier_changes_safe = 2;
    public static final int allowed_modifier_changes_none = 3;
    /** Allowed Modifier Changes --
    
     * .
    
     * Specify which changes in visibility modifiers are allowed. 
     */
    public int allowed_modifier_changes() {
        String s = soot.PhaseOptions.getString( options, "allowed-modifier-changes" );
        
        if( s.equalsIgnoreCase( "unsafe" ) )
            return allowed_modifier_changes_unsafe;
        
        if( s.equalsIgnoreCase( "safe" ) )
            return allowed_modifier_changes_safe;
        
        if( s.equalsIgnoreCase( "none" ) )
            return allowed_modifier_changes_none;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option allowed-modifier-changes" );
    }
    
}
        