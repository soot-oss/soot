
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

/** Option parser for Type Assigner. */
public class JBTROptions
{
    private Map<String, String> options;

    public JBTROptions( Map<String, String> options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** Ignore wrong static-ness --
    
     * Ignores errors due to wrong staticness.
    
     * Some projects have been shown to contain invalid bytecode that 
     * tries to access a static field or method in a non-static way or 
     * the other way around. The VM's bytecode verifier will reject 
     * such bytecode when loaded into the VM. This option, when 
     * enabled, causes to create Jimple bodies in such cases 
     * nonetheless, ignoring the error. 
     */
    public boolean ignore_wrong_staticness() {
        return soot.PhaseOptions.getBoolean( options, "ignore-wrong-staticness" );
    }
    
    /** Use older type assigner --
    
     * Enables the older type assigner.
    
     * This enables the older type assigner that was in use until May 
     * 2008. The current type assigner is a reimplementation by Ben 
     * Bellamy that uses an entirely new and faster algorithm which 
     * always assigns the most narrow type possible. If 
     * compare-type-assigners is on, this option causes the older type 
     * assigner to execute first. (Otherwise the newer one is executed 
     * first.) 
     */
    public boolean use_older_type_assigner() {
        return soot.PhaseOptions.getBoolean( options, "use-older-type-assigner" );
    }
    
    /** Compare type assigners --
    
     * Compares Ben Bellamy's and the older type assigner.
    
     * Enables comparison (both runtime and results) of Ben Bellamy's 
     * type assigner with the older type assigner that was in Soot. 
     */
    public boolean compare_type_assigners() {
        return soot.PhaseOptions.getBoolean( options, "compare-type-assigners" );
    }
    
}
        
