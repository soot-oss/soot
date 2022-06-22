package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetBasicTypes;
import soot.jimple.*;

/**
 * Check for finite value otherwise infinity exception instruction
 */
public class CilCkFiniteInstruction extends AbstractCilnstruction {
    public CilCkFiniteInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getArgument(), dotnetBody, cilBlock);
        Value argument = cilExpr.jimplifyExpr(jb);
        DoubleConstant posInfinity = DoubleConstant.v(Double.POSITIVE_INFINITY);
        DoubleConstant negInfinity = DoubleConstant.v(Double.NEGATIVE_INFINITY);

        // infinity conditions
        EqExpr eqPosInfExpr = Jimple.v().newEqExpr(argument, posInfinity);
        EqExpr eqNegInfExpr = Jimple.v().newEqExpr(argument, negInfinity);
        NeExpr eqNaNExpr = Jimple.v().newNeExpr(argument, argument);

        // if value is infinity, throw exception
        SootClass exceptionClass = Scene.v().getSootClass(DotnetBasicTypes.SYSTEM_ARITHMETICEXCEPTION);
        Local tmpLocalVar = dotnetBody.variableManager.localGenerator.generateLocal(exceptionClass.getType());
        ThrowStmt throwStmt = Jimple.v().newThrowStmt(tmpLocalVar);

        // if true throw exception
        jb.getUnits().add(Jimple.v().newIfStmt(eqPosInfExpr, throwStmt));
        jb.getUnits().add(Jimple.v().newIfStmt(eqNegInfExpr, throwStmt));
        jb.getUnits().add(Jimple.v().newIfStmt(eqNaNExpr, throwStmt));
        // if false goto
        NopStmt nopStmt = Jimple.v().newNopStmt();
        jb.getUnits().add(Jimple.v().newGotoStmt(nopStmt));
        jb.getUnits().add(throwStmt);
        jb.getUnits().add(nopStmt);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        throw new NoExpressionInstructionException(instruction);
    }
}
