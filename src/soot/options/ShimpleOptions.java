
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

/** Option parser for Shimple Control. */
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
    
    /** Local Name Standardization --
    
     * Uses naming scheme of the Local Name Standardizer..
    
     * If enabled, the Local Name Standardizer is applied 
     * after Shimple creates new locals. Normally, Shimple 
     * will retain the original local names as far as 
     * possible and use an underscore notation to denote 
     * SSA subscripts. This transformation does not 
     * otherwise affect Shimple behaviour. 
     */
    public boolean standard_local_names() {
        return soot.PhaseOptions.getBoolean( options, "standard-local-names" );
    }
    
    public static final int phi_elim_opt_none = 1;
    public static final int phi_elim_opt_pre = 2;
    public static final int phi_elim_opt_post = 3;
    public static final int phi_elim_opt_pre_and_post = 4;
    /** Phi Node Elimination Optimizations --
    
     * Phi node elimination optimizations.
    
     * 
     */
    public int phi_elim_opt() {
        String s = soot.PhaseOptions.getString( options, "phi-elim-opt" );
        
        if( s.equalsIgnoreCase( "none" ) )
            return phi_elim_opt_none;
        
        if( s.equalsIgnoreCase( "pre" ) )
            return phi_elim_opt_pre;
        
        if( s.equalsIgnoreCase( "post" ) )
            return phi_elim_opt_post;
        
        if( s.equalsIgnoreCase( "pre-and-post" ) )
            return phi_elim_opt_pre_and_post;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option phi-elim-opt" );
    }
    
}
        