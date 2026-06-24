package soot.tagkit;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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
public class PermittedSubclassesTag implements Tag {

  public static final String NAME = "PermittedSubclassesTag";

  private final Set<String> subClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

  /**
   * Adds a permitted sub class
   * 
   * @param subclass
   *          the subclass
   * @return true if the sub class was added successfully, otherwise false
   */
  public boolean addPermittedSubClass(String subclass) {
    return this.subClasses.add(subclass);
  }

  /**
   * Returns all permitted subclasses
   * 
   * @return all permitted subclasses
   */
  public Set<String> getPermittedSubClasses() {
    return this.subClasses;
  }

  @Override
  public String toString() {
    return "Permitted subclasses: " + subClasses;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
