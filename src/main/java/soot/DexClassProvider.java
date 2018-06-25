package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dexpler.DexFileProvider;
import soot.dexpler.Util;
import soot.options.Options;

/**
 * Looks for a dex file which includes the definition of a class.
 */
public class DexClassProvider implements ClassProvider {
  private static final Logger logger = LoggerFactory.getLogger(DexClassProvider.class);

  public static Set<String> classesOfDex(DexBackedDexFile dexFile) {
    Set<String> classes = new HashSet<String>();
    for (ClassDef c : dexFile.getClasses()) {
      String name = Util.dottedClassName(c.getType());
      classes.add(name);
    }
    return classes;
  }

  /**
   * Provides the DexClassSource for the class.
   *
   * @param className
   *          class to provide.
   * @return a DexClassSource that defines the className named class.
   */
  public ClassSource find(String className) {
    ensureDexIndex();

    Map<String, File> index = SourceLocator.v().dexClassIndex();
    File file = index.get(className);
    if (file == null) {
      return null;
    }

    return new DexClassSource(className, file);
  }

  /**
   * Checks whether the dex class index needs to be (re)built and triggers the build if necessary
   */
  protected void ensureDexIndex() {
    Map<String, File> index = SourceLocator.v().dexClassIndex();
    if (index == null) {
      index = new HashMap<String, File>();
      buildDexIndex(index, SourceLocator.v().classPath());
      SourceLocator.v().setDexClassIndex(index);
    }

    // Process the classpath extensions
    if (SourceLocator.v().getDexClassPathExtensions() != null) {
      buildDexIndex(SourceLocator.v().dexClassIndex(), new ArrayList<>(SourceLocator.v().getDexClassPathExtensions()));
      SourceLocator.v().clearDexClassPathExtensions();
    }
  }

  /**
   * Build index of ClassName-to-File mappings.
   *
   * @param index
   *          map to insert mappings into
   * @param classPath
   *          paths to index
   */
  private void buildDexIndex(Map<String, File> index, List<String> classPath) {
    for (String path : classPath) {
      try {
        File dexFile = new File(path);
        if (dexFile.exists()) {
          for (DexFileProvider.DexContainer container : DexFileProvider.v().getDexFromSource(dexFile)) {
            for (String className : classesOfDex(container.getBase())) {
              if (!index.containsKey(className)) {
                index.put(className, container.getFilePath());
              } else if (Options.v().verbose()) {
                logger.debug("" + String.format(
                    "Warning: Duplicate of class '%s' found in dex file '%s' from source '%s'. Omitting class.", className,
                    container.getDexName(), container.getFilePath().getCanonicalPath()));
              }
            }
          }
        }
      } catch (IOException e) {
        logger.warn("IO error while processing dex file '" + path + "'");
        logger.debug("Exception: " + e);
      } catch (Exception e) {
        logger.warn("exception while processing dex file '" + path + "'");
        logger.debug("Exception: " + e);
      }
    }

  }
}
