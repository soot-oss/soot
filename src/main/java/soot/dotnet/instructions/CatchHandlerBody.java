package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetBasicTypes;
import soot.jimple.*;

import java.util.List;

/**
 * Represents a TryCatch handler
 */
public class CatchHandlerBody {

    // variable which contains thrown exception
    private final Local exceptionVariable;
    // method body of this handler
    private final ProtoIlInstructions.IlTryCatchHandlerMsg handlerMsg;
    private final DotnetBody dotnetBody;
    private final SootClass exceptionClass = Scene.v().getSootClass(DotnetBasicTypes.SYSTEM_EXCEPTION);
    // Jimple Body of TryCatch Try part
    private final Body tryBody;
    private final Unit exceptionIdentityStmt;
    private final List<Unit> nopsToReplaceWithGoto;


    public CatchHandlerBody(Local exceptionVariable, ProtoIlInstructions.IlTryCatchHandlerMsg handlerMsg, DotnetBody dotnetBody, Body tryBody, Unit exceptionIdentityStmt, List<Unit> nopsToReplaceWithGoto) {
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
        CilBlockContainer handlerBlock = new CilBlockContainer(handlerMsg.getBody(), dotnetBody);
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
                tryBody.getUnits().getFirst(),
                tryBody.getUnits().getLast(),
                excStmt);
        jb.getTraps().add(trap);

        // Add trap for exception in catch blocks
        Trap trapCatchThrow = Jimple.v().newTrap(exceptionClass,
                excStmt,
                handlerBody.getUnits().getLast(),
                exceptionIdentityStmt);
        jb.getTraps().add(trapCatchThrow);

        return jb;
    }

    private boolean lastStmtIsNotReturn(Body handlerBody) {
        if (handlerBody.getUnits().size() == 0)
            return true;
        return !isExitStmt(handlerBody.getUnits().getLast());
    }

    private static boolean isExitStmt(Unit unit) {
        return unit instanceof ReturnStmt ||
                unit instanceof ReturnVoidStmt ||
                unit instanceof ThrowStmt;
    }
}
