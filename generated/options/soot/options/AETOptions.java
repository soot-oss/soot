
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

/** Option parser for Available Expressions Tagger. */
public class AETOptions
{
    private Map options;

    public AETOptions( Map options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    public static final int kind_optimistic = 1;
    public static final int kind_pessimistic = 2;
    /** Kind --
    
     * .
    
     * 
     */
    public int kind() {
        String s = soot.PhaseOptions.getString( options, "kind" );
        
        if( s.equalsIgnoreCase( "optimistic" ) )
            return kind_optimistic;
        
        if( s.equalsIgnoreCase( "pessimistic" ) )
            return kind_pessimistic;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option kind" );
    }
    
}
        