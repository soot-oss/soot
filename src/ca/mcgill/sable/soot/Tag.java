package ca.mcgill.sable.soot;

import java.util.*;


public class Tag 
{
    String mName;
    Object mValue;
    
    Tag(String aName, Object aValue)
    {
	mName = aName;
	mValue = aValue;
	validateType();
    }
    
    public String getName()
    {
	return mName;
    }
    public String toString()
    {
	return mName + ": " + mValue;
    }
    public Object getValue()
    {
	return mValue;
    }
    public void setValue(Object o)
    {	
	mValue = o;
	validateType();
    }
    
    private void validateType()
    {
	if(mName.endsWith(".l") && !(mValue instanceof Long) ||
	   mName.endsWith(".d") && !(mValue instanceof Double) ||
	   mName.endsWith(".s") && !(mValue instanceof String) )
	    throw new RuntimeException("invalid type for tag: " + mName);	    
    }
     
}
