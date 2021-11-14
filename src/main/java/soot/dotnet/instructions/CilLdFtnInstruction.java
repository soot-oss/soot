package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetBasicTypes;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.Jimple;

import java.util.ArrayList;
import java.util.List;

/**
 * Create Fake stub for LdFtn (load function), cannot be represented in Jimple
 */
public class CilLdFtnInstruction extends AbstractCilnstruction {

    public CilLdFtnInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    /**
     * Call Expression
     * @param jb
     * @return
     */
    @Override
    public Value jimplifyExpr(Body jb) {

        SootClass clazz = Scene.v().getSootClass(DotnetBasicTypes.FAKE_LDFTN);

        // Define static fake method
        String methodName = "FakeLdFtn";
        // arguments which are passed to this function
        List<Local> argsVariables = new ArrayList<>();
        // method-parameters (signature)
        List<Type> methodParams = new ArrayList<>();

        SootMethodRef methodRef = Scene.v().makeMethodRef(clazz, methodName, methodParams,
                DotnetTypeFactory.toSootType(DotnetBasicTypes.SYSTEM_INTPTR), true);
        return Jimple.v().newStaticInvokeExpr(methodRef, argsVariables);
    }

}
