
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

/** Option parser for Array Bound Checker. */
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
    
     * Setting the With All option to true is equivalent to setting 
     * each of With CSE, With Array Ref, With Field Ref, With Class 
     * Field, and With Rectangular Array to true.
     */
    public boolean with_all() {
        return soot.PhaseOptions.getBoolean( options, "with-all" );
    }
    
    /** With Common Sub-expressions --
    
     * .
    
     * The analysis will consider common subexpressions. For example, 
     * consider the situation where r1 is assigned a*b; later, r2 is 
     * assigned a*b, where neither a nor b have changed between the two 
     * statements. The analysis can conclude that r2 has the same value 
     * as r1. Experiments show that this option can improve the result 
     * slightly.
     */
    public boolean with_cse() {
        return soot.PhaseOptions.getBoolean( options, "with-cse" );
    }
    
    /** With Array References --
    
     * .
    
     * With this option enabled, array references can be considered as 
     * common subexpressions; however, we are more conservative when 
     * writing into an array, because array objects may be aliased. We 
     * also assume that the application is single-threaded or that the 
     * array references occur in a synchronized block. That is, we 
     * assume that an array element may not be changed by other threads 
     * between two array references.
     */
    public boolean with_arrayref() {
        return soot.PhaseOptions.getBoolean( options, "with-arrayref" );
    }
    
    /** With Field References --
    
     * .
    
     * The analysis treats field references (static and instance) as 
     * common subexpressions; however, we are more conservative when 
     * writing to a field, because the base of the field reference may 
     * be aliased. We also assume that the application is 
     * single-threaded or that the field references occur in a 
     * synchronized block. That is, we assume that a field may not be 
     * changed by other threads between two field references.
     */
    public boolean with_fieldref() {
        return soot.PhaseOptions.getBoolean( options, "with-fieldref" );
    }
    
    /** With Class Field --
    
     * .
    
     * This option makes the analysis work on the class level. The 
     * algorithm analyzes final or private class fields first. It can 
     * recognize the fields that hold array objects of constant length. 
     * In an application using lots of array fields, this option can 
     * improve the analysis results dramatically.
     */
    public boolean with_classfield() {
        return soot.PhaseOptions.getBoolean( options, "with-classfield" );
    }
    
    /** With Rectangular Array --
    
     * .
    
     * This option is used together with wjap.ra to make Soot run the 
     * whole-program analysis for rectangular array objects. This 
     * analysis is based on the call graph, and it usually takes a long 
     * time. If the application uses rectangular arrays, these options 
     * can improve the analysis result. 
     */
    public boolean with_rectarray() {
        return soot.PhaseOptions.getBoolean( options, "with-rectarray" );
    }
    
    /** Profiling --
    
     * Profile the results of array bounds check analysis..
    
     * Profile the results of array bounds check analysis. The 
     * inserted profiling code assumes the existence of a MultiCounter 
     * class implementing the methods invoked. For details, see the 
     * ArrayBoundsChecker source code.
     */
    public boolean profiling() {
        return soot.PhaseOptions.getBoolean( options, "profiling" );
    }
    
    /** Add Color Tags --
    
     * Add color tags to results of array bound check analysis..
    
     * Add color tags to the results of the array bounds check 
     * analysis.
     */
    public boolean add_color_tags() {
        return soot.PhaseOptions.getBoolean( options, "add-color-tags" );
    }
    
}
        