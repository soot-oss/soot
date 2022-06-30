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

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Steven Arzt
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
import soot.SootMethodRef;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.SpecialInvokeExpr;

public abstract class AbstractNewObjInstanceInstruction extends AbstractCilnstruction {
  public AbstractNewObjInstanceInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody,
      CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  protected SootMethodRef methodRef;
  protected List<Local> listOfArgs;

  public SootMethodRef getMethodRef() {
    return methodRef;
  }

  public List<Local> getListOfArgs() {
    return listOfArgs;
  }

  /**
   * Call the constructor of the instantiated object
   *
   * @param jb
   * @param variableObject
   */
  public void resolveCallConstructorBody(Body jb, Local variableObject) {
    // if new Obj also add call of constructor
    SpecialInvokeExpr specialInvokeExpr = Jimple.v().newSpecialInvokeExpr(variableObject, getMethodRef(), getListOfArgs());
    InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(specialInvokeExpr);
    jb.getUnits().add(invokeStmt);
  }
}
