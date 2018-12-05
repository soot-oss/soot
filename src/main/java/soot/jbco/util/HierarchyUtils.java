package soot.jbco.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;

/**
 * Utility class for convenient hierarchy checks.
 *
 * @since 07.02.18
 */
public final class HierarchyUtils {

  private HierarchyUtils() {
    throw new IllegalAccessError();
  }

  /**
   * Get whole tree of interfaces on {@code Scene} for class/interface.
   *
   * @param sc
   *          class or interface to get all its interfaces
   * @return all interfaces on {@code Scene} for class or interface
   */
  public static List<SootClass> getAllInterfacesOf(SootClass sc) {
    Hierarchy hierarchy = Scene.v().getActiveHierarchy();
    Stream<SootClass> superClassInterfaces = sc.isInterface() ? Stream.empty()
        : hierarchy.getSuperclassesOf(sc).stream().map(HierarchyUtils::getAllInterfacesOf).flatMap(Collection::stream);
    Stream<SootClass> directInterfaces = Stream.concat(sc.getInterfaces().stream(),
        sc.getInterfaces().stream().map(HierarchyUtils::getAllInterfacesOf).flatMap(Collection::stream));
    return Stream.concat(superClassInterfaces, directInterfaces).collect(toList());
  }

}
