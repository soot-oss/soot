
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

import soot.PackManager;

public class JBOptions
{
    public static String getDeclaredOptions() {
        return ""
            +"disabled "
            +"no-splitting "
            +"no-typing "
            +"aggregate-all-locals "
            +"no-aggregating "
            +"use-original-names "
            +"pack-locals "
            +"no-cp "
            +"no-nop-elimination "
            +"no-unreachable-code-elimination "
            +"verbatim ";
    }

    public static String getDefaultOptions() {
        return "";
    }

    private Map options;

    public JBOptions( Map options ) {
        this.options = options;
    }
    
    /** Disabled --  */
    public boolean disabled() {
        return PackManager.getBoolean( options, "disabled" );
    }
    
    /** No Splitting --  */
    public boolean noSplitting() {
        return PackManager.getBoolean( options, "no-splitting" );
    }
    
    /** No Typing --  */
    public boolean noTyping() {
        return PackManager.getBoolean( options, "no-typing" );
    }
    
    /** Aggregate All Locals --  */
    public boolean aggrAllLocals() {
        return PackManager.getBoolean( options, "aggregate-all-locals" );
    }
    
    /** No Aggregating --  */
    public boolean noAggregating() {
        return PackManager.getBoolean( options, "no-aggregating" );
    }
    
    /** Use Original Names --  */
    public boolean useOrigNames() {
        return PackManager.getBoolean( options, "use-original-names" );
    }
    
    /** Pack Locals --  */
    public boolean packLocals() {
        return PackManager.getBoolean( options, "pack-locals" );
    }
    
    /** No Copy Propogator --  */
    public boolean noCp() {
        return PackManager.getBoolean( options, "no-cp" );
    }
    
    /** No Nop Elimination --  */
    public boolean noNopElim() {
        return PackManager.getBoolean( options, "no-nop-elimination" );
    }
    
    /** No Unreachable Code Elimination --  */
    public boolean noUnreachCodeElim() {
        return PackManager.getBoolean( options, "no-unreachable-code-elimination" );
    }
    
    /** Verbatim --  */
    public boolean verbatim() {
        return PackManager.getBoolean( options, "verbatim" );
    }
    
}
        
