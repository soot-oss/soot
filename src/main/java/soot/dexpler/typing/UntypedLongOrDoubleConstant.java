package soot.dexpler.typing;

import soot.DoubleType;
import soot.LongType;
import soot.Type;
import soot.Value;
import soot.jimple.DoubleConstant;
import soot.jimple.LongConstant;

public class UntypedLongOrDoubleConstant extends UntypedConstant {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3970057807907204253L;
	public final long value;

    private UntypedLongOrDoubleConstant(long value)
    {
        this.value = value;
    }

    public static UntypedLongOrDoubleConstant v(long value)
    {
        return new UntypedLongOrDoubleConstant(value);
    }

    public boolean equals(Object c)
    {
        return c instanceof UntypedLongOrDoubleConstant && ((UntypedLongOrDoubleConstant) c).value == this.value;
    }

    /** Returns a hash code for this DoubleConstant object. */
    public int hashCode()
    {
        return (int)(value^(value>>>32));
    }
    
    public DoubleConstant toDoubleConstant() {
        return DoubleConstant.v(Double.longBitsToDouble(value));
    }
    
    public LongConstant toLongConstant() {
        return LongConstant.v(value);
    }
    
	@Override
	public Value defineType(Type t) {
		if (t instanceof DoubleType) {
			return this.toDoubleConstant();
		} else if (t instanceof LongType) {
			return this.toLongConstant();
		} else {
			throw new RuntimeException("error: expected Double type or Long type. Got " + t);
		}
	}

}
