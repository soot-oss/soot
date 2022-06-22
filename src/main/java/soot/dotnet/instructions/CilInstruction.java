package soot.dotnet.instructions;

import soot.Body;
import soot.Value;
import soot.dotnet.exceptions.NoExpressionInstructionException;

/**
 * Base CIL Instruction
 */
public interface CilInstruction {
    /**
     * Jimplify a starting statement of ILSpy AST
     * @param jb
     */
    void jimplify(Body jb);

    /**
     * Jimplify an expression of ILSpy AST statement children
     * @param jb
     * @return
     */
    Value jimplifyExpr(Body jb);

}
