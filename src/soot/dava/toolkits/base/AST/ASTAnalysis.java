package soot.dava.toolkits.base.AST;

import soot.dava.internal.AST.*;

public abstract class ASTAnalysis
{
    public static boolean modified;

    public abstract void analyse( ASTNode n);
}
