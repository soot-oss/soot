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

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;

import soot.ClassSource;
import soot.IFoundFile;
import soot.SootClass;
import soot.SootResolver;
import soot.javaToJimple.IInitialResolver.Dependencies;

/**
 * ASM class source implementation.
 *
 * @author Aaloan Miftah
 */
public class AsmClassSource extends ClassSource {

  protected IFoundFile foundFile;

  /**
   * Constructs a new ASM class source.
   * 
   * @param cls
   *          fully qualified name of the class.
   * @param foundFile
   *          foundfile pointing to the data for class.
   */
  public AsmClassSource(String cls, IFoundFile foundFile) {
    super(cls);
    if (foundFile == null) {
      throw new IllegalStateException("Error: The FoundFile must not be null.");
    }
    this.foundFile = foundFile;
  }

  @Override
  public Dependencies resolve(SootClass sc) {
    InputStream d = null;
    try {
      d = foundFile.inputStream();
      ClassReader clsr = new ClassReader(d);
      SootClassBuilder scb = new SootClassBuilder(sc);
      clsr.accept(scb, ClassReader.SKIP_FRAMES);
      Dependencies deps = new Dependencies();
      deps.typesToSignature.addAll(scb.deps);
      // add the outer class information, could not be called in the builder, since sc needs to be
      // resolved - before calling setOuterClass()
      if (!sc.hasOuterClass() && className.contains("$")) {
        String outerClassName;
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
      return deps;
    } catch (IOException e) {
      throw new RuntimeException("Error: Failed to create class reader from class source.", e);
    } finally {
      try {
        if (d != null) {
          d.close();
          d = null;
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