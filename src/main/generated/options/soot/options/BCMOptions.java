
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

/** Option parser for Busy Code Motion. */
public class BCMOptions
{
    private Map<String, String> options;

    public BCMOptions( Map<String, String> options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** Naive Side Effect Tester --
    
     * Use a naive side effect analysis even if interprocedural 
     * information is available.
    
     * If Naive Side Effect Tester is set to true, Busy Code Motion 
     * uses the conservative side effect information provided by the 
     * NaiveSideEffectTester class, even if interprocedural information 
     * about side effects is available. The naive side effect analysis 
     * is based solely on the information available locally about a 
     * statement. It assumes, for example, that any method call has the 
     * potential to write and read all instance and static fields in 
     * the program. If Naive Side Effect Tester is set to false and 
     * Soot is in whole program mode, then Busy Code Motion uses the 
     * side effect information provided by the PASideEffectTester 
     * class. PASideEffectTester uses a points-to analysis to determine 
     * which fields and statics may be written or read by a given 
     * statement. If whole program analysis is not performed, naive 
     * side effect information is used regardless of the setting of 
     * Naive Side Effect Tester. 
     */
    public boolean naive_side_effect() {
        return soot.PhaseOptions.getBoolean( options, "naive-side-effect" );
    }
    
}
        