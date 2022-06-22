package soot.dotnet.exceptions;

import soot.dotnet.proto.ProtoIlInstructions;

public class NoExpressionInstructionException extends RuntimeException{
    public NoExpressionInstructionException() {
        super("This instruction does not have expressions!");
    }

    public NoExpressionInstructionException(String className, String methodName, String instructionName) {
        super("In class " + className + " at method " + methodName + ": The instruction " + instructionName + " does not have expressions!");
    }

    public NoExpressionInstructionException(ProtoIlInstructions.IlInstructionMsg instructionMsg) {
        super("CilInstruction " + instructionMsg.getOpCode().name() + " does not have expressions!");
    }
}
