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

import org.objectweb.asm.ClassReader;
import soot.ClassSource;
import soot.SootClass;
import soot.SootResolver;
import soot.javaToJimple.IInitialResolver.Dependencies;

/**
 * ASM class source implementation based on byte array.
 *
 * @see soot.asm.AsmClassSource
 */
public class ArrayAsmClassSource extends ClassSource {

  private final byte[] data;

  /**
   * Constructs a new ASM class source.
   *
   * @param cls  fully qualified name of the class.
   * @param data byte array containing data for class.
   */
  protected ArrayAsmClassSource(String cls, byte[] data) {
    super(cls);
    this.data = data;
  }

  @SuppressWarnings("DuplicatedCode")
  @Override
  public Dependencies resolve(SootClass sc) {
    ClassReader reader = new ClassReader(data);
    SootClassBuilder scb = new SootClassBuilder(sc);
    reader.accept(scb, ClassReader.SKIP_FRAMES);
    Dependencies deps = new Dependencies();
    deps.typesToSignature.addAll(scb.deps);
    // add the outer class information, could not be called in the builder, since sc needs to be
    // resolved - before calling setOuterClass()
    String outerClassName;
    if (!sc.hasOuterClass() && className.contains("$")) {
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
  }

  @Override
  public void close() {
  }
}
