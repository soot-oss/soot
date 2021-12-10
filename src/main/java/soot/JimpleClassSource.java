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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.jimple.JimpleMethodSource;
import soot.jimple.parser.lexer.LexerException;
import soot.jimple.parser.parser.ParserException;
import soot.options.Options;

/**
 * A class source for resolving from .jimple files using the Jimple parser.
 */
public class JimpleClassSource extends ClassSource {
  private static final Logger logger = LoggerFactory.getLogger(JimpleClassSource.class);

  private FoundFile foundFile;

  public JimpleClassSource(String className, FoundFile foundFile) {
    super(className);
    if (foundFile == null) {
      throw new IllegalStateException("Error: The FoundFile must not be null.");
    }
    this.foundFile = foundFile;
  }

  @Override
  public Dependencies resolve(SootClass sc) {
    if (Options.v().verbose()) {
      logger.debug("resolving [from .jimple]: " + className);
    }

    InputStream classFile = null;
    try {
      // Parse jimple file
      classFile = foundFile.inputStream();
      soot.jimple.parser.JimpleAST jimpAST = new soot.jimple.parser.JimpleAST(classFile);
      jimpAST.getSkeleton(sc);

      // Set method source for all methods
      JimpleMethodSource mtdSrc = new JimpleMethodSource(jimpAST);
      for (Iterator<SootMethod> mtdIt = sc.methodIterator(); mtdIt.hasNext();) {
        SootMethod sm = mtdIt.next();
        sm.setSource(mtdSrc);
      }

      // set outer class if not set (which it should not be) and class name contains outer class indicator
      String outerClassName = null;
      if (!sc.hasOuterClass()) {
        String className = sc.getName();
        if (className.contains("$")) {
          if (className.contains("$-")) {
            /*
             * This is a special case for generated lambda classes of jack and jill compiler. Generated lambda classes may
             * contain '$' which do not indicate an inner/outer class separator if the '$' occurs after a inner class with a
             * name starting with '-'. Thus we search for '$-' and anything after it including '-' is the inner classes name
             * and anything before it is the outer classes name.
             */
            outerClassName = className.substring(0, className.indexOf("$-"));
          } else {
            outerClassName = className.substring(0, className.lastIndexOf('$'));
          }
          sc.setOuterClass(SootResolver.v().makeClassRef(outerClassName));
        }
      }

      // Construct the type dependencies of the class
      Dependencies deps = new Dependencies();
      // The method documentation states it returns RefTypes only, so this is a transformation safe
      for (String t : jimpAST.getCstPool()) {
        deps.typesToSignature.add(RefType.v(t));
      }
      if (outerClassName != null) {
        deps.typesToSignature.add(RefType.v(outerClassName));
      }

      return deps;
    } catch (IOException e) {
      throw new RuntimeException("Error: Failed to create JimpleAST from source input stream for class " + className + ".",
          e);
    } catch (ParserException e) {
      throw new RuntimeException("Error: Failed when parsing class " + className + ".", e);
    } catch (LexerException e) {
      throw new RuntimeException("Error: Failed when lexing class " + className + ".", e);
    } finally {
      try {
        if (classFile != null) {
          classFile.close();
          classFile = null;
        }
      } catch (IOException e) {
        throw new RuntimeException("Error: Failed to close source input stream.", e);
      } finally {
        close();
      }
    }
  }

  @Override
  public void close() {
    if (foundFile != null) {
      foundFile.close();
      foundFile = null;
    }
  }
}
