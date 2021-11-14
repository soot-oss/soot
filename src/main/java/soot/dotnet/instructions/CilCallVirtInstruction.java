package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.members.AbstractDotnetMember;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetBasicTypes;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.toolkits.scalar.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Invoking methods
 */
public class CilCallVirtInstruction extends AbstractCilnstruction {

    public CilCallVirtInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
        localsToCastForCall = new ArrayList<>();
    }

    @Override
    public void jimplify(Body jb) {
        CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction, dotnetBody, cilBlock);
        Value value = cilExpr.jimplifyExpr(jb);
        InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(value);
        jb.getUnits().add(invokeStmt);
    }

    /**
     * Call Expression
     * @param jb
     * @return
     */
    @Override
    public Value jimplifyExpr(Body jb) {

        SootClass clazz = Scene.v().getSootClass(instruction.getMethod().getDeclaringType().getFullname());
        // STATIC
        if (instruction.getMethod().getIsStatic()) {
            String methodName = instruction.getMethod().getName();
            if (methodName.trim().isEmpty())
                throw new RuntimeException("Opcode: " + instruction.getOpCode() + ": Given method " + instruction.getMethod().getName() + " of declared type " +
                        instruction.getMethod().getDeclaringType().getFullname() +
                        " has no method name!");
            List<Local> argsVariables = new ArrayList<>();
            List<Type> argsTypes = new ArrayList<>();

            // If System.Array.Empty
            Value rewriteField = AbstractDotnetMember.checkRewriteCilSpecificMember(clazz, methodName);
            if (rewriteField != null)
                return rewriteField;

            // arguments which are passed to this function
            for (int z = 0; z < instruction.getArgumentsCount(); z++) {
                Local variable = (Local) CilInstructionFactory.fromInstructionMsg(instruction.getArguments(z), dotnetBody, cilBlock).jimplifyExpr(jb);
                argsVariables.add(variable);
            }
            // method-parameters (signature)
            for (ProtoAssemblyAllTypes.ParameterDefinition parameterDefinition : instruction.getMethod().getParameterList())
                argsTypes.add(DotnetTypeFactory.toSootType(parameterDefinition.getType()));

            // rename invoked method if has generics as params
            if (DotnetMethod.hasGenericRefParameters(instruction.getMethod().getParameterList()))
                methodName = DotnetMethod.convertGenRefMethodName(methodName, instruction.getMethod().getParameterList());

            SootMethodRef methodRef = Scene.v().makeMethodRef(clazz, DotnetMethod.convertCtorName(methodName), argsTypes,
                    DotnetTypeFactory.toSootType(instruction.getMethod().getReturnType()), true);
            return Jimple.v().newStaticInvokeExpr(methodRef, argsVariables);
        }
        // INTERFACE
        else if(clazz.isInterface()) {
            MethodParams methodParams = getMethodCallParams(jb);
            return Jimple.v().newInterfaceInvokeExpr(
                    methodParams.Base,
                    methodParams.MethodRef,
                    methodParams.ArgumentVariables
            );
        }
        // CONSTRUCTOR and PRIVATE METHOD CALLS
        else if(instruction.getMethod().getIsConstructor()
                || instruction.getMethod().getAccessibility().equals(ProtoAssemblyAllTypes.Accessibility.PRIVATE)) {
            MethodParams methodParams = getMethodCallParams(jb);
            return Jimple.v().newSpecialInvokeExpr(
                    methodParams.Base,
                    methodParams.MethodRef,
                    methodParams.ArgumentVariables
            );
        }
        // DYNAMIC OBJECT METHOD
        else {
            MethodParams methodParams = getMethodCallParams(jb);
            return Jimple.v().newVirtualInvokeExpr(
                    methodParams.Base,
                    methodParams.MethodRef,
                    methodParams.ArgumentVariables
            );
        }
    }

    public List<Pair<Local, Local>> getLocalsToCastForCall() {
        return localsToCastForCall;
    }

    private final List<Pair<Local, Local>> localsToCastForCall;

    private MethodParams getMethodCallParams(Body jb) {
        SootClass clazz = Scene.v().getSootClass(instruction.getMethod().getDeclaringType().getFullname());
        String methodName = instruction.getMethod().getName();
        if (methodName.trim().isEmpty())
            throw new RuntimeException("Opcode: " + instruction.getOpCode() + ": Given method " + instruction.getMethod().getName() + " of declared type " +
        instruction.getMethod().getDeclaringType().getFullname() +
                " has no method name!");
        List<Local> argsVariables = new ArrayList<>();
        List<Type> methodParamTypes = new ArrayList<>();
        if (instruction.getArgumentsCount() == 0)
            throw new RuntimeException("Opcode: " + instruction.getOpCode() + ": Given method " + instruction.getMethod().getName() + " of declared type " +
                    instruction.getMethod().getDeclaringType().getFullname() +
                    " has no arguments! This means there is no base variable for the virtual invoke!");
        Local base = (Local) CilInstructionFactory.fromInstructionMsg(instruction.getArguments(0), dotnetBody, cilBlock).jimplifyExpr(jb);
        if (instruction.getArgumentsCount() > 1) {
            for (int z = 1; z < instruction.getArgumentsCount(); z++) {
                Local variable = (Local) CilInstructionFactory.fromInstructionMsg(instruction.getArguments(z), dotnetBody, cilBlock).jimplifyExpr(jb);
                argsVariables.add(variable);
            }
            for (ProtoAssemblyAllTypes.ParameterDefinition parameterDefinition : instruction.getMethod().getParameterList())
                methodParamTypes.add(DotnetTypeFactory.toSootType(parameterDefinition.getType()));
        }

        // Check if cast is needed for correct validation, e.g.:
        // System.Object modifiers = null;
        // constructor = virtualinvoke type.<System.Type: ConstructorInfo GetConstructor(System.Reflection.ParameterModifier[])>(modifiers);
        for (int i = 0; i < argsVariables.size(); i++) {
            Local arg = argsVariables.get(i);
            Type methodParam = methodParamTypes.get(i);
            if (arg.getType().toString().equals(DotnetBasicTypes.SYSTEM_OBJECT) &&
                    !methodParam.toString().equals(DotnetBasicTypes.SYSTEM_OBJECT)) {
                Local castLocal = dotnetBody.variableManager.localGenerator.generateLocal(methodParam);
                localsToCastForCall.add(new Pair<>(arg, castLocal));
                argsVariables.set(i, castLocal);
            }
        }

        // rename invoked method if has generics as params
        if (DotnetMethod.hasGenericRefParameters(instruction.getMethod().getParameterList()))
            methodName = DotnetMethod.convertGenRefMethodName(methodName, instruction.getMethod().getParameterList());

        // return type of the method
        // due to unsafe methods with "void*" rewrite this
        final ProtoAssemblyAllTypes.MethodDefinition protoMethod = instruction.getMethod();
        Type return_type;
        if (protoMethod.getReturnType().getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.POINTER)
                && protoMethod.getReturnType().getFullname().equals(DotnetBasicTypes.SYSTEM_VOID))
            return_type = DotnetTypeFactory.toSootType(protoMethod.getDeclaringType());
        else
            return_type = DotnetTypeFactory.toSootType(instruction.getMethod().getReturnType());

        SootMethodRef methodRef = Scene.v().makeMethodRef(
                clazz,
                DotnetMethod.convertCtorName(methodName),
                methodParamTypes,
                return_type, false);

        return new MethodParams(base, methodRef, argsVariables);
    }

    private static class MethodParams {
        public MethodParams(Local base, SootMethodRef methodRef, List<Local> argumentVariables) {
            Base = base;
            MethodRef = methodRef;
            ArgumentVariables = argumentVariables;
        }

        public Local Base;
        public SootMethodRef MethodRef;
        public List<Local> ArgumentVariables;
    }
}
