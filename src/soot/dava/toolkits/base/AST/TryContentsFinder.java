package soot.dava.toolkits.base.AST;

import java.util.*;
import soot.dava.internal.AST.*;

public class TryContentsFinder extends ASTAnalysis
{
    private TryContentsFinder() {}
    private static TryContentsFinder instance = new TryContentsFinder();

    public static TryContentsFinder v() 
    {
	return instance;
    }

    public void analyse( ASTNode n)
    {
    }
}
