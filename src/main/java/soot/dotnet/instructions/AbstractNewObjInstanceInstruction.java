package soot.dotnet.instructions;

import soot.Body;
import soot.Local;
import soot.SootMethodRef;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.SpecialInvokeExpr;

import java.util.List;

public abstract class AbstractNewObjInstanceInstruction extends AbstractCilnstruction {
    public AbstractNewObjInstanceInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    protected SootMethodRef methodRef;
    protected List<Local> listOfArgs;

    public SootMethodRef getMethodRef() {
        return methodRef;
    }

    public List<Local> getListOfArgs() {
        return listOfArgs;
    }

    /**
     * Call the constructor of the instantiated object
     * @param jb
     * @param variableObject
     */
    public void resolveCallConstructorBody(Body jb, Local variableObject) {
        // if new Obj also add call of constructor
        SpecialInvokeExpr specialInvokeExpr = Jimple.v().newSpecialInvokeExpr(
                variableObject,
                getMethodRef(),
                getListOfArgs()
        );
        InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(specialInvokeExpr);
        jb.getUnits().add(invokeStmt);
    }
}
