
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

/** Option parser for Static Method Binder. */
public class SMBOptions
{
    private Map options;

    public SMBOptions( Map options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** Insert Null Checks --
    
     * .
    
     * Insert a check that, before invoking the static copy of the 
     * target method, throws a NullPointerException if the receiver 
     * object is null. This ensures that static method binding does 
     * not eliminate exceptions which would have occurred in its 
     * absence. 
     */
    public boolean insert_null_checks() {
        return soot.PhaseOptions.getBoolean( options, "insert-null-checks" );
    }
    
    /** Insert Redundant Casts --
    
     * .
    
     * Insert extra casts for the Java bytecode verifier. If the 
     * target method uses its this parameter, a reference to the 
     * receiver object must be passed to the static copy of the target 
     * method. The verifier may complain if the declared type of the 
     * receiver parameter does not match the type implementing the 
     * target method. Say, for example, that Singer is an interface 
     * declaring the sing() method and that the call graph shows all 
     * receiver objects at a particular call site, singer.sing() (with 
     * singer declared as a Singer) are in fact Bird objects (Bird 
     * being a class that implements Singer). The virtual call 
     * singer.sing() is effectively replaced with the static call 
     * Bird.staticsing(singer). Bird.staticsing() may perform 
     * operations on its parameter which are only allowed on Birds, 
     * rather than Singers. The Insert Redundant Casts option inserts 
     * a cast of singer to the Bird type, to prevent complaints from 
     * the verifier.
     */
    public boolean insert_redundant_casts() {
        return soot.PhaseOptions.getBoolean( options, "insert-redundant-casts" );
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
        