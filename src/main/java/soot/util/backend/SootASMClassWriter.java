package soot.util.backend;

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

import static soot.util.backend.ASMBackendUtils.slashify;

import org.objectweb.asm.ClassWriter;

import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;

/**
 * ASM class writer with soot-specific resolution of common superclasses
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class SootASMClassWriter extends ClassWriter {

  /**
   * Constructs a new {@link ClassWriter} object.
   *
   * @param flags
   *          option flags that can be used to modify the default behavior of this class. See {@link #COMPUTE_MAXS},
   *          {@link #COMPUTE_FRAMES}.
   */
  public SootASMClassWriter(int flags) {
    super(flags);
  }

  /*
   * We need to overwrite this method here, as we are generating multiple classes that might reference each other. See
   * asm4-guide, top of page 45 for more information.
   */
  /*
   * (non-Javadoc)
   *
   * @see org.objectweb.asm.ClassWriter#getCommonSuperClass(java.lang.String, java.lang.String)
   */
  @Override
  protected String getCommonSuperClass(String type1, String type2) {
    String typeName1 = type1.replace('/', '.');
    String typeName2 = type2.replace('/', '.');

    SootClass s1 = Scene.v().getSootClass(typeName1);
    SootClass s2 = Scene.v().getSootClass(typeName2);

    // If these two classes haven't been loaded yet or are phantom, we take
    // java.lang.Object as the common superclass
    final Type mergedType;
    if (s1.isPhantom() || s2.isPhantom() || s1.resolvingLevel() == SootClass.DANGLING
        || s2.resolvingLevel() == SootClass.DANGLING) {
      mergedType = Scene.v().getObjectType();
    } else {
      Type t1 = s1.getType();
      Type t2 = s2.getType();

      mergedType = t1.merge(t2, Scene.v());
    }

    if (mergedType instanceof RefType) {
      return slashify(((RefType) mergedType).getClassName());
    } else {
      throw new RuntimeException("Could not find common super class");
    }
  }

}
