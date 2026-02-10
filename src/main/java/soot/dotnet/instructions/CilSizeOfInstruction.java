package soot.dotnet.instructions;

import java.util.Collections;

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
import soot.IntType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethodRef;
import soot.Value;
import soot.dotnet.DotnetClassConstant;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotNetBasicTypes;
import soot.jimple.Jimple;

/**
 * Return size of given object/type
 */
public class CilSizeOfInstruction extends AbstractCilnstruction {
  public CilSizeOfInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    throw new NoStatementInstructionException(instruction);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    String typeName = instruction.getType().getFullname();

    // // generate dummy local with ClassConstant - may not needed
    // LocalGenerator localGenerator = new LocalGenerator(jb);
    // Local tmpLocalVar = localGenerator.generateLocal(RefType.v("System.Object"));
    // Unit stmt = Jimple.v().newAssignStmt(tmpLocalVar, DotnetClassConstant.v(typeName));
    // jb.getUnits().add(stmt);

    SootClass clazz = Scene.v().getSootClass(DotNetBasicTypes.SYSTEM_RUNTIME_INTEROPSERVICES_MARSHAL);
    // SootMethod method = clazz.getMethod("SizeOf",
    // Collections.singletonList(Scene.v().getRefType(DotnetBasicTypes.SYSTEM_OBJECT)));
    SootMethodRef methodRef = Scene.v().makeMethodRef(clazz, "SizeOf",
        Collections.singletonList(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OBJECT)), IntType.v(), true);
    return Jimple.v().newStaticInvokeExpr(methodRef, DotnetClassConstant.v(typeName));
  }
}
