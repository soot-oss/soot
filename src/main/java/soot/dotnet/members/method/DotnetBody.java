package soot.dotnet.members.method;

import soot.*;
import soot.dotnet.instructions.CilBlockContainer;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.*;

/**
 * Represents a .NET Method Body
 * A method body starts with a BlockContainer, which contains Blocks, which have IL Instructions
 * .NET Method Body (with ILSpy AST) -> BlockContainer -> Block -> IL Instruction
 */
public class DotnetBody {

    private final ProtoIlInstructions.IlFunctionMsg ilFunctionMsg;
    private JimpleBody jb;

    public BlockEntryPointsManager blockEntryPointsManager;
    public DotnetBodyVariableManager variableManager;

    /**
     * Get method signature of this method body
     * @return method signature
     */
    public DotnetMethod getDotnetMethodSig() {
        return dotnetMethodSig;
    }

    private final DotnetMethod dotnetMethodSig;

    public DotnetBody(DotnetMethod methodSignature, ProtoIlInstructions.IlFunctionMsg ilFunctionMsg) {
        this.dotnetMethodSig = methodSignature;
        this.ilFunctionMsg = ilFunctionMsg;
        blockEntryPointsManager = new BlockEntryPointsManager();
    }

    public void jimplify(JimpleBody jb) {
        this.jb = jb;
        variableManager = new DotnetBodyVariableManager(this, this.jb);

        // resolve initial variable assignments
        addThisStmt();
        variableManager.fillMethodParameter();
        variableManager.addInitLocalVariables(ilFunctionMsg.getVariablesList());

        // Resolve .NET Method Body -> BlockContainer -> Block -> IL Instruction
        CilBlockContainer blockContainer = new CilBlockContainer(ilFunctionMsg.getBody(), this);
        Body b = blockContainer.jimplify();
        this.jb.getUnits().addAll(b.getUnits());
        this.jb.getTraps().addAll(b.getTraps());
        blockEntryPointsManager.swapGotoEntriesInJBody(this.jb);
    }

    private void addThisStmt() {
        if (dotnetMethodSig.isStatic())
            return;
        RefType thisType = dotnetMethodSig.getDeclaringClass().getType();
        Local l = Jimple.v().newLocal("this", thisType);
        IdentityStmt identityStmt = Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef(thisType));
        this.jb.getLocals().add(l);
        this.jb.getUnits().add(identityStmt);
    }

    /**
     * Due to three address code, inline cast expr
     * @param v
     * @return
     */
    public static Value inlineCastExpr(Value v) {
        if (v instanceof Immediate)
            return v;
        if (v instanceof CastExpr)
            return inlineCastExpr(((CastExpr) v).getOp());
        return v;
    }

    public static JimpleBody getEmptyJimpleBody(SootMethod m) {
        JimpleBody b = Jimple.v().newBody(m);
        resolveEmptyJimpleBody(b, m);
        return b;
    }

    public static void resolveEmptyJimpleBody(JimpleBody b, SootMethod m) {
        // if not static add this stmt
        if (!m.isStatic()) {
            RefType thisType = m.getDeclaringClass().getType();
            Local l = Jimple.v().newLocal("this", thisType);
            IdentityStmt identityStmt = Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef(thisType));
            b.getLocals().add(l);
            b.getUnits().add(identityStmt);
        }
        // parameters
        for (int i = 0; i < m.getParameterCount(); i++) {
            Type parameterType = m.getParameterType(i);
            Local paramLocal = Jimple.v().newLocal("arg"+i, parameterType);
            b.getLocals().add(paramLocal);
            b.getUnits().add(Jimple.v().newIdentityStmt(paramLocal, Jimple.v().newParameterRef(parameterType, i)));
        }
        if (m.getReturnType() instanceof VoidType)
            b.getUnits().add(Jimple.v().newReturnVoidStmt());
        else if (m.getReturnType() instanceof PrimType)
            b.getUnits().add(Jimple.v().newReturnStmt(DotnetTypeFactory.initType(m.getReturnType())));
        else
            b.getUnits().add(Jimple.v().newReturnStmt(NullConstant.v()));
    }

}
