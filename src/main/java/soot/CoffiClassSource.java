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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.javaToJimple.IInitialResolver;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;

/**
 * A class source for resolving from .class files through coffi.
 */
public class CoffiClassSource extends ClassSource {
  private static final Logger logger = LoggerFactory.getLogger(CoffiClassSource.class);

  private FoundFile foundFile;
  private InputStream classFile;
  private final String fileName;
  private final String zipFileName;

  public CoffiClassSource(String className, FoundFile foundFile) {
    super(className);
    if (foundFile == null) {
      throw new IllegalStateException("Error: The FoundFile must not be null.");
    }
    this.foundFile = foundFile;
    this.classFile = foundFile.inputStream();
    this.fileName = foundFile.getFile().getAbsolutePath();
    this.zipFileName = !foundFile.isZipFile() ? null : foundFile.getFilePath();
  }

  public CoffiClassSource(String className, InputStream classFile, String fileName) {
    super(className);
    if (classFile == null || fileName == null) {
      throw new IllegalStateException("Error: The class file input strean and file name must not be null.");
    }
    this.classFile = classFile;
    this.fileName = fileName;
    this.zipFileName = null;
    this.foundFile = null;
  }

  public Dependencies resolve(SootClass sc) {
    if (Options.v().verbose()) {
      logger.debug("resolving [from .class]: " + className);
    }
    List<Type> references = new ArrayList<Type>();

    try {
      soot.coffi.Util.v().resolveFromClassFile(sc, classFile, fileName, references);
    } finally {
      close();
    }

    addSourceFileTag(sc);

    IInitialResolver.Dependencies deps = new IInitialResolver.Dependencies();
    deps.typesToSignature.addAll(references);
    return deps;
  }

  private void addSourceFileTag(soot.SootClass sc) {
    if (fileName == null && zipFileName == null) {
      return;
    }

    soot.tagkit.SourceFileTag tag = null;
    if (sc.hasTag("SourceFileTag")) {
      tag = (soot.tagkit.SourceFileTag) sc.getTag("SourceFileTag");
    } else {
      tag = new soot.tagkit.SourceFileTag();
      sc.addTag(tag);
    }

    // Sets sourceFile only when it hasn't been set before
    if (tag.getSourceFile() == null) {
      String name = zipFileName == null ? new File(fileName).getName() : new File(zipFileName).getName();
      tag.setSourceFile(name);
    }
  }

  @Override
  public void close() {
    try {
      if (classFile != null) {
        classFile.close();
        classFile = null;
      }
    } catch (IOException e) {
      throw new RuntimeException("Error: Failed to close source input stream.", e);
    } finally {
      if (foundFile != null) {
        foundFile.close();
        foundFile = null;
      }
    }
  }
}
