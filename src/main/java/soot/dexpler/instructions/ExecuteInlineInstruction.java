package soot.dexpler.instructions;

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

import java.util.List;

import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.analysis.AnalyzedInstruction;
import org.jf.dexlib2.analysis.ClassPath;
import org.jf.dexlib2.analysis.InlineMethodResolver;
import org.jf.dexlib2.analysis.MethodAnalyzer;
import org.jf.dexlib2.dexbacked.DexBackedOdexFile;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35mi;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rmi;
import org.jf.dexlib2.iface.reference.MethodReference;

import soot.dexpler.DexBody;

public class ExecuteInlineInstruction extends MethodInvocationInstruction implements OdexInstruction {

  private Method targetMethod = null;

  public ExecuteInlineInstruction(Instruction instruction, int codeAddress) {
    super(instruction, codeAddress);
  }

  @Override
  public void deOdex(DexFile parentFile, Method method, ClassPath cp) {
    if (!(parentFile instanceof DexBackedOdexFile)) {
      throw new RuntimeException("ODEX instruction in non-ODEX file");
    }
    DexBackedOdexFile odexFile = (DexBackedOdexFile) parentFile;

    InlineMethodResolver inlineMethodResolver = InlineMethodResolver.createInlineMethodResolver(odexFile.getOdexVersion());
    MethodAnalyzer analyzer = new MethodAnalyzer(cp, method, inlineMethodResolver, false);
    targetMethod = inlineMethodResolver.resolveExecuteInline(new AnalyzedInstruction(analyzer, instruction, -1, -1));
  }

  @Override
  protected MethodReference getTargetMethodReference() {
    return targetMethod;
  }

  @Override
  public void jimplify(DexBody body) {
    int acccessFlags = targetMethod.getAccessFlags();
    if (AccessFlags.STATIC.isSet(acccessFlags)) {
      jimplifyStatic(body);
    } else if (AccessFlags.PRIVATE.isSet(acccessFlags)) {
      jimplifySpecial(body);
    } else {
      jimplifyVirtual(body);
    }
  }

  /**
   * Return the indices used in this instruction.
   *
   * @return a list of register indices
   */
  protected List<Integer> getUsedRegistersNums() {
    if (instruction instanceof Instruction35mi) {
      return getUsedRegistersNums((Instruction35mi) instruction);
    } else if (instruction instanceof Instruction3rmi) {
      return getUsedRegistersNums((Instruction3rmi) instruction);
    }
    throw new RuntimeException("Instruction is not an ExecuteInline");
  }

}
