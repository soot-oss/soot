package ca.mcgill.sable.soot.jimple.toolkit.invoke;

// import java.util.*;
import java.io.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*; 
import ca.mcgill.sable.soot.coffi.*;
// import ca.mcgill.sable.soot.sideEffect.*;



class UnknownDeclaringMethodException extends java.lang.RuntimeException
{
    UnknownDeclaringMethodException(String s)
    {
        super(s);
    }
    
    UnknownDeclaringMethodException()
    {
    }
}
