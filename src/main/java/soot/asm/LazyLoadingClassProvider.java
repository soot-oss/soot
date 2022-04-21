package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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

import static com.google.common.cache.CacheBuilder.newBuilder;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.ClassProvider;
import soot.ClassSource;
import soot.options.Options;

public class LazyLoadingClassProvider implements ClassProvider {
  private static final Logger logger = LoggerFactory.getLogger(LazyLoadingClassProvider.class);

  private final Map<String, PathEntry> classToPath = new HashMap<>();
  private final LoadingCache<PathEntry, Map<String, ArrayAsmClassSource>> pathCache = newBuilder().initialCapacity(30)
      .maximumSize(30).concurrencyLevel(Runtime.getRuntime().availableProcessors())
      .build(new CacheLoader<PathEntry, Map<String, ArrayAsmClassSource>>() {
        @SuppressWarnings("NullableProblems")
        @Override
        public Map<String, ArrayAsmClassSource> load(PathEntry path) {
          return loadPath(path);
        }
      });

  public LazyLoadingClassProvider(List<String> classPath) {
    try {
      for (String path : classPath) {
        mapClasses(path);
      }
      logger.info("Loaded: {} path entries, {} classes", classPath.size(), classToPath.size());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ClassSource find(String cls) {
    PathEntry pathEntry = classToPath.get(cls);
    if (pathEntry == null) {
      return null;
    }
    try {
      return pathCache.get(pathEntry).get(cls);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private void mapClasses(String path) {
    File f = new File(path);
    if (!f.exists() && !Options.v().ignore_classpath_errors()) {
      throw new RuntimeException("Error: The path '" + path + "' does not exist.");
    }
    if (!f.canRead() && !Options.v().ignore_classpath_errors()) {
      throw new RuntimeException("Error: The path '" + path + "' exists but is not readable.");
    }
    if (f.isFile()) {
      if (path.endsWith(".zip") || path.endsWith(".jar")) {
        mapArchive(path);
      } else {
        throw new RuntimeException("Cannot define type for " + path);
      }
    } else {
      mapDir(path);
    }
  }

  private void mapDir(String path) {
    String canonicalPath = canonical(path);
    PathEntry pathEntry = new FolderPathEntry(canonicalPath);
    try {
      String prefix = canonicalPath + "/";
      Files.walk(Paths.get(canonicalPath))
          .filter(Files::isRegularFile)
          .filter(file -> file.toString().endsWith(".class"))
          .forEach(file -> {
            String canonicalFileName = removePrefix(canonical(file.toString()), prefix);
            String className = className(canonicalFileName);
            classToPath.put(className, pathEntry);
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Map<String, ArrayAsmClassSource> loadDir(String path) {
    String canonicalPath = canonical(path);
    PathEntry pathEntry = new FolderPathEntry(canonicalPath);
    Map<String, ArrayAsmClassSource> cacheEntry = new HashMap<>();
    try {
      String prefix = canonicalPath + "/";
      Files.walk(Paths.get(canonicalPath))
          .filter(Files::isRegularFile)
          .filter(file -> file.toString().endsWith(".class"))
          .forEach(file -> {
            String canonicalFileName = removePrefix(canonical(file.toString()), prefix);
            String className = className(canonicalFileName);
            try {
              byte[] bytes = Files.readAllBytes(file);
              classToPath.put(className, pathEntry);
              cacheEntry.put(className, new ArrayAsmClassSource(className, bytes));
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return cacheEntry;
  }

  private void mapArchive(String path) {
    PathEntry pathEntry = new ArchivePathEntry(path);
    try (ZipFile archive = new ZipFile(path)) {
      Enumeration<? extends ZipEntry> it = archive.entries();
      while (it.hasMoreElements()) {
        ZipEntry entry = it.nextElement();
        if (entry.getName().endsWith(".class")) {
          String className = className(entry.getName());
          classToPath.put(className, pathEntry);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Map<String, ArrayAsmClassSource> loadPath(PathEntry path) {
    if (path instanceof ArchivePathEntry) {
      ArchivePathEntry archive = (ArchivePathEntry) path;
      return loadArchive(archive.getPath());
    } else if (path instanceof FolderPathEntry) {
      FolderPathEntry folder = (FolderPathEntry) path;
      return loadDir(folder.getPath());
    } else {
      throw new RuntimeException("Not supported");
    }
  }

  private Map<String, ArrayAsmClassSource> loadArchive(String path) {
    Map<String, ArrayAsmClassSource> cacheEntry = new HashMap<>();
    try (ZipFile archive = new ZipFile(path)) {
      Enumeration<? extends ZipEntry> it = archive.entries();
      while (it.hasMoreElements()) {
        ZipEntry entry = it.nextElement();
        if (entry.getName().endsWith(".class")) {
          String className = className(entry.getName());
          try (InputStream is = archive.getInputStream(entry)) {
            byte[] bytes = readFully(is);
            cacheEntry.put(className, new ArrayAsmClassSource(className, bytes));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return cacheEntry;
  }

  private static String canonical(String path) {
    return path.replace('\\', '/');
  }

  private static String className(String fileName) {
    return removeSuffix(fileName, ".class").replace('/', '.');
  }

  private static byte[] readFully(final InputStream is) throws IOException {
    final byte[] buf = new byte[1024];
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    while (true) {
      final int r = is.read(buf);
      if (r == -1) {
        break;
      }
      out.write(buf, 0, r);
    }
    return out.toByteArray();
  }

  @SuppressWarnings("SameParameterValue")
  private static String removeSuffix(String string, String suffix) {
    return string.endsWith(suffix) ? string.substring(0, string.length() - suffix.length()) : string;
  }

  private static String removePrefix(String string, String prefix) {
    return string.startsWith(prefix) ? string.substring(prefix.length()) : string;
  }
}