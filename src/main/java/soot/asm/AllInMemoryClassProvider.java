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
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.ClassProvider;
import soot.ClassSource;
import soot.options.Options;

public class AllInMemoryClassProvider implements ClassProvider {
  private static final Logger logger = LoggerFactory.getLogger(AllInMemoryClassProvider.class);

  private final Map<String, ArrayAsmClassSource> classCache = new HashMap<>();

  public AllInMemoryClassProvider(List<String> classPath) {
    long bytes = 0;
    try {
      for (String path : classPath) {
        if (path.contains("!")) {
          continue;
        }
        bytes += load(path);
      }
      logger.info("Loaded: {} path entries, {} classes, {} bytes", classPath.size(), classCache.size(), bytes);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ClassSource find(String cls) {
    return classCache.get(cls);
  }

  private long load(String path) {
    File f = new File(path);
    if (!f.exists() && !Options.v().ignore_classpath_errors()) {
      throw new RuntimeException("Error: The path '" + path + "' does not exist.");
    }
    if (!f.canRead() && !Options.v().ignore_classpath_errors()) {
      throw new RuntimeException("Error: The path '" + path + "' exists but is not readable.");
    }
    if (f.isFile()) {
      if (path.endsWith(".zip") || path.endsWith(".jar")) {
        return loadArchive(path);
      } else {
        throw new RuntimeException("Cannot define type for " + path);
      }
    } else {
      return loadDir(path);
    }
  }

  private long loadDir(String path) {
    AtomicLong loaded = new AtomicLong();
    try {
      String canonicalPath = canonical(path);
      String prefix = canonicalPath + "/";
      Files.walk(Paths.get(canonicalPath))
          .filter(Files::isRegularFile)
          .filter(file -> file.toString().endsWith(".class"))
          .forEach(file -> {
            String canonicalFileName = removePrefix(canonical(file.toString()), prefix);
            String className = className(canonicalFileName);
            try {
              byte[] bytes = Files.readAllBytes(file);
              putIntoCache(className, bytes);
              loaded.addAndGet(bytes.length);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return loaded.get();
  }

  private void putIntoCache(String className, byte[] bytes) {
    classCache.put(className, new ArrayAsmClassSource(className, bytes));
  }

  private long loadArchive(String path) {
    long loaded = 0;
    try (ZipFile archive = new ZipFile(path)) {
      Enumeration<? extends ZipEntry> it = archive.entries();
      while (it.hasMoreElements()) {
        ZipEntry entry = it.nextElement();
        if (entry.getName().endsWith(".class")) {
          try (InputStream is = archive.getInputStream(entry)) {
            String className = className(entry.getName());
            byte[] bytes = readFully(is);
            putIntoCache(className, bytes);
            loaded += bytes.length;
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return loaded;
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