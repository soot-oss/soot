
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

/** Option parser for Jimple Body Creation. */
public class JBOptions
{
    private Map<String, String> options;

    public JBOptions( Map<String, String> options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** Use Original Names --
    
     * .
    
     * Retain the original names for local variables when the source 
     * includes those names. Otherwise, Soot gives variables generic 
     * names based on their types. 
     */
    public boolean use_original_names() {
        return soot.PhaseOptions.getBoolean( options, "use-original-names" );
    }
    
    /** Preserve source-level annotations --
    
     * .
    
     * Preserves annotations of retention type SOURCE. (for everything 
     * but package and local variable annotations) 
     */
    public boolean preserve_source_annotations() {
        return soot.PhaseOptions.getBoolean( options, "preserve-source-annotations" );
    }
    
}
        