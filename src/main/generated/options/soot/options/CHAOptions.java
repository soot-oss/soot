
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

/** Option parser for Class Hierarchy Analysis. */
public class CHAOptions
{
    private Map<String, String> options;

    public CHAOptions( Map<String, String> options ) {
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
    
     * Print statistics about the resulting call graph.
    
     * Setting this option to true causes Soot to print out statistics 
     * about the call graph computed by this phase, such as the number 
     * of methods determined to be reachable.
     */
    public boolean verbose() {
        return soot.PhaseOptions.getBoolean( options, "verbose" );
    }
    
    /** AppOnly --
    
     * Consider only application classes.
    
     * Setting this option to true causes Soot to only consider 
     * application classes when building the callgraph. The resulting 
     * callgraph will be inherently unsound. Still, this option can 
     * make sense if performance optimization and memory reduction are 
     * your primary goal.
     */
    public boolean apponly() {
        return soot.PhaseOptions.getBoolean( options, "apponly" );
    }
    
}
        