// package ca.mcgill.sable.soot.sideEffect;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;

public
class NoSuchCallSiteException extends RuntimeException
{
    public NoSuchCallSiteException(String s)
    {
        super(s);
    }
    
    public NoSuchCallSiteException()
    {
    }
}
