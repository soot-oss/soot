package soot.dotnet.instructions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import soot.Scene;
import soot.SootClass;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotNetBasicTypes;
import soot.jimple.GotoStmt;
import soot.jimple.Jimple;
import soot.jimple.NopStmt;

/**
 *
 */
public class CilTryCatchInstruction extends AbstractCilnstruction {
  public CilTryCatchInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody,
      CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    List<Unit> nopsToReplaceWithGoto = new ArrayList<>();
    final SootClass exceptionClass = Scene.v().getSootClass(DotNetBasicTypes.SYSTEM_EXCEPTION);
    NopStmt gotoEndTryCatchBlockNop = Jimple.v().newNopStmt();

    // try block
    CilBlockContainer tryContainer
        = new CilBlockContainer(instruction.getTryBlock(), dotnetBody, CilBlockContainer.BlockContainerKind.TRY);
    Body tryContainerBlock = tryContainer.jimplify();
    if (CilBlockContainer.LastStmtIsNotReturn(tryContainerBlock)) {
      // if last stmt is not return, insert goto stmt
      NopStmt nopStmt = Jimple.v().newNopStmt();
      tryContainerBlock.getUnits().add(nopStmt);
      nopsToReplaceWithGoto.add(nopStmt);
    }
    jb.getLocals().addAll(tryContainerBlock.getLocals());
    jb.getUnits().addAll(tryContainerBlock.getUnits());
    jb.getTraps().addAll(tryContainerBlock.getTraps());

    // add boilerplate code, due to blocks in CIL and no blocks in Jimple
    // add block - after exception not caught in catch block
    // generate new local with caught exception ref - add to jb afterwards due to traps
    Local uncaughtExceptionVar = dotnetBody.variableManager.localGenerator.generateLocal(exceptionClass.getType());
    Unit exceptionIdentityStmt = Jimple.v().newIdentityStmt(uncaughtExceptionVar, Jimple.v().newCaughtExceptionRef());

    // handlers
    List<ProtoIlInstructions.IlTryCatchHandlerMsg> protoHandlersList = instruction.getHandlersList();

    List<CatchFilterHandlerBody> handlersWithFilterList = new ArrayList<>();
    List<CatchHandlerBody> handlersList = new ArrayList<>();
    CatchHandlerBody systemExceptionHandler = null;

    for (ProtoIlInstructions.IlTryCatchHandlerMsg handlerMsg : protoHandlersList) {

      // Exception object / class - add as identity stmt to jimple body
      Local exceptionVar = dotnetBody.variableManager.addOrGetVariable(handlerMsg.getVariable(), jb);

      if (handlerMsg.getHasFilter()) {
        CatchFilterHandlerBody filterHandler
            = new CatchFilterHandlerBody(dotnetBody, handlerMsg, exceptionVar, gotoEndTryCatchBlockNop);
        handlersWithFilterList.add(filterHandler);
        continue;
      }

      CatchHandlerBody handler = new CatchHandlerBody(exceptionVar, handlerMsg, dotnetBody, tryContainerBlock,
          exceptionIdentityStmt, nopsToReplaceWithGoto);
      handlersList.add(handler);

      if (handlerMsg.getVariable().getType().getFullname().equals(DotNetBasicTypes.SYSTEM_EXCEPTION)) {
        systemExceptionHandler = handler;
      }
    }

    for (CatchHandlerBody handlerBody : handlersList) {
      Body body = handlerBody.getBody();
      if (handlerBody == systemExceptionHandler) {
        Map<Trap, Unit> tmpTrapEnds = new HashMap<>();
        for (Trap trap : body.getTraps()) {
          tmpTrapEnds.put(trap, trap.getEndUnit());
        }
        for (CatchFilterHandlerBody filterHandler : handlersWithFilterList) {
          Local eVar = systemExceptionHandler.getExceptionVariable();
          Body filterHandlerBody = filterHandler.getFilterHandlerBody(eVar);

          body.getUnits().insertAfter(filterHandlerBody.getUnits(), body.getUnits().getFirst());
          body.getTraps().addAll(filterHandlerBody.getTraps());
        }
        for (Map.Entry<Trap, Unit> trapMap : tmpTrapEnds.entrySet()) {
          trapMap.getKey().setEndUnit(trapMap.getValue());
        }
      }
      jb.getUnits().addAll(body.getUnits());
      jb.getTraps().addAll(body.getTraps());
    }

    // If System.Exception catch not declared, add this trap, if any other exception then declared one was thrown
    if (systemExceptionHandler == null) {
      jb.getTraps().add(Jimple.v().newTrap(exceptionClass, tryContainerBlock.getUnits().getFirst(),
          tryContainerBlock.getUnits().getLast(), exceptionIdentityStmt));
    }

    // --- add boilerplate code (uncaught exceptions in catch) to jimple body: add identity and throw stmt

    jb.getUnits().add(exceptionIdentityStmt);
    // TryFilter
    if (systemExceptionHandler == null) {
      for (CatchFilterHandlerBody filterHandler : handlersWithFilterList) {
        Body filterHandlerBody = filterHandler.getFilterHandlerBody(uncaughtExceptionVar);

        jb.getUnits().addAll(filterHandlerBody.getUnits());
        jb.getTraps().addAll(filterHandlerBody.getTraps());
      }
    }
    jb.getUnits().add(Jimple.v().newThrowStmt(uncaughtExceptionVar));

    // add nop for goto end of try/catch block
    jb.getUnits().add(gotoEndTryCatchBlockNop);
    for (Unit nop : nopsToReplaceWithGoto) {
      GotoStmt gotoStmt = Jimple.v().newGotoStmt(gotoEndTryCatchBlockNop);
      jb.getUnits().swapWith(nop, gotoStmt);
    }
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    throw new NoExpressionInstructionException(instruction);
  }
}
