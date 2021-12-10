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

/** Option parser for Shimple Control. */
@javax.annotation.Generated(value = "Saxonica v3.0", comments = "from soot_options.xml")
public class ShimpleOptions {

    private Map<String, String> options;

    public ShimpleOptions(Map<String, String> options) {
        this.options = options;
    }

    /**
     * Enabled
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean(options, "enabled");
    }

    /**
     * Shimple Node Elimination Optimizations --
     * Node elimination optimizations.
     *
     * Perform some optimizations, such as dead code elimination and 
     * local aggregation, before/after eliminating nodes.
     */
    public boolean node_elim_opt() {
        return soot.PhaseOptions.getBoolean(options, "node-elim-opt");
    }

    /**
     * Local Name Standardization --
     * Uses naming scheme of the Local Name Standardizer..
     *
     * If enabled, the Local Name Standardizer is applied whenever 
     * Shimple creates new locals. Normally, Shimple will retain the 
     * original local names as far as possible and use an underscore 
     * notation to denote SSA subscripts. This transformation does not 
     * otherwise affect Shimple behaviour.
     */
    public boolean standard_local_names() {
        return soot.PhaseOptions.getBoolean(options, "standard-local-names");
    }

    /**
     * Extended SSA (SSI) --
     * Compute extended SSA (SSI) form.
     *
     * If enabled, Shimple will create extended SSA (SSI) form.
     */
    public boolean extended() {
        return soot.PhaseOptions.getBoolean(options, "extended");
    }

    /**
     * Debugging Output --
     * Enables debugging output, if any.
     *
     * If enabled, Soot may print out warnings and messages useful for 
     * debugging the Shimple module. Automatically enabled by the 
     * global debug switch.
     */
    public boolean debug() {
        return soot.PhaseOptions.getBoolean(options, "debug");
    }

}
