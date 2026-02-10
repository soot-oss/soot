package soot.options;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

/* THIS FILE IS AUTO-GENERATED FROM soot_options.xml. DO NOT MODIFY. */

import java.util.*;

/** Option parser for Copy Propagator. */
@jakarta.annotation.Generated(value = "Saxonica v3.0", comments = "from soot_options.xml")
public class CPOptions {

    private Map<String, String> options;

    public CPOptions(Map<String, String> options) {
        this.options = options;
    }

    /**
     * Enabled
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean(options, "enabled");
    }

    /**
     * Only Regular Locals
     * Only propagate copies through ``regular'' locals, that is, those 
     * declared in the source bytecode.
     */
    public boolean only_regular_locals() {
        return soot.PhaseOptions.getBoolean(options, "only-regular-locals");
    }

    /**
     * Only Stack Locals
     * Only propagate copies through locals that represent stack 
     * locations in the original bytecode.
     */
    public boolean only_stack_locals() {
        return soot.PhaseOptions.getBoolean(options, "only-stack-locals");
    }

}
