package soot.dotnet.instructions;

import soot.Body;
import soot.Immediate;
import soot.Local;
import soot.Type;
import soot.Value;
import soot.ValueBox;

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

import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.StaticFieldRef;

public abstract class AbstractCilnstruction implements CilInstruction {

  protected final ProtoIlInstructions.IlInstructionMsg instruction;
  protected final DotnetBody dotnetBody;
  protected final CilBlock cilBlock;

  public AbstractCilnstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    this.instruction = instruction;
    this.dotnetBody = dotnetBody;
    this.cilBlock = cilBlock;
  }

  protected Value simplifyComplexExpression(Body jb, Value var) {
    if (var instanceof Immediate) {
      return var;
    }
    final Jimple jimple = Jimple.v();
    if (var instanceof InvokeExpr) {
      InvokeExpr inv = (InvokeExpr) var;
      for (int arg = 0; arg < inv.getArgCount(); arg++) {
        ValueBox argBox = inv.getArgBox(arg);
        argBox.setValue(simplifyComplexExpression(jb, argBox.getValue()));
      }
      if (var instanceof InstanceInvokeExpr) {
        Value base = ((InstanceInvokeExpr) var).getBase();
        if (!(base instanceof Local)) {
          // we have to see how the order plays out: Does a complex argument or base object get evaluated first?
          throw new RuntimeException("Non-local base value currently not supported");
        }
      }
      return createTempVar(jb, jimple, inv);
    } else if (var instanceof StaticFieldRef) {
      return createTempVar(jb, jimple, var);
    } else if (var instanceof InstanceFieldRef) {
      Value base = ((InstanceFieldRef) var).getBase();
      if (!(base instanceof Local)) {
        base = simplifyComplexExpression(jb, base);
        ((InstanceFieldRef) var).setBase(base);
      }
      return createTempVar(jb, jimple, var);
    } else {
      for (ValueBox i : var.getUseBoxes()) {
        i.setValue(simplifyComplexExpression(jb, i.getValue()));
      }
      return createTempVar(jb, jimple, var);
    }
  }

  protected Local createTempVar(Body jb, final Jimple jimple, Value inv) {
    Local interimLocal = dotnetBody.variableManager.localGenerator.generateLocal(inv.getType());
    jb.getLocals().add(interimLocal);
    jb.getUnits().add(jimple.newAssignStmt(interimLocal, inv));
    return interimLocal;
  }

  protected Local createTempVar(Body jb, Type type) {
    Local interimLocal = dotnetBody.variableManager.localGenerator.generateLocal(type);
    jb.getLocals().add(interimLocal);
    return interimLocal;
  }

}
