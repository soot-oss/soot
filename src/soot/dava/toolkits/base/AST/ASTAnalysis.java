package soot.dava.toolkits.base.AST;

import soot.*;
import soot.jimple.*;
import soot.dava.internal.AST.*;

public abstract class ASTAnalysis
{
    public static final int

	ANALYSE_AST    = 0,
	ANALYSE_STMTS  = 1,
	ANALYSE_VALUES = 2;


    public abstract int getAnalysisDepth();

    public void analyseASTNode( ASTNode n)
    {
    }
    public void analyseDefinitionStmt( DefinitionStmt s)
    {
    }
    public void analyseReturnStmt( ReturnStmt s)
    {
    }
    public void analyseInvokeStmt( InvokeStmt s)
    {
    }
    public void analyseThrowStmt( ThrowStmt s)
    {
    }
    public void analyseStmt( Stmt s)
    {
    }
    public void analyseBinopExpr( BinopExpr v)
    {
    }
    public void analyseUnopExpr( UnopExpr v)
    {
    }
    public void analyseNewArrayExpr( NewArrayExpr v)
    {
    }
    public void analyseNewMultiArrayExpr( NewMultiArrayExpr v)
    {
    }
    public void analyseInstanceOfExpr( InstanceOfExpr v)
    {
    }
    public void analyseInstanceInvokeExpr( InstanceInvokeExpr v)
    {
    }
    public void analyseInvokeExpr( InvokeExpr v)
    {
    }
    public void analyseExpr( Expr v)
    {
    }
    public void analyseArrayRef( ArrayRef v)
    {
    }
    public void analyseInstanceFieldRef( InstanceFieldRef v)
    {
    }
    public void analyseRef( Ref v)
    {
    }
    public void analyseValue( Value v)
    {
    }
}
