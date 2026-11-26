package soot.dotnet;

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

import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.options.Options;

/**
 * Locates the .NET NativeHost JNI library and the matching .NET runtime.
 *
 * <p>
 * The Soot build packages the complete .NET wrapper (native host library,
 * managed decompiler and its dependencies) as resources below
 * {@value #WRAPPER_RESOURCE_PATH}. If no location is configured explicitly,
 * this class extracts those resources to a temporary directory on first use.
 * That way the .NET frontend works out of the box.
 *
 * <p>
 * Explicit locations, in decreasing priority:
 * <ol>
 * <li>the {@code -dotnet-nativehost-path} Soot option (a library file or its
 * directory)</li>
 * <li>the {@code soot.dotnet.wrapper.path} system property</li>
 * <li>the {@code SOOT_DOTNET_WRAPPER} environment variable</li>
 * </ol>
 */
public final class DotnetNativeHost {
  private static final Logger logger = LoggerFactory.getLogger(DotnetNativeHost.class);

  /** Classpath directory holding the bundled wrapper. */
  public static final String WRAPPER_RESOURCE_PATH = "soot/dotnet/wrapper";

  static final String WRAPPER_PATH_PROPERTY = "soot.dotnet.wrapper.path";
  static final String WRAPPER_PATH_ENV = "SOOT_DOTNET_WRAPPER";
  static final String RUNTIME_DIR_PROPERTY = "soot.dotnet.runtime.dir";

  private static final Pattern RUNTIME_LIST_ENTRY = Pattern.compile("^Microsoft\\.NETCore\\.App (\\S+) \\[(.+)]$");

  private static final Object LOCK = new Object();
  private static volatile String nativeHostPath;
  private static volatile String runtimeDirectory;
  private static volatile boolean runtimeDirectoryResolved;

  private DotnetNativeHost() {
  }

  /**
   * Drops all cached resolutions. Intended for tests.
   */
  static void resetCache() {
    synchronized (LOCK) {
      nativeHostPath = null;
      runtimeDirectory = null;
      runtimeDirectoryResolved = false;
    }
  }

  /**
   * Returns the absolute path of the NativeHost JNI library.
   *
   * @throws IllegalStateException if no wrapper is configured and none is bundled
   */
  public static String getNativeHostPath() {
    String cached = nativeHostPath;
    if (cached != null) {
      return cached;
    }
    synchronized (LOCK) {
      if (nativeHostPath == null) {
        nativeHostPath = resolveNativeHostPath().getAbsolutePath();
      }
      return nativeHostPath;
    }
  }

  /**
   * Whether the .NET frontend can be used at all, i.e. a wrapper is either
   * configured or bundled.
   */
  public static boolean isAvailable() {
    try {
      String path = getNativeHostPath();
      return new File(path).isFile();
    } catch (RuntimeException e) {
      return false;
    }
  }

  /**
   * Returns the directory of the .NET runtime assemblies (the directory
   * containing {@code System.Private.CoreLib.dll}), or {@code null} if none can
   * be determined.
   *
   * <p>
   * The directory is used as an implicit Soot classpath entry for the .NET
   * frontend, so users do not have to pass it manually. Explicit configuration
   * via {@value #RUNTIME_DIR_PROPERTY} takes precedence.
   */
  public static String getRuntimeDirectory() {
    if (runtimeDirectoryResolved) {
      return runtimeDirectory;
    }
    synchronized (LOCK) {
      if (!runtimeDirectoryResolved) {
        runtimeDirectory = resolveRuntimeDirectory();
        runtimeDirectoryResolved = true;
        if (runtimeDirectory != null) {
          logger.debug("Using .NET runtime directory {}", runtimeDirectory);
        }
      }
      return runtimeDirectory;
    }
  }

  private static File resolveNativeHostPath() {
    String option = Options.v().dotnet_nativehost_path();
    if (!Strings.isNullOrEmpty(option)) {
      return resolveExplicit(option, "-dotnet-nativehost-path");
    }
    String property = System.getProperty(WRAPPER_PATH_PROPERTY);
    if (!Strings.isNullOrEmpty(property)) {
      return resolveExplicit(property, "-D" + WRAPPER_PATH_PROPERTY);
    }
    String environment = System.getenv(WRAPPER_PATH_ENV);
    if (!Strings.isNullOrEmpty(environment)) {
      return resolveExplicit(environment, WRAPPER_PATH_ENV);
    }
    return extractBundledWrapper();
  }

  private static File resolveExplicit(String location, String source) {
    File file = new File(location);
    File library = file.isDirectory() ? new File(file, getLibraryFileName()) : file;
    if (!library.isFile()) {
      throw new IllegalStateException("The .NET NativeHost library configured via " + source + " does not exist: "
          + library.getAbsolutePath());
    }
    return library;
  }

  private static File extractBundledWrapper() {
    File root = classpathRoot();
    File directoryMode = new File(root, WRAPPER_RESOURCE_PATH);
    File directoryLibrary = new File(directoryMode, getLibraryFileName());
    if (directoryLibrary.isFile()) {
      // Running from an exploded classes directory (IDE, tests): load in place.
      return directoryLibrary;
    }

    if (!root.isFile()) {
      throw new IllegalStateException(
      "No .NET NativeHost library available. Either build Soot with the .NET wrapper "
          + "(do not pass -DskipDotnet), or set -dotnet-nativehost-path, -D" + WRAPPER_PATH_PROPERTY
          + " or the " + WRAPPER_PATH_ENV
          + " environment variable to a NativeHost library or its directory.");
    }
    try {
      return extractFromJar(root);
    } catch (IOException e) {
      throw new IllegalStateException("Could not extract the bundled .NET wrapper from " + root, e);
    }
  }

  private static File classpathRoot() {
    try {
      URL location = DotnetNativeHost.class.getProtectionDomain().getCodeSource().getLocation();
      return new File(location.toURI());
    } catch (URISyntaxException | RuntimeException e) {
      throw new IllegalStateException("Could not determine the Soot classpath root", e);
    }
  }

  private static File extractFromJar(File jar) throws IOException {
    Path target = Files.createTempDirectory("soot-dotnet-wrapper-");
    target.toFile().deleteOnExit();

    String prefix = WRAPPER_RESOURCE_PATH + "/";
    try (JarFile jarFile = new JarFile(jar)) {
      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        String name = entry.getName();
        if (!name.startsWith(prefix) || name.equals(prefix)) {
          continue;
        }
        String relative = name.substring(prefix.length());
        Path output = target.resolve(relative).normalize();
        if (!output.startsWith(target)) {
          continue;
        }
        if (entry.isDirectory()) {
          Files.createDirectories(output);
          continue;
        }
        Files.createDirectories(output.getParent());
        try (InputStream in = jarFile.getInputStream(entry)) {
          Files.copy(in, output, StandardCopyOption.REPLACE_EXISTING);
        }
        output.toFile().deleteOnExit();
      }
    }

    File library = target.resolve(getLibraryFileName()).toFile();
    if (!library.isFile()) {
      throw new IllegalStateException(
          "The bundled .NET wrapper in " + jar + " does not contain " + getLibraryFileName());
    }
    logger.debug("Extracted bundled .NET wrapper to {}", target);
    return library;
  }
  
  public static boolean isWindows() {
    String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
    return os.contains("win");
  }

  static String getLibraryFileName() {
    String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
    if (os.contains("win")) {
      return "NativeHost.dll";
    }
    if (os.contains("mac") || os.contains("darwin")) {
      return "libNativeHost.dylib";
    }
    return "libNativeHost.so";
  }

  private static String resolveRuntimeDirectory() {
    String explicit = System.getProperty(RUNTIME_DIR_PROPERTY);
    if (!Strings.isNullOrEmpty(explicit)) {
      return bestRuntimeDirectory(new File(explicit));
    }

    List<String> command = new ArrayList<>();
    String dotnetRoot = System.getenv("DOTNET_ROOT");
    if (!Strings.isNullOrEmpty(dotnetRoot)) {
      command.add(new File(dotnetRoot, "dotnet").getAbsolutePath());
    } else {
      command.add("dotnet");
    }
    command.add("--list-runtimes");

    Process process;
    try {
      process = new ProcessBuilder(command).redirectErrorStream(true).start();
    } catch (IOException e) {
      logger.debug("Could not run 'dotnet --list-runtimes'", e);
      return null;
    }

    List<String> lines = new ArrayList<>();
    try (InputStreamReader pis = new InputStreamReader(process.getInputStream())) {
      lines = IOUtils.readLines(pis);
    } catch (IOException e1) {
      throw new RuntimeException(e1);
    }
    try {
      process.waitFor();
    } catch (InterruptedException e) {
      return null;
    }

    String bestDirectory = null;
    long[] bestVersion = null;
    for (String line : lines) {
      Matcher matcher = RUNTIME_LIST_ENTRY.matcher(line.trim());
      if (!matcher.matches()) {
        continue;
      }
      String versionString = matcher.group(1);
      long[] version = parseVersion(versionString);
      if (version == null) {
        continue;
      }
      // The list shows the runtime root; the assemblies live in the version
      // subdirectory.
      File directory = new File(matcher.group(2), versionString);
      if (!hasCoreLibrary(directory)) {
        continue;
      }
      if (bestVersion == null || compareVersions(version, bestVersion) > 0) {
        bestVersion = version;
        bestDirectory = directory.getAbsolutePath();
      }
    }
    return bestDirectory;
  }

  private static boolean hasCoreLibrary(File directory) {
    return directory.isDirectory() && new File(directory, "System.Private.CoreLib.dll").isFile();
  }

  /**
   * Returns the newest runtime directory below {@code root} (or {@code root}
   * itself) that contains the core library.
   */
  private static String bestRuntimeDirectory(File root) {
    if (hasCoreLibrary(root)) {
      return root.getAbsolutePath();
    }
    File[] versions = root.listFiles(File::isDirectory);
    if (versions == null) {
      return null;
    }
    File best = null;
    long[] bestVersion = null;
    for (File version : versions) {
      long[] parsed = parseVersion(version.getName());
      if (parsed == null || !hasCoreLibrary(version)) {
        continue;
      }
      if (bestVersion == null || compareVersions(parsed, bestVersion) > 0) {
        bestVersion = parsed;
        best = version;
      }
    }
    return best == null ? null : best.getAbsolutePath();
  }

  private static long[] parseVersion(String version) {
    String[] parts = version.split("\\.");
    long[] result = new long[3];
    try {
      for (int i = 0; i < result.length; i++) {
        result[i] = i < parts.length ? Long.parseLong(parts[i].replaceAll("[^0-9].*$", "")) : 0;
      }
    } catch (NumberFormatException e) {
      return null;
    }
    return result;
  }

  private static int compareVersions(long[] left, long[] right) {
    for (int i = 0; i < left.length; i++) {
      int comparison = Long.compare(left[i], right[i]);
      if (comparison != 0) {
        return comparison;
      }
    }
    return 0;
  }
}
