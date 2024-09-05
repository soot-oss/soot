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
import soot.Local;
import soot.SootClass;
import soot.Type;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.ArrayByReferenceWrapperGenerator;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.proto.ProtoIlInstructions.IlInstructionMsg;
import soot.jimple.Jimple;
import soot.jimple.internal.JArrayRef;

/**
 * Load element out of an array local In ILSpy/.NET instruction an element can be loaded by one instruction (e.g. elem[1,5]);
 * unfolding it
 * 
 * This opcode loads an address, i.e. a pointer on a specific element
 */
public class CilLdElemaInstruction extends AbstractCilnstruction {
  private Local targetVar;

  public CilLdElemaInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock,
      Local variable) {
    super(instruction, dotnetBody, cilBlock);
    this.targetVar = variable;
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
      final IlInstructionMsg ind = instruction.getIndices(i);
      Value indExpr = CilInstructionFactory.fromInstructionMsg(ind, dotnetBody, cilBlock).jimplifyExpr(jb);
      Value index = simplifyComplexExpression(jb, indExpr);
      baseArrayLocal = simplifyComplexExpression(jb, Jimple.v().newArrayRef(baseArrayLocal, index));

    }

    //the last one is going to be a pointer!
    IlInstructionMsg last = instruction.getIndices(instruction.getIndicesCount() - 1);
    Value ind = CilInstructionFactory.fromInstructionMsg(last, dotnetBody, cilBlock).jimplifyExpr(jb);
    Value index = simplifyComplexExpression(jb, ind);
    //In the CilLdElemInstruction instruction, we would generate an arrayref 
    //Jimple.v().newArrayRef(baseArrayLocal, index);
    //however, this is a pointer, so we'll use a special generated class
    Type elemType = JArrayRef.getElementType(baseArrayLocal.getType());
    SootClass wc = ArrayByReferenceWrapperGenerator.getWrapperClass(elemType);
    Jimple j = Jimple.v();

    Local base = dotnetBody.variableManager.getReferenceLocal(targetVar);
    if (base == null) {
      base = (Local) simplifyComplexExpression(jb, j.newNewExpr(wc.getType()));
      dotnetBody.variableManager.addReferenceLocal(targetVar, (Local) base);
    }
    jb.getUnits()
        .add(j.newInvokeStmt(j.newSpecialInvokeExpr(base, wc.getMethodByName("<init>").makeRef(), baseArrayLocal, index)));
    return base;
  }

}
