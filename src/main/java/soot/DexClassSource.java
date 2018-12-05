package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2011 - 2012 Michael Markert
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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dexpler.DexResolver;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;

/**
 * Responsible for resolving a single class from a dex source format.
 */
public class DexClassSource extends ClassSource {
  private static final Logger logger = LoggerFactory.getLogger(DexClassSource.class);
  protected File path;

  /**
   * @param className
   *          the class which dependencies are to be resolved.
   * @param path
   *          to the file that defines the class.
   */
  public DexClassSource(String className, File path) {
    super(className);
    this.path = path;
  }

  /**
   * Resolve dependencies of class.
   *
   * @param sc
   *          The SootClass to resolve into.
   * @return Dependencies of class (Strings or Types referenced).
   */
  public Dependencies resolve(SootClass sc) {
    if (Options.v().verbose()) {
      logger.debug("resolving " + className + " from file " + path.getPath());
    }
    return DexResolver.v().resolveFromFile(path, className, sc);
  }
}
