package soot.dotnet.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.values.FunctionPointerConstant;

/**
 * Create Fake stub for LdFtn (load function), cannot be represented in Jimple
 */
public class CilLdFtnInstruction extends AbstractCilnstruction {

  public CilLdFtnInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    throw new NoStatementInstructionException(instruction);
  }

  /**
   * Call Expression
   *
   * @param jb
   * @return
   */
  @Override
  public Value jimplifyExpr(Body jb) {
    SootClass clazz = Scene.v().getSootClass(instruction.getMethod().getDeclaringType().getFullname());
    DotnetMethod method = new DotnetMethod(instruction.getMethod(), clazz);
    return new FunctionPointerConstant(method, false);
  }

}
