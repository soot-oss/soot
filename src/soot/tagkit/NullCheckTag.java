package soot.tagkit;

import soot.*;

public class NullCheckTag implements OneByteCodeTag
{
    private final static String NAME = "NullCheckTag";
    
    private byte value = 0;

    public NullCheckTag(boolean needCheck)
    {
    	if (needCheck)
	    value = 0x04;
    }

    public String getName()
    {
	return NAME;
    }

    public byte[] getValue()
    {
        byte[] bv = new byte[1];
	bv[0] = value;
	return bv;
    }

    public boolean needCheck()
    {
    	if (value == 0)
	    return false;
	else
	    return true;
    }

    public String toString()
    {
	return ((value==0)?"[Unknown]":"[NonNULL]");
    }
}
