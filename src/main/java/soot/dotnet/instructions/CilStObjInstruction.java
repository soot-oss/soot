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
import soot.Value;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetBasicTypes;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.Expr;
import soot.jimple.Jimple;
import soot.jimple.internal.JStaticInvokeExpr;

/**
 * AssignStmt - Store ValueTypes to a local
 */
public class CilStObjInstruction extends AbstractCilnstruction {
  public CilStObjInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    CilInstruction cilTargetExpr = CilInstructionFactory.fromInstructionMsg(instruction.getTarget(), dotnetBody, cilBlock);
    Value target = cilTargetExpr.jimplifyExpr(jb);
    CilInstruction cilExpr
        = CilInstructionFactory.fromInstructionMsg(instruction.getValueInstruction(), dotnetBody, cilBlock);
    Value value = cilExpr.jimplifyExpr(jb);

    // create this cast, to validate successfully
    if (value instanceof Local && !target.getType().toString().equals(value.getType().toString())) {
      if (value.getType().toString().equals(DotnetBasicTypes.SYSTEM_OBJECT)
          && !target.getType().toString().equals(DotnetBasicTypes.SYSTEM_OBJECT)) {
        value = Jimple.v().newCastExpr(value, target.getType());
      }
    }

    // if new Obj also add call of constructor - relevant for structs (System.ValueType)
    if (cilExpr instanceof AbstractNewObjInstanceInstruction) {      
      if (target instanceof Local) {
    	  jb.getUnits().add(Jimple.v().newAssignStmt(target, value));
       	  ((AbstractNewObjInstanceInstruction) cilExpr).resolveCallConstructorBody(jb, (Local) target);
      } else {   
    	  Local local = dotnetBody.variableManager.localGenerator.generateLocal(target.getType());
    	  jb.getUnits().add(Jimple.v().newAssignStmt(local, value));
    	  ((AbstractNewObjInstanceInstruction) cilExpr).resolveCallConstructorBody(jb, local);
    	  jb.getUnits().add(Jimple.v().newAssignStmt(target, local));  
  	  }
      return;
    }
    
    // if rvalue is not single value and lvalue is also no local you need made a temp war
    if (!(target instanceof Local)) {
      value = dotnetBody.variableManager.simplifyIfNotPrimitiveWithLocal(value);
    }

    jb.getUnits().add(Jimple.v().newAssignStmt(target, value));

   
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    throw new NoExpressionInstructionException(instruction);
  }
}
