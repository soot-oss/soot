
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

/** Option parser for Null Pointer Check Options. */
public class NPCOptions
{
    private Map options;

    public NPCOptions( Map options ) {
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
    
     * .
    
     * By default, all bytecodes that need null pointer checks 
     * are annotated with the analysis result. When this option is 
     * set to true, Soot will annotate only array-referencing 
     * bytecodes with null pointer check information; other bytecodes, 
     * such as getfield and putfield, will not be annotated. 
     * 
     */
    public boolean only_array_ref() {
        return soot.PhaseOptions.getBoolean( options, "only-array-ref" );
    }
    
    /** Profiling --
    
     * Insert profiling instructions counting the number of safe null 
     * pointer accesses..
    
     * If this option is true, the analysis inserts profiling 
     * instructions counting the number of eliminated safe null pointer 
     * checks at runtime. This is only for profiling purpose. 						
     */
    public boolean profiling() {
        return soot.PhaseOptions.getBoolean( options, "profiling" );
    }
    
}
        