package soot.jimple.toolkits.annotation.tags;

import soot.*;

import java.util.*;
import java.io.*;

/** Implementation of the Tag interface for the array bounds check.
 */
public class ArrayCheckTag implements OneByteCodeTag
{
    private final static String NAME = "ArrayCheckTag";

    private boolean lowerCheck = true;
    private boolean upperCheck = true;

    /** 
     * A tag represents two bounds checks of an array reference.
     * The value 'true' indicates check needed.
     */
    public ArrayCheckTag(boolean lower, boolean upper)
    {
	lowerCheck = lower;
	upperCheck = upper;
    }

    /** 
     * Returns back the check information in binary form, which
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
   
  /** Needs upper bound check?
   */ 
    public boolean isCheckUpper()
    {
	return upperCheck;
    }
   
  /** Needs lower bound check?
   */ 
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


