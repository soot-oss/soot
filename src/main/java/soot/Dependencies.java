package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import java.util.HashSet;
import java.util.Set;

public class Dependencies {
    public final Set<Type> typesToHierarchy, typesToSignature;

    public Dependencies() {
      typesToHierarchy = new HashSet<>();
      typesToSignature = new HashSet<>();
    }

    public Dependencies(Set<Type> typesToHierarchy, Set<Type> typesToSignature) {
      this.typesToHierarchy = typesToHierarchy == null ? new HashSet<>() : typesToHierarchy;
      this.typesToSignature = typesToSignature == null ? new HashSet<>() : typesToSignature;
    }
  }