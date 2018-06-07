package soot.jimple.toolkits.base;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.G;
import soot.PhaseOptions;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.options.Options;
import soot.util.Chain;

/**
 * A scene transformer that renames the duplicated class names.
 *
 * The definition of duplicated class names. if (className1.equalsIgnoreCase(className2) { //className1 and className2 are
 * duplicated class names. }
 *
 * Because some file systems are case-insensitive (e.g., Mac OS). When file a.b.c.class exists, a.b.C.class will over-write
 * the content of a.b.c.class and case inconsistent that a.b.c.class file contains the content of a.b.C.class.
 *
 * However, in some case, at lest in Android applications, the duplicated class names exist. For example, an app (Sha256:
 * 0015AE7C27688D45F79170DCEA16131CE557912A1A0C5F3B6B0465EE0774A452) in the Genome project contains duplicated class names.
 * When transforming the app to classes, some classes are missing and consequently case problems for other analysis tools
 * that relay on Soot (e.g., Error: class com.adwo.adsdk.s read in from a classfile in which com.adwo.adsdk.S was expected).
 *
 *
 */
public class RenameDuplicatedClasses extends SceneTransformer {
  private static final Logger logger = LoggerFactory.getLogger(RenameDuplicatedClasses.class);

  public RenameDuplicatedClasses(Singletons.Global g) {
  }

  public static RenameDuplicatedClasses v() {
    return G.v().soot_jimple_toolkits_base_RenameDuplicatedClasses();
  }

  private static final String FIXED_CLASS_NAME_SPERATOR = "-";

  @Override
  protected void internalTransform(String phaseName, Map<String, String> options) {
    // If the file system is case sensitive, no need to rename the classes
    if (isFileSystemCaseSensitive()) {
      return;
    }

    String fixedClassNamesStr = PhaseOptions.getString(options, "fixedClassNames");
    String[] classNames = fixedClassNamesStr.split(FIXED_CLASS_NAME_SPERATOR);
    List<String> fixedClassNames = Arrays.asList(classNames);
    duplicatedCheck(fixedClassNames);

    if (Options.v().verbose()) {
      logger.debug("The fixed class names are: " + fixedClassNames);
    }

    Chain<SootClass> sootClasses = Scene.v().getClasses();
    Map<String, String> lowerCaseClassNameToReal = new HashMap<String, String>();

    int count = 0;

    for (Iterator<SootClass> iter = sootClasses.snapshotIterator(); iter.hasNext();) {
      SootClass sootClass = iter.next();
      String className = sootClass.getName();

      if (lowerCaseClassNameToReal.containsKey(className.toLowerCase())) {
        if (fixedClassNames.contains(className)) {
          sootClass = Scene.v().getSootClass(lowerCaseClassNameToReal.get(className.toLowerCase()));
          className = lowerCaseClassNameToReal.get(className.toLowerCase());
        }

        String newClassName = className + (count++);
        sootClass.rename(newClassName);

        // if(Options.v().verbose())
        // {
        logger.debug("Rename duplicated class " + className + " to class " + newClassName);
        // }
      } else {
        lowerCaseClassNameToReal.put(className.toLowerCase(), className);
      }
    }
  }

  public void duplicatedCheck(List<String> classNames) {
    Set<String> classNameSet = new HashSet<String>();
    for (String className : classNames) {
      if (classNameSet.contains(className.toLowerCase())) {
        throw new RuntimeException("The fixed class names cannot contain duplicated class names.");
      } else {
        classNameSet.add(className.toLowerCase());
      }
    }
  }

  /**
   * An naive approach to check whether the file system is case sensitive or not
   *
   * @return
   */
  public boolean isFileSystemCaseSensitive() {
    File dir = new File(".");
    File[] files = dir.listFiles();
    if (files == null) {
      return false;
    }

    for (File file : files) {
      if (file.isFile()) {
        String lowerCaseFilePath = file.getAbsolutePath().toLowerCase();
        String upperCaseFilePath = file.getAbsolutePath().toUpperCase();

        File lowerCaseFile = new File(lowerCaseFilePath);
        File upperCaseFile = new File(upperCaseFilePath);

        if (!(lowerCaseFile.exists() && upperCaseFile.exists())) {
          return true;
        }

      }
    }

    return false;
  }
}
