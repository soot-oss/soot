package soot.tagkit;

import soot.*;

public class ArrayNullCheckTag implements OneByteCodeTag
{
    private final static String NAME = "NullArrayCheckTag";
    
    private byte value = 0;

    public ArrayNullCheckTag()
    {}

    public ArrayNullCheckTag(byte v)
    {
    	value = v;
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

    public String toString()
    {
	return Byte.toString(value);
    }

    public byte accumulate(byte other)
    {
	byte oldv = value;
	value |= other;
	return oldv;
    }
}
