package soot.jimple.toolkits.annotation.tags;

/** ArrayNullCheckTag combines ArrayCheckTag and NullCheckTag
 * into one tag. It uses bits of one byte value to represent
 * the check information. The right-most two bits stand for
 * the array bounds checks, and the right third bit represents
 * the null check. 
 * 
 * For array references, the right three bits are meaningful;
 * for other object refrences, only null check bit should be used.
 * 
 * @see ArrayCheckTag
 * @see NullCheckTag
 */
public class ArrayNullCheckTag implements OneByteCodeTag
{
    private final static String NAME = "ArrayNullCheckTag";
    
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

  /** Accumulates another byte value by OR. */
    public byte accumulate(byte other)
    {
	byte oldv = value;
	value |= other;
	return oldv;
    }
}
