package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.soot.DotnetClassConstant;
import soot.dotnet.types.DotnetBasicTypes;
import soot.jimple.Jimple;

import java.util.Collections;

/**
 * Return size of given object/type
 */
public class CilSizeOfInstruction extends AbstractCilnstruction {
    public CilSizeOfInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        String typeName = instruction.getType().getFullname();

//        // generate dummy local with ClassConstant - may not needed
//        LocalGenerator localGenerator = new LocalGenerator(jb);
//        Local tmpLocalVar = localGenerator.generateLocal(RefType.v("System.Object"));
//        Unit stmt = Jimple.v().newAssignStmt(tmpLocalVar, DotnetClassConstant.v(typeName));
//        jb.getUnits().add(stmt);

        SootClass clazz = Scene.v().getSootClass(DotnetBasicTypes.SYSTEM_RUNTIME_INTEROPSERVICES_MARSHAL);
        // SootMethod method = clazz.getMethod("SizeOf", Collections.singletonList(Scene.v().getRefType(DotnetBasicTypes.SYSTEM_OBJECT)));
        SootMethodRef methodRef = Scene.v().makeMethodRef(clazz, "SizeOf",
                Collections.singletonList(Scene.v().getRefType(DotnetBasicTypes.SYSTEM_OBJECT)), IntType.v(), true);
        return Jimple.v().newStaticInvokeExpr(methodRef, DotnetClassConstant.v(typeName));
    }
}
