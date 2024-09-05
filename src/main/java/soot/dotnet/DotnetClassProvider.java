package soot.dotnet;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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

import com.google.common.base.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ClassProvider;
import soot.ClassSource;
import soot.SourceLocator;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.options.Options;

/**
 * This ClassProvider provides the assembly files with their types as ClassSource SourceLocator -> ClassProvider ->
 * ClassSource -> MethodSource
 */
public class DotnetClassProvider implements ClassProvider {
  private static final Logger logger = LoggerFactory.getLogger(DotnetClassProvider.class);

  /**
   * Return the ClassSource of requested class
   *
   * @param className
   *          requested class
   * @return class source of the class
   */
  @Override
  public ClassSource find(String className) {
    ensureAssemblyIndex();

    File assemblyFile = SourceLocator.v().dexClassIndex().get(className);
    return assemblyFile == null ? null : new DotnetClassSource(className, assemblyFile);
  }

  /**
   * Generate index of all assembly files with their types. An assembly file contains several types in one file
   */
  public static void ensureAssemblyIndex() {
    Map<String, File> index = SourceLocator.v().dexClassIndex();
    if (index == null) {
      if (Options.v().verbose()) {
        logger.info("Creating assembly index");
      }
      index = new HashMap<>();
      buildAssemblyIndex(index, SourceLocator.v().classPath());
      SourceLocator.v().setDexClassIndex(index);
      if (Options.v().verbose()) {
        logger.info("Created assembly index");
      }
    }

    // Process the classpath extensions
    if (SourceLocator.v().getDexClassPathExtensions() != null) {
      if (Options.v().verbose()) {
        logger.info("Process classpath extensions");
      }
      buildAssemblyIndex(index, new ArrayList<>(SourceLocator.v().getDexClassPathExtensions()));
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
  private static void buildAssemblyIndex(Map<String, File> index, List<String> classPath) {
    if (Strings.isNullOrEmpty(Options.v().dotnet_nativehost_path())) {
      throw new RuntimeException("Dotnet NativeHost Path is not set! Use -dotnet-nativehost-path Soot parameter!");
    }

    for (String path : classPath) {
      try {
        File file = new File(path);
        if (file.exists()) {
          // if classpath is only directory, look for dll/exe inside dir to add to index - only one hierarchical step
          File[] listFiles = file.isDirectory() ? file.listFiles(File::isFile) : new File[] { file };
          for (File f : Objects.requireNonNull(listFiles)) {
            if (Options.v().verbose()) {
              logger.info("Process " + f.getCanonicalPath() + " file");
            }
            // Check if given assembly is dll or exe and is assembly
            if (!f.getCanonicalPath().endsWith(".exe") && !f.getCanonicalPath().endsWith(".dll")) {
              continue;
            }
            AssemblyFile assemblyFile = new AssemblyFile(f.getCanonicalPath());
            if (!assemblyFile.isAssembly()) {
              continue;
            }

            // Get all classes of given assembly
            ProtoAssemblyAllTypes.AssemblyAllTypes assemblyDefinition = assemblyFile.getAllTypes();
            if (assemblyDefinition == null) {
              continue;
            }
            // save later computation and calls of nativehost
            if (!index.containsKey(f.getCanonicalPath())) {
              index.put(f.getCanonicalPath(), assemblyFile);
            }
            List<ProtoAssemblyAllTypes.TypeDefinition> allTypesOfMainModule = assemblyDefinition.getListOfTypesList();
            for (ProtoAssemblyAllTypes.TypeDefinition type : allTypesOfMainModule) {
              String typeName = type.getFullname();
              if (Options.v().verbose()) {
                logger.info("Add class " + typeName + " to index");
              }

              if (!index.containsKey(typeName)) {
                index.put(typeName, assemblyFile);
              } else if (Options.v().verbose()) {
                logger.debug("" + String.format(
                    "Warning: Duplicate of class '%s' found in assembly file '%s' from source '%s'. Omitting class.", type,
                    assemblyFile.getAssemblyFileName(), assemblyFile.getFullPath()));
              }
            }
          }
        }
      } catch (Exception e) {
        logger.warn("exception while processing assembly file '" + path + "'");
        logger.warn("Exception: " + e);
      }
    }

  }
}
