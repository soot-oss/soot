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

import com.google.common.base.Optional;

import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;

import soot.RefType;
import soot.SootClass;
import soot.SootModuleInfo;
import soot.SootModuleResolver;

/**
 * Builds Soot's representation for a module-info class.
 * 
 * @author Andreas Dann
 */
public class SootModuleInfoBuilder extends ModuleVisitor {

  private final SootClassBuilder scb;
  private final SootModuleInfo klass;
  private final String name;

  public SootModuleInfoBuilder(String name, SootModuleInfo klass, SootClassBuilder scb) {
    super(Opcodes.ASM8);
    this.klass = klass;
    this.name = name;
    this.scb = scb;
  }

  @Override
  public void visitRequire(String module, int access, String version) {
    SootClass moduleInfo = SootModuleResolver.v().makeClassRef(SootModuleInfo.MODULE_INFO, Optional.of(module));
    klass.getRequiredModules().put((SootModuleInfo) moduleInfo, access);
    scb.addDep(RefType.v(moduleInfo));
  }

  @Override
  public void visitExport(String packaze, int access, String... modules) {
    if (packaze != null) {
      klass.addExportedPackage(packaze, modules);
    }
  }

  @Override
  public void visitOpen(String packaze, int access, String... modules) {
    if (packaze != null) {
      klass.addOpenedPackage(packaze, modules);
    }
  }
}
