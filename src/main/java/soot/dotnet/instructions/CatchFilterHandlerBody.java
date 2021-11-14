package soot.dotnet.instructions;

import soot.Body;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.*;

import java.util.ArrayList;

/**
 * Represents a TryCatch handler with a filter
 * https://docs.microsoft.com/en-us/dotnet/standard/exceptions/using-user-filtered-exception-handlers
 */
public class CatchFilterHandlerBody {

    private final ProtoIlInstructions.IlTryCatchHandlerMsg handlerMsg;
    // Base Exception (System.Exception) which will throw and is assigned to this local
    private final Local exceptionVar;
    // statement where to jump is condition does not fulfill
    private final Unit nopStmtEnd;
    private final DotnetBody dotnetBody;

    public CatchFilterHandlerBody(DotnetBody dotnetBody, ProtoIlInstructions.IlTryCatchHandlerMsg handlerMsg, Local exceptionVar, Unit nopStmtEnd) {
        this.dotnetBody = dotnetBody;
        this.handlerMsg = handlerMsg;
        this.exceptionVar = exceptionVar;
        this.nopStmtEnd = nopStmtEnd;
    }

    public Body getFilterHandlerBody(Value generalExceptionVariable) {

        Body jb = new JimpleBody();

        // Exception object / class - add as identity stmt to jimple body

        AssignStmt assignStmt = Jimple.v().newAssignStmt(exceptionVar, generalExceptionVariable);
        jb.getUnits().add(assignStmt);

        NopStmt filterCondFalseNop = Jimple.v().newNopStmt(); // jump to end of handler
        CilBlockContainer handlerFilterContainerBlock = new CilBlockContainer(handlerMsg.getFilter(), dotnetBody);
        Body handlerFilterContainerBlockBody = handlerFilterContainerBlock.jimplify();
        CilBlockContainer handlerBlock = new CilBlockContainer(handlerMsg.getBody(), dotnetBody);
        Body handlerBody = handlerBlock.jimplify();

        // replace return stmts with if/goto to skip handler or execute
        ArrayList<Unit> tmpToInsert = new ArrayList<>();
        for (Unit unit : handlerFilterContainerBlockBody.getUnits())
            if (unit instanceof ReturnStmt)
                tmpToInsert.add(unit);
        // will only run once or not at all
        for (Unit returnStmt : tmpToInsert) {
            // get return value and check if 0
            Value returnValue = ((ReturnStmt) returnStmt).getOp();
            ConditionExpr cond = Jimple.v().newEqExpr(returnValue, IntConstant.v(0));
            IfStmt ifRetCondStmt = Jimple.v().newIfStmt(cond, filterCondFalseNop); // if ret==0 ignore handler
            // jump to end of filter instructions - cond true
            GotoStmt gotoHandlerBodyCondTrueStmt = Jimple.v().newGotoStmt(handlerBody.getUnits().getFirst());

            handlerFilterContainerBlockBody.getUnits().insertAfter(gotoHandlerBodyCondTrueStmt, returnStmt);
            handlerFilterContainerBlockBody.getUnits().swapWith(returnStmt, ifRetCondStmt);
            dotnetBody.blockEntryPointsManager.swapGotoEntryUnit(ifRetCondStmt, returnStmt);
        }
        jb.getUnits().addAll(handlerFilterContainerBlockBody.getUnits());

        // handler body
        if (lastStmtIsNotReturn(handlerBody)) {
            // if last stmt is not return, insert goto stmt, to go to end whole block
            handlerBody.getUnits().add(Jimple.v().newGotoStmt(nopStmtEnd));
        }
        jb.getLocals().addAll(handlerBody.getLocals());
        jb.getUnits().addAll(handlerBody.getUnits());
        jb.getTraps().addAll(handlerBody.getTraps());

        // add nop at the end of handler, where filter can jump to
        jb.getUnits().add(filterCondFalseNop);

        return jb;
    }

    private static boolean lastStmtIsNotReturn(Body jb) {
        return !isExitStmt(jb.getUnits().getLast());
    }

    /**
     * Check if given unit "exists a method"
     * @param unit
     * @return
     */
    private static boolean isExitStmt(Unit unit) {
        return unit instanceof ReturnStmt ||
                unit instanceof ReturnVoidStmt ||
                unit instanceof ThrowStmt;
    }
}
