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
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.proto.ProtoIlInstructions.IlInstructionMsg;
import soot.jimple.Jimple;

/**
 * Load element out of an array local In ILSpy/.NET instruction an element can be loaded by one instruction (e.g. elem[1,5]);
 * unfolding it
 * 
 * Loads the object itself, not a reference!
 * 
 * https://learn.microsoft.com/en-us/dotnet/api/system.reflection.emit.opcodes.ldelem?view=net-8.0
 */
public class CilLdElemInstruction extends AbstractCilnstruction {
  public CilLdElemInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    throw new NoStatementInstructionException(instruction);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getArray(), dotnetBody, cilBlock);
    Value baseArrayLocal = cilExpr.jimplifyExpr(jb);
    baseArrayLocal = simplifyComplexExpression(jb, baseArrayLocal);

    for (int i = 0; i < instruction.getIndicesCount() - 1; i++) {
      IlInstructionMsg ind = instruction.getIndicesList().get(i);
      Value indExpr = CilInstructionFactory.fromInstructionMsg(ind, dotnetBody, cilBlock).jimplifyExpr(jb);
      Value index = simplifyComplexExpression(jb, indExpr);
      baseArrayLocal = simplifyComplexExpression(jb, Jimple.v().newArrayRef(baseArrayLocal, index));
    }

    Value ind = CilInstructionFactory
        .fromInstructionMsg(instruction.getIndices(instruction.getIndicesCount() - 1), dotnetBody, cilBlock)
        .jimplifyExpr(jb);
    Value index = simplifyComplexExpression(jb, ind);
    return Jimple.v().newArrayRef(baseArrayLocal, index);
  }

}
