
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

/** Option parser for Lazy Code Motion. */
public class LCMOptions
{
    private Map options;

    public LCMOptions( Map options ) {
        this.options = options;
    }
    
    /** Disabled --  */
    public boolean disabled() {
        return soot.PackManager.getBoolean( options, "disabled" );
    }
    
    /** Unroll --  */
    public boolean unroll() {
        return soot.PackManager.getBoolean( options, "unroll" );
    }
    
    public static final int safe_safe = 1;
    public static final int safe_medium = 2;
    public static final int safe_unsafe = 3;
    /** Safe --  */
    public int safe() {
        String s = soot.PackManager.getString( options, "safe" );
        
        if( s.equalsIgnoreCase( "safe" ) )
            return safe_safe;
        
        if( s.equalsIgnoreCase( "medium" ) )
            return safe_medium;
        
        if( s.equalsIgnoreCase( "unsafe" ) )
            return safe_unsafe;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option safe" );
    }
    
}
        
