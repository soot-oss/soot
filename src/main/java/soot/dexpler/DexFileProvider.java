package soot.dexpler;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.MultiDexContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.CompilationDeathException;
import soot.G;
import soot.Scene;
import soot.Singletons;
import soot.options.Options;

/**
 * Class providing dex files from a given source, e.g., jar, apk, dex, folder containing multiple dex files
 *
 * @author Manuel Benz created on 16.10.17
 */
public class DexFileProvider {
  private static final Logger logger = LoggerFactory.getLogger(DexFileProvider.class);

  private final static Comparator<DexContainer> DEFAULT_PRIORITIZER = new Comparator<DexContainer>() {

    @Override
    public int compare(DexContainer o1, DexContainer o2) {
      String s1 = o1.getDexName(), s2 = o2.getDexName();

      // "classes.dex" has highest priority
      if (s1.equals("classes.dex")) {
        return 1;
      } else if (s2.equals("classes.dex")) {
        return -1;
      }

      // if one of the strings starts with "classes", we give it the edge right here
      boolean s1StartsClasses = s1.startsWith("classes");
      boolean s2StartsClasses = s2.startsWith("classes");

      if (s1StartsClasses && !s2StartsClasses) {
        return 1;
      } else if (s2StartsClasses && !s1StartsClasses) {
        return -1;
      }

      // otherwise, use natural string ordering
      return s1.compareTo(s2);
    }
  };
  /**
   * Mapping of filesystem file (apk, dex, etc.) to mapping of dex name to dex file
   */
  private final Map<String, Map<String, DexContainer>> dexMap = new HashMap<>();

  public DexFileProvider(Singletons.Global g) {
  }

  public static DexFileProvider v() {
    return G.v().soot_dexpler_DexFileProvider();
  }

  /**
   * Returns all dex files found in dex source sorted by the default dex prioritizer
   *
   * @param dexSource
   *          Path to a jar, apk, dex, odex or a directory containing multiple dex files
   * @return List of dex files derived from source
   */
  public List<DexContainer> getDexFromSource(File dexSource) throws IOException {
    return getDexFromSource(dexSource, DEFAULT_PRIORITIZER);
  }

  /**
   * Returns all dex files found in dex source sorted by the default dex prioritizer
   *
   * @param dexSource
   *          Path to a jar, apk, dex, odex or a directory containing multiple dex files
   * @param prioritizer
   *          A comparator that defines the ordering of dex files in the result list
   * @return List of dex files derived from source
   */
  public List<DexContainer> getDexFromSource(File dexSource, Comparator<DexContainer> prioritizer) throws IOException {
    ArrayList<DexContainer> resultList = new ArrayList<>();
    List<File> allSources = allSourcesFromFile(dexSource);
    updateIndex(allSources);

    for (File theSource : allSources) {
      resultList.addAll(dexMap.get(theSource.getCanonicalPath()).values());
    }

    if (resultList.size() > 1) {
      Collections.sort(resultList, Collections.reverseOrder(prioritizer));
    }
    return resultList;
  }

  /**
   * Returns the first dex file with the given name found in the given dex source
   *
   * @param dexSource
   *          Path to a jar, apk, dex, odex or a directory containing multiple dex files
   * @return Dex file with given name in dex source
   * @throws CompilationDeathException
   *           If no dex file with the given name exists
   */
  public DexContainer getDexFromSource(File dexSource, String dexName) throws IOException {
    List<File> allSources = allSourcesFromFile(dexSource);
    updateIndex(allSources);

    // we take the first dex we find with the given name
    for (File theSource : allSources) {
      DexContainer dexFile = dexMap.get(theSource.getCanonicalPath()).get(dexName);
      if (dexFile != null) {
        return dexFile;
      }
    }

    throw new CompilationDeathException("Dex file with name '" + dexName + "' not found in " + dexSource);
  }

  private List<File> allSourcesFromFile(File dexSource) throws IOException {
    if (dexSource.isDirectory()) {
      List<File> dexFiles = getAllDexFilesInDirectory(dexSource);
      if (dexFiles.size() > 1 && !Options.v().process_multiple_dex()) {
        File file = dexFiles.get(0);
        logger.warn("Multiple dex files detected, only processing '" + file.getCanonicalPath()
            + "'. Use '-process-multiple-dex' option to process them all.");
        return Collections.singletonList(file);
      } else {
        return dexFiles;
      }
    } else {
      String ext = com.google.common.io.Files.getFileExtension(dexSource.getName()).toLowerCase();
      if ((ext.equals("jar") || ext.equals("zip")) && !Options.v().search_dex_in_archives()) {
        return Collections.EMPTY_LIST;
      } else {
        return Collections.singletonList(dexSource);
      }
    }
  }

  private void updateIndex(List<File> dexSources) throws IOException {
    for (File theSource : dexSources) {
      String key = theSource.getCanonicalPath();
      Map<String, DexContainer> dexFiles = dexMap.get(key);
      if (dexFiles == null) {
        try {
          dexFiles = mappingForFile(theSource);
          dexMap.put(key, dexFiles);
        } catch (IOException e) {
          throw new CompilationDeathException("Error parsing dex source", e);
        }
      }
    }
  }

  /**
   * @param dexSourceFile
   *          A file containing either one or multiple dex files (apk, zip, etc.) but no directory!
   * @return
   * @throws IOException
   */
  private Map<String, DexContainer> mappingForFile(File dexSourceFile) throws IOException {
    int api = Scene.v().getAndroidAPIVersion();
    boolean multiple_dex = Options.v().process_multiple_dex();

    // load dex files from apk/folder/file
    MultiDexContainer<? extends DexBackedDexFile> dexContainer
        = DexFileFactory.loadDexContainer(dexSourceFile, Opcodes.forApi(api));

    List<String> dexEntryNameList = dexContainer.getDexEntryNames();
    int dexFileCount = dexEntryNameList.size();

    if (dexFileCount < 1) {
      if (Options.v().verbose()) {
        logger.debug("" + String.format("Warning: No dex file found in '%s'", dexSourceFile));
      }
      return Collections.emptyMap();
    }

    Map<String, DexContainer> dexMap = new HashMap<>(dexFileCount);

    // report found dex files and add to list.
    // We do this in reverse order to make sure that we add the first entry if there is no classes.dex file in single dex
    // mode
    ListIterator<String> entryNameIterator = dexEntryNameList.listIterator(dexFileCount);
    while (entryNameIterator.hasPrevious()) {
      String entryName = entryNameIterator.previous();
      DexBackedDexFile entry = dexContainer.getEntry(entryName);
      entryName = deriveDexName(entryName);
      logger.debug("" + String.format("Found dex file '%s' with %d classes in '%s'", entryName, entry.getClasses().size(),
          dexSourceFile.getCanonicalPath()));

      if (multiple_dex) {
        dexMap.put(entryName, new DexContainer(entry, entryName, dexSourceFile));
      } else if (dexMap.isEmpty() && (entryName.equals("classes.dex") || !entryNameIterator.hasPrevious())) {
        // We prefer to have classes.dex in single dex mode.
        // If we haven't found a classes.dex until the last element, take the last!
        dexMap = Collections.singletonMap(entryName, new DexContainer(entry, entryName, dexSourceFile));
        if (dexFileCount > 1) {
          logger.warn("Multiple dex files detected, only processing '" + entryName
              + "'. Use '-process-multiple-dex' option to process them all.");
        }
      }
    }
    return Collections.unmodifiableMap(dexMap);
  }

  private String deriveDexName(String entryName) {
    return new File(entryName).getName();
  }

  private List<File> getAllDexFilesInDirectory(File path) {
    Queue<File> toVisit = new ArrayDeque<File>();
    Set<File> visited = new HashSet<File>();
    List<File> ret = new ArrayList<File>();
    toVisit.add(path);
    while (!toVisit.isEmpty()) {
      File cur = toVisit.poll();
      if (visited.contains(cur)) {
        continue;
      }
      visited.add(cur);
      if (cur.isDirectory()) {
        toVisit.addAll(Arrays.asList(cur.listFiles()));
      } else if (cur.isFile() && cur.getName().endsWith(".dex")) {
        ret.add(cur);
      }
    }
    return ret;
  }

  public static final class DexContainer {
    private final DexBackedDexFile base;
    private final String name;
    private final File filePath;

    public DexContainer(DexBackedDexFile base, String name, File filePath) {
      this.base = base;
      this.name = name;
      this.filePath = filePath;
    }

    public DexBackedDexFile getBase() {
      return base;
    }

    public String getDexName() {
      return name;
    }

    public File getFilePath() {
      return filePath;
    }
  }

}
