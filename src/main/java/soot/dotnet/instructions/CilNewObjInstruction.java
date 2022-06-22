package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;

import java.util.ArrayList;

/**
 * Combi instruction with instantiating a new object and calling the constructor (no structs often)
 * Call resolveCallConstructorBody() afterwards in StLoc
 */
public class CilNewObjInstruction extends AbstractNewObjInstanceInstruction {
    public CilNewObjInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        if (!instruction.hasMethod())
            throw new RuntimeException("NewObj: There is no method information in the method definiton!");
        SootClass clazz = Scene.v().getSootClass(instruction.getMethod().getDeclaringType().getFullname());
        NewExpr newExpr = Jimple.v().newNewExpr(clazz.getType());

        ArrayList<Local> argsVariables = new ArrayList<>();
        ArrayList<Type> argsTypes = new ArrayList<>();
        for (ProtoIlInstructions.IlInstructionMsg a : instruction.getArgumentsList()) {
            argsVariables.add(dotnetBody.variableManager.addOrGetVariable(a.getVariable(), jb));
            argsTypes.add(DotnetTypeFactory.toSootType(a.getVariable().getType().getFullname()));
        }

        // Constructor call expression
        methodRef = Scene.v().makeConstructorRef(clazz, argsTypes);
        listOfArgs = argsVariables;

        return newExpr;
    }
}
