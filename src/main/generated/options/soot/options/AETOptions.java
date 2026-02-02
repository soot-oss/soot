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

/** Option parser for Available Expressions Tagger. */
@jakarta.annotation.Generated(value = "Saxonica v3.0", comments = "from soot_options.xml")
public class AETOptions {

    private Map<String, String> options;

    public AETOptions(Map<String, String> options) {
        this.options = options;
    }

    /**
     * Enabled
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean(options, "enabled");
    }

    public static final int kind_optimistic = 1;
    public static final int kind_pessimistic = 2;

    /**
     * Kind
     */
    public int kind() {
        String s = soot.PhaseOptions.getString(options, "kind");
        if (s == null || s.isEmpty())
        	return kind_optimistic;
	
        if (s.equalsIgnoreCase("optimistic"))
            return kind_optimistic;
        if (s.equalsIgnoreCase("pessimistic"))
            return kind_pessimistic;

        throw new RuntimeException(String.format("Invalid value %s of phase option kind", s));
    }

}
