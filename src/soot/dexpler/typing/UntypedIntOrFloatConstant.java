package soot.dexpler.typing;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.FloatType;
import soot.IntType;
import soot.RefLikeType;
import soot.ShortType;
import soot.Type;
import soot.Value;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.NullConstant;

public class UntypedIntOrFloatConstant extends UntypedConstant {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4413439694269487822L;
	public final int value;

    private UntypedIntOrFloatConstant(int value)
    {
        this.value = value;
    }

    public static UntypedIntOrFloatConstant v(int value)
    {
        return new UntypedIntOrFloatConstant(value);
    }

    public boolean equals(Object c)
    {
        return c instanceof UntypedIntOrFloatConstant && ((UntypedIntOrFloatConstant) c).value == this.value;
    }

    /** Returns a hash code for this DoubleConstant object. */
    public int hashCode()
    {
        return (int)(value^(value>>>32));
    }
    
    public FloatConstant toFloatConstant() {
        return  FloatConstant.v(Float.intBitsToFloat((int) value));
    }
    
    public IntConstant toIntConstant() {
        return IntConstant.v(value);
    }

	@Override
	public Value defineType(Type t) {
		if (t instanceof FloatType) {
			return this.toFloatConstant();
		} else if (t instanceof IntType || t instanceof CharType || t instanceof BooleanType || t instanceof ByteType || t instanceof ShortType) {
			return this.toIntConstant();
		} else {
			if (value == 0 && t instanceof RefLikeType)
				return NullConstant.v();
			if (t == null) { // if the value is only used in a if to compare against another integer, then use default type of integer
				return this.toIntConstant();
			}
			throw new RuntimeException("error: expected Float type or Int-like type. Got " + t);
		}
	}

}
