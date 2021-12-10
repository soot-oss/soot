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
public class JavaClassProvider implements ClassProvider {

  public static class JarException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JarException(String className) {
      super("Class " + className
          + " was found in an archive, but Soot doesn't support reading source files out of an archive");
    }
  }

  /**
   * Look for the specified class. Return a ClassSource for it if found, or null if it was not found.
   */
  @Override
  public ClassSource find(String className) {
    if (Options.v().polyglot() && soot.javaToJimple.InitialResolver.v().hasASTForSootName(className)) {
      soot.javaToJimple.InitialResolver.v().setASTForSootName(className);
      return new JavaClassSource(className);
    } else { // jastAdd; or polyglot AST not yet produced
      /*
       * 04.04.2006 mbatch if there is a $ in the name, we need to check if it's a real file, not just inner class
       */
      boolean checkAgain = className.indexOf('$') >= 0;

      FoundFile file = null;
      try {
        final SourceLocator loc = SourceLocator.v();
        String javaClassName = loc.getSourceForClass(className);
        file = loc.lookupInClassPath(javaClassName.replace('.', '/') + ".java");

        /*
         * 04.04.2006 mbatch if inner class not found, check if it's a real file
         */
        if (file == null && checkAgain) {
          file = loc.lookupInClassPath(className.replace('.', '/') + ".java");
        }
        /* 04.04.2006 mbatch end */

        if (file == null) {
          return null;
        }

        if (file.isZipFile()) {
          throw new JarException(className);
        }
        return new JavaClassSource(className, file.getFile());
      } finally {
        if (file != null) {
          file.close();
        }
      }
    }
  }
}
