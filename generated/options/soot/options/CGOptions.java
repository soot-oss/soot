
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

/** Option parser for Call Graph Constructor. */
public class CGOptions
{
    private Map options;

    public CGOptions( Map options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** Safe forName --
    
     * Handle Class.forName() calls conservatively.
    
     * When a program calls Class.forName(), the named class is 
     * resolved, and its static initializer executed. In many cases, it 
     * cannot be determined statically which class will be loaded, and 
     * which static initializer executed. When this option is set to 
     * true, Soot will conservatively assume that any static 
     * initializer could be executed. This may make the call graph very 
     * large. When this option is set to false, any calls to 
     * Class.forName() for which the class cannot be determined 
     * statically are assumed to call no static initializers. 
     */
    public boolean safe_forname() {
        return soot.PhaseOptions.getBoolean( options, "safe-forname" );
    }
    
    /** Safe newInstance --
    
     * Handle Class.newInstance() calls conservatively.
    
     * When a program calls Class.newInstance(), a new object is 
     * created and its constructor executed. Soot does not determine 
     * statically which type of object will be created, and which 
     * constructor executed. When this option is set to true, Soot will 
     * conservatively assume that any constructor could be executed. 
     * This may make the call graph very large. When this option is set 
     * to false, any calls to Class.newInstance() are assumed not to 
     * call the constructor of the created object. 
     */
    public boolean safe_newinstance() {
        return soot.PhaseOptions.getBoolean( options, "safe-newinstance" );
    }
    
    /** Verbose --
    
     * Print warnings about where the call graph may be incomplete.
    
     * Due to the effects of native methods and reflection, it may not 
     * always be possible to construct a fully conservative call graph. 
     * Setting this option to true causes Soot to point out the parts 
     * of the call graph that may be incomplete, so that they can be 
     * checked by hand. 
     */
    public boolean verbose() {
        return soot.PhaseOptions.getBoolean( options, "verbose" );
    }
    
    /** All Application Class Methods Reachable --
    
     * Assume all methods of application classes are reachable..
    
     * When this option is false, the call graph is built starting at a 
     * set of entry points, and only methods reachable from those entry 
     * points are processed. Unreachable methods will not have any call 
     * graph edges generated out of them. Setting this option to true 
     * makes Soot consider all methods of application classes to be 
     * reachable, so call edges are generated for all of them. This 
     * leads to a larger call graph. For program visualization 
     * purposes, it is sometimes desirable to include edges from 
     * unreachable methods; although these methods are unreachable in 
     * the version being analyzed, they may become reachable if the 
     * program is modified.
     */
    public boolean all_reachable() {
        return soot.PhaseOptions.getBoolean( options, "all-reachable" );
    }
    
    /** Trim Static Initializer Edges --
    
     * Removes redundant static initializer calls.
    
     * The call graph contains an edge from each statement that could 
     * trigger execution of a static initializer to that static 
     * initializer. However, each static initializer is triggered only 
     * once. When this option is enabled, after the call graph is 
     * built, an intra-procedural analysis is performed to detect 
     * static initializer edges leading to methods that must have 
     * already been executed. Since these static initializers cannot be 
     * executed again, the corresponding call graph edges are removed 
     * from the call graph. 
     */
    public boolean trim_clinit() {
        return soot.PhaseOptions.getBoolean( options, "trim-clinit" );
    }
    
    public static final int context_insens = 1;
    public static final int context_1cfa = 2;
    public static final int context_objsens = 3;
    /** Context sensitivity --
    
     * Select context-sensitivity level.
    
     * This option tells Paddle which level of context-sensitivity to 
     * use in constructing the call graph. 
     */
    public int context() {
        String s = soot.PhaseOptions.getString( options, "context" );
        
        if( s.equalsIgnoreCase( "insens" ) )
            return context_insens;
        
        if( s.equalsIgnoreCase( "1cfa" ) )
            return context_1cfa;
        
        if( s.equalsIgnoreCase( "objsens" ) )
            return context_objsens;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option context" );
    }
    
}
        