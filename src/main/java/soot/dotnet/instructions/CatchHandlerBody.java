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
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.Trap;
import soot.Unit;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotNetBasicTypes;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NopStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ThrowStmt;

/**
 * Represents a TryCatch handler
 */
public class CatchHandlerBody {

  // variable which contains thrown exception
  private final Local exceptionVariable;
  // method body of this handler
  private final ProtoIlInstructions.IlTryCatchHandlerMsg handlerMsg;
  private final DotnetBody dotnetBody;
  private final SootClass exceptionClass = Scene.v().getSootClass(DotNetBasicTypes.SYSTEM_EXCEPTION);
  // Jimple Body of TryCatch Try part
  private final Body tryBody;
  private final Unit exceptionIdentityStmt;
  private final List<Unit> nopsToReplaceWithGoto;

  public CatchHandlerBody(Local exceptionVariable, ProtoIlInstructions.IlTryCatchHandlerMsg handlerMsg,
      DotnetBody dotnetBody, Body tryBody, Unit exceptionIdentityStmt, List<Unit> nopsToReplaceWithGoto) {
    this.exceptionVariable = exceptionVariable;
    this.handlerMsg = handlerMsg;
    this.dotnetBody = dotnetBody;
    this.tryBody = tryBody;
    this.exceptionIdentityStmt = exceptionIdentityStmt;
    this.nopsToReplaceWithGoto = nopsToReplaceWithGoto;
  }

  public Local getExceptionVariable() {
    return exceptionVariable;
  }

  public Body getBody() {
    Body jb = new JimpleBody();

    // handler body
    Unit excStmt = Jimple.v().newIdentityStmt(exceptionVariable, Jimple.v().newCaughtExceptionRef());
    jb.getUnits().add(excStmt);
    CilBlockContainer handlerBlock
        = new CilBlockContainer(handlerMsg.getBody(), dotnetBody, CilBlockContainer.BlockContainerKind.CATCH_HANDLER);
    Body handlerBody = handlerBlock.jimplify();
    if (lastStmtIsNotReturn(handlerBody)) {
      // if last stmt is not return, insert goto stmt
      NopStmt nopStmt = Jimple.v().newNopStmt();
      handlerBody.getUnits().add(nopStmt);
      nopsToReplaceWithGoto.add(nopStmt);
    }
    jb.getLocals().addAll(handlerBody.getLocals());
    jb.getUnits().addAll(handlerBody.getUnits());
    jb.getTraps().addAll(handlerBody.getTraps());

    Trap trap = Jimple.v().newTrap(Scene.v().getSootClass(exceptionVariable.getType().toString()),
        tryBody.getUnits().getFirst(), tryBody.getUnits().getLast(), excStmt);
    jb.getTraps().add(trap);

    // Add trap for exception in catch blocks
    Trap trapCatchThrow
        = Jimple.v().newTrap(exceptionClass, excStmt, handlerBody.getUnits().getLast(), exceptionIdentityStmt);
    jb.getTraps().add(trapCatchThrow);

    return jb;
  }

  private boolean lastStmtIsNotReturn(Body handlerBody) {
    if (handlerBody.getUnits().size() == 0) {
      return true;
    }
    return !isExitStmt(handlerBody.getUnits().getLast());
  }

  private static boolean isExitStmt(Unit unit) {
    return unit instanceof ReturnStmt || unit instanceof ReturnVoidStmt || unit instanceof ThrowStmt;
  }
}
