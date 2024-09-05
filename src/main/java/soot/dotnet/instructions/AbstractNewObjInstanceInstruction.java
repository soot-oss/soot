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

import java.util.List;

import soot.Body;
import soot.SootClass;
import soot.SootMethodRef;
import soot.Value;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.SpecialInvokeExpr;

public abstract class AbstractNewObjInstanceInstruction extends CilCallInstruction {
  public AbstractNewObjInstanceInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody,
      CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  protected SootMethodRef methodRef;
  protected List<Value> listOfArgs;

  public SootMethodRef getMethodRef() {
    return methodRef;
  }

  public List<Value> getListOfArgs() {
    return listOfArgs;
  }

  @Override
  protected InvokeExpr createInvokeExpr(Body jb, SootClass clazz, DotnetMethod method, MethodParams methodParams) {
    SpecialInvokeExpr specialInvokeExpr
        = Jimple.v().newSpecialInvokeExpr(methodParams.base, getMethodRef(), getListOfArgs());
    return specialInvokeExpr;
  }
}
