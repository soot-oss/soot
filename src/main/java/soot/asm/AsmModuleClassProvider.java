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
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ClassProvider;
import soot.ClassSource;
import soot.FoundFile;
import soot.IFoundFile;
import soot.ModulePathSourceLocator;

/**
 * Objectweb ASM class provider.
 *
 * @author Andreas Dann
 */
public class AsmModuleClassProvider implements ClassProvider {
  private static final Logger logger = LoggerFactory.getLogger(AsmModuleClassProvider.class);

  @Override
  public ClassSource find(String cls) {
    final int idx = cls.lastIndexOf(':') + 1;
    String clsFile = cls.substring(0, idx) + cls.substring(idx).replace('.', '/') + ".class";
    IFoundFile file = ModulePathSourceLocator.v().lookUpInModulePath(clsFile);
    return file == null ? null : new AsmClassSource(cls, file);
  }

  public String getModuleName(FoundFile file) {
    final String[] moduleName = { null };
    ClassVisitor visitor = new ClassVisitor(Opcodes.ASM8) {

      @Override
      public ModuleVisitor visitModule(String name, int access, String version) {
        moduleName[0] = name;
        return null;
      }
    };
    try (InputStream d = file.inputStream()) {
      new ClassReader(d).accept(visitor, ClassReader.SKIP_FRAMES);
      return moduleName[0];
    } catch (IOException e) {
      logger.debug(e.getMessage(), e);
    } finally {
      file.close();
    }
    return null;
  }
}
