
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

/** Option parser for Null Pointer Checker. */
public class NPCOptions
{
    private Map<String, String> options;

    public NPCOptions( Map<String, String> options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** Only Array Ref --
    
     * Annotate only array references.
    
     * Annotate only array-referencing instructions, instead of all 
     * instructions that need null pointer checks. 
     */
    public boolean only_array_ref() {
        return soot.PhaseOptions.getBoolean( options, "only-array-ref" );
    }
    
    /** Profiling --
    
     * Insert instructions to count safe pointer accesses.
    
     * Insert profiling instructions that at runtime count the number 
     * of eliminated safe null pointer checks. The inserted profiling 
     * code assumes the existence of a MultiCounter class implementing 
     * the methods invoked. For details, see the NullPointerChecker 
     * source code.
     */
    public boolean profiling() {
        return soot.PhaseOptions.getBoolean( options, "profiling" );
    }
    
}
        