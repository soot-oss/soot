package soot.dotnet.instructions;

import java.util.ArrayList;
import java.util.List;

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
import soot.ArrayType;
import soot.Body;
import soot.Type;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.Jimple;

/**
 * Instantiate new array
 */
public class CilNewArrInstruction extends AbstractCilnstruction {
  public CilNewArrInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    throw new NoStatementInstructionException(instruction);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    Type type = DotnetTypeFactory.toSootType(instruction.getType().getFullname());

    List<Value> sizesOfArr = new ArrayList<>();
    for (ProtoIlInstructions.IlInstructionMsg index : instruction.getIndicesList()) {
      CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(index, dotnetBody, cilBlock);
      Value value = cilExpr.jimplifyExpr(jb);
      Value val = simplifyComplexExpression(jb, value);
      sizesOfArr.add(val);
    }

    // if only one dim array
    if (sizesOfArr.size() == 1) {
      return Jimple.v().newNewArrayExpr(type, sizesOfArr.get(0));
    }

    ArrayType arrayType = ArrayType.v(type, sizesOfArr.size());
    return Jimple.v().newNewMultiArrayExpr(arrayType, sizesOfArr);
  }
}
