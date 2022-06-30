package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ondrej Lhotak
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

import soot.options.Options;

/**
 * A class provider looks for a file of a specific format for a specified class, and returns a ClassSource for it if it finds
 * it.
 */
public class JimpleClassProvider implements ClassProvider {

  /**
   * Look for the specified class. Return a ClassSource for it if found, or null if it was not found.
   */
  @Override
  public ClassSource find(String className) {
    IFoundFile file = SourceLocator.v().lookupInClassPath(className + ".jimple");
    if (file == null) {
      if (Options.v().permissive_resolving()) {
        file = SourceLocator.v().lookupInClassPath(className.replace('.', '/') + ".jimple");
      }
      if (file == null) {
        return null;
      }
    }
    return new JimpleClassSource(className, file);
  }
}
