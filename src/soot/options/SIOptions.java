
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

/** Option parser for Static Inlining. */
public class SIOptions
{
    private Map options;

    public SIOptions( Map options ) {
        this.options = options;
    }
    
    /** Enabled --  */
    public boolean enabled() {
        return soot.PackManager.getBoolean( options, "enabled" );
    }
    
    /** Insert Null Checks --  */
    public boolean insert_null_checks() {
        return soot.PackManager.getBoolean( options, "insert-null-checks" );
    }
    
    /** Insert Redundant Casts --  */
    public boolean insert_redundant_casts() {
        return soot.PackManager.getBoolean( options, "insert-redundant-casts" );
    }
    
    /** Max Container Size --  */
    public int max_container_size() {
        return soot.PackManager.getInt( options, "max-container-size" );
    }
    
    /** Max Inline Size --  */
    public int max_inlinee_size() {
        return soot.PackManager.getInt( options, "max-inlinee-size" );
    }
    
    /** Expansion Factor --  */
    public float expansion_factor() {
        return soot.PackManager.getFloat( options, "expansion-factor" );
    }
    
    public static final int allowed_modifier_changes_unsafe = 1;
    public static final int allowed_modifier_changes_safe = 2;
    public static final int allowed_modifier_changes_none = 3;
    /** Allow Modifier Changes --  */
    public int allowed_modifier_changes() {
        String s = soot.PackManager.getString( options, "allowed-modifier-changes" );
        
        if( s.equalsIgnoreCase( "unsafe" ) )
            return allowed_modifier_changes_unsafe;
        
        if( s.equalsIgnoreCase( "safe" ) )
            return allowed_modifier_changes_safe;
        
        if( s.equalsIgnoreCase( "none" ) )
            return allowed_modifier_changes_none;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option allowed-modifier-changes" );
    }
    
}
        