
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

/** Option parser for Copy Propagator. */
public class CPOptions
{
    private Map options;

    public CPOptions( Map options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** Only Regular Locals --
    
     * .
    
     * Only propagate copies through ``regular'' locals, that is, 
     * those declared in the source bytecode. 
     */
    public boolean only_regular_locals() {
        return soot.PhaseOptions.getBoolean( options, "only-regular-locals" );
    }
    
    /** Only Stack Locals --
    
     * .
    
     * Only propagate copies through locals that represent stack 
     * locations in the original bytecode. 
     */
    public boolean only_stack_locals() {
        return soot.PhaseOptions.getBoolean( options, "only-stack-locals" );
    }
    
}
        