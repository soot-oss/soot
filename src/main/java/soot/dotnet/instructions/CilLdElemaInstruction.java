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
import soot.Body;
import soot.Immediate;
import soot.Local;
import soot.Type;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.members.method.DotnetBodyVariableManager;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;

/**
 * Load element out of an array local In ILSpy/.NET instruction an element can be loaded by one instruction (e.g.
 * elem[1,5]); unfolding it
 */
public class CilLdElemaInstruction extends AbstractCilnstruction {
  public CilLdElemaInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);

    if (instruction.getIndicesCount() > 1) {
      isMultiArrayRef = true;
    }
  }

  private boolean isMultiArrayRef = false;

  private final List<Value> indices = new ArrayList<>();

  /**
   * Base array value/local from where to load element
   */
  private Value baseArrayLocal;

  @Override
  public void jimplify(Body jb) {
    throw new NoStatementInstructionException(instruction);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getArray(), dotnetBody, cilBlock);
    baseArrayLocal = cilExpr.jimplifyExpr(jb);

    if (instruction.getIndicesCount() == 1) {
      Value ind
          = CilInstructionFactory.fromInstructionMsg(instruction.getIndices(0), dotnetBody, cilBlock).jimplifyExpr(jb);
      Value index = ind instanceof Immediate ? ind : DotnetBodyVariableManager.inlineLocals(ind, jb);
      return Jimple.v().newArrayRef(baseArrayLocal, index);
    }

    // if is multiArrayRef
    for (ProtoIlInstructions.IlInstructionMsg ind : instruction.getIndicesList()) {
      Value indExpr = CilInstructionFactory.fromInstructionMsg(ind, dotnetBody, cilBlock).jimplifyExpr(jb);
      Value index = indExpr instanceof Immediate ? indExpr : DotnetBodyVariableManager.inlineLocals(indExpr, jb);
      indices.add(index);
    }
    // only temporary - MultiArrayRef is rewritten later in in StLoc with the resolveRewriteMultiArrAccess instruction
    return Jimple.v().newArrayRef(baseArrayLocal, indices.get(0));
  }

  public boolean isMultiArrayRef() {
    return isMultiArrayRef;
  }

  public Value getBaseArrayLocal() {
    return baseArrayLocal;
  }

  public List<Value> getIndices() {
    return indices;
  }

  /**
   * Resolve Jimple Body, rollout array access dimension for dimension and return new rValue
   *
   * @param jb
   *          Jimple Body where to insert the rewritten statements
   */
  public Value resolveRewriteMultiArrAccess(Body jb) {
    // can only be >1
    int size = getIndices().size();

    Local lLocalVar;
    Local rLocalVar = null;
    for (int z = 0; z < size; z++) {
      ArrayRef arrayRef;
      if (z == 0) {
        arrayRef = Jimple.v().newArrayRef(getBaseArrayLocal(), getIndices().get(z));
      } else {
        arrayRef = Jimple.v().newArrayRef(rLocalVar, getIndices().get(z));
      }
      Type arrayType = arrayRef.getType();
      lLocalVar = dotnetBody.variableManager.localGenerator.generateLocal(arrayType);

      AssignStmt assignStmt = Jimple.v().newAssignStmt(lLocalVar, arrayRef);
      jb.getUnits().add(assignStmt);
      rLocalVar = lLocalVar;
    }
    return rLocalVar;
  }
}
