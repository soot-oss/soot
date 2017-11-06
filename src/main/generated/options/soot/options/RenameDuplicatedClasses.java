
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

/** Option parser for Rename duplicated classes. */
public class RenameDuplicatedClasses
{
    private Map<String, String> options;

    public RenameDuplicatedClasses( Map<String, String> options ) {
        this.options = options;
    }
    
    /** Enabled --
    
     * .
    
     * 
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean( options, "enabled" );
    }
    
    /** FixedClassNames --
    
     * Set for the fixed class names..
    
     * 							Use this parameter to set some class names unchangable 
     * even they are duplicated. 							The fixed class name list 
     * cannot contain duplicated class names. 							Using '-' to split 
     * multiple class names (e.g., fcn:a.b.c-a.b.d). 						
     */
    public String fixed_class_names() {
        return soot.PhaseOptions.getString( options, "fcn" );
    }
    
}
        