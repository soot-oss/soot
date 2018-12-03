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
package soot.asm;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;

import soot.ClassProvider;
import soot.ClassSource;
import soot.FoundFile;
import soot.ModulePathSourceLocator;

/**
 * Objectweb ASM class provider.
 *
 * @author Andreas Dann
 */
public class AsmModuleClassProvider implements ClassProvider {

  public ClassSource find(String cls) {
    String clsFileName = cls.substring(cls.lastIndexOf(":") + 1, cls.length()).replace('.', '/') + ".class";
    String modules = cls.substring(0, cls.lastIndexOf(":") + 1);
    String clsFile = modules + clsFileName;
    FoundFile file = ModulePathSourceLocator.v().lookUpInModulePath(clsFile);
    return file == null ? null : new AsmClassSource(cls, file);
  }

  public String getModuleName(FoundFile file) {
    final String[] moduleName = { null };
    org.objectweb.asm.ClassVisitor visitor = new org.objectweb.asm.ClassVisitor(Opcodes.ASM7) {

      @Override
      public ModuleVisitor visitModule(String name, int access, String version) {
        moduleName[0] = name;
        return null;
      }
    };
    InputStream d = null;
    try {
      d = file.inputStream();

      new ClassReader(d).accept(visitor, ClassReader.SKIP_FRAMES);
      return moduleName[0];
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (d != null) {
          d.close();
          d = null;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (file != null) {
        file.close();
        file = null;
      }

    }
    return null;
  }
}
