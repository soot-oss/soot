
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

/** Option parser for Call Graph. */
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
     * statically are not assumed to call any static initializers. 
     */
    public boolean safe_forname() {
        return soot.PhaseOptions.getBoolean( options, "safe-forname" );
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
    
}
        