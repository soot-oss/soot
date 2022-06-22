package soot.dotnet.instructions;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;

import java.util.ArrayList;

/**
 * Structs (System.ValueType) instantiation opcode
 */
public class CilDefaultValueInstruction extends AbstractNewObjInstanceInstruction {
    public CilDefaultValueInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        // RefType refType = RefType.v(i.getType().getFullname());
        // return Jimple.v().newNewExpr(refType);

        SootClass clazz = Scene.v().getSootClass(instruction.getType().getFullname());
        NewExpr newExpr = Jimple.v().newNewExpr(clazz.getType());

        methodRef = Scene.v().makeConstructorRef(clazz, new ArrayList<>());
        listOfArgs = new ArrayList<>();

        return newExpr;
    }

}
