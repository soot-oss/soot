
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

/** Option parser for Shimple Body Options. */
public class ShimpleOptions
{
    private Map options;

    public ShimpleOptions( Map options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** Naive Phi Node Elimination --
    
     * If enabled, no pre- or post- optimizations are 
     * applied before eliminating Phi nodes..
    
     * If set to true, neither pre-optimization nor 
     * post-optimization will be applied to the Phi elimination 
     * process. This is useful for monitoring and understanding 
     * the behaviour of Shimple optimizations or transformations. 
     * Note that setting this option to true takes precedence 
     * over other Phi elimination options.
     */
    public boolean naive_phi_elimination() {
        return soot.PhaseOptions.getBoolean( options, "naive-phi-elimination" );
    }
    
    /** Pre-optimize Phi Elimination --
    
     * If enabled, some optimizations are applied before 
     * Phi nodes are eliminated..
    
     * If set to true, some recommended optimizations such as 
     * dead code elimination and aggregation are applied 
     * before Phi node elimination. This does not appear to be as 
     * effective as post-optimization, but the option is provided 
     * for future testing and investigation.
     */
    public boolean pre_optimize_phi_elimination() {
        return soot.PhaseOptions.getBoolean( options, "pre-optimize-phi-elimination" );
    }
    
    /** Post-optimize Phi Elimination --
    
     * If enabled, some optimizations are applied after Phi 
     * nodes are eliminated..
    
     * Applies recommended optimizations such as dead code 
     * elimination and aggregation after Phi node elimination. 
     * This appears to be more effective than 
     * pre-optimization.
     */
    public boolean post_optimize_phi_elimination() {
        return soot.PhaseOptions.getBoolean( options, "post-optimize-phi-elimination" );
    }
    
}
        