package soot.jimple.toolkits.annotation.tags;

import soot.*;

import java.util.*;
import java.io.*;

public class ArrayCheckTag implements OneByteCodeTag
{
    private final static String NAME = "ArrayCheckTag";

    private boolean lowerCheck = true;
    private boolean upperCheck = true;

    /** 
     * Tag represents two bounds checks of an array reference.
     * Value 'true' indicates check needed.
     */
    public ArrayCheckTag(boolean lower, boolean upper)
    {
	lowerCheck = lower;
	upperCheck = upper;
    }

    /** 
     * Return back the check information in binary form, which
     * will be written into the class file.
     */    
    public byte[] getValue()
    {
        byte[] value = new byte[1];

	value[0] = 0;
	
	if (lowerCheck)
	    value[0] |= 0x01;
	
	if (upperCheck)
	    value[0] |= 0x02;

	return value;
    }
    
    public boolean isCheckUpper()
    {
	return upperCheck;
    }
    
    public boolean isCheckLower()
    {
	return lowerCheck;
    }


    public String getName()
    {
	return NAME;
    }

    public String toString()
    {
	return   (lowerCheck ? "": "[safe lower bound]") +"" +  (upperCheck ? "":"[safe upper bound]");
    }
}
