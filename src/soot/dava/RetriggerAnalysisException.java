package soot.dava;

import java.util.*;
import soot.util.*;
import soot.dava.toolkits.base.finders.*;

public class RetriggerAnalysisException extends Exception 
{
    public RetriggerAnalysisException()
    {
	Dava.v().log( "RetriggerAnalysisException");
    }
}
