
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
    public static String getDeclaredOptions() {
        return ""
            +"disabled "
            +"insert-null-checks "
            +"insert-redundant-casts "
            +"allow-modifier-changes "
            +"expansion-factor "
            +"max-container-size "
            +"max-inline-size "
            +"VTA-passes ";
    }

    public static String getDefaultOptions() {
        return "";
    }

    private Map options;

    public SIOptions( Map options ) {
        this.options = options;
    }
    
    /** Disabled --  */
    public boolean disabled() {
        return soot.Options.getBoolean( options, "disabled" );
    }
    
    /** Insert Null Checks --  */
    public boolean insertNullChecks() {
        return soot.Options.getBoolean( options, "insert-null-checks" );
    }
    
    /** Insert Redundant Casts --  */
    public boolean insertRedundantCasts() {
        return soot.Options.getBoolean( options, "insert-redundant-casts" );
    }
    
    /** Max Container Size --  */
    public int maxContainerSize() {
        return soot.Options.getInt( options, "max-container-size" );
    }
    
    /** Max Inline Size --  */
    public int maxInlineSize() {
        return soot.Options.getInt( options, "max-inline-size" );
    }
    
    /** VTA Passes --  */
    public int vtaPasses() {
        return soot.Options.getInt( options, "VTA-passes" );
    }
    
    /** Expansion Factor --  */
    public float expansionFactor() {
        return soot.Options.getFloat( options, "expansion-factor" );
    }
    
    public static final int allowModChanges_unsafe = 1;
    public static final int allowModChanges_safe = 2;
    public static final int allowModChanges_none = 3;
    /** Allow Modifier Changes --  */
    public int allowModChanges() {
        String s = soot.Options.getString( options, "allow-modifier-changes" );
        
        if( s.equalsIgnoreCase( "unsafe" ) )
            return allowModChanges_unsafe;
        
        if( s.equalsIgnoreCase( "safe" ) )
            return allowModChanges_safe;
        
        if( s.equalsIgnoreCase( "none" ) )
            return allowModChanges_none;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option allow-modifier-changes" );
    }
    
}
        
