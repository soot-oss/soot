
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

/** Option parser for Array Bound Check Options. */
public class ABCOptions
{
    private Map options;

    public ABCOptions( Map options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** With All --
    
     * .
    
     * A macro. Instead of typing a long string of phase options, 
     * this option will turn on all options of the phase ``jap.abc''. 
     * 
     */
    public boolean with_all() {
        return soot.PhaseOptions.getBoolean( options, "with-all" );
    }
    
    /** With Field References --
    
     * .
    
     * The analysis treats field references (static and instance) as 
     * common subexpressions. The restrictions from the `with-arrayref' 
     * option also apply. 
     */
    public boolean with_fieldref() {
        return soot.PhaseOptions.getBoolean( options, "with-fieldref" );
    }
    
    /** With Array References --
    
     * .
    
     * With this option enabled, array references can be considered as 
     * common subexpressions; however, we are more conservative when 
     * writing into an array, because array objects may be aliased. 
     * NOTE: We also assume that the application in a single-threaded 
     * program or in a synchronized block. That is, an array element 
     * may not be changed by other threads between two array 
     * references. 
     */
    public boolean with_arrayref() {
        return soot.PhaseOptions.getBoolean( options, "with-arrayref" );
    }
    
    /** With Common Sub-expressions --
    
     * .
    
     * The analysis will consider common subexpressions. For example, 
     * consider the situation where r1 is assigned a*b; later, r2 
     * is assigned a*b, where both a and b have not been changed 
     * between the two statements. The analysis can conclude that r2 
     * has the same value as r1. Experiments show that this option 
     * can improve the result slightly. 
     */
    public boolean with_cse() {
        return soot.PhaseOptions.getBoolean( options, "with-cse" );
    }
    
    /** With Class Field --
    
     * .
    
     * This option makes the analysis work on the class level. The 
     * algorithm analyzes `final' or `private' class fields first. It 
     * can recognize the fields that hold array objects with constant 
     * length. In an application using lots of array fields, this 
     * option can improve the analysis results dramatically. 
     * 
     */
    public boolean with_classfield() {
        return soot.PhaseOptions.getBoolean( options, "with-classfield" );
    }
    
    /** With Rectangular Array --
    
     * .
    
     * This option is used together with "wjap.ra" to make Soot run 
     * the whole-program analysis for rectangular array objects. This 
     * analysis is based on the call graph, and it usually takes a long 
     * time. If the application uses rectangular arrays, these options 
     * can improve the analysis result. 
     */
    public boolean with_rectarray() {
        return soot.PhaseOptions.getBoolean( options, "with-rectarray" );
    }
    
    /** Profiling --
    
     * Profile the results of array bounds check analysis..
    
     * 
     */
    public boolean profiling() {
        return soot.PhaseOptions.getBoolean( options, "profiling" );
    }
    
}
        